package ztech.tileentities;

import java.util.List;

import ztech.Ztech;
import ztech.client.entities.EntitySparksFX;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityThirdRail  extends TileEntityElectricSink
{
    @SideOnly(Side.CLIENT)
    public void doSparks(double sparksX, double sparksZ)
    {
        // Get rail orientation
        int meta = worldObj.getBlockMetadata((int) Math.floor(sparksX), yCoord, (int) Math.floor(sparksZ)) & 7;

        // Bind sparks to side rail
        double x = sparksX - xCoord;
        double y = 4.0D/16.0D;
        double z = sparksZ - zCoord;

        //if (sparksX > x + 0.5D) x += 1.0D;
        //if (sparksZ > z + 0.5D) z += 1.0D;

        if (meta == 0 || meta == 4 || meta == 5) // Motion by Z axis
        {
            x = (x >= 0.5D) ? 1.0D : 0.0D; // clamp x to nearest side rail
            if (z <= 0.0D) z = 0.0D; // clamp z to block bounds
            if (z >= 1.0D) z = 1.0D;
            //if (meta == 4) y = z; ignore slopes
            //if (meta == 5) y = 1.0D - z;
        }
        else if (meta == 1 || meta == 2 || meta == 3) // Motion by X axis
        {
            if (x <= 0.0D) x = 0.0D; // clamp x to block bounds
            if (x >= 1.0D) x = 1.0D;
            z = (z >= 0.5F) ? 1.0D : 0.0D; // clamp z to nearest side rail
            //if (meta == 2) y = x; ignore slopes
            //if (meta == 3) y = 1.0D - x;
        }

        // Spawn particles in the world
        EntitySparksFX.spawnSparks(worldObj, x + xCoord, y + yCoord, z + zCoord);
    }

    @Override
    public void updateEntity()
    {
    	super.updateEntity();
        if (energyStored == 0)
        {
        	return;
        }

        boolean b = boostCartsAt(xCoord - 1, zCoord) &&
                    boostCartsAt(xCoord + 1, zCoord) &&
                    boostCartsAt(xCoord, zCoord - 1) &&
                    boostCartsAt(xCoord, zCoord + 1);
    }

    /**
     * Checks whether block is rail and then boost carts on that block.
     * @return true to continue processing or false to stop, usually if there is not enough energy.
     */
    public boolean boostCartsAt(int x, int z)
    {
        if (!BlockRailBase.func_150049_b_(worldObj, x, yCoord, z))
        {
        	return true;
        }

        // Get sensitive AABB
        final double f = 0.125F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x + f, yCoord, z + f, x + 1.0D - f, yCoord + 0.25D, z + 1.0D - f);
        List<?> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
        if (list != null && !list.isEmpty())
        {
            for (Object obj : list)
            {
                if (obj == null) continue;
                if (obj instanceof EntityMinecart == false) continue;
                EntityMinecart cart = (EntityMinecart) obj;
                if (energyStored >= Ztech.config.thirdRailEU && boostCart(cart))
                {
                	energyStored -= Ztech.config.thirdRailEU;
                    if (worldObj.isRemote && rand.nextDouble() < 0.01D)
                    {
                        doSparks(cart.posX, cart.posZ);
                    }
                    if (energyStored == 0)
                    {
                        list.clear();
                        return false;
                    }
                }
                // don't harm livings, because it is covered
            }
            list.clear();
        }
        return true;
    }
}
