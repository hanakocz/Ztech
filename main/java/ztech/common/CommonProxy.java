package ztech.common;

import ztech.Ztech;
import ztech.containers.ContainerNuclearPan;
import ztech.tileentities.TileEntityNuclearPan;
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
	}
}
