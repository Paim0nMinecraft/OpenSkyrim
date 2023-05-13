/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code was taken from UnlegitMC/FDPClient. Please credit them when using this code in your repository.
 */
package net.ccbluex.liquidbounce.ui.client.clickgui.style.styles;

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;
import net.ccbluex.liquidbounce.ui.client.clickgui.Panel;
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ButtonElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ModuleElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.Style;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BlackStyle extends Style {

    private boolean mouseDown;
    private boolean rightMouseDown;

    @Override
    public void drawPanel(int mouseX, int mouseY, Panel panel) {
        RenderUtils.drawBorderedRect((float) panel.getX(), (float) panel.getY() - 3, (float) panel.getX() + panel.getWidth(), (float) panel.getY() + 17, 3, new Color(20, 20, 20).getRGB(), new Color(20, 20, 20).getRGB());
        if (panel.getFade() > 0) {
            RenderUtils.drawBorderedRect((float) panel.getX(), (float) panel.getY() + 17, (float) panel.getX() + panel.getWidth(), panel.getY() + 19 + panel.getFade(), 3, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
            RenderUtils.drawBorderedRect((float) panel.getX(), panel.getY() + 17 + panel.getFade(), (float) panel.getX() + panel.getWidth(), panel.getY() + 19 + panel.getFade() + 5, 3, new Color(20, 20, 20).getRGB(), new Color(20, 20, 20).getRGB());
        }
        GlStateManager.resetColor();
        float textWidth = Fonts.posterama35.getStringWidth("§f" + StringUtils.stripControlCodes(panel.getName()));
        Fonts.posterama35.drawString(panel.getName(), (int) (panel.getX() - (textWidth - 100.0F) / 2F), panel.getY() + 7 - 3, Color.WHITE.getRGB());
    }

    @Override
    public void drawDescription(int mouseX, int mouseY, String text) {
        int textWidth = Fonts.posterama35.getStringWidth(text);

        RenderUtils.drawBorderedRect(mouseX + 9, mouseY, mouseX + textWidth + 14, mouseY + Fonts.posterama35.getFontHeight() + 3, 3F, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
        GlStateManager.resetColor();
        Fonts.posterama35.drawString(text, mouseX + 12, mouseY + (Fonts.posterama35.getFontHeight() / 2), Color.WHITE.getRGB());
    }

    @Override
    public void drawButtonElement(int mouseX, int mouseY, ButtonElement buttonElement) {
        GlStateManager.resetColor();

        Fonts.posterama35.drawString(buttonElement.getDisplayName(), buttonElement.getX() + 5, buttonElement.getY() + 5, Color.WHITE.getRGB());
    }

    @Override
    public void drawModuleElement(int mouseX, int mouseY, ModuleElement moduleElement) {
        Gui.drawRect(moduleElement.getX() - 1, moduleElement.getY() - 1, moduleElement.getX() + moduleElement.getWidth() + 1, moduleElement.getY() + moduleElement.getHeight() + 1, hoverColor(new Color(40, 40, 40), moduleElement.hoverTime).getRGB());
        Gui.drawRect(moduleElement.getX() - 1, moduleElement.getY() - 1, moduleElement.getX() + moduleElement.getWidth() + 1, moduleElement.getY() + moduleElement.getHeight() + 1, hoverColor(new Color(20, 20, 20, moduleElement.slowlyFade), moduleElement.hoverTime).getRGB());
        GlStateManager.resetColor();
        Fonts.posterama35.drawString(moduleElement.getDisplayName(), moduleElement.getX() + 5, moduleElement.getY() + 5, Color.WHITE.getRGB());

        // Draw settings
        final List<Value<?>> moduleValues = moduleElement.getModule().getValues();

        if (!moduleValues.isEmpty()) {
            Fonts.posterama35.drawString(">", moduleElement.getX() + moduleElement.getWidth() - 8, moduleElement.getY() + 5, Color.WHITE.getRGB());

            if (moduleElement.isShowSettings()) {
                if (moduleElement.getSettingsWidth() > 0F && moduleElement.slowlySettingsYPos > moduleElement.getY() + 6)
                    RenderUtils.drawBorderedRect(moduleElement.getX() + moduleElement.getWidth() + 4, moduleElement.getY() + 6, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), moduleElement.slowlySettingsYPos + 2, 3F, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());

                moduleElement.slowlySettingsYPos = moduleElement.getY() + 6;
                for (final Value value : moduleValues) {
                    boolean isNumber = value.get() instanceof Number;
                    if (isNumber) {
                        ////////AWTFontRenderer.Companion.setAssumeNonVolatile(false);
                    }
                    if (value instanceof BoolValue) {
                        final String text = value.getName();
                        final float textWidth = Fonts.posterama35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + 12 && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                            final BoolValue boolValue = (BoolValue) value;

                            boolValue.set(!boolValue.get());
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }

                        Fonts.posterama35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, ((BoolValue) value).get() ? Color.WHITE.getRGB() : Integer.MAX_VALUE);
                        moduleElement.slowlySettingsYPos += 11;
                    } else if (value instanceof ListValue) {
                        final ListValue listValue = (ListValue) value;

                        final String text = value.getName();
                        final float textWidth = Fonts.posterama35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 16)
                            moduleElement.setSettingsWidth(textWidth + 16);

                        Fonts.posterama35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, 0xffffff);
                        Fonts.posterama35.drawString(listValue.openList ? "-" : "+", (int) (moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - (listValue.openList ? 5 : 6)), moduleElement.slowlySettingsYPos + 2, 0xffffff);

                        if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + Fonts.posterama35.getStringWidth(text) && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                            listValue.openList = !listValue.openList;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }

                        moduleElement.slowlySettingsYPos += 12;

                        for (final String valueOfList : listValue.getValues()) {
                            final float textWidth2 = Fonts.posterama35.getStringWidth("> " + valueOfList);

                            if (moduleElement.getSettingsWidth() < textWidth2 + 12)
                                moduleElement.setSettingsWidth(textWidth2 + 12);

                            if (listValue.openList) {
                                if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos + 2 && mouseY <= moduleElement.slowlySettingsYPos + 14 && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                    listValue.set(valueOfList);
                                    mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                                }

                                GlStateManager.resetColor();
                                Fonts.posterama35.drawString("> " + valueOfList, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, listValue.get() != null && listValue.get().equalsIgnoreCase(valueOfList) ? Color.WHITE.getRGB() : Integer.MAX_VALUE);
                                moduleElement.slowlySettingsYPos += Fonts.posterama35.getStringWidth(text) + 1;
                            }
                        }

                        if (!listValue.openList) {
                            moduleElement.slowlySettingsYPos += 1;
                        }
                    } else if (value instanceof FloatValue) {
                        final FloatValue floatValue = (FloatValue) value;
                        String text = value.getName() + "§f: §c" + round(floatValue.get());
                        final float textWidth = Fonts.posterama35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        final float valueOfSlide = drawSlider(floatValue.get(), floatValue.getMinimum(), floatValue.getMaximum(), false, moduleElement.getX() + moduleElement.getWidth() + 8, moduleElement.slowlySettingsYPos + 14, (int) moduleElement.getSettingsWidth() - 12, mouseX, mouseY, new Color(20, 20, 20));

                        if (valueOfSlide != floatValue.get())
                            floatValue.set(valueOfSlide);

                        Fonts.posterama35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 3, 0xffffff);
                        moduleElement.slowlySettingsYPos += 19;
                    } else if (value instanceof IntegerValue) {
                        final IntegerValue integerValue = (IntegerValue) value;
                        String text = value.getName() + "§f: §c" + round(integerValue.get());
                        final float textWidth = Fonts.posterama35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        final float valueOfSlide = drawSlider(integerValue.get(), integerValue.getMinimum(), integerValue.getMaximum(), true, moduleElement.getX() + moduleElement.getWidth() + 8, moduleElement.slowlySettingsYPos + 14, (int) moduleElement.getSettingsWidth() - 12, mouseX, mouseY, new Color(20, 20, 20));

                        if (valueOfSlide != integerValue.get())
                            integerValue.set((int) valueOfSlide);

                        Fonts.posterama35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 3, 0xffffff);
                        moduleElement.slowlySettingsYPos += 19;
                    } else if (value instanceof FontValue) {
                        final FontValue fontValue = (FontValue) value;
                        final IFontRenderer fontRenderer = fontValue.get();

                        String displayString = "Font: Unknown";

                        if (fontRenderer.isGameFontRenderer()) {
                            final GameFontRenderer liquidFontRenderer = fontRenderer.getGameFontRenderer();

                            displayString = "Font: " + liquidFontRenderer.getDefaultFont().getFont().getName() + " - " + liquidFontRenderer.getDefaultFont().getFont().getSize();
                        } else if (fontRenderer == Fonts.minecraftFont)
                            displayString = "Font: Minecraft";
                        else {
                            final Fonts.FontInfo objects = Fonts.getFontDetails(fontRenderer);

                            if (objects != null) {
                                displayString = objects.getName() + (objects.getFontSize() != -1 ? " - " + objects.getFontSize() : "");
                            }
                        }

                        Fonts.posterama35.drawString(displayString, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, Color.WHITE.getRGB());
                        int stringWidth = Fonts.posterama35.getStringWidth(displayString);

                        if (moduleElement.getSettingsWidth() < stringWidth + 8)
                            moduleElement.setSettingsWidth(stringWidth + 8);

                        if ((Mouse.isButtonDown(0) && !mouseDown || Mouse.isButtonDown(1) && !rightMouseDown) && mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + 12) {
                            final List<IFontRenderer> fonts = Fonts.getFonts();

                            if (Mouse.isButtonDown(0)) {
                                for (int i = 0; i < fonts.size(); i++) {
                                    final IFontRenderer font = fonts.get(i);

                                    if (font.equals(fontRenderer)) {
                                        i++;

                                        if (i >= fonts.size())
                                            i = 0;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            } else {
                                for (int i = fonts.size() - 1; i >= 0; i--) {
                                    final IFontRenderer font = fonts.get(i);

                                    if (font.equals(fontRenderer)) {
                                        i--;

                                        if (i >= fonts.size())
                                            i = 0;

                                        if (i < 0)
                                            i = fonts.size() - 1;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            }
                        }

                        moduleElement.slowlySettingsYPos += 11;
                    } else {
                        final String text = value.getName() + "§f: " + value.get();
                        final float textWidth = Fonts.posterama35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GlStateManager.resetColor();
                        Fonts.posterama35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 4, 0xffffff);
                        moduleElement.slowlySettingsYPos += 12;
                    }
                }

                moduleElement.updatePressed();
                mouseDown = Mouse.isButtonDown(0);
                rightMouseDown = Mouse.isButtonDown(1);
            }
        }
    }

    /*public static boolean drawCheckbox(final boolean value, final int x, final int y, final int mouseX, final int mouseY, final Color color) {
        RenderUtils.drawRect(x, y, x + 20, y + 10, value ? Color.GREEN : Color.RED);
        RenderUtils.drawFilledCircle(x + (value ? 15 : 5),y + 5, 5, Color.WHITE);

        if(mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 10 && Mouse.isButtonDown(0))
            return !value;

        return value;
    }*/

    private BigDecimal round(final float v) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(v));
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal;
    }

    private Color hoverColor(final Color color, final int hover) {
        final int r = color.getRed() - (hover * 2);
        final int g = color.getGreen() - (hover * 2);
        final int b = color.getBlue() - (hover * 2);

        return new Color(Math.max(r, 0), Math.max(g, 0), Math.max(b, 0), color.getAlpha());
    }

    public static float drawSlider(final float value, final float min, final float max, final boolean inte, final int x, final int y, final int width, final int mouseX, final int mouseY, final Color color) {
        final float displayValue = Math.max(min, Math.min(value, max));

        final float sliderValue = (float) x + (float) width * (displayValue - min) / (max - min);

        RenderUtils.drawRect(x, y, x + width, y + 2, Integer.MAX_VALUE);
        RenderUtils.drawRect(x, y, sliderValue, y + 2, color);
        RenderUtils.drawFilledCircle((int) sliderValue, y + 1, 3, color);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 3) {
            int dWheel = Mouse.getDWheel();
            if (dWheel != 0) {
                if (dWheel > 0)
                    return Math.min(value + (inte ? 1F : 0.01F), max);
                if (dWheel < 0)
                    return Math.max(value - (inte ? 1F : 0.01F), min);
            }
        }

        return value;
    }

}
