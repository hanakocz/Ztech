package ztech.blocks;

import ic2.api.tile.IWrenchable;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import ztech.Ztech;
import ztech.items.ItemSeedManager;
import ztech.tileentities.TileEntitySeedAnalyzer;
import ztech.tileentities.TileEntitySeedLibrary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSeedManager extends BlockContainer
{
	private static final int[][] facingAndSideToSpriteOffset = { { 3, 5, 1, 0, 4, 2 }, { 5, 3, 1, 0, 2, 4 }, { 0, 1, 3, 5, 4, 2 }, { 0, 1, 5, 3, 2, 4 }, { 0, 1, 2, 4, 3, 5 }, { 0, 1, 4, 2, 5, 3 } };
	public IIcon[] analyzer = new IIcon[7];
	public IIcon[] library = new IIcon[6];
	public BlockSeedManager()
	{
		super(Material.iron);
		setBlockName("seedManager");
		setStepSound(Block.soundTypeMetal);
		setHardness(5.0F);
		setResistance(10.0F);
		setCreativeTab(Ztech.tabMod);
		GameRegistry.registerBlock(this, ItemSeedManager.class, "seedManager");
		// Register custom stacks
		GameRegistry.registerCustomItemStack("seedAnalyzer", new ItemStack(this, 1, 0));
		GameRegistry.registerCustomItemStack("seedLibrary", new ItemStack(this, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		analyzer[0] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_bottom");
		analyzer[1] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_side");
		analyzer[2] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_front_off");
		analyzer[3] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_front_on");
		analyzer[4] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_front_anim");
		analyzer[5] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_top_off");
		analyzer[6] = iconRegister.registerIcon(Ztech.MODID + ":seedAnalyzer_top_on");		
		library[0] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_bottom");
		library[1] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_side");
		library[2] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_top_off");
		library[3] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_top_on");
		library[4] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_front_off");
		library[5] = iconRegister.registerIcon(Ztech.MODID + ":seedLibrary_front_on");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata)
	{
		switch (metadata)
		{
		case 0:
			switch (side)
			{
			case 0:
				return analyzer[0];
			case 1:
				return analyzer[5];
			case 2:
				return analyzer[1];
			case 3:
				return analyzer[2];
			case 4:
				return analyzer[1];
			case 5:
				return analyzer[1];
			}
		case 1:
			switch (side)
			{
			case 0:
				return library[0];
			case 1:
				return library[2];
			case 2:
				return library[1];
			case 3:
				return library[4];
			case 4:
				return library[1];
			case 5:
				return library[1];
			}
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess blockaccess, int x, int y, int z, int side)
	{
		int blockType = blockaccess.getBlockMetadata(x, y, z);

		TileEntity tileentity = blockaccess.getTileEntity(x, y, z);
		int metaSide = 0;
		if (tileentity instanceof IWrenchable)
		{
			metaSide = getTextureSubIndex(Facing.oppositeSide[((IWrenchable)tileentity).getFacing()], side);
		}
		switch (blockType)
		{
		case 0:
			if (metaSide == 5)
			{
				if (((TileEntityBlock)tileentity).getActive())
				{
					return analyzer[4];
				}			
				if (((TileEntityStandardMachine)tileentity).energy != 0)
				{
					return analyzer[3];
				}
				return analyzer[2];
			}
			if (metaSide == 0)
			{
				return analyzer[0];
			}
			if (metaSide == 1)
			{
				if (((TileEntityBlock)tileentity).getActive())
				{
					return analyzer[6];
				}
				return analyzer[5];
			}
			return analyzer[1];
		case 1:
			if (metaSide == 0)
			{
				return library[0];
			}
			if (metaSide == 5)
			{
				if (((TileEntityElectricMachine)tileentity).energy != 0)
				{
					return library[5];
				}
				return library[4];					
			}
			if (metaSide == 1)
			{
				if (((TileEntityElectricMachine)tileentity).energy != 0)
				{
					return library[3];
				}
				return library[2];
			}
			return library[1];
		}
		return null;
	}

	public static final int getTextureSubIndex(int facing, int side)
	{
		return facingAndSideToSpriteOffset[facing][side];
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}


	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3)
	{
		int blockType = world.getBlockMetadata(x, y, z);

		/*if (WrenchHelper.isWrenchClicked(tileEntity, player, side))
		{
			return true;
		}*/
		if (player != null && player.isSneaking())
		{
			return false;
		}
		if (blockType == 0 || blockType == 1)
		{
			if (player instanceof EntityPlayerMP)
			{
				player.openGui(Ztech.instance, blockType, world, x, y, z);
			}
			return true;
		}
		return super.onBlockActivated(world, x, y, z, player, side, f1, f2, f3);
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack itemStack)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof IWrenchable)
		{
			IWrenchable te = (IWrenchable)tileEntity;
			if (entityliving == null)
			{
				te.setFacing((short)2);
			}
			else
			{
				int l = MathHelper.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
				switch (l)
				{
				case 0: 
					te.setFacing((short)2);
					break;
				case 1: 
					te.setFacing((short)5);
					break;
				case 2: 
					te.setFacing((short)3);
					break;
				case 3: 
					te.setFacing((short)4);
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return null;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata)
	{
		switch (metadata)
		{
		case 0: 
			return new TileEntitySeedAnalyzer();
		case 1: 
			return new TileEntitySeedLibrary();
		default: 
			return super.createTileEntity(world, metadata);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
