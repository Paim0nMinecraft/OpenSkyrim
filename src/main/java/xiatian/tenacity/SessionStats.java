package xiatian.tenacity;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.RoundedUtil;

import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import xiatian.novoline.font.Fonts;


import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "SessionStats", description = "", category = ModuleCategory.RENDER)
public class SessionStats extends Module {
    private final List<String> linesLeft = Arrays.asList("Games Played: " + LiquidBounce.combatManager.getKills() , "K/D: 0.0", "Kills: " + LiquidBounce.combatManager.getKills());

    public IntegerValue dragx = new IntegerValue("X",0,-500,500);
    public IntegerValue dragy = new IntegerValue("Y",0,-500,500);

    public final ListValue colorMode = new ListValue("Color",new String[]{ "Sync", "Analogous", "Tenacity", "Gradient", "Modern"}, "Tenacity");
    public final ListValue degree = new ListValue("Degree", new String[]{"30", "-30"}, "-30");

    private Color gradientColor1 = Color.WHITE, gradientColor2 = Color.WHITE, gradientColor3 = Color.WHITE, gradientColor4 = Color.WHITE;


    @EventTarget
    public void Render2d(Render2DEvent e){
        final HUD hudMod = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        float x = this.dragx.get(), y = this.dragy.get();
        float height = linesLeft.size() * (xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.getHeight() + 6) + 24 + 5;
        float width = 155;
        switch (colorMode.get()) {
            case "Sync":
                Color[] colors =   new Color[]{ClickGUI.generateColor(), ClickGUI.generateColor()};;
                gradientColor1 = RenderUtils.interpolateColorsBackAndForth(15, 0, colors[0], colors[1], hudMod.getHueInterpolation().get());
                gradientColor2 = RenderUtils.interpolateColorsBackAndForth(15, 90, colors[0], colors[1], hudMod.getHueInterpolation().get());
                gradientColor3 = RenderUtils.interpolateColorsBackAndForth(15, 180, colors[0], colors[1], hudMod.getHueInterpolation().get());
                gradientColor4 = RenderUtils.interpolateColorsBackAndForth(15, 270, colors[0], colors[1], hudMod.getHueInterpolation().get());
                break;
            case "Tenacity":
                gradientColor1 = RenderUtils.interpolateColorsBackAndForth(15, 0,new Color(hudMod.getR().get(),hudMod.getG().get(),hudMod.getB().get()),new Color(hudMod.getR2().get(),hudMod.getG2().get(),hudMod.getB2().get()), hudMod.getHueInterpolation().get());
                gradientColor2 = RenderUtils.interpolateColorsBackAndForth(15, 90,new Color(hudMod.getR().get(),hudMod.getG().get(),hudMod.getB().get()),new Color(hudMod.getR2().get(),hudMod.getG2().get(),hudMod.getB2().get()), hudMod.getHueInterpolation().get());
                gradientColor3 = RenderUtils.interpolateColorsBackAndForth(15, 180,new Color(hudMod.getR().get(),hudMod.getG().get(),hudMod.getB().get()),new Color(hudMod.getR2().get(),hudMod.getG2().get(),hudMod.getB2().get()), hudMod.getHueInterpolation().get());
                gradientColor4 = RenderUtils.interpolateColorsBackAndForth(15, 270,new Color(hudMod.getR().get(),hudMod.getG().get(),hudMod.getB().get()),new Color(hudMod.getR2().get(),hudMod.getG2().get(),hudMod.getB2().get()), hudMod.getHueInterpolation().get());
                break;
            case "Gradient":
                gradientColor1 = RenderUtils.interpolateColorsBackAndForth(15, 0,ClickGUI.generateColor(), ClickGUI.generateColor(), hudMod.getHueInterpolation().get());
                gradientColor2 = RenderUtils.interpolateColorsBackAndForth(15, 90,ClickGUI.generateColor(), ClickGUI.generateColor(), hudMod.getHueInterpolation().get());
                gradientColor3 = RenderUtils.interpolateColorsBackAndForth(15, 180,ClickGUI.generateColor(), ClickGUI.generateColor(), hudMod.getHueInterpolation().get());
                gradientColor4 = RenderUtils.interpolateColorsBackAndForth(15, 270,ClickGUI.generateColor(), ClickGUI.generateColor(), hudMod.getHueInterpolation().get());
                break;
            case "Analogous":
                int val = degree.get() == "30" ? 0 : 1;
                Color analogous = RenderUtils.getAnalogousColor(ClickGUI.generateColor())[val];
                gradientColor1 = RenderUtils.interpolateColorsBackAndForth(15, 0,ClickGUI.generateColor(), analogous, hudMod.getHueInterpolation().get());
                gradientColor2 = RenderUtils.interpolateColorsBackAndForth(15, 90,ClickGUI.generateColor(), analogous, hudMod.getHueInterpolation().get());
                gradientColor3 = RenderUtils.interpolateColorsBackAndForth(15, 180,ClickGUI.generateColor(), analogous, hudMod.getHueInterpolation().get());
                gradientColor4 = RenderUtils.interpolateColorsBackAndForth(15, 270,ClickGUI.generateColor(), analogous, hudMod.getHueInterpolation().get());
                break;
            case "Modern":
                RoundedUtil.drawRoundOutline(x, y, width, height, 6, .5f, new Color(10, 10, 10, 80), new Color(-2));
                break;
        }
        boolean outlinedRadar = !(colorMode.get() == "Modern");
        DrRenderUtils.setAlphaLimit(0);
        if (outlinedRadar) {
            RoundedUtil.drawGradientRound(x, y, width, height, 6, DrRenderUtils.applyOpacity(gradientColor4, .85f), gradientColor1, gradientColor3, gradientColor2);
            //DrRenderUtils.drawGradientRect2(x - 1, y + 15, width + 2, 5, DrRenderUtils.applyOpacity(Color.BLACK, .2f).getRGB(), DrRenderUtils.applyOpacity(Color.BLACK, 0).getRGB());
        }else {
            DrRenderUtils.drawGradientRect2(x +1, y + 15, width - 2, 5, DrRenderUtils.applyOpacity(Color.BLACK, .2f).getRGB(), DrRenderUtils.applyOpacity(Color.BLACK, 0).getRGB());
        }
        //标题
        Fonts.tenacity.tenacity22.tenacity22.drawString("Statistics", x + 5, y + (colorMode.get() == "Modern" ? 3 : 2), -1);
        RoundedUtil.drawRound(x + 5,y + (colorMode.get() == "Modern" ? 3 : 2 + Fonts.tenacity.tenacity22.tenacity22.getHeight() + 2),Fonts.tenacity.tenacity22.tenacity22.stringWidth("Statistics"),1,0F,Color.WHITE);
        //信息
        for (int i = 0; i < linesLeft.size(); i++) {
            int offset = i * (xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.getHeight() + 6);
            xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.drawString(linesLeft.get(i), x + 5, (float) (y + offset + (i == 0 ? 23.5 : 25)), -1);
        }
        Fonts.tenacity.tenacity22.tenacity22.drawString("Play Time", x + 150 - 5 -Fonts.tenacity.tenacity22.tenacity22.stringWidth("Play Time"), y + (colorMode.get() == "Modern" ? 3 : 2) + 1, -1);
        RoundedUtil.drawRound(x + 150 - 5 -Fonts.tenacity.tenacity22.tenacity22.stringWidth("Play Time"),y + (colorMode.get() == "Modern" ? 3 : 2 + Fonts.tenacity.tenacity22.tenacity22.getHeight() + 3),Fonts.tenacity.tenacity22.tenacity22.stringWidth("Play Time"),1,0F,Color.WHITE);
        xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.drawString("00",x + 150 - 6F - 22F-xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.stringWidth("00") / 2,y + (colorMode.get() == "Modern" ? 3 : 2 + Fonts.tenacity.tenacity22.tenacity22.getHeight() + 3 + 28 - xiatian.novoline.font.Fonts.tenacity.tenacity18.tenacity18.getHeight() / 2),-1);
        RenderUtils.drawArc(x + 150 - 6F - 22F,y + (colorMode.get() == "Modern" ? 3 : 2 + Fonts.tenacity.tenacity22.tenacity22.getHeight() + 3 + 28), 22.0, new Color(30,30,30,100).getRGB(), 0, 360.0, 5);
        // 自己填值 把360换成时间 再 * 360RenderUtils.drawArc(x + 150 - 6F - 22F,y + (colorMode.get() == "Modern" ? 3 : 2 + Fonts.tenacity.tenacity22.tenacity22.getHeight() + 3 + 28), 22.0, new Color(0,0,0,160).getRGB(), 0, 360.0, 5);
    }

}
