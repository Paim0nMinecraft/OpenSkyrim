package net.ccbluex.liquidbounce.management

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.timer.MSTimer

class CombatManager : Listenable, MinecraftInstance() {
    private val lastAttackTimer = MSTimer()
    var kills = 0
    var inCombat = false
        private set
    var target: IEntityLivingBase? = null
        private set
    val attackedEntityList = mutableListOf<IEntityLivingBase>()
    val focusedPlayerList = mutableListOf<IEntityPlayer>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        //MovementUtils.updateBlocksPerSecond()

        // bypass java.util.ConcurrentModificationException
        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                LiquidBounce.eventManager.callEvent(EntityKilledEvent(it))
                kills++
                attackedEntityList.remove(it)
            }
        }

        inCombat = false

        if (!lastAttackTimer.hasTimePassed(1000)) {
            inCombat = true
            return
        }

        if (target != null) {
            if (mc.thePlayer!!.getDistanceToEntity(target!!) > 7 || !inCombat || target!!.isDead) {
                target = null
            } else {
                inCombat = true
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity

        if (classProvider.isEntityLivingBase(target) && EntityUtils.isSelected(target, true)) {
            this.target = target as IEntityLivingBase?
            if (!attackedEntityList.contains(target!!)) {
                attackedEntityList.add(target)
            }
        }
        lastAttackTimer.reset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        inCombat = false
        target = null
        attackedEntityList.clear()
        focusedPlayerList.clear()
    }

//    @EventTarget
//    fun onPacket(event: PacketEvent) {
//        val packet = event.packet
//        if(packet is S02PacketChat) {
//            val raw = packet.chatComponent.unformattedText
//            val found = hackerWords.filter { raw.contains(it, true) }
//            if(raw.contains(mc.session.username, true) && found.isNotEmpty()) {
//                LiquidBounce.hud.addNotification(Notification("Someone call you a hacker!", found.joinToString(", "), NotifyType.ERROR))
//            }
//        }
//    }

    fun getNearByEntity(radius: Float): IEntityLivingBase? {
        return try {
            mc.theWorld!!.loadedEntityList
                .filter { mc.thePlayer!!.getDistanceToEntity(it) < radius && EntityUtils.isSelected(it, true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer!!) }[0] as IEntityLivingBase?
        } catch (e: Exception) {
            null
        }
    }

    fun isFocusEntity(entity: IEntityPlayer): Boolean {
        if (focusedPlayerList.isEmpty()) {
            return true // no need 2 focus
        }

        return focusedPlayerList.contains(entity)
    }

    override fun handleEvents() = true
}