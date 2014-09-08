package ztech.blocks;

import ic2.api.item.IC2Items;

import java.util.List;
import java.util.Random;

import ztech.Ztech;
import ztech.items.ItemNetworkAnchor;
import ztech.tileentities.TileEntityNetworkAnchor;
import ztech.tileentities.TileEntityScanTerminator;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetworkAnchor  extends Block
{
	public static final String[] sideNames = {"bottom", "top", "side"};
	public IIcon[] icons = new IIcon[4];
	public Random rand = new Random();
	//public ItemStack stackNetworkAnchor, stackScanTerminator;
	
    public BlockNetworkAnchor(Material material)
    {
        super(material);
        setBlockName("networkAnchor");
        setStepSound(Block.soundTypeMetal);
        setHardness(2.0F);
        setCreativeTab(Ztech.tabMod);
        GameRegistry.registerBlock(this, ItemNetworkAnchor.class, "networkAnchor");
        // Register custom stacks   
        GameRegistry.registerCustomItemStack("networkAnchor", new ItemStack(this, 1, 0));
        GameRegistry.registerCustomItemStack("scanTerminator", new ItemStack(this, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        for (int i = 0; i < 3; i++)
        {
            icons[i] = iconRegister.registerIcon(Ztech.MODID + ":network_anchor_" + sideNames[i]);
        }
        icons[3] = iconRegister.registerIcon(Ztech.MODID + ":scan_terminator");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        switch (metadata)
        {
            case 0: 
            	return icons[side >= 2 ? 2 : side];
            case 1: 
            	return icons[3];
            default: 
            	return super.getIcon(side, metadata);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (player.isSneaking())
        {
        	return false; // Drop through if player is sneaking
        }
        if (world.getBlockMetadata(x, y, z) == 0)
        {
            if (!world.isRemote)
            {
            	player.openGui(Ztech.instance, 0, world, x, y, z);
            }
            return true;
        }
        return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        switch (metadata)
        {
            case 0: 
            	return new TileEntityNetworkAnchor();
            case 1: 
            	return new TileEntityScanTerminator();
            default: 
            	return super.createTileEntity(world, metadata);
        }
    }

    @Override
    public Item getItemDropped(int metadata, Random rand, int fortune)
    {
        if (metadata == 0 && Ztech.config.clientRules.wrenchRequired)
        {
            return IC2Items.getItem("machine").getItem();
        }
        else
        {
            return super.getItemDropped(metadata, rand, fortune);
        }
    }

    @Override
    public int damageDropped(int metadata)
    {
        if (metadata == 0 && Ztech.config.clientRules.wrenchRequired)
        {
            return IC2Items.getItem("machine").getItemDamage();
        }
        else
        {
            return metadata;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        //list.add(stackNetworkAnchor);
        //list.add(stackScanTerminator);
    	list.add(new ItemStack(this, 1, 0));
    	list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block par5, int par6)
    {
        // Drop items from IInventory
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || tile instanceof IInventory == false) return;
        IInventory inventory = (IInventory) tile;
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) continue;

            float dx = rand.nextFloat() * 0.8F + 0.1F;
            float dy = rand.nextFloat() * 0.8F + 0.1F;
            float dz = rand.nextFloat() * 0.8F + 0.1F;

            while (stack.stackSize > 0)
            {
                int count = rand.nextInt(21) + 10;

                if (count > stack.stackSize) count = stack.stackSize;

                stack.stackSize -= count;
                EntityItem entity = new EntityItem(world, x + dx, y + dy, z + dz, new ItemStack(stack.getItem(), count, stack.getItemDamage()));

                if (stack.hasTagCompound())
                {
                    entity.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                }

                float var15 = 0.05F;
                entity.motionX = (float) rand.nextGaussian() * var15;
                entity.motionY = (float) rand.nextGaussian() * var15 + 0.2F;
                entity.motionZ = (float) rand.nextGaussian() * var15;
                world.spawnEntityInWorld(entity);
            }
        }

        super.breakBlock(world, x, y, z, par5, par6);
    }
}
