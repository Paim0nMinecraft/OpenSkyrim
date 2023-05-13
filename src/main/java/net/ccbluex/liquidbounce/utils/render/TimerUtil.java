package net.ccbluex.liquidbounce.utils.render;

public final class TimerUtil {

    private long time = -1L;

    private int tick;

    public void update() {
        tick++;
    }

    public void reset3() {
        tick = 0;
    }

    public boolean hasTimePassed3(final int ticks) {
        return tick >= ticks;
    }

    private long currentMS = System.currentTimeMillis();

    public long lastReset() {
        return currentMS;
    }

    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {

        if (currentMS > System.currentTimeMillis()) {
            currentMS = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - currentMS > time) {

            if (reset)
                reset();

            return true;


        } else {
            return false;
        }

    }

    public boolean hasTimePassed(final long MS) {
        return System.currentTimeMillis() >= time + MS;
    }

    public long hasTimeLeft(final long MS) {
        return (MS + time) - System.currentTimeMillis();
    }


    public long elapsed() {
        return System.currentTimeMillis() - currentMS;
    }

    public void reset() {
        currentMS = System.currentTimeMillis();
    }

    public void reset2() {
        time = System.currentTimeMillis();
    }

    public void setCurrentMS(long currentMS) {
        this.currentMS = currentMS;
    }
}
