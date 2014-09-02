package ztech.client;

import cpw.mods.fml.common.registry.GameRegistry;
import ztech.Ztech;
import ztech.client.gui.GuiNuclearPan;
import ztech.common.CommonProxy;
import ztech.tileentities.TileEntityNuclearPan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy
{
	@Override
	public void PlaySound()
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(new ResourceLocation("electricfish:electricOverload")));
	}
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
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
        return new GuiNuclearPan(player, (TileEntityNuclearPan) tile);
    }

    @Override
	public void registerTileEntities()
	{
    	if (Ztech.config.enableNuclearPan)
    	{
    		GameRegistry.registerTileEntity(TileEntityNuclearPan.class, "TileEntityNuclearPan");
    	}
	}
}
