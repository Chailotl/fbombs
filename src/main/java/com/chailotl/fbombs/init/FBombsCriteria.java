package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.advancement.SplitTntBlockCriterion;
import com.chailotl.fbombs.advancement.UsedDynamiteCriterion;
import com.chailotl.fbombs.advancement.WaterloggedTntBlockCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("SameParameterValue")
public class FBombsCriteria {
    public static final UsedDynamiteCriterion USED_DYNAMITE = register("used_dynamite", new UsedDynamiteCriterion());
    public static final SplitTntBlockCriterion SPLIT_TNT_BLOCK = register("split_tnt_block", new SplitTntBlockCriterion());
    public static final WaterloggedTntBlockCriterion WATERLOGGED_TNT_BLOCK = register("waterlogged_tnt_block", new WaterloggedTntBlockCriterion());


    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, FBombs.getId(name), criterion);
    }

    public static void initialize() {
        // static initialisation
    }
}
