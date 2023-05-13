package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventState
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.SlowDownEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.render.Animations
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.render.BlockAnimationUtils.thePlayerisBlocking
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketHeldItemChange

@ModuleInfo(
    name = "NoSlowDown", description = "取消灵魂沙和使用物品时造成的减速效果",
    category = ModuleCategory.MOVEMENT
)
class NoSlow : Module() {
    private val msTimer = MSTimer()
    private val modeValue = ListValue(
        "PacketMode",
        arrayOf("AntiCheat", "Custom", "NCP", "Vanilla", "AAC", "AAC5", "Grim", "Hyt-Vanilla"),
        "Vanilla"
    )
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround = BoolValue("CustomOnGround", false)
    private val customDelayValue = IntegerValue("CustomDelay", 60, 10, 200)

    // Soulsand
    val soulsandValue = BoolValue("Soulsand", false)

    var pendingFlagApplyPacket = false

    override fun onDisable() {
        msTimer.reset()
        pendingFlagApplyPacket = false
    }

    private fun sendPacket(
        event: MotionEvent,
        sendC07: Boolean,
        sendC08: Boolean,
        delay: Boolean,
        delayValue: Long,
        onGround: Boolean
    ) {
        val digging = classProvider.createCPacketPlayerDigging(
            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
            WBlockPos(-1, -1, -1),
            classProvider.getEnumFacing(EnumFacingType.DOWN)
        )
        val blockMain = createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND)
        val blockOFF = createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.OFF_HAND)
        if (onGround && !mc.thePlayer!!.onGround) {
            return
        }
        if (sendC07 && event.eventState == EventState.PRE) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && event.eventState == EventState.POST) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                if (LiquidBounce.moduleManager[Animations::class.java].state) {
                    mc.netHandler.addToSendQueue(blockOFF)
                } else {
                    mc.netHandler.addToSendQueue(blockMain)
                    mc.netHandler.addToSendQueue(blockOFF)
                }
                msTimer.reset()
            } else if (!delay) {
                if (LiquidBounce.moduleManager[Animations::class.java].state) {
                    mc.netHandler.addToSendQueue(blockOFF)
                } else {
                    mc.netHandler.addToSendQueue(blockMain)
                    mc.netHandler.addToSendQueue(blockOFF)
                }
            }
        }
    }

    val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura

    fun isBlock(): Boolean {
        return thePlayerisBlocking || killAura.blockingStatus
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!MovementUtils.isMoving) {
            return
        }
        when (modeValue.get().toLowerCase()) {
            "grim" -> {
                if ((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && mc.thePlayer!!.isBlocking || thePlayerisBlocking || killAura.blockingStatus) {
                    if (mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1) {
                        val curSlot = mc.thePlayer!!.inventory.currentItem
                        val spoof = if (curSlot == 0) 1 else -1
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(curSlot + spoof))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(curSlot))
                    }
                }
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                }
                val item = mc.thePlayer!!.heldItem?.item

                if (event.eventState == EventState.PRE && classProvider.isItemFood(item) || classProvider.isItemPotion(
                        item
                    ) || classProvider.isItemBucketMilk(item)
                ) {
                    val curSlot = mc.thePlayer!!.inventory.currentItem
                    val spoof = if (curSlot == 0) 1 else -1
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot + spoof))
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot))
                }
            }

            "hyt-vanilla" -> {
                val item = mc.thePlayer!!.heldItem?.item
                if (event.eventState == EventState.PRE && classProvider.isItemFood(item) || classProvider.isItemPotion(
                        item
                    ) || classProvider.isItemBucketMilk(item)
                ) {
                    val curSlot = mc.thePlayer!!.inventory.currentItem
                    val spoof = if (curSlot == 0) 1 else -1
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot + spoof))
                    PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(curSlot))
                }
            }

            "anticheat" -> {
                this.sendPacket(event, true, false, false, 0, false)
                if (mc.thePlayer!!.ticksExisted % 2 == 0) {
                    this.sendPacket(event, false, true, false, 0, false)
                }
            }

            "aac" -> {
                if (mc.thePlayer!!.ticksExisted % 3 == 0) {
                    sendPacket(event, true, false, false, 0, false)
                } else {
                    sendPacket(event, false, true, false, 0, false)
                }
            }

            "aac5" -> {
                if (event.eventState == EventState.POST && (mc.thePlayer!!.isUsingItem || mc.thePlayer!!.isBlocking || killAura.blockingStatus)) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerBlockPlacement(
                            WBlockPos(-1, -1, -1),
                            255,
                            mc.thePlayer!!.inventory.getCurrentItemInHand(),
                            0f,
                            0f,
                            0f
                        )
                    )
                }
            }

            "custom" -> {
                sendPacket(event, true, true, true, customDelayValue.get().toLong(), customOnGround.get())
            }

            "ncp" -> {
                sendPacket(event, true, true, false, 0, false)
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }

            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }

            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }

            else -> 0.2F
        }
    }
}