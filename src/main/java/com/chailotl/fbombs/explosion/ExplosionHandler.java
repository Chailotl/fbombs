package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.api.VolumetricExplosion;
import com.chailotl.fbombs.data.BlockAndEntityData;
import com.chailotl.fbombs.data.LocatableBlock;
import com.chailotl.fbombs.init.FBombsGamerules;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.Locatable;
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
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.logging.Logger;

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
        BlockAndEntityData output = new BlockAndEntityData(world);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        scorchedThreshold = Math.clamp(scorchedThreshold, 0f, startBlastStrength);
        startBlastStrength = Math.max(0, startBlastStrength);

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    if (!shape.isInsideVolume(radius, extrusion, new Vec3d(x, y, z))) {
                        continue;
                    }
                    mutablePos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockState currentState = world.getBlockState(mutablePos.toImmutable());

                    // 3D DDA
                    Vec3d startPos = origin.toCenterPos();
                    Vec3d direction = mutablePos.toImmutable().toCenterPos().subtract(startPos);
                    int steps = (int) MathHelper.absMax(direction.x, MathHelper.absMax(direction.y, direction.z));
                    Vec3d stepSize = new Vec3d(direction.x / steps, direction.y / steps, direction.z / steps);

                    float deterioratingPower = startBlastStrength;
                    BlockPos.Mutable posWalker = origin.mutableCopy();

                    LoggerUtil.devLogger("For Block: %s | Distance:  %s".formatted(currentState, Vec3d.of(mutablePos.toImmutable()).subtract(Vec3d.of(origin)).length()));

                    for (int i = 0; i < steps; i++) {
                        BlockPos stepPos = posWalker.toImmutable();
                        BlockState stepState = world.getBlockState(stepPos);
                        FluidState stepFluidState = world.getFluidState(stepPos);

                        LoggerUtil.devLogger("currentStep: %s at: %s | stepState : %s | det.Power: %s"
                                .formatted(i, stepPos, stepState, deterioratingPower));

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
                        output.addToAffectedBlocks(new LocatableBlock(mutablePos.toImmutable(), currentState));
                    } else if (deterioratingPower + scorchedThreshold > currentState.getBlock().getBlastResistance()) {
                        output.addToScorchedBlocks(new LocatableBlock(mutablePos.toImmutable(), currentState));
                    } else {
                        output.addToUnaffectedBlocks(mutablePos.toImmutable());
                    }

                }
            }
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

        CompletableFuture.supplyAsync(() -> collect(world, origin, /*radius*/ 15, ExplosionShape.SPHERE, null,
                isImmune, /*strength*/ 8, 0.4f, 4))
                .thenAccept(blockAndEntityData -> LoggerUtil.devLogger("yup"));

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

    private static void spawnParticlesAndSound(ServerWorld world, BlockPos origin, Iterator<Locatable> hitBlocks) {
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
