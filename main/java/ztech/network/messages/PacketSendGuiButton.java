package ztech.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import ztech.containers.ContainerSeedLibrary;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSendGuiButton implements IMessage, IMessageHandler<PacketSendGuiButton, IMessage>
{
	private byte button;
	private byte rightClick;

	public PacketSendGuiButton() {}

	public PacketSendGuiButton(byte button, byte rightClick)
	{
		this.button = button;
		this.rightClick = rightClick;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		button = buf.readByte();
		rightClick = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(button);
		buf.writeByte(rightClick);
	}

	@Override
	public IMessage onMessage(PacketSendGuiButton message, MessageContext ctx)
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
		container.seedlibrary.receiveGuiButton(message.button, message.rightClick == 1);
		return null;
	}
}