/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.utils.particles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class PlayerParticles {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotations(Entity ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 4.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static Block getBlock(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.world.getBlockState(new BlockPos(offsetX, offsetY, offsetZ)).getBlock();
    }

    private static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - mc.player.posX;
        double zDiff = z - mc.player.posZ;
        double yDiff = y - mc.player.posY - 0.6D;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }


    public static float getDirection() {
        float yaw = mc.player.rotationYawHead;
        float forward = mc.player.moveForward;
        float strafe = mc.player.moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward < 0.0F ? -45 : forward == 0.0F ? 90 : 45);
        }
        return yaw * 0.017453292F;
    }

    public static boolean isInWater() {
        return mc.world.getBlockState(new BlockPos(PlayerParticles.mc.player.posX, PlayerParticles.mc.player.posY, PlayerParticles.mc.player.posZ)).getMaterial() == Material.WATER;
    }

    public static Block getBlock(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static Block getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
        return PlayerParticles.getBlock(new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z));
    }

    public static ArrayList<Vector3f> vanillaTeleportPositions(double tpX, double tpY, double tpZ, double speed) {
        double d;
        ArrayList positions = new ArrayList();
        double posX = tpX - mc.player.posX;
        double posY = tpY - (mc.player.posY + (double) mc.player.getEyeHeight() + 1.1);
        double posZ = tpZ - mc.player.posZ;
        float yaw = (float) (Math.atan2(posZ, posX) * 180.0 / 3.141592653589793 - 90.0);
        float pitch = (float) ((-Math.atan2(posY, Math.sqrt(posX * posX + posZ * posZ))) * 180.0 / 3.141592653589793);
        double tmpX = mc.player.posX;
        double tmpY = mc.player.posY;
        double tmpZ = mc.player.posZ;
        double steps = 1.0;
        for (d = speed; d < PlayerParticles.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ, tpX, tpY, tpZ); d += speed) {
            steps += 1.0;
        }
        for (d = speed; d < PlayerParticles.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ, tpX, tpY, tpZ); d += speed) {
            tmpX = mc.player.posX - Math.sin(PlayerParticles.getDirection(yaw)) * d;
            tmpZ = mc.player.posZ + Math.cos(PlayerParticles.getDirection(yaw)) * d;
            positions.add(new Vector3f((float) tmpX, (float) (tmpY -= (mc.player.posY - tpY) / steps), (float) tmpZ));
        }
        positions.add(new Vector3f((float) tpX, (float) tpY, (float) tpZ));
        return positions;
    }

    public static float getDirection(float yaw) {
        if (mc.player.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        }
        if (mc.player.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw *= 0.017453292f;
    }

    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d2 = y1 - y2;
        double d3 = z1 - z2;
        return MathHelper.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
    }


    public static boolean hotbarIsFull() {
        for (int i = 0; i <= 36; ++i) {
            ItemStack itemstack = PlayerParticles.mc.player.inventory.getStackInSlot(i);
            if (itemstack != null) continue;
            return false;
        }
        return true;
    }

    public static Vec3 getLook(float p_174806_1_, float p_174806_2_) {
        float var3 = MathHelper.cos(-p_174806_2_ * 0.017453292F - 3.1415927F);
        float var4 = MathHelper.sin(-p_174806_2_ * 0.017453292F - 3.1415927F);
        float var5 = -MathHelper.cos(-p_174806_1_ * 0.017453292F);
        float var6 = MathHelper.sin(-p_174806_1_ * 0.017453292F);
        return new Vec3(var4 * var5, var6, var3 * var5);
    }

    public static boolean isMoving() {
        if ((!mc.player.collidedHorizontally) && (!mc.player.isSneaking())) {
            return ((mc.player.movementInput.moveForward != 0.0F || mc.player.movementInput.moveStrafe != 0.0F));
        }
        return false;
    }

    public EntityLivingBase getEntity() {

        return null;
    }

    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

}
