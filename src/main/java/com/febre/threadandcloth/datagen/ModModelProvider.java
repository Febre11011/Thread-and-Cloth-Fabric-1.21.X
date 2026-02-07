package com.febre.threadandcloth.datagen;

import com.febre.threadandcloth.ThreadAndCloth;
import com.febre.threadandcloth.block.ModBlocks;
import com.febre.threadandcloth.block.custom.CottonBush;
import com.febre.threadandcloth.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CLOTH_BLOCK);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.COTTON_BUSH)
                .coordinate(BlockStateVariantMap.create(CottonBush.AGE, CottonBush.HALF)
                        .register((age, half) -> {

                            // PODMÍNKA: Pokud je to vršek pro věk 0 nebo 1, nasměrujeme to na AIR
                            if (half == DoubleBlockHalf.UPPER && age < 2) {
                                return BlockStateVariant.create().put(VariantSettings.MODEL,
                                        ModelIds.getBlockModelId(Blocks.AIR));
                            }

                            // Generování názvů modelů podle věku a části
                            if (age < 2) {
                                // Pro věk 0 a 1 (vždy jen LOWER díky podmínce nahoře)
                                return BlockStateVariant.create().put(VariantSettings.MODEL,
                                        blockStateModelGenerator.createSubModel(ModBlocks.COTTON_BUSH, "_age" + age, Models.CROSS, TextureMap::cross));
                            } else {
                                // Pro věk 2, 3, 4 (LOWER i UPPER)
                                String suffix = (half == DoubleBlockHalf.LOWER ? "_bottom" : "_top");
                                return BlockStateVariant.create().put(VariantSettings.MODEL,
                                        blockStateModelGenerator.createSubModel(ModBlocks.COTTON_BUSH, "_age" + age + suffix, Models.CROSS, TextureMap::cross));
                            }
                        })
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.THREAD, Models.GENERATED);
        itemModelGenerator.register(ModItems.CLOTH, Models.GENERATED);
        itemModelGenerator.register(ModItems.COTTON, Models.GENERATED);
    }


}
