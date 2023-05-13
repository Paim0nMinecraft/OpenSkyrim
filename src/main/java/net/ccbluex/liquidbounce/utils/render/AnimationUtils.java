package net.ccbluex.liquidbounce.utils.render;

public class AnimationUtils {
    public static float easeOut(float t, float d) {
        return (t = t / d - 1) * t * t + 1;
    }

    public static float lstransition(float now, float desired, double speed) {
        final double dif = Math.abs(desired - now);
        float a = (float) Math.abs((desired - (desired - (Math.abs(desired - now)))) / (100 - (speed * 10)));
        float x = now;

        if (dif > 0) {
            if (now < desired)
                x += a * RenderUtils.deltaTime;
            else if (now > desired)
                x -= a * RenderUtils.deltaTime;
        } else
            x = desired;

        if (Math.abs(desired - x) < 10.0E-3 && x != desired)
            x = desired;

        return x;
    }

    public static double animate(double target, double current, double speed) {
        if (current == target) return current;

        boolean larger = target > current;
        if (speed < 0.0D) {
            speed = 0.0D;
        } else if (speed > 1.0D) {
            speed = 1.0D;
        }

        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1D) {
            factor = 0.1D;
        }

        if (larger) {
            current += factor;
            if (current >= target) current = target;
        } else if (target < current) {
            current -= factor;
            if (current <= target) current = target;
        }

        return current;
    }
}
