/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.features.module.modules.render;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.RenderWings;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "Wings", description = "NO!", category = ModuleCategory.RENDER)
class Wings extends Module {

    private final BoolValue onlyThirdPerson = new BoolValue("OnlyThirdPerson", true);

    @EventTarget
    public void onRenderPlayer(Render3DEvent event) {
        if (onlyThirdPerson.get()) {
            return;
        }

        RenderWings renderWings = new RenderWings();
        renderWings.renderWings(event.getPartialTicks());
    }

}
