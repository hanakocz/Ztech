package ztech.containers;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.slot.SlotInvSlot;
import ztech.tileentities.TileEntitySeedLibrary;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSeedLibrary extends ContainerElectricMachine
{
    public TileEntitySeedLibrary seedlibrary;
    
    public ContainerSeedLibrary(EntityPlayer player, TileEntitySeedLibrary seedlibrary)
    {
    	super(player, seedlibrary, 222, 8, 108);
    	
    	this.seedlibrary = seedlibrary;
    	
        for (int i = 1; i < 9; i++)
        {
           addSlotToContainer(new SlotInvSlot(seedlibrary.invSlot[i - 1], 0, 8 + i * 18, 108));
        }

        addSlotToContainer(new SlotInvSlot(seedlibrary.fakeSlot, 0, 38, 16));
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return seedlibrary.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack slotClick(int i, int j, int k, EntityPlayer entityplayer)
    {
        if (i == 45)
        {
            // Clicked the "take a seed's type" slot.
            ItemStack seed = entityplayer.inventory.getItemStack();
            seedlibrary.getGUIFilter().setCropFromSeed(seed);
            return null;
        }
        return super.slotClick(i, j, k, entityplayer);
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);

        seedlibrary.updateGUIFilter();
        seedlibrary.updateSeedCount();
    }
}
