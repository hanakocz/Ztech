package ztech.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import ztech.Ztech;
import ztech.containers.ContainerSeedLibrary;
import ztech.tileentities.TileEntitySeedLibrary;
import ztech.utils.SeedLibraryFilter;

public class GuiSeedLibrary extends GuiContainer
{
	protected static final ResourceLocation texture = new ResourceLocation(Ztech.MODID + ":textures/gui/guiSeedLibrary.png");
	
    public final String BLACK = "\u00A70";
    public final String DARK_BLUE = "\u00A71";
    public final String DARK_GREEN = "\u00A72";
    public final String DARK_AQUA = "\u00A73";
    public final String DARK_RED = "\u00A74";
    public final String DARK_PURPLE = "\u00A75";
    public final String GOLD = "\u00A76";
    public final String GRAY = "\u00A77";
    public final String DARK_GRAY = "\u00A78";
    public final String BLUE = "\u00A79";
    public final String GREEN = "\u00A7A";
    public final String AQUA = "\u00A7B";
    public final String RED = "\u00A7C";
    public final String LIGHT_PURPLE = "\u00A7D";
    public final String YELLOW = "\u00A7E";
    public final String WHITE = "\u00A7F";

    public int lastMouseX = -1;
    public int lastMouseY = -1;
    String tooltip = null;

    protected List realControls = null;
    protected List noControls = new ArrayList();
    private boolean rightClick = false;
    private GuiButton rightSelect;

    public int world_x, world_y, world_z;
    public static final int BORDER = 4;
    public int main_width, main_height, left, top, center, middle, right, bottom, sliders_x, sliders_y, sliders_spacing;
    public int current_slider = -1, drag_start_x = 0, drag_start_value = 0;
    public TooltipIconButton unk_type_button, unk_ggr_button;

    public TooltipButton[] directionButtons = new TooltipButton[6];

    public GuiSeedLibrary(EntityPlayer player, TileEntity seedmanager)
    {
        super(new ContainerSeedLibrary(player, (TileEntitySeedLibrary)seedmanager));

        world_x = seedmanager.xCoord;
        world_y = seedmanager.yCoord;
        world_z = seedmanager.zCoord;


        ySize = 222;

        main_width = xSize - BORDER * 2;
        main_height = (ySize - 96) - BORDER * 2 - 18*2;

        left = BORDER;
        top = BORDER;
        center = left + main_width/2;
        middle = top + main_height/2;
        right = left + main_width;
        bottom = top + main_height;

        sliders_x = center + main_width / 4 - (63/2);
        sliders_y = top + 2 + 9 - 1;
        sliders_spacing = 11 + 9;
    }

    public TileEntitySeedLibrary getLibrary()
    {
        World world = Minecraft.getMinecraft().thePlayer.worldObj;
        TileEntity te = world.getTileEntity(world_x, world_y, world_z);

        if (te instanceof TileEntitySeedLibrary) 
        {
            return (TileEntitySeedLibrary) te;
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        TooltipIconButton importButton = new TooltipIconButton(0, guiLeft + 132, guiTop + 86, 18, 20, texture, 176+2, 0+1);
        importButton.tooltip = "Import seeds";
        buttonList.add(importButton);

        TooltipIconButton exportButton = new TooltipIconButton(1, guiLeft + 151, guiTop + 86, 18, 20, texture, 176+2, 18+1);
        exportButton.tooltip = "Export seeds";
        buttonList.add(exportButton);


        unk_type_button = new TooltipIconButton(2, guiLeft + left + main_width/8 - 9, guiTop + middle + 20, 18, 20, texture, 176+2, 90+1);
        unk_type_button.tooltip = "Seeds with unknown type included";
        buttonList.add(unk_type_button);

        unk_ggr_button = new TooltipIconButton(3, guiLeft + left + (main_width*3)/8 - 9, guiTop + middle + 20, 18, 20, texture, 176+2, 90+1);
        unk_ggr_button.tooltip = "Seeds with unknown GGR included";
        buttonList.add(unk_ggr_button);


        int x = guiLeft + left + 3;
        int y = guiTop + 86;
        for (int dir = 0; dir < 6; dir++)
        {
            // Down = -Y = 0
            // Up = +Y = 1
            // North = -Z = 2
            // South = +Z = 3
            // West = -X = 4
            // East = +X = 5
            String key = "BTNSWE";
            String name = "" + key.charAt(dir);

            TooltipButton button = new TooltipButton(dir + 4, x + dir*13, y, 12, 20, name);
            buttonList.add(button);
            directionButtons[dir] = button;
        }

        String[] labels = new String[] {"Growth", "Gain", "Resistance", "Total"};
        String[] tooltips = new String[] {"Faster growth speed",
                                          "More resources on harvest",
                                          "Better weed resistance",
                                          "Worse environmental tolerance"};
        int label_left = guiLeft + center + 10;
        int label_width = (main_width / 2) - 20;
        int label_top = guiTop + top + 2;
        int label_height = 9;

        for (int i = 0; i < 4; i++)
        {
            TooltipLabel label = new TooltipLabel(-1, label_left, label_top, label_width, label_height, labels[i]);
            label.tooltip = tooltips[i];
            buttonList.add(label);

            label_top += 9 + 11;
        }

        realControls = buttonList;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
    	TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return;
        }

        seedlibrary.sendGuiButton(guibutton.id, rightClick);
        super.actionPerformed(guibutton);
    }

