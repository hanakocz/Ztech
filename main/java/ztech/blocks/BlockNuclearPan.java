package ztech.blocks;

import ic2.api.item.IC2Items;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import ztech.Ztech;
import ztech.tileentities.TileEntityNuclearPan;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNuclearPan extends BlockContainer
{
    private Random rand = new Random();
    private IIcon[] textures = new IIcon[3];

    public BlockNuclearPan(String name, Material material)
    {
        super(material);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
		setBlockName(name);
		setHardness(2.0F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(Ztech.tabMod);
		GameRegistry.registerBlock(this, name);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata)
	{
		if (side > 1)
		{
			return textures[2];
		}
		return textures[side];
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
	    this.textures[0] = iconRegister.registerIcon(Ztech.MODID + ":pan_bottom");
	    this.textures[1] = iconRegister.registerIcon(Ztech.MODID + ":pan_top");
	    this.textures[2] = iconRegister.registerIcon(Ztech.MODID + ":pan_side");
	}
	
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
        if (player.isSneaking()) 
        {
        	return false;
        }
        if (!world.isRemote)
        {
        	player.openGui(Ztech.instance, 0, world, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i)
    {
        return new TileEntityNuclearPan();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6)
    {
        TileEntityNuclearPan tile = (TileEntityNuclearPan) world.getTileEntity(x, y, z);

        if (tile != null)
        {
            for (int i = 0; i < tile.getSizeInventory(); i++)
            {
                ItemStack stack = tile.getStackInSlot(i);

                if (stack != null)
                {
                    float dx = rand.nextFloat() * 0.8F + 0.1F;
                    float dy = rand.nextFloat() * 0.8F + 0.1F;
                    float dz = rand.nextFloat() * 0.8F + 0.1F;

                    while (stack.stackSize > 0)
                    {
                        int count = rand.nextInt(21) + 10;

                        if (count > stack.stackSize) count = stack.stackSize;

                        stack.stackSize -= count;
                        EntityItem entity = new EntityItem(world, x + dx, y + dy, z + dz, stack);

                        if (stack.hasTagCompound())
                        {
                            entity.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
                        }

                        float var15 = 0.05F;
                        entity.motionX = (float)rand.nextGaussian() * var15;
                        entity.motionY = (float)rand.nextGaussian() * var15 + 0.2F;
                        entity.motionZ = (float)rand.nextGaussian() * var15;
                        world.spawnEntityInWorld(entity);
                    }
                }
            }
        }

        super.breakBlock(world, x, y, z, block, par6);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        if (super.canPlaceBlockAt(world, x, y, z))
        {
            return canBlockStay(world, x, y, z);
        }
        return false;
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z)
    {
        if (y == 0)
        {
        	return false;
        }

        // Get block id below current position
        Item item = Item.getItemFromBlock(world.getBlock(x, y - 1, z));

        // Check whether block is reactor or reactor chamber
        ItemStack nuclearReactor = IC2Items.getItem("nuclearReactor");
        ItemStack reactorChamber = IC2Items.getItem("reactorChamber");

        return (item == nuclearReactor.getItem() || item == reactorChamber.getItem());
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock)
    {
        if (!canBlockStay(world, x, y, z))
        {
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.notifyBlockChange(x, y, z, Blocks.air);
        }
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
}
