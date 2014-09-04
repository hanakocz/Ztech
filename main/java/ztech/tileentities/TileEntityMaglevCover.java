package ztech.tileentities;

import java.util.List;

import ztech.Ztech;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityMaglevCover extends TileEntityElectricBase
{
    // Keep rails as coordinates instead of direct references to their TileEntities.
    // Not sure whether it will/can be used with RedPower frames, so better to keep it relative.
    protected boolean railA_found = false;
    protected int railA_rx, railA_rz;

    protected boolean railB_found = false;
    protected int railB_rx, railB_rz;

    public TileEntityMaglevRail getRail(int rx, int rz)
    {
        TileEntity tile = worldObj.getTileEntity(xCoord + rx, yCoord, zCoord + rz);
        if (tile != null && tile instanceof TileEntityMaglevRail)
        {
        	return (TileEntityMaglevRail) tile;
        }
        return null;
    }

    @Override
    public void updateEntity()
    {
        // Check surrounding rails(magnets)
        if (!updateRails(false)) return;

        TileEntityMaglevRail railA = null, railB = null;

        // Get sensitive AABB
        final double f = 0.125F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord + f, yCoord, zCoord + f, xCoord + 1.0D - f, yCoord + 0.25D, zCoord + 1.0D - f);
        List<?> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
        if (list != null && !list.isEmpty())
        {
            for (Object obj : list)
            {
                if (obj == null)
                {
                	continue;
                }
                if (obj instanceof EntityMinecart == false)
                {
                	continue;
                }
                EntityMinecart cart = (EntityMinecart) obj;

                // Retrieve rail entities only on demand
                if (railA == null)
                {
                    railA = getRail(railA_rx, railA_rz); // first trial
                    if (railA == null)
                    {
                        if (!updateRails(true))
                        {
                        	break; // try to recover
                        }
                        railA = getRail(railA_rx, railA_rz); // second trial
                        if (railA == null)
                        {
                        	break;
                        }
                    }
                }

                if (railB == null)
                {
                    railB = getRail(railB_rx, railB_rz); // first trial
                    if (railB == null)
                    {
                        if (!updateRails(true))
                        {
                        	break; // try to recover
                        }
                        railB = getRail(railB_rx, railB_rz); // second trial
                        if (railB == null)
                        {
                        	break;
                        }
                    }
                }

                // Check rails energy levels
                if (railA.energyStored >= Ztech.config.maglevRailEU && railB.energyStored >= Ztech.config.maglevRailEU)
                {
                    // Boost cart and drain energy from both rails
                    if (boostCart(cart))
                    {
                        railA.energyStored -= Ztech.config.maglevRailEU;
                        railB.energyStored -= Ztech.config.maglevRailEU;
                    }
                }

                // TODO : If rails are missing or don't have enough energy and cart have enough speed, then crash cart and bring death and destruction.
            }
            list.clear();
        }
    }

    // returns true if rails found
    public boolean updateRails(boolean force)
    {
        if (force)
        {
            railA_found = false;
            railB_found = false;
        }
        return (railA_found && railB_found) ||
               checkRail(-1, 0) ||
               checkRail( 1, 0) ||
               checkRail(0, -1) ||
               checkRail(0,  1);
    }

    public boolean checkRail(int rx, int rz)
    {
        if (getRail(rx, rz) != null)
        {
            if (!railA_found && !(railB_found && rx == railB_rx && rz == railB_rz))
            {
                railA_found = true;
                railA_rx = rx;
                railA_rz = rz;
            }
            else if (!railB_found && !(railA_found && rx == railA_rx && rz == railA_rz))
            {
                railB_found = true;
                railB_rx = rx;
                railB_rz = rz;
            }
        }
        return railA_found && railB_found;
    }
}
