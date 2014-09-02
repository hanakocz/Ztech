package ztech.items;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;

import java.util.List;
import java.util.Random;

import ztech.Ztech;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElectricFishBase extends Item implements IElectricItem
{
	double charge;
	int tier;
	double transfer;
	int fish;
	int selfdamage;
	int damage;
	double useLeft;
	double useRight;

	public ItemElectricFishBase(String name, double charge, int tier, double transfer, int fish, int selfdamage, int damage, double useLeft, double useRight)
	{
		super();
		this.charge = charge;
		this.tier = tier;
		this.transfer = transfer;
		this.fish = fish;
		this.selfdamage = selfdamage;
		this.damage = damage;
		this.useLeft = useLeft;
		this.useRight = useRight;
		setMaxDamage(27);
		setMaxStackSize(1);
		setUnlocalizedName(name);
		setTextureName(Ztech.MODID + ":" + name);
		setCreativeTab(Ztech.tabMod);
		GameRegistry.registerItem(this, name);
	}

	@Override
	public boolean canProvideEnergy(ItemStack stack)
	{
		return true;
	}

	@Override
	public Item getChargedItem(ItemStack stack)
	{
		return this;
	}

	@Override
	public Item getEmptyItem(ItemStack stack)
	{
		return this;
	}

	@Override
	public double getMaxCharge(ItemStack stack)
	{
		return charge;
	}

	@Override
	public int getTier(ItemStack stack)
	{
		return tier;
	}

	@Override
	public double getTransferLimit(ItemStack stack)
	{
		return transfer;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!ElectricItem.manager.canUse(itemStack, useRight))
		{
			if (player instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)player).addChatMessage(new ChatComponentText(StatCollector.translateToLocal("info.outOfEnergy")));
			}
			return itemStack;
		}
		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);

		if (movingobjectposition == null)
		{
			return itemStack;
		}
		if (movingobjectposition.typeOfHit != MovingObjectType.BLOCK)
		{
			return itemStack;
		}
		if (world.getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ) == Blocks.water ||
				world.getBlock(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ) == Blocks.flowing_water)
		{
			if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			{
				Ztech.proxy.PlaySound();
			}
			else
			{
				EntityItem entityItem = new EntityItem(world, movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ, new ItemStack(Items.cooked_fished, new Random().nextInt(fish)));
				entityItem.motionX = movingobjectposition.blockX - player.posX*2;
				entityItem.motionY = movingobjectposition.blockY - player.posY*2;
				entityItem.motionZ = movingobjectposition.blockZ - player.posZ*2;
				world.spawnEntityInWorld(entityItem);
			}
			ElectricItem.manager.use(itemStack, useRight, player);
			if (player instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)player).addChatMessage(new ChatComponentText(String.format(StatCollector.translateToLocal("info.discharged"), useRight)));
			}
		}
		return itemStack;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity)
	{
		if (entity instanceof EntityLiving)
		{
			if (ElectricItem.manager.canUse(itemStack, useLeft))
			{
				ElectricItem.manager.use(itemStack, useLeft, player);
				EntityLiving entityLiving = (EntityLiving) entity;
				entityLiving.attackEntityFrom(ElectricFishDamageSource.electricity, damage);
				player.attackEntityFrom(ElectricFishDamageSource.electricity, selfdamage);
			}
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List itemList)
	{
		ItemStack itemstack = new ItemStack(this, 1);
		ElectricItem.manager.charge(itemstack, 0x7fffffff, 0x7fffffff, true, false);
		itemList.add(itemstack);
		itemList.add(new ItemStack(this, 1, getMaxDamage()));
	}
}
