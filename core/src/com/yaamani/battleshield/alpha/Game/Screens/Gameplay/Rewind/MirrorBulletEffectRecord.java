package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;

public class MirrorBulletEffectRecord extends RewindEngine.RewindEvent {



    public MirrorBulletEffectRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart() {
        gameplayScreen.getShieldsAndContainersHandler().initiateMirrorBulletEffect();
        gameplayScreen.getShieldsAndContainersHandler().getMirrorControlsTimer().setPercentage(0.99f);
        gameplayScreen.getMirrorTempProgressBarUI().getTween().setPercentage(0.99f);
    }
}
