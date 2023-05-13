package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import org.jetbrains.annotations.NotNull;

/**
 * @author ChengFeng
 * @since 2022/11/26
 */
public class LegitHop extends SpeedMode {
    public LegitHop() {
        super("LegitHop");
    }


    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(@NotNull MoveEvent event) {
        if (minecraft.player.onGround) {
            minecraft.player.jump();
        }
    }
}
