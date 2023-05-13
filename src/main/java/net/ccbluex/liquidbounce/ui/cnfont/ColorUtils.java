package net.ccbluex.liquidbounce.ui.cnfont;

public class ColorUtils {
    public static final int RED = ColorUtils.getRGB(255, 0, 0);
    public static final int GREED = ColorUtils.getRGB(0, 255, 0);
    public static final int BLUE = ColorUtils.getRGB(0, 0, 255);
    public static final int WHITE = ColorUtils.getRGB(255, 255, 255);
    public static final int BLACK = ColorUtils.getRGB(0, 0, 0);
    public static final int NO_COLOR = ColorUtils.getRGB(0, 0, 0, 0);

    public static int getRGB(int r, int g, int b) {
        return ColorUtils.getRGB(r, g, b, 255);
    }

    public static int getRGB(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public static int[] splitRGB(int rgb) {
        return new int[]{rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF};
    }

    public static int getRGB(int rgb) {
        return 0xFF000000 | rgb;
    }

    public static int reAlpha(int rgb, int alpha) {
        return ColorUtils.getRGB(ColorUtils.getRed(rgb), ColorUtils.getGreen(rgb), ColorUtils.getBlue(rgb), alpha);
    }

    public static int getRed(int rgb) {
        return rgb >> 16 & 0xFF;
    }

    public static int getGreen(int rgb) {
        return rgb >> 8 & 0xFF;
    }

    public static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    public static int getAlpha(int rgb) {
        return rgb >> 24 & 0xFF;
    }
}
