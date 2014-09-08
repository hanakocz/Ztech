package ztech.tileentities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import ztech.Ztech;
import ztech.init.ModBlocks;
import ztech.utils.BlockCoords;
import ztech.utils.ChunkCoords;
import ztech.utils.UpgradeDictionary;
import ztech.utils.UpgradeModule;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IWrenchable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityNetworkAnchor  extends TileEntity implements IInventory, IEnergySink, IWrenchable
{
	public ItemStack[] inventory = new ItemStack[4]; // The ItemStacks that hold the items in upgrade slots
    public boolean addedToEnergyNet = false;
    public byte scan = 0x00; // bitfield: bit.0-enable, bit.1-optimize
    public short area = 0; // Radius of chunk grid area. NxN, where N = 2 * area + 1.
    public long tilesFound = 0;
    public long chunksForced = 0;
    public long chunksFound = 0;
    public long ticketsUsed = 0;
    public int blocksPerTick = 1;
    public double energyStored = 0;
    public long capacity = 10000;
    public int maxInput = 32;
    public int tier = 1;
    public ArrayList<Ticket> tickets = new ArrayList<Ticket>();
    public HashSet<BlockCoords> network = new HashSet<BlockCoords>();
    public LinkedList<BlockCoords> pending = new LinkedList<BlockCoords>();
    public TreeSet<ChunkCoords> chunks = new TreeSet<ChunkCoords>();
    
    /**
     * States:<br>
     * 0 - initialization (generate chunk grid);<br>
     * 1 - network scanning;<br>
     * 2 - work done, do nothing and consume energy;<br>
     */
    public int state = 0;
    
    public TileEntityNetworkAnchor()
    {
        applyUpgrades();
    }
    
	public void removeFromEnergyNet()
	{
		if (addedToEnergyNet)
		{
			MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			addedToEnergyNet = false;
		}
	}
	
	@Override
	public void invalidate()
	{
		removeFromEnergyNet();
        cleanupGeometry(); // should do nothing on client side, because lists are empty
        releaseChunks();
		super.invalidate();
	}

	@Override
	public void onChunkUnload()
	{
		removeFromEnergyNet(); // hmm, if I remove this line does block will remain in e-net and will continue to be processed?
		super.onChunkUnload();
	}

    public void init(boolean now)
    {
        // iterate through area and generate chunk grid
        for (int x = -area; x <= area; x++)
        {
            for (int z = -area; z <= area; z++)
            {
                chunks.add(new ChunkCoords(x * 16 + xCoord, z * 16 + zCoord));
            }
        }

        // network mode part
        if ((scan & 0x01) != 0)
        {
            // add self as starting point
            BlockCoords self = new BlockCoords(xCoord, yCoord, zCoord);
            network.add(self);
            pending.addFirst(self);

            if (now == false)
            {
                state = 1; // schedule network scan
                return;
            }

            // immediately scan network
            while (pending.isEmpty() == false) processPendingBlock();
        }
        forceChunks();
    }
	
    @Override
    public void updateEntity()
    {
        super.updateEntity();

        // Done for client
        if (worldObj.isRemote)
        {
        	return;
        }

        // Make sure that we connected to energy net
        if (!addedToEnergyNet)
        {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            addedToEnergyNet = true;
        }

        // Obtain energy from items, if energy consumption is enabled
        if (Ztech.config.clientRules.bEnergy)
        {
            for (ItemStack stack : inventory)
            {
            	//TODO
                double amount = (int) Math.floor(capacity / 2.0D - energyStored);
                if (amount <= 0)
                {
                	break; // stop if full
                }
                IElectricItem electricItem = getBattery(stack);
                if (electricItem == null)
                {
                	continue;
                }
                amount = Math.min(electricItem.getTransferLimit(stack), amount);
                if (amount <= 0)
                {
                	continue;
                }
                amount = ElectricItem.manager.discharge(stack, amount, tier, false, true, false);
                energyStored += amount;
                if (amount != 0)
                {
                	break; // no more than one item at once
                }
            }
        }

        // Work

        // State 0: wait for internal storage to be full, if energy consumption is enabled
        if (state == 0 && (energyStored >= capacity / 2.0D || Ztech.config.clientRules.bEnergy == false))
        {
            init(false);
        }

        // State 1: scan network
        if (state == 1)
        {
            for (int i = 0; i < blocksPerTick; i++) // process some pending blocks
            {
                if (pending.isEmpty())
                {
                    // work done - try to force chunks and finish.
                    forceChunks();
                    break;
                }
                processPendingBlock(); // process single pending block
            }
            //while (!pending.isEmpty()) processPendingBlock();
            //forceChunks();
        }

        // State 2: work done - stay idle and consume energy
        if (state == 2 && Ztech.config.clientRules.bEnergy == true)
        {
            // Calculate energy requirement
            double a = Ztech.config.energyBase;
            double b = Ztech.config.energyPerTile * tilesFound;
            double c = Ztech.config.energyPerChunk * chunksForced;
            double cost = a + b + c;
            double energy2 = energyStored - cost; // *workaround

            if (energy2 >= 0)
            {
            	energyStored = energy2;
            }
            else
            {
                reset(); // not enough energy - start over
            }
        }
    }
    
    public Ticket newTicket()
    {
        Ticket ticket;

        // Quick fix for unistall issue
        // if (placedBy == null)
            ticket = ForgeChunkManager.requestTicket(Ztech.instance, worldObj, Type.NORMAL);
        // else
        //     ticket = ForgeChunkManager.requestPlayerTicket(NetworkAnchor.instance, placedBy.username, worldObj, Type.NORMAL);

        if (ticket != null)
        {
            NBTTagCompound nbt = ticket.getModData();
            nbt.setInteger("x", xCoord);
            nbt.setInteger("y", yCoord);
            nbt.setInteger("z", zCoord);
            tickets.add(ticket);
        }

        return ticket;
    }
    
    public void forceChunks()
    {
        Ticket ticket;
        int ticketIndex = 0;

        // collect statistics
        chunksFound = chunks.size();

        while (chunks.isEmpty() == false)
        {
            // pick existing ticket or make a new one
            if (ticketIndex < tickets.size())
                ticket = tickets.get(ticketIndex);
            else
                ticket = newTicket();

            // error check if no more tickets available
            if (ticket == null) break;

            for (int i = ticket.getMaxChunkListDepth(); i > 0; i--)
            {
                if (chunks.isEmpty() == true) break;

                // get chunk and force it
                ForgeChunkManager.forceChunk(ticket, chunks.pollFirst());

                // collect statistics
                chunksForced++;
            }

            // next ticket
            ticketIndex++;
        }

        // free not used tickets if they present
        for (int i = tickets.size() - 1; i >= 0; i--)
        {
            ticket = tickets.get(i);
            if (ticket.getChunkList().isEmpty() == true)
            {
                tickets.remove(i);
                ForgeChunkManager.releaseTicket(ticket);
            }
        }

        // collect statistics
        ticketsUsed = tickets.size();

        // free memory from geometry - we don't need it any more
        cleanupGeometry();

        // work done
        state = 2;
    }

    public void processPendingBlock()
    {
        // get first block
        BlockCoords b = pending.pollFirst();

        boolean tileValid = true;
        if (scan > 1) // if optional flags enabled
        {
            // optimize chunk structure to contain only "end-point" blocks, e.g. machines/generators
            if (tileValid && (scan & 0x02) != 0)
            {
                TileEntity tile = worldObj.getTileEntity(b.x, b.y, b.z);
                if (tile != null)
                {
                    tileValid &= (tile instanceof IEnergySink || tile instanceof IEnergySource);
                }
            }
        }

        // add chunk
        if (tileValid)
        {
            chunks.add(new ChunkCoords(b.x, b.z));
            chunksFound = chunks.size(); // collect statistics
            tilesFound++;
        }

        // check and schedule surrounding blocks
        checkAndScheduleBlock(b.x - 1, b.y, b.z);
        checkAndScheduleBlock(b.x + 1, b.y, b.z);
        checkAndScheduleBlock(b.x, b.y - 1, b.z);
        checkAndScheduleBlock(b.x, b.y + 1, b.z);
        checkAndScheduleBlock(b.x, b.y, b.z - 1);
        checkAndScheduleBlock(b.x, b.y, b.z + 1);
    }
    
    public void checkAndScheduleBlock(int x, int y, int z)
    {
        TileEntity tile = worldObj.getTileEntity(x, y, z);
        if (tile == null || tile instanceof TileEntityScanTerminator)
        {
            // don't add it to pending blocks, and terminate scan that way
        }
        else if (tile instanceof IEnergyTile)
        {
            BlockCoords b = new BlockCoords(x, y, z);
            if (network.add(b)) pending.addFirst(b);
        }
    }
    
    public void cleanupGeometry()
    {
        network.clear();
        pending.clear();
        chunks.clear();
    }

    public void releaseChunks()
    {
        for (Ticket t : tickets) ForgeChunkManager.releaseTicket(t);
        tickets.clear();
    }

    public void reset()
    {
        cleanupGeometry();
        releaseChunks();
        tilesFound = 0; // reset statistics
        chunksForced = 0;
        chunksFound = 0;
        ticketsUsed = 0;
        state = 0; // start over
    }
    
    /**
     * Reads a tile entity from NBT.
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        // Read inventory stacks from NBT.

        NBTTagList items = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = (NBTTagCompound)items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");

            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        // Read rest parameters with old NBT conversion

        if (nbt.hasKey("mode"))
        {
            NBTBase tag = nbt.getTag("mode");
            if (tag instanceof NBTTagInt)
            {
                int mode = ((NBTTagInt) tag).func_150287_d();
                if (mode >= 1)
                {
                	scan |= 0x01; // network scan is enabled in modes 1 and 2
                }
                if (mode == 1)
                {
                	area = 0; // area should be 1x1 in network mode
                }
            }
            nbt.removeTag("mode");
        }

        scan = nbt.getByte("scan"); // boolean is stored as byte in nbt, so it automatically compatible

        if (nbt.hasKey("area"))
        {
            NBTBase tag = nbt.getTag("area");
            if (tag instanceof NBTTagInt)
            {
                int i = ((NBTTagInt) tag).func_150287_d();

                if (i > Short.MAX_VALUE)
                    area = Short.MAX_VALUE;
                else if (i < 0)
                    area = 0;
                else
                    area = (short) i;

                nbt.removeTag("area");
            }
            else if (tag instanceof NBTTagShort)
            {
                area = ((NBTTagShort) tag).func_150289_e();
            }
        }

        if (nbt.hasKey("energy"))
        {
            energyStored = nbt.getDouble("energy");
        }

        // Calculate parameters depending on installed upgrades

        applyUpgrades();
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

        for (int i = 0; i < inventory.length; i++)
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

        nbt.setByte("scan", scan);
        nbt.setShort("area", area);
        nbt.setDouble("energy", energyStored);
    }
    
    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty()
    {
        super.markDirty();
        applyUpgrades();
    }

    /**
     * Update parameters depending on installed upgrades
     */
    public void applyUpgrades()
    {
        // Counters with initial parameters for the device
        int cTier = 1;
        int cSpeed = 1;
        int cStorage = 10000;

        // Collecting tier upgrades first (e.g. transformers)
        for (ItemStack stack : inventory)
        {
            if (stack == null)
            {
            	continue;
            }
            UpgradeModule module = UpgradeDictionary.get(stack);
            if (module == null)
            {
            	continue;
            }
            if (module.type == UpgradeDictionary.TIER)
            {
                cTier += module.amplifier * stack.stackSize;
            }
        }

        // Iterate through upgrade slots and collect other upgrades
        for (ItemStack stack : inventory)
        {
            if (stack == null)
            {
            	continue;
            }
            UpgradeModule module = UpgradeDictionary.get(stack);
            if (module == null)
            {
            	continue;
            }
            if (cTier < module.tier)
            {
            	continue; // skip module which have not enough tier
            }
            if (module.type == UpgradeDictionary.SPEED)
            {
                cSpeed += module.amplifier * stack.stackSize;
            }
            else if (module.type == UpgradeDictionary.STORAGE)
            {
                cStorage += module.amplifier * stack.stackSize;
            }
        }

        // Actually apply parameters

        tier = cTier; // tier

        // Scan Speed

        // Calculate scan speed by formula
        //blocksPerTick = (int) Math.pow(2, cSpeed);
        double n = Ztech.config.scanRateBase + Ztech.config.scanRateA * Math.pow(Ztech.config.scanRateB, cSpeed);
        if (n > Integer.MAX_VALUE)
        {
        	n = Integer.MAX_VALUE; // overflow check
        }

        int h = Ztech.config.hardLimit;  // apply hard limit
        if (h > 0 && n > h)
        {
        	n = h;
        }

        if (n < 1) n = 1; // scan speed should not be less than 1 after all
        blocksPerTick = (int) n; // apply scan speed

        //maxInput = (int) Math.pow(2, cTier * 2 + 3);
        //maxInput = EnergyNet.instance.getPowerFromTier(tier);
        capacity = cStorage;

        // Cut overcharge (usually when removing storage upgrades)
        if (energyStored > capacity)
        {
        	energyStored = capacity;
        }
    }

    public void buttonHandler(int button)
    {
        switch (button)
        {
            case 0: scan ^= 0x01; break; // E
            case 1: scan ^= 0x02; break; // O
            case 2: if (area > 0) area--; break; // A-
            case 3: if (area < Short.MAX_VALUE) area++; break; // A+
            case 4: reset(); break; // restart
        }
    }
    
    public static IElectricItem getBattery(ItemStack stack)
    {
        if (stack == null)
        {
        	return null;
        }
        Item item = stack.getItem();
        if (item == null || !(item instanceof IElectricItem)) 
        {
        	return null;
        }
        IElectricItem electricItem = (IElectricItem) item;
        if (!electricItem.canProvideEnergy(stack))
        {
        	return null;
        }
        return electricItem;
    }
    
	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true; // always return true so cables visually connect to block
	}

	@Override
	public boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side)
	{
		return false;
	}

	@Override
	public short getFacing()
	{
		return 1; // always pointing to the sky, just in case
	}

	@Override
	public void setFacing(short facing) {}

	@Override
	public boolean wrenchCanRemove(EntityPlayer entityPlayer)
	{
		return true;
	}

	@Override
	public float getWrenchDropRate()
	{
		return Ztech.config.clientRules.wrenchRequired ? Ztech.config.clientRules.wrenchChance : 1.0F;
	}

	@Override
	public ItemStack getWrenchDrop(EntityPlayer entityPlayer)
	{
		return new ItemStack(ModBlocks.blockNetworkAnchor, 1, 0); // same block
	}

	@Override
	public double getDemandedEnergy()
	{
        if (Ztech.config.clientRules.bEnergy == false)
        {
        	return 0.0D;
        }
        double d = capacity / 2.0D - energyStored;
        if (d < 1.0D)
        {
        	d = 0.0D; // don't request amounts that are negative or too small
        }
        return d;
	}

	@Override
	public int getSinkTier()
	{
		return tier;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
        if (!Ztech.config.clientRules.bEnergy)
        {
        	return amount;
        }
/*
        // Check voltage
        if (amount > getMaxSafeInput())
        {
            // Explode
            invalidate();
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            worldObj.createExplosion(null, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 1.25F, true);
            return 0; // and eat all energy
        }*/

        double d = Math.min(capacity - energyStored, amount);
        energyStored += d;
        return amount - d;
	}

    /**
     * Returns the number of slots in the inventory.
     */
	@Override
	public int getSizeInventory()
	{
		return inventory.length;
	}

    /**
     * Returns the stack in slot i
     */
	@Override
	public ItemStack getStackInSlot(int i)
	{
		return inventory[i];
	}

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a new stack.
     */
	@Override
	public ItemStack decrStackSize(int i, int count)
	{
        if (inventory[i] == null)
        {
        	return null;
        }

        ItemStack stack;

        if (inventory[i].stackSize <= count)
        {
            stack = inventory[i];
            inventory[i] = null;
            return stack;
        }

        stack = inventory[i].splitStack(count);
        if (inventory[i].stackSize == 0)
        {
        	inventory[i] = null;
        }
        return stack;
	}

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem - like when you close a workbench GUI.
     */
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
        ItemStack stack = inventory[i];
        inventory[i] = null;
        return stack;
	}

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
	@Override
	public void setInventorySlotContents(int i, ItemStack stack)
	{
        inventory[i] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
	}

	@Override
	public String getInventoryName()
	{
		return "container.networkAnchor";
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack)
	{
        return stack != null && (UpgradeDictionary.get(stack) != null || getBattery(stack) != null);
	}

}
