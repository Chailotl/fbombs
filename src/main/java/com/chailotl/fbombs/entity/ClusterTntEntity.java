package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.init.FBombsTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ClusterTntEntity extends AbstractTntEntity {
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

    public ClusterTntEntity(EntityType<ClusterTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public ClusterTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.CLUSTER_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected int getDefaultFuse() {
        return 80;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.CLUSTER_TNT;
    }

    @Override
    protected float getPower() {
        return 3f;
    }

    @Override
    protected ExplosionBehavior getExplosionBehavior() {
        return EXPLOSION_BEHAVIOR;
    }

    @Override
    protected void explode() {
        super.explode();

        for (int i = 0; i < 12; ++i) {
            DynamiteEntity dynamiteEntity = new DynamiteEntity(this.getWorld(), this.getX(), this.getY() + 0.5, this.getZ());
            dynamiteEntity.setItem(FBombsItems.DYNAMITE.getDefaultStack());
            dynamiteEntity.setFuse(this.random.nextBetween(40, 80));

            float pitch = this.random.nextBetween(35, 80);
            float speed = (float) this.random.nextTriangular(0.6, 0.1);

            dynamiteEntity.setVelocity(
                this,
                -pitch,
                this.random.nextFloat() * 360,
                0,
                speed,
                1.0f
            );
            this.getWorld().spawnEntity(dynamiteEntity);
        }
    }
}