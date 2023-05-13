package net.ccbluex.liquidbounce.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ColorValue
        extends Value<Integer> {


    public boolean Expanded;

    public ColorValue(String name, int color) {
        super(name, color);
        this.setValue(color);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element.isJsonPrimitive())
            setValue(element.getAsInt());
    }


    public boolean isExpanded() {
        return Expanded;
    }

    public void setExpanded(boolean expanded) {
        Expanded = expanded;
    }

    public float[] getHSB() {
        // if (getVaule() == null) return new float[]{0.0F, 0.0F, 0.0F};
        float[] hsbValues = new float[3];

        float saturation, brightness;
        float hue;

        int cMax = max(getValue() >>> 16 & 0xFF, getValue() >>> 8 & 0xFF);
        if ((getValue() & 0xFF) > cMax) cMax = getValue() & 0xFF;

        int cMin = min(getValue() >>> 16 & 0xFF, getValue() >>> 8 & 0xFF);
        if ((getValue() & 0xFF) < cMin) cMin = getValue() & 0xFF;

        brightness = (float) cMax / 255.0F;
        saturation = cMax != 0 ? (float) (cMax - cMin) / (float) cMax : 0;

        if (saturation == 0) {
            hue = 0;
        } else {
            float redC = (float) (cMax - (getValue() >>> 16 & 0xFF)) / (float) (cMax - cMin), // @off
                    greenC = (float) (cMax - (getValue() >>> 8 & 0xFF)) / (float) (cMax - cMin),
                    blueC = (float) (cMax - (getValue() & 0xFF)) / (float) (cMax - cMin); // @on

            hue = ((getValue() >>> 16 & 0xFF) == cMax ?
                    blueC - greenC :
                    (getValue() >>> 8 & 0xFF) == cMax ? 2.0F + redC - blueC : 4.0F + greenC - redC) / 6.0F;

            if (hue < 0) hue += 1.0F;
        }

        hsbValues[0] = hue;
        hsbValues[1] = saturation;
        hsbValues[2] = brightness;

        return hsbValues;
    }
}
