package net.ccbluex.liquidbounce

import cc.paimon.ui.client.NewGuiWelcome
import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.management.CombatManager
import net.ccbluex.liquidbounce.management.MemoryManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.Display
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.File

object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "Skyrim"
    const val CLIENT_VERSION = "3.1.2"
    const val CLIENT_CREATOR = "CCBlueX & paimonqwq & xiatian233 & chengfeng"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    var mainMenuPrep = false
    var darkMode = false
    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    lateinit var user: String
    lateinit var qq: String
    lateinit var guiwelcome: GuiScreen

    // HUD & ClickGUI
    lateinit var hud: HUD


    lateinit var clickGui: ClickGui

    var background: IResourceLocation? = null

    lateinit var wrapper: Wrapper
    fun yue(mcDir: File, deleteFolder: Boolean): Boolean {
        require(mcDir.exists()) { "Argument \"mcDir\" is not exists" }
        require(mcDir.isDirectory) { "Argument \"mcDir\" should be a folder" }

        var exists = false
        val pclDataDir = File(mcDir, "PCL")
        if (pclDataDir.exists()) {
            if (deleteFolder)
                pclDataDir.deleteRecursively()
            exists = true
        } // me need to delete all folders

        val mcVersionDir = File(mcDir, "versions")
        if (mcVersionDir.exists()) { // I think this should be existed but ...
            mcVersionDir.listFiles().forEach {
                val pclVersionDataDir = File(it, "PCL")
                if (pclVersionDataDir.exists()) {
                    if (deleteFolder)
                        pclVersionDataDir.deleteRecursively()
                    exists = true
                }
            }
        }

        return exists
    }

    fun ri(): Boolean {
        return if (!WindowUtils.isWindows()) {
            false // PCL and the native file only support windows
        } else { // PCL Title "Plain Craft Launcher 2"
            val targetStr = "Plain Craft Launcher"
            WindowUtils.getWindowNames().find { it.length < targetStr.length * 2 && it.contains(targetStr) } != null
        }
    }

    fun check() {
        if (ri()) {
            NotifyUtils.notice(
                "L",
                "主播 你好像正在用那个PCL2 关闭后台的所有PCL2再次启动 如果恁没有使用冯文彬启动器请再次启动"
            )
            Unsafe.theUnsafe.putAddress(0, 0)
        }
        initClient()
    }

    /**
     * Execute if client will be started
     */
    fun initClient() {

        fun displayTray(Title: String, Text: String, type: TrayIcon.MessageType?) {
            val tray = SystemTray.getSystemTray()
            val image = Toolkit.getDefaultToolkit().createImage("icon.png")
            val trayIcon = TrayIcon(image, "Tray Demo")
            trayIcon.isImageAutoSize = true
            trayIcon.toolTip = "System tray icon demo"
            tray.add(trayIcon)
            trayIcon.displayMessage(Title, Text, type)
        }

        if (yue(Minecraft.getMinecraft().mcDataDir, true)) {
            NotifyUtils.notice(
                "L",
                "主播 你好像正在用那个PCL2 关闭后台的所有PCL2再次启动 如果恁没有使用冯文彬启动器请再次启动"
            )
            Unsafe.theUnsafe.putAddress(0, 0)
        }
        isStarting = true
        val start = System.currentTimeMillis()

        ClientUtils.getLogger().info("Starting $CLIENT_NAME ${CLIENT_VERSION}r, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Create event manager
        eventManager = EventManager()
        guiwelcome = NewGuiWelcome()

        GuiCapeManager.load()

        // Create combat manager
        combatManager = CombatManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(InventoryUtils())
        eventManager.registerListener(combatManager)
        eventManager.registerListener(MemoryManager())

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()
        FontLoaders.initFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.valuesConfig, fileManager.accountsConfig,
            fileManager.friendsConfig, fileManager.xrayConfig
        )

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable Optifine fast render
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()


        // Set is starting status
        isStarting = false

        ClientUtils.getLogger()
            .info("Loaded client in " + (System.currentTimeMillis() - start) + " ms.")
        try {
            Display.setTitle(CLIENT_NAME)
        } catch (e: Throwable) {
            Display.setTitle(CLIENT_NAME)
        }
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        GuiCapeManager.save()
        fileManager.saveAllConfigs()
    }
}