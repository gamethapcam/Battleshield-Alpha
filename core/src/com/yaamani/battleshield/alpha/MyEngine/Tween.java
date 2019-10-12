package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

public abstract class Tween extends Timer implements Updatable {
    //As long as you're calling update, the percentage will be increased. Once update is no longer being called, the percentage will stop incrementing and will stuck at the percentage of the last update call.

    public static final String TAG = Tween.class.getSimpleName();

    private Interpolation interpolation;

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

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public void setPercentage(float percentage) {
        super.setPercentage(percentage);
        if (isFinished() | isPaused()) tween(percentage, interpolation);
    }
}