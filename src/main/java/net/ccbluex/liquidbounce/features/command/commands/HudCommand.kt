package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.Config
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

/**
@author ChengFeng
@since 2022/11/28
 */
class HudCommand : Command("hudconfig") {
    override fun execute(args: Array<String>) {
        if (args.size >= 2) {
            val command = args[1]
            val dir = LiquidBounce.fileManager.hudsDir!!

            when (command) {
                "list" -> {
                    chat("Huds :")
                    for (listFile in dir.listFiles()) {
                        chat(listFile.name)
                    }
                }

                "delete" -> {
                    if (args.size == 3) {
                        if (File(LiquidBounce.fileManager.configsDir, args[2]).exists()) {
                            try {
                                FileUtils.forceDelete(File(LiquidBounce.fileManager.hudsDir, args[2]))
                                chat("Deleted hud: " + args[2])
                            } catch (e: Exception) {
                                chat("Failed to delete hud: " + args[2])
                            }

                        } else {
                            chat("Hud " + args[2] + " not found.")
                        }
                    } else {
                        chatSyntax("hudconfig < list / save <name> / load <name> / delete <name> >")
                    }
                }

                "load" -> {
                    if (args.size == 3) {

                        if (File(LiquidBounce.fileManager.configsDir, args[2]).exists()) {

                            LiquidBounce.hud.clearElements()
                            LiquidBounce.hud = Config(
                                FileUtils.readFileToString(
                                    File(
                                        LiquidBounce.fileManager.hudsDir,
                                        args[2]
                                    )
                                )
                            ).toHUD()

                            chat("Loaded hud: " + args[2])
                        } else {
                            chat("Hud " + args[2] + " not found.")
                        }

                    } else {
                        chatSyntax("hudconfig < list / save <name> / load <name> / delete <name> >")
                    }
                }

                "save" -> {
                    if (args.size == 3) {
                        val printWriter = PrintWriter(FileWriter(File(LiquidBounce.fileManager.hudsDir, args[2])))
                        printWriter.println(Config(LiquidBounce.hud).toJson())
                        printWriter.close()
                        chat("Saved hud " + args[2])
                    } else {
                        chatSyntax("hudconfig < list / save <name> / load <name> / delete <name> >")
                    }
                }
            }
        } else {
            chatSyntax("hudconfig < list / save <name> / load <name> / delete <name> >")
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> arrayOf("list", "save", "load", "delete").toList()
            2 -> {
                val array = ArrayList<String>()
                for (listFile in LiquidBounce.fileManager.hudsDir!!.listFiles()) {
                    array.add(listFile.name)
                }
                array
            }

            else -> emptyList()
        }
    }
}