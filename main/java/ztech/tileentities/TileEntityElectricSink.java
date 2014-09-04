package ztech.tileentities;

import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;

public abstract class TileEntityElectricSink extends TileEntityElectricBase implements IEnergySink
{
	private static final double capacity = 20.0D;
	protected int tier = 4;
	protected boolean addedToEnergyNet;
	protected double energyStored;
	public Random rand = new Random();

	@Override
	public void updateEntity()
	{
		// Make sure that we connected to energy net
		if (!FMLCommonHandler.instance().getEffectiveSide().isClient() && !addedToEnergyNet)
		{
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			addedToEnergyNet = true;
		}
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
		super.invalidate();
	}

	@Override
	public void onChunkUnload()
	{
		removeFromEnergyNet();
		super.onChunkUnload();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		// Read "consumed" with backward compatibility
		double consumed = 0.0D;
		if (nbt.hasKey("consumed"))
		{
			consumed = nbt.getDouble("consumed");
			nbt.removeTag("consumed");
		}

		// Read "energy" key with backward compatibility
		if (nbt.hasKey("energy"))
		{
			energyStored = nbt.getDouble("energy");
/*			nbt.removeTag("energy");
			energyStored -= consumed;
			nbt.setDouble("energy", energyStored);*/
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setDouble("energy", energyStored);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return direction == ForgeDirection.DOWN;
	}

	@Override
	public double getDemandedEnergy()
	{
		return Math.max(0.0D, this.capacity - this.energyStored);
	}

	@Override
	public int getSinkTier()
	{
		return tier;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage)
	{
		this.energyStored += amount;
		return 0.0D;
	}

}
