package ztech.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import ztech.containers.ContainerSeedLibrary;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSendGuiSlider implements IMessage, IMessageHandler<PacketSendGuiSlider, IMessage>
{
	private byte slider;
	private byte value;

	public PacketSendGuiSlider() {}

	public PacketSendGuiSlider(byte slider, byte value)
	{
		this.slider = slider;
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		slider = buf.readByte();
		value = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(slider);
		buf.writeByte(value);
	}

	@Override
	public IMessage onMessage(PacketSendGuiSlider message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (!(player.openContainer instanceof ContainerSeedLibrary))
		{
			return null;
		}
		ContainerSeedLibrary container = (ContainerSeedLibrary)player.openContainer;
		if (container.seedlibrary.energy <= 0)
		{
			return null;
		}
		container.seedlibrary.receiveGuiSlider(message.slider, message.value);
		return null;
	}
}