package cc.paimon.modules.hyt;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;

import static net.ccbluex.liquidbounce.LiquidBounce.moduleManager;

@ModuleInfo(
        name = "ScaffoldHelper",
        description = "Grim bypass",
        category = ModuleCategory.WORLD
)
public class ScaffoldHelper extends Module {
    @EventTarget
    public void onUpdate(UpdateEvent event) {
        Scaffold scaffoldModule = (Scaffold) moduleManager.getModule(Scaffold.class);
        if (mc2.player.onGround) {
            scaffoldModule.setState(false);
        } else {
            scaffoldModule.setState(true);
        }
    }
    public void onDisable() {
        Scaffold scaffoldModule = (Scaffold) moduleManager.getModule(Scaffold.class);
        scaffoldModule.setState(false);
    }
}



