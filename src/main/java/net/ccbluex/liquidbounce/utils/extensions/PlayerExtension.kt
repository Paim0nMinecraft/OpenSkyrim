/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.extensions

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.minecraft.client.Minecraft
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Allows to get the distance between the current entity and [entity] from the nearest corner of the bounding box
 */
fun IEntity.isAnimal(): Boolean {
    return MinecraftInstance.classProvider.isEntityAnimal(this) ||
            MinecraftInstance.classProvider.isEntitySquid(this) ||
            MinecraftInstance.classProvider.isEntityGolem(this) ||
            MinecraftInstance.classProvider.isEntityBat(this)
}

fun IEntity.isMob(): Boolean {
    return MinecraftInstance.classProvider.isEntityMob(this) ||
            MinecraftInstance.classProvider.isEntityVillager(this) ||
            MinecraftInstance.classProvider.isEntitySlime(this)
            || MinecraftInstance.classProvider.isEntityGhast(this) ||
            MinecraftInstance.classProvider.isEntityDragon(this) ||
            MinecraftInstance.classProvider.isEntityShulker(this)
}

fun IEntityPlayer.isClientFriend(): Boolean {
    val entityName = name ?: return false

    return LiquidBounce.fileManager.friendsConfig.isFriend(ColorUtils.stripColor(entityName))
}

fun IEntity.rayTraceWithCustomRotation(blockReachDistance: Double, rotation: Rotation): IMovingObjectPosition? {
    return this.rayTraceWithCustomRotation(blockReachDistance, rotation.yaw, rotation.pitch)
}

fun IEntity.rayTraceWithCustomRotation(blockReachDistance: Double, yaw: Float, pitch: Float): IMovingObjectPosition? {
    val vec3 = this.getPositionEyes(1f)
    val vec31 = ClientUtils.getVectorForRotation(pitch, yaw)
    val vec32 = vec3.addVector(
        vec31.xCoord * blockReachDistance,
        vec31.yCoord * blockReachDistance,
        vec31.zCoord * blockReachDistance
    )
    return MinecraftInstance.mc.theWorld!!.rayTraceBlocks(vec3, vec32, false, false, true)
}

fun IEntity.rayTraceWithServerSideRotation(blockReachDistance: Double): IMovingObjectPosition? {
    return this.rayTraceWithCustomRotation(blockReachDistance, RotationUtils.serverRotation)
}

fun IEntity.getDistanceToEntityBox(entity: IEntity): Double {
    val eyes = this.getPositionEyes(1F)
    val pos = getNearestPointBB(eyes, entity.entityBoundingBox)
    val xDist = abs(pos.xCoord - eyes.xCoord)
    val yDist = abs(pos.yCoord - eyes.yCoord)
    val zDist = abs(pos.zCoord - eyes.zCoord)
    return sqrt(xDist.pow(2) + yDist.pow(2) + zDist.pow(2))
}

val IEntityLivingBase.renderHurtTime: Float
    get() = this.hurtTime - if (this.hurtTime != 0) {
        Minecraft.getMinecraft().timer.renderPartialTicks
    } else {
        0f
    }
val IEntityLivingBase.hurtPercent: Float
    get() = (this.renderHurtTime) / 10

fun getNearestPointBB(eye: WVec3, box: IAxisAlignedBB): WVec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    for (i in 0..2) {
        if (origin[i] > destMaxs[i]) origin[i] = destMaxs[i] else if (origin[i] < destMins[i]) origin[i] = destMins[i]
    }
    return WVec3(origin[0], origin[1], origin[2])
}
