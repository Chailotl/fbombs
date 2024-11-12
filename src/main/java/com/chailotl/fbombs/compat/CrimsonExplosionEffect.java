package com.chailotl.fbombs.compat;

import com.chailotl.fbombs.FBombs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.reoseah.magisterium.data.ItemValuesLoader;
import io.github.reoseah.magisterium.data.effect.SpellEffect;
import io.github.reoseah.magisterium.recipe.SpellRecipeInput;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CrimsonExplosionEffect extends SpellEffect {
    public static final MapCodec<CrimsonExplosionEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Identifier.CODEC.fieldOf("utterance").forGetter(effect -> effect.utterance),
        Codec.INT.fieldOf("duration").forGetter(effect -> effect.duration),
        Codec.INT.fieldOf("max_range").forGetter(effect -> effect.maxRange)
    ).apply(instance, CrimsonExplosionEffect::new));

    public final int maxRange;

    public CrimsonExplosionEffect(Identifier utterance, int duration, int maxRange) {
        super(utterance, duration);
        this.maxRange = maxRange;
    }

    @Override
    public MapCodec<? extends SpellEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void finish(SpellRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        PlayerEntity player = input.getPlayer();
        World world = player.getWorld();

        int totalValue = 0;
        for (int i = 0; i < input.getSize(); i++) {
            var stack = input.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int value = ItemValuesLoader.getValue(stack);
                totalValue += value;

                stack.decrement(1);
                input.inventory.setStack(i, stack);
            }
        }

        int power = 2 + (int) Math.sqrt(totalValue);

        for (ItemStack stack : player.getArmorItems()) {
            if (!stack.isEmpty() && stack.isOf(Registries.ITEM.get(Identifier.of("familiar_magic:megumins_hat")))) {
                power = 8;
                player.getHungerManager().addExhaustion(40f);
                break;
            }
        }

        if (!world.isClient) {
            Vec3d pos = player.getEyePos();
            BlockHitResult blockHitResult = world.raycast(new RaycastContext(
                pos,
                pos.add(player.getRotationVector().multiply(maxRange)),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
            ));
            pos = blockHitResult.getPos();
            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                pos = pos.offset(blockHitResult.getSide(), 0.0625);
            }

            world.createExplosion(
                player,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                power,
                World.ExplosionSourceType.MOB
            );
        }
    }
}