package ztech.items;

import ztech.Ztech;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemFood;

public class ItemPanFood  extends ItemFood
{
	public ItemPanFood(String name, int p_i45339_1_, float p_i45339_2_, boolean p_i45339_3_)
	{
		super(p_i45339_1_, p_i45339_2_, p_i45339_3_);
		setUnlocalizedName(name);
		setTextureName(Ztech.MODID + ":" + name);
		GameRegistry.registerItem(this, name);		
	}
}
