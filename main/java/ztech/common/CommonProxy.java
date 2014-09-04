package ztech.common;

import ztech.Ztech;
import ztech.containers.ContainerNuclearPan;
import ztech.tileentities.TileEntityElectricRail;
import ztech.tileentities.TileEntityMaglevCover;
import ztech.tileentities.TileEntityMaglevRail;
import ztech.tileentities.TileEntityNuclearPan;
import ztech.tileentities.TileEntityThirdRail;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CommonProxy implements IGuiHandler
{
	public void PlaySound() {}
	
	@Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (!world.blockExists(x, y, z)) 
        {
        	return null;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityNuclearPan)) 
        {
        	return null;
        }
        return new ContainerNuclearPan(player, (TileEntityNuclearPan) tile);		
    }

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		// null on server
		return null;
	}
	
	public void registerTileEntities()
	{
    	if (Ztech.config.enableNuclearPan)
    	{
    		GameRegistry.registerTileEntity(TileEntityNuclearPan.class, "TileEntityNuclearPan");
    	}
    	if (Ztech.config.enableElectricRails)
    	{
            GameRegistry.registerTileEntity(TileEntityElectricRail.class, "TileEntityElectricRail");
            GameRegistry.registerTileEntity(TileEntityThirdRail.class, "TileEntityThirdRail");
            GameRegistry.registerTileEntity(TileEntityMaglevCover.class, "TileEntityMaglevCover");
            GameRegistry.registerTileEntity(TileEntityMaglevRail.class, "TileEntityMaglevRail");
    	}
	}
}
