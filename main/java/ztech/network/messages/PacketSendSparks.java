package ztech.network.messages;

import ztech.client.entities.EntitySparksFX;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSendSparks implements IMessage, IMessageHandler<PacketSendSparks, IMessage>
{
	private double x;
	private double y;
	private double z; 

	public PacketSendSparks() {}

	public PacketSendSparks(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public IMessage onMessage(PacketSendSparks message, MessageContext ctx)
	{
		EntitySparksFX.spawnSparks(Minecraft.getMinecraft().thePlayer.worldObj, message.x, message.y, message.z);
		return null;
	}
}
