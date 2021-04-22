package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletEffectRecord extends RewindEngine.RewindEvent {

    public enum BulletEffectRecordType {MIRROR, FASTER_DIZZINESS_ROTATION, TWO_EXIT_PORTAL, ARMOR}

    public BulletEffectRecordType bulletEffectRecordType;

    public float val;

    public BulletEffectRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {

        switch (bulletEffectRecordType) {
            case MIRROR:
                gameplayScreen.getShieldsAndContainersHandler().initiateMirrorBulletEffect();
                val = MathUtils.clamp(val, 0, 1);
                gameplayScreen.getShieldsAndContainersHandler().getMirrorControlsTimer().setPercentage(val);
                gameplayScreen.getMirrorTempProgressBarUI().getTween().setPercentage(val);
                break;

            case FASTER_DIZZINESS_ROTATION:
                gameplayScreen.getShieldsAndContainersHandler().initiateFasterDizzinessRotationBulletEffect();
                val = MathUtils.clamp(val, 0, 1);
                gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer().setPercentage(val);
                gameplayScreen.getFasterDizzinessRotationTempProgressBarUI().getTween().setPercentage(val);
                break;

            case TWO_EXIT_PORTAL: // Mostly UI stuff.
                if (val < 0) {
                    gameplayScreen.displayTwoExitPortalUI();
                    gameplayScreen.getBulletsHandler().setRemainingTwoExitPortals(0);
                } else if (val > D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES) {
                    gameplayScreen.stopDisplayingTwoExitPortalUI();
                } else {
                    gameplayScreen.getBulletsHandler().setRemainingTwoExitPortals(Math.round(val));
                }

                gameplayScreen.getTwoExitPortalUI().updateText(gameplayScreen.getBulletsHandler().getRemainingTwoExitPortals());
                break;

            case ARMOR:
                gameplayScreen.getLazerAttackStuff().decrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack(-1);
                break;

        }
    }

    @Override
    public String toString() {
        return "BulletEffectRecord{" +
                "bulletEffectRecordType=" + bulletEffectRecordType +
                ", val=" + val +
                '}';
    }
}
