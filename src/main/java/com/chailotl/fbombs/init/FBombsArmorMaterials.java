package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("SameParameterValue")
public class FBombsArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> HAZMAT = register("hazmat", "", Map.of(
                    ArmorItem.Type.BODY, 5,
                    ArmorItem.Type.HELMET, 1,
                    ArmorItem.Type.CHESTPLATE, 3,
                    ArmorItem.Type.LEGGINGS, 2,
                    ArmorItem.Type.BOOTS, 1
            ), 9, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, () -> Ingredient.ofItems(Items.PAPER),
            1.0f, 0.0f, false);


    private static RegistryEntry<ArmorMaterial> register(String name, String layerSuffix, Map<ArmorItem.Type, Integer> defense,
                                                         int enchantability, RegistryEntry<SoundEvent> equipSound,
                                                         Supplier<Ingredient> repairIngredientSupplier, float toughness,
                                                         float knockbackResistance, boolean dyeable) {
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(FBombs.getId(name), layerSuffix, dyeable));
        ArmorMaterial material = new ArmorMaterial(defense, enchantability, equipSound, repairIngredientSupplier, layers, toughness, knockbackResistance);
        material = Registry.register(Registries.ARMOR_MATERIAL, FBombs.getId(name), material);
        return RegistryEntry.of(material);
    }


    public static void initialize() {
        // static initialisation
    }
}
