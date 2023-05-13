package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import cc.paimon.utils.ClickEffect;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.miku.animations.Animation;
import net.ccbluex.liquidbounce.utils.render.miku.animations.Direction;
import net.ccbluex.liquidbounce.utils.render.miku.animations.impl.DecelerateAnimation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

@Mixin(GuiChat.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiChat extends MixinGuiScreen {
    @Shadow
    protected GuiTextField inputField;

    private boolean buttonAction;
    private Element selectedElement;
    private final java.util.List<ClickEffect> clickEffects = new ArrayList<>();

    private float yPosOfInputField;
    private float fade = 0;
    private static final Animation openingAnimation = new DecelerateAnimation(175, 1, Direction.BACKWARDS);

    @Shadow
    public abstract void setCompletions(String... p_setCompletions_1_);

    @Inject(method = "initGui", at = @At("RETURN"))
    private void init(CallbackInfo callbackInfo) {

        inputField.y = height + 1;
        yPosOfInputField = inputField.y;
    }

    @Inject(method = "keyTyped", at = @At("RETURN"))
    private void updateLength(CallbackInfo callbackInfo) {
        if (!inputField.getText().startsWith(String.valueOf(LiquidBounce.commandManager.getPrefix()))) return;
        LiquidBounce.commandManager.autoComplete(inputField.getText());

        if (!inputField.getText().startsWith(LiquidBounce.commandManager.getPrefix() + "lc"))
            inputField.setMaxStringLength(10000);
        else
            inputField.setMaxStringLength(100);
    }

    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void updateScreen(CallbackInfo callbackInfo) {
        final int delta = RenderUtils.deltaTime;

        if (fade < 14) fade += 0.4F * delta;
        if (fade > 14) fade = 14;

        if (yPosOfInputField > height - 12) yPosOfInputField -= 0.4F * delta;
        if (yPosOfInputField < height - 12) yPosOfInputField = height - 12;

        inputField.y = (int) yPosOfInputField;
    }


    /**
     * @author CCBlueX
     * @reason 1
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        LiquidBounce.hud.render(true, mouseX, mouseY);
        LiquidBounce.hud.handleMouseMove(mouseX, mouseY);

        if (!LiquidBounce.hud.getElements().contains(selectedElement))
            selectedElement = null;
        GlStateManager.resetColor();
        //SB
        Gui.drawRect(2, this.height - (int) fade, this.width - 2, this.height, Integer.MIN_VALUE);
        this.inputField.drawTextBox();

        if (LiquidBounce.commandManager.getLatestAutoComplete().length > 0 && !inputField.getText().isEmpty() && inputField.getText().startsWith(String.valueOf(LiquidBounce.commandManager.getPrefix()))) {
            String[] latestAutoComplete = LiquidBounce.commandManager.getLatestAutoComplete();
            String[] textArray = inputField.getText().split(" ");
            String trimmedString = latestAutoComplete[0].replaceFirst("(?i)" + textArray[textArray.length - 1], "");

            FontLoaders.F18.drawStringWithShadow(trimmedString, inputField.x + FontLoaders.F18.getStringWidth(inputField.getText()), inputField.y, new Color(165, 165, 165).getRGB());
        }

        ITextComponent ichatcomponent =
                this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null)
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        if (clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator = clickEffects.iterator();
            while (clickEffectIterator.hasNext()) {
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }
    }


    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    public void onGuiClosed(CallbackInfo ci) {
        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.hudConfig);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {

        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        if (buttonAction) {
            buttonAction = false;
            return;
        }

        LiquidBounce.hud.handleMouseClick(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            for (Element element : LiquidBounce.INSTANCE.getHud().getElements()) {
                if (element.isInBorder((double) ((float) mouseX / element.getScale()) - element.getRenderX(), (double) ((float) mouseY / element.getScale()) - element.getRenderY())) {
                    this.selectedElement = element;
                    break;
                }
            }
        }
    }
}
