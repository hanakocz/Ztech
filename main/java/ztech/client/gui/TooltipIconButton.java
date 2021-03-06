package ztech.client.gui;

import ic2.core.GuiIconButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class TooltipIconButton extends GuiIconButton implements IHasTooltip
{
	public String tooltip = "";

	private static java.lang.reflect.Field y_field = null;
	static
	{
		try
		{
			y_field = GuiIconButton.class.getDeclaredField("textureY");
			y_field.setAccessible(true);
		}
		catch (NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}

	public TooltipIconButton(int id, int x, int y, int w, int h, ResourceLocation texture, int textureX, int textureY)
	{
		super(id, x, y, w, h, texture, textureX, textureY);
	}

	public TooltipIconButton(int id, int x, int y, int w, int h, ItemStack icon, boolean drawQuantity)
	{
		super(id, x, y, w, h, icon, drawQuantity);
	}


	public void setTextureY(int new_y)
	{
		try 
		{
			y_field.set(this, new_y);
		}
		catch (IllegalAccessException e) {}
		catch (IllegalArgumentException e) {}
	}

	@Override
	public String getActiveTooltip(int mouse_x, int mouse_y)
	{
		if (mouse_x < xPosition || mouse_x >= xPosition + width)
		{
			// Left/right of button.
			return null;
		}

		if (mouse_y < yPosition || mouse_y >= yPosition + height)
		{
			// Above/below button.
			return null;
		}

		// On button.
		return tooltip;
	}
}
