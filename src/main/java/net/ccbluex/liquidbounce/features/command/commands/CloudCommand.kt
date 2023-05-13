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
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.misc.HttpUtils
import net.ccbluex.liquidbounce.value.Value
import okhttp3.FormBody
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.function.Consumer


/**
@author ChengFeng
@since 2022/12/1
 */
class CloudCommand : Command("cloud") {

    companion object {
        private var web = HttpUtils.download("https://gitee.com/FengGod/liquidwing-cloud/raw/master/config-list")

        private var list = web.split(";")

        private fun startAutoLoad() {
            val t = Thread {
                while (true) {
                    web = HttpUtils.download("https://gitee.com/FengGod/liquidwing-cloud/raw/master/config-list")
                    list = web.split(";")
                    ClientUtils.getLogger().info("Updated cloud.")
                    Thread.sleep(5000)
                }
            }

            t.name = "Cloud-Thread"
            t.start()
        }

        private fun execCurl(commands: Array<String>): String? {
            val process = ProcessBuilder(*commands)
            val p: Process
            try {
                p = process.start()
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                val builder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                    builder.append(System.getProperty("line.separator"))
                }
                return builder.toString()
            } catch (e: IOException) {
                print("error")
                e.printStackTrace()
            }
            return null
        }

        val s = arrayOf(
            "curl",
            "-X",
            "POST",
            "--data-urlencode",
            "grant_type=password",
            "--data-urlencode",
            "username=masterlost",
            "--data-urlencode",
            "password=sakura0426",
            "--data-urlencode",
            "client_id=4e6fc4d7fa019a48cf0dbd69e8467363df74904f7076ab6ae1a082a40a49305d",
            "--data-urlencode",
            "client_secret=df384fd5a5c5eee7cfc2feb4857d10becf1775a0d22c9e447c8c53f7436daf18",
            "--data-urlencode",
            "scope=projects user_info issues notes",
            "https://gitee.com/oauth/token"
        )

        private val jsonObject: JsonObject = JsonParser().parse(execCurl(s)).asJsonObject

        val accessToken: String = jsonObject["access_token"].asString

    }

    init {
        startAutoLoad()
    }

    override fun execute(args: Array<String>) {

        if (args.size > 1) {
            when (args[1].toLowerCase()) {
                "list" -> {
                    chat("Cloud configs:")

                    for (s in list) {
                        chat(s)
                    }
                }

                "save" -> {
                    if (args.size == 3) {
                        Thread {
                            chat("Uploading config ${args[2]}...")

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

                            val body = FormBody.Builder().apply {
                                add("access_token", accessToken)
                                add("owner", "FengGod")
                                add("repo", "liquidwing-cloud")
                                add("path", "configs/" + args[2])
                                add(
                                    "content",
                                    Base64.getEncoder().encodeToString(
                                        Base64.getEncoder()
                                            .encodeToString(FileManager.PRETTY_GSON.toJson(jsonObject).toByteArray())
                                            .toByteArray()
                                    )
                                )
                                add("message", "Uploaded config by ${LiquidBounce.user}(${LiquidBounce.qq})")
                            }.build()


                            val json: JsonObject =
                                JsonParser().parse(HttpUtils.download("https://gitee.com/api/v5/repos/FengGod/liquidwing-cloud/contents/config-list?access_token=$accessToken")).asJsonObject
                            val sha = json["sha"].asString
                            val content = String(Base64.getDecoder().decode(json["content"].asString))

                            val body1 = FormBody.Builder().apply {
                                add("access_token", accessToken)
                                add("owner", "FengGod")
                                add("repo", "liquidwing-cloud")
                                add("path", "config-list")
                                add("sha", sha)
                                add(
                                    "content",
                                    Base64.getEncoder().encodeToString("${content};${args[2]}".toByteArray())
                                )
                                add("message", "Uploaded config by ${LiquidBounce.user}(${LiquidBounce.qq})")
                            }.build()

                            HttpUtils.post(
                                "https://gitee.com/api/v5/repos/FengGod/liquidwing-cloud/contents/configs%2F" + args[2],
                                body
                            )
                            HttpUtils.put(
                                "https://gitee.com/api/v5/repos/FengGod/liquidwing-cloud/contents/config-list",
                                body1
                            )

                            web =
                                HttpUtils.download("https://gitee.com/FengGod/liquidwing-cloud/raw/master/config-list")
                            list = web.split(";")

                            chat("Saved config " + args[2])

                        }.start()
                    } else {
                        chatSyntax("cloud save <name>")
                    }
                }

                "load" -> {
                    if (args.size == 3) {

                        val name = args[2]

                        if (list.contains(name)) {
                            Thread {
                                chat("Loading config $name...")

                                try {
                                    val jsonElement = JsonParser().parse(
                                        InputStreamReader(
                                            ByteArrayInputStream(
                                                String(
                                                    Base64.getDecoder().decode(
                                                        HttpUtils.download("https://gitee.com/FengGod/liquidwing-cloud/raw/master/configs/$name")
                                                    )
                                                )
                                                    .toByteArray()
                                            )
                                        )
                                    )

                                    if (jsonElement is JsonNull) return@Thread

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
                                            if (jsonValue.has("AntiForge")) AntiForge.enabled =
                                                jsonValue["AntiForge"].asBoolean
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
                                                if (jsonModule.has("Array")) module.array =
                                                    jsonModule["Array"].asBoolean
                                                for (moduleValue in module.values) {
                                                    val element = jsonModule[moduleValue.name]
                                                    if (element != null) moduleValue.fromJson(element)
                                                }
                                            }
                                        }
                                    }

                                    chat("Loaded cloud config $name.")
                                } catch (e: Throwable) {
                                    chat("Failed to load config $name .")
                                }

                            }.start()
                        } else chat("Cloud config $name not found.")
                    } else chatSyntax("cloud load <name>")
                }
            }
        } else {
            chatSyntax("cloud < list / load <name> / save<name> >")
        }
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> arrayListOf("list", "load")
            2 -> list
            else -> emptyList()
        }
    }
}