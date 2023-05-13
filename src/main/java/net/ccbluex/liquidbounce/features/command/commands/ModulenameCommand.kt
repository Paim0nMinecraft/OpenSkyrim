package net.ccbluex.liquidbounce.features.command.commands


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType

class ModulenameCommand : Command("modulerename") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 2) {
            // Get module by name
            val module = LiquidBounce.moduleManager.getModule(args[1])

            if (module == null) {
                chat("Module §a§l" + args[1] + "§3 not found.")
                return
            }
            var i = 2
            var name = ""
            while (i < args.size) {
                if (i < args.size - 1) {
                    name += args[i] + " "
                    i++
                } else {
                    name += args[i]
                    i++
                }
            }
            // Response to user
            module.name = name
            LiquidBounce.hud.addNotification(
                Notification(
                    "ModuleRename",
                    "Change module's name ${module.name} to ${name}.",
                    NotifyType.INFO
                )
            )

            return
        }

        chatSyntax(arrayOf("<module> <name>", "<module> none"))
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> LiquidBounce.moduleManager.modules
                .map { it.name }
                .filter { it.startsWith(moduleName, true) }
                .toList()

            else -> emptyList()
        }
    }
}