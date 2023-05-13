package net.ccbluex.liquidbounce.ui.font;

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fonts extends MinecraftInstance {

    @FontDetails(fontName = "Minecraft Font")
    public static final IFontRenderer minecraftFont = mc.getFontRendererObj();
    @FontDetails(fontName = "Notification Icon", fontSize = 80)
    public static IFontRenderer notificationIcon80;
    @FontDetails(fontName = "Tenacity", fontSize = 80)
    public static IFontRenderer tenacity80;

    @FontDetails(fontName = "Tenacity", fontSize = 50)
    public static IFontRenderer tenacity50;
    @FontDetails(fontName = "Bahnschrift", fontSize = 40)
    public static IFontRenderer title40;
    @FontDetails(fontName = "Bahnschrift", fontSize = 35)
    public static IFontRenderer title35;
    @FontDetails(fontName = "Tenacity", fontSize = 50)
    public static IFontRenderer title82;
    //    @FontDetails(fontName = "Tenacity", fontSize = 50)
//    public static IFontRenderer title;
    @FontDetails(fontName = "Tenacity", fontSize = 82)
    public static IFontRenderer tenacity81;
    @FontDetails(fontName = "Posterama", fontSize = 35)
    public static IFontRenderer posterama35;
    @FontDetails(fontName = "Posterama", fontSize = 40)
    public static IFontRenderer posterama40;
    @FontDetails(fontName = "Posterama", fontSize = 30)
    public static IFontRenderer posterama30;
    @FontDetails(fontName = "Posterama", fontSize = 25)
    public static IFontRenderer posterama25;
    @FontDetails(fontName = "Posterama", fontSize = 20)
    public static IFontRenderer posterama20;

    @FontDetails(fontName = "Tenacity", fontSize = 40)
    public static IFontRenderer tenacity40;

    @FontDetails(fontName = "Posterama", fontSize = 22)
    public static IFontRenderer fontTahomaSmall;
    @FontDetails(fontName = "Posterama", fontSize = 35)
    public static IFontRenderer fontTahoma;
    @FontDetails(fontName = "Posterama", fontSize = 72)
    public static IFontRenderer exhi72;
    @FontDetails(fontName = "Posterama", fontSize = 72)
    public static IFontRenderer posterama72;
    @FontDetails(fontName = "Posterama", fontSize = 96)
    public static IFontRenderer posterama96;

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");

        notificationIcon80 = getFont("notification-icon.ttf", 80);
        posterama35 = getFont("posterama.ttf", 35);
        posterama40 = getFont("posterama.ttf", 40);
        posterama30 = getFont("posterama.ttf", 30);
        posterama25 = getFont("posterama.ttf", 25);
        posterama20 = getFont("posterama.ttf", 20);
        posterama72 = getFont("posterama.ttf", 72);
        exhi72 = getFont("etb.ttf", 72);
        fontTahoma = getFont("tahoma.ttf", 35);
        fontTahomaSmall = getFont("tahoma.ttf", 22);
        posterama96 = getFont("posterama.ttf", 96);
        tenacity81 = getFont("tenacitybold.ttf", 82);
        tenacity50 = getFont("tenacitybold.ttf", 50);
        tenacity40 = getFont("tenacitybold.ttf", 40);
        title82 = getFont("logo.ttf", 50);
        title35 = getFont("title.ttf", 35);
        title40 = getFont("title.ttf", 40);


        tenacity80 = getFont("tenacitybold.ttf", 80);


        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    public static IFontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object o = field.get(null);

                if (o instanceof IFontRenderer) {
                    FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (IFontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return getFont("default", 35);
    }

    public static FontInfo getFontDetails(final IFontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new FontInfo(fontDetails.fontName(), fontDetails.fontSize());
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static List<IFontRenderer> getFonts() {
        final List<IFontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof IFontRenderer) fonts.add((IFontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return fonts;
    }

    private static IFontRenderer getFont(final String fontName, final int size) {
        Font font;
        try {
            final InputStream inputStream = minecraft.getResourceManager().getResource(new ResourceLocation("liquidwing/font/" + fontName)).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            font = awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, size);
        }

        return classProvider.wrapFontRenderer(new GameFontRenderer(font));
    }

    public static class FontInfo {
        private final String name;
        private final int fontSize;

        public FontInfo(String name, int fontSize) {
            this.name = name;
            this.fontSize = fontSize;
        }

        public FontInfo(Font font) {
            this(font.getName(), font.getSize());
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfo fontInfo = (FontInfo) o;

            if (fontSize != fontInfo.fontSize) return false;
            return Objects.equals(name, fontInfo.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + fontSize;
            return result;
        }
    }
}