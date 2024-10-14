package com.chailotl.fbombs;

import com.chailotl.fbombs.init.*;
import com.chailotl.fbombs.network.FBombsC2SNetworking;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class FBombs implements ModInitializer {
	public static final String MOD_ID = "fbombs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		FBombsBlocks.initialize();
		FBombsItems.initialize();
		FBombsItemGroups.initialize();
		FBombsBlockEntities.initialize();
		FBombsEntityTypes.initialize();
		FBombsC2SNetworking.initialize();
		FBombsNetworkPayloads.initialize();
		FBombsGamerules.initialize();
		FBombsItemComponents.initialize();
		FBombsCriteria.initialize();
		FBombsCommonEvents.initialize();

		LOGGER.info("May contain traces of nuclear explosions");
	}

	public static Identifier getId(String path) {
		return Identifier.of(MOD_ID, path);
	}
	public static Identifier getCommonId(String path) {
		return Identifier.of("c", path);
	}
}