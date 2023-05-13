package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.api.minecraft.client.gui.IGuiTextField;

public final class TabUtils {

    public static void tab(final IGuiTextField... textFields) {
        for (int i = 0; i < textFields.length; i++) {
            final IGuiTextField textField = textFields[i];

            if (textField.isFocused()) {
                textField.setFocused(false);
                i++;

                if (i >= textFields.length)
                    i = 0;

                textFields[i].setFocused(true);
                break;
            }
        }
    }
}
