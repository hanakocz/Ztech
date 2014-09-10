package ztech.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import ztech.tileentities.TileEntitySeedLibrary;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketMarkBlockForUpdate implements IMessage, IMessageHandler<PacketMarkBlockForUpdate, IMessage>
{
	private int x;
	private int y;
	private int z;
	private double energy;	

	public PacketMarkBlockForUpdate() {}

	public PacketMarkBlockForUpdate(int x, int y, int z, double energy)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.energy = energy;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		energy = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeDouble(energy);
	}

	@Override
	public IMessage onMessage(PacketMarkBlockForUpdate message, MessageContext ctx)
	{
		WorldClient world = Minecraft.getMinecraft().theWorld;
		TileEntity tile = world.getTileEntity(message.x,message.y,message.z);
		if (tile instanceof TileEntitySeedLibrary)
		{
			((TileEntitySeedLibrary) tile).energy = message.energy;
		}
		world.func_147479_m(message.x,message.y,message.z);
		return null;
	}
}