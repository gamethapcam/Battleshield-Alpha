package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.LazerAttackStuff;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class NextLazerAttackTimerRecord extends RewindEngine.RewindEvent {

    public NextLazerAttackTimerRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();

        lazerAttackStuff.setLazerAttacking(false);
        lazerAttackStuff.setWaitingForAllRemainingBulletsToBeCleared(false);

        //lazerAttackStuff.getNextLazerAttackTimerText().addAction(Actions.alpha(1, LAZER_FADE_DURATION));
        //lazerAttackStuff.getNextLazerAttackTimerText().setColor(1, 1, 1, 1);

        Timer nextLazerAttackTimer = lazerAttackStuff.getNextLazerAttackTimer();
        nextLazerAttackTimer.setCurrentTime(nextLazerAttackTimer.getDurationMillis() - overTimeMillis);
    }

}
