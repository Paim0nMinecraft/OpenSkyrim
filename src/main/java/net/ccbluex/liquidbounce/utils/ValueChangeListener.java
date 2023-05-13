package net.ccbluex.liquidbounce.utils;

public interface ValueChangeListener<T> {
    void onValueChange(T oldValue, T value);
}