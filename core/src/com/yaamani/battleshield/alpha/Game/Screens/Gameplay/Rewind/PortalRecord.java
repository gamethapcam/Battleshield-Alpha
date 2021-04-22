package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class PortalRecord extends RewindEngine.RewindEvent {

    public BulletPortalType type;

    public int containerIndex;

    public float duration;

    public PortalRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        if (type == BulletPortalType.PORTAL_ENTRANCE)
            gameplayScreen.getBulletsAndShieldContainers()[containerIndex].showPortalEntranceRewinding(this);
        else
            gameplayScreen.getBulletsAndShieldContainers()[containerIndex].showPortalExitRewinding(this);

        if (!gameplayScreen.getBulletsHandler().isThereAPortal())
            gameplayScreen.getBulletsHandler().portalIsOn();

    }

    @Override
    public String toString() {
        return "PortalRecord{" +
                "type=" + type +
                ", containerIndex=" + containerIndex +
                ", duration=" + duration +
                '}';
    }
}
