package ztech.init;

import java.io.File;
import java.util.LinkedList;

import ztech.Ztech;
import ztech.utils.RealmRules;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigurationHandler
{
	public Configuration configuration;

	public static boolean enableElecticFishingRod;

	public static boolean enableNuclearPan;
	public static final int NOMINAL_HEAT = 8500;
	public static final int NOMINAL_HPS = 170000;
	public static int heatPerItem = 170000;
	public static int passiveCooling = 5;
	public static int activeCooling = 20;
	public static final LinkedList<ItemStack> panWhiteList = new LinkedList<ItemStack>();	

	public static boolean enableElectricRails;
	public static int coverRenderId;	
	// ===== POWER PARAMATERS =====
	public double electricTracksEU = 1.0D; // EU/t
	public double thirdRailEU      = 1.0D;
	public double maglevRailEU     = 0.1D;
	// ===== SPEED AMPLIFIERS =====
	public double advancedSpeed    = 1.25; // xN
	public double maglevSpeed      = 3.0D; // xN

	public static boolean enableSeedManager;

	public static boolean enableNetworkAnchor;
	// Realm rules. Will be passed from server to client.
	public RealmRules serverRules = new RealmRules(); // source copy of config for a side
	public RealmRules clientRules = new RealmRules(); // working copy for client or source config for server
	public double energyBase = 0; // energy consumption variables.
	public double energyPerTile = 0; // -//-
	public double energyPerChunk = 4; // -//-
	public int hardLimit = 0; // 0 = disabled/infinity
	public double scanRateBase = 0;
	public double scanRateA = 100;
	public double scanRateB = 1.3;

	public void init(File configFile)
	{
		if (configuration == null)
		{
			configuration = new Configuration(configFile);
		}
		loadConfiguration();
	}

	private void loadConfiguration()
	{
		try
		{
			enableElecticFishingRod = configuration.get(Configuration.CATEGORY_GENERAL, "enableElecticFishingRod", true).getBoolean();
			enableNuclearPan = configuration.get(Configuration.CATEGORY_GENERAL, "enableNuclearPan", true).getBoolean();

			Property prop = configuration.get("mod_NuclearPan", "HeatPerItem", heatPerItem);
			prop.comment = "Amount of heat required to cook one item.";
			heatPerItem = prop.getInt(heatPerItem);

			prop = configuration.get("mod_NuclearPan", "PassiveCooling", passiveCooling);
			prop.comment = "Amount of heat to wear from reactor when not cooking (heat per second).";
			passiveCooling = prop.getInt(passiveCooling);

			prop = configuration.get("mod_NuclearPan", "ActiveCooling", activeCooling);
			prop.comment = "Amount of heat to wear from reactor when cooking (heat per second).";
			activeCooling = prop.getInt(activeCooling);

			enableElectricRails = configuration.get(Configuration.CATEGORY_GENERAL, "enableElectricRails", true).getBoolean();
			configuration.addCustomCategoryComment("mod_ElectricRails.Energy", "Energy consumption rates when boosting cart (EU/t)");
			electricTracksEU = configuration.get("mod_ElectricRails.Energy", "ElectricTracksEU", electricTracksEU).getDouble();
			thirdRailEU      = configuration.get("mod_ElectricRails.Energy", "ThirdRailEU", thirdRailEU).getDouble();
			maglevRailEU     = configuration.get("mod_ElectricRails.Energy", "MaglevRailEU", maglevRailEU, "Amount of EU/t to consume per magnet rail block, e.g. it will be doubled in total but each part will be drained from different magnets.").getDouble();
			configuration.addCustomCategoryComment("mod_ElectricRails.Speed", "Max speed factors of tracks relative to standard tracks (1.0).");
			advancedSpeed    = configuration.get("mod_ElectricRails.Speed", "AdvancedTracksSpeed", advancedSpeed).getDouble();
			maglevSpeed      = configuration.get("mod_ElectricRails.Speed", "MaglevSpeed", maglevSpeed).getDouble();

			enableSeedManager = configuration.get(Configuration.CATEGORY_GENERAL, "enableSeedManager", true).getBoolean();
			enableNetworkAnchor = configuration.get(Configuration.CATEGORY_GENERAL, "enableNetworkAnchor", true).getBoolean();

			serverRules.hardGregTechRecipe = configuration.get("mod_NetworkAnchor", "HardGregTechRecipe", serverRules.hardGregTechRecipe).getBoolean();
			clientRules.hardGregTechRecipe = serverRules.hardGregTechRecipe; // copy to client

			energyBase = configuration.get("mod_NetworkAnchor", "EnergyBase", energyBase).getDouble();
			if (energyBase < 0)
			{
				energyBase = 0; // sentinel
			}

			energyPerTile = configuration.get("mod_NetworkAnchor", "EnergyPerTile", energyPerTile).getDouble();
			if (energyPerTile < 0)
			{
				energyPerTile = 0; // sentinel
			}

			energyPerChunk = configuration.get("mod_NetworkAnchor", "EnergyPerChunk", energyPerChunk).getDouble();
			if (energyPerChunk < 0)
			{
				energyPerChunk = 0; // sentinel
			}

			// Update helper variable
			serverRules.bEnergy = energyBase != 0 || energyPerTile != 0 || energyPerChunk != 0;
			clientRules.bEnergy = serverRules.bEnergy; // copy to client

			hardLimit = configuration.get("mod_NetworkAnchor", "ScanRateHardLimit", hardLimit).getInt();
			if (hardLimit < 0)
			{
				hardLimit = 0; // sentinel
			}

			serverRules.wrenchRequired = configuration.get("mod_NetworkAnchor", "WrenchRequired", serverRules.wrenchRequired).getBoolean();
			clientRules.wrenchRequired = serverRules.wrenchRequired; // copy to client

			serverRules.wrenchChance = (float) configuration.get("mod_NetworkAnchor", "WrenchChance", serverRules.wrenchChance).getDouble();
			if (serverRules.wrenchChance < 0.0F)
			{
				serverRules.wrenchChance = 0.0F; // sentinel
			}
			if (serverRules.wrenchChance > 1.0F)
			{
				serverRules.wrenchChance = 1.0F; // sentinel
			}
			clientRules.wrenchChance = serverRules.wrenchChance; // copy to client

			scanRateBase = configuration.get("mod_NetworkAnchor", "ScanRateBase", scanRateBase).getDouble();
			if (scanRateBase < 0)
			{
				scanRateBase = 0;
			}

			scanRateA = configuration.get("mod_NetworkAnchor", "ScanRateA", scanRateA).getDouble();
			if (scanRateA < 0)
			{
				scanRateA = 0;
			}

			scanRateB = configuration.get("mod_NetworkAnchor", "ScanRateB", scanRateB).getDouble();
			if (scanRateB < 1)
			{
				scanRateB = 1;
			}
		}
		catch (Exception e)
		{
			Ztech.logger.error("Mod has a problem loading it's configuration", e);
		}
		finally
		{
			if (configuration.hasChanged())
			{
				configuration.save();
			}
		}
	}

	public void save()
	{
		if (configuration.hasChanged())
		{
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.modID.equalsIgnoreCase(Ztech.MODID))
		{
			loadConfiguration();
		}
	}

	public static boolean inPanWhiteList(ItemStack stack)
	{
		for (ItemStack i : panWhiteList)
		{
			if (stack.isItemEqual(i))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean addToWhiteList(ItemStack stack)
	{
		return panWhiteList.add(stack.copy());
	}
}
