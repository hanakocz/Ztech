package ztech.utils;

public final class BlockCoords
{
    public final int x, y, z, hkey;

    public BlockCoords(int blockX, int blockY, int blockZ)
    {
        x = blockX;
        y = blockY;
        z = blockZ;
        hkey = (x * 31 + y) * 31 + z;
    }

    @Override
    public int hashCode()
    {
        return hkey;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
        	return true;
        }
        if (obj == null)
        {
        	return false;
        }
        if (getClass() != obj.getClass())
        {
        	return false;
        }
        BlockCoords other = (BlockCoords) obj;
        return x == other.x && y == other.y && z == other.z;
    }
}
