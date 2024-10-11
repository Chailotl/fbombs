package com.chailotl.fbombs;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItemGroups;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FBombs implements ModInitializer {
	public static final String MOD_ID = "fbombs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		FBombsBlocks.initialize();
		FBombsItems.initialize();
		FBombsItemGroups.initialize();

		LOGGER.info("May contain traces of nuclear explosions");
	}

	public static Identifier getId(String path) {
		return Identifier.of(MOD_ID, path);
	}
}