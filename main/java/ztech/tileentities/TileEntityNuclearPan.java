package ztech.tileentities;

import ztech.Ztech;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityNuclearPan extends TileEntity implements ISidedInventory
{
	public ItemStack[] inventory;
	public IReactor reactor;
	public int heat, heatPercent;
	public int progress;
	public int heatPerItem;
	public long cooling;

	public TileEntityNuclearPan()
	{
		super();
		inventory = new ItemStack[2];
		reactor = null;
		heat = 0;
		heatPercent = 0;
		progress = 0;
		heatPerItem = Ztech.config.heatPerItem;
		cooling = 0;
	}

	/**
	 * Checks whether item stack is food.
	 */
	 public static boolean isFood(ItemStack stack)
	 {
		 return (stack.getItem() instanceof ItemFood) || Ztech.config.inPanWhiteList(stack);
	 }

	 /**
	  * Returns true if the pan can cook an item, i.e. has a source item, destination stack isn't full, etc.
	  */
	 public boolean canCook()
	 {
		 if (inventory[0] == null)
		 {
			 return false;
		 }
		 if (!isFood(inventory[0]))
		 {
			 return false;
		 }
		 ItemStack stack = FurnaceRecipes.smelting().getSmeltingResult(inventory[0]);
		 if (stack == null)
		 {
			 return false;
		 }

		 // Check whether output slot can fit smelting result
		 if (inventory[1] == null)
		 {
			 return true;
		 }
		 if (!inventory[1].isItemEqual(stack))
		 {
			 return false;
		 }
		 int result = inventory[1].stackSize + stack.stackSize;
		 return (result <= getInventoryStackLimit() && result <= stack.getMaxStackSize());
	 }

	 /**
	  * Turn one item from the pan source stack into the appropriate cooked item in the pan result stack
	  */
	 public void cookItem()
	 {
		 if (!canCook())
		 {
			 return;
		 }

		 ItemStack stack = FurnaceRecipes.smelting().getSmeltingResult(inventory[0]);

		 if (inventory[1] == null)
		 {
			 inventory[1] = stack.copy();
		 }
		 else if (inventory[1].isItemEqual(stack) && (inventory[1].stackSize + stack.stackSize <= stack.getMaxStackSize()))
		 {
			 inventory[1].stackSize += stack.stackSize;
		 }
		 inventory[0].stackSize--;
		 if (inventory[0].stackSize <= 0)
		 {
			 inventory[0] = null;
		 }
	 }

	 @Override
	 public void updateEntity()
	 {
		 super.updateEntity();

		 // Sentinel
		 if (isInvalid()) return;

		 // Done for client
		 if (worldObj.isRemote) return;

		 // Try to find reactor
		 if (reactor == null && yCoord > 0)
		 {
			 TileEntity tile = worldObj.getTileEntity(xCoord, yCoord-1, zCoord);
			 if (tile != null)
			 {
				 if (tile instanceof IReactor)
				 {
					 reactor = (IReactor) tile;
				 }
				 else if (tile instanceof IReactorChamber)
				 {
					 reactor = ((IReactorChamber) tile).getReactor();
				 }
			 }
		 }

		 // Obtain heat if reactor present
		 if (reactor != null)
		 {
			 try
			 {
				 heat = reactor.getHeat();
				 heatPercent = Math.round(heat * 100.0F / (float) Ztech.config.NOMINAL_HEAT);
			 }
			 catch (Exception e)
			 {
				 reactor = null;
				 heat = 0;
				 heatPercent = 0;
				 cooling = 0;
			 }
		 }
		 else
		 {
			 heat = 0;
			 heatPercent = 0;
			 cooling = 0;
		 }

		 // Check whether we can cook input item
		 if (canCook())
		 {
			 // Collect heat
			 progress += heat;

			 // Collect heat as active cooling (cooking)
			 cooling += heat * Ztech.config.activeCooling;

			 // Check whether there is enough heat to cook items
			 if (progress >= heatPerItem)
			 {
				 do
				 {
					 cookItem();
					 progress -= heatPerItem;
				 }
				 while (canCook() && progress >= heatPerItem);

				 // Cooking finished - reset progress
				 progress = 0;
			 }
		 }
		 else
		 {
			 // Not cooking - reset progress
			 progress = 0;

			 // Collect heat as passive cooling
			 cooling += heat * Ztech.config.passiveCooling;
		 }

		 // Cool reactor if there is enough heat weared
		 if (cooling >= Ztech.config.NOMINAL_HPS)
		 {
			 int n = (int) Math.floor(cooling / (double) Ztech.config.NOMINAL_HPS);
			 cooling -= n * Ztech.config.NOMINAL_HPS;
			 reactor.addHeat(-n);
		 }
	 }

	 /**
	  * Reads a tile entity from NBT.
	  */
	 @Override
	 public void readFromNBT(NBTTagCompound nbt)
	 {
		 super.readFromNBT(nbt);

		 // Read inventory stacks from NBT.
		 if (nbt.hasKey("Items"))
		 {
			 NBTTagList items = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
			 inventory = new ItemStack[getSizeInventory()];

			 for (int i = 0; i < items.tagCount(); i++)
			 {
				 NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				 byte slot = item.getByte("Slot");

				 if (slot >= 0 && slot < inventory.length)
				 {
					 inventory[slot] = ItemStack.loadItemStackFromNBT(item);
				 }
			 }
		 }

		 // Read rest parameters
		 progress = nbt.getInteger("progress");
	 }

	 /**
	  * Writes a tile entity to NBT.
	  */
	 @Override
	 public void writeToNBT(NBTTagCompound nbt)
	 {
		 super.writeToNBT(nbt);

		 // Write inventory stacks to NBT.

		 NBTTagList items = new NBTTagList();

		 for (int i = 0; i < getSizeInventory(); ++i)
		 {
			 if (inventory[i] != null)
			 {
				 NBTTagCompound item = new NBTTagCompound();
				 item.setByte("Slot", (byte) i);
				 inventory[i].writeToNBT(item);
				 items.appendTag(item);
			 }
		 }

		 nbt.setTag("Items", items);

		 // Write rest parameters

		 nbt.setInteger("progress", progress);
	 }

	 @Override
	 public int getSizeInventory()
	 {
		 return inventory.length;
	 }

	 @Override
	 public ItemStack getStackInSlot(int index)
	 {
		 return inventory[index];
	 }

	 @Override
	 public ItemStack decrStackSize(int index, int amount)
	 {
		 ItemStack itemStack = getStackInSlot(index);
		 if (itemStack == null)
		 {
			 return null;
		 }
		 if (amount >= itemStack.stackSize)
		 {
			 setInventorySlotContents(index, null);

			 return itemStack;
		 }
		 itemStack.stackSize -= amount;

		 ItemStack stack = itemStack.copy();
		 stack.stackSize = amount;

		 return stack;
	 }

	 @Override
	 public ItemStack getStackInSlotOnClosing(int index)
	 {
		 ItemStack stack = getStackInSlot(index);
		 if (stack != null)
		 {
			 setInventorySlotContents(index, null);
		 }
		 return stack;
	 }

	 @Override
	 public void setInventorySlotContents(int index, ItemStack stack)
	 {
		 inventory[index] = stack;

		 if (stack != null && stack.stackSize > getInventoryStackLimit())
		 {
			 stack.stackSize = getInventoryStackLimit();
		 }
	 }

	 @Override
	 public String getInventoryName()
	 {
		 return "Nuclear-Pan";
	 }

	 @Override
	 public boolean hasCustomInventoryName()
	 {
		 return false;
	 }

	 @Override
	 public int getInventoryStackLimit()
	 {
		 return 64;
	 }

	 @Override
	 public boolean isUseableByPlayer(EntityPlayer player)
	 {
		 return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	 }

	 @Override
	 public void openInventory() {}

	 @Override
	 public void closeInventory() {}

	 @Override
	 public boolean isItemValidForSlot(int index, ItemStack stack)
	 {
		 //TODO
		 return false;
	 }

	 @Override
	 public int[] getAccessibleSlotsFromSide(int side)
	 {
		 int[] slots = new int[getSizeInventory()];
		 for (int i = 0; i < slots.length; i++)
		 {
			 slots[i] = i;
		 }
		 return slots;
	 }

	 @Override
	 public boolean canInsertItem(int index, ItemStack itemStack, int side)
	 {
		 // TODO
		 return false;
	 }

	 @Override
	 public boolean canExtractItem(int index, ItemStack itemStack, int side)
	 {
		 // TODO
		 return false;
	 }
}
