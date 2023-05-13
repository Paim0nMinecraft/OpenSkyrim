package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(
    name = "AutoRespawn",
    description = "Automatically respawns you after dying.",
    category = ModuleCategory.PLAYER
)
class AutoRespawn : Module() {

    private val instantValue = BoolValue("Instant", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null)
            return

        if (if (instantValue.get()) thePlayer.health == 0F || thePlayer.isDead else classProvider.isGuiGameOver(mc.currentScreen)
                    && (mc.currentScreen!!.asGuiGameOver()).enableButtonsTimer >= 20
        ) {
            thePlayer.respawnPlayer()
            mc.displayGuiScreen(null)
        }
    }
}