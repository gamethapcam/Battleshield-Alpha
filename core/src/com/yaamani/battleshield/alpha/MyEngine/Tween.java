package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.Interpolation;

public abstract class Tween extends Timer implements Updatable {
    //As long as you're calling update, the percentage will be increased. Once update is no longer being called, the percentage will stop incrementing and will stuck at the percentage of the last update call.

    public static final String TAG = Tween.class.getSimpleName();

    private Interpolation interpolation;

    private boolean reversed = false;

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
        this.interpolation = Interpolation.linear;
    }

    public Tween() {
        this(0);
    }

    public abstract void tween(float percentage, Interpolation interpolation);

    @Override
    public boolean onUpdate(float delta) {
        //pausingResumingGradually(delta);
        if (isStarted() & (getPercentage() < 1 & !reversed | getPercentage() > 0 & reversed))
            tween(getPercentage(), interpolation);
        return false;
    }

    @Override
    public void onFinish() {
        if (!reversed)
            tween(1, interpolation);
        else tween(0, interpolation);
    }


    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    @Override
    public float getPercentage() {
        if (!reversed)
            return super.getPercentage();
        else
            return 1-super.getPercentage();
    }

    public void setReversed(boolean reversed) {
        if (this.reversed == reversed) return;

        this.reversed = reversed;
        setPercentage(1-getPercentage());
    }

    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setPercentage(float percentage) {
        if (!reversed)
            super.setPercentage(percentage);
        else super.setPercentage(1-percentage);

        if (isFinished() | isPaused()) tween(getPercentage(), interpolation);
    }
}