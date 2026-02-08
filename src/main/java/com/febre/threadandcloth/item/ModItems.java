package com.febre.threadandcloth.item;

import com.febre.threadandcloth.ThreadAndCloth;
import com.febre.threadandcloth.block.ModArmorMaterials;
import com.febre.threadandcloth.block.ModBlocks;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item COTTON = registerItem("cotton", new AliasedBlockItem(ModBlocks.COTTON_BUSH, new Item.Settings()));
    public static final Item THREAD = registerItem("thread", new Item(new Item.Settings()));
    public static final Item CLOTH = registerItem("cloth", new Item(new Item.Settings()));

    public static final Item THIGH_HIGHS = registerItem("thigh_highs", new ArmorItem(ModArmorMaterials.THIGH_HIGHS_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ThreadAndCloth.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ThreadAndCloth.LOGGER.info("Registering Mod Items for " + ThreadAndCloth.MOD_ID);
    }

}
