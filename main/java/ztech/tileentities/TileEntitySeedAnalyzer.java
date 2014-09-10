package ztech.tileentities;

import ic2.api.item.IC2Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeOutput;
import ic2.core.BasicMachineRecipeManager;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.item.ItemCropSeed;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import ztech.client.gui.GuiSeedAnalyzer;

public class TileEntitySeedAnalyzer extends TileEntityStandardMachine
{
	public static BasicMachineRecipeManager analyzer;
	public static final int[] cost_to_upgrade = {10, 90, 900, 9000};

	public TileEntitySeedAnalyzer()
	{
		super(10, 400, 1);
		this.inputSlot = new InvSlotProcessableGeneric(this, "input", 0, 1, analyzer);
	}

	public static void init()
	{
		analyzer = new BasicMachineRecipeManager();
		analyzer.addRecipe(new RecipeInputItemStack(IC2Items.getItem("cropSeed"), 1), null, new ItemStack[] { IC2Items.getItem("cropSeed") });
	}

	public static boolean isSeed(ItemStack stack)
	{
		if (stack == null)
		{
			return false;
		}

		return stack.getItem() == IC2Items.getItem("cropSeed").getItem();
	}

	public void updateEntity()
	{
		super.updateEntity();
	}
	/* TODO	
    public boolean canOperate()
    {
        if (isRedstonePowered())
        {
            boolean need_input = (inventory[0] == null);
            boolean need_output = isSeed(inventory[2]);

            if (need_input && need_output) {
                if (ItemCropSeed.getScannedFromStack(inventory[2]) < 4) {
                    inventory[0] = inventory[2];
                    inventory[2] = null;
                    return true;
                }
            }

            for (int dir=0; dir<4; dir++) {
                if (!need_input && !need_output) {
                    break;
                }

                int x = xCoord;
                int y = yCoord;
                int z = zCoord;
                if (dir == 0) {
                    x++;
                } else if (dir == 1) {
                    x--;
                } else if (dir == 2) {
                    z++;
                } else {
                    z--;
                }

                TileEntity te = worldObj.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntitySeedLibrary)
                {
                	TileEntitySeedLibrary library = (TileEntitySeedLibrary) te;
                    if (need_input && library.energy > 0)
                    {
                        ItemStack seed = library.getResearchSeed();
                        if (seed != null) {
                            inventory[0] = seed;
                            need_input = false;
                        }
                    }

                    if (need_output)
                    {
                        library.storeSeeds(inventory[2]);
                        inventory[2] = null;
                        need_output = false;
                    }
                }
            }
        }

        if (!isSeed(inventory[0])) {
            return false;
        }

        if (inventory[2] != null) {
            return false;
        }

        byte scan = ItemCropSeed.getScannedFromStack(inventory[0]);

        if (scan < 0) {
            scan = 0;
        }

        if (scan > 3) {
            return false;
        }

        defaultOperationLength = cost_to_upgrade[scan] / (defaultEnergyConsume * cost_reduction);
        return true;
    }*/

	@Override
	public RecipeOutput getOutput()
	{
		if (this.inputSlot.isEmpty())
		{
			return null;
		}
		RecipeOutput output = this.inputSlot.process();
		if (output == null)
		{
			return null;
		}

		if (this.outputSlot.canAdd(output.items))
		{
			ItemStack old_seed = this.inputSlot.get();
			short id = ItemCropSeed.getIdFromStack(old_seed);
			byte growth = ItemCropSeed.getGrowthFromStack(old_seed);
			byte gain = ItemCropSeed.getGainFromStack(old_seed);
			byte resistance = ItemCropSeed.getResistanceFromStack(old_seed);
			byte scan = ItemCropSeed.getScannedFromStack(old_seed);

			if (scan < 0)
			{
				scan = 0;
			}

			if (scan > 3)
			{
				return null;
			}
			operationLength = cost_to_upgrade[scan] / energyConsume;
			old_seed = ItemCropSeed.generateItemStackFromValues(id, growth, gain, resistance, (byte)(scan + 1));
			return new RecipeOutput(old_seed.getTagCompound(), new ItemStack[] { old_seed });
		}
		return null;
	}

	@Override
	public void operateOnce(RecipeOutput output, List<ItemStack> processResult)
	{
		this.inputSlot.consume();

		this.outputSlot.add(processResult);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiScreen getGui(EntityPlayer player, boolean bool)
	{ 
		return new GuiSeedAnalyzer(new ContainerStandardMachine(player, this));
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return direction.ordinal() != getFacing();
	}

	@Override
	public List<ItemStack> getCompatibleUpgradeList()
	{
		List<ItemStack> itemstack = new ArrayList();
		itemstack.add(IC2Items.getItem("overclockerUpgrade"));
		itemstack.add(IC2Items.getItem("transformerUpgrade"));
		itemstack.add(IC2Items.getItem("energyStorageUpgrade"));
		itemstack.add(IC2Items.getItem("ejectorUpgrade"));
		return itemstack;
	}

	@Override
	public String getInventoryName()
	{
		return "Seed Analyzer";
	}

	@Override
	public List<String> getNetworkedFields()
	{
		List<String> ret = super.getNetworkedFields();

		ret.add("energy");

		return ret;
	}
}
