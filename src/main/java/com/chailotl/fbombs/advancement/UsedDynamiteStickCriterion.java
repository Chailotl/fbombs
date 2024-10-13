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

public class UsedDynamiteStickCriterion extends AbstractCriterion<UsedDynamiteStickCriterion.Conditions> {
    @Override
    public Codec<UsedDynamiteStickCriterion.Conditions> getConditionsCodec() {
        return UsedDynamiteStickCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, Conditions::matches);
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions {
        public static final Codec<UsedDynamiteStickCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(UsedDynamiteStickCriterion.Conditions::player)
                        )
                        .apply(instance, UsedDynamiteStickCriterion.Conditions::new)
        );

        public boolean matches() {
            return true;
        }

        public static AdvancementCriterion<UsedDynamiteStickCriterion.Conditions> any() {
            return FBombsCriteria.USED_DYNAMITE_STICK.create(new UsedDynamiteStickCriterion.Conditions(Optional.empty()));
        }
    }
}
