package com.yaamani.battleshield.alpha.MyEngine;

public class Timer implements Updatable {
    //private long startTime;
    private float currentTime;
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
        percentage = 0;
        started = true;
        finished = false;
        if (isPaused()) paused = false;
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

        //if (onUpdate(delta)) return;

        if (started) {
            //float currentTime = MyMath.millisSince(startTime);
            percentage = currentTime / durationMillis;

            if (!onUpdate(delta)) currentTime += delta*MyMath.secondsToMillis;

            if (currentTime >= durationMillis) {
                finish();
                //return;
            }
            //tween(percentage);
        }

        //onUpdate(delta);
    }

    /**
     * You should return false unless you handled the currentTime or the percentage values in your implementation of onUpdate(float), then return true. This way the Timer class won't update the currentTime value or the percentage, as you've already handled them.
     * @param delta
     * @return
     */
    public boolean onUpdate(float delta) {
        return false;
    }

    public final void pause() {
        paused = true;
        if (inPauseDelay) inPauseDelay = false;
        onPause();
    }

    public final void pauseFor(float millis) {
        this.pauseDelayMillis = millis;
        pauseDelayCurrentTime = 0;
        pause();
        inPauseDelay = true;
    }

    public void onPause() {

    }

    public final void resume() {
        paused = false;
        inPauseDelay = false;
        onResume();
    }

    public void onResume() {

    }

    public final void finish() {
        started = false;
        finished = true;
        inPauseDelay = false;
        inStartDelay = false;
        //if (isPaused()) paused = false;
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
            throw new ValueOutOfRangeException("percentage(" + percentage + ") must be smaller than 1 and greater than 0.");
        this.percentage = percentage;
        currentTime = percentage * getDurationMillis();

        if (isFinished()) started = true;
    }

    protected void setCurrentTime(float currentTime) {
        if (currentTime > durationMillis | currentTime < 0)
            throw new ValueOutOfRangeException("currentTime(" + currentTime + ") must be smaller than durationMillis and greater than 0.");
        this.currentTime = currentTime;
        percentage = currentTime / durationMillis;

        if (isFinished()) started = true;
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
