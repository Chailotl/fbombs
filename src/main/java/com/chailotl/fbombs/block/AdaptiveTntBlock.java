package com.chailotl.fbombs.block;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.entity.AdaptiveTntBlockEntity;
import com.chailotl.fbombs.entity.AdaptiveTntEntity;
import com.chailotl.fbombs.entity.util.TntEntityType;
import com.chailotl.fbombs.init.FBombsTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class AdaptiveTntBlock extends GenericTntBlock implements BlockEntityProvider {
    public static final MapCodec<AdaptiveTntBlock> CODEC = createCodec(AdaptiveTntBlock::new);

    @Override
    public MapCodec<AdaptiveTntBlock> getCodec() {
        return CODEC;
    }

    public AdaptiveTntBlock(Settings settings) {
        super(TntEntityType.register("adaptive_tnt", AdaptiveTntEntity::new), settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AdaptiveTntBlockEntity(pos, state);
    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof AdaptiveTntBlockEntity adaptiveTntBlockEntity) {
            List<ItemStack> list = new ArrayList<>();
            list.add(adaptiveTntBlockEntity.getStack());
            return list;
            /*builder = builder.addDynamicDrop(FBombs.getId("adaptive_tnt"), lootConsumer -> {
               lootConsumer.accept(adaptiveTntBlockEntity.getStack());
            });*/
        }

        return super.getDroppedStacks(state, builder);
    }

    @Override
    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (!state.isAir() && explosion.getDestructionType() != Explosion.DestructionType.TRIGGER_BLOCK) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            onDestroyedByExplosion(world, pos, explosion, state, blockEntity);
        }
    }

    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion, BlockState state, BlockEntity blockEntity) {
        if (!world.isClient) {
            AdaptiveTntEntity tntEntity = new AdaptiveTntEntity(world,
                (double)pos.getX() + 0.5, pos.getY(),
                (double)pos.getZ() + 0.5,
                explosion.getCausingEntity(),
                state);
            if (blockEntity instanceof AdaptiveTntBlockEntity) {
                configureAdaptiveTnt(tntEntity, (AdaptiveTntBlockEntity) blockEntity);
            }
            int i = tntEntity.getFuse();
            tntEntity.setFuse((short)(world.random.nextInt(Math.max(1, i / 4)) + i / 8));
            world.spawnEntity(tntEntity);
        }
    }

    @Override
    protected void primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            BlockState state = world.getBlockState(pos);
            AdaptiveTntEntity tntEntity = new AdaptiveTntEntity(world, (double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, igniter, state);
            if (world.getBlockEntity(pos) instanceof AdaptiveTntBlockEntity blockEntity) {
                configureAdaptiveTnt(tntEntity, blockEntity);
            }
            world.spawnEntity(tntEntity);
            if (tntEntity.getFuse() >= 10) {
                world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        }
    }

    private static void configureAdaptiveTnt(AdaptiveTntEntity tntEntity, AdaptiveTntBlockEntity blockEntity) {
        tntEntity.power = blockEntity.power;
        tntEntity.setFuse(blockEntity.fuse);
        boolean damage = blockEntity.damage;
        boolean blockDamage = blockEntity.blockDamage;
        boolean underwater = blockEntity.underwater;
        boolean sponge = blockEntity.sponge;
        tntEntity.fireCharged = blockEntity.fireCharged;
        float knockbackModifier = blockEntity.windCharged ? 2 : 1;
        if (blockEntity.levitating) { tntEntity.enableLevitating(); }
        if (blockEntity.firework) { tntEntity.enableFirework(); }

        tntEntity.explosionBehavior = new ExplosionBehavior() {
            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                if (blockState.isAir() && fluidState.isEmpty()) {
                    return Optional.empty();
                } else if (underwater && blockState.isOf(Blocks.WATER)) {
                    return Optional.of(0f);
                } else if (blockDamage) {
                    return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
                } else {
                    float blastResistance = blockState.getBlock().getBlastResistance();
                    return Optional.of(blastResistance <= 0.1f ? blastResistance : 3600000f);
                }
            }

            @Override
            public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                if (state.isIn(FBombsTags.Blocks.TNT_VARIANTS)) {
                    return true;
                } else if (sponge && state.isOf(Blocks.WATER)) {
                    return true;
                } else if (underwater && state.isOf(Blocks.WATER)) {
                    return false;
                } else if (blockDamage) {
                    return super.canDestroyBlock(explosion, world, pos, state, power);
                } else {
                    return false;
                }
            }

            @Override
            public float getKnockbackModifier(Entity entity) {
                return knockbackModifier;
            }

            @Override
            public float calculateDamage(Explosion explosion, Entity entity) {
                return damage ? super.calculateDamage(explosion, entity) : 0;
            }
        };
    }

    public static void configureAdaptiveTnt(AdaptiveTntEntity tntEntity, ItemStack stack) {
        NbtComponent nbtComponent = stack.getComponents().get(DataComponentTypes.BLOCK_ENTITY_DATA);
        NbtCompound nbt = nbtComponent != null ? nbtComponent.getNbt() : new NbtCompound();

        tntEntity.power = getIntOrDefault(nbt, "power", 0);
        tntEntity.setFuse(getIntOrDefault(nbt, "fuse", 0));
        boolean damage = getBooleanOrDefault(nbt, "damage", true);
        boolean blockDamage = getBooleanOrDefault(nbt, "block_damage", true);
        boolean underwater = getBooleanOrDefault(nbt, "underwater", false);
        boolean sponge = getBooleanOrDefault(nbt, "sponge", false);
        tntEntity.fireCharged = getBooleanOrDefault(nbt, "fire_charged", false);
        float knockbackModifier = getBooleanOrDefault(nbt, "wind_charged", false) ? 2 : 1;
        if (getBooleanOrDefault(nbt, "levitating", false)) { tntEntity.enableLevitating(); }
        if (getBooleanOrDefault(nbt, "firework", false)) { tntEntity.enableFirework(); }

        tntEntity.explosionBehavior = new ExplosionBehavior() {
            @Override
            public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
                if (blockState.isAir() && fluidState.isEmpty()) {
                    return Optional.empty();
                } else if (underwater && blockState.isOf(Blocks.WATER)) {
                    return Optional.of(0f);
                } else if (blockDamage) {
                    return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
                } else {
                    float blastResistance = blockState.getBlock().getBlastResistance();
                    return Optional.of(blastResistance <= 0.1f ? blastResistance : 3600000f);
                }
            }

            @Override
            public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
                if (state.isIn(FBombsTags.Blocks.TNT_VARIANTS)) {
                    return true;
                } else if (sponge && state.isOf(Blocks.WATER)) {
                    return true;
                } else if (underwater && state.isOf(Blocks.WATER)) {
                    return false;
                } else if (blockDamage) {
                    return super.canDestroyBlock(explosion, world, pos, state, power);
                } else {
                    return false;
                }
            }

            @Override
            public float getKnockbackModifier(Entity entity) {
                return knockbackModifier;
            }

            @Override
            public float calculateDamage(Explosion explosion, Entity entity) {
                return damage ? super.calculateDamage(explosion, entity) : 0;
            }
        };
    }

    private static int getIntOrDefault(NbtCompound nbt, String key, int defaultValue) {
        return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
    }

    private static boolean getBooleanOrDefault(NbtCompound nbt, String key, boolean defaultValue) {
        return nbt.contains(key) ? nbt.getBoolean(key) : defaultValue;
    }
}
