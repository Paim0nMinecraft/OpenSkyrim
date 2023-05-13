package net.ccbluex.liquidbounce.utils;

import java.lang.reflect.Field;

public class Unsafe {

    public static final sun.misc.Unsafe theUnsafe;

    static {
        Field f;
        try {
            f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = (sun.misc.Unsafe) f.get(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
