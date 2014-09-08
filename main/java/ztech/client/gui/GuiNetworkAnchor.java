package ztech.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import ztech.Ztech;
import ztech.containers.ContainerNetworkAnchor;
import ztech.network.ChannelHandler;
import ztech.network.messages.PacketSendButtonClick;
import ztech.network.messages.PacketSendSparks;
import ztech.tileentities.TileEntityNetworkAnchor;

public class GuiNetworkAnchor extends GuiContainer
{
    private static ResourceLocation texture = new ResourceLocation(Ztech.MODID + ":textures/gui/guiNetworkAnchor.png");
    public TileEntityNetworkAnchor tile;

    public GuiNetworkAnchor(EntityPlayer player, TileEntityNetworkAnchor tile)
    {
        super(new ContainerNetworkAnchor(player, tile));
        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButtonFix(0, guiLeft + 92, guiTop + 17, 23, 18, "E"));
        buttonList.add(new GuiButtonFix(1, guiLeft + 119, guiTop + 17, 23, 18, "O"));

        buttonList.add(new GuiButtonFix(2, guiLeft + 92, guiTop + 39, 23, 18, "gui.networkAnchor.decArea"));
        buttonList.add(new GuiButtonFix(3, guiLeft + 119, guiTop + 39, 23, 18, "gui.networkAnchor.incArea"));

        buttonList.add(new GuiButtonFix(4, guiLeft + 92, guiTop + 61, 50, 18, "gui.networkAnchor.restart"));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        super.actionPerformed(button);
        ChannelHandler.network.sendToServer(new PacketSendButtonClick(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, button.id));
        tile.buttonHandler(button.id);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal(tile.getInventoryName()), 8, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.networkAnchor.scan"), 8, 16 + 1, 4210752);

        int d = 2 * tile.area + 1;
        fontRendererObj.drawString(StatCollector.translateToLocalFormatted("gui.networkAnchor.area", d, d), 8, 26 + 2, 4210752);

        fontRendererObj.drawString(StatCollector.translateToLocalFormatted("gui.networkAnchor.tiles", tile.tilesFound), 8, 36 + 3, 4210752);

        if (tile.ticketsUsed > 0 && tile.chunksForced != tile.chunksFound)
        {
        	fontRendererObj.drawString(StatCollector.translateToLocalFormatted("gui.networkAnchor.chunks2", tile.chunksForced, tile.chunksFound), 8, 46 + 4, 4210752);
        }
        else
        {
            fontRendererObj.drawString(StatCollector.translateToLocalFormatted("gui.networkAnchor.chunks1", tile.chunksFound), 8, 46 + 4, 4210752);
        }

        fontRendererObj.drawString(StatCollector.translateToLocalFormatted("gui.networkAnchor.tickets", tile.ticketsUsed), 8, 56 + 5, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);

        if (Ztech.config.clientRules.bEnergy && func_146978_c(146, 8, 4, 70, mouseX, mouseY))
        {
            GL11.glPushMatrix();
            drawCreativeTabHoveringText(StatCollector.translateToLocalFormatted("gui.networkAnchor.charge", (long) tile.energyStored, tile.capacity), mouseX - guiLeft, mouseY - guiTop);
            GL11.glPopMatrix();

            // *** This thing is crashing :( ***
            // List<String> list = new ArrayList<String>(1);
            // list.add(String.format("%d/%d EU", tile.energy, tile.energy));
            // drawHoveringText(list, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        mc.renderEngine.bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // Draw leds
        // e: 101,8
        // o: 128,8
        // red: 176,70
        // green: 176,75
        int f = tile.scan, x[] = {101, 128}, v[] = {70, 75};
        for (int i = 0; i < 2; i++)
        {
            drawTexturedModalRect(guiLeft + x[i], // x
                    guiTop + 8, // y
                    176, // u
                    v[f & 0x01], // v
                    5, // width
                    5); // height
            f >>= 1;
        }

        // Draw charge bar
        if (Ztech.config.clientRules.bEnergy)
        {
            int h = (int) Math.round(tile.energyStored * 70.0D / tile.capacity);
            if (h < 0) h = 0; // clamp undercharge
            if (h > 70) h = 70; // clamp overcharge
            drawTexturedModalRect(guiLeft + 146, // x
                    guiTop + 8 + 70 - h, // y
                    176, // u
                    70 - h, // v
                    4, // width
                    h); // height
        }


    }
}
