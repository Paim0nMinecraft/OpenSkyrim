package net.ccbluex.liquidbounce.features.module.modules.misc

import cc.paimon.modules.combat.OldVelocity
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.AutoArmor
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.features.module.modules.world.ChestStealer
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.InfosUtils.Recorder
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketChat
import net.minecraft.network.play.server.SPacketOpenWindow
import java.util.*
import kotlin.concurrent.schedule

@ModuleInfo(name = "AutoPlay", category = ModuleCategory.PLAYER, description = "e")
class AutoPlay : Module() {
    private var clickState = 0
    private val modeValue =
        ListValue("Server", arrayOf("RedeSky", "Minemora", "HuaYuTingGG", "HuaYuTingSw", "HuaYuTing16"), "HuaYuTingGG")
    private val delayValue = IntegerValue("JoinDelay", 3, 0, 7)
    private val customTextValue = TextValue("CustomText", "Skyrim on TOP")
    private var clicking = false
    private var queued = false
    override fun onEnable() {
        clickState = 0
        clicking = false
        queued = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()

        when (modeValue.get().toLowerCase()) {
            "redesky" -> {
                if (clicking && (packet is CPacketClickWindow || packet is CPacketPlayerDigging)) {
                    event.cancelEvent()
                    return
                }
                if (clickState == 2 && packet is SPacketOpenWindow) {
                    event.cancelEvent()
                }
            }

            "hypixel" -> {
                if (clickState == 1 && packet is SPacketOpenWindow) {
                    event.cancelEvent()
                }
            }
        }

        val KillAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        val Velocity = LiquidBounce.moduleManager.getModule(OldVelocity::class.java) as OldVelocity
        val ChestStealer = LiquidBounce.moduleManager.getModule(ChestStealer::class.java) as ChestStealer
        val InventoryCleaner = LiquidBounce.moduleManager.getModule(InventoryCleaner::class.java) as InventoryCleaner
        val AutoArmor = LiquidBounce.moduleManager.getModule(AutoArmor::class.java) as AutoArmor
        if (packet is SPacketChat) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().toLowerCase()) {
                "minemora" -> {
                    if (text.contains("Has click en alguna de las siguientes opciones", true)) {
                        queueAutoPlay {
                            mc.thePlayer!!.sendChatMessage("/join")
                            Recorder.win++
                        }
                    }
                }

                "huayutinggg" -> {
                    if (text.contains("      喜欢      一般      不喜欢", true)) {
                        LiquidBounce.hud.addNotification(
                            Notification(
                                name,
                                "Sending you to the lobby..",
                                NotifyType.INFO
                            )
                        )
                        mc.thePlayer!!.sendChatMessage(LiquidBounce.CLIENT_NAME + " | GG | " + customTextValue)
                        KillAura.state = false
                        Velocity.state = false
                        LiquidBounce.hud.addNotification(
                            Notification(
                                "Warning",
                                "KillAura was disabled, because game has ended",
                                NotifyType.WARNING
                            )
                        )
                        Recorder.win++

                    }
                }

                "huayutingsw" -> {
                    if (text.contains("你现在是观察者状态. 按E打开菜单.", true)) {
                        LiquidBounce.hud.addNotification(
                            Notification(
                                name,
                                "Sending you to the lobby..",
                                NotifyType.INFO
                            )
                        )
                        LiquidBounce.hud.addNotification(
                            Notification(
                                "Warning",
                                "KillAura was disabled, because game has ended",
                                NotifyType.WARNING
                            )
                        )
                        LiquidBounce.hud.addNotification(
                            Notification(
                                "Warning",
                                "InventoryCleaner was disabled, because game has ended",
                                NotifyType.WARNING
                            )
                        )
                        mc.thePlayer!!.sendChatMessage(LiquidBounce.CLIENT_NAME + " | GG | " + customTextValue)
                        KillAura.state = false
                        Velocity.state = false
                        ChestStealer.state = false
                        InventoryCleaner.state = false
                        AutoArmor.state = false
                    }
                }

                "huayuting16" -> {
                    if (text.contains("[起床战争] Game 结束！感谢您的参与！", true)) {
                        LiquidBounce.hud.addNotification(
                            Notification(
                                name,
                                "Sending you to the lobby..",
                                NotifyType.INFO
                            )
                        )
                        LiquidBounce.hud.addNotification(
                            Notification(
                                "Warning",
                                "KillAura was disabled, because game has ended",
                                NotifyType.WARNING
                            )
                        )
                        mc.thePlayer!!.sendChatMessage(LiquidBounce.CLIENT_NAME + " | GG | " + customTextValue)

                        KillAura.state = false
                        Velocity.state = false
                        Recorder.win++
                    }
                }
            }
        }
    }

    private fun queueAutoPlay(runnable: () -> Unit) {
        if (queued) {
            return
        }
        queued = true
        if (this.state) {
            Timer().schedule(delayValue.get().toLong() * 1000) {
                if (state) {
                    runnable()
                }
            }

            //play sound when everything done
            LiquidBounce.hud.addNotification(
                Notification(
                    name,
                    "Sending you to next game in ${delayValue.get()}s...",
                    NotifyType.INFO
                )
            )
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clicking = false
        clickState = 0
        queued = false
    }

    override val tag: String
        get() = modeValue.get()

}
