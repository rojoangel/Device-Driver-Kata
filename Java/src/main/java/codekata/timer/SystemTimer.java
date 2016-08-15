package codekata.timer;

import codekata.Timer;

public class SystemTimer implements Timer {

    private long thresholdMillis;
    private long start;

    public SystemTimer(long thresholdMillis) {
        this.thresholdMillis = thresholdMillis;
    }

    @Override
    public void start() {
        start = System.currentTimeMillis();
    }

    @Override
    public boolean hasTimedOut() {
        return System.currentTimeMillis() - start > thresholdMillis;
    }
}
