package com.febre.threadandcloth.datagen;

import com.febre.threadandcloth.block.ModBlocks;
import com.febre.threadandcloth.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.THREAD, 16)
                .pattern("CCC")
                .pattern("CSC")
                .pattern("CCC")
                .input('C', ModItems.COTTON)
                .input('S', Items.STICK)
                .criterion(hasItem(ModItems.COTTON), conditionsFromItem(ModItems.COTTON))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CLOTH)
                .pattern("TT")
                .pattern("TT")
                .input('T', ModItems.THREAD)
                .criterion(hasItem(ModItems.THREAD), conditionsFromItem(ModItems.THREAD))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CLOTH_BLOCK)
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .input('C', ModItems.CLOTH)
                .criterion(hasItem(ModItems.CLOTH), conditionsFromItem(ModItems.CLOTH))
                .offerTo(recipeExporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.THIGH_HIGHS)
                .pattern("C C")
                .pattern("C C")
                .pattern("C C")
                .input('C', ModItems.CLOTH)
                .criterion(hasItem(ModItems.CLOTH), conditionsFromItem(ModItems.CLOTH))
                .offerTo(recipeExporter);
    }
}
