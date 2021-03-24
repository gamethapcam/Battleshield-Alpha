package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreTimerStuff;

public class AffectTimerColorRecord extends RewindEngine.RewindEvent {

    private static final String TAG = AffectTimerColorRecord.class.getSimpleName();

    public float affectTimerTweenInitialValue, affectTimerTweenFinalValue;
    public float affectTimerColorTweenDurationMillis;

    public float affectTimerColorTweenFinalPercentage;

    public AffectTimerColorRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart() {
        ScoreTimerStuff scoreTimerStuff = gameplayScreen.getScoreTimerStuff();

        scoreTimerStuff.setAffectTimerTweenInitialValue(affectTimerTweenInitialValue);
        scoreTimerStuff.setAffectTimerTweenFinalValue(affectTimerTweenFinalValue);
        scoreTimerStuff.getAffectTimerColorTween().setDurationMillis(affectTimerColorTweenDurationMillis);
        float p = MathUtils.clamp(affectTimerColorTweenFinalPercentage * 2, 0.01f, 0.99f);
        //Gdx.app.log(TAG, affectTimerColorTweenFinalPercentage + ", p = " + p);
        scoreTimerStuff.getAffectTimerColorTween().setPercentage(p);
    }

    @Override
    public String toString() {
        return "\n{" + AffectTimerColorRecord.class.getSimpleName() + "|"
                + super.toString() + "|"
                + affectTimerTweenInitialValue + "|"
                + affectTimerTweenFinalValue + "|"
                + affectTimerColorTweenFinalPercentage + "}";
    }
}
