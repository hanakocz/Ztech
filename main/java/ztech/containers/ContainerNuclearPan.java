package ztech.containers;

import ztech.tileentities.TileEntityNuclearPan;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerNuclearPan extends Container
{
    public TileEntityNuclearPan tile;
    public int progress;
    public int heatPerItem;
    public int heatPercent;

    public ContainerNuclearPan(EntityPlayer player, TileEntityNuclearPan tile)
    {
        // Remember associated tile entity
        this.tile = tile;

        // Add pan slots [0..1]
        addSlotToContainer(new Slot(tile, 0, 48, 29));
        addSlotToContainer(new SlotFurnace(player, tile, 1, 108, 29));

        // Player inventory slots [2..29]
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));

        // Player hotbar slots []
        for (int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 142));
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting)
    {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, tile.progress);
        crafting.sendProgressBarUpdate(this, 1, tile.heatPerItem);
        crafting.sendProgressBarUpdate(this, 2, tile.heatPercent);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); i++)
        {
            ICrafting crafter = (ICrafting) crafters.get(i);
            if (progress != tile.progress) crafter.sendProgressBarUpdate(this, 0, tile.progress);
            if (heatPerItem != tile.heatPerItem) crafter.sendProgressBarUpdate(this, 1, tile.heatPerItem);
            if (heatPercent != tile.heatPercent) crafter.sendProgressBarUpdate(this, 2, tile.heatPercent);
        }
        progress = tile.progress;
        heatPerItem = tile.heatPerItem;
        heatPercent = tile.heatPercent;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int key, int value)
    {
        switch (key)
        {
            case 0: tile.progress = value; break;
            case 1: tile.heatPerItem = value; break;
            case 2: tile.heatPercent = value; break;
        }
        super.updateProgressBar(key, value);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        ItemStack stack2 = null;
        Slot slot = (Slot) inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            stack2 = stack.copy();

            if (slotIndex == 1)
            {
                if (!this.mergeItemStack(stack, 2, 38, true)) return null;
                slot.onSlotChange(stack, stack2);
            }
            else if (slotIndex != 0)
            {
                if (TileEntityNuclearPan.isFood(stack) && FurnaceRecipes.smelting().getSmeltingResult(stack) != null)
                {
                    if (!this.mergeItemStack(stack, 0, 1, false)) return null;
                }
                else if (slotIndex >= 2 && slotIndex < 29)
                {
                    if (!this.mergeItemStack(stack, 29, 38, false)) return null;
                }
                else if (slotIndex >= 29 && slotIndex < 38 && !this.mergeItemStack(stack, 2, 29, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(stack, 2, 38, false))
            {
                return null;
            }

            if (stack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();

            if (stack.stackSize == stack2.stackSize) return null;
            slot.onPickupFromSlot(player, stack);
        }

        return stack2;
    }
}
