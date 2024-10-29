package com.chailotl.fbombs.data;

import com.chailotl.fbombs.FBombs;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScorchedBlockDataLoader implements SimpleSynchronousResourceReloadListener {
    public static final HashMap<List<Block>, List<Block>> SCORCHED_VARIANT_HOLDER = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return FBombs.getId("scorched_block_replacement");
    }

    @Override
    public void reload(ResourceManager manager) {
        var resources = manager.findResources("scorched_block_replacement", id -> id.getPath().endsWith(".json"));
        resources.forEach((identifier, resource) -> {
            try {
                InputStream stream = resource.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (var replacementBlockData : data.entrySet()) {
                    if (!replacementBlockData.getValue().isJsonArray()) {
                        FBombs.LOGGER.error("Couldn't read {}. Please make sure it's an JSON array", replacementBlockData.getKey());
                        continue;
                    }

                    List<Block> originalEntries = new ArrayList<>(getEntries(replacementBlockData.getKey()));
                    List<Block> replacementEntries = new ArrayList<>();
                    for (JsonElement replacementBlockEntry : replacementBlockData.getValue().getAsJsonArray()) {
                        replacementEntries.addAll(getEntries(replacementBlockEntry.getAsString()));
                    }
                    SCORCHED_VARIANT_HOLDER.put(originalEntries, replacementEntries);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static List<Block> getEntries(String jsonString) {
        List<Block> blocks = new ArrayList<>();
        if (jsonString.startsWith("#")) {
            TagKey<Block> blockTag = TagKey.of(RegistryKeys.BLOCK, Identifier.of(jsonString.substring(1)));
            List<Block> blocksFromTag = Registries.BLOCK.stream().filter(block -> block.getDefaultState().isIn(blockTag)).toList();
            blocks.addAll(blocksFromTag);
        } else {
            Block block = Registries.BLOCK.get(Identifier.of(jsonString));
            if (block.equals(Blocks.AIR)) {
                FBombs.LOGGER.warn("Either added \"Air\" Block entry or couldn't read [%s] and will be skipped"
                        .formatted(jsonString));
            }
            blocks.add(block);
        }
        return blocks;
    }

    @Nullable
    public static Map.Entry<List<Block>, List<Block>> getEntry(Block block) {
        for (var dataHolderEntry : SCORCHED_VARIANT_HOLDER.entrySet()) {
            if (dataHolderEntry.getKey().contains(block)) return dataHolderEntry;
        }
        return null;
    }

    public static void initialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ScorchedBlockDataLoader());
    }
}
