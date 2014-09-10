package ztech.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ic2.core.init.InternalName;
import ic2.core.item.ItemCropSeed;

public class ExtendedCropSeed  extends ItemCropSeed
{
    public static final String yellow = '\247' + "e";
	public ExtendedCropSeed()
	{
		super(InternalName.itemCropSeed);
	}
	
    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
    {
        /*if (getScannedFromStack(stack) == -42) {
            // This is a custom setting for the fake display seed used by SeedAnalyzerTileEntityUU
            list.clear();

            String name = "Unknown Seeds";
            int id = getIdFromStack(stack);
            if (id != -1) {
                name = CropCard.getCrop(id).name();
            }
            list.add(name);
            list.add("\u00a72Gr\u00a77 ?");
            list.add("\u00a76Ga\u00a77 ?");
            list.add("\u00a73Re\u00a77 ?");
            list.add(yellow + "Lv ?");

            if (player.capabilities.isCreativeMode) {
                list.add("\u00a7oSc " + getScannedFromStack(stack));
            }

            return;
        }*/

        super.addInformation(stack, player, list, flag);
        if (getScannedFromStack(stack) == 4)
        {
            int growth = getGrowthFromStack(stack);
            int gain = getGainFromStack(stack);
            int resistance = getResistanceFromStack(stack);
            int total = growth + gain + resistance;
            list.add(yellow + "Lv " + total);
        }

        if (player.capabilities.isCreativeMode)
        {
            list.add("\u00a7oSc " + getScannedFromStack(stack));
        }
    }
}
