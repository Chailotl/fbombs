package com.chailotl.fbombs.api;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/**
 * Define custom behavior for your Blocks if they are affected by a Volumetric Explosion.
 *
 * @apiNote only implement if you have custom behaviour in mind. A default implementation is provided already
 * with the {@link com.chailotl.fbombs.mixin.AbstractBlockMixin AbstractBlockMixin} class.
 */
public interface VolumetricExplosion {
    /**
     * Will run, if the current Block was taken by a Volumetric Explosion. By default, this method contains the logic
     * which removes the block and distributes the block loot content, so the method itself is running, when the block
     * still exists.
     *
     * @param pos       currently affected Block
     * @param originPos center of the Volumetric Explosion
     * @param state     BlockState of the currently affected Block
     * @implNote call the super method, if you want to keep the block loot distribution and removal functionality.
     */
    void fbombs$onExploded(ServerWorld world, BlockPos pos, boolean replace, BlockPos originPos, BlockState state/*, int explosionStrength*/);
}
