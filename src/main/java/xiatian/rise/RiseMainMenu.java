package xiatian.rise;

import net.ccbluex.liquidbounce.utils.render.RoundedUtil;
import net.ccbluex.liquidbounce.utils.render.blur.BlurUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import xiatian.novoline.font.Fonts;

import java.awt.*;

public class RiseMainMenu extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        ScaledResolution sr = new  ScaledResolution(Minecraft.getMinecraft());
    }

}
