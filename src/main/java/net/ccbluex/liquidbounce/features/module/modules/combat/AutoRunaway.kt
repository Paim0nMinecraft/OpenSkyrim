package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.features.module.modules.world.ChestStealer
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.ModuleUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.gui.inventory.GuiInventory

@ModuleInfo(
    name = "AutoRunaway",
    description = "Automatically makes you /hub whenever your health is low.",
    category = ModuleCategory.COMBAT
)
class AutoRunaway : Module() {
    var health = FloatValue("Health", 5F, 0F, 20F)
    var text = TextValue("Text", "/hub")
    var autoDisable = BoolValue("AutoDisable", true)
    var keepArmor = BoolValue("KeepArmor", true)

    var lmao = false

    private fun autoArmor(item: Int, isArmorSlot: Boolean) {
        if (item != -1) {
            val openInventory = mc.currentScreen !is GuiInventory
            if (openInventory) mc.netHandler.addToSendQueue(
                classProvider.createCPacketEntityAction(
                    mc.thePlayer!!,
                    ICPacketEntityAction.WAction.OPEN_INVENTORY
                )
            )
            mc.playerController.windowClick(
                mc.thePlayer!!.inventoryContainer.windowId,
                if (isArmorSlot) item else if (item < 9) item + 36 else item,
                0,
                1,
                mc.thePlayer!!
            )
            if (openInventory) mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer != null && mc.thePlayer!!.health < health.get()) {
            if (keepArmor.get()) {
                for (i in 0..3) {
                    val armorSlot = 3 - i
                    autoArmor(8 - armorSlot, true)
                }
            }

            if (mc.thePlayer!!.health <= health.get() && !lmao) {
                minecraft.player.sendChatMessage(text.get())
                lmao = true
            }

            if (mc.thePlayer!!.health <= health.get() && autoDisable.get()) {
                ModuleUtils.disableModules(
                    KillAura::class.java,
                    Speed::class.java,
                    Scaffold::class.java,
                    InventoryCleaner::class.java,
                    ChestStealer::class.java
                )
            }

            if (mc.thePlayer!!.health > health.get()) {
                lmao = false
            }
        }
    }
}