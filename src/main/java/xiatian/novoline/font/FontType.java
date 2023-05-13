package xiatian.novoline.font;

@SuppressWarnings("SpellCheckingInspection")
public enum FontType {

    Jello_Light("jellolight.ttf"),
    Jello_Medium("jellomedium.ttf"),
    Jello_Regular("jelloregular.ttf"),
    SF("sf.ttf"),
    SFBOLD("sfbold.ttf"),
    SFTHIN("SFREGULAR.ttf"),
    Check("check.ttf"),
    ICONFONT("stylesicons.ttf"),
    flux("flux.ttf"),
    posterama("posterama.ttf"),
    csgoicon("icomoon.ttf"),
    Tahoma("tahoma.ttf"),

    NeverLoserf("neverlose500.ttf"),

    Novoicon("iconnovo.ttf"),
    Neverlose_icon("neverlose_icon.ttf"),

    Debug_Icon("Icon.ttf"),

    Notification("notification-icon.ttf"),
    Novo2("novogui.ttf"),
    tenacity("tenacity.ttf"),
    tenacityBlod("tenacitybold.ttf"),
    tenacityCheck("check.ttf");


    private final String fileName;

    FontType(String fileName) {
        this.fileName = fileName;
    }

    public String fileName() { return fileName; }
}
