package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.util.LoggerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

/**
 * Explosions based on Volume instead of Ray casts. There are no performance improvements.
 * Implement custom performance gaining methods as needed
 */
public class ExplosionHandler {
    /**
     * @param radius          specifies Radius of the explosion perimeter. Define the shape further with {@link ExplosionShape}
     * @param shape           defines the shape of the explosion, which is located in the explosion perimeter
     * @param extrusion       only needed if the shape is {@link ExplosionShape#CYLINDER}
     * @param blockExceptions sets ignored blocks from the explosion. This exists in addition to blastResistance calculations
     * @param blastStrength   used to calculate influenced blocks when the blastResistance of it is lower than that
     */
    public static HashMap<BlockPos, BlockState> collect(ServerWorld world, BlockPos origin, int radius, ExplosionShape shape,
                                                        @Nullable Double extrusion, Predicate<BlockState> blockExceptions, int blastStrength) {
        HashMap<BlockPos, BlockState> explodedBlocks = new HashMap<>();
        List<BlockPos> excludedBlocks = new ArrayList<>();
        BlockPos.Mutable currentPos = new BlockPos.Mutable();

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    if (!shape.isInsideVolume(radius, extrusion, new Vec3d(x, y, z))) continue;

                    currentPos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    BlockState currentState = world.getBlockState(currentPos);
                    boolean isAir = currentState.isAir();
                    boolean resistsBlast = currentState.getBlock().getBlastResistance() > blastStrength;
                    boolean isExcluded = !blockExceptions.test(currentState);
                    if (isAir) {
                        continue;
                    }
                    if (resistsBlast || isExcluded) {
                        excludedBlocks.add(currentPos.mutableCopy());
                        continue;
                    }
                    explodedBlocks.put(currentPos.mutableCopy(), currentState);
                }
            }
        }
        StringBuilder sb = new StringBuilder("Ignored ");
        excludedBlocks.forEach(pos -> sb.append(pos.toShortString()).append(" | "));
        LoggerUtil.devLogger("Ignored " + sb);

        //TODO: [ShiroJR] calculate explosion shadow based on excluded blocks?
        return explodedBlocks;
    }

    public static void explodeSpherical(ServerWorld world, BlockPos origin, int radius, int strength) {
        Predicate<BlockState> isSafeFromExplosion = blockState -> {
            if (blockState.contains(Properties.WATERLOGGED)) return true;
            if (blockState.isOf(Blocks.BEDROCK)) return true;
            return true;
        };

        HashMap<BlockPos, BlockState> influencedBlocks = collect(world, origin, radius, ExplosionShape.SPHERE, null, isSafeFromExplosion, strength);
        for (var entry : influencedBlocks.entrySet()) {
            if (world.getRandom().nextFloat() > 0.5f) continue;
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            world.spawnParticles(ParticleTypes.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 1,
                    world.getRandom().nextGaussian() - 0.5,
                    world.getRandom().nextGaussian() - 0.5,
                    world.getRandom().nextGaussian() - 0.5,
                    0.25);
            // state.onExploded(world, pos, );
        }
        if (!influencedBlocks.isEmpty()) {
            world.playSound(null, origin, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.BLOCKS, 1f, 1f);
        }
    }
}
