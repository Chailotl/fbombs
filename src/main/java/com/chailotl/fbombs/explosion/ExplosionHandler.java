package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.api.VolumetricExplosion;
import com.chailotl.fbombs.data.BlockAndEntityGroup;
import com.chailotl.fbombs.data.LocatableBlock;
import com.chailotl.fbombs.data.ScorchedBlockDataLoader;
import com.chailotl.fbombs.init.FBombsGamerules;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.LoggerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Predicate;

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
    public static BlockAndEntityGroup collect(ServerWorld world, BlockPos origin, int radius, ExplosionShape shape,
                                              @Nullable Double extrusion, Predicate<BlockState> blockExceptions,
                                              float startBlastStrength, float blastStrengthFalloffMultiplier, float scorchedThreshold) {

        BlockAndEntityGroup output = new BlockAndEntityGroup(world.getRegistryKey(), origin);

        BlockingQueue<BlockPos.Mutable> pool = new LinkedBlockingQueue<>();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors() / 2; i++) {
            pool.add(new BlockPos.Mutable());
        }
        ThreadLocal<BlockPos.Mutable> threadMutablePos = ThreadLocal.withInitial(() ->
                Optional.ofNullable(pool.poll()).orElse(new BlockPos.Mutable()));

        scorchedThreshold = Math.clamp(scorchedThreshold, 0f, startBlastStrength);
        startBlastStrength = Math.max(0, startBlastStrength);

        Map<BlockPos, BlockState> blockStateCache = new ConcurrentHashMap<>();
        Map<BlockPos, FluidState> fluidStateCache = new ConcurrentHashMap<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
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

                            Vec3d startPos = origin.toCenterPos();
                            Vec3d direction = currentPos.toCenterPos().subtract(startPos);

                            Vec3i stepDirection = new Vec3i(direction.x > 0 ? 1 : -1, direction.y > 0 ? 1 : -1, direction.z > 0 ? 1 : -1);
                            Vec3d distanceToFirstBoundary = new Vec3d(
                                    stepDirection.getX() > 0 ? Math.ceil(startPos.x) - startPos.x : startPos.x - Math.floor(startPos.x),
                                    stepDirection.getY() > 0 ? Math.ceil(startPos.y) - startPos.y : startPos.y - Math.floor(startPos.y),
                                    stepDirection.getZ() > 0 ? Math.ceil(startPos.z) - startPos.z : startPos.z - Math.floor(startPos.z)
                            );
                            Vec3d distanceToNextStep = new Vec3d(
                                    Math.abs(1.0 / direction.x),
                                    Math.abs(1.0 / direction.y),
                                    Math.abs(1.0 / direction.z)
                            );

                            float deterioratingPower = finalStartBlastStrength;
                            BlockPos.Mutable rayWalker = origin.mutableCopy();
                            LoggerUtil.devLogger("started ray with power of: " + deterioratingPower);

                            while (true) {
                                BlockPos stepPos = rayWalker.toImmutable();
                                BlockState stepState = blockStateCache.computeIfAbsent(stepPos, world::getBlockState);
                                FluidState stepFluidState = fluidStateCache.computeIfAbsent(stepPos, world::getFluidState);

                                // https://minecraft.wiki/w/Explosion#Blast_resistance
                                float blastResistance = stepState.getBlock().getBlastResistance() + stepFluidState.getBlastResistance();
                                deterioratingPower -= blastResistance * blastStrengthFalloffMultiplier;

                                if (blockExceptions.test(stepState) || deterioratingPower <= 0) {
                                    deterioratingPower = 0;
                                    break;
                                }
                                distanceToFirstBoundary = movePosWalkerAlongSmallestAxis(rayWalker, distanceToFirstBoundary, distanceToNextStep, stepDirection);
                                if (rayWalker.equals(currentPos)) {
                                    break;
                                }
                                if (!new Box(origin).expand(radius).contains(rayWalker.toCenterPos())) {
                                    break;
                                }
                            }


                            finishRayHandling(currentState, output, origin.getSquaredDistance(currentPos), currentPos.toImmutable(), finalScorchedThreshold, deterioratingPower);

                            threadMutablePos.remove();
                        }));
                    }
                }
            }
            for (Future<?> future : futures) {
                future.get();  // Waits for completion of each task
            }
            executor.shutdown();
        } catch (Exception e) {
            LoggerUtil.devLogger("Exception while handling Explosion Data Gathering on multiple threads", LoggerUtil.Type.ERROR, e);
        }

        List<Entity> entitiesInRange = world.getEntitiesByClass(Entity.class, new Box(origin).expand(radius),
                entity -> shape.isInsideVolume(radius, extrusion, entity.getPos()));


        return output;
    }

    private static Vec3d movePosWalkerAlongSmallestAxis(BlockPos.Mutable rayWalker, Vec3d distanceToFirstBoundary, Vec3d distanceToNextStep, Vec3i stepDirection) {
        if (distanceToFirstBoundary.x < distanceToFirstBoundary.y && distanceToFirstBoundary.x < distanceToFirstBoundary.z) {
            // towards X axis
            rayWalker.setX(rayWalker.getX() + stepDirection.getX());
            distanceToFirstBoundary = new Vec3d(distanceToFirstBoundary.x + distanceToNextStep.x, distanceToFirstBoundary.y, distanceToFirstBoundary.z); // Advance along X
        } else if (distanceToFirstBoundary.y < distanceToFirstBoundary.z) {
            // towards Y axis
            rayWalker.setY(rayWalker.getY() + stepDirection.getY());
            distanceToFirstBoundary = new Vec3d(distanceToFirstBoundary.x, distanceToFirstBoundary.y + distanceToNextStep.y, distanceToFirstBoundary.z); // Advance along Y
        } else {
            // towards Z axis
            rayWalker.setZ(rayWalker.getZ() + stepDirection.getZ());
            distanceToFirstBoundary = new Vec3d(distanceToFirstBoundary.x, distanceToFirstBoundary.y, distanceToFirstBoundary.z + distanceToNextStep.z); // Advance along Z
        }
        return distanceToFirstBoundary;
    }

    private static void finishRayHandling(BlockState currentState, BlockAndEntityGroup output, double distanceToOrigin,
                                          BlockPos currentPos, float finalScorchedThreshold, float deterioratingPower) {
        if (deterioratingPower > currentState.getBlock().getBlastResistance()) {
            output.getAffectedBlocks().add(new LocatableBlock(currentPos, currentState, distanceToOrigin));
            LoggerUtil.devLogger("Added Ray to affected at %s".formatted(currentPos));
        } else if (deterioratingPower + finalScorchedThreshold > currentState.getBlock().getBlastResistance()) {
            output.getScorchedBlocks().add(new LocatableBlock(currentPos, currentState, distanceToOrigin));
            LoggerUtil.devLogger("Added Ray to scorched at %s".formatted(currentPos));
        } else {
            output.getUnaffectedBlocks().add(new LocatableBlock(currentPos, currentState, distanceToOrigin));
            LoggerUtil.devLogger("Added Ray to unaffected at %s".formatted(currentPos));
        }
    }


    public static void explodeSpherical(ServerWorld world, BlockPos origin, int radius, int strength, float falloff, int scorchedThreshold) {
        Predicate<BlockState> isImmune = blockState -> {
            if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) return true;
            if (!world.getGameRules().getBoolean(FBombsGamerules.ALLOW_VOLUMETRIC_EXPLOSION_DAMAGE)) return true;
            return blockState.isIn(FBombsTags.Blocks.VOLUMETRIC_EXPLOSION_IMMUNE);
        };

        CompletableFuture.supplyAsync(() -> collect(world, origin, radius, ExplosionShape.SPHERE, null,
                        isImmune, strength, falloff, scorchedThreshold))
                .thenAccept(blockAndEntityGroup -> world.getServer().execute(() -> {
                    FBombs.modifyCachedPersistentState(world, state -> {
                        state.getExplosions().add(blockAndEntityGroup);
                        ExplosionManager.getInstance(world.getServer()).addExplosion(world, blockAndEntityGroup);
                    });
                    LoggerUtil.devLogger("finished explosion data gathering");
                }));
    }

    public static int handleExplosion(ServerWorld world, BlockAndEntityGroup group, int blocksPerTick) {
        int processedAffectedBlocks = 0;
        int processedScorchedBlocks = 0;
        int processedUnaffectedBlocks = 0;

        while (processedAffectedBlocks < blocksPerTick / 3 && !group.getAffectedBlocks().isEmpty()) {
            // TODO: [ShiroJR] add radiation even to air blocks

            processedAffectedBlocks++;
            LocatableBlock entry = group.getAffectedBlocks().poll();
            if (entry == null) continue;
            ((VolumetricExplosion) entry.state().getBlock()).fbombs$onExploded(
                    world, entry.pos(), false, group.getOrigin(), entry.state()
            );
            // spawnParticlesAndSound(world, group.getOrigin(), entry);
        }
        while (processedScorchedBlocks < blocksPerTick / 3 && !group.getScorchedBlocks().isEmpty()) {
            // TODO: [ShiroJR] add radiation even to air blocks

            processedScorchedBlocks++;
            LocatableBlock entry = group.getScorchedBlocks().poll();
            if (entry == null) continue;
            if (world.getRandom().nextFloat() > 0.2) continue;
            var scorchedVariant = ScorchedBlockDataLoader.getEntry(entry.state().getBlock());
            if (scorchedVariant == null) {
                ((VolumetricExplosion) entry.state().getBlock()).fbombs$onExploded(
                        world, entry.pos(), true, group.getOrigin(), entry.state()
                );
            } else {
                Block chosenBlock = scorchedVariant.getValue().get(world.getRandom().nextInt(scorchedVariant.getValue().size()));
                world.setBlockState(entry.pos(), chosenBlock.getDefaultState());
            }
            // spawnParticlesAndSound(world, group.getOrigin(), scorchedBlockEntry);
        }

        while (processedUnaffectedBlocks < blocksPerTick / 3 && !group.getUnaffectedBlocks().isEmpty()) {
            processedUnaffectedBlocks++;
            LocatableBlock entry = group.getUnaffectedBlocks().poll();
            if (entry == null) continue;
            // TODO: [ShiroJR] add radiation even to air blocks

            // spawnParticlesAndSound(world, group.getOrigin(), entry);
        }
        return processedAffectedBlocks + processedScorchedBlocks + processedUnaffectedBlocks;
    }

    /*private static void spawnParticlesAndSound(ServerWorld world, BlockPos origin, LocatableBlock targetPosition) {
        Vec3d pos = targetPosition.pos().toCenterPos();
        if (world.getRandom().nextFloat() > 0.3f) return;
        world.spawnParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 1,
                world.getRandom().nextGaussian() - 0.5,
                world.getRandom().nextGaussian() - 0.5,
                world.getRandom().nextGaussian() - 0.5,
                0.25);

        world.playSound(null, origin, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 1f, 1f);
    }*/
}
