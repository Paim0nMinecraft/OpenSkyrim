package net.ccbluex.liquidbounce.features.command.commands

import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.AutoReconnect.delay
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.altgenerator.GuiTheAltening.Companion.apiKey
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.value.Value
import org.apache.commons.io.FileUtils
import java.io.*
import java.util.function.Consumer

/**
@author ChengFeng
@since 2022/11/26
 */
class ConfigCommand : Command("config") {
    override fun execute(args: Array<String>) {
        if (args.size >= 2) {
            val command = args[1]
            val dir = LiquidBounce.fileManager.configsDir!!

            when (command) {
                "list" -> {
                    chat("Configs :")
                    for (listFile in dir.listFiles()) {
                        chat(listFile.name)
                    }
                }

                "delete" -> {
                    if (args.size == 3) {
                        if (File(LiquidBounce.fileManager.configsDir, args[2]).exists()) {
                            try {
                                FileUtils.forceDelete(File(LiquidBounce.fileManager.configsDir, args[2]))
                                chat("Deleted config: " + args[2])
                            } catch (e: Exception) {
                                chat("Failed to delete config: " + args[2])
                            }

                        } else {
                            chat("Config " + args[2] + " not found.")
                        }
                    } else {
                        chatSyntax(".config < list / save <name> / load <name> / delete <name> >")
                    }
                }

                "load" -> {
                    if (args.size == 3) {
                        try {

                            val jsonElement = JsonParser().parse(
                                BufferedReader(
                                    FileReader(
                                        File(
                                            LiquidBounce.fileManager.configsDir,
                                            args[2]
                                        )
                                    )
                                )
                            )

                            if (jsonElement is JsonNull) {
                                chatSyntax("Config " + args[2] + " not found.")
                                return
                            }

                            val jsonObject = jsonElement as JsonObject

                            for (set in jsonObject.entrySet()) {
                                val key = set.key
                                val value = set.value
                                if (key.equals("CommandPrefix", ignoreCase = true)) {
                                    LiquidBounce.commandManager.prefix = value.asCharacter
                                } else if (key.equals("targets", ignoreCase = true)) {
                                    val jsonValue = value as JsonObject
                                    if (jsonValue.has("TargetPlayer")) EntityUtils.targetPlayer =
                                        jsonValue["TargetPlayer"].asBoolean
                                    if (jsonValue.has("TargetMobs")) EntityUtils.targetMobs =
                                        jsonValue["TargetMobs"].asBoolean
                                    if (jsonValue.has("TargetAnimals")) EntityUtils.targetAnimals =
                                        jsonValue["TargetAnimals"].asBoolean
                                    if (jsonValue.has("TargetInvisible")) EntityUtils.targetInvisible =
                                        jsonValue["TargetInvisible"].asBoolean
                                    if (jsonValue.has("TargetDead")) EntityUtils.targetDead =
                                        jsonValue["TargetDead"].asBoolean
                                } else if (key.equals("features", ignoreCase = true)) {
                                    val jsonValue = value as JsonObject
                                    if (jsonValue.has("AntiForge")) AntiForge.enabled = jsonValue["AntiForge"].asBoolean
                                    if (jsonValue.has("AntiForgeFML")) AntiForge.blockFML =
                                        jsonValue["AntiForgeFML"].asBoolean
                                    if (jsonValue.has("AntiForgeProxy")) AntiForge.blockProxyPacket =
                                        jsonValue["AntiForgeProxy"].asBoolean
                                    if (jsonValue.has("AntiForgePayloads")) AntiForge.blockPayloadPackets =
                                        jsonValue["AntiForgePayloads"].asBoolean
                                    if (jsonValue.has("BungeeSpoof")) BungeeCordSpoof.enabled =
                                        jsonValue["BungeeSpoof"].asBoolean
                                    if (jsonValue.has("AutoReconnectDelay")) delay =
                                        jsonValue["AutoReconnectDelay"].asInt
                                } else if (key.equals("thealtening", ignoreCase = true)) {
                                    val jsonValue = value as JsonObject
                                    if (jsonValue.has("API-Key")) apiKey = jsonValue["API-Key"].asString
                                } else {
                                    val module = LiquidBounce.moduleManager.getModule(key)
                                    if (module != null) {
                                        val jsonModule = value as JsonObject
                                        module.state = jsonModule["State"].asBoolean
                                        module.keyBind = jsonModule["KeyBind"].asInt
                                        if (jsonModule.has("Array")) module.array = jsonModule["Array"].asBoolean
                                        for (moduleValue in module.values) {
                                            val element = jsonModule[moduleValue.name]
                                            if (element != null) moduleValue.fromJson(element)
                                        }
                                    }
                                }
                            }

                            chat("Loaded config: " + args[2])
                        } catch (e: Throwable) {
                            chat("Failed to load config: " + args[2])
                        }
                    } else {
                        chatSyntax(".config < list / save <name> / load <name> / delete <name> >")
                    }
                }

                "save" -> {
                    if (args.size == 3) {
                        val jsonObject = JsonObject()

                        jsonObject.addProperty("CommandPrefix", LiquidBounce.commandManager.prefix)

                        val jsonTargets = JsonObject()
                        jsonTargets.addProperty("TargetPlayer", EntityUtils.targetPlayer)
                        jsonTargets.addProperty("TargetMobs", EntityUtils.targetMobs)
                        jsonTargets.addProperty("TargetAnimals", EntityUtils.targetAnimals)
                        jsonTargets.addProperty("TargetInvisible", EntityUtils.targetInvisible)
                        jsonTargets.addProperty("TargetDead", EntityUtils.targetDead)
                        jsonObject.add("targets", jsonTargets)

                        val jsonFeatures = JsonObject()
                        jsonFeatures.addProperty("AntiForge", AntiForge.enabled)
                        jsonFeatures.addProperty("AntiForgeFML", AntiForge.blockFML)
                        jsonFeatures.addProperty("AntiForgeProxy", AntiForge.blockProxyPacket)
                        jsonFeatures.addProperty("AntiForgePayloads", AntiForge.blockPayloadPackets)
                        jsonFeatures.addProperty("BungeeSpoof", BungeeCordSpoof.enabled)
                        jsonFeatures.addProperty("AutoReconnectDelay", delay)
                        jsonObject.add("features", jsonFeatures)

                        val theAlteningObject = JsonObject()
                        theAlteningObject.addProperty("API-Key", apiKey)
                        jsonObject.add("thealtening", theAlteningObject)

                        LiquidBounce.moduleManager.modules.stream()
                            .forEach { module: Module ->
                                val jsonModule = JsonObject()
                                jsonModule.addProperty("State", module.state)
                                jsonModule.addProperty("KeyBind", module.keyBind)
                                jsonModule.addProperty("Array", module.array)
                                module.values.forEach(Consumer { value: Value<*> ->
                                    jsonModule.add(
                                        value.name,
                                        value.toJson()
                                    )
                                })
                                jsonObject.add(module.name, jsonModule)
                            }

                        val printWriter = PrintWriter(FileWriter(File(LiquidBounce.fileManager.configsDir, args[2])))
                        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
                        printWriter.close()
                        chat("Saved config " + args[2])
                    } else {
                        chatSyntax(".config < list / save <name> / load <name> / delete <name> >")
                    }
                }
            }
        } else {
            chatSyntax(".config < list / save <name> / load <name> / delete <name> >")
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> arrayOf("list", "save", "load", "delete").toList()
            2 -> {
                val array = ArrayList<String>()
                for (listFile in LiquidBounce.fileManager.configsDir!!.listFiles()) {
                    array.add(listFile.name)
                }

                array
            }

            else -> emptyList()
        }
    }
}