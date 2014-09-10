package ztech.init;

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameData;
import ic2.core.Ic2Items;
import ic2.core.item.ItemCropSeed;
import ztech.Ztech;
//import ztech.items.ExtendedCropSeed;
import ztech.items.ItemAdvancedElectricRod;
import ztech.items.ItemElectricRail;
import ztech.items.ItemElectricRod;
import ztech.items.ItemFoodOnAStick;
import ztech.items.ItemPanFood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ObjectIntIdentityMap;

public class ModItems
{
	// Electic Fishing Rod
	public static ItemStack electricFishingRod;
	public static ItemStack advancedElectricFishingRod;
	// Nuclear Pan
	public static ItemStack meltedSugar;
	public static ItemStack sugarCandy;
	public static ItemStack lollipop;
	// Electric Rails
	public static ItemStack electricRail;

	public static void init()
	{
		initElecticFishingRod();
		initNuclearPan();
		initElecticRails();
		initSeedManager();
	}

	private static void initElecticFishingRod()
	{
		if (Ztech.config.enableElecticFishingRod)
		{
			electricFishingRod = new ItemStack(new ItemElectricRod("electricFishingRod"));
			advancedElectricFishingRod = new ItemStack(new ItemAdvancedElectricRod("advancedElectricFishingRod"));
		}
	}

	private static void initNuclearPan()
	{
		if (Ztech.config.enableNuclearPan)
		{
			meltedSugar = new ItemStack(new ItemPanFood("meltedSugar", 2, 0.0F, false));
			sugarCandy = new ItemStack(new ItemPanFood("sugarCandy", 4, 0.0F, false));
			lollipop = new ItemStack(new ItemFoodOnAStick("lollipop", 8, 0.0F, false));
		}
	}

	private static void initElecticRails()
	{
		if (Ztech.config.enableElectricRails)
		{
			electricRail = new ItemStack(new ItemElectricRail("electricRail"));
		}
	}

	private static void initSeedManager()
	{
		if (Ztech.config.enableSeedManager)
		{
/*			try
			{
				Field registryObjects = Item.itemRegistry.getClass().getSuperclass().getSuperclass().getDeclaredField("registryObjects");
				registryObjects.setAccessible(true);
				Map val = (Map) registryObjects.get(Item.itemRegistry);
				//val.remove("IC2:itemCropSeed");
				Field underlyingIntegerMap = Item.itemRegistry.getClass().getSuperclass().getDeclaredField("underlyingIntegerMap");
				underlyingIntegerMap.setAccessible(true);
				ObjectIntIdentityMap intMap = (ObjectIntIdentityMap) underlyingIntegerMap.get(Item.itemRegistry);
				Field field_148749_a = intMap.getClass().getDeclaredField("field_148749_a");
				field_148749_a.setAccessible(true);
				IdentityHashMap idHash = (IdentityHashMap) field_148749_a.get(intMap);
				int i = idHash.get(Ic2Items.cropSeed.getItem()).hashCode();
				//idHash.remove(Ic2Items.cropSeed.getItem());
				Field field_148748_b = intMap.getClass().getDeclaredField("field_148748_b");
				field_148748_b.setAccessible(true);
				List list = (List) field_148748_b.get(intMap);
				//list.remove(Ic2Items.cropSeed.getItem());
				Field mainData = GameData.class.getDeclaredField("mainData");
				mainData.setAccessible(true);
				GameData data = (GameData) mainData.get(GameData.class);
				Field availabilityMap = data.getClass().getDeclaredField("availabilityMap");
				availabilityMap.setAccessible(true);
				BitSet set = (BitSet)availabilityMap.get(data);
				set.clear(i);
			}
			catch (NoSuchFieldException e) {}
			catch (IllegalAccessException e) {}*/
//			Ic2Items.cropSeed = new ItemStack(new ExtendedCropSeed());
		}
	}
}
