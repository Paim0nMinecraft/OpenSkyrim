package xiatian

import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.utils.MinecraftInstance.mc
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.util.math.MathHelper

object PlayerUtils {
    fun randomUnicode(str: String): String {
        val stringBuilder = StringBuilder()
        for (c in str.toCharArray()) {
            if (Math.random()> 0.5 && c.hashCode() in 33..128) {
                stringBuilder.append(Character.toChars(c.hashCode() + 65248))
            } else {
                stringBuilder.append(c)
            }
        }
        return stringBuilder.toString()
    }
    fun isUsingFood(): Boolean {
        val usingItem = mc.thePlayer!!.itemInUse!!.item
        return if (mc.thePlayer!!.itemInUse != null) {
            mc.thePlayer!!.isUsingItem && (usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion)
        } else false
    }
    fun isBlockUnder(): Boolean {
        if (mc.thePlayer!!.posY < 0) return false
        var off = 0
        while (off < mc.thePlayer!!.posY.toInt() + 2) {
            val bb: IAxisAlignedBB = mc.thePlayer!!.entityBoundingBox
                .offset(0.0, -off.toDouble(), 0.0)
            if (mc.theWorld!!.getCollidingBoundingBoxes(
                    mc.thePlayer!!,
                    bb
                ).isNotEmpty()
            ) {
                return true
            }
            off += 2
        }
        return false
    }
    fun getAr(player : IEntityLivingBase):Double{
        var arPercentage: Double = (player!!.totalArmorValue / player!!.maxHealth).toDouble()
        arPercentage = MathHelper.clamp(arPercentage, 0.0, 1.0)
        return 100 * arPercentage
    }
    fun getBlockRelativeToPlayer(offsetX: Double, offsetY: Double, offsetZ: Double): IBlock? {
        return mc.theWorld!!.getBlockState(
            WBlockPos(
                mc.thePlayer!!.posX + offsetX,
                mc.thePlayer!!.posY + offsetY,
                mc.thePlayer!!.posZ + offsetZ
            )
        ).block
    }
}