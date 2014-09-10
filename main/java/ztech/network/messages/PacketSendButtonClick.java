package ztech.network.messages;

import ztech.tileentities.TileEntityNetworkAnchor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSendButtonClick implements IMessage, IMessageHandler<PacketSendButtonClick, IMessage>
{
	private int dim;
	private int x;
	private int y;
	private int z;
	private int id;

	public PacketSendButtonClick() {}

	public PacketSendButtonClick(int dim, int x, int y, int z, int id)
	{
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		dim = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	@Override
	public IMessage onMessage(PacketSendButtonClick message, MessageContext ctx)
	{
		if (DimensionManager.isDimensionRegistered(message.dim))
		{
			World world = DimensionManager.getWorld(message.dim);
			if (world == null)
			{
				DimensionManager.initDimension(message.dim);
				world = DimensionManager.getWorld(message.dim);
			}

			TileEntity tile = world.getTileEntity(message.x, message.y, message.z);
			if (tile != null && !tile.isInvalid() && tile instanceof TileEntityNetworkAnchor)
			{
				((TileEntityNetworkAnchor) tile).buttonHandler(message.id);
			}
		}
		return null;
	}
}