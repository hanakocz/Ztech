package ztech.tileentities;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ztech.containers.FakeSlot;
import ztech.network.ChannelHandler;
import ztech.network.messages.PacketMarkBlockForUpdate;
import ztech.network.messages.PacketSendGuiButton;
import ztech.network.messages.PacketSendGuiSlider;
import ztech.network.messages.PacketSetSeedCount;
import ztech.network.messages.PacketUpdateGUIFilter;
import ztech.utils.SeedLibraryFilter;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.item.ItemCropSeed;

public class TileEntitySeedLibrary extends TileEntityElectricMachine // implements ISpecialInventory
{
	protected SeedLibraryFilter[] filters = new SeedLibraryFilter[7];
	protected HashMap<String, ItemStack> deepContents = new HashMap<String, ItemStack>();
	protected Vector<ItemStack> unresearched = new Vector<ItemStack>();
	public InvSlot invSlot[] = new InvSlot[8];
	public FakeSlot fakeSlot;

	// The number of seeds that match the GUI filter.
	public int seeds_available = 0;

	public TileEntitySeedLibrary()
	{
		super(10000, 1, 1);

		for (int i = 0; i < filters.length - 1; i++)
		{
			filters[i] = new SeedLibraryFilter(null);
		}

		// The GUI filter gets a reference to the library, so that it can
		// announce when its count changes.
		filters[filters.length - 1] = new SeedLibraryFilter(this);

		for (int i = 0; i < 8; i++)
		{
			this.invSlot[i] = new InvSlot(this, "inventory" + i, i + 2, InvSlot.Access.IO, 1);
		}
		this.fakeSlot = new FakeSlot(this);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return direction.ordinal() != getFacing();
	}

	@Override
	public String getInventoryName()
	{
		return "Seed Library";
	}

	public void updateCountIfMatch(ItemStack seed)
	{
		SeedLibraryFilter filter = getGUIFilter();
		if (!filter.bulk_mode && filter.isMatch(seed))
		{
			updateSeedCount();
		}
	}

	public void updateSeedCount()
	{
		setSeedCount(getGUIFilter().getCount(deepContents.values()));
	}

	public void setSeedCount(int new_count)
	{
		seeds_available = new_count;

		// We only need to do the rest on the server side.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		// Notify all nearby players that the seed count has changed.

		if (new_count > 65535)
		{
			new_count = 65535;
		}

		if (worldObj != null)
		{
			ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketSetSeedCount((byte) (xCoord & 0xff), (byte) (yCoord & 0xff), (byte) (zCoord & 0xff), (byte) (new_count % 256), (byte) (new_count / 256)));
		}
	}

	public void updateGUIFilter()
	{
		// We only need to do this on the server side.
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

		markDirty();

		// Notify all nearby players that the GUI filter has changed.
		NBTTagCompound nbt = new NBTTagCompound();

		getGUIFilter().writeToNBT(nbt);

		if (worldObj != null)
		{
			ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketUpdateGUIFilter((byte) (xCoord & 0xff), (byte) (yCoord & 0xff), (byte) (zCoord & 0xff), nbt));
		}
	}

	@Override
	public void updateEntity()
	{
		double prevEnergy = energy;
		super.updateEntity();
		if (!FMLCommonHandler.instance().getEffectiveSide().isClient() && energy > 0)
		{
			if (prevEnergy == 0 && energy != 0)
			{
				ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketMarkBlockForUpdate(this.xCoord, this.yCoord, this.zCoord, this.energy));
			}			
			energy -= 10;
			if (energy < 0)
			{
				energy = 0;
				ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketMarkBlockForUpdate(this.xCoord, this.yCoord, this.zCoord, this.energy));
			}
		}
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
		if (this.energy >= this.maxEnergy)
		{
			return amount;
		}
		this.energy += amount;
		if (energy == amount)
		{
			ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketMarkBlockForUpdate(this.xCoord, this.yCoord, this.zCoord, this.energy));
		}	
		return 0.0D;
	}

	public boolean hasEnergy()
	{
		return getChargeLevel() > 0;
	}

	public void importFromInventory()
	{
		getGUIFilter().bulk_mode = true;
		for (int i = 1; i < 9; i++)
		{
			if (TileEntitySeedAnalyzer.isSeed(this.invSlots.get(i).get()))
			{
				storeSeeds(this.invSlots.get(i).get());
				this.invSlots.get(i).clear();
			}
		}
		getGUIFilter().bulk_mode = false;
		updateSeedCount();
	}

	public void exportToInventory()
	{
		getGUIFilter().bulk_mode = true;
		for (int i = 1; i < 9; i++)
		{
			if (this.invSlots.get(i).get() == null)
			{
				// Get a seed from the active filter.
				ItemStack seed = filters[6].getSeed(deepContents.values());

				if (seed == null)
				{
					// No seeds left; stop exporting.
					break;
				}

				// Add one of the seed to the inventory.
				ItemStack stack = seed.copy();
				stack.stackSize = 1;
				this.invSlots.get(i).put(stack);

				// And remove the seed from main storage.
				removeSeeds(this.invSlots.get(i).get());
			}
		}
		getGUIFilter().bulk_mode = false;
		updateSeedCount();
	}

	public SeedLibraryFilter getGUIFilter()
	{
		return filters[6];
	}

	public void sendGuiButton(int button, boolean rightClick)
	{
		ChannelHandler.network.sendToServer(new PacketSendGuiButton((byte) button, (byte) (rightClick ? 1 : 0)));    	
	}

	public void receiveGuiButton(int button, boolean rightClick)
	{
		if (button == 0)
		{
			importFromInventory();
		}
		else if (button == 1)
		{
			exportToInventory();
		}
		else if (button == 2)
		{
			SeedLibraryFilter filter = getGUIFilter();
			filter.unknown_type = (filter.unknown_type + 1) % 3;
			filter.settingsChanged();
		}
		else if (button == 3) 
		{
			SeedLibraryFilter filter = getGUIFilter();
			filter.unknown_ggr = (filter.unknown_ggr + 1) % 3;
			filter.settingsChanged();
		}
		else if (button < 10)
		{
			int dir = button - 4;
			if (rightClick)
			{
				filters[dir].copyFrom(filters[6]);
				markDirty();
			}
			else
			{
				filters[6].copyFrom(filters[dir]);
			}
		}
	}

	public void sendGuiSlider(int slider, int value)
	{
		ChannelHandler.network.sendToServer(new PacketSendGuiSlider((byte) slider, (byte) value));    	
	}

	public void receiveGuiSlider(int slider, int value)
	{
		SeedLibraryFilter filter = getGUIFilter();
		int bar = slider / 2;
		int arrow = slider % 2;
		if (bar == 0)
		{
			if (arrow == 0)
			{
				filter.min_growth = value;
			}
			else
			{
				filter.max_growth = value;
			}
		}
		else if (bar == 1)
		{
			if (arrow == 0)
			{
				filter.min_gain = value;
			}
			else
			{
				filter.max_gain = value;
			}
		}
		else if (bar == 2)
		{
			if (arrow == 0)
			{
				filter.min_resistance = value;
			}
			else
			{
				filter.max_resistance = value;
			}
		}
		else
		{ // if (bar == 3)
			if (arrow == 0)
			{
				filter.min_total = value * 3;
			}
			else
			{
				filter.max_total = value * 3;
			}
		}

		filter.settingsChanged();
	}

	public void storeSeeds(ItemStack seeds)
	{
		String key = getKey(seeds);
		ItemStack stored = deepContents.get(key);
		if (stored != null)
		{
			// Found a pre-existing stack.  Using it will update everything...
			stored.stackSize += seeds.stackSize;

			// ...except the GUI's seed count, so update that now.
			updateCountIfMatch(stored);
		}
		else
		{
			// No pre-existing stack.  Make a new one.
			stored = seeds.copy();

			// If it's not fully scanned, prep it for analysis.
			if (ItemCropSeed.getScannedFromStack(stored) < 4)
			{
				unresearched.add(stored);
			}

			// Add it to the main storage bank.
			deepContents.put(key, stored);

			// Inform filters of the new seed.
			for (SeedLibraryFilter filter : filters)
			{
				filter.newSeed(stored);
			}
		}
		markDirty();
	}

	public void removeSeeds(ItemStack seeds)
	{
		String key = getKey(seeds);
		ItemStack stored = deepContents.get(key);
		if (stored != null)
		{
			// Found a pre-existing stack, so we can reduce it.
			stored.stackSize -= seeds.stackSize;

			if (stored.stackSize <= 0)
			{
				// None left.

				// If it's not fully scanned, remove it from the analyser menu.
				if (ItemCropSeed.getScannedFromStack(stored) < 4)
				{
					unresearched.remove(stored);
				}

				// Remove it from main storage.
				deepContents.remove(getKey(stored));

				// Inform filters that the seed isn't available anymore.
				for (SeedLibraryFilter filter : filters)
				{
					filter.lostSeed(stored);
				}
			}
			else
			{
				// All we need to do is update the GUI count.
				updateCountIfMatch(stored);
			}
			markDirty();
		}
	}

	// Save/load
	@Override
	public void readFromNBT(NBTTagCompound input)
	{
		super.readFromNBT(input);
		deepContents.clear();
		unresearched.clear();

		NBTTagList filterlist = input.getTagList("Filters", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < 7; i++)
		{
			NBTTagCompound filter = (NBTTagCompound)filterlist.getCompoundTagAt(i);
			filters[i].loadFromNBT(filter);
		}

		NBTTagList inventorytag = input.getTagList("Items_", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < inventorytag.tagCount(); i++)
		{
			NBTTagCompound slot = (NBTTagCompound)inventorytag.getCompoundTagAt(i);
			int j = slot.getByte("Slot");
			ItemStack stack = ItemStack.loadItemStackFromNBT(slot);
			storeSeeds(stack);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound output)
	{
		super.writeToNBT(output);
		NBTTagList inventorytag = new NBTTagList();

		for (ItemStack seed : deepContents.values())
		{
			NBTTagCompound seedtag = new NBTTagCompound();
			seedtag.setByte("Slot", (byte) -1);
			seed.writeToNBT(seedtag);
			inventorytag.appendTag(seedtag);
		}

		output.setTag("Items_", inventorytag);

		NBTTagList filterlist = new NBTTagList();
		for (int i = 0; i < 7; i++)
		{
			NBTTagCompound filtertag = new NBTTagCompound();
			filters[i].writeToNBT(filtertag);
			filterlist.appendTag(filtertag);
		}

		output.setTag("Filters", filterlist);
	}

	// Deep inventory management.
	public String getKey(ItemStack seed)
	{
		short id = ItemCropSeed.getIdFromStack(seed);
		byte growth = ItemCropSeed.getGrowthFromStack(seed);
		byte gain = ItemCropSeed.getGainFromStack(seed);
		byte resistance = ItemCropSeed.getResistanceFromStack(seed);
		byte scan = ItemCropSeed.getScannedFromStack(seed);

		return id + ":" + growth + ":" + gain + ":" + resistance + ":" + scan;
	}

	/*@Override
	public List<String> getNetworkedFields()
	{
		List<String> ret = super.getNetworkedFields();

		ret.add("energy");

		return ret;
	}*/
}
