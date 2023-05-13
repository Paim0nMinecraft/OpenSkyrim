package net.ccbluex.liquidbounce.utils.render;

/**
 * @author ChengFeng
 * @since 2022/11/26
 */
public class DoublePosition {
    private float x, y;

    public DoublePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public DoublePosition() {
        this.x = 0;
        this.y = 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
