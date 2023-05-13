package cc.paimon.ui.client;

import cc.paimon.utils.Animation;
import cc.paimon.utils.ClickEffect;
import cc.paimon.utils.EaseBackIn;
import cc.paimon.utils.MouseUtils;
import net.ccbluex.liquidbounce.injection.backend.GuiScreenImplKt;
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.RoundedUtil;
import net.ccbluex.liquidbounce.utils.render.miku.animations.Direction;
import net.ccbluex.liquidbounce.utils.render.tenacity.ColorUtil;
import net.ccbluex.liquidbounce.utils.render.tenacity.GradientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.classProvider;

public class GuiSoarMainMenu extends GuiScreen {
	private ArrayList<SoarMainMenuButton> menus = new ArrayList<SoarMainMenuButton>();
	private Animation introAnimation;
	private List<ClickEffect> clickEffects = new ArrayList<>();
	private boolean closeIntro;
//	private HudMod hudmode = new HudMod();

	public GuiSoarMainMenu() {
		menus.add(new SoarMainMenuButton("Singleplayer"));
		menus.add(new SoarMainMenuButton("Multiplayer"));
		menus.add(new SoarMainMenuButton("Account Manager"));
		menus.add(new SoarMainMenuButton("Options"));
		menus.add(new SoarMainMenuButton("Quit"));
	}

	@Override
	public void initGui() {
		mc.displayGuiScreen(new MainMenu());

		introAnimation = new EaseBackIn(450, 1, 1.5F);
	}

	private Color mixColors(Color color1, Color color2) {
//		if (movingColors.isEnabled()) {
			return ColorUtil.interpolateColorsBackAndForth(15, 1, color1, color2,true);
//		} else {
//			return ColorUtil.interpolateColorC(color1, color2, 0);
//		}
	}
	public Color getClientColor(){
		return new Color(166, 107, 102);
	}
	public Color getAlternateClientColor(){
		return new Color(95, 132, 213);
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		Color bg3Color = ColorUtil.getBackgroundColor(3);
		Color font2Color = ColorUtil.getFontColor(2);
		if(closeIntro) {
			introAnimation.setDirection(Direction.BACKWARDS);
			if(introAnimation.isDone(Direction.BACKWARDS)) {
				closeIntro = false;
			}
		}
		int addX = 65;
		int addY = 85;
		int offsetY = -45;

		//background
		Color gradientColor1 = ColorUtil.interpolateColorsBackAndForth(15, 1,getClientColor(),getAlternateClientColor(), false);
		Color gradientColor2 = ColorUtil.interpolateColorsBackAndForth(15, 1,getAlternateClientColor(),getClientColor(), false);
		GradientUtil.drawGradient(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 1, gradientColor1, gradientColor1, gradientColor2, gradientColor2);

		RenderUtils.startScale(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2, (float) introAnimation.getValue());

		RoundedUtil.drawRound(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY, addX * 2, addY * 2, 6, true, bg3Color);

		Color firstColor;
		Color secondColor;
		firstColor = mixColors(getClientColor(), getAlternateClientColor());
		secondColor = mixColors(getAlternateClientColor(), getClientColor());
		Color[] clientColors = new  Color[]{firstColor, secondColor};
		GradientUtil.applyGradientHorizontal((float) ((sr.getScaledWidth() / 2 - addX) -Fonts.posterama72.getStringWidth("Skyrim Main") + 115 + 5), sr.getScaledHeight() / 2 - 80, (float)Fonts.posterama72.getStringWidth("Skyrim Main"), 20, 1, clientColors[0], clientColors[1], () -> {
			RenderUtils.setAlphaLimit(0);
			Fonts.posterama72.drawStringWithShadow("Skyrim Main", (sr.getScaledWidth() / 2 - addX) -Fonts.posterama72.getStringWidth("Skyrim Main") + 115 + 5, sr.getScaledHeight() / 2 - 80, new Color(0,0,0,0).getRGB());
		});

		for(SoarMainMenuButton b : menus) {

			boolean isInside = cc.paimon.utils.MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20);

			b.opacityAnimation.setAnimation(isInside ? 255 : 0, 10);

			RoundedUtil.drawRound(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20, 6, getSelectButtonColor(((int) b.opacityAnimation.getValue())));
			Fonts.posterama40.drawCenteredString(b.getName(), sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + offsetY, font2Color.getRGB());

			offsetY+=26;
		}
		RenderUtils.stopScale();
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(clickEffects.size() > 0) {
			Iterator<ClickEffect> clickEffectIterator= clickEffects.iterator();
			while(clickEffectIterator.hasNext()){
				ClickEffect clickEffect = clickEffectIterator.next();
				clickEffect.draw();
				if (clickEffect.canRemove()) clickEffectIterator.remove();
			}
		}
	}
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
		clickEffects.add(clickEffect);
		if(mouseButton == 0) {
			int addX = 65;
			int addY = 85;
			int offsetY = -45;

			for(SoarMainMenuButton b : menus) {
				if(MouseUtils.isInside(mouseX, mouseY, sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + offsetY + 78, addX * 2, 20)){
					switch(b.getName()) {
						case "Singleplayer":
							mc.displayGuiScreen(new GuiWorldSelection(this));
							break;
						case "Multiplayer":
							mc.displayGuiScreen(new GuiMultiplayer(this));
							break;
						case "Account Manager":
							mc.displayGuiScreen(GuiScreenImplKt.unwrap(classProvider.wrapGuiScreen(new GuiAltManager())));
							break;
						case "Options":
							mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
							break;
						case "Quit":
							mc.shutdown();
							break;
					}
				}
				offsetY+=26;
			}
		}
	}
	private Color getSelectButtonColor(int opacity) {
		return ColorUtil.getBackgroundColor(4,  opacity);
	}
}