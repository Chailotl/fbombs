package com.chailotl.fbombs.item;

import com.chailotl.fbombs.init.FBombsArmorMaterials;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class HazmatArmor extends ArmorItem {
    public static final int HAZMAT_MATERIAL_DURABILITY_MULTIPLIER = 5;

    public HazmatArmor(Type type, Settings settings) {
        super(FBombsArmorMaterials.HAZMAT, type, settings.maxDamage(type.getMaxDamage(HAZMAT_MATERIAL_DURABILITY_MULTIPLIER)));
    }

    public static boolean hasFullSetEquipped(LivingEntity entity) {
        for (ItemStack stack : entity.getArmorItems()) {
            if (!(stack.getItem() instanceof HazmatArmor)) return false;
        }
        return true;
    }

    public static void damageHazmatSet(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld)) return;
        if (entity instanceof PlayerEntity player && player.isInCreativeMode()) return;
        for (ItemStack stack : entity.getArmorItems()) {
            if (!(stack.getItem() instanceof HazmatArmor armor)) continue;
            stack.damage(1, entity, armor.getSlotType());
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
