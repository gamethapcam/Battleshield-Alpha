package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

public abstract class Tween extends Timer implements Updatable {
    //As long as you're calling update, the percentage will be increased. Once update is no longer being called, the percentage will stop incrementing and will stuck at the percentage of the last update call.

    public static final String TAG = Tween.class.getSimpleName();

    private Interpolation interpolation;

    /*private boolean isPausingGradually;
    private boolean isResumingGradually;
    private float pauseResumeGraduallyDurationMillis;
    private float pauseResumeGraduallyCurrentTime;
    private float pauseResumeGraduallyInitialPercentage;
    private float pauseResumeGraduallyFinalPercentage;
    private Interpolation pauseResumeGraduallyInterpolation;*/

    public Tween(float durationMillis, Interpolation interpolation, Transition transition) {
        super(durationMillis);
        this.interpolation = interpolation;
        transition.addTween(this);
    }

    public Tween(float durationMillis, Interpolation interpolation, AdvancedStage game) {
        super(durationMillis, game);
        this.interpolation = interpolation;
    }

    public Tween(float durationMillis, Interpolation interpolation) {
        super(durationMillis);
        this.interpolation = interpolation;
    }

    public Tween(Interpolation interpolation) {
        super(0);
        this.interpolation = interpolation;
    }

    public Tween(float durationMillis) {
        super(durationMillis);
    }

    public Tween() {
        super(0);
    }

    public abstract void tween(float percentage, Interpolation interpolation);

    @Override
    public void onUpdate(float delta) {
        //pausingResumingGradually(delta);
        if (isStarted() & getPercentage() < 1) tween(getPercentage(), interpolation);
    }

    @Override
    public void onFinish() {
        tween(1, interpolation);
    }

    /*public void pauseGradually(float pauseDurationMillis, Interpolation pauseInterpolation) {
        isPausingGradually = true;
        isResumingGradually = false;
        if (pauseDurationMillis > getDurationMillis() - getPercentage() * getDurationMillis()) {
            Gdx.app.log(TAG, "The duration for pausing gradually (" + pauseDurationMillis + "ms) is greater than the remaining duration of the tween itself (" + getPercentage() * getDurationMillis() + "ms). So, the duration that is going to be applied for pausing gradually is (" + getPercentage() * getDurationMillis() + "ms).");
            pauseResumeGraduallyDurationMillis = getDurationMillis() - getPercentage() * getDurationMillis();
        } else
            pauseResumeGraduallyDurationMillis = pauseDurationMillis;

        pauseResumeGraduallyCurrentTime = 0;
        pauseResumeGraduallyInterpolation = pauseInterpolation;
        pauseResumeGraduallyInitialPercentage = getPercentage();
        pauseResumeGraduallyFinalPercentage = (getPercentage() * getDurationMillis() + pauseResumeGraduallyDurationMillis) / getDurationMillis();
        Gdx.app.log(TAG, "initialPercentage = " + pauseResumeGraduallyInitialPercentage + ", finalPercentage = " + pauseResumeGraduallyFinalPercentage);
    }

    public void resumeGradually(float resumeDurationMillis, Interpolation resumeInterpolation) {
        isPausingGradually = false;
        isResumingGradually = true;
        if (resumeDurationMillis > getDurationMillis() - getPercentage() * getDurationMillis()) {
            Gdx.app.log(TAG, "The duration for resuming gradually (" + resumeDurationMillis + "ms) is greater than the remaining duration of the tween itself (" + getPercentage() * getDurationMillis() + "ms). So, the duration that is going to be applied for resuming gradually is (" + getPercentage() * getDurationMillis() + "ms).");
            pauseResumeGraduallyDurationMillis = getDurationMillis() - getPercentage() * getDurationMillis();
        } else
            pauseResumeGraduallyDurationMillis = resumeDurationMillis;

        pauseResumeGraduallyCurrentTime = 0;
        pauseResumeGraduallyInterpolation = resumeInterpolation;
        pauseResumeGraduallyInitialPercentage = getPercentage();
        pauseResumeGraduallyFinalPercentage = (getPercentage() * getDurationMillis() + pauseResumeGraduallyDurationMillis) / getDurationMillis();

        resume();
    }*/

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    /*private void pausingResumingGradually(float delta) {
        if (!(isPausingGradually | isResumingGradually))
            return;

        pauseResumeGraduallyCurrentTime += delta * MyMath.secondsToMillis;

        if (pauseResumeGraduallyCurrentTime > pauseResumeGraduallyDurationMillis) {
            if (isPausingGradually) pause();

            isPausingGradually = isResumingGradually = false;
            return;
        }

        setPercentage(pauseResumeGraduallyInterpolation.apply(pauseResumeGraduallyInitialPercentage, pauseResumeGraduallyFinalPercentage, pauseResumeGraduallyCurrentTime / pauseResumeGraduallyDurationMillis));

        Gdx.app.log(TAG, "" + getPercentage() + ", " + isFinished());
    }*/
}