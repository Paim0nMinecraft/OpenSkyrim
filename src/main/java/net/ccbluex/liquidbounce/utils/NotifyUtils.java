package net.ccbluex.liquidbounce.utils;

import javax.swing.*;

public class NotifyUtils {
    public static void notice(String title, String message) {
        JOptionPane.getRootFrame().setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static String showInputDialog(String message) {
        JOptionPane.getRootFrame().setAlwaysOnTop(true);
        return JOptionPane.showInputDialog(message);
    }
}
