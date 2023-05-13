package cc.paimon.modules.combat;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.injection.backend.PacketImplKt;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * Skid by Paimon.
 *
 * @Date 2023/2/18
 */
@ModuleInfo(name="BlatantAura",description = "L",category = ModuleCategory.COMBAT)
public class BlatantAura extends Module {
    public final BoolValue debug = new BoolValue("Debug",false);
    private Float karange;
    private String oldvelMode;

    @Override
    public void onDisable() {
        final KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        final OldVelocity oldVelocity = (OldVelocity) LiquidBounce.moduleManager.getModule(OldVelocity.class);
        killAura.setState(false);
        killAura.getRangeValue().set(karange);
        oldVelocity.getModeValue().set(oldvelMode);
    }

    @Override
    public void onEnable() {
        final KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        final  OldVelocity oldVelocity = (OldVelocity) LiquidBounce.moduleManager.getModule(OldVelocity.class);
        karange = killAura.getRangeValue().get();
        oldvelMode = oldVelocity.getModeValue().get();
        killAura.getRangeValue().set(5.2f);
        oldVelocity.getModeValue().set("HytCancel");
        killAura.setState(true);
        oldVelocity.setState(true);
        if(debug.get()) ClientUtils.displayChatMessage("§7[§8§6Skyrim§7]§f Auto Change Value");
    }
    @EventTarget
    public void onPacket(PacketEvent event){
        Packet packet = PacketImplKt.unwrap(event.getPacket());
        if(packet instanceof CPacketPlayer){
            event.cancelEvent();
            if(debug.get()) ClientUtils.displayChatMessage("§7[§8§6Skyrim§7]§f Cancel C03");

        }
    }

}
