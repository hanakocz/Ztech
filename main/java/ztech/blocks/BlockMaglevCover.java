package ztech.blocks;

import ztech.Ztech;
import ztech.tileentities.TileEntityMaglevCover;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMaglevCover extends BlockRailEx
{
	public BlockMaglevCover(String name, float speedAmplifier)
	{
		super(name, speedAmplifier, TileEntityMaglevCover.class);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        setStepSound(Block.soundTypeStone);
	}
	
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        super.onNeighborBlockChange(world, x, y, z, neighbor);

        // When neighbor block is changed, cover should update it's linked rails, e.g. reset linked rails and force to find rails again
        if (world.isRemote)
        {
        	return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityMaglevCover)
        {
            ((TileEntityMaglevCover) tile).updateRails(true);
        }
    }

    @Override
    public boolean canMakeSlopes(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }
    
	@Override
	public int getRenderType()
	{
		return Ztech.config.coverRenderId;
	}
}
