package ztech.network.messages;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import ztech.Ztech;
import ztech.tileentities.TileEntitySeedLibrary;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateGUIFilter implements IMessage, IMessageHandler<PacketUpdateGUIFilter, IMessage>
{
	private byte x;
	private byte y;
	private byte z;
	private NBTTagCompound nbt;

	public PacketUpdateGUIFilter() {}

	public PacketUpdateGUIFilter(byte x, byte y, byte z, NBTTagCompound nbt)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readByte();
		y = buf.readByte();
		z = buf.readByte();
		int size = buf.readInt();
		try
		{
			DataInputStream dat = new DataInputStream(new ByteArrayInputStream(Arrays.copyOfRange(buf.array(), buf.readerIndex() + 1, buf.readerIndex() + 1 + size)));
			nbt = CompressedStreamTools.readCompressed(dat);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(x);
		buf.writeByte(y);
		buf.writeByte(z);
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream output = new DataOutputStream(stream);
			CompressedStreamTools.writeCompressed(nbt, output);
			buf.writeInt(stream.size());
			buf.writeBytes(stream.toByteArray());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public IMessage onMessage(PacketUpdateGUIFilter message, MessageContext ctx)
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

		seedLibrary.getGUIFilter().loadFromNBT(message.nbt);

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