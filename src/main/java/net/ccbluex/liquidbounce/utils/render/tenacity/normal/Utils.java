package net.ccbluex.liquidbounce.utils.render.tenacity.normal;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.IMinecraft;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;

public interface Utils {
    IMinecraft mc = LiquidBounce.INSTANCE.getWrapper().getMinecraft();
    IFontRenderer fr = mc.getFontRendererObj();
}
