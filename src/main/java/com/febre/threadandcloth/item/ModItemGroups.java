package com.febre.threadandcloth.item;

import com.febre.threadandcloth.ThreadAndCloth;
import com.febre.threadandcloth.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup TC_INGREDIENTS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(ThreadAndCloth.MOD_ID, "tc_ingredients"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.THREAD))
                    .displayName(Text.translatable("itemgroup.thread-and-cloth.tc_ingredients"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.THREAD);
                        entries.add(ModItems.COTTON);
                        entries.add(ModItems.CLOTH);
                    })
            .build());

    public static final ItemGroup TC_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(ThreadAndCloth.MOD_ID, "tc_blocks"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.CLOTH_BLOCK))
                    .displayName(Text.translatable("itemgroup.thread-and-cloth.tc_blocks"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.CLOTH_BLOCK);
                    })
                    .build());

    public static void registerItemGroups() {
        ThreadAndCloth.LOGGER.info("Registering Mod Item Groups for " + ThreadAndCloth.MOD_ID);
    }
}
