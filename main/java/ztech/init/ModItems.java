package ztech.init;

import ztech.Ztech;
import ztech.items.ItemAdvancedElectricRod;
import ztech.items.ItemElectricRod;
import net.minecraft.item.ItemStack;

public class ModItems
{
	// Electic Fishing Rod
    public static ItemStack electricFishingRod;
    public static ItemStack advancedElectricFishingRod;
    
    public static void init()
    {
    	initElecticFishingRod();
    }
    
    private static void initElecticFishingRod()
    {
    	if (Ztech.config.enableElecticFishingRod)
    	{
    		electricFishingRod = new ItemStack(new ItemElectricRod("electricFishingRod"), 1);
    		advancedElectricFishingRod = new ItemStack(new ItemAdvancedElectricRod("advancedElectricFishingRod"), 1);
    	}
    }
}
