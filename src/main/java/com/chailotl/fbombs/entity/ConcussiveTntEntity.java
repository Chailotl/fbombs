package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConcussiveTntEntity extends AbstractTntEntity {
    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            if (blockState.isAir() && fluidState.isEmpty()) {
                return Optional.empty();
            } else {
                float blastResistance = blockState.getBlock().getBlastResistance();
                return Optional.of(blastResistance <= 0.1f ? blastResistance : 3600000f);
            }
        }

        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return state.isIn(FBombsTags.Blocks.TNT_VARIANTS);
        }
    };

    public ConcussiveTntEntity(EntityType<ConcussiveTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public ConcussiveTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.CONCUSSIVE_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.CONCUSSIVE_TNT;
    }

    @Override
    protected ExplosionBehavior getExplosionBehavior() {
        return EXPLOSION_BEHAVIOR;
    }
}