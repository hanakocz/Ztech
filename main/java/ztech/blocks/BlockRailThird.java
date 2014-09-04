package ztech.blocks;

import ztech.Ztech;
import ztech.tileentities.TileEntityThirdRail;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRailThird  extends BlockContainer
{
    // blockIcon - top and bottom textures
    public IIcon sideEmpty; // texture without rail
    public IIcon sideRail; // texture with rail
    
	public BlockRailThird(String name, Material material)
	{
		super(material);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        setBlockName(name);
        setBlockTextureName(Ztech.MODID + ":" + name);
        setHardness(0.7F);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(Ztech.tabMod);
        GameRegistry.registerBlock(this, name);
	}

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        /**
         * Sides:
         * 0 - bottom (-y)
         * 1 - top (+y)
         * 2 - north (-z)
         * 3 - south (+z)
         * 4 - west (-x)
         * 5 - east (+x)
         */
        if (side == 0 || side == 1) return blockIcon;

        // Display sides depending on metadata mask (inverted for inventory display purpose)
        if ((metadata & 1 << side - 2) == 0 )
        {
        	return sideRail;
        }
        return sideEmpty;
    }
	
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String textureName = getTextureName();
        blockIcon = iconRegister.registerIcon(textureName);
        sideEmpty = iconRegister.registerIcon(textureName + "_side");
        sideRail  = iconRegister.registerIcon(textureName + "_side_rail");
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor)
    {
        onBlockAdded(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (world.isRemote) return;

        // Update metadata depending on adjacent rails
        int metadata = 15;
        if (BlockRailBase.func_150049_b_(world, x, y, z - 1)) metadata &= -2; // z- => north
        if (BlockRailBase.func_150049_b_(world, x, y, z + 1)) metadata &= -3; // z+ => south
        if (BlockRailBase.func_150049_b_(world, x - 1, y, z)) metadata &= -5; // x- => west
        if (BlockRailBase.func_150049_b_(world, x + 1, y, z)) metadata &= -9; // x+ => east
        world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
    }
    
	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_)
	{
		return new TileEntityThirdRail();
	}
}
