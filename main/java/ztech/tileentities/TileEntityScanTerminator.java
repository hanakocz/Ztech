package ztech.tileentities;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.item.IC2Items;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityScanTerminator extends TileEntity implements IEnergyConductor
{
	public boolean addedToEnergyNet = false;

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
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction)
	{
		return true;
	}

	@Override
	public double getConductionLoss()
	{
		return 0.8D;
	}

	@Override
	public double getInsulationEnergyAbsorption()
	{
		return 9001;
	}

	@Override
	public double getInsulationBreakdownEnergy()
	{
		return 9001;
	}

	@Override
	public double getConductorBreakdownEnergy()
	{
		return 2049;
	}

	@Override
	public void removeInsulation() {}

	@Override
	public void removeConductor()
	{
		invalidate();

		// burn out circuits and leave empty machine block
		worldObj.playSoundEffect(xCoord+0.5D, yCoord+0.5D, zCoord+0.5D, "random.fizz", 0.5F, 2.6F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.8F);
		for (int l = 0; l < 8; ++l)
		{
			worldObj.spawnParticle("largesmoke", xCoord+Math.random(), yCoord + 1.2D, zCoord+Math.random(), 0.0D, 0.0D, 0.0D);
		}

		ItemStack machine = IC2Items.getItem("machine");
		worldObj.setBlock(xCoord, yCoord, zCoord, Block.getBlockFromItem(machine.getItem()), machine.getItemDamage(), 3);
	}

}
