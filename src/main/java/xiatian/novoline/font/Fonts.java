/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package xiatian.novoline.font;


@SuppressWarnings("SpellCheckingInspection")
public interface Fonts {
    public final static String
            BUG = "a",
            LIST = "b",
            BOMB = "c",
            EYE = "d",
            PERSON = "e",
            WHEELCHAIR = "f",
            SCRIPT = "g",
            SKIP_LEFT = "h",
            PAUSE = "i",
            PLAY = "j",
            SKIP_RIGHT = "k",
            SHUFFLE = "l",
            INFO = "m",
            SETTINGS = "n",
            CHECKMARK = "o",
            XMARK = "p",
            TRASH = "q",
            WARNING = "r",
            FOLDER = "s",
            LOAD = "t",
            SAVE = "u";
    FontManager FONT_MANAGER = (FontManager) Client.getFontManager();

    interface NovolineIcon{
        FontFamily NovolineIcon = FONT_MANAGER.fontFamily(FontType.Novoicon);
        final class NovolineIcon75 {public static  final  FontRenderer NovolineIcon75 = NovolineIcon.ofSize(75); private NovolineIcon75() {}}
        final class NovolineIcon45 {public static  final  FontRenderer NovolineIcon45= NovolineIcon.ofSize(35); private NovolineIcon45() {}}
    }


    interface notification {
        FontFamily notification = FONT_MANAGER.fontFamily(FontType.Notification);
        final class notification35 { public static final FontRenderer notification35 = notification.ofSize(35); private notification35() {} }
    }
    interface tenacity {
        FontFamily tenacity = FONT_MANAGER.fontFamily(FontType.tenacity);
        final class tenacity18 { public static final FontRenderer tenacity18 = tenacity.ofSize(18); private tenacity18() {} }
        final class tenacity22 { public static final FontRenderer tenacity22 = tenacity.ofSize(22); private tenacity22() {} }
    }
    interface tenacityblod {
        FontFamily tenacityblod = FONT_MANAGER.fontFamily(FontType.tenacityBlod);
        final class tenacityblod22 { public static final FontRenderer tenacityblod22 = tenacityblod.ofSize(22); private tenacityblod22() {} }
        final class tenacityblod40 { public static final FontRenderer tenacityblod40 = tenacityblod.ofSize(40); private tenacityblod40() {} }

    }
    interface CsgoIcon {

        FontFamily csgoicon = FONT_MANAGER.fontFamily(FontType.csgoicon);
        final class csgoicon_18 { public static final FontRenderer csgoicon_18 = csgoicon.ofSize(18); private csgoicon_18() {} }
        final class csgoicon_20 { public static final FontRenderer csgoicon_20 = csgoicon.ofSize(20); private csgoicon_20() {} }
        final class csgoicon_24 { public static final FontRenderer csgoicon_24 = csgoicon.ofSize(24); private csgoicon_24() {} }
        final class csgoicon_32 { public static final FontRenderer csgoicon_32 = csgoicon.ofSize(32); private csgoicon_32() {} }
        final class csgoicon_35 { public static final FontRenderer csgoicon_35 = csgoicon.ofSize(35); private csgoicon_35() {} }
        final class csgoicon_40 { public static final FontRenderer csgoicon_40 = csgoicon.ofSize(40); private csgoicon_40() {} }
        final class csgoicon_55 { public static final FontRenderer csgoicon_55 = csgoicon.ofSize(55); private csgoicon_55() {} }
        //final class csgoicon_60 { public static final FontRenderer csgoicon_60 = csgoicon.ofSize(60); private csgoicon_60() {} }

    }
    interface tenacityCheck {
        FontFamily tenacitycheck = FONT_MANAGER.fontFamily(FontType.tenacityCheck);
        final class tenacitycheck35 { public static final FontRenderer tenacitycheck35 = tenacitycheck.ofSize(35); private tenacitycheck35() {} }
    }
    interface posterama {
        FontFamily posterama = FONT_MANAGER.fontFamily(FontType.posterama);
        final class posterama13 { public static final FontRenderer posterama13 = posterama.ofSize(13); private posterama13() {} }
        final class posterama20 { public static final FontRenderer posterama20 = posterama.ofSize(20); private posterama20() {} }
        final class posterama16 { public static final FontRenderer posterama16 = posterama.ofSize(16); private posterama16() {} }
        final class posterama18 { public static final FontRenderer posterama18 = posterama.ofSize(18); private posterama18() {} }
    }

}
