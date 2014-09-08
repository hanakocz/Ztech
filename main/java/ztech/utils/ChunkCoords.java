package ztech.utils;

import net.minecraft.world.ChunkCoordIntPair;

public class ChunkCoords extends ChunkCoordIntPair implements Comparable<ChunkCoords>
{
    public ChunkCoords(int blockX, int blockZ)
    {
        super(blockX >> 4, blockZ >> 4);
    }

    @Override
    public int compareTo(ChunkCoords other)
    {
        int n = chunkXPos - other.chunkXPos;
        if (n == 0)
        {
        	n = chunkZPos - other.chunkZPos;
        }
        return n;
    }
}
