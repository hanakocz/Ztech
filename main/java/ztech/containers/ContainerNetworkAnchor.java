package ztech.containers;

import ztech.tileentities.TileEntityNetworkAnchor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerNetworkAnchor extends Container
{
    public TileEntityNetworkAnchor tile;

    public byte scan; // bitfield: 0-enable, 1-optimize
    public short area; // Radius of chunk grid in area mode. NxN, where N = 2 * area + 1.
    public long tilesFound, chunksForced, chunksFound, ticketsUsed, capacity, energy;

    public ContainerNetworkAnchor(EntityPlayer player, TileEntityNetworkAnchor tile)
    {
        // Remember associated tile entity
        this.tile = tile;

        // Add universal/upgrade slots
        addSlotToContainer(new Slot(tile, 0, 152, 8));
        addSlotToContainer(new Slot(tile, 1, 152, 26));
        addSlotToContainer(new Slot(tile, 2, 152, 44));
        addSlotToContainer(new Slot(tile, 3, 152, 62));

        // Player inventory slots
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 9; y++)
                addSlotToContainer(new Slot(player.inventory, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));

        // Player hotbar slots
        for (int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUseableByPlayer(player);
    }

    // replace short part inside long. parts: 0 - lowest, 4 - highest
    public long tolong(long n, int part, long s)
    {
        part *= 16;
        long m = 0xFFFF;
        s &= m;
        s <<= part;
        m <<= part;
        long i = -1;
        m ^= i;
        n &= m;
        n |= s;
        return n;
    }

    // replace short part inside double. parts: 0 - lowest, 4 - highest
    /*public double todouble(double n, int part, long s)
    {
        long l = Double.doubleToRawLongBits(n);
        l = tolong(l, part, s);
        return Double.longBitsToDouble(l);
    }*/

    // send four parts of long value
    public void sendLong(ICrafting crafting, int base, long value)
    {
        for (int i = 0; i < 4; i++)
        {
            crafting.sendProgressBarUpdate(this, base+i, (short) (value & 0xFFFF));
            value >>= 16;
        }
    }

    // sand four part of double value as raw shorts
    /*public void sendDouble(ICrafting crafting, int base, double value)
    {
        sendLong(crafting, base, Double.doubleToRawLongBits(value));
    }*/

    @Override
    public void addCraftingToCrafters(ICrafting crafting)
    {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, tile.scan);
        crafting.sendProgressBarUpdate(this, 1, tile.area);

        sendLong(crafting, 2, tile.tilesFound);
        sendLong(crafting, 6, tile.chunksForced);
        sendLong(crafting, 10, tile.chunksFound);
        sendLong(crafting, 14, tile.ticketsUsed);
        sendLong(crafting, 18, (long) tile.energyStored);
        sendLong(crafting, 22, tile.capacity);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        // detect changes only each even tick
        //if ((tile.worldObj.getWorldTime() % 2) == 0) return;
        // hmm, this thing produces visual bugs for some reason

        long tile_energy = (long) tile.energyStored;
        for (int i = 0; i < crafters.size(); i++)
        {
            ICrafting crafting = (ICrafting) crafters.get(i);

            if (scan         != tile.scan)         crafting.sendProgressBarUpdate(this, 0, tile.scan);
            if (area         != tile.area)         crafting.sendProgressBarUpdate(this, 1, tile.area);
            if (tilesFound   != tile.tilesFound)   sendLong(crafting, 2,  tile.tilesFound);
            if (chunksForced != tile.chunksForced) sendLong(crafting, 6,  tile.chunksForced);
            if (chunksFound  != tile.chunksFound)  sendLong(crafting, 10, tile.chunksFound);
            if (ticketsUsed  != tile.ticketsUsed)  sendLong(crafting, 14, tile.ticketsUsed);
            if (energy       != tile_energy)       sendLong(crafting, 18, tile_energy);
            if (capacity    != tile.capacity)    sendLong(crafting, 22, tile.capacity);
        }

        scan         = tile.scan;
        area         = tile.area;
        tilesFound   = tile.tilesFound;
        chunksForced = tile.chunksForced;
        chunksFound  = tile.chunksFound;
        ticketsUsed  = tile.ticketsUsed;
        energy       = tile_energy;
        capacity    = tile.capacity;
    }

    @Override
    public void updateProgressBar(int key, int value)
    {
        switch (key)
        {
            case 0: tile.scan = (byte) value; break;
            case 1: tile.area = (short) value; break;

            case 2:
            case 3:
            case 4:
            case 5:
                tile.tilesFound = tolong(tile.tilesFound, key-2, value);
                break;

            case 6:
            case 7:
            case 8:
            case 9:
                tile.chunksForced = tolong(tile.chunksForced, key-6, value);
                break;

            case 10:
            case 11:
            case 12:
            case 13:
                tile.chunksFound = tolong(tile.chunksFound, key-10, value);
                break;

            case 14:
            case 15:
            case 16:
            case 17:
                tile.ticketsUsed = tolong(tile.ticketsUsed, key-14, value);
                break;

            case 18:
            case 19:
            case 20:
            case 21:
                tile.energyStored = tolong((long) tile.energyStored, key-18, value);
                break;

            case 22:
            case 23:
            case 24:
            case 25:
                tile.capacity = tolong(tile.capacity, key-22, value);
                break;
        }
    }

    /**
     * Called when a player shift-clicks on a slot. Slot index are ordered the same way as they created in the constructor.
     *
     * @param player who shift-clicked slot
     * @param slotIndex on which player is shift-clicked
     * @return ItemStack to put into shift-clicked slot or null if you want to leave Slot content as is.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack var3 = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            var3 = stack.copy();

            if (slotIndex >= 4)
            {
                if (tile.isItemValidForSlot(slotIndex, stack))
                {
                    if (!mergeItemStack(stack, 0, 4, false)) return null;
                }
                else if (slotIndex >= 4 && slotIndex < 31)
                {
                    if (!mergeItemStack(stack, 31, 40, false)) return null;
                }
                else if (slotIndex >= 31 && slotIndex < 40 && !mergeItemStack(stack, 4, 31, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(stack, 4, 40, false))
            {
                return null;
            }

            if (stack.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (stack.stackSize == var3.stackSize)
            {
            	return null;
            }
            slot.onPickupFromSlot(player, stack);
        }

        return var3;
    }
}
