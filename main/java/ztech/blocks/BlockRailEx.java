package ztech.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ztech.Ztech;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockRailEx extends BlockRail
{
    public ItemStack stack;
    public float speedAmplifier;
    public Class<? extends TileEntity> tileEntity;
    
    public BlockRailEx(String name, float speedAmplifier, Class<? extends TileEntity> tileEntity)
    {
        super();
        this.stack = new ItemStack(this);
        this.speedAmplifier = speedAmplifier;
        this.tileEntity = tileEntity;
        setBlockName(name);
        setBlockTextureName(Ztech.MODID + ":" + name);
        setHardness(0.7F);
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(Ztech.tabMod);
        GameRegistry.registerBlock(this, name);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int y, int x, int z)
    {
        return super.getRailMaxSpeed(world, cart, y, x, z) * speedAmplifier;
    }

    @Override
    public boolean hasTileEntity(int metadata)
    {
        return tileEntity != null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        try
        {
            return tileEntity != null ? tileEntity.newInstance() : null;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
