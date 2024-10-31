package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.item.HazmatArmor;
import com.chailotl.fbombs.util.NbtKeys;
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
public abstract class LivingEntityMixin extends Entity implements Attackable {
    @Unique
    private static final TrackedData<Float> CPS = DataTracker.registerData(LivingEntityMixin.class, TrackedDataHandlerRegistry.FLOAT);

    @Unique
    private int tick = 0;

    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void addCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(CPS, 0.0f);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readFBombsDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NbtKeys.CPS)) {
            this.dataTracker.set(CPS, nbt.getFloat(NbtKeys.CPS));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void addCpsPerTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity.getWorld() instanceof ServerWorld world)) return;
        this.tick++;
        if (tick % 40 != 0) return;

        //TODO: [ShiroJR] get BlockPos cps value from PersistentState
        float locationCps = 0;  // placeholder!

        if (locationCps > 0) {
            HazmatArmor.damageHazmatSet(entity);
            if (!HazmatArmor.hasFullSetEquipped(entity)) {
                float currentPlayerCps = this.dataTracker.get(CPS);
                this.dataTracker.set(CPS, currentPlayerCps + locationCps);
            }
        }
    }
}
