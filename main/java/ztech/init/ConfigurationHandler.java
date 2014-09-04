package ztech.init;

import java.io.File;
import java.util.LinkedList;

import ztech.Ztech;
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
			
			Property prop = configuration.get("NuclearPan", "HeatPerItem", heatPerItem);
			prop.comment = "Amount of heat required to cook one item.";
			heatPerItem = prop.getInt(heatPerItem);

			prop = configuration.get("NuclearPan", "PassiveCooling", passiveCooling);
			prop.comment = "Amount of heat to wear from reactor when not cooking (heat per second).";
			passiveCooling = prop.getInt(passiveCooling);

			prop = configuration.get("NuclearPan", "ActiveCooling", activeCooling);
			prop.comment = "Amount of heat to wear from reactor when cooking (heat per second).";
			activeCooling = prop.getInt(activeCooling);
			
			enableElectricRails = configuration.get(Configuration.CATEGORY_GENERAL, "enableElectricRails", true).getBoolean();
			configuration.addCustomCategoryComment("Energy", "Energy consumption rates when boosting cart (EU/t)");
            electricTracksEU = configuration.get("Energy", "ElectricTracksEU", electricTracksEU).getDouble();
            thirdRailEU      = configuration.get("Energy", "ThirdRailEU", thirdRailEU).getDouble();
            maglevRailEU     = configuration.get("Energy", "MaglevRailEU", maglevRailEU, "Amount of EU/t to consume per magnet rail block, e.g. it will be doubled in total but each part will be drained from different magnets.").getDouble();
            configuration.addCustomCategoryComment("Speed", "Max speed factors of tracks relative to standard tracks (1.0).");
            advancedSpeed    = configuration.get("Speed", "AdvancedTracksSpeed", advancedSpeed).getDouble();
            maglevSpeed      = configuration.get("Speed", "MaglevSpeed", maglevSpeed).getDouble();
            
            enableSeedManager = configuration.get(Configuration.CATEGORY_GENERAL, "enableSeedManager", false).getBoolean();
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
