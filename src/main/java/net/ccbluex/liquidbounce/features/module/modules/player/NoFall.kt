package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.api.enums.BlockType
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.ItemType
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3i
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.VecRotation
import net.ccbluex.liquidbounce.utils.block.BlockUtils.collideBlock
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.ceil
import kotlin.math.sqrt

@ModuleInfo(name = "NoFall", description = "Prevents you from taking fall damage.", category = ModuleCategory.PLAYER)
class NoFall : Module() {
    @JvmField
    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "AAC4",
            "SpoofGround",
            "NoGround",
            "Packet",
            "MLG",
            "AAC",
            "LAAC",
            "AAC3.3.11",
            "AAC3.3.15",
            "Spartan",
            "CubeCraft",
            "Hypixel",
            "HytTest"
        ),
        "SpoofGround"
    )
    private val minFallDistance = FloatValue("MinMLGHeight", 5f, 2f, 50f)
    private val spartanTimer = TickTimer()
    private val mlgTimer = TickTimer()
    private var currentState = 0
    private var jumped = false
    private var currentMlgRotation: VecRotation? = null
    private var currentMlgItemIndex = 0
    private var currentMlgBlock: WBlockPos? = null
    private var fakelag = false
    private var packetmodify = false
    private val packets = LinkedBlockingQueue<IPacket>()

    override fun onEnable() {
        if (modeValue.get().equals("AAC4", ignoreCase = true)) {
            fakelag = false
            packetmodify = false
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent?) {
        if (mc.thePlayer!!.onGround)
            jumped = false

        if (mc.thePlayer!!.motionY > 0)
            jumped = true

        if (!state)
            return

        if (collideBlock(mc.thePlayer!!.entityBoundingBox, classProvider::isBlockLiquid) ||
            collideBlock(
                classProvider.createAxisAlignedBB(
                    mc.thePlayer!!.entityBoundingBox.maxX,
                    mc.thePlayer!!.entityBoundingBox.maxY,
                    mc.thePlayer!!.entityBoundingBox.maxZ,
                    mc.thePlayer!!.entityBoundingBox.minX,
                    mc.thePlayer!!.entityBoundingBox.minY - 0.01,
                    mc.thePlayer!!.entityBoundingBox.minZ
                ), classProvider::isBlockLiquid
            )
        )
            return

        when (modeValue.get().toLowerCase()) {
            "packet" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                }
            }

            "cubecraft" -> if (mc.thePlayer!!.fallDistance > 2f) {
                mc.thePlayer!!.onGround = false
                mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketPlayer(true))
            }

            "aac" -> {
                if (mc.thePlayer!!.fallDistance > 2f) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
                    currentState = 2
                } else if (currentState == 2 && mc.thePlayer!!.fallDistance < 2) {
                    mc.thePlayer!!.motionY = 0.1
                    currentState = 3
                    return
                }
                when (currentState) {
                    3 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 4
                    }

                    4 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 5
                    }

                    5 -> {
                        mc.thePlayer!!.motionY = 0.1
                        currentState = 1
                    }
                }
            }

            "laac" -> if (!jumped && mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater
                && !mc.thePlayer!!.isInWeb
            ) mc.thePlayer!!.motionY = (-6).toDouble()

            "aac3.3.11" -> if (mc.thePlayer!!.fallDistance > 2) {
                mc.thePlayer!!.motionZ = 0.0
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionZ
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY - 10E-4, mc.thePlayer!!.posZ, mc.thePlayer!!.onGround
                    )
                )
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))
            }

            "aac3.3.15" -> if (mc.thePlayer!!.fallDistance > 2) {
                if (!mc.isIntegratedServerRunning) mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        Double.NaN,
                        mc.thePlayer!!.posZ,
                        false
                    )
                )
                mc.thePlayer!!.fallDistance = (-9999).toFloat()
            }

            "spartan" -> {
                spartanTimer.update()
//                ClientUtils.displayChatMessage(mc.thePlayer!!.fallDistance.toString())

                if (mc.thePlayer!!.fallDistance > 1.5 && spartanTimer.hasTimePassed(10)) {
//                    ClientUtils.displayChatMessage("111")
                    val thePlayer = mc.thePlayer ?: return
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand() as IItemStack))
                    spartanTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {

        if (mc.thePlayer == null) return

        if (mc.thePlayer!!.isInWater) {
            return
        }
        val packet = event.packet
        val mode = modeValue.get()
        if (mode.equals("AAC4", ignoreCase = true)) {
            if (classProvider.isCPacketPlayer(packet) && fakelag) {
                event.cancelEvent()
                if (packetmodify) {
                    (packet.asCPacketPlayer()).onGround = true
                    packetmodify = false
                }
                packets.add(packet)
            }
        }
        if (classProvider.isCPacketPlayer(packet)) {
            val playerPacket = packet.asCPacketPlayer()

            if (mode.equals("SpoofGround", ignoreCase = true)) playerPacket.onGround = true
            if (mode.equals("NoGround", ignoreCase = true)) playerPacket.onGround = false
            if (mode.equals("Hypixel", ignoreCase = true)
                && mc.thePlayer != null && mc.thePlayer!!.fallDistance > 1.5
            ) playerPacket.onGround = mc.thePlayer!!.ticksExisted % 2 == 0
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (collideBlock(
                mc.thePlayer!!.entityBoundingBox,
                classProvider::isBlockLiquid
            ) || collideBlock(
                classProvider.createAxisAlignedBB(
                    mc.thePlayer!!.entityBoundingBox.maxX,
                    mc.thePlayer!!.entityBoundingBox.maxY,
                    mc.thePlayer!!.entityBoundingBox.maxZ,
                    mc.thePlayer!!.entityBoundingBox.minX,
                    mc.thePlayer!!.entityBoundingBox.minY - 0.01,
                    mc.thePlayer!!.entityBoundingBox.minZ
                ), classProvider::isBlockLiquid
            )
        )
            return

        if (modeValue.get().equals("laac", ignoreCase = true)) {
            if (!jumped && !mc.thePlayer!!.onGround && !mc.thePlayer!!.isOnLadder && !mc.thePlayer!!.isInWater && !mc.thePlayer!!.isInWeb && mc.thePlayer!!.motionY < 0.0) {
                event.x = 0.0
                event.z = 0.0
            }
        }
    }

    @EventTarget
    private fun onMotionUpdate(event: MotionEvent) {
        if (mc.thePlayer!!.isInWater) {
            return
        }
        if (modeValue.get().equals("AAC4", ignoreCase = true)) {
            val eventState = event.eventState
            if (eventState === EventState.PRE) {
                if (!inVoid()) {
                    if (fakelag) {
                        fakelag = false
                        if (packets.size > 0) {
                            for (packet in packets) {
                                mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                            }
                            packets.clear()
                        }
                    }
                    return
                }
                if (mc.thePlayer!!.onGround && fakelag) {
                    fakelag = false
                    if (packets.size > 0) {
                        for (packet in packets) {
                            mc.thePlayer!!.sendQueue.addToSendQueue(packet)
                        }
                        packets.clear()
                    }
                    return
                }
                if (mc.thePlayer!!.fallDistance > 3 && fakelag) {
                    packetmodify = true
                    mc.thePlayer!!.fallDistance = 0f
                }
                if (inAir(4.0, 1.0)) {
                    return
                }
                if (!fakelag) {
                    fakelag = true
                }
            }
        }

        if (modeValue.get().equals("MLG", ignoreCase = true)) {
            if (event.eventState == EventState.PRE) {
                currentMlgRotation = null

                mlgTimer.update()

                if (!mlgTimer.hasTimePassed(10))
                    return

                if (mc.thePlayer!!.fallDistance > minFallDistance.get()) {
                    val fallingPlayer = FallingPlayer(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY,
                        mc.thePlayer!!.posZ,
                        mc.thePlayer!!.motionX,
                        mc.thePlayer!!.motionY,
                        mc.thePlayer!!.motionZ,
                        mc.thePlayer!!.rotationYaw,
                        mc.thePlayer!!.moveStrafing,
                        mc.thePlayer!!.moveForward
                    )

                    val maxDist: Double = mc.playerController.blockReachDistance + 1.5

                    val collision = fallingPlayer.findCollision(ceil(1.0 / mc.thePlayer!!.motionY * -maxDist).toInt())
                        ?: return

                    var ok: Boolean = WVec3(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + mc.thePlayer!!.eyeHeight,
                        mc.thePlayer!!.posZ
                    ).distanceTo(
                        WVec3(collision.pos).addVector(
                            0.5,
                            0.5,
                            0.5
                        )
                    ) < mc.playerController.blockReachDistance + sqrt(0.75)

                    if (mc.thePlayer!!.motionY < collision.pos.y + 1 - mc.thePlayer!!.posY) {
                        ok = true
                    }

                    if (!ok)
                        return

                    var index = -1

                    for (i in 36..44) {
                        val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack

                        if (itemStack != null && (itemStack.item == classProvider.getItemEnum(ItemType.WATER_BUCKET) || classProvider.isItemBlock(
                                itemStack.item
                            ) && (itemStack.item?.asItemBlock())?.block == classProvider.getBlockEnum(BlockType.WEB))
                        ) {
                            index = i - 36

                            if (mc.thePlayer!!.inventory.currentItem == index)
                                break
                        }
                    }
                    if (index == -1)
                        return

                    currentMlgItemIndex = index
                    currentMlgBlock = collision.pos

                    if (mc.thePlayer!!.inventory.currentItem != index) {
                        mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(index))
                    }

                    currentMlgRotation = RotationUtils.faceBlock(collision.pos)
                    currentMlgRotation!!.rotation.toPlayer(mc.thePlayer!!)
                }
            } else if (currentMlgRotation != null) {
                val stack = mc.thePlayer!!.inventory.getStackInSlot(currentMlgItemIndex + 36)

                if (classProvider.isItemBucket(stack!!.item)) {
                    mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, stack)
                } else {
                    val dirVec: WVec3i = classProvider.getEnumFacing(EnumFacingType.UP).directionVec

                    if (mc.playerController.sendUseItem(mc.thePlayer!!, mc.theWorld!!, stack)) {
                        mlgTimer.reset()
                    }
                }
                if (mc.thePlayer!!.inventory.currentItem != currentMlgItemIndex)
                    mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
            }
        }

    }

    fun isBlockUnder(): Boolean {
        if (mc.thePlayer!!.posY < 0.0) {
            return false
        } else {
            var off = 0
            while (true) {
                if (off >= mc.thePlayer!!.posY.toInt() + 2) {
                    return false
                }
                val bb: IAxisAlignedBB = mc.thePlayer!!.entityBoundingBox.offset(0.0, (-off).toDouble(), 0.0)
                if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                    return true
                }
                off += 2
            }
        }
    }

    fun getJumpEffect(): Int {
        return if (mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.JUMP))) mc.thePlayer!!.getActivePotionEffect(
            classProvider.getPotionEnum(PotionType.JUMP)
        )!!.amplifier + 1 else 0
    }

    fun inVoid(): Boolean {
        if (mc.thePlayer!!.posY < 0) {
            return false
        }
        var off = 0
        while (off < mc.thePlayer!!.posY + 2) {
            val bb: IAxisAlignedBB = classProvider.createAxisAlignedBB(
                mc.thePlayer!!.posX,
                mc.thePlayer!!.posY,
                mc.thePlayer!!.posZ,
                mc.thePlayer!!.posX,
                off.toDouble(),
                mc.thePlayer!!.posZ
            )
            if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                return true
            }
            off += 2
        }
        return false
    }

    fun inAir(height: Double, plus: Double): Boolean {
        if (mc.thePlayer!!.posY < 0) return false
        var off = 0
        while (off < height) {
            val bb: IAxisAlignedBB = classProvider.createAxisAlignedBB(
                mc.thePlayer!!.posX,
                mc.thePlayer!!.posY,
                mc.thePlayer!!.posZ,
                mc.thePlayer!!.posX,
                mc.thePlayer!!.posY - off,
                mc.thePlayer!!.posZ
            )
            if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, bb).isEmpty()) {
                return true
            }
            off += plus.toInt()
        }
        return false
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent?) {
        jumped = true
    }

    override val tag: String
        get() = modeValue.get()
}