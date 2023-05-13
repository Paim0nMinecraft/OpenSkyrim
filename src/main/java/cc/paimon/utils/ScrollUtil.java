package cc.paimon.utils;

import org.lwjgl.input.Mouse;

public class ScrollUtil {

    public double target, scroll, max = 25;
    public StopWatch stopwatch = new StopWatch();
    public StopWatch stopWatch2 = new StopWatch();
    public boolean scrollingIsAllowed;

    public void onRender() {

        //Sets target scroll every tick, this way scrolling will only change if there's less than 1 frame per tick
        if (stopWatch2.finished(50)) {
            final float wheel = Mouse.getDWheel();
            double stretch = 30;
            target = Math.min(Math.max(target + wheel / 2, max - (wheel == 0 ? 0 : stretch)), (wheel == 0 ? 0 : stretch));

            stopWatch2.reset();
        }

        //Moving render scroll towards target
        for (int i = 0; i < stopwatch.getElapsedTime(); ++i) {
            scroll = MathUtil.lerp(scroll, target, 1E-2F);
        }

        //resetting stopwatch
        stopwatch.reset();
    }


    public void reset() {
        this.scroll = 0;
        this.target = 0;
    }
}
