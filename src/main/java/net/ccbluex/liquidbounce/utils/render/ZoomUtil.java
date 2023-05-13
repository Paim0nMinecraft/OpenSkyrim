package net.ccbluex.liquidbounce.utils.render;


import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.minecraft.client.Minecraft;

public class ZoomUtil {

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final float originalX;
    private final float originalY;
    private final float originalWidth;
    private final float originalHeight;
    private final float speed;
    private final float zoomFactor;
    private final long nextUpdateTime;
    private final TimeUtils timer = new TimeUtils();
    private float x, y, width, height;

    public ZoomUtil(float x, float y, float width, float height, long nextUpdateTime, float speed, float zoomFactor) {
        this.originalX = x;
        this.originalY = y;
        this.originalWidth = width;
        this.originalHeight = height;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.zoomFactor = zoomFactor;
        this.nextUpdateTime = nextUpdateTime;
    }

    public void update(int mouseX, int mouseY) {
        if (RenderUtils.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (timer.hasElapsed(nextUpdateTime)) {
                x = RenderUtils.animate(originalX - zoomFactor / 2, x, speed) - 0.1f;
                y = RenderUtils.animate(originalY - zoomFactor / 2, y, speed) - 0.1f;
                width = RenderUtils.animate(originalWidth + zoomFactor, width, speed) - 0.1f;
                height = RenderUtils.animate(originalHeight + zoomFactor, height, speed) - 0.1f;
                timer.reset();
            }
        } else if (timer.hasElapsed(nextUpdateTime)) {
            x = RenderUtils.animate(originalX, x, speed) - 0.1f;
            y = RenderUtils.animate(originalY, y, speed) - 0.1f;
            width = RenderUtils.animate(originalWidth, width, speed) - 0.1f;
            height = RenderUtils.animate(originalHeight, height, speed) - 0.1f;
            timer.reset();
        }
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getOriginalX() {
        return originalX;
    }

    public float getOriginalY() {
        return originalY;
    }

    public float getOriginalWidth() {
        return originalWidth;
    }

    public float getOriginalHeight() {
        return originalHeight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    public TimeUtils getTimer() {
        return timer;
    }
}
