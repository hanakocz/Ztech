package ztech.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class UpgradeModule
{
	public final Item item;
	public final int metadata;
	public final String type;
	public final int amplifier;
	public final int tier;

	public UpgradeModule(ItemStack stack, String type, int amplifier, int tier)
	{
		this.item = stack.getItem();
		this.metadata = stack.getHasSubtypes() ? stack.getItemDamage() : -1;
		this.type = type;
		this.amplifier = amplifier;
		this.tier = tier;
	}
}
