package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.data.BlockAndEntityData;
import com.chailotl.fbombs.data.LocatableBlock;
import com.chailotl.fbombs.init.FBombsGamerules;
import com.chailotl.fbombs.init.FBombsPersistentState;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.LoggerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * Explosions based on Volume instead of Ray casts. There are no performance improvements.
 * Implement custom performance gaining methods as needed
 */
public class ExplosionHandler {
    /**
     * @param radius                         specifies Radius of the explosion perimeter. Define the shape further with {@link ExplosionShape}
     * @param shape                          defines the shape of the explosion, which is located in the explosion perimeter
     * @param extrusion                      only needed if the shape is {@link ExplosionShape#CYLINDER}
     * @param blockExceptions                sets ignored blocks from the explosion. This exists in addition to blastResistance calculations
     * @param startBlastStrength             used to calculate influenced blocks when the blastResistance of it is lower than that
     * @param blastStrengthFalloffMultiplier when blast strength is bigger then the block's blast resistance it will continue
     *                                       on with the next Block. For each passed Block the explosion strength is subtracted by
     *                                       the Block's blast resistance. This value minimizes this blast strength falloff (if < 1)
     * @param scorchedThreshold              this value is an extra bonus on top of the already existing blast strength.
     *                                       It applies when:
     *                                       <code>block's blast resistance > blast strength &&
     *                                       block's blast resistance < blast strength + scorched threshold</code>
     *                                       It can be used to just change blocks into e.g. scorched variants instead of
     *                                       completely removing them
     */
    public static BlockAndEntityData collect(ServerWorld world, BlockPos origin, int radius, ExplosionShape shape,
                                             @Nullable Double extrusion, Predicate<BlockState> blockExceptions,
                                             float startBlastStrength, float blastStrengthFalloffMultiplier, float scorchedThreshold) {
        BlockAndEntityData output = new BlockAndEntityData();

        BlockingQueue<BlockPos.Mutable> pool = new LinkedBlockingQueue<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            pool.add(new BlockPos.Mutable());
        }
        ThreadLocal<BlockPos.Mutable> threadMutablePos = ThreadLocal.withInitial(() ->
                Optional.ofNullable(pool.poll()).orElse(new BlockPos.Mutable()));

        scorchedThreshold = Math.clamp(scorchedThreshold, 0f, startBlastStrength);
        startBlastStrength = Math.max(0, startBlastStrength);

        Map<BlockPos, BlockState> blockStateCache = new ConcurrentHashMap<>();
        Map<BlockPos, FluidState> fluidStateCache = new ConcurrentHashMap<>();


