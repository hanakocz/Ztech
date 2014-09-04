package ztech.init;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import ztech.Ztech;
import ztech.blocks.BlockRailEx;
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
		initElecticRails();
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

	private static void initElecticRails()
	{
		if (Ztech.config.enableElectricRails)
		{
			String RAILCRAFT = "Railcraft"; // RailCraft ModID (case sensitive)

			ItemStack stackIronIngot = new ItemStack(Items.iron_ingot);
			ItemStack stackIronCable = IC2Items.getItem("ironCableItem");

			ItemStack stackStandardRail  = GameRegistry.findItemStack(RAILCRAFT, "part.rail.standard", 1);
			ItemStack stackWoodenRailbed = GameRegistry.findItemStack(RAILCRAFT, "part.railbed.wood", 1);
			ItemStack stackStoneRailbed  = GameRegistry.findItemStack(RAILCRAFT, "part.railbed.stone", 1);
			
			ItemStack stackStandardTrack = new ItemStack(Blocks.rail);
			ItemStack stackStoneSlab = new ItemStack(Blocks.stone_slab);

			boolean isRailCraft = Loader.isModLoaded(RAILCRAFT) && stackStandardRail != null;

			// Electric Rail (RailCraft only)

			if (isRailCraft)
			{
				GameRegistry.addShapelessRecipe(
						ModItems.electricRail,
						new Object[] {
								stackStandardRail,
								stackIronCable
						});
			}

			// Standard Electric Track

			if (isRailCraft && stackWoodenRailbed != null)
			{
				// RailCraft recipe that uses electric rails
				GameRegistry.addRecipe(
						new ItemStack(ModBlocks.blockStandardElectricTrack, 16),
						new Object[] {
							"R R",
							"RBR",
							"R R",
							'R', ModItems.electricRail,
							'B', stackWoodenRailbed
						});
			}

			// Upgrade/crafting recipe for standard rails for both RailCraft and vanilla
			GameRegistry.addShapelessRecipe(
					new ItemStack(ModBlocks.blockStandardElectricTrack, 2),
					new Object[] {
						stackIronCable,
						stackStandardTrack,
						stackStandardTrack,
					});

			// Advanced Track

			if (isRailCraft && stackStoneRailbed != null)
			{
				// RailCraft
				GameRegistry.addRecipe(
						new ItemStack(ModBlocks.blockAdvancedTrack, 16),
						new Object[] {
							"R R",
							"RBR",
							"R R",
							'R', stackStandardRail,
							'B', stackStoneRailbed
						});
			}
			else
			{
				// Vanilla
				GameRegistry.addRecipe(
						new ItemStack(ModBlocks.blockAdvancedTrack, 16),
						new Object[] {
							"ISI",
							"ISI",
							"ISI",
							'I', stackIronIngot,
							'S', stackStoneSlab
						});
			}

			// Advanced Electric Track Recipe

			if (isRailCraft && stackStoneRailbed != null)
			{
				// RailCraft
				GameRegistry.addRecipe(
						new ItemStack(ModBlocks.blockAdvancedElectricTrack, 16),
						new Object[] {
							"R R",
							"RBR",
							"R R",
							'R', ModItems.electricRail,
							'B', stackStoneRailbed
						});
			}

			// Upgrade/crafting recipe for advanced rails for both RailCraft and vanilla
			GameRegistry.addShapelessRecipe(
					new ItemStack(ModBlocks.blockAdvancedElectricTrack, 2),
					new Object[] {
						stackIronCable,
						ModBlocks.blockAdvancedTrack,
						ModBlocks.blockAdvancedTrack
					});

			// "Third Rail"

			if (isRailCraft)
			{
				// RailCraft
				GameRegistry.addShapelessRecipe(
						new ItemStack(ModBlocks.blockThirdRail, 3),
						new Object[] {
							stackStoneSlab,
							ModItems.electricRail
						});
			}
			else
			{
				// Vanilla
				GameRegistry.addShapelessRecipe(
						new ItemStack(ModBlocks.blockThirdRail, 2),
						new Object[] {
							stackStoneSlab,
							stackIronCable
						});
			}

			// Maglev Rail (magnet block)

			GameRegistry.addRecipe(
					new ItemStack(ModBlocks.blockMaglevRail, 6),
					new Object[] {
						"SSS", "CCC", "SSS",
						'S', new ItemStack(Blocks.stone),
						'C', IC2Items.getItem("coil")
					});

			// Maglev Cover (physical rails)

			GameRegistry.addShapelessRecipe(
					new ItemStack(ModBlocks.blockMaglevCover, 2),
					new Object[] {
						stackStoneSlab,
						new ItemStack(Blocks.sand)
					});
		}
	}
}
