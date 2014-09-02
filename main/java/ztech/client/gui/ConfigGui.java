package ztech.client.gui;

import ztech.Ztech;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;

public class ConfigGui extends GuiConfig 
{
	public ConfigGui(GuiScreen parent) 
	{
		super(parent, new ConfigElement(Ztech.config.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), 
				Ztech.MODID, false, false, GuiConfig.getAbridgedConfigPath(Ztech.config.configuration.toString()));
	}
}