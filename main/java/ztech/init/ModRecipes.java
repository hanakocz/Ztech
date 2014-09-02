package ztech.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import ztech.Ztech;
import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;

public class ModRecipes
{
	public static void init()
	{
		initElecticFishingRod();
		initNuclearPan();
	}

	private static void initElecticFishingRod()
	{
		if (Ztech.config.enableElecticFishingRod)
		{
			Recipes.advRecipes.addRecipe(
					ModItems.electricFishingRod,
					new Object[] {
							"  M",
							" T ",
							"C  ",
							'T', IC2Items.getItem("teslaCoil"),
							'C', StackUtil.copyWithWildCard(IC2Items.getItem("energyCrystal")),
							'M', IC2Items.getItem("magnetizer")
					});
			Recipes.advRecipes.addRecipe(
					ModItems.advancedElectricFishingRod,
					new Object[] {
							"  R",
							" A ",
							"C  ",
							'A', IC2Items.getItem("advancedCircuit"),
							'C', StackUtil.copyWithWildCard(IC2Items.getItem("lapotronCrystal")),
							'R', StackUtil.copyWithWildCard(ModItems.electricFishingRod)
					});
		}
	}
	
    private static void initNuclearPan()
    {
    	if (Ztech.config.enableNuclearPan)
    	{
			FurnaceRecipes.smelting().func_151396_a(Items.sugar, ModItems.meltedSugar, 0.1F);
			Recipes.macerator.addRecipe(new RecipeInputItemStack(ModItems.meltedSugar, 1), null, new ItemStack(Items.sugar));

			Recipes.advRecipes.addShapelessRecipe(
					ModItems.sugarCandy,
					new Object[] {
							ModItems.meltedSugar,
							ModItems.meltedSugar
					});
			FurnaceRecipes.smelting().func_151396_a(ModItems.sugarCandy.getItem(), new ItemStack(ModItems.meltedSugar.getItem(), 2), 0.1F);
			Recipes.macerator.addRecipe(new RecipeInputItemStack(ModItems.sugarCandy, 1), null, new ItemStack(Items.sugar, 2));

			Recipes.advRecipes.addShapelessRecipe(
					ModItems.lollipop,
					new Object[] {
							ModItems.meltedSugar,
							ModItems.meltedSugar,
							ModItems.meltedSugar,
							new ItemStack(Items.stick)
					});    
			
	        Recipes.advRecipes.addRecipe(
	                new ItemStack(ModBlocks.nuclearPan),
	                new Object[] {
	                    	"   ",
	                    	"RAR",
	                    	"RCR",
	                    	'R', IC2Items.getItem("plateiron"),
	                    	'A', IC2Items.getItem("advancedAlloy"),
	                    	'C', IC2Items.getItem("denseplatelead")
	                });
	        
	        Ztech.config.addToWhiteList(IC2Items.getItem("mugCoffee"));
	        Ztech.config.addToWhiteList(new ItemStack(Blocks.cactus));
	        Ztech.config.addToWhiteList(new ItemStack(Items.sugar));
    	}
    }
}
