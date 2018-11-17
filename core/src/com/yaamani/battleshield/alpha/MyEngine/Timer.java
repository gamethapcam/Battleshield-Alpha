package com.yaamani.battleshield.alpha.MyEngine;

public class Timer implements Updatable {
    //private long startTime;
    private double currentTime;
    private float durationMillis; // millis
    protected float percentage;
    private boolean started = false;
    private boolean finished = false;

    //private long delayTimeStart;
    private double delayCurrentTime;
    private float delay = 0;
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

    public final void start() {
        //startTime = TimeUtils.nanoTime();
        currentTime = 0;
        started = true;
        finished = false;
        onStart();
    }

    public final void start(float delay) {
        this.delay = delay;
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
        if (inDelay) {
            //float currentDelayTime = MyMath.millisSince(delayTimeStart);
            delayCurrentTime += delta*MyMath.millisToNano;
            if(/*currentDelayTime*/ delayCurrentTime >= delay) {
                inDelay = false;
                start();
            } else return;
        }

        if (started) {
            //float currentTime = MyMath.millisSince(startTime);
            percentage = (float) (currentTime/ durationMillis);
            currentTime += delta*MyMath.millisToNano;
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

    public boolean isStarted() {
        return started;
    }

    public boolean isFinished() {
        return finished;
    }
}
