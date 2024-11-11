package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.contamination.ContaminationHandler;
import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.init.FBombsStatusEffects;
import com.chailotl.fbombs.item.HazmatArmor;
import com.chailotl.fbombs.util.NbtKeys;
import com.chailotl.fbombs.util.cast.Contaminatable;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, Contaminatable {
    @Unique
    private static final TrackedData<Float> CPS = DataTracker.registerData(LivingEntityMixin.class, TrackedDataHandlerRegistry.FLOAT);

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CPS, 0.0f);
    }

    @Override
    public float fbombs$getCps() {
        return this.dataTracker.get(CPS);
    }

    @Override
    public void fbombs$setCps(float cps) {
        this.dataTracker.set(CPS, cps);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickContamination(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) return;
        if (this.age % 40 != 0) return;

        if (fbombs$getCps() > RadiationCategory.SAFE.getMaxCps()) {
            if (!entity.hasStatusEffect(FBombsStatusEffects.RADIATION_POISONING)) {
                ContaminationHandler.applyEffectIfMissing(entity, fbombs$getCps());
            }

            ContaminationHandler.applyCpsToInventoryAndEquipment(entity, fbombs$getCps());
        }

        float currentCps = ContaminationHandler.getLocationCps(serverWorld, entity.getBlockPos());
        currentCps = Math.max(currentCps, ContaminationHandler.getMaxContaminationInInventory(entity));

        if (currentCps > RadiationCategory.SAFE.getMaxCps()) {
            HazmatArmor.damageHazmatSet(entity);
            if (!HazmatArmor.hasFullSetEquipped(entity)) {
                float currentPlayerCps = fbombs$getCps();
                fbombs$setCps(currentPlayerCps + currentCps);
            }
        }

        if (fbombs$getCps() > 0) {
            RadiationCategory category = RadiationCategory.getRadiationCategory(fbombs$getCps());
            fbombs$setCps(Math.max(0, fbombs$getCps() - category.getCpsDecay()));
        }/*
        if (entity instanceof ServerPlayerEntity) {
            LoggerUtil.devLogger("cps: %s | category: %s | decay: %s".formatted(fbombs$getCps(), RadiationCategory.getRadiationCategory(fbombs$getCps()).name(), RadiationCategory.getRadiationCategory(fbombs$getCps()).getCpsDecay()));
        }*/
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readFBombsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NbtKeys.CPS)) {
            fbombs$setCps(nbt.getFloat(NbtKeys.CPS));
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeFBombsDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat(NbtKeys.CPS, fbombs$getCps());
    }
}
