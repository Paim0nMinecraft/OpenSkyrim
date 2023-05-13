package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

@ModuleInfo(
    name = "AutoTool",
    description = "Automatically selects the best tool in your inventory to mine a block.",
    category = ModuleCategory.PLAYER
)
class AutoTool : Module() {

    private val silentSwitch = BoolValue("Silent", false)
    private var backSlot = -1
    private var destroyTime = 0
    private var facing: EnumFacing? = null
    private var breakingBlockPos: BlockPos = BlockPos(0, 0, 0)

    private fun swapBack() {
        if (backSlot == -1) return
        mc2.connection!!.sendPacket(CPacketHeldItemChange(backSlot))

        backSlot = -1
        destroyTime = 0
    }

    @EventTarget
    fun onPacket(e: PacketEvent) {
        val packet = e.packet.unwrap()
        if (packet is CPacketPlayerDigging) {
            if (packet.action == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) switchSlot(packet.position)
            else if (packet.action == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || packet.action == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) swapBack()
            facing = packet.facing
        }
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        if (backSlot == -1) return
        if (destroyTime != 0) {
            --destroyTime
            return
        }
        mc2.world.setBlockState(breakingBlockPos, Blocks.AIR.defaultState, 11)
        PacketUtils.sendPacketNoEvent(
            CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                breakingBlockPos,
                facing!!
            )
        )
        swapBack()
    }

    fun switchSlot(blockPos: BlockPos) {
        var bestSpeed = 1F
        var bestSlot = -1

        val block = mc2.world.getBlockState(blockPos)

        for (i in 0..8) {
            val item = mc2.player.inventory.getStackInSlot(i) ?: continue
            val speed = item.getDestroySpeed(block)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }

        if (bestSlot != -1 && bestSlot != mc2.player.inventory.currentItem) {
            if (silentSwitch.get()) {
                val blockState = mc2.world.getBlockState(blockPos)
                val blockHardness = blockState.block.getBlockHardness(blockState, mc2.world, blockPos)
                val digSpeed = mc2.player.inventory.getStackInSlot(bestSlot).getDestroySpeed(block)
                destroyTime += ((blockHardness / digSpeed) * 30).toInt() + 1
                mc2.connection!!.sendPacket(CPacketHeldItemChange(bestSlot))
                backSlot = mc2.player.inventory.currentItem
                breakingBlockPos = blockPos
            } else mc2.player.inventory.currentItem = bestSlot
        }
    }

    override val tag: String
        get() = if (silentSwitch.get()) "Silent" else ""
}