package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.math.Interpolation;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreTimerStuff;

public class AffectTimerRecord extends RewindEngine.RewindEvent {

    public float affectTimerTweenInitialValue, affectTimerTweenFinalValue;
    public float affectTimerTweenDurationMillis;
    public Interpolation affectTimerTweenInterpolation;

    public float affectTimerTweenFinalPercentage;

    public AffectTimerRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        ScoreTimerStuff scoreTimerStuff = gameplayScreen.getScoreTimerStuff();

        scoreTimerStuff.setAffectTimerTweenInitialValue(affectTimerTweenInitialValue);
        scoreTimerStuff.setAffectTimerTweenFinalValue(affectTimerTweenFinalValue);
        scoreTimerStuff.getAffectTimerTween().setDurationMillis(affectTimerTweenDurationMillis);
        scoreTimerStuff.getAffectTimerTween().setInterpolation(affectTimerTweenInterpolation);
        //float p = MathUtils.clamp(affectTimerTweenFinalPercentage, 0.01f, 0.99f);
        scoreTimerStuff.getAffectTimerTween().setPercentage(/*p*/affectTimerTweenFinalPercentage);
        //scoreTimerStuff.getAffectTimerTween().setCurrentTime(/*p*/affectTimerTweenFinalPercentage * affectTimerTweenDurationMillis - overTimeMillis);
    }

    @Override
    public String toString() {
        return "\n{" + AffectTimerRecord.class.getSimpleName() + "|"
                + super.toString() + "|"
                + affectTimerTweenInitialValue + "|"
                + affectTimerTweenFinalValue + "|"
                + affectTimerTweenDurationMillis + "|"
                + affectTimerTweenInterpolation + "|"
                + affectTimerTweenFinalPercentage + "}";
    }
}
