package cc.paimon.modules.combat


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.TimeHelper
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.server.SPacketPlayerPosLook

@ModuleInfo(name = "AntiVoid",  description = "GrimAC is the best", category = ModuleCategory.HYT)
class HytAntiVoid : Module() {
    private val pullbackTime = IntegerValue("PullbackTime", 850, 800, 1800)
    private val autoScaffoldValue = BoolValue("AutoScaffold", false)
    private val debug = BoolValue("Debug", false)
    var timer: TimeHelper = TimeHelper()
    var lastGroundPos = DoubleArray(3)
    var packets = ArrayList<IPacket>()

    @EventTarget
    open fun isInVoid(): Boolean {
        for (i in 0..128) {
            if (MovementUtils.isOnGround(i.toDouble())) {
                return false
            }
        }
        return true
    }
    @EventTarget
    fun onPacket(e: PacketEvent) {
        var packet = e.packet.unwrap()
        if (!LiquidBounce.moduleManager.get(Fly::class.java)!!.state && !LiquidBounce.moduleManager.get(Scaffold::class.java)!!.state) {
            if (!packets.isEmpty() && mc.thePlayer!!.ticksExisted < 100) packets.clear()
            if (packet is CPacketPlayer) {
                if (isInVoid()) {
                    e.cancelEvent()
                    packets.add(e.packet)
                    if (timer.delay(pullbackTime.get().toLong())) {
                        e.cancelEvent()
                    }
                } else {
                    lastGroundPos[0] = mc.thePlayer!!.posX
                    lastGroundPos[1] = mc.thePlayer!!.posY
                    lastGroundPos[2] = mc.thePlayer!!.posZ
                    if (!packets.isEmpty()) {
                        if (autoScaffoldValue.get()) {
                            LiquidBounce.moduleManager[Scaffold::class.java]!!.state = true
                        }
                        val var3: Iterator<*> = packets.iterator()
                        debug("[HytAntiVoid] Release Packets - " + packets.size)
                        while (var3.hasNext()) {
                            val p = var3.next() as CPacketPlayer
                            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(false))
                        }
                        packets.clear()
                    }
                    timer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onRevPacket(e: PacketEvent) {
        if (e.packet.unwrap() is SPacketPlayerPosLook && packets.size > 1) {
            debug("[AntiVoid] Pullbacks Detected, clear packets list!")
            packets.clear()
        }
    }

    private fun debug(str: String) {
        if (debug.get()) {
            ClientUtils.displayChatMessage(str)
        }
    }

    override val tag: String
        get() = pullbackTime.get().toString()
}