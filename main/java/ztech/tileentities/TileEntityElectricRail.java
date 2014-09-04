package ztech.tileentities;

import java.util.List;
import java.util.Random;

import ztech.Ztech;
import ztech.network.ChannelHandler;
import ztech.network.messages.PacketSendSparks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.info.Info;

public class TileEntityElectricRail extends TileEntityElectricSink implements IEnergyConductor
{
	private int lastHit = 0;
	
    public void doSparks(double sparksX, double sparksZ, boolean randomizeRail)
    {
        // Get rail orientation
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        // Bind sparks to rails
        double x = sparksX - xCoord;
        double y = 0.0D;
        double z = sparksZ - zCoord;
        double d1 = 3.25D / 16.0D;
        double d2 = 1.0D - d1;

        if (meta == 0 || meta == 4 || meta == 5) // Motion by Z axis
        {
            if (randomizeRail) x = x + rand.nextGaussian() * 2.0D - 1.0D;
            x = (x > 0.5) ? d2 : d1;
            if (z <= 0.0D) z = 0.0D;
            if (z >= 1.0D) z = 1.0D;
            if (meta == 4) y = z;
            if (meta == 5) y = 1.0D - z;
        }
        else if (meta == 1 || meta == 2 || meta == 3) // Motion by X axis
        {
            if (randomizeRail) z = z + rand.nextGaussian() * 2.0D - 1.0D;
            if (x <= 0.0D) x = 0.0D;
            if (x >= 1.0D) x = 1.0D;
            z = (z > 0.5) ? d2 : d1;
            if (meta == 2) y = x;
            if (meta == 3) y = 1.0D - x;
        }
        else if (meta == 6 || meta == 7 || meta == 8 || meta == 9)
        {
            double anchorX = 0.0;
            double anchorZ = 0.0;

            if (meta == 6) // 0x6: WestNorth corner (connecting East and South)
            {
                anchorX = 1.0;
                anchorZ = 1.0;
            }
            else if (meta == 7) // 0x7: EastNorth corner (connecting West and South)
            {
                anchorX = 0.0;
                anchorZ = 1.0;
            }
            else if (meta == 8) // 0x8: EastSouth corner (connecting West and North)
            {
                anchorX = 0.0;
                anchorZ = 0.0;
            }
            else if (meta == 9) // 0x9: WestSouth corner (connecting East and North)
            {
                anchorX = 1.0;
                anchorZ = 0.0;
            }

            if (x <= 0.0D) x = 0.0D;
            if (x >= 1.0D) x = 1.0D;
            if (z <= 0.0D) z = 0.0D;
            if (z >= 1.0D) z = 1.0D;

            // Rotate
            x = anchorX - x;
            z = anchorZ - z;

            // Get length from entity to corner
            double l = Math.sqrt(x * x + z * z);

            // Randomize if needed
            if (randomizeRail) l = l + rand.nextGaussian() * 2.0 - 1.0;

            // Bind it to rail
            l = (l > 0.5) ? d2 : d1;

            // Calculate angle
            double angle = Math.atan2(z, x);

            // Calculate binded coords and rotate back
            x = anchorX - Math.cos(angle) * l;
            z = anchorZ - Math.sin(angle) * l;
        }

        // Spawn particles in the world
        //if (worldObj.isRemote) EntitySparksFX.spawnSparks(worldObj, x + xCoord, y + yCoord, z + zCoord);
        ChannelHandler.sendPacketToAllAround(xCoord, yCoord, zCoord, 64, worldObj, new PacketSendSparks(x + xCoord, y + yCoord, z + zCoord));
    }
    
    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (worldObj.isRemote) return;

        if (lastHit > 0) lastHit--;

        // Flag to determine whether rails are generated sparks this tick
        boolean sparks = false;

        float pf = 1.0F; // particle probability factor
        if (worldObj.isRaining() && worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord))
        {
            pf *= 3.0F;
        }

        if (energyStored == 0)
        {
        	return;
        }

        // Get sensitive AABB
        final double f = 0.125F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(xCoord + f, yCoord, zCoord + f, xCoord + 1.0D - f, yCoord + 0.25D, zCoord + 1.0D - f);
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(null, aabb);
        if (list != null && !list.isEmpty())
        {
            for (Object obj : list)
            {
                if (obj == null)
                {
                	continue;
                }
                if (obj instanceof EntityMinecart)
                {
                    // if legacy mode, then boost usual carts
                    EntityMinecart cart = (EntityMinecart) obj;
                    if (energyStored >= Ztech.config.electricTracksEU && boostCart(cart))
                    {
                    	energyStored -= Ztech.config.electricTracksEU;
                        if (/*!worldObj.isRemote &&*/ rand.nextDouble() < 0.01D * pf)
                        {
                            //double xx = cart.prevPosX - cart.in
                            doSparks(cart.posX, cart.posZ, true);
                            sparks = true;
                        }
                        if (energyStored == 0)
                        {
                            list.clear();
                            return;
                        }
                    }
                }
                else if (obj instanceof EntityLivingBase && lastHit == 0)
                {
                    EntityLivingBase living = (EntityLivingBase) obj;

                    // Find last riding entity
                    Entity riding = living;
                    while (riding.ridingEntity != null)
                    {
                    	riding = riding.ridingEntity;
                    }

                    // Skip living if it riding a cart
                    if (riding instanceof EntityMinecart)
                    {
                    	continue;
                    }

                    // Otherwise generate sparks and hurt it
                    //if (!worldObj.isRemote)
                    {
                        doSparks(living.posX, living.posZ, false);
                        sparks = true;
                    }
                    living.attackEntityFrom(Info.DMG_ELECTRIC, living.isWet() ? 4 : 2);
                    energyStored = 0;
                    lastHit = 20;
                    //consumed = 0;
                    list.clear();
                    return;
                }
            }
            list.clear();
        }

        // Do random sparks from rails when no carts and no livings
        if (/*!worldObj.isRemote &&*/ sparks == false && energyStored > 0 && rand.nextDouble() < 0.000075D * pf)
        {
            doSparks(xCoord + rand.nextGaussian(), zCoord + rand.nextGaussian(), false);
        }
    }
	
    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
    {
        return (emitter != this && emitter instanceof TileEntityElectricRail) || direction == ForgeDirection.DOWN;
    }
    
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction)
	{
		return (receiver != this && receiver instanceof TileEntityElectricRail) || direction == ForgeDirection.DOWN;
	}

	@Override
	public double getConductionLoss()
	{
		return 1.0D;
	}

	@Override
	public double getInsulationEnergyAbsorption()
	{
		return 2048.0D;
	}

	@Override
	public double getInsulationBreakdownEnergy()
	{
		return 9001.0D;
	}

	@Override
	public double getConductorBreakdownEnergy()
	{
		return 2049.0D;
	}

	@Override
	public void removeInsulation() {}

	@Override
	public void removeConductor()
	{
		// TODO		
	}
}
