package com.yaamani.battleshield.alpha.MyEngine;

public class Timer implements Updatable {
    //private long startTime;
    private double currentTime;
    private float durationMillis; // millis
    private float percentage;
    private boolean started = false;
    private boolean finished = false;
    private boolean paused = false;

    //private long delayTimeStart;
    private double delayCurrentTime;
    private float delayMillis = 0;
    private boolean inDelay = false;

    /**
     * When calling this constructor, you don't need to call {@link #update(float)} by yourself. It's called automatically by the {@link AdvancedStage} class.
     * @param durationMillis
     * @param game the game class.
     */
    public Timer(float durationMillis, AdvancedStage game) {
        this(durationMillis);
        game.addUpdatable(this);
    }

    /**
     * When calling this constructor, you have to call {@link #update(float)} by yourself. As long as you're calling update, the percentage will be increased. Once update is no longer being called, the percentage will stop incrementing and will stuck at the percentage of the last update call.
     * @param durationMillis
     */
    public Timer(float durationMillis) {
        this.durationMillis = durationMillis;
    }

    public Timer() {
        this(0);
    }

    public final void start() {
        //startTime = TimeUtils.nanoTime();
        currentTime = 0;
        started = true;
        finished = false;
        onStart();
    }

    public final void start(float delayMillis) {
        this.delayMillis = delayMillis;
        //delayTimeStart = TimeUtils.nanoTime();
        delayCurrentTime = 0;
        inDelay = true;
        finished = false;
    }

    public void onStart() {

    }

    //public abstract void tween(float percentage);

    @Override
    public final void update(float delta) {
        if (paused) return;

        if (inDelay) {
            //float currentDelayTime = MyMath.millisSince(delayTimeStart);
            delayCurrentTime += delta*MyMath.secondsToMillis;
            if(/*currentDelayTime*/ delayCurrentTime >= delayMillis) {
                inDelay = false;
                start();
            } else return;
        }

        if (started) {
            //float currentTime = MyMath.millisSince(startTime);
            percentage = (float) (currentTime / durationMillis);
            currentTime += delta*MyMath.secondsToMillis;
            if (currentTime > durationMillis) {
                finish();
                //return;
            }
            //tween(percentage);
        }

        onUpdate(delta);
    }

    public void onUpdate(float delta) {

    }

    public final void pause() {
        paused = true;
        onPause();
    }

    public void onPause() {

    }

    public final void resume() {
        paused = false;
        onResume();
    }

    public void onResume() {

    }

    public final void finish() {
        started = false;
        finished = true;
        onFinish();
        //tween(1);
    }

    public void onFinish() {

    }

    public void setDurationMillis(float durationMillis) {
        this.durationMillis = durationMillis;
    }

    public float getDurationMillis() {
        return durationMillis;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        if (percentage > 1 | percentage < 0)
            throw new ValueOutOfRangeException("percentage must be smaller than 1 and greater than 0.");
        this.percentage = percentage;
        currentTime = percentage * getDurationMillis();
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isPaused() {
        return paused;
    }
}
