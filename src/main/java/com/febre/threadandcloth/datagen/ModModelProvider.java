package com.febre.threadandcloth.datagen;

import com.febre.threadandcloth.block.ModBlocks;
import com.febre.threadandcloth.block.custom.CottonBush;
import com.febre.threadandcloth.item.ModItems;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.data.client.*;

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
                            if (half == DoubleBlockHalf.UPPER && age < 2) {
                                return BlockStateVariant.create().put(VariantSettings.MODEL,
                                        ModelIds.getBlockModelId(Blocks.AIR));
                            }
                            if (age < 2) {
                                return BlockStateVariant.create().put(VariantSettings.MODEL,
                                        blockStateModelGenerator.createSubModel(ModBlocks.COTTON_BUSH, "_age" + age, Models.CROSS, TextureMap::cross));
                            } else {
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

        itemModelGenerator.writer.accept(ModelIds.getItemModelId(ModItems.THIGH_HIGHS), () -> {
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", TextureMap.getId(ModItems.THIGH_HIGHS).toString());
            textures.addProperty("layer1", TextureMap.getId(ModItems.THIGH_HIGHS).withSuffixedPath("_overlay").toString());

            json.add("textures", textures);
            return json;
        });
    }


}
