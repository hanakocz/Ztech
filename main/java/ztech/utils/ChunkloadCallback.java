package ztech.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ztech.Ztech;
import ztech.init.ModBlocks;
import ztech.tileentities.TileEntityNetworkAnchor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class ChunkloadCallback implements ForgeChunkManager.OrderedLoadingCallback
{
	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world)
	{
		HashSet<BlockCoords> anchors = new HashSet<BlockCoords>();

		// iterate through tickets
		for (Ticket ticket : tickets)
		{
			// get anchor coords from ticket
			int x = ticket.getModData().getInteger("x");
			int y = ticket.getModData().getInteger("y");
			int z = ticket.getModData().getInteger("z");

			// check whether entity at specified coords is network anchor
			TileEntity tile = world.getTileEntity(x, y, z);
			if (tile != null && tile instanceof TileEntityNetworkAnchor)
			{
				// add ticket to anchor ticket list
				((TileEntityNetworkAnchor) tile).tickets.add(ticket);

				// add anchor to set of anchors
				anchors.add(new BlockCoords(x, y, z));
			}
		}

		// Iterate discovered anchors and restore chunks geometry
		Ztech.logger.info("Restoring anchors...");
		for (BlockCoords anchor : anchors)
		{
			TileEntity tile = world.getTileEntity(anchor.x, anchor.y, anchor.z);
			if (tile != null && /*tile.isInvalid() == false &&*/ tile instanceof TileEntityNetworkAnchor)
			{
				// Init anchor
				TileEntityNetworkAnchor a = (TileEntityNetworkAnchor) tile;
				a.init(true);

				// Spam anchor info into forge log

				String f = "";
				if ((a.scan & 0x01) != 0) f += "E";
				if ((a.scan & 0x02) != 0) f += "O";
				if ("".equals(f)) f = "none";
				Ztech.logger.info("d:" + a.getWorldObj().provider.dimensionId + ", x:" + a.xCoord + ", y:" + a.yCoord + ", z:" + a.zCoord + ", f: " + f + ", c:" + a.chunksForced + ", t:" + a.ticketsUsed);
			}
		}
	}

	@Override
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
	{
		LinkedList<Ticket> validTickets = new LinkedList<Ticket>();

		for (Ticket ticket : tickets)
		{
			int x = ticket.getModData().getInteger("x");
			int y = ticket.getModData().getInteger("y");
			int z = ticket.getModData().getInteger("z");

			if (world.getBlock(x, y, z) == ModBlocks.blockNetworkAnchor) validTickets.add(ticket);
		}

		return validTickets;
	}
}
