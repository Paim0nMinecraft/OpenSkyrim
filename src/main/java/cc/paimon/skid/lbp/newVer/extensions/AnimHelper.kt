package cc.paimon.skid.lbp.newVer.extensions


import cc.paimon.modules.render.NewGUI
import cc.paimon.utils.AnimationUtils2
import net.ccbluex.liquidbounce.utils.render.RenderUtils

fun Float.animSmooth(target: Float, speed: Float) = if (NewGUI.fastRenderValue.get()) target else AnimationUtils2.animate(target, this, speed * RenderUtils.deltaTime * 0.025F)
fun Float.animLinear(speed: Float, min: Float, max: Float) = if (NewGUI.fastRenderValue.get()) { if (speed < 0F) min else max } else (this + speed).coerceIn(min, max)