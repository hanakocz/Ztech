package ztech.client.entities;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySparksFX extends EntityFX
{
    public EntitySparksFX(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6);
        particleGravity = Blocks.snow.blockParticleGravity;
        particleMaxAge *= 3;
    }

    public EntitySparksFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
    {
        super(par1World, par2, par4, par6, par8, par10, par12);
        particleGravity = Blocks.snow.blockParticleGravity;
        particleMaxAge *= 3;
    }

    // Clamp
    public static float clamp(float y)
    {
        if (y < 0.0F) return 0.0F;
        if (y > 1.0F) return 1.0F;
        return y;
    }

    // Linear kernel
    public static float linear(float x, float startX, float endX)
    {
        float y = (x - startX) / (endX - startX);
        return clamp(y);
    }

    // Handy gaussian kernel
    public static float gauss(float x)
    {
        double d = x * Math.E;
        return (float) Math.exp(- d * d);
    }

    // This is really cool function!... probably
    public static float cool(float x, float start, float end)
    {
        return clamp(gauss(1.0F - linear(x, start, end)));
    }

    @Override
    public void renderParticle(Tessellator t, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        // Thermal color transition
        // inverted gradient:
        // red -> yellow -> white
        // transparent --> opaque
        float stage = 1.0F - (float) particleAge / (float) particleMaxAge;
        particleRed   = cool(stage, 0.000F, 1.0F / 3.0F);
        particleGreen = cool(stage, 0.000F, 2.0F / 3.0F);
        particleBlue  = cool(stage, 0.000F, 1.000F);
        particleAlpha = particleRed;
        super.renderParticle(t, par2, par3, par4, par5, par6, par7);
    }

    @Override
    public float getBrightness(float par1)
    {
        return particleRed;
    }

    @Override
    public int getBrightnessForRender(float par1)
    {
        int j = (int) (particleRed * 240);
        if (j < 0) j = 0;
        if (j > 240) j = 240;
        return j | j << 16;
    }

    public static final Random rand = new Random();

    public static void spawnSpark(World world, double startX, double startY, double startZ, double motionX, double motionY, double motionZ)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null)
        {
            int i = mc.gameSettings.particleSetting;
            if (i == 1 && world.rand.nextInt(3) == 0) i = 2;
            //double d6 = mc.renderViewEntity.posX - startX;
            //double d7 = mc.renderViewEntity.posY - startY;
            //double d8 = mc.renderViewEntity.posZ - startZ;
            //double d9 = 16.0D;
            if (/*d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9 ||*/ i > 1) return;
            mc.effectRenderer.addEffect(new EntitySparksFX(world, startX, startY, startZ, motionX, motionY, motionZ));
        }
    }

    public static void spawnSpark(World world, double startX, double startY, double startZ)
    {
        spawnSpark(world, startX, startY, startZ, rand.nextGaussian() * 0.15D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.15D);
    }

    public static void spawnSparks(World world, double startX, double startY, double startZ)
    {
        for (int i = 0; i < 20; i++) spawnSpark(world, startX, startY, startZ);
    }

    public static void spawnRandomSparks(World world, int blockX, int blockY, int blockZ)
    {
        for (int i = 0; i < 20; i++) spawnSparks(world, blockX + rand.nextGaussian(), blockY, blockZ + rand.nextGaussian());
    }
}