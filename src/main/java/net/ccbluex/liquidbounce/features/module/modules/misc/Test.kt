package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader
import java.awt.Color

/**
@author ChengFeng
@since 2022/12/1
 */

@ModuleInfo(name = "Test", description = "awa", category = ModuleCategory.MISC)
class Test : Module() {
    @EventTarget
    fun on2D(e: Render2DEvent) {
        val shader = GlowShader.GLOW_SHADER
        shader.startDraw(e.partialTicks)

        RenderUtils.drawRect(50, 50, 100, 100, Color(Integer.MAX_VALUE).rgb)

        val radius = 2.5f
        shader.stopDraw(Color(Integer.MAX_VALUE), radius, 1f)
    }
}