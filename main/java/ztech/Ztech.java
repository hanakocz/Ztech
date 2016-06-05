package ztech;

import org.apache.logging.log4j.Logger;

import ztech.common.CommonProxy;
import ztech.init.ConfigurationHandler;
import ztech.init.ModBlocks;
import ztech.init.ModCreativeTab;
import ztech.init.ModItems;
import ztech.init.ModRecipes;
import ztech.items.ItemAdvancedElectricRod;
import ztech.items.ItemElectricRod;
import ztech.network.ChannelHandler;
import ztech.tileentities.TileEntitySeedAnalyzer;
import ztech.utils.ChunkloadCallback;
import ic2.api.item.IC2Items;
import ic2.api.recipe.Recipes;
import ic2.core.util.StackUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = Ztech.MODID, name = Ztech.MODNAME, version = "0.0.3", guiFactory = "ztech.client.gui.GuiFactory", dependencies = "required-after:IC2; after:gregtech_addon; after:IC2CA; after:Railcraft")
public class Ztech
{
	@Instance(Ztech.MODID)
	public static Ztech instance;

	@SidedProxy(clientSide = "ztech.client.ClientProxy", serverSide = "ztech.common.CommonProxy")
	public static CommonProxy proxy;

	public static final String MODID = "ztech";
	public static final String MODNAME = "Ztech";

	public static CreativeTabs tabMod = new ModCreativeTab();
	public static Logger logger;
	public static ConfigurationHandler config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		//Load configuration
		config = new ConfigurationHandler();
		FMLCommonHandler.instance().bus().register(config);
		config.init(event.getSuggestedConfigurationFile());

		//Register channel handler
		ChannelHandler.init();

		ModItems.init();
		TileEntitySeedAnalyzer.init();
		ModBlocks.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		// Register chunk loading callback
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkloadCallback());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ModRecipes.init();
		proxy.registerTileEntities();
	}
}
