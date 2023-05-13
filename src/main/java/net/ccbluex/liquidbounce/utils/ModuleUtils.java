package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;

/**
 * @author ChengFeng
 * @since 2022/11/29
 */
public class ModuleUtils {
    @SafeVarargs
    public static void disableModules(Class<? extends Module>... modules) {
        for (Class<? extends Module> module : modules) {
            LiquidBounce.moduleManager.getModule(module).setState(false);
        }
    }
}
