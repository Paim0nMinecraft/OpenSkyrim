package cc.paimon.modules.hyt


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "HytGetName", description = "anti fake tips", category = ModuleCategory.HYT)
class HytGetName : Module() {

    private val mode = ListValue("GetNameMode", arrayOf("4V4/1V1", "32/64", "16V16"), "4V4/1V1")
    private val tips = BoolValue("Tips",true)

    override fun onDisable() {
        clearAll()
        super.onDisable()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat) {
            if(packet.chatComponent.unformattedText.contains("获得胜利!") || packet.chatComponent.unformattedText.contains("游戏开始 ...")){
                clearAll()
            }
            when (mode.get().toLowerCase()) {
                "4v4/1v1", "32/64" -> {
                    val matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(packet.chatComponent.unformattedText)
                    val matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?)死了!)))").matcher(packet.chatComponent.unformattedText)
                    if (matcher.find() && !packet.chatComponent.unformattedText.contains(": 起床战争>>") || !packet.chatComponent.unformattedText.contains(": 杀死了")) {
                        val name = matcher.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            if(tips.get())
                                ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eAdd Bot : " + name)
                            Thread {
                                try {
                                    Thread.sleep(6000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    if(tips.get())
                                        ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eRemoved Bot : " + name)
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                    if (matcher2.find() && !packet.chatComponent.unformattedText.contains(": 起床战争>>") || !packet.chatComponent.unformattedText.contains(": 杀死了")) {
                        val name = matcher2.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            if(tips.get())
                                ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eAdd Bot : " + name)
                            Thread {
                                try {
                                    Thread.sleep(6000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    if(tips.get())
                                        ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eRemoved Bot : " + name)
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                }
                "16v16" ->{
                    val matcher = Pattern.compile("击败了 (.*?)!").matcher(packet.chatComponent.unformattedText)
                    val matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(packet.chatComponent.unformattedText)
                    if (matcher.find() && !packet.chatComponent.unformattedText.contains(": 击败了") || !packet.chatComponent.unformattedText.contains(": 玩家 ")) {
                        val name = matcher.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            if(tips.get())
                                ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eAdd Bot : " + name)
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    if(tips.get())
                                        ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eRemoved Bot : " + name)
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                    if (matcher2.find() && !packet.chatComponent.unformattedText.contains(": 击败了") || !packet.chatComponent.unformattedText.contains(": 玩家 ")) {
                        val name = matcher2.group(1).trim()
                        if (name != "") {
                            LiquidBounce.fileManager.friendsConfig.addFriend(name)
                            ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eAdd Bot : " + name)
                            Thread {
                                try {
                                    Thread.sleep(10000)
                                    LiquidBounce.fileManager.friendsConfig.removeFriend(name)
                                    ClientUtils.displayChatMessage("§8[§c§lBotKiller§r§8] §eRemoved Bot : " + name)
                                } catch (ex: InterruptedException) {
                                    ex.printStackTrace()
                                }
                            }.start()
                        }
                    }
                }
            }
        }
    }
    @EventTarget
    fun onWorld(event: WorldEvent?) {
        clearAll()
    }
    private fun clearAll() {
        LiquidBounce.fileManager.friendsConfig.clearFriends()
    }
}