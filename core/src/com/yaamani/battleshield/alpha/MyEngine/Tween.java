package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class Tween extends Timer implements Updatable{
    //As long as you're calling update, the percentage will be increased. Once update is no longer being called, the percentage will stop incrementing and will stuck at the percentage of the last update call.

    public static final String TAG = Tween.class.getSimpleName();

    public Tween(float durationMillis, Transition transition) {
        super(durationMillis);
        transition.addTween(this);
    }

    public Tween(float durationMillis, AdvancedStage game) {
        super(durationMillis, game);
    }

    public Tween(float durationMillis) {
        super(durationMillis);
    }

    public abstract void tween(float percentage);

    @Override
    public void onUpdate(float delta) {
        if (isStarted() & getPercentage() < 1)tween(getPercentage());
    }

    @Override
    public void onFinish() {
        tween(1);
    }
}
