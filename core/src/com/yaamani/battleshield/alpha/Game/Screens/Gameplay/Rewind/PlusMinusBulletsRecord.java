package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;

public class PlusMinusBulletsRecord extends RewindEngine.RewindEvent {

    public boolean plus; // If false, minus

    public int containerIndex;

    public PlusMinusBulletsRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        if (plus) {
            int toBeDecrementedContainerIndex = containerIndex + 1;
            BulletsAndShieldContainer toBeDecrementedContainer = gameplayScreen.getBulletsAndShieldContainers()[toBeDecrementedContainerIndex];
            gameplayScreen.getShieldsAndContainersHandler().decrementActiveShieldsNum(toBeDecrementedContainer);
        } else {
            int index = containerIndex - 1;
            if (index == -1) index = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();
            BulletsAndShieldContainer container = gameplayScreen.getBulletsAndShieldContainers()[index];
            gameplayScreen.getShieldsAndContainersHandler().incrementActiveShieldsNum(container);
        }
    }

    @Override
    public String toString() {
        return "PlusMinusBulletsRecord{" +
                "plus=" + plus +
                ", containerIndex=" + containerIndex +
                '}';
    }
}
