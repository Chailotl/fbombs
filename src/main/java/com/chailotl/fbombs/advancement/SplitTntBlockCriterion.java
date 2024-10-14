package com.chailotl.fbombs.advancement;

import com.chailotl.fbombs.init.FBombsCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class SplitTntBlockCriterion extends AbstractCriterion<SplitTntBlockCriterion.Conditions> {
    @Override
    public Codec<SplitTntBlockCriterion.Conditions> getConditionsCodec() {
        return SplitTntBlockCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, Conditions::matches);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<SplitTntBlockCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(SplitTntBlockCriterion.Conditions::player)
                        )
                        .apply(instance, SplitTntBlockCriterion.Conditions::new)
        );

        public boolean matches() {
            return true;
        }

        public static AdvancementCriterion<SplitTntBlockCriterion.Conditions> any() {
            return FBombsCriteria.SPLIT_TNT_BLOCK.create(new SplitTntBlockCriterion.Conditions(Optional.empty()));
        }
    }
}
