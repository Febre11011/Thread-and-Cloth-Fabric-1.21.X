package com.febre.threadandcloth;

import com.febre.threadandcloth.block.ModBlocks;
import com.febre.threadandcloth.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;

public class ThreadAndClothClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COTTON_BUSH, RenderLayer.getCutout());

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            // Barvíme pouze základní vrstvu (layer0)
            if (tintIndex == 0) {
                DyedColorComponent colorComponent = stack.get(DataComponentTypes.DYED_COLOR);
                if (colorComponent != null) {
                    // Přidáme alfa kanál (0xFF000000), aby barva nebyla průhledná
                    return colorComponent.rgb() | 0xFF000000;
                }
                // Pokud není obarveno, vrátíme -1 (původní barva textury)
                return -1;
            }
            // Pruhy a ostatní necháme být
            return -1;
        }, ModItems.THIGH_HIGHS);
    }
}
