/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.color;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;

import java.awt.*;

public class ColorMixer {
    public static Color getMixedColor(int index, int seconds) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        return RenderUtils.getGradientOffset(
                new Color(hud.getR().get(), hud.getG().get(), hud.getB().get()),
                new Color(hud.getR2().get(), hud.getG2().get(), hud.getB2().get()),
                10.0
        );
    }

}