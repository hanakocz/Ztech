package ztech.init;

import ztech.Ztech;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModCreativeTab extends CreativeTabs
{
	private static ItemStack icon;

	public ModCreativeTab()
	{
		super(Ztech.MODNAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack()
	{
		if (icon == null)
		{
			icon = ModItems.electricFishingRod;
		}
		return icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return ModItems.electricFishingRod.getItem();
	}

	@Override
	public String getTranslatedTabLabel()
	{
		return Ztech.MODNAME;
	}
}