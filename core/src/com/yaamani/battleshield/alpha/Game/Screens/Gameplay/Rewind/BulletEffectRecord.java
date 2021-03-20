package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;

public class BulletEffectRecord extends RewindEngine.RewindEvent {

    public enum BulletEffectRecordType {MIRROR, FASTER_DIZZINESS_ROTATION}

    public BulletEffectRecordType bulletEffectRecordType;

    public BulletEffectRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart() {

        switch (bulletEffectRecordType) {
            case MIRROR:
                gameplayScreen.getShieldsAndContainersHandler().initiateMirrorBulletEffect();
                gameplayScreen.getShieldsAndContainersHandler().getMirrorControlsTimer().setPercentage(0.99f);
                gameplayScreen.getMirrorTempProgressBarUI().getTween().setPercentage(0.99f);
                break;

            case FASTER_DIZZINESS_ROTATION:
                gameplayScreen.getShieldsAndContainersHandler().initiateFasterDizzinessRotationBulletEffect();
                gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer().setPercentage(0.99f);
                gameplayScreen.getFasterDizzinessRotationTempProgressBarUI().getTween().setPercentage(0.99f);
                break;
        }
    }
}
