package com.chailotl.fbombs.data;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.ArrayUtils;

public enum RadiationCategory implements StringIdentifiable {
    SAFE(0.6f, 0, 0.1f, -1),
    LOW(1.25f, 1, 0.02f, -0),
    MID(6f, 3, 0.08f, 1),
    HIGH(25f, 8, 0.07f, 3),
    DEADLY(250f, 20, 1f, 5),
    INSTANT_DEATH(25_000f, 100, 10f, 10);

    private final float maxCps;
    private final int amplifier;
    private final float cpsDecay;
    private final int durationIncrease;

    RadiationCategory(float maxCps, int effectAmplifier, float cpsDecay, int durationIncrease) {
        this.maxCps = maxCps;
        this.amplifier = effectAmplifier;
        this.cpsDecay = cpsDecay;
        this.durationIncrease = durationIncrease;
    }

    public float getMinCps() {
        if (this.equals(SAFE)) return 0.0f;
        return this.stepDown().getMaxCps();
    }

    public float getMaxCps() {
        return maxCps;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public float getCpsDecay() {
        return cpsDecay;
    }

    public int getDurationIncrease() {
        return durationIncrease;
    }

    public static RadiationCategory getRadiationCategory(float cps) {
        RadiationCategory output = SAFE;
        for (RadiationCategory category : RadiationCategory.values()) {
            if (cps > category.getMaxCps()) output = output.stepUp();
        }
        return output;
    }

    public RadiationCategory stepDown() {
        int index = ArrayUtils.indexOf(RadiationCategory.values(), this);
        if (index <= 0) return SAFE;
        return RadiationCategory.values()[index - 1];
    }

    public RadiationCategory stepUp() {
        int index = ArrayUtils.indexOf(RadiationCategory.values(), this);
        if (index == -1) return SAFE;
        if (index == RadiationCategory.values().length - 1) return INSTANT_DEATH;
        return RadiationCategory.values()[index + 1];
    }

    @Override
    public String asString() {
        return this.name();
    }

    public static class ArgumentType extends EnumArgumentType<RadiationCategory> {
        public static final Codec<RadiationCategory> CODEC = StringIdentifiable.createCodec(RadiationCategory::values);

        private ArgumentType() {
            super(CODEC, RadiationCategory::values);
        }

        public static ArgumentType contaminationCategory() {
            return new ArgumentType();
        }

        public static RadiationCategory getRadiationCategory(CommandContext<ServerCommandSource> context, String id) {
            return context.getArgument(id, RadiationCategory.class);
        }
    }
}
