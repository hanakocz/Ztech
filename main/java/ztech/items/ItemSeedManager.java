package ztech.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemSeedManager extends ItemBlock
{
	public ItemSeedManager(Block block)
	{
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		switch (stack.getItemDamage())
		{
		case 0:
			return "tile.seedAnalyzer";
		case 1:
			return "tile.seedLibrary";
		default:
			return super.getUnlocalizedName(stack);
		}
	}

	@Override
	public IIcon getIconFromDamage(int metadata)
	{
		//TODO
		//return field_150939_a.getIcon(2, metadata);
		return null;
	}

	@Override
	public int getMetadata(int metadata)
	{
		return metadata;
	}
}
