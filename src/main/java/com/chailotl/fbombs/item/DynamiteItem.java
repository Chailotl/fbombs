package com.chailotl.fbombs.item;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.DynamiteEntity;
import com.chailotl.fbombs.entity.util.DynamiteEntityProviderOwner;
import com.chailotl.fbombs.entity.util.DynamiteEntityProviderPos;
import com.chailotl.fbombs.init.FBombsCriteria;
import com.chailotl.fbombs.init.FBombsTags;
import com.chailotl.fbombs.util.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.List;

public class DynamiteItem extends Item implements ProjectileItem {
    protected final DynamiteEntityProviderOwner dynamiteEntityProviderOwner;
    protected final DynamiteEntityProviderPos dynamiteEntityProviderPos;

    public DynamiteItem(DynamiteEntityProviderOwner dynamiteEntityProviderOwner, DynamiteEntityProviderPos dynamiteEntityProviderPos, Item.Settings settings) {
        super(settings);
        this.dynamiteEntityProviderOwner = dynamiteEntityProviderOwner;
        this.dynamiteEntityProviderPos = dynamiteEntityProviderPos;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.fbombs.dynamite_stick.tooltip").formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        ItemStack offStack = user.getOffHandStack();

        //TODO: [ShiroJR] make it switchable in hands

        if (!offStack.isIn(FBombsTags.Items.IGNITES_TNT)) {
            return super.use(world, user, hand);
        }

        if (!world.isClient) {
            DynamiteEntity dynamiteEntity = dynamiteEntityProviderOwner.spawn(world, user);
            dynamiteEntity.setItem(stack);
            dynamiteEntity.setVelocity(user, user.getPitch(), user.getYaw(), -10.0f, 0.75f, 1.0f);
            world.spawnEntity(dynamiteEntity);

            FBombsCriteria.USED_DYNAMITE.trigger((ServerPlayerEntity) user);
        }

        FBombs.streamEntries(Registries.ITEM).forEach(item -> {
            if (item instanceof DynamiteItem) {
                user.getItemCooldownManager().set(item, 10);
            }
        });
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        ItemStackHelper.decrementOrDamageInNonCreative(stack, 1, user);
        ItemStackHelper.decrementOrDamageInNonCreative(offStack, 1, user);
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        DynamiteEntity dynamiteEntity = dynamiteEntityProviderPos.spawn(world, pos.getX(), pos.getY(), pos.getZ());
        dynamiteEntity.setItem(stack);
        return dynamiteEntity;
    }
}