        try(ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            List<Future<?>> futures = new ArrayList<>();

            for (int x = -radius; x < radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    for (int z = -radius; z < radius; z++) {
                        if (!shape.isInsideVolume(radius, extrusion, new Vec3d(x, y, z))) {
                            continue;
                        }
                        BlockPos.Mutable currentPos = threadMutablePos.get().set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                        BlockState currentState = blockStateCache.computeIfAbsent(currentPos.toImmutable(), world::getBlockState);

                        float finalStartBlastStrength = startBlastStrength;
                        float finalScorchedThreshold = scorchedThreshold;

                        // Parallel Rays for 3D DDA
                        futures.add(executor.submit(() -> {
                            //FIXME: [ShiroJR] use normalized unit vector? current calculation might skip some BlockPos
                            Vec3d startPos = origin.toCenterPos();
                            Vec3d direction = currentPos.toCenterPos().subtract(startPos);
                            int steps = (int) MathHelper.absMax(direction.x, MathHelper.absMax(direction.y, direction.z));
                            Vec3d stepSize = new Vec3d(direction.x / steps, direction.y / steps, direction.z / steps);

                            float deterioratingPower = finalStartBlastStrength;
                            BlockPos.Mutable posWalker = origin.mutableCopy();

                            for (int i = 0; i < steps; i++) {
                                BlockPos stepPos = posWalker.toImmutable();
                                BlockState stepState = blockStateCache.computeIfAbsent(stepPos, world::getBlockState);
                                FluidState stepFluidState = fluidStateCache.computeIfAbsent(stepPos, world::getFluidState);

                                if (stepState.isAir()) continue;
                                if (blockExceptions.test(stepState)) {
                                    deterioratingPower = 0;
                                    break;
                                }

                                // https://minecraft.wiki/w/Explosion#Blast_resistance
                                float blastResistance = stepState.getBlock().getBlastResistance() + stepFluidState.getBlastResistance();
                                deterioratingPower -= blastResistance * blastStrengthFalloffMultiplier;
                                if (deterioratingPower <= 0) {
                                    deterioratingPower = 0;
                                    break;
                                }
                                posWalker.set(posWalker.add(BlockPos.ofFloored(stepSize)));
                            }

                            if (deterioratingPower > currentState.getBlock().getBlastResistance()) {
                                if (!currentState.isAir()) LoggerUtil.devLogger("gone");
                                output.addToAffectedBlocks(new LocatableBlock(currentPos.toImmutable(), currentState));
                            } else if (deterioratingPower + finalScorchedThreshold > currentState.getBlock().getBlastResistance()) {
                                output.addToScorchedBlocks(new LocatableBlock(currentPos.toImmutable(), currentState));
                            } else {
                                output.addToUnaffectedBlocks(currentPos.toImmutable());
                            }
                            threadMutablePos.remove();
                            LoggerUtil.devLogger("ray is done done");
                        }));
                    }
                }
            }
            for (Future<?> future : futures) {
                future.get(15, TimeUnit.SECONDS);  // Waits for completion of each task
            }
            executor.shutdown();
            if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                LoggerUtil.devLogger("Forcefully shut down Explosion Data Gathering Executor", LoggerUtil.Type.WARNING, null);
            }
        }
        catch (Exception e) {
            LoggerUtil.devLogger("Exception while handling Explosion Data Gathering on multiple threads", LoggerUtil.Type.ERROR, e);
        }

        List<Entity> entitiesInRange = world.getEntitiesByClass(Entity.class, new Box(origin).expand(radius),
                entity -> shape.isInsideVolume(radius, extrusion, entity.getPos()));

        /*StringBuilder sb = new StringBuilder("[%s] Ignored ".formatted(output.getUnaffectedBlocks().size()));
        output.getAffectedBlocks().forEach(locatableBlock -> sb.append(locatableBlock.state().toString()).append(" | "));
        LoggerUtil.devLogger(sb.toString());*/

        return output;
    }

    public static void explodeSpherical(ServerWorld world, BlockPos origin, int radius, int strength) {
        Predicate<BlockState> isImmune = blockState -> {
            if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) return true;
            if (!world.getGameRules().getBoolean(FBombsGamerules.ALLOW_VOLUMETRIC_EXPLOSION_DAMAGE)) return true;
            return blockState.isIn(FBombsTags.Blocks.VOLUMETRIC_EXPLOSION_IMMUNE);
        };

        CompletableFuture.supplyAsync(() -> collect(world, origin, /*radius*/ 30, ExplosionShape.SPHERE, null,
                isImmune, /*strength*/ 8, 0.4f, 4))
                .thenAccept(blockAndEntityData -> FBombsPersistentState.fromServer(world).orElseThrow().getExplosions().add(blockAndEntityData));

        /*BlockAndEntityData blockAndEntityData = collect(world, origin, *//*radius*//* 15, ExplosionShape.SPHERE, null,
                isImmune, *//*strength*//* 8, 0.4f, 4);*/

        /*for (Locatable entry : blockAndEntityData.getAffectedBlocks()) {
            if (entry instanceof LocatableBlock block) {
                if (block.state().isAir()) continue;
                BlockPos pos = block.pos();
                BlockState state = block.state();
                ((VolumetricExplosion) state.getBlock()).fbombs$onExploded(world, pos, origin, state, strength);
            }
        }
        spawnParticlesAndSound(world, origin, blockAndEntityData.iterateAffectedTargets());*/
    }

    private static void spawnParticlesAndSound(ServerWorld world, BlockPos origin, Iterator<LocatableBlock> hitBlocks) {
        //TODO: [ShiroJR] don't only spawn on affected targets but in whole explosion shape with a low chance?
        while (hitBlocks.hasNext()) {
            Vec3d pos = hitBlocks.next().getPos();
            if (world.getRandom().nextFloat() > 0.5f) continue;
            world.spawnParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 1,
                    world.getRandom().nextGaussian() - 0.5,
                    world.getRandom().nextGaussian() - 0.5,
                    world.getRandom().nextGaussian() - 0.5,
                    0.25);
        }
        world.playSound(null, origin, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 1f, 1f);
    }
}
