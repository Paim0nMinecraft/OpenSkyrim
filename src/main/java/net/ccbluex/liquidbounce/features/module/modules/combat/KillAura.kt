/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat


import cc.paimon.modules.misc.StrafeFix
import cc.paimon.utils.LocationCache
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.api.minecraft.world.IWorldSettings
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.Block
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.util.EnumParticleTypes
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import java.awt.Color
import java.awt.Robot
import java.awt.event.InputEvent
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

// TODO: Recode KillAura mark
@ModuleInfo(
    name = "KillAura",
    category = ModuleCategory.COMBAT,
    keyBind = Keyboard.KEY_R,
    description = "Kill Your Mom"
)
class KillAura : Module() {

    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = getAttackDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = getAttackDelay(this.get(), maxCPS.get())
        }
    }

    val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val cooldownValue = FloatValue("Cooldown", 1f, 0f, 1f)
    private val combatDelayValue = BoolValue("1.9CombatDelay", false)

    // Range
    val rangeValue = object : FloatValue("Range", 3.7f, 1f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val throughWallsRangeValue = object : FloatValue("ThroughWallsRange", 1.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = rangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val discoverRangeValue = FloatValue("DiscoverRange", 6f, 0f, 15f)
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)

    // Modes
    private val priorityValue =
        ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime", "Armor"), "Distance")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Single")

    // Bypass
    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val keepSprintValue = BoolValue("KeepSprint", true)
    private val airStopSprintValue = BoolValue("AirStopSprint", true)

    // AutoBlock
    private val autoBlockRangeValue = object : FloatValue("AutoBlockRange", 2.5f, 0f, 8f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = discoverRangeValue.get()
            if (i < newValue) set(i)
        }
    }
    private val autoBlockValue =
        ListValue("AutoBlockPacket", arrayOf("Off", "Normal", "Packet", "KeyBind", "Right"), "KeyBind")
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", true)
    private val blockRate = IntegerValue("AutoBlockRate", 100, 1, 100)

    // Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false)
    private val livingRaycastValue = BoolValue("LivingRayCast", true)

    // Bypass
    private val aacValue = BoolValue("AAC", true)

    // Turn Speed
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }
    private val rotations = ListValue(
        "RotationMode",
        arrayOf("Vanilla", "Other", "BackTrack", "LiquidSensePlus", "Test", "HytRotation", "SmoothCenter", "Smooth"),
        "BackTrack"
    )

    private val silentRotationValue = BoolValue("SilentRotation", true)
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Silent")
    private val strafeOnlyGroundValue = BoolValue("StrafeOnlyGround", true)
    private val randomCenterValue = BoolValue("RandomCenter", false)
    private val outborderValue = BoolValue("Outborder", false)
    private val hitableValue = BoolValue("AlwaysHitable", true)
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)

    private val rotationSmoothModeValue =
        ListValue("SmoothMode", arrayOf("Custom", "Line", "Quad", "Sine", "QuadSine"), "Custom")

    private val rotationSmoothValue = FloatValue("CustomSmooth", 2f, 1f, 10f)

    private val randomCenterModeValue =
        ListValue("RandomCenterMode", arrayOf("Off", "Cubic", "Horizonal", "Vertical"), "Off")
    private val randomCenRangeValue = FloatValue("RandomRange", 0.0f, 0.0f, 1.2f)

    private val rotationRevValue = BoolValue("RotationReverse", false)
    private val rotationRevTickValue = IntegerValue("RotationReverseTick", 5, 1, 20)
    private val keepDirectionValue = BoolValue("KeepDirection", true)
    private val keepDirectionTickValue = IntegerValue("KeepDirectionTick", 15, 1, 20)
    private val backtraceValue = BoolValue("Backtrace", false)
    private val backtraceTickValue = IntegerValue("BacktraceTick", 2, 1, 10)

    // Predict
    private val predictValue = BoolValue("Predict", true)


    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }

    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val switchDelayValue = IntegerValue("SwitchDelay", 300, 1, 2000)
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50)

    // Visuals
    private val markValue =
        ListValue("Mark", arrayOf("Off", "Rise", "Liquid", "FDP", "Block", "Jello", "Plat", "Red", "Sims"), "FDP")
    private val hiteffect = ListValue(
        "HitEffect",
        arrayOf("Off", "Lightningbolt", "Criticals", "Blood", "Fire", "Water", "Smoke", "Flame", "Heart"),
        "Off"
    )
    private val lightingSoundValue = BoolValue("LightingSound", true)
    private val fakeSharpValue = BoolValue("FakeSharp", true)
    private val circleValue = BoolValue("Circle", true)
    private val circletargetValue = BoolValue("CircleTarget", true)
    private val circleRed = IntegerValue("CircleRed", 255, 0, 255)
    private val circleGreen = IntegerValue("CircleGreen", 255, 0, 255)
    private val circleBlue = IntegerValue("CircleBlue", 255, 0, 255)
    private val circleAlpha = IntegerValue("CircleAlpha", 255, 0, 255)
    private val circleAccuracy = IntegerValue("CircleAccuracy", 15, 0, 60)

    /**
     * MODULE
     */

    // Target
    var target: IEntityLivingBase? = null
    private var lastTarget: IEntityLivingBase? = null
    private val markTimer = MSTimer()
    private var currentTarget: IEntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()
    private val discoveredTargets = mutableListOf<IEntityLivingBase>()
    private val inRangeDiscoveredTargets = mutableListOf<IEntityLivingBase>()

    // Attack delay
    private val attackTimer = MSTimer()
    private val switchTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    private var predictX = 1.0f
    private var predictY = 1.0f
    private var predictZ = 1.0f

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc.thePlayer!!.isRiding)
            return
        if (this.airStopSprintValue.get()) {
            if (mc.thePlayer!!.onGround) {
                this.keepSprintValue.set(true)
            } else {
                this.keepSprintValue.set(false)
            }
        }
        if (event.eventState == EventState.POST) {

            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()
            // AutoBlock
            if (!autoBlockValue.get().equals(
                    "Off",
                    true
                ) && discoveredTargets.isNotEmpty() || discoveredTargets.filter {
                    mc.thePlayer!!.getDistanceToEntityBox(
                        it
                    ) > maxRange
                }.isNotEmpty() && canBlock
            ) {
                val target = discoveredTargets[0]
                if (mc.thePlayer!!.getDistanceToEntityBox(target) < autoBlockRangeValue.get())
                    startBlocking(
                        target,
                        interactAutoBlockValue.get() && (mc.thePlayer!!.getDistanceToEntityBox(target) < maxRange)
                    )
            }

            return
        }
        if (event.eventState == EventState.PRE) {
            update()
            val strafeFix = LiquidBounce.moduleManager[StrafeFix::class.java] as StrafeFix
            strafeFix.applyForceStrafe(
                rotationStrafeValue.get().equals("Silent"),
                !rotationStrafeValue.get().equals("Off") && !rotations.get()
                    .equals("None") && !((!mc2.player.onGround && strafeOnlyGroundValue.get()))
            )

        }
//        if (target != null && currentTarget != null) {
//            while (clicks > 0) {
//                runAttack()
//                clicks--
//            }
//        }
    }

    /**
     * Strafe event
     */
//    @EventTarget
//    fun onStrafe(event: StrafeEvent) {
//        if (rotationStrafeValue.get().equals("Off", true) && !mc.thePlayer!!.isRiding)
//            return
//
//        update()
//
//        if(strafeOnlyGroundValue.get()&&!mc.thePlayer!!.onGround)
//            return
//
//        if (discoveredTargets.isNotEmpty() && RotationUtils.targetRotation != null) {
//            when (rotationStrafeValue.get().toLowerCase()) {
//                "strict" -> {
//                    val (yaw) = RotationUtils.targetRotation ?: return
//                    var strafe = event.strafe
//                    var forward = event.forward
//                    val friction = event.friction
//
//                    var f = strafe * strafe + forward * forward
//
//                    if (f >= 1.0E-4F) {
//                        f = MathHelper.sqrt(f)
//
//                        if (f < 1.0F)
//                            f = 1.0F
//
//                        f = friction / f
//                        strafe *= f
//                        forward *= f
//
//                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
//                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())
//
//                        mc.thePlayer!!.motionX += strafe * yawCos - forward * yawSin
//                        mc.thePlayer!!.motionZ += forward * yawCos + strafe * yawSin
//                    }
//                    event.cancelEvent()
//                }
//                "silent" -> {
//                    update()
//
//                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
//                    event.cancelEvent()
//                }
//            }
//        }
//    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get()))
        )
            return

        // Update target
        updateTarget()

        if (discoveredTargets.isEmpty()) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && EntityUtils.isSelected(
                currentTarget!!.asEntityLivingBase(),
                true
            )
        )
            target = currentTarget
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }
        if (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())
        ) {
            target = null
            currentTarget = null
            hitable = false
            if (classProvider.isGuiContainer(mc.currentScreen)) containerOpen = System.currentTimeMillis()
            return
        }

        if (target != null && currentTarget != null && (Backend.MINECRAFT_VERSION_MINOR == 8 || mc.thePlayer!!.getCooledAttackStrength(
                0.0F
            ) >= cooldownValue.get())
        ) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (circletargetValue.get()) {
            if (LiquidBounce.combatManager.target != null) {
                GL11.glPushMatrix()
                GL11.glTranslated(
                    LiquidBounce.combatManager.target!!.lastTickPosX + (LiquidBounce.combatManager.target!!.posX - LiquidBounce.combatManager.target!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                    LiquidBounce.combatManager.target!!.lastTickPosY + (LiquidBounce.combatManager.target!!.posY - LiquidBounce.combatManager.target!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                    LiquidBounce.combatManager.target!!.lastTickPosZ + (LiquidBounce.combatManager.target!!.posZ - LiquidBounce.combatManager.target!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
                )
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

                GL11.glLineWidth(1F)
                GL11.glColor4f(
                    circleRed.get().toFloat() / 255.0F,
                    circleGreen.get().toFloat() / 255.0F,
                    circleBlue.get().toFloat() / 255.0F,
                    circleAlpha.get().toFloat() / 255.0F
                )
                GL11.glRotatef(90F, 1F, 0F, 0F)
                GL11.glBegin(GL11.GL_LINE_STRIP)

                for (i in 0..360 step 61 - circleAccuracy.get()) { // You can change circle accuracy  (60 - accuracy)
                    GL11.glVertex2f(
                        cos(i * Math.PI / 180.0).toFloat() * 2,
                        (sin(i * Math.PI / 180.0).toFloat() * 2)
                    )
                }
                GL11.glVertex2f(
                    cos(360 * Math.PI / 180.0).toFloat() * 2,
                    (sin(360 * Math.PI / 180.0).toFloat() * 2)
                )

                GL11.glEnd()

                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)

                GL11.glPopMatrix()
            }
        }
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer!!.lastTickPosX + (mc.thePlayer!!.posX - mc.thePlayer!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer!!.lastTickPosY + (mc.thePlayer!!.posY - mc.thePlayer!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer!!.lastTickPosZ + (mc.thePlayer!!.posZ - mc.thePlayer!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(1F)
            GL11.glColor4f(
                circleRed.get().toFloat() / 255.0F,
                circleGreen.get().toFloat() / 255.0F,
                circleBlue.get().toFloat() / 255.0F,
                circleAlpha.get().toFloat() / 255.0F
            )
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            for (i in 0..360 step 61 - circleAccuracy.get()) { // You can change circle accuracy  (60 - accuracy)
                GL11.glVertex2f(
                    cos(i * Math.PI / 180.0).toFloat() * rangeValue.get(),
                    (sin(i * Math.PI / 180.0).toFloat() * rangeValue.get())
                )
            }
            GL11.glVertex2f(
                cos(360 * Math.PI / 180.0).toFloat() * rangeValue.get(),
                (sin(360 * Math.PI / 180.0).toFloat() * rangeValue.get())
            )

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            discoveredTargets.clear()
            inRangeDiscoveredTargets.clear()
        }
        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) && currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = getAttackDelay(minCPS.get(), maxCPS.get())
        }

        when (hiteffect.get().toLowerCase()) {
            "flame" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.FLAME)
            "smoke" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.SMOKE_LARGE)
            "heart" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.HEART)
            "fire" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.LAVA)
            "water" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.WATER_DROP)
            "criticals" -> mc.effectRenderer.emitParticleAtEntity(target!!, EnumParticleTypes.CRIT)
            "blood" -> {
                repeat(10) {
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.BLOCK_CRACK.particleID,
                        target!!.posX,
                        target!!.posY + target!!.height / 2,
                        target!!.posZ,
                        target!!.motionX + RandomUtils.nextFloat(-0.5f, 0.5f),
                        target!!.motionY + RandomUtils.nextFloat(-0.5f, 0.5f),
                        target!!.motionZ + RandomUtils.nextFloat(-0.5f, 0.5f),
                        Block.getStateId(Blocks.REDSTONE_BLOCK.defaultState)
                    )
                }
            }
        }

        when (markValue.get().toLowerCase()) {
            "liquid" -> {
                discoveredTargets.forEach {
                    RenderUtils.drawPlatform(
                        it,
                        if (it.hurtTime <= 0) Color(37, 126, 255, 170) else Color(255, 0, 0, 170)
                    )
                }
            }

            "rise" -> {
                discoveredTargets.forEach {
                    RenderUtils.drawCircle(it, 0.67, Color(159, 24, 242).rgb, true)
                }
            }

            "plat" -> RenderUtils.drawPlatform(
                target!!,
                if (hitable) Color(37, 126, 255, 70) else Color(255, 0, 0, 70)
            )

            "block" -> {
                discoveredTargets.forEach {
                    val bb = it.entityBoundingBox
                    it.entityBoundingBox = bb.expand(0.2, 0.2, 0.2)
                    RenderUtils.drawEntityBox(it, if (it.hurtTime <= 0) Color.GREEN else Color.RED, true)
                    it.entityBoundingBox = bb
                }
            }

            "red" -> {
                discoveredTargets.forEach {
                    RenderUtils.drawPlatform(
                        it,
                        if (it.hurtTime <= 0) Color(255, 255, 255, 255) else Color(124, 215, 255, 255)
                    )
                }
            }

            "sims" -> {
                discoveredTargets.forEach {
                    val radius = 0.15f
                    val side = 4
                    GL11.glPushMatrix()
                    GL11.glTranslated(
                        it.lastTickPosX + (it.posX - it.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX,
                        (it.lastTickPosY + (it.posY - it.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + it.height * 1.1,
                        it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                    )
                    GL11.glRotatef(-it.width, 0.0f, 1.0f, 0.0f)
                    GL11.glRotatef((mc.thePlayer!!.ticksExisted + mc.timer.renderPartialTicks) * 5, 0f, 1f, 0f)
                    RenderUtils.glColor(if (it.hurtTime <= 0) Color(80, 255, 80) else Color(255, 0, 0))
                    RenderUtils.enableSmoothLine(1.5F)
                    val c = Cylinder()
                    GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                    c.draw(0F, radius, 0.3f, side, 1)
                    c.drawStyle = 100012
                    GL11.glTranslated(0.0, 0.0, 0.3)
                    c.draw(radius, 0f, 0.3f, side, 1)
                    GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
                    GL11.glTranslated(0.0, 0.0, -0.3)
                    c.draw(0F, radius, 0.3f, side, 1)
                    GL11.glTranslated(0.0, 0.0, 0.3)
                    c.draw(radius, 0F, 0.3f, side, 1)
                    RenderUtils.disableSmoothLine()
                    GL11.glPopMatrix()
                }
            }

            "fdp" -> {
                val drawTime = (System.currentTimeMillis() % 1500).toInt()
                val drawMode = drawTime > 750
                var drawPercent = drawTime / 750.0
                //true when goes up
                if (!drawMode) {
                    drawPercent = 1 - drawPercent
                } else {
                    drawPercent -= 1
                }
                drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                discoveredTargets.forEach {
                    GL11.glPushMatrix()
                    GL11.glDisable(3553)
                    GL11.glEnable(2848)
                    GL11.glEnable(2881)
                    GL11.glEnable(2832)
                    GL11.glEnable(3042)
                    GL11.glBlendFunc(770, 771)
                    GL11.glHint(3154, 4354)
                    GL11.glHint(3155, 4354)
                    GL11.glHint(3153, 4354)
                    GL11.glDisable(2929)
                    GL11.glDepthMask(false)

                    val bb = it.entityBoundingBox
                    val radius = (bb.maxX - bb.minX) + 0.3
                    val height = bb.maxY - bb.minY
                    val x =
                        it.lastTickPosX + (it.posX - it.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
                    val y =
                        (it.lastTickPosY + (it.posY - it.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + height * drawPercent
                    val z =
                        it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                    GL11.glLineWidth((radius * 5f).toFloat())
                    GL11.glBegin(3)
                    for (i in 0..360) {
                        val rainbow = Color(
                            Color.HSBtoRGB(
                                (mc.thePlayer!!.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f,
                                0.7f,
                                1.0f
                            )
                        )
                        GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                        GL11.glVertex3d(
                            x + radius * cos(i * 6.283185307179586 / 45.0),
                            y,
                            z + radius * sin(i * 6.283185307179586 / 45.0)
                        )
                    }
                    GL11.glEnd()

                    GL11.glDepthMask(true)
                    GL11.glEnable(2929)
                    GL11.glDisable(2848)
                    GL11.glDisable(2881)
                    GL11.glEnable(2832)
                    GL11.glEnable(3553)
                    GL11.glPopMatrix()
                }
            }

            "jello" -> {
                discoveredTargets.forEach {
                    val drawTime = (System.currentTimeMillis() % 2000).toInt()
                    val drawMode = drawTime > 1000
                    var drawPercent = drawTime / 1000.0
                    //true when goes up
                    if (!drawMode) {
                        drawPercent = 1 - drawPercent
                    } else {
                        drawPercent -= 1
                    }
                    drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                    val points = mutableListOf<WVec3>()
                    val bb = it.entityBoundingBox
                    val radius = bb.maxX - bb.minX
                    val height = bb.maxY - bb.minY
                    val posX = it.lastTickPosX + (it.posX - it.lastTickPosX) * mc.timer.renderPartialTicks
                    var posY = it.lastTickPosY + (it.posY - it.lastTickPosY) * mc.timer.renderPartialTicks
                    if (drawMode) {
                        posY -= 0.5
                    } else {
                        posY += 0.5
                    }
                    val posZ = it.lastTickPosZ + (it.posZ - it.lastTickPosZ) * mc.timer.renderPartialTicks
                    for (i in 0..360 step 7) {
                        points.add(
                            WVec3(
                                posX - sin(i * Math.PI / 180F) * radius,
                                posY + height * drawPercent,
                                posZ + cos(i * Math.PI / 180F) * radius
                            )
                        )
                    }
                    points.add(points[0])
                    //draw
                    mc.entityRenderer.disableLightmap()
                    GL11.glPushMatrix()
                    GL11.glDisable(GL11.GL_TEXTURE_2D)
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                    GL11.glEnable(GL11.GL_LINE_SMOOTH)
                    GL11.glEnable(GL11.GL_BLEND)
                    GL11.glDisable(GL11.GL_DEPTH_TEST)
                    GL11.glBegin(GL11.GL_LINE_STRIP)
                    val baseMove = (if (drawPercent > 0.5) {
                        1 - drawPercent
                    } else {
                        drawPercent
                    }) * 2
                    val min = (height / 60) * 20 * (1 - baseMove) * (if (drawMode) {
                        -1
                    } else {
                        1
                    })
                    for (i in 0..20) {
                        var moveFace = (height / 60F) * i * baseMove
                        if (drawMode) {
                            moveFace = -moveFace
                        }
                        val firstPoint = points[0]
                        GL11.glVertex3d(
                            firstPoint.xCoord - mc.renderManager.viewerPosX,
                            firstPoint.yCoord - moveFace - min - mc.renderManager.viewerPosY,
                            firstPoint.zCoord - mc.renderManager.viewerPosZ
                        )
                        GL11.glColor4f(1F, 1F, 1F, 0.7F * (i / 20F))
                        for (vec3 in points) {
                            GL11.glVertex3d(
                                vec3.xCoord - mc.renderManager.viewerPosX,
                                vec3.yCoord - moveFace - min - mc.renderManager.viewerPosY,
                                vec3.zCoord - mc.renderManager.viewerPosZ
                            )
                        }
                        GL11.glColor4f(0F, 0F, 0F, 0F)
                    }
                    GL11.glEnd()
                    GL11.glEnable(GL11.GL_DEPTH_TEST)
                    GL11.glDisable(GL11.GL_LINE_SMOOTH)
                    GL11.glDisable(GL11.GL_BLEND)
                    GL11.glEnable(GL11.GL_TEXTURE_2D)
                    GL11.glPopMatrix()
                }
            }
        }
    }

    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val openInventory = aacValue.get() && classProvider.isGuiInventory(mc.currentScreen)
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())

        // Check is not hitable or check failrate
        if (!hitable || failHit) {
            if (!swing.equals("none", true) && (fakeSwingValue.get() || failHit)) {
                if (swing.equals("packet", true)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())
                } else {
                    mc.thePlayer!!.swingItem()
                }
            }
        } else {
            // Attack
            if (!targetModeValue.get().equals("Multi", ignoreCase = true)) {
                attackEntity(currentTarget!!)
            } else {
                inRangeDiscoveredTargets.forEachIndexed { index, entity ->
                    if (limitedMultiTargetsValue.get() == 0 || index < limitedMultiTargetsValue.get())
                        attackEntity(entity)
                }
            }

            if (targetModeValue.get().equals("Switch", true)) {
                if (switchTimer.hasTimePassed(switchDelayValue.get().toLong())) {
                    prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                    switchTimer.reset()
                }
            } else {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
            }

            if (target == currentTarget)
                target = null
        }

        // Open inventory
        if (openInventory) createOpenInventoryPacket()
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        target = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        discoveredTargets.clear()

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (!classProvider.isEntityLivingBase(entity) || !EntityUtils.isSelected(
                    entity,
                    true
                ) || (switchMode && prevTargetEntities.contains(entity.entityId))
            )
                continue

            val distance = mc.thePlayer!!.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= discoverRangeValue.get() && (fov == 180F || entityFov <= fov) && entity.asEntityLivingBase().hurtTime <= hurtTime)
                discoveredTargets.add(entity.asEntityLivingBase())
        }

        // Sort targets by priority
        when (priorityValue.get().toLowerCase()) {
            "distance" -> discoveredTargets.sortBy { mc.thePlayer!!.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> discoveredTargets.sortBy { it.health } // Sort by health
            "direction" -> discoveredTargets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> discoveredTargets.sortBy { -it.ticksExisted } // Sort by existence
            "armor" -> discoveredTargets.sortBy { it.totalArmorValue } // Sort by armor
        }

        inRangeDiscoveredTargets.clear()
        inRangeDiscoveredTargets.addAll(discoveredTargets.filter {
            mc.thePlayer!!.getDistanceToEntityBox(it) < getRange(
                it
            )
        })

        // Cleanup last targets when no targets found and try again
        if (inRangeDiscoveredTargets.isEmpty() && prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
            return
        }

        // Find best target
        for (entity in discoveredTargets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            if (mc.thePlayer!!.getDistanceToEntityBox(entity) < maxRange)
                target = entity

            return
        }
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: IEntityLivingBase) {
        // Call attack event
        LiquidBounce.eventManager.callEvent(AttackEvent(entity))
        markTimer.reset()
        if (mc.thePlayer!!.isBlocking || blockingStatus) {
            stopBlocking()
        }

        // Attack target
        val swing = swingValue.get()
        if (swing.equals("packet", true)) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())
        } else if (swing.equals("normal", true)) {
            mc.thePlayer!!.swingItem()
        }

        mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(entity, ICPacketUseEntity.WAction.ATTACK))

        if (keepSprintValue.get()) {
            // Critical Effect
            if (mc.thePlayer!!.fallDistance > 0F && !mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder &&
                !mc.thePlayer!!.isInWater && !mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS)) && !mc.thePlayer!!.isRiding
            )
                mc.thePlayer!!.onCriticalHit(entity)

            // Enchant Effect
            if (functions.getModifierForCreature(
                    mc.thePlayer!!.heldItem,
                    entity.creatureAttribute
                ) > 0F || fakeSharpValue.get()
            )
                mc.thePlayer!!.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != IWorldSettings.WGameType.SPECTATOR)
                mc.thePlayer!!.attackTargetEntityWithCurrentItem(entity)
        }
        // Start blocking after attack
        if (mc.thePlayer!!.isBlocking || (!autoBlockValue.get().equals("None") && canBlock)) {
            if (!(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get()))
                return

//            if (delay.get())
//                return

            startBlocking(entity, interactAutoBlockValue.get())
        }
    }

    private val getAABB: ((IEntity) -> IAxisAlignedBB) = {
        var aabb = it.entityBoundingBox
        aabb = if (backtraceValue.get()) LocationCache.getPreviousAABB(
            it.entityId,
            backtraceTickValue.get(),
            aabb
        ) else aabb
        aabb = if (predictValue.get()) aabb.offset(
            (it.posX - it.lastTickPosX) * predictX,
            (it.posY - it.lastTickPosY) * predictY,
            (it.posZ - it.lastTickPosZ) * predictZ
        ) else aabb
        aabb
    }
    /**
     * Update killaura rotations to enemy
     */
    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: IEntity): Boolean {
        var boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Vanilla", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("Smooth", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get()) {
                predictX = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
                predictY = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
                predictZ = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
            }
            val boundingBox = getAABB(entity)
//            val rModes = "CenterLine"

            val (_, directRotation) =
                RotationUtils.searchCenter2(
                    boundingBox,
                    outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                    randomCenterValue.get(),
                    predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                    maxRange,
                    false,
                    false
                ) ?: return false


            var diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, directRotation)
            if (diffAngle < 0) diffAngle = -diffAngle
            if (diffAngle > 180.0) diffAngle = 180.0
            val calculateSpeed = when (rotationSmoothModeValue.get()) {
                "Custom" -> diffAngle / rotationSmoothValue.get()
                "Line" -> (diffAngle / 360) * maxTurnSpeed.get() + (1 - diffAngle / 360) * minTurnSpeed.get()
                //"Quad" -> Math.pow((diffAngle / 180.0), 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow((diffAngle / 180.0), 2.0)) * minTurnSpeedValue.get()
                "Quad" -> (diffAngle / 360.0).pow(2.0) * maxTurnSpeed.get() + (1 - (diffAngle / 360.0).pow(2.0)) * minTurnSpeed.get()
                "Sine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * maxTurnSpeed.get() + (cos(diffAngle / 360 * Math.PI) * 0.5 + 0.5) * minTurnSpeed.get()
                //"QuadSine" -> Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0)) * minTurnSpeedValue.get()
                "QuadSine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5).pow(2.0) * maxTurnSpeed.get() + (1 - (-cos(
                    diffAngle / 180 * Math.PI
                ) * 0.5 + 0.5).pow(2.0)) * minTurnSpeed.get()

                else -> 360.0
            }
            val rotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )

            if (silentRotationValue.get()) {
                if (rotationRevTickValue.get() > 0 && rotationRevValue.get()) {
                    if (keepDirectionValue.get()) {
                        RotationUtils.setTargetRotationReverse(
                            rotation,
                            keepDirectionTickValue.get(),
                            rotationRevTickValue.get()
                        )
                    } else {
                        RotationUtils.setTargetRotationReverse(rotation, 1, rotationRevTickValue.get())
                    }
                } else {
                    if (keepDirectionValue.get()) {
                        RotationUtils.setTargetRotation(rotation, keepDirectionTickValue.get())
                    } else {
                        RotationUtils.setTargetRotation(rotation, 1)
                    }
                }
            } else {
                rotation.toPlayer(mc.thePlayer!!)
            }
            return true
        }
        if (rotations.get().equals("SmoothCenter", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get()) {
                predictX = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
                predictY = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
                predictZ = RandomUtils.nextFloat(maxPredictSize.get(), minPredictSize.get())
            }
            val boundingBox = getAABB(entity)
            val rModes = "CenterLine"

            val (_, directRotation) =
                RotationUtils.calculateCenter(
                    rModes,
                    randomCenterModeValue.get(),
                    (randomCenRangeValue.get()).toDouble(),
                    boundingBox,
                    predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) <= throughWallsRangeValue.get()
                ) ?: return false


            var diffAngle = RotationUtils.getRotationDifference(RotationUtils.serverRotation, directRotation)
            if (diffAngle < 0) diffAngle = -diffAngle
            if (diffAngle > 180.0) diffAngle = 180.0
            val calculateSpeed = when (rotationSmoothModeValue.get()) {
                "Custom" -> diffAngle / rotationSmoothValue.get()
                "Line" -> (diffAngle / 360) * maxTurnSpeed.get() + (1 - diffAngle / 360) * minTurnSpeed.get()
                //"Quad" -> Math.pow((diffAngle / 180.0), 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow((diffAngle / 180.0), 2.0)) * minTurnSpeedValue.get()
                "Quad" -> (diffAngle / 360.0).pow(2.0) * maxTurnSpeed.get() + (1 - (diffAngle / 360.0).pow(2.0)) * minTurnSpeed.get()
                "Sine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5) * maxTurnSpeed.get() + (cos(diffAngle / 360 * Math.PI) * 0.5 + 0.5) * minTurnSpeed.get()
                //"QuadSine" -> Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0) * maxTurnSpeedValue.get() + (1 - Math.pow(-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5, 2.0)) * minTurnSpeedValue.get()
                "QuadSine" -> (-cos(diffAngle / 180 * Math.PI) * 0.5 + 0.5).pow(2.0) * maxTurnSpeed.get() + (1 - (-cos(
                    diffAngle / 180 * Math.PI
                ) * 0.5 + 0.5).pow(2.0)) * minTurnSpeed.get()

                else -> 360.0
            }
            val rotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                directRotation,
                (calculateSpeed).toFloat()
            )

            if (silentRotationValue.get()) {
                if (rotationRevTickValue.get() > 0 && rotationRevValue.get()) {
                    if (keepDirectionValue.get()) {
                        RotationUtils.setTargetRotationReverse(
                            rotation,
                            keepDirectionTickValue.get(),
                            rotationRevTickValue.get()
                        )
                    } else {
                        RotationUtils.setTargetRotationReverse(rotation, 1, rotationRevTickValue.get())
                    }
                } else {
                    if (keepDirectionValue.get()) {
                        RotationUtils.setTargetRotation(rotation, keepDirectionTickValue.get())
                    } else {
                        RotationUtils.setTargetRotation(rotation, 1)
                    }
                }
            } else {
                rotation.toPlayer(mc.thePlayer!!)
            }
            return true
        }/*
        if (rotations.get().equals("BackTrack", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.searchCenter(
                    boundingBox,
                    outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                    randomCenterValue.get(),
                    predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                    maxRange
            ) ?: return false
            //debug
            // ClientUtils.displayChatMessage((mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get()).toString())
            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            }else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }*/
        if (rotations.get().equals("Other", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                RotationUtils.OtherRotation(
                    boundingBox, RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(), maxRange
                ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            } else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        if (rotations.get().equals("BackTrack", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                RotationUtils.OtherRotation(
                    boundingBox, RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(), maxRange
                ), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            } else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        if (rotations.get().equals("HytRotation", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.lockView(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }

        if (rotations.get().equals("Test", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.lockView(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false
            //debug
            // ClientUtils.displayChatMessage((mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get()).toString())
            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            } else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        if (rotations.get().equals("LiquidSensePlus", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return true

            var boundingBox = entity.entityBoundingBox

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX - (mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX)) * RandomUtils.nextFloat(
                        minPredictSize.get(),
                        maxPredictSize.get()
                    ),
                    (entity.posY - entity.prevPosY - (mc.thePlayer!!.posY - mc.thePlayer!!.prevPosY)) * RandomUtils.nextFloat(
                        minPredictSize.get(),
                        maxPredictSize.get()
                    ),
                    (entity.posZ - entity.prevPosZ - (mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ)) * RandomUtils.nextFloat(
                        minPredictSize.get(),
                        maxPredictSize.get()
                    )
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        return true
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if (hitableValue.get()) {
            hitable = true
            return
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeed.get() <= 0F) {
            hitable = true
            return
        }

        val reach = maxRange.toDouble()


        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach, object : RaycastUtils.EntityFilter {
                override fun canRaycast(entity: IEntity?): Boolean {
                    return (!livingRaycastValue.get() || (classProvider.isEntityLivingBase(entity) && !classProvider.isEntityArmorStand(
                        entity
                    ))) &&
                            (EntityUtils.isSelected(
                                entity,
                                false
                            ) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld!!.getEntitiesWithinAABBExcludingEntity(
                                entity,
                                entity!!.entityBoundingBox
                            ).isNotEmpty())
                }

            })

            if (raycastValue.get() && classProvider.isEntityLivingBase(raycastedEntity)
                && !EntityUtils.isFriend(raycastedEntity)
            )
                currentTarget = raycastedEntity!!.asEntityLivingBase()

            hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: IEntity, interact: Boolean) {
        if (!(blockRate.get() > 0 && Random().nextInt(100) <= blockRate.get()))
            return

        if (blockingStatus)
            return

        if (interact) {
            mc.netHandler.addToSendQueue(
                classProvider.createCPacketUseEntity(
                    interactEntity,
                    interactEntity.positionVector
                )
            )
            mc.netHandler.addToSendQueue(
                classProvider.createCPacketUseEntity(
                    interactEntity,
                    ICPacketUseEntity.WAction.INTERACT
                )
            )
        }

        if (autoBlockValue.get().equals("Packet", true)) {
            mc.netHandler.addToSendQueue(
                createUseItemPacket(
                    mc.thePlayer!!.inventory.getCurrentItemInHand(),
                    WEnumHand.MAIN_HAND
                )
            )
            mc.netHandler.addToSendQueue(
                createUseItemPacket(
                    mc.thePlayer!!.inventory.getCurrentItemInHand(),
                    WEnumHand.OFF_HAND
                )
            )
        }

        if (autoBlockValue.get().equals("Normal", true)) {
            mc.netHandler.addToSendQueue(
                classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                    WBlockPos.ORIGIN,
                    classProvider.getEnumFacing(EnumFacingType.DOWN)
                )
            )
        }

        if (autoBlockValue.get().equals("KeyBind", true)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, true)

            mc.thePlayer!!.itemInUseCount = 72000
        }

        if (autoBlockValue.get().equals("Right", true)) {
            Robot().mousePress(InputEvent.BUTTON3_DOWN_MASK)
        }

        blockingStatus = true
    }

    private fun stopBlocking() {
        if (blockingStatus) {

            if (autoBlockValue.get().equals("KeyBind", true)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.keyCode, false)

                mc.playerController.onStoppedUsingItem(mc.thePlayer!!)
                mc.thePlayer!!.itemInUseCount = 0
            }

            if (autoBlockValue.get().equals("Right", true)) {
                Robot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK)
            }

            if (autoBlockValue.get().equals("Packet", true)) {
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                        WBlockPos.ORIGIN,
                        classProvider.getEnumFacing(EnumFacingType.DOWN)
                    )
                )
                mc.thePlayer!!.itemInUseCount = 0
            }

            if (autoBlockValue.get().equals("Normal", true)) {
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                        WBlockPos.ORIGIN,
                        classProvider.getEnumFacing(
                            EnumFacingType.DOWN
                        )
                    )
                )
                mc.thePlayer!!.itemInUseCount = 0
            }

            blockingStatus = false

        }
    }

    fun getNearByEntity(radius: Float): IEntityLivingBase? {
        return try {
            mc.theWorld!!.loadedEntityList
                .filter { mc.thePlayer!!.getDistanceToEntity(it) < radius && EntityUtils.isSelected(it, true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer!!) }[0].asEntityLivingBase()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Attack Delay
     */
    private fun getAttackDelay(minCps: Int, maxCps: Int): Long {
        var delay = TimeUtils.randomClickDelay(minCps.coerceAtMost(maxCps), minCps.coerceAtLeast(maxCps))
        if (combatDelayValue.get()) {
            var value = 4.0
            if (mc.thePlayer!!.inventory.getCurrentItemInHand() != null) {
                val currentItem = mc.thePlayer!!.inventory.getCurrentItemInHand()!!.item
                if (classProvider.isItemSword(currentItem)) {
                    value -= 2.4
                } else if (classProvider.isItemPickaxe(currentItem)) {
                    value -= 2.8
                } else if (classProvider.isItemAxe(currentItem)) {
                    value -= 3
                }
            }
            delay = delay.coerceAtLeast((1000 / value).toLong())
        }
        return delay
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        inline get() = mc.thePlayer!!.spectator || !isAlive(mc.thePlayer!!)
                || LiquidBounce.moduleManager[Blink::class.java].state

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: IEntityLivingBase) = entity.asEntityLivingBase().entityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 3


    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer!!.heldItem != null && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(rangeValue.get(), throughWallsRangeValue.get())

    private fun getRange(entity: IEntity) =
        (if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= throughWallsRangeValue.get()) rangeValue.get() else throughWallsRangeValue.get()) - if (mc.thePlayer!!.sprinting) rangeSprintReducementValue.get() else 0F

    /**
     * HUD Tag
     */
    override val tag: String
        get() = targetModeValue.get()
}