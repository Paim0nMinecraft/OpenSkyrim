package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "EnchantEffect", description = "Change Sword Color", category = ModuleCategory.RENDER)
class EnchantEffect : Module() {
    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 160, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val rainbow = BoolValue("RainBow", false)

    fun getRedValue(): IntegerValue {
        return colorRedValue
    }

    fun getRainbow(): BoolValue {
        return rainbow
    }

    fun getGreenValue(): IntegerValue {
        return colorGreenValue
    }

    fun getBlueValue(): IntegerValue {
        return colorBlueValue
    }

    fun getalphaValue(): IntegerValue {
        return alphaValue
    }
}