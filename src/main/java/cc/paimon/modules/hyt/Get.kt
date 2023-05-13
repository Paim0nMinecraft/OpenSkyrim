package cc.paimon.modules.hyt

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.minecraft.network.play.client.CPacketCustomPayload

@ModuleInfo(name = "Get", description = "Test", category = ModuleCategory.HYT)
class Get : Module() {

  @EventTarget
  fun onPacket(event : PacketEvent){
      val packet = event.packet.unwrap()
      if(packet is CPacketCustomPayload){
          ClientUtils.displayChatMessage(packet.bufferData.toString())
      }
  }
}
