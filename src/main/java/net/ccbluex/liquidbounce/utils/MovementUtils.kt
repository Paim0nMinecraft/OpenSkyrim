package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.event.MoveEvent
import net.minecraft.entity.EntityLivingBase
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance() {
    val speed: Float
        get() = sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ).toFloat()

    fun resetMotion(y: Boolean) {
        mc.thePlayer!!.motionX = 0.0
        mc.thePlayer!!.motionZ = 0.0
        if (y) mc.thePlayer!!.motionY = 0.0
    }

    @JvmStatic
    val isMoving: Boolean
        get() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0f || mc.thePlayer!!.movementInput.moveStrafe != 0f)

    fun hasMotion(): Boolean {
        return mc.thePlayer!!.motionX != 0.0 && mc.thePlayer!!.motionZ != 0.0 && mc.thePlayer!!.motionY != 0.0
    }

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()

    fun doTargetStrafe(
        curTarget: EntityLivingBase,
        direction_: Float,
        radius: Float,
        moveEvent: MoveEvent,
        mathRadius: Int = 0
    ) {
        if (!isMoving) return

        var forward_ = 0.0
        var strafe_ = 0.0
        val speed_ = sqrt(moveEvent.x * moveEvent.x + moveEvent.z * moveEvent.z)

        if (speed_ <= 0.0001)
            return

        var _direction = 0.0
        if (direction_ > 0.001) {
            _direction = 1.0
        } else if (direction_ < -0.001) {
            _direction = -1.0
        }
        var curDistance = (0.01).toFloat()
        if (mathRadius == 1) {
            curDistance = mc2.player.getDistance(curTarget)
        } else if (mathRadius == 0) {
            curDistance =
                sqrt((mc.thePlayer!!.posX - curTarget.posX) * (mc.thePlayer!!.posX - curTarget.posX) + (mc.thePlayer!!.posZ - curTarget.posZ) * (mc.thePlayer!!.posZ - curTarget.posZ)).toFloat()
        }
        if (curDistance < radius - speed_) {
            forward_ = -1.0
        } else if (curDistance > radius + speed_) {
            forward_ = 1.0
        } else {
            forward_ = (curDistance - radius) / speed_
        }
        if (curDistance < radius + speed_ * 2 && curDistance > radius - speed_ * 2) {
            strafe_ = 1.0
        }
        strafe_ *= _direction
        var strafeYaw = RotationUtils.getRotationsEntity(curTarget).yaw.toDouble()
        val covert_ = sqrt(forward_ * forward_ + strafe_ * strafe_)

        forward_ /= covert_
        strafe_ /= covert_
        var turnAngle = Math.toDegrees(asin(strafe_))
        if (turnAngle > 0) {
            if (forward_ < 0)
                turnAngle = 180F - turnAngle
        } else {
            if (forward_ < 0)
                turnAngle = -180F - turnAngle
        }
        strafeYaw = Math.toRadians((strafeYaw + turnAngle))
        moveEvent.x = -sin(strafeYaw) * speed_
        moveEvent.z = cos(strafeYaw) * speed_
        mc.thePlayer!!.motionX = moveEvent.x
        mc.thePlayer!!.motionZ = moveEvent.z
    }

    fun isOnGround(height: Double): Boolean {
        return !mc.theWorld!!.getCollidingBoundingBoxes(
                mc.thePlayer!!,
                mc.thePlayer!!.entityBoundingBox.offset(0.0, -height, 0.0)
            ).isEmpty()
    }

    @JvmStatic
    @JvmOverloads
    fun strafe(speed: Float = this.speed) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }

    @JvmStatic
    fun forward(length: Double) {
        val thePlayer = mc.thePlayer!!
        val yaw = Math.toRadians(thePlayer.rotationYaw.toDouble())
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }

    @JvmStatic
    val direction: Double
        get() {
            val thePlayer = mc.thePlayer!!
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }
}