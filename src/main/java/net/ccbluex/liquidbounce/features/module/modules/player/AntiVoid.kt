/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketPlayerPosLook

@ModuleInfo(name = "AntiVoid", category = ModuleCategory.PLAYER, description = "null")
class AntiVoid : Module() {
    private val modeValue = ListValue(
        "Mode",
        arrayOf(
            "Blink",
            "TPBack",
            "MotionFlag",
            "PacketFlag",
            "GroundSpoof",
            "OldHypixel",
            "Jartex",
            "OldCubecraft",
            "Packet"
        ),
        "Blink"
    )
    private val maxFallDistValue = FloatValue("MaxFallDistance", 10F, 5F, 20F)
    private val resetMotionValue = BoolValue("ResetMotion", false)
    private val startFallDistValue = FloatValue("BlinkStartFallDistance", 2F, 0F, 5F)
    private val autoScaffoldValue = BoolValue("BlinkAutoScaffold", true)
    private val motionflagValue = FloatValue("MotionFlag-MotionY", 1.0F, 0.0F, 5.0F)
    private val voidOnlyValue = BoolValue("OnlyVoid", true)

    private val packetCache = ArrayList<CPacketPlayer>()
    private var blink = false
    private var canBlink = false
    private var canCancel = false
    private var canSpoof = false
    private var tried = false
    private var flagged = false

    private var posX = 0.0
    private var posY = 0.0
    private var posZ = 0.0
    private var motionX = 0.0
    private var motionY = 0.0
    private var motionZ = 0.0
    private var lastRecY = 0.0

    override fun onEnable() {
        canCancel = false
        blink = false
        canBlink = false
        canSpoof = false
        if (mc.thePlayer != null) {
            lastRecY = mc.thePlayer!!.posY
        } else {
            lastRecY = 0.0
        }
        tried = false
        flagged = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        if (lastRecY == 0.0) {
            lastRecY = mc.thePlayer!!.posY
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.onGround) {
            tried = false
            flagged = false
        }

        when (modeValue.get().toLowerCase()) {
            "groundspoof" -> {
                if (!voidOnlyValue.get() || checkVoid()) {
                    canSpoof = mc.thePlayer!!.fallDistance > maxFallDistValue.get()
                }
            }

            "motionflag" -> {
                if (!voidOnlyValue.get() || checkVoid()) {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get() && !tried) {
                        mc.thePlayer!!.motionY += motionflagValue.get()
                        mc.thePlayer!!.fallDistance = 0.0F
                        tried = true
                    }
                }
            }

            "packetflag" -> {
                if (!voidOnlyValue.get() || checkVoid()) {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get() && !tried) {
                        mc2.connection!!.sendPacket(
                            CPacketPlayer.Position(
                                mc.thePlayer!!.posX + 1,
                                mc.thePlayer!!.posY + 1,
                                mc.thePlayer!!.posZ + 1,
                                false
                            )
                        )
                        tried = true
                    }
                }
            }

            "tpback" -> {
                if (mc.thePlayer!!.onGround && BlockUtils.getBlock(
                        WBlockPos(
                            mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY - 1.0,
                            mc.thePlayer!!.posZ
                        )
                    ) !is BlockAir
                ) {
                    posX = mc.thePlayer!!.prevPosX
                    posY = mc.thePlayer!!.prevPosY
                    posZ = mc.thePlayer!!.prevPosZ
                }
                if (!voidOnlyValue.get() || checkVoid()) {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get() && !tried) {
                        mc.thePlayer!!.setPositionAndUpdate(posX, posY, posZ)
                        mc.thePlayer!!.fallDistance = 0F
                        mc.thePlayer!!.motionX = 0.0
                        mc.thePlayer!!.motionY = 0.0
                        mc.thePlayer!!.motionZ = 0.0
                        tried = true
                    }
                }
            }

            "jartex" -> {
                canSpoof = false
                if (!voidOnlyValue.get() || checkVoid()) {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get() && mc.thePlayer!!.posY < lastRecY + 0.01 && mc.thePlayer!!.motionY <= 0 && !mc.thePlayer!!.onGround && !flagged) {
                        mc.thePlayer!!.motionY = 0.0
                        mc.thePlayer!!.motionZ *= 0.838
                        mc.thePlayer!!.motionX *= 0.838
                        canSpoof = true
                    }
                }
                lastRecY = mc.thePlayer!!.posY
            }

            "oldcubecraft" -> {
                canSpoof = false
                if (!voidOnlyValue.get() || checkVoid()) {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get() && mc.thePlayer!!.posY < lastRecY + 0.01 && mc.thePlayer!!.motionY <= 0 && !mc.thePlayer!!.onGround && !flagged) {
                        mc.thePlayer!!.motionY = 0.0
                        mc.thePlayer!!.motionZ = 0.0
                        mc.thePlayer!!.motionX = 0.0
                        mc.thePlayer!!.jumpMovementFactor = 0.00f
                        canSpoof = true
                        if (!tried) {
                            tried = true
                            mc2.connection!!.sendPacket(
                                CPacketPlayer.Position(
                                    mc.thePlayer!!.posX,
                                    (32000.0).toDouble(),
                                    mc.thePlayer!!.posZ,
                                    false
                                )
                            )
                        }
                    }
                }
                lastRecY = mc.thePlayer!!.posY
            }

            "packet" -> {
                if (checkVoid()) {
                    canCancel = true
                }

                if (canCancel) {
                    if (mc.thePlayer!!.onGround) {
                        for (packet in packetCache) {
                            mc2.connection!!.sendPacket(packet)
                        }
                        packetCache.clear()
                    }
                    canCancel = false
                }
            }

            "blink" -> {
                if (!blink) {
                    val collide = FallingPlayer(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY,
                        mc.thePlayer!!.posZ,
                        0.0,
                        0.0,
                        0.0,
                        0F,
                        0F,
                        0F
                    ).findCollision(60)
                    if (canBlink && (collide == null || (mc.thePlayer!!.posY - collide.pos.y) > startFallDistValue.get())) {
                        posX = mc.thePlayer!!.posX
                        posY = mc.thePlayer!!.posY
                        posZ = mc.thePlayer!!.posZ
                        motionX = mc.thePlayer!!.motionX
                        motionY = mc.thePlayer!!.motionY
                        motionZ = mc.thePlayer!!.motionZ

                        packetCache.clear()
                        blink = true
                    }

                    if (mc.thePlayer!!.onGround) {
                        canBlink = true
                    }
                } else {
                    if (mc.thePlayer!!.fallDistance > maxFallDistValue.get()) {
                        mc.thePlayer!!.setPositionAndUpdate(posX, posY, posZ)
                        if (resetMotionValue.get()) {
                            mc.thePlayer!!.motionX = 0.0
                            mc.thePlayer!!.motionY = 0.0
                            mc.thePlayer!!.motionZ = 0.0
                            mc.thePlayer!!.jumpMovementFactor = 0.00f
                        } else {
                            mc.thePlayer!!.motionX = motionX
                            mc.thePlayer!!.motionY = motionY
                            mc.thePlayer!!.motionZ = motionZ
                            mc.thePlayer!!.jumpMovementFactor = 0.00f
                        }

                        if (autoScaffoldValue.get()) {
                            LiquidBounce.moduleManager[Scaffold::class.java].state = true
                        }

                        packetCache.clear()
                        blink = false
                        canBlink = false
                    } else if (mc.thePlayer!!.onGround) {
                        blink = false

                        for (packet in packetCache) {
                            mc2.connection!!.sendPacket(packet)
                        }
                    }
                }
            }
        }
    }

    private fun checkVoid(): Boolean {
        var i = (-(mc.thePlayer!!.posY - 1.4857625)).toInt()
        var dangerous = true
        while (i <= 0) {
            dangerous = mc.theWorld!!.getCollisionBoxes(
                mc.thePlayer!!.entityBoundingBox.offset(
                    mc.thePlayer!!.motionX * 0.5,
                    i.toDouble(),
                    mc.thePlayer!!.motionZ * 0.5
                )
            ).isEmpty()
            i++
            if (!dangerous) break
        }
        return dangerous
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().toLowerCase()) {
            "blink" -> {
                if (blink && (packet is CPacketPlayer)) {
                    packetCache.add(packet)
                    event.cancelEvent()
                }
            }

            "packet" -> {
                if (canCancel && (packet is CPacketPlayer)) {
                    packetCache.add(packet)
                    event.cancelEvent()
                }

                if (packet is SPacketPlayerPosLook) {
                    packetCache.clear()
                    canCancel = false
                }
            }

            "groundspoof" -> {
                if (canSpoof && (packet is CPacketPlayer)) {
                    packet.onGround = true
                }
            }

            "jartex" -> {
                if (canSpoof && (packet is CPacketPlayer)) {
                    packet.onGround = true
                }
                if (canSpoof && (packet is SPacketPlayerPosLook)) {
                    flagged = true
                }
            }

            "oldcubecraft" -> {
                if (canSpoof && (packet is CPacketPlayer)) {
                    if (packet.y < 1145.141919810) event.cancelEvent()
                }
                if (canSpoof && (packet is SPacketPlayerPosLook)) {
                    flagged = true
                }
            }

            "oldhypixel" -> {
                if (packet is SPacketPlayerPosLook && mc.thePlayer!!.fallDistance > 3.125) mc.thePlayer!!.fallDistance =
                    3.125f

                if (packet is CPacketPlayer) {
                    if (voidOnlyValue.get() && mc.thePlayer!!.fallDistance >= maxFallDistValue.get() && mc.thePlayer!!.motionY <= 0 && checkVoid()) {
                        packet.y += 11.0
                    }
                    if (!voidOnlyValue.get() && mc.thePlayer!!.fallDistance >= maxFallDistValue.get()) packet.y += 11.0
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}
