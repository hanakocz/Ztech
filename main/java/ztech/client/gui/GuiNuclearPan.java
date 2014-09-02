package ztech.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import ztech.Ztech;
import ztech.containers.ContainerNuclearPan;
import ztech.tileentities.TileEntityNuclearPan;

public class GuiNuclearPan extends GuiContainer
{
	private static ResourceLocation texture = new ResourceLocation(Ztech.MODID + ":textures/gui/guiNuclearPan.png");
    TileEntityNuclearPan tile;

    public GuiNuclearPan(EntityPlayer player, TileEntityNuclearPan tile)
    {
        super(new ContainerNuclearPan(player, tile));
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        //fontRenderer.drawString(StatCollector.translateToLocal(entity.getInvName()), 8, 6, 4210752);
        fontRendererObj.drawString("Nuclear Pan", 8, 6, 4210752);
        fontRendererObj.drawString(String.format("Heat: %d%%", tile.heatPercent), 48, 54, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        float scaled = (tile.progress >= tile.heatPerItem) ? 1.0F : tile.progress / (float) tile.heatPerItem;
        drawTexturedModalRect(
                guiLeft + 71, // x
                guiTop + 29, // y
                176, // u
                0, // v
                Math.round(scaled * 24), // width
                17); // height
    }

}
