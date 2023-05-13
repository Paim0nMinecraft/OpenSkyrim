package cc.paimon.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue


/**
 *
 * Skid by Paimon.
 * @Date 2023/1/23
 */
//你好我是无敌雨露我回来了我拉的无敌参数殴打你
@ModuleInfo(name="AutoRange", description = "Fix Killaura AirBan", category = ModuleCategory.COMBAT)
class Helper : Module() {
    private val betterAuraValue = BoolValue("BetterAura",false)
    private val hurttime = IntegerValue("BetterAura-HurtTime1", 9, 1, 10).displayable { betterAuraValue.get() }
    private val hurttime2 = IntegerValue("BetterAura-HurtTime2", 10, 1, 10).displayable { betterAuraValue.get() }
    private val AirRange = FloatValue("BetterAura-AirRange", 3.0f, 0.0f, 5.0f).displayable { betterAuraValue.get() }
    private val GroundRange = FloatValue("BetterAura-GroundRange", 3.5f, 0.0f, 5.0f).displayable { betterAuraValue.get() }
    private val Debug = BoolValue("Debug", true)
    private var ticks = 0
    fun debug(s: String, force: Boolean = false) {
        if (Debug.get() || force)
            ClientUtils.displayChatMessage("§7[§3§6Helper§7]§f $s")
    }
    @EventTarget
    fun onUpdate(event : UpdateEvent){
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        if(mc.thePlayer == null) return
        if(betterAuraValue.get() && killAura.state){
            if(mc2.player.onGround && killAura.rangeValue.get() != GroundRange.get()){
                killAura.rangeValue.set(this.GroundRange.get())
                debug("NewRange: "+GroundRange.get())
            }else
                if(!mc2.player.onGround && killAura.rangeValue.get() != AirRange.get()){
                    killAura.rangeValue.set(this.AirRange.get())
                    debug("NewRange: "+AirRange.get())
                }
            ticks ++
            if(ticks == 1 && killAura.hurtTimeValue.get() != hurttime.get()){
                killAura.hurtTimeValue.set(this.hurttime.get())
                debug("NewHurtTime: "+hurttime.get())
            }
            if(ticks == 2&& killAura.hurtTimeValue.get() != hurttime2.get()){
                killAura.hurtTimeValue.set(hurttime2.get())
                debug("NewHurtTime: "+hurttime2.get())
            }
            if(ticks == 3) ticks == 0
        }

    }
}