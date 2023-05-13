/*
 Skid from FDPClient
 */
package cc.paimon.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "NoAchievements",description="L", category = ModuleCategory.MISC, array = false)
class NoAchievements : Module() {
    @EventTarget
    fun onTick(event: TickEvent) {
//        StatisticsManager

    }
}
