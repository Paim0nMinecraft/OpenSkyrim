package xiatian;

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotion;
import net.ccbluex.liquidbounce.api.minecraft.potion.IPotionEffect;
import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.HanaBiColors;
import net.ccbluex.liquidbounce.utils.PotionData;
import net.ccbluex.liquidbounce.utils.Translate;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
@ElementInfo(name = "NewEffects")
public class NewEffects extends Element {

    private final Map<IPotion, PotionData> potionMap = new HashMap<>();

    /**
     * Draw the entity.
     */
    @Override
    public Border drawElement() {
        GlStateManager.pushMatrix();
        int y = 0;
        for (final IPotionEffect potionEffect : mc.getThePlayer().getActivePotionEffects()) {
            final IPotion potion = functions.getPotionById(potionEffect.getPotionID());
            final String name = functions.formatI18n(potion.getName());
            final PotionData potionData;
            if(potionMap.containsKey(potion) && potionMap.get(potion).level == potionEffect.getAmplifier())
                potionData = potionMap.get(potion);
            else
                potionMap.put(potion, (potionData = new PotionData(potion, new Translate(0, - 40F + y), potionEffect.getAmplifier())));
            boolean flag = true;
            for(final IPotionEffect checkEffect : mc.getThePlayer().getActivePotionEffects())
                if (checkEffect.getAmplifier() == potionData.level) {
                    flag = false;
                    break;
                }
            if(flag) potionMap.remove(potion);
            int potionTime,potionMaxTime;
            try {
                potionTime = Integer.parseInt(potionEffect.getDurationString().split(":")[0]);
                potionMaxTime = Integer.parseInt(potionEffect.getDurationString().split(":")[1]);
            } catch(Exception ignored) {
                potionTime = 100;
                potionMaxTime = 1000;
            }
            final int lifeTime = (potionTime * 60 + potionMaxTime);
            if (potionData.getMaxTimer() == 0 || lifeTime > (double)potionData.getMaxTimer()) potionData.maxTimer = lifeTime;
            float state = 0.0F;
            if (lifeTime >= 0.0D) state = (float)(lifeTime / (double)((float)potionData.getMaxTimer()) * 100.0D);
            final int position = Math.round(potionData.translate.getY() + 5);
            state = Math.max(state, 2.0F);
            potionData.translate.interpolate(0, y, 0.1);
            potionData.animationX = (float) RenderUtils.getAnimationState2(potionData.getAnimationX(), 1.2F * state, Math.max(10.0F, Math.abs(potionData.animationX - 1.2F * state) * 15.0F) * 0.3F);
            RenderUtils.drawRectPotion(0, potionData.translate.getY(), 120, potionData.translate.getY() + 30F, ClientUtils.reAlpha(HanaBiColors.GREY.c, 0.1F));
            RenderUtils.drawRectPotion(0, potionData.translate.getY(), potionData.animationX, potionData.translate.getY() + 30F, ClientUtils.reAlpha((new Color(34, 24, 20)).brighter().getRGB(), 0.3F));
            RenderUtils.drawShadowWithCustomAlpha(0, Math.round(potionData.translate.getY()), 120, 30, 200);
            float posY = potionData.translate.getY() + 13F;
            Fonts.posterama35.drawString(name + " " + intToRomanByGreedy(potionEffect.getAmplifier() + 1), 29F, posY - mc.getFontRendererObj().getFontHeight(), ClientUtils.reAlpha(HanaBiColors.WHITE.c, 0.8F));
            Fonts.posterama35.drawString(potionEffect.getDurationString(), 29F, posY + 4.0F, ClientUtils.reAlpha((new Color(200, 200, 200)).getRGB(), 0.5F));
            if (potion.getHasStatusIcon()) {
                GlStateManager.pushMatrix();
                GL11.glDisable(2929);
                GL11.glEnable(3042);
                GL11.glDepthMask(false);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                int statusIconIndex = potion.getStatusIconIndex();
                mc.getTextureManager().bindTexture(classProvider.createResourceLocation("textures/gui/container/inventory.png"));
                mc2.ingameGUI.drawTexturedModalRect(6F, (float)(position + 40), statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glEnable(2929);
                GlStateManager.popMatrix();
            }
            y -= 35;
        }
        GlStateManager.popMatrix();
        return new Border(0F, 0F, 120F, 30F,0F);
    }

    private String intToRomanByGreedy(int num) {
        final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < values.length && num >= 0; i++)
            while (values[i] <= num){
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }

        return stringBuilder.toString();
    }
}
