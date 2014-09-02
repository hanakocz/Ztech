package ztech.init;

import ztech.Ztech;
import ztech.blocks.BlockNuclearPan;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class ModBlocks
{
	public static Block nuclearPan;
	
    public static void init()
    {
    	initNuclearPan();
    }
    
    private static void initNuclearPan()
    {
    	if (Ztech.config.enableNuclearPan)
    	{
        	nuclearPan = new BlockNuclearPan("nuclearPan", new Material(MapColor.ironColor));
    	}
    }
}
