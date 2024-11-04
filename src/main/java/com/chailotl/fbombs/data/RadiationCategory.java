package com.chailotl.fbombs.data;

import org.apache.commons.lang3.ArrayUtils;

public enum RadiationCategory {
    SAFE(0.6f),
    LOW(1.25f),
    MID(6f),
    HIGH(25f),
    DEADLY(250f),
    INSTANT_DEATH(25_000f);

    private final float maxCps;

    RadiationCategory(float maxCps) {
        this.maxCps = maxCps;
    }

    public float getMinCps() {
        if (this.equals(SAFE)) return 0.0f;
        return this.stepDown().getMaxCps();
    }

    public float getMaxCps() {
        return maxCps;
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
        if (index == - 1) return SAFE;
        if  (index == RadiationCategory.values().length - 1) return INSTANT_DEATH;
        return RadiationCategory.values()[index + 1];
    }
}
