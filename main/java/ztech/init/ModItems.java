package ztech.init;

import ztech.Ztech;
import ztech.items.ItemAdvancedElectricRod;
import ztech.items.ItemElectricRod;
import ztech.items.ItemFoodOnAStick;
import ztech.items.ItemPanFood;
import net.minecraft.item.ItemStack;

public class ModItems
{
	// Electic Fishing Rod
    public static ItemStack electricFishingRod;
    public static ItemStack advancedElectricFishingRod;
    // Nuclear Pan
    public static ItemStack meltedSugar;
    public static ItemStack sugarCandy;
    public static ItemStack lollipop;
    
    public static void init()
    {
    	initElecticFishingRod();
    	initNuclearPan();
    }
    
    private static void initElecticFishingRod()
    {
    	if (Ztech.config.enableElecticFishingRod)
    	{
    		electricFishingRod = new ItemStack(new ItemElectricRod("electricFishingRod"));
    		advancedElectricFishingRod = new ItemStack(new ItemAdvancedElectricRod("advancedElectricFishingRod"));
    	}
    }
    
    private static void initNuclearPan()
    {
    	if (Ztech.config.enableNuclearPan)
    	{
	        meltedSugar = new ItemStack(new ItemPanFood("meltedSugar", 2, 0.0F, false));
	        sugarCandy = new ItemStack(new ItemPanFood("sugarCandy", 4, 0.0F, false));
	        lollipop = new ItemStack(new ItemFoodOnAStick("lollipop", 8, 0.0F, false));
    	}    	
    }
}
