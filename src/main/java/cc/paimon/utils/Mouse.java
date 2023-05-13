package cc.paimon.utils;

import net.minecraft.client.gui.ScaledResolution;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc2;

public class Mouse {
    public static Vector2d getMouse() {
        final ScaledResolution scaledResolution = new ScaledResolution(mc2);
        final int mouseX = org.lwjgl.input.Mouse.getX() * scaledResolution.getScaledWidth() / mc2.displayWidth;
        final int mouseY = scaledResolution.getScaledHeight() - org.lwjgl.input.Mouse.getY() * scaledResolution.getScaledHeight() / mc2.displayHeight - 1;

        return new Vector2d(mouseX, mouseY);
    }
}
