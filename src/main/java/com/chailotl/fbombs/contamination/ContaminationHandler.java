package com.chailotl.fbombs.contamination;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.data.RadiationData;
import com.chailotl.fbombs.init.FBombsItemComponents;
import com.chailotl.fbombs.init.FBombsStatusEffects;
import net.minecraft.entity.EquipmentHolder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ContaminationHandler {
    public static void applyEffectIfMissing(LivingEntity entity, float currentCps) {
        if (entity.hasStatusEffect(FBombsStatusEffects.RADIATION_POISONING)) return;
        int duration = (int) (currentCps) * 20;
        int amplifier = RadiationCategory.getRadiationCategory(currentCps).getAmplifier();

        entity.addStatusEffect(new StatusEffectInstance(FBombsStatusEffects.RADIATION_POISONING, duration, amplifier,
                true, false, true), entity);
    }

    public static float getMaxContaminationInInventory(LivingEntity player) {
        float contamination = 0f;

        for (ItemStack handStack : player.getHandItems()) {
            contamination = Math.max(contamination, handStack.getOrDefault(FBombsItemComponents.CONTAMINATION, 0f));
        }
        for (ItemStack armorStack : player.getArmorItems()) {
            contamination = Math.max(contamination, armorStack.getOrDefault(FBombsItemComponents.CONTAMINATION, 0f));
        }
        if (player instanceof Inventory inventory) {
            for (int i = 0; i < inventory.size(); i++) {
                contamination = Math.max(contamination, inventory.getStack(i).getOrDefault(FBombsItemComponents.CONTAMINATION, 0f));
            }
        }

        return contamination;
    }

    public static void applyCpsToInventoryAndEquipment(LivingEntity entity, float cps) {
        if (entity.getWorld().isClient()) return;
        if (entity instanceof Inventory inventory) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isEmpty()) continue;
                stack.set(FBombsItemComponents.CONTAMINATION, cps);
            }
        } else if (entity instanceof EquipmentHolder equipmentHolder) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!entity.canUseSlot(slot)) continue;
                ItemStack stack = equipmentHolder.getEquippedStack(slot);
                if (stack.isEmpty()) continue;
                stack.set(FBombsItemComponents.CONTAMINATION, cps);
            }
        }
    }

    /**
     * Gets the CPS level of the given BlockPos. This includes the center pos and pos which are still in
     * contamination range. If multiple contamination spots overlap, they will be added on top of each
     * other.
     *
     * @param pos BlockPos to check
     * @return CPS level of the given BlockPos in the world
     */
    public static float getLocationCps(ServerWorld world, BlockPos pos) {
        float cps = 0;
        for (RadiationData radiationSpot : FBombs.getCachedPersistentState(world).getRadiationSources()) {
            float centerCps = radiationSpot.cps();
            if (radiationSpot.pos().equals(pos)) {
                cps += centerCps;
                continue;
            }
            double sqDistance = radiationSpot.pos().getSquaredDistance(pos);
            float sqRadius = radiationSpot.radius() * radiationSpot.radius();
            if (sqDistance <= sqRadius) {
                double normalizedDistance = sqDistance / sqRadius;
                cps += (float) MathHelper.lerp(normalizedDistance, 0, centerCps);
            }
        }
        return cps;
    }

    public static void addContaminationSpot(ServerWorld world, RadiationData radiationData) {
        FBombs.modifyCachedPersistentState(world, state -> {
            for (RadiationData storedData : state.getRadiationSources()) {
                if (storedData.pos().equals(radiationData.pos())) return;
            }
            state.getRadiationSources().add(radiationData);
        });
    }

    /**
     * Removes contamination spot of specified BlockPos. This also removes all spots, which are still in range
     * and may remove multiple contaminated spots, if they were overlapping
     */
    public static List<RadiationData> clearContaminationSpot(ServerWorld world, BlockPos pos) {
        List<RadiationData> deletedSpots = new ArrayList<>();
        FBombs.modifyCachedPersistentState(world, state -> {
            List<RadiationData> radiationSpots = state.getRadiationSources();
            for (RadiationData data : radiationSpots) {
                if (data.pos().equals(pos)) {
                    deletedSpots.add(data);
                    radiationSpots.remove(data);
                } else if (data.pos().getSquaredDistance(pos) <= data.radius() * data.radius()) {
                    deletedSpots.add(data);
                    radiationSpots.remove(data);
                }
            }
        });
        return deletedSpots;
    }

    public static void clearContaminationSpot(ServerWorld world, RadiationData radiationData) {
        FBombs.modifyCachedPersistentState(world, state -> state.getRadiationSources().remove(radiationData));
    }

    public static void clearAllContaminationSpots(ServerWorld world) {
        FBombs.modifyCachedPersistentState(world, state -> state.getRadiationSources().clear());
    }
}
