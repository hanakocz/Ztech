package ztech.init;

import java.io.File;

import ztech.Ztech;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler
{
	public Configuration configuration;

	public static boolean enableElecticFishingRod; 

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
}
