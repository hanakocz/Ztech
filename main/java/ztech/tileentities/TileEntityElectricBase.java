package ztech.tileentities;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityElectricBase extends TileEntity
{
	// Boost cart, taken from EntityMinecart, same as for powered rails
	public boolean boostCart(EntityMinecart cart)
	{
		int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		double d15 = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);

		if (d15 > 0.01D)
		{
			double d16 = 0.06D;
			cart.motionX += cart.motionX / d15 * d16;
			cart.motionZ += cart.motionZ / d15 * d16;
			return true;
		}
		else if (metadata == 1)
		{
			if (worldObj.getBlock(xCoord - 1, yCoord, zCoord).isNormalCube())
			{
				cart.motionX = 0.02D;
				return true;
			}
			else if (worldObj.getBlock(xCoord + 1, yCoord, zCoord).isNormalCube())
			{
				cart.motionX = -0.02D;
				return true;
			}
		}
		else if (metadata == 0)
		{
			if (worldObj.getBlock(xCoord, yCoord, zCoord - 1).isNormalCube())
			{
				cart.motionZ = 0.02D;
				return true;
			}
			else if (worldObj.getBlock(xCoord, yCoord, zCoord + 1).isNormalCube())
			{
				cart.motionZ = -0.02D;
				return true;
			}
		}
		return false;
	}
}
