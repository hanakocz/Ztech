package ztech.items;

import ztech.Ztech;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemElectricRail extends Item
{
	public ItemElectricRail (String name)
	{
		super();
		setMaxStackSize(64);
		setUnlocalizedName(name);
		setTextureName(Ztech.MODID + ":" + name);
		setCreativeTab(Ztech.tabMod);
		GameRegistry.registerItem(this, name);
	}
}
