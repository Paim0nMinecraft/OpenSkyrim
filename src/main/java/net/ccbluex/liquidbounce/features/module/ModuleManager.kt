package net.ccbluex.liquidbounce.features.module

import cc.paimon.modules.combat.BlatantAura
import cc.paimon.modules.combat.HytAntiVoid
import cc.paimon.modules.combat.OldVelocity
import cc.paimon.modules.hyt.*
import cc.paimon.modules.misc.PingSpoof
import cc.paimon.modules.misc.StrafeFix
import cc.paimon.modules.render.BlockESP
import cc.paimon.modules.render.NewGUI
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.client.CapeManager
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.AutoPlay
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import xiatian.novoline.module.Ambience
import xiatian.novoline.module.ChatTranslator
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            StrafeFix::class.java,
            PingSpoof::class.java,
            BlockESP::class.java,
            HytAntiVoid::class.java,
            ScaffoldHelper::class.java,
            NewGUI::class.java,
            BlatantAura::class.java,
            Helper::class.java,
            OldVelocity::class.java,
            HytGetName::class.java,
            TargetStrafe::class.java,
            Title::class.java,
            AutoArmor::class.java,
            AutoBow::class.java,
            AutoRunaway::class.java,
            AutoPot::class.java,
            AutoSoup::class.java,
            AutoWeapon::class.java,
            BowAimbot::class.java,
            Criticals::class.java,
            KillAura::class.java,
            Trigger::class.java,
            Fly::class.java,
            ClickGUI::class.java,
            InventoryMove::class.java,
            SafeWalk::class.java,
            WallClimb::class.java,
            Strafe::class.java,
            Sprint::class.java,
            Teams::class.java,
            NoRotateSet::class.java,
            AntiBot::class.java,
            ChestStealer::class.java,
            Scaffold::class.java,
            CivBreak::class.java,
            Tower::class.java,
            FastPlace::class.java,
            ESP::class.java,
            NoSlow::class.java,
            Speed::class.java,
            NameTags::class.java,
            FastUse::class.java,
            Teleport::class.java,
            Fullbright::class.java,
            ItemESP::class.java,
            NoClip::class.java,
            FastClimb::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            NoHurtCam::class.java,
            XRay::class.java,
            Timer::class.java,
            Aimbot::class.java,
            Eagle::class.java,
            HitBox::class.java,
            AntiCactus::class.java,
            ConsoleSpammer::class.java,
            LongJump::class.java,
            FastBow::class.java,
            AutoClicker::class.java,
            NoBob::class.java,
            NoFriends::class.java,
            Chams::class.java,
            Clip::class.java,
            ServerCrasher::class.java,
            NoFOV::class.java,
            FastStairs::class.java,
            TNTBlock::class.java,
            InventoryCleaner::class.java,
            TrueSight::class.java,
            AntiBlind::class.java,
            NoSwing::class.java,
            Breadcrumbs::class.java,
            AntiVoid::class.java,
            AbortBreaking::class.java,
            PotionSaver::class.java,
            CameraClip::class.java,
            NoPitchLimit::class.java,
            AirLadder::class.java,
            TeleportHit::class.java,
            BufferSpeed::class.java,
            SuperKnockback::class.java,
            ProphuntESP::class.java,
            Damage::class.java,
            KeepContainer::class.java,
            VehicleOneHit::class.java,
            Reach::class.java,
            Rotations::class.java,
            NoJumpDelay::class.java,
            AntiAFK::class.java,
            HUD::class.java,
            ComponentOnHover::class.java,
            ResourcePackSpoof::class.java,
            NoSlowBreak::class.java,
            PortalMenu::class.java,
            EnchantEffect::class.java,
            KeepChest::class.java,
            SpeedMine::class.java,
            AutoHead::class.java,
            Animations::class.java,
            Test::class.java,
            AutoLeos::class.java,
            BlurSettings::class.java,
            JumpCircle::class.java,
            CapeManager::class.java,
            AutoPlay::class.java,
            HytGetName::class.java,
            Ambience::class.java,
            ChatTranslator::class.java,
            DMGParticle::class.java,
            FakeFPS::class.java,
            HotbarSettings::class.java,
            Crosshair::class.java,
            Get::class.java
        )

        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger()
                .error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    fun getModuleInCategory(category: ModuleCategory) = modules.filter { it.category == category }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    fun getModulesInCategory(cat: ModuleCategory): ArrayList<Module?> {
        val modsInCat: ArrayList<Module?> = ArrayList<Module?>()
        for (mod in LiquidBounce.moduleManager.modules) {
            if (mod.category == cat) {
                modsInCat.add(mod)
            }
        }
        return modsInCat
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]!!

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
