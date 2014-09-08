package ztech.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

//Allows to draw smaller buttons
public class GuiButtonFix extends GuiButton
{
	public GuiButtonFix(int id, int x, int y, String displayString)
	{
		super(id, x, y, displayString);
	}

	public GuiButtonFix(int id, int x, int y, int w, int h, String displayString)
	{
		super(id, x, y, w, h, displayString);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		if (visible)
		{
			mc.renderEngine.bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int hoverState = getHoverState(field_146123_n);

			// !!!
			int w2 = width / 2;
			int w3 = width - w2;
			int h2 = height / 2;
			int h3 = height - h2;
			int u = 200 - w3;
			int v = 46 + hoverState * 20;
			int v2 = v + 20 - h3;

			drawTexturedModalRect(xPosition, yPosition, 0, v, w2, h2); // top-left
			drawTexturedModalRect(xPosition + w2, yPosition, u, v, w3, h2); // top-right

			drawTexturedModalRect(xPosition, yPosition + h2, 0, v2, w2, h3); // bottom-left
			drawTexturedModalRect(xPosition + w2, yPosition + h2, u, v2, w3, h3); // bottom-right
			// !!!

			mouseDragged(mc, mouseX, mouseY);
			int textColor = 14737632;

			if (!enabled)
			{
				textColor = -6250336;
			}
			else if (field_146123_n)
			{
				textColor = 16777120;
			}

			drawCenteredString(
					mc.fontRenderer,
					StatCollector.translateToLocal(displayString),
					Math.round(xPosition + width / 2.0F), // !!!
					yPosition + (height - 8) / 2,
					textColor);
		}
	}
}