    public void drawCenteredString(String s, int x, int y, int color)
    {
        fontRendererObj.drawString(s, x - fontRendererObj.getStringWidth(s) / 2, y, color);
    }

    public void drawRightString(String s, int x, int y, int color)
    {
    	fontRendererObj.drawString(s, x - fontRendererObj.getStringWidth(s), y, color);
    }

    public void draw3DRect(int left, int top, int right, int bottom)
    {
        drawRect(left, top, right, bottom, 0xff373737);
        drawRect(left+1, top+1, right, bottom, 0xffffffff);
        drawRect(left+1, top+1, right-1, bottom-1, 0xffc6c6c6);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouse_x, int mouse_y)
    {
    	TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
            return;
        }

        SeedLibraryFilter filter = seedlibrary.getGUIFilter();

        drawCenteredString("Seed Type", left + main_width / 4, top + 2, 0x404040);
        drawCenteredString(filter.getCropName(), left + main_width / 4, top + 2 + 8 + 1 + 18 + 2, 0x404040);

        String count;
        if (seedlibrary.seeds_available >= 65535)
        {
            count = "MANY";
        }
        else
        {
            count = seedlibrary.seeds_available + "";
        }
        drawCenteredString(count, 108, 88, 0x404040);
        drawCenteredString("Seeds", 108, 97, 0x404040);

        drawCenteredString("Missing info", left + main_width / 4, middle + 2, 0x404040);
        drawCenteredString("Type", left + main_width/8, middle + 11, 0x404040);
        drawCenteredString("GGR", left + (main_width*3)/8, middle + 11, 0x404040);

        if (filter.unknown_type == 0)
		{
            unk_type_button.setTextureY(72 + 1);
            unk_type_button.tooltip = "Seeds with unknown type " + RED + "excluded";
        }
		else if (filter.unknown_type == 1)
		{
            unk_type_button.setTextureY(90 + 1);
            unk_type_button.tooltip = "Seeds with unknown type included";
        }
		else
		{
            unk_type_button.setTextureY(108 + 1);
            unk_type_button.tooltip = "Seeds with unknown type " + GREEN + "only";
        }

        if (filter.unknown_ggr == 0)
		{
            unk_ggr_button.setTextureY(72 + 1);
            unk_ggr_button.tooltip = "Seeds with unknown GGR " + RED + "excluded";
        }
		else if (filter.unknown_ggr == 1)
		{
            unk_ggr_button.setTextureY(90 + 1);
            unk_ggr_button.tooltip = "Seeds with unknown GGR included";
        }
		else
		{
            unk_ggr_button.setTextureY(108 + 1);
            unk_ggr_button.tooltip = "Seeds with unknown GGR " + GREEN + "only";
        }

