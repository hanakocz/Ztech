package ztech.init;

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
	
    public static void init()
    {
    	initNuclearPan();
    	initElectricRails();
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
}
