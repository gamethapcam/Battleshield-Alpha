package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;

public abstract class TweenedFloat implements Updatable {

    private float value;
    private Tween tweenObj;

    private float from;
    private float to;

    public TweenedFloat(float value) {
        this.value = value;
        this.from = value;
        this.to = value;

        tweenObj = new Tween() {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                TweenedFloat.this.value = interpolation.apply(from, to, percentage);
                onTween(TweenedFloat.this.value);
            }
        };
    }

    public TweenedFloat() {
        this(0);
    }

    public void tween(float from, float to, float durationMillis, Interpolation interpolation) {
        if (tweenObj.isStarted()) {
            tweenObj.finish();
        }

        tweenObj.setDurationMillis(durationMillis);
        tweenObj.setInterpolation(interpolation);
        this.from = from;
        this.to = to;
        tweenObj.start();
    }

    public void tween(float to, float durationMillis, Interpolation interpolation) {
        tween(value, to, durationMillis, interpolation);
    }

    @Override
    public void update(float delta) {
        tweenObj.update(delta);
    }

    /**
     * Called as long as the tween is in progress.
     * @param value The exact current value.
     */
    public abstract void onTween(float value);

    /**
     * @return The exact current value.
     */
    public float getExactCurrentValue() {
        return value;
    }

    /**
     * If it's in the middle of the tween, this returns the final after the tween finishes.
     * @return
     */
    public float getCurrentFinalValue() {
        return to;
    }

    public Tween getTweenObj() {
        return tweenObj;
    }
}
