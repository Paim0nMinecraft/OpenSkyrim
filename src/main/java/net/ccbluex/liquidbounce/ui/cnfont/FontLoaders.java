package net.ccbluex.liquidbounce.ui.cnfont;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public abstract class FontLoaders {
    public static FontDrawer F14;
    public static FontDrawer F18;
    public static FontDrawer F16;
    public static FontDrawer F64;

    public static FontDrawer TITLE18;
    public static FontDrawer TITLE16;
    public static FontDrawer TITLE20;
    public static FontDrawer TITLE25;


    public static void initFonts() {
        F18 = getFont("misans", 18, true);
        F16 = getFont("misans", 16, true);
        F14 = getFont("misans", 14, true);
        F64 = getFont("misans", 64, true);

        TITLE18 = getFont("title", 18, true);
        TITLE16 = getFont("logo", 16, true);
        TITLE20 = getFont("logo", 20, true);
        TITLE25 = getFont("logo", 23, true);

    }

    public static FontDrawer getFont(String name, int size, boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidwing/font/" + name + ".ttf")).getInputStream()).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }
        return new FontDrawer(font, antiAliasing);
    }
}
