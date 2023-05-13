/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager

@ModuleInfo(name = "CapeManager", category = ModuleCategory.RENDER, canEnable = false, description = "e")
class CapeManager : Module() {
    override fun onEnable() {
        minecraft.displayGuiScreen(GuiCapeManager)
    }
}