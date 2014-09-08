package ztech.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemNetworkAnchor extends ItemBlock
{
    public ItemNetworkAnchor(Block block)
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
            	return "tile.networkAnchor";
            case 1:
            	return "tile.scanTerminator";
            default:
            	return super.getUnlocalizedName(stack);
        }
    }

    @Override
    public IIcon getIconFromDamage(int metadata)
    {
        return field_150939_a.getIcon(2, metadata);
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }
}
