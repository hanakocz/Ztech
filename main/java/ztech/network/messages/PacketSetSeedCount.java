package ztech.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import ztech.Ztech;
import ztech.tileentities.TileEntitySeedLibrary;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetSeedCount implements IMessage, IMessageHandler<PacketSetSeedCount, IMessage>
{
	private byte x;
	private byte y;
	private byte z;
	private byte b1;
	private byte b2;

	public PacketSetSeedCount() {}

	public PacketSetSeedCount(byte x, byte y, byte z, byte b1, byte b2)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.b1 = b1;
		this.b2 = b2;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readByte();
		y = buf.readByte();
		z = buf.readByte();
		b1 = buf.readByte();
		b2 = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(x);
		buf.writeByte(y);
		buf.writeByte(z);
		buf.writeByte(b1);
		buf.writeByte(b2);
	}

	@Override
	public IMessage onMessage(PacketSetSeedCount message, MessageContext ctx)
	{
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY);
		int z = MathHelper.floor_double(player.posZ);

		int x_end = message.x & 0xff;
		int y_end = message.y & 0xff;
		int z_end = message.z & 0xff;

		// Clobber the last few bits of x,y,z with the values we got
		// in from the packet.
		x += x_end - real_mod(x, 256);
		y += y_end - real_mod(y, 256);
		z += z_end - real_mod(z, 256);

		TileEntity te = player.worldObj.getTileEntity(x,y,z);

		TileEntitySeedLibrary seedLibrary = null;
		if (te instanceof TileEntitySeedLibrary)
		{
			seedLibrary = (TileEntitySeedLibrary) te;
		}
		else
		{
			Ztech.logger.error("Seed Library packet recieved, but missing or incompatible tile entity found.");
			return null;
		}

		int seed_count = message.b1 & 0xff;
		seed_count += (message.b2 & 0xff) * 256;

		seedLibrary.seeds_available = seed_count;

		return null;
	}

	public static int real_mod(int number, int modulus)
	{
		int mod = number % modulus;
		if (mod < 0)
		{
			// Java is a fucking idiot.
			mod += modulus;
		}

		return mod;
	}
}