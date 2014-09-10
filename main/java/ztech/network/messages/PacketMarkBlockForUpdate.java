package ztech.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import ztech.client.entities.EntitySparksFX;
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
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		TileEntity tile = player.worldObj.getTileEntity(message.x,message.y,message.z);
		if (tile instanceof TileEntitySeedLibrary)
		{
			((TileEntitySeedLibrary) tile).energy = message.energy;
		}
		player.worldObj.func_147479_m(message.x,message.y,message.z);
		return null;
	}
}