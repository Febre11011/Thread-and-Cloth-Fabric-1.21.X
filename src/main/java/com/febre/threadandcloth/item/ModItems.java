package com.febre.threadandcloth.item;

import com.febre.threadandcloth.ThreadAndCloth;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item COTTON = registerItem("cotton", new Item(new Item.Settings()));
    public static final Item THREAD = registerItem("thread", new Item(new Item.Settings()));
    public static final Item CLOTH = registerItem("cloth", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ThreadAndCloth.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ThreadAndCloth.LOGGER.info("Registering Mod Items for " + ThreadAndCloth.MOD_ID);
    }

}
