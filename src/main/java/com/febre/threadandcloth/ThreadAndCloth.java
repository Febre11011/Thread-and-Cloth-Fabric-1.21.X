package com.febre.threadandcloth;

import com.febre.threadandcloth.block.ModBlocks;
import com.febre.threadandcloth.item.ModItemGroups;
import com.febre.threadandcloth.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadAndCloth implements ModInitializer {
	public static final String MOD_ID = "thread-and-cloth";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBLocks();
	}
}