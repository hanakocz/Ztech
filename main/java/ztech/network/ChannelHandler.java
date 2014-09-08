package ztech.network;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import ztech.network.messages.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ChannelHandler
{
    public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("ztech");
    public static void init()
    {
    	network.registerMessage(PacketSendSparks.class, PacketSendSparks.class, 1, Side.CLIENT);
    	network.registerMessage(PacketSendButtonClick.class, PacketSendButtonClick.class, 2, Side.SERVER);
    }
    
	public static void sendPacketToAllAround(int x, int y, int z, int dist, World world, IMessage packet)
	{
		@SuppressWarnings("unchecked")
		List<EntityPlayerMP> players = world.playerEntities;
		for (EntityPlayerMP player : players)
		{
			double dx = x - player.posX;
			double dy = y - player.posY;
			double dz = z - player.posZ;

			if (dx*dx + dy*dy + dz*dz < dist * dist)
			{
				ChannelHandler.network.sendTo(packet, player);
			}
		}

	}
}
