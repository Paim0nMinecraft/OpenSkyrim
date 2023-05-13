package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.Criticals;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.injection.backend.EntityLivingBaseImplKt;
import net.ccbluex.liquidbounce.utils.Location;
import net.ccbluex.liquidbounce.utils.Particles;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@ModuleInfo(name = "DamageParticle", description = "Display heatlh volume change value", category = ModuleCategory.RENDER)
public class DMGParticle extends Module {

    private final HashMap<EntityLivingBase, Float> healthMap = new HashMap<>();
    private final List<Particles> particles = new ArrayList<>();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);

        int i1 = 0;
        while (i1 < particles.size()) {
            Particles update = particles.get(i1);
            int i = ++update.ticks;
            if (i < 10) {
                update.location.setY(update.location.getY() + update.ticks * 0.002);
            }
            if (i > 20) {
                particles.remove(update);
            }
            i1++;
        }

        EntityLivingBase entity = ka.getTarget() == null ? null : EntityLivingBaseImplKt.unwrap(ka.getTarget());
        if (entity == null || entity == mc.getThePlayer()) {
            return;
        }

        if (!this.healthMap.containsKey(entity)) {
            this.healthMap.put(entity, entity.getHealth());
        }

        float floatValue = this.healthMap.get(entity);
        float health = entity.getHealth();
        final Criticals criticals = (Criticals) LiquidBounce.moduleManager.get(Criticals.class);

        if (floatValue != health) {
            String text;

            if (floatValue - health < 0.0f) {
                text = "§a" + roundToPlace((floatValue - health) * -1.0f, 1);
            } else {
                text = "§e" + roundToPlace(floatValue - health, 1);
            }

            Location location = new Location(entity);
            location.setY(entity.getEntityBoundingBox().minY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) / 2.0);
            location.setX(location.getX() - 0.5 + new Random(System.currentTimeMillis()).nextInt(5) * 0.15);
            location.setZ(location.getZ() - 0.5 + new Random(System.currentTimeMillis() + (0x203FF36645D9EA2EL ^ 0x203FF36645D9EA2FL)).nextInt(5) * 0.15);
            this.particles.add(new Particles(location, text));
            this.healthMap.remove(entity);
            this.healthMap.put(entity, entity.getHealth());
        }
    }

    @EventTarget
    public void onRender(Render3DEvent event) {
        for (Particles p : this.particles) {
            double x = p.location.getX();
            mc.getRenderManager();
            double n = x - mc.getRenderManager().getRenderPosX();
            double y = p.location.getY();
            mc.getRenderManager();
            double n2 = y - mc.getRenderManager().getRenderPosY();
            double z = p.location.getZ();
            mc.getRenderManager();
            double n3 = z - mc.getRenderManager().getRenderPosZ();
            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.translate((float) n, (float) n2, (float) n3);
            GlStateManager.rotate(-mc.getRenderManager().getPlayerViewY(), 0.0f, 1.0f, 0.0f);
            float textY;

            textY = 1.0f;

            GlStateManager.rotate(mc.getRenderManager().getPlayerViewX(), textY, 0.0f, 0.0f);
            final double size = 0.025;
            GlStateManager.scale(-size, -size, size);
            enableGL2D();
            disableGL2D();
            GL11.glDepthMask(false);
            net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.drawString(p.text, (float) (-(net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.getStringWidth(p.text) / 2)), (float) (-(net.ccbluex.liquidbounce.ui.font.Fonts.posterama40.getFontHeight() - 1)), 0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDepthMask(true);
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.popMatrix();
        }
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static double roundToPlace(double p_roundToPlace_0_, int p_roundToPlace_2_) {
        if (p_roundToPlace_2_ < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(p_roundToPlace_0_).setScale(p_roundToPlace_2_, RoundingMode.HALF_UP).doubleValue();
    }
}
