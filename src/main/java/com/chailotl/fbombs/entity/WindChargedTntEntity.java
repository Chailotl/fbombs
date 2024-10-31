package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WindChargedTntEntity extends AbstractTntEntity {
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

        @Override
        public float getKnockbackModifier(Entity entity) {
            return 2;
        }

        @Override
        public float calculateDamage(Explosion explosion, Entity entity) {
            return 0;
        }
    };

    public WindChargedTntEntity(EntityType<WindChargedTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindChargedTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.WIND_CHARGED_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.WIND_CHARGED_TNT;
    }

    @Override
    protected ExplosionBehavior getExplosionBehavior() {
        return EXPLOSION_BEHAVIOR;
    }

    @Override
    protected ParticleEffect getParticle() {
        return ParticleTypes.GUST_EMITTER_SMALL;
    }

    @Override
    protected ParticleEffect getEmitterParticle() {
        return ParticleTypes.GUST_EMITTER_LARGE;
    }

    @Override
    protected RegistryEntry<SoundEvent> getSoundEvent() {
        return SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST;
    }
}