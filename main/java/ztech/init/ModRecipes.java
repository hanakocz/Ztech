package ztech.init;

import ztech.Ztech;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;

public class ModRecipes
{
    public static void init()
    {
    	initElecticFishingRod();
    }
    
    private static void initElecticFishingRod()
    {
    	if (Ztech.config.enableElecticFishingRod)
    	{
    		Recipes.advRecipes.addRecipe(ModItems.electricFishingRod, new Object[] {"  M", " T ", "C  ", 'T', IC2Items.getItem("teslaCoil"), 'C', StackUtil.copyWithWildCard(IC2Items.getItem("energyCrystal")), 'M', IC2Items.getItem("magnetizer") } );
    		Recipes.advRecipes.addShapelessRecipe(ModItems.advancedElectricFishingRod, new Object[] {IC2Items.getItem("advancedCircuit"), StackUtil.copyWithWildCard(IC2Items.getItem("lapotronCrystal")), StackUtil.copyWithWildCard(ModItems.electricFishingRod) } );
    	}
    }
}