        if (!seedlibrary.hasEnergy())
        {
            drawRect(left, top, right, bottom + 20, 0xff000000);
            drawCenteredString("Out of power.", center, middle - 3, 0x404040);
            drawCenteredString("Connect to LV power", center, middle + 6, 0x404040);
            drawCenteredString("or insert a battery.", center, middle + 15, 0x404040);

            // Re-bind the GUI's texture, because something else took over.
            mc.renderEngine.bindTexture(texture);

            drawTexturedModalRect(left + 3, bottom, 176, 18, 18, 18);
            fontRendererObj.drawString("Battery slot", left + 23, bottom + 5, 0x404040);

            buttonList = noControls;
        }
        else
        {
        	buttonList = realControls;
        }

        fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);

        if (lastMouseX != mouse_x || lastMouseY != mouse_y)
        {
            onMouseMoved(mouse_x, mouse_y);
        }

        if (tooltip != null && tooltip.length() > 0)
        {
            showTooltip(mouse_x, mouse_y, tooltip);
        }

        super.drawGuiContainerForegroundLayer(mouse_x, mouse_y);
    }

    public String getTooltip(int x, int y)
    {
        int slider;
        if (current_slider != -1)
        {
            slider = current_slider;
        }
        else
        {
            slider = getSliderAt(x, y);
        }

        if (slider != -1)
        {
            int value = getSliderValue(slider);
            if (slider > 5)
            {
                value *= 3;
            }

            return getSliderName(slider) + WHITE + ": " + value;
        }

        TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return null;
        }

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int f = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        for (int dir = 0; dir < 6; dir++)
        {
            TooltipButton button = directionButtons[dir];
            if (button.getActiveTooltip(x, y) == null)
            {
                continue;
            }
            String base_dir = null;
            String left   = "To your left";
            String right  = "To your right";
            String ahead  = "Ahead of you";
            String behind = "Behind you";
            if (dir == 0)
            {
                // Down = -Y = 0
                return "Down: Below you";
            }
            else if (dir == 1)
            {
                // Up = +Y = 1
                return "Up: Above you";
            }
            else if (dir == 2)
            {
                // North = -Z = 2
                base_dir = "North: ";
                if (f == 2)
                {
                    // f: 2 = North
                    return base_dir + ahead;
                }
                else if (f == 3)
                {
                    // f: 3 = East
                    return base_dir + left;
                }
                else if (f == 0)
                {
                    // f: 0 = South
                    return base_dir + behind;
                }
                else if (f == 1)
                {
                    // f: 1 = West
                    return base_dir + right;
                }
            }
            else if (dir == 5)
            {
                // East = +X = 5
                base_dir = "East: ";
                if (f == 2)
                {
                    // f: 2 = North
                    return base_dir + right;
                }
                else if (f == 3)
                {
                    // f: 3 = East
                    return base_dir + ahead;
                }
                else if (f == 0)
                {
                    // f: 0 = South
                    return base_dir + left;
                }
                else if (f == 1)
                {
                    // f: 1 = West
                    return base_dir + behind;
                }
            }
            else if (dir == 3)
            {
                // South = +Z = 3
                base_dir = "South: ";
                if (f == 2)
                {
                    // f: 2 = North
                    return base_dir + behind;
                }
                else if (f == 3)
                {
                    // f: 3 = East
                    return base_dir + right;
                }
                else if (f == 0)
                {
                    // f: 0 = South
                    return base_dir + ahead;
                }
                else if (f == 1)
                {
                    // f: 1 = West
                    return base_dir + left;
                }
            }
            else if (dir == 4)
            {
                // West = -X = 4
                base_dir = "West: ";
                if (f == 2)
                {
                    // f: 2 = North
                    return base_dir + left;
                }
                else if (f == 3)
                {
                    // f: 3 = East
                    return base_dir + behind;
                }
                else if (f == 0)
                {
                    // f: 0 = South
                    return base_dir + right;
                }
                else if (f == 1)
                {
                    // f: 1 = West
                    return base_dir + ahead;
                }
            }
        }

        for (Object control : buttonList)
        {
            if (control instanceof IHasTooltip)
            {
                String tooltip = ((IHasTooltip)control).getActiveTooltip(x, y);
                if (tooltip != null)
                {
                    return tooltip;
                }
            }
        }

        return null;
    }

    public void showTooltip(int x, int y, String contents)
    {
        drawCreativeTabHoveringText(contents, x - guiLeft, y - guiTop);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
    	TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return;
        }

        // Bind the GUI's texture.
        mc.renderEngine.bindTexture(texture);

        // Ensure the color is standard.
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Transfer the coordinate space to within the GUI screen.
        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0.0F);

        // Draw the background.
        drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);

        /*
        // Draw the borders for the three upper sections.
        draw3DRect(left, top, center, middle);
        draw3DRect(left, middle, center, bottom);
        draw3DRect(center, top, right, bottom);
        */

        // Draw the dashed outline for getting seed types.
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(left + (main_width/4) - 9, top + 11, 176, 54, 18, 18);

        // Draw the faded seed bag in the dashed outline.
        drawTexturedModalRect(left + (main_width/4) - 9, top + 11, 194, 0, 18, 18);

        // Draw the sliders and arrows.
        SeedLibraryFilter filter = seedlibrary.getGUIFilter();
        drawSlider(0, filter.min_growth, filter.max_growth);
        drawSlider(1, filter.min_gain, filter.max_gain);
        drawSlider(2, filter.min_resistance, filter.max_resistance);
        drawSlider(3, filter.min_total / 3, filter.max_total / 3);

        // Restore previous coordinates.
        GL11.glPopMatrix();
    }

    public void drawSlider(int index, int min, int max)
    {
        int pre_size = min * 2;
        int in_size = (max - min) * 2 + 1;
        int post_size = (31 - max) * 2;

        int x = sliders_x;
        int y = sliders_y + 1 + sliders_spacing*index;

        // Black before.
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        drawTexturedModalRect(x, y, 0, 222, pre_size, 7);

        // Green during.
        GL11.glColor4f(0.0F, 0.5F, 0.0F, 1.0F);
        drawTexturedModalRect(x + pre_size, y, pre_size, 222, in_size, 7);

        // Black after.
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        drawTexturedModalRect(x + pre_size + in_size, y, pre_size + in_size, 222, post_size, 7);

        // Green arrows.
        GL11.glColor4f(0.0F, 0.5F, 0.0F, 1.0F);
        drawTexturedModalRect(x + pre_size - 2, y-1, 176, 36, 3, 9);
        drawTexturedModalRect(x + pre_size + in_size - 1, y-1, 179, 36, 3, 9);
        
        // With slight smoothing.
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glColor4f(0.0F, 0.5F, 0.0F, 0.25F);
        drawTexturedModalRect(x + pre_size - 2, y-1, 182, 36, 3, 9);
        drawTexturedModalRect(x + pre_size + in_size - 1, y-1, 185, 36, 3, 9);
        GL11.glDisable(3042 /*GL_BLEND*/);

        // Return to standard colors
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return;
        }

        if (!seedlibrary.hasEnergy())
        {
            current_slider = -1;
            return;
        }

        if (button == 1)
        {
            // Pass the right click to the directional buttons.
            rightClick = true;
            for (int l = 0; l < buttonList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)buttonList.get(l);
                if (guibutton.id < 4 || guibutton.id > 9) {
                    continue;
                }
                if (guibutton.mousePressed(mc, x, y))
                {
                    rightSelect = guibutton;
                    //mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F); TODO
                    actionPerformed(guibutton);
                }
            }
            rightClick = false;
        }

        if (button == 0)
        {
            // LMB down.

            // Set current slider to what's under the mouse, so it can track.
            current_slider = getSliderAt(x, y);

            // And if there is one, keep track of the starting point as well.
            if (current_slider != -1)
            {
                drag_start_x = x;
                drag_start_value = getSliderValue(current_slider);
            }
        }
    }


    public int getSliderAt(int x, int y)
    {
        // Adjust for GUI coordinates.
        x -= guiLeft;
        y -= guiTop;

        if (x < (sliders_x - 2) || y < sliders_y) {
            // Above or left of the bars.
            return -1;
        }

        x -= sliders_x;
        y -= sliders_y;

        int bar = y / sliders_spacing;
        int remainder = y % sliders_spacing;
        if (bar > 3 || remainder >= 10) {
            // Below or between the bars.
            return -1;
        }

        int min = getSliderValue(bar*2);
        int max = getSliderValue(bar*2 + 1);

        if (x < min * 2 - 2) {
            // Left of both arrows.
            return -1;
        } else if (x <= min * 2) {
            // Over the minimum arrow.
            return bar * 2;
        } else if (x < max * 2) {
            // Between the arrows.
            return -1;
        } else if (x <= max * 2 + 2) {
            // Over the maximum arrow;
            return bar * 2 + 1;
        } else {
            // Right of both arrows.
            return -1;
        }
    }

    public String getSliderName(int slider)
    {
        String name;
        int bar = slider / 2;
        int arrow = slider % 2;

        if (arrow == 0) {
            name = "Minimum ";
        } else {
            name = "Maximum ";
        }

        if (bar == 0) {
            name += DARK_GREEN + "Growth";
        } else if (bar == 1) {
            name += GOLD + "Gain";
        } else if (bar == 2) {
            name += AQUA + "Resistance";
        } else { // bar == 3
            name += YELLOW + "Total";
        }

        return name;
    }

    public int getSliderValue(int slider)
    {
    	TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return 0;
        }

        SeedLibraryFilter filter = seedlibrary.getGUIFilter();
        int bar = slider / 2;
        int arrow = slider % 2;
        if (bar == 0)
        {
            if (arrow == 0)
            {
                return filter.min_growth;
            }
            else
            {
                return filter.max_growth;
            }
        }
        else if (bar == 1)
        {
            if (arrow == 0)
            {
                return filter.min_gain;
            }
            else
            {
                return filter.max_gain;
            }
        }
        else if (bar == 2)
        {
            if (arrow == 0)
            {
                return filter.min_resistance;
            }
            else
            {
                return filter.max_resistance;
            }
        }
        else
        { // if (bar == 3) 
            if (arrow == 0)
            {
                return filter.min_total / 3;
            }
            else
            {
                return filter.max_total / 3;
            }
        }
    }


    public void setSliderValue(int slider, int value)
    {
    	TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return;
        }

        seedlibrary.sendGuiSlider(slider, value);
    }


    @Override
    protected void mouseMovedOrUp(int x, int y, int button)
    {
        super.mouseMovedOrUp(x, y, button);

        TileEntitySeedLibrary seedlibrary = getLibrary();
        if (seedlibrary == null)
        {
            return;
        }

        if (rightSelect != null && button == 1)
        {
            // Release a button pressed with RMB.
            rightSelect.mouseReleased(x, y);
            rightSelect = null;
        }

        if (!seedlibrary.hasEnergy())
        {
            current_slider = -1;
            return;
        }

        if (button == 0)
        {
            // LMB up.
            // Stop tracking the mouse with a slider.
            if (current_slider != -1)
            {
                current_slider = -1;
            }
        }
    }

    public void onMouseMoved(int new_x, int new_y)
    {
        // Update the tooltip.
        tooltip = getTooltip(new_x, new_y);

        // If we're following the mouse with a slider, move it.
        if (current_slider != -1)
        {
            int value = drag_start_value + (new_x - drag_start_x) / 2;
            if (value < 0) 
            {
                value = 0;
            }
            else if (value > 31)
            {
                value = 31;
            }

            int bar = (current_slider / 2);
            int min = getSliderValue(bar * 2);
            int max = getSliderValue(bar * 2 + 1);
            boolean is_max = (current_slider % 2) == 1;
            if (is_max && min > value) 
            {
                value = min;
            }
            else if (!is_max && max < value)
            {
                value = max;
            }

            if (getSliderValue(current_slider) != value) 
            {
                setSliderValue(current_slider, value);
            }
        }
    }
}
