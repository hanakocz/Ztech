package ztech.init;

import cpw.mods.fml.common.registry.GameRegistry;
import ztech.Ztech;
import ztech.blocks.*;
import ztech.tileentities.TileEntityElectricRail;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class ModBlocks
{
	public static Block nuclearPan;
	public static Block blockStandardElectricTrack;
	public static Block blockAdvancedTrack;
	public static Block blockAdvancedElectricTrack;
	public static Block blockThirdRail;
	public static Block blockMaglevRail;
	public static Block blockMaglevCover;
	public static BlockNetworkAnchor blockNetworkAnchor;
	public static Block blockSeedManager;

	public static void init()
	{
		initNuclearPan();
		initElectricRails();
		initNetworkAnchor();
		initSeedManager();
	}

	private static void initNuclearPan()
	{
		if (Ztech.config.enableNuclearPan)
		{
			nuclearPan = new BlockNuclearPan("nuclearPan", new Material(MapColor.ironColor));
		}
	}

	private static void initElectricRails()
	{
		if (Ztech.config.enableElectricRails)
		{
			blockStandardElectricTrack = new BlockRailEx("trackElectric", 1.0F, TileEntityElectricRail.class);
			blockAdvancedTrack = new BlockRailEx("trackAdvanced", 1.25F, null);
			blockAdvancedElectricTrack = new BlockRailEx("trackAdvancedElectric", 1.25F, TileEntityElectricRail.class);
			blockThirdRail = new BlockRailThird("thirdRail", Material.rock);
			blockMaglevRail = new BlockMaglevRail("maglevRail", Material.rock);
			blockMaglevCover = new BlockMaglevCover("maglevCover", (float) Ztech.config.maglevSpeed);
		}
	}

	private static void initNetworkAnchor()
	{
		if (Ztech.config.enableNetworkAnchor)
		{
			blockNetworkAnchor = new BlockNetworkAnchor(new Material(MapColor.ironColor));
		}
	}

	private static void initSeedManager()
	{
		if (Ztech.config.enableSeedManager)
		{
			blockSeedManager = new BlockSeedManager();
		}
	}
}
