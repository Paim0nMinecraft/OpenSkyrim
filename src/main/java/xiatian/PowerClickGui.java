package xiatian;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.ListValue;
import net.ccbluex.liquidbounce.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class PowerClickGui extends GuiScreen {
   public static boolean binding = false;
   public static Module currentMod = null;
   private final PowerButton handlerMid;
   private final PowerButton handlerRight;
   private final PowerButton handler;
   public int moveX;
   public int moveY;
   public int startX;
   public int startY;
   public int selectCategory;
   public Module bmod;
   public boolean dragging;
   public boolean drag;
   public boolean Mdrag;
   ArrayList<Module> mods;
   ScaledResolution res;
   Value<?> value;
   ScaledResolution sr;
   private float scrollY;
   private float modscrollY;

   public PowerClickGui() {
      this.mods = new ArrayList<>(LiquidBounce.moduleManager.getModules());
      this.handlerMid = new PowerButton(2);
      this.handlerRight = new PowerButton(1);
      this.handler = new PowerButton(0);
      this.res = new ScaledResolution(Minecraft.getMinecraft());
      this.moveX = 0;
      this.moveY = 0;
      this.startX = 50;
      this.startY = 40;
      this.selectCategory = 0;
      this.sr = new ScaledResolution(Minecraft.getMinecraft());
   }

   public static void erase(boolean stencil) {
      GL11.glStencilFunc(stencil ? 514 : 517, 1, '\uffff');
      GL11.glStencilOp(7680, 7680, 7681);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GL11.glAlphaFunc(516, 0.0F);
   }

   public static List getValueList(Module module) {

      return module.getValues();
   }

   public static List<Module> getModsInCategory(ModuleCategory cat) {
      ArrayList<Module> list = new ArrayList<>();

      for (Module m : LiquidBounce.moduleManager.getModules()) {
         if (m.getCategory() == cat) {
            list.add(m);
         }
      }

      return list;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (binding) {
         if (keyCode != 1 && keyCode != 211) {
            this.bmod.setKeyBind(keyCode);
         } else if (keyCode == 211) {
            this.bmod.setKeyBind(0);
         }

         binding = false;
      }

      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.dragging) {
         this.dragging = false;
      }

      if (this.drag) {
         this.drag = false;
      }
   }
   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.sr = new ScaledResolution(Minecraft.getMinecraft());
      if (this.isHovered((float) this.startX, (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 5), mouseX, mouseY) && !this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY) && this.handler.canExcecute()) {
         this.dragging = true;
      }

      if (this.dragging) {
         if (this.moveX == 0 && this.moveY == 0) {
            this.moveX = mouseX - this.startX;
            this.moveY = mouseY - this.startY;
         } else {
            this.startX = mouseX - this.moveX;
            this.startY = mouseY - this.moveY;
         }
      } else if (this.moveX != 0 || this.moveY != 0) {
         this.moveX = 0;
         this.moveY = 0;
      }

      if ((float) this.startX > (float) (this.sr.getScaledWidth() - 303)) {
         this.startX = this.sr.getScaledWidth() - 303;
      }

      if (this.startX < 3) {
         this.startX = 3;
      }

      if ((float) this.startY > (float) (this.sr.getScaledHeight() - 190)) {
         this.startY = this.sr.getScaledHeight() - 190;
      }

      if ((float) this.startY < 12.0F) {
         this.startY = 12;
      }

      GL11.glPushMatrix();
      erase(false);
      GL11.glEnable(3089);
      RenderUtils.doGlScissor1((float) this.startX, (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 185));
      int y = 0;
      ModuleCategory[] ModuleCategorys = ModuleCategory.values();
      int length = ModuleCategorys.length;

      for (int i = 0; i < length; ++i) {
         ModuleCategory moduleCategory = ModuleCategory.values()[i];
         String str = moduleCategory.name().replaceAll("Movement", "Move").replaceAll("MiniGames", "GAMES");
         FontLoaders.F18.drawCenteredString(str.charAt(0) + str.toLowerCase().substring(1, str.length()), (float) (this.startX + 36), (float) (this.startY + 18 + y), this.selectCategory == i ? (new Color(0, 170, 255)).getRGB() : (new Color(170, 170, 170)).getRGB());
         if (this.isHovered((float) (this.startX + 3), (float) (this.startY + 14 + y), (float) (this.startX + 50), (float) (this.startY + 32 + y), mouseX, mouseY) && this.handler.canExcecute()) {
            this.selectCategory = i;
         }

         y += 25;
      }

      int buttonX = this.startX + 64;
      int buttonY = this.startY + 12;

      int modulePos = this.startY + 8;


      for (int i = 0; i < getModsInCategory(ModuleCategory.values()[this.selectCategory]).size(); ++i) {
         Module mod = getModsInCategory(ModuleCategory.values()[this.selectCategory]).get(i);
         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 185), mouseX, mouseY) && getModsInCategory(ModuleCategory.values()[this.selectCategory]).size() > 11 && this.isHovered((float) buttonX, (float) (modulePos - 2), (float) (buttonX + 82), (float) (modulePos + 12), mouseX, mouseY)) {
            float wheel = (float) Mouse.getDWheel();
            this.modscrollY += wheel / 10.0F;
         }

         if (getModsInCategory(ModuleCategory.values()[this.selectCategory]).size() < 12) {
            this.modscrollY = 0.0F;
         }

         if ((double) this.modscrollY > 0.0D) {
            this.modscrollY = 0.0F;
         }

         if (getModsInCategory(ModuleCategory.values()[this.selectCategory]).size() > 11 && this.modscrollY < (float) ((getModsInCategory(ModuleCategory.values()[this.selectCategory]).size() - 11) * -16)) {
            this.modscrollY = (float) ((getModsInCategory(ModuleCategory.values()[this.selectCategory]).size() - 11) * -16);
         }



         RenderUtils.circle((float) (buttonX + 8), (float) (modulePos + 5) + this.modscrollY, 1.5F, mod.getState() ? (new Color(0, 124, 255)).getRGB() : (new Color(153, 153, 153)).getRGB());
         FontLoaders.F18.drawCenteredString(binding ? (mod == this.bmod ? "Binding Key" : mod.getName()) : mod.getName(), (float) (buttonX + 40), (float) (modulePos + 1) + this.modscrollY, mod.getState() ? (new Color(220, 220, 220)).getRGB() : (new Color(90, 90, 90)).getRGB());
         FontLoaders.F18.drawCenteredString(!mod.getValues().isEmpty() ? (mod.getOpenValues() ? "-" : "+") : "", (float) (buttonX + 76), (float) (modulePos + 1) + this.modscrollY, (new Color(153, 153, 153)).getRGB());
         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerMid.canExcecute()) {
            binding = true;
            this.bmod = mod;
         }

         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handler.canExcecute()) {
            mod.setState(!mod.getState());
         }

         if (this.isHovered((float) (this.startX + 60), (float) (this.startY + 5), (float) (this.startX + 150), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) buttonX, (float) (modulePos - 2) + this.modscrollY, (float) (buttonX + 82), (float) (modulePos + 12) + this.modscrollY, mouseX, mouseY) && this.handlerRight.canExcecute() && !mod.getOpenValues() && !mod.getValues().isEmpty()) {
            mod.setOpenValues(!mod.getOpenValues());
            currentMod = mod;
            this.scrollY = 0.0F;

            for (Module m : LiquidBounce.moduleManager.getModules()) {
               if (m.getOpenValues() && !Objects.equals(m.getName(), mod.getName())) {
                  m.setOpenValues(false);
               }
            }
         }

         if (mod.getOpenValues()) {
            for (Value value : mod.getValues()) {
               if (value instanceof ListValue) {
                  ListValue listValue = (ListValue) value;
                  String name  = value.getName();
                  FontLoaders.F14.drawString(name, (float) (buttonX + 180 - FontLoaders.F14.getStringWidth("" + name) / 2), (float) buttonY + this.scrollY - 1.0F, (new Color(200, 200, 200)).getRGB());
                  FontLoaders.F14.drawString(value.getName(), (float) (buttonX + 90), (float) buttonY + this.scrollY - 1.0F, (new Color(153, 153, 169)).getRGB());
                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 144), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 153), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     ListValue m = (ListValue) value;
                     final String[] valueOfList = m.getValues();
                     listValue.set(String.valueOf(valueOfList));
                  }

                  if (this.isHovered((float) (this.startX + 151), (float) (this.startY + 5), (float) (this.startX + 300), (float) (this.startY + 184), mouseX, mouseY) && this.isHovered((float) (buttonX + 208), (float) buttonY + this.scrollY - 1.0F, (float) (buttonX + 217), (float) (buttonY + 7) + this.scrollY, mouseX, mouseY) && this.handler.canExcecute()) {
                     final ListValue m = (ListValue) value;
                     final String[] valueOfList = m.getValues();
                     listValue.set(String.valueOf(valueOfList));
                  }

                  buttonY += 18;
               }
            }

       

            if (getValueList(mod).size() > 10 && buttonY > this.startY + 185 && this.isHovered((float) (this.startX + 151), (float) (this.startY - 8), (float) (this.startX + 300), (float) (this.startY + 185), mouseX, mouseY)) {
               float wheel = (float) Mouse.getDWheel();
               this.scrollY += wheel / 10.0F;
            }

            if ((double) this.scrollY > 0.0D) {
               this.scrollY = 0.0F;
            }

            if (getValueList(mod).size() > 10 && this.scrollY < (float) ((getValueList(mod).size() - 10) * -18)) {
               this.scrollY = (float) ((getValueList(mod).size() - 10) * -18);
            }
         }

         modulePos += 16;
      }

      GL11.glDisable(3089);
      if (this.isHovered((float) (this.startX + 289), (float) (this.startY - 8), (float) (this.startX + 296), (float) (this.startY), mouseX, mouseY)) {
         if (this.handler.canExcecute()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
         }
      }

       FontLoaders.F18.drawCenteredString("LiquidWing", (float) (this.startX + 28), (float) (this.startY - 6), (new Color(170, 170, 170)).getRGB());
       FontLoaders.F14.drawCenteredString(ModuleCategory.values()[this.selectCategory].name(), (float) (this.startX + 80), (float) (this.startY - 6), (new Color(153, 153, 159)).getRGB());
      GL11.glPopMatrix();
   }

   public boolean isHovered(float x, float y, float width, float height, int mouseX, int mouseY) {
      return (float) mouseX >= x && (float) mouseX <= width && (float) mouseY >= y && (float) mouseY <= height;
   }

   public void onGuiClosed() {
      if (this.mc.entityRenderer.getShaderGroup() != null) {
         this.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
      }

      this.dragging = false;
      this.drag = false;
      this.Mdrag = false;
      super.onGuiClosed();
   }
}
