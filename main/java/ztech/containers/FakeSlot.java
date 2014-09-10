package ztech.containers;

import ztech.tileentities.TileEntitySeedLibrary;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class FakeSlot extends InvSlot
{
    public FakeSlot(TileEntitySeedLibrary tile)
    {
        super(tile, "fake", 10, InvSlot.Access.NONE, 1);
    }
    
    @Override
    public boolean accepts(ItemStack itemStack)
    {
      return false;
    }
}
