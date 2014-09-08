package ztech.utils;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

public class UpgradeDictionary
{
    public static final String SPEED = "speed";
    public static final String STORAGE = "storage";
    public static final String TIER = "tier"; // e.g. transformers
    public static Set<UpgradeModule> upgrades = new HashSet<UpgradeModule>();

    public static void put(ItemStack stack, String type, int amplifier, int tier)
    {
        if (stack != null)
        {
        	upgrades.add(new UpgradeModule(stack, type, amplifier, tier));
        }
    }

    public static UpgradeModule get(ItemStack stack)
    {
        if (stack == null)
        {
        	return null;
        }
        for (UpgradeModule module : upgrades)
        {
            if (module == null || module.item != stack.getItem())
            {
            	continue;
            }
            if (stack.getHasSubtypes() && module.metadata != stack.getItemDamage())
            {
            	continue;
            }
            return module;
        }
        return null;
    }
}
