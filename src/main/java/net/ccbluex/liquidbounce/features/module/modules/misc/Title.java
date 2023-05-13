package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.TextValue;
import org.lwjgl.opengl.Display;

import static net.ccbluex.liquidbounce.LiquidBounce.CLIENT_VERSION;

/**
 * @author ChengFeng
 * @since 2022/12/5
 */

@ModuleInfo(name = "Title", description = "Custom title.", category = ModuleCategory.MISC)
public class Title extends Module {
    private static final BoolValue time = new BoolValue("TimeDisplay", false);
    private static final TextValue defaultText = new TextValue("Title", "Example title");


    private int ticks;
    private int seconds;
    private int minutes;
    private int hours;
    private String timeText = "";

    @EventTarget
    public void update(UpdateEvent event) {
        ticks++;
        if (ticks == 20) {
            seconds++;
            ticks = 0;
        }

        if (seconds == 60) {
            minutes++;
            seconds = 0;
        }

        if (minutes == 60) {
            hours++;
            minutes = 0;
        }

        timeText = hours + " 时 " + minutes + " 分 " + seconds + " 秒 ";

        Display.setTitle(defaultText.get() + (time.get() ? " " + timeText : ""));
    }

    @Override
    public void onDisable() {
        try {
            Display.setTitle("𝙎𝙠𝙮𝙧𝙞𝙢" + CLIENT_VERSION + "r | " + HttpUtils.get("https://v1.hitokoto.cn/?c=f&encode=text"));
        } catch (Exception e) {
            Display.setTitle("𝙎𝙠𝙮𝙧𝙞𝙢" + CLIENT_VERSION + "r");
        }
    }
}
