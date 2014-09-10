package ztech.client.gui;

import ic2.core.block.machine.container.ContainerStandardMachine;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ztech.Ztech;

public class GuiSeedAnalyzer extends GuiContainer
{
	public ContainerStandardMachine container;
	private static final ResourceLocation background = new ResourceLocation(Ztech.MODID, "textures/gui/guiSeedAnalyzer.png");

	public GuiSeedAnalyzer(ContainerStandardMachine container)
	{
		super(container);
		this.container = container;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
	{
		fontRendererObj.drawString("Seed Analyzer", 58, 6, 0x404040);
		fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int x, int y)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int j = (this.width - this.xSize) / 2;
		int k = (this.height - this.ySize) / 2;
		drawTexturedModalRect(j, k, 0, 0, this.xSize, this.ySize);        
		int chargeLevel = Math.round(14.0F * ((TileEntityStandardMachine)this.container.base).getChargeLevel());
		int progress = Math.round(24.0F * ((TileEntityStandardMachine)this.container.base).getProgress());
		if (chargeLevel > 0) 
		{
			drawTexturedModalRect(j + 56, k + 36 + 14 - chargeLevel, 176, 14 - chargeLevel, 14, chargeLevel);
		}
		if (progress > 0) 
		{
			drawTexturedModalRect(j + 79, k + 34, 176, 14, progress + 1, 16);
		}
	}
}
