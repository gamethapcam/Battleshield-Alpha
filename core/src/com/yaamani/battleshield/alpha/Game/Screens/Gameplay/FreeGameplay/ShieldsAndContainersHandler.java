package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class ShieldsAndContainersHandler {

    private GameplayScreen gameplayScreen;

    private int activeShieldsNum;
    private Array<BulletsAndShieldContainer> probability;

    public ShieldsAndContainersHandler(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    // TODO: [FIX A BUG] Sometimes (only sometimes which is really weird) when the gameplay begins the shields and the bullets won't be displayed (But they do exist, meaning that the bullets reduce the health and the shield can be on and block the bullets). And get displayed after a plus or a minus bullet hit the turret. (Not sure if this is a desktop specific or happens on android too) [PATH TO VIDEO = Junk/Shield and bullets don't appear [BUG].mov] .. It looks like it always happen @ the first run of the program on desktop just after I open android studio
    private void setVisibilityAndAlphaForContainers() {
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            if (i < activeShieldsNum) {
                gameplayScreen.getBulletsAndShieldContainers()[i].setVisible(true);
                gameplayScreen.getBulletsAndShieldContainers()[i].setNewAlpha(1);
            } else {
                gameplayScreen.getBulletsAndShieldContainers()[i].setNewAlpha(0);
                //gameplayScreen.getBulletsAndShieldContainers()[i].setVisible(false);
            }
        }
    }

    private void setRotationForContainers() {
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            if (i < activeShieldsNum)
                gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(i * 360f / activeShieldsNum + SHIELDS_SHIFT_ANGLES[activeShieldsNum - SHIELDS_MIN_COUNT]);
            else gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(360);
        }
    }

    private void setOmegaForShieldObjects() {
        float omegaDeg = 360f / activeShieldsNum;
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            gameplayScreen.getBulletsAndShieldContainers()[i].setNewOmegaDeg(omegaDeg);
        }
    }

    private void startRotationOmegaTweenForAll() {
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            gameplayScreen.getBulletsAndShieldContainers()[i].startRotationOmegaAlphaTween();
        }
    }

    public void handleOnShields() {
        Float[] cAs = {gameplayScreen.getControllerLeft().getAngleDegNoNegative(), gameplayScreen.getControllerRight().getAngleDegNoNegative()}; // cAs is for controllerAngles.

        for (int l = 0; l < activeShieldsNum; l++)
            gameplayScreen.getBulletsAndShieldContainers()[l].getShield().setOn(false);

        for (int i = 0; i < 2; i++) {
            if (cAs[i] == null) continue;

            for (int c = 0; c < activeShieldsNum; c++) {
                float onStartAngle = gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() - (360f / activeShieldsNum / 2f);
                float onEndAngle = gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + (360f / activeShieldsNum / 2f);
                if (onStartAngle < 0) onStartAngle += 360f; //To avoid -ve angles.

                boolean setOnToTrue = false;

                if (onStartAngle > onEndAngle) {
                    if (cAs[i] >= onStartAngle | cAs[i] <= onEndAngle) setOnToTrue = true;
                } else if (cAs[i] > onStartAngle & cAs[i] <= onEndAngle) setOnToTrue = true;

                if (setOnToTrue) {
                    gameplayScreen.getBulletsAndShieldContainers()[c].getShield().setOn(true);
                    break;
                }
            }
        }
    }

    public void setActiveShieldsNum(int activeShieldsNum) {
        if (activeShieldsNum > SHIELDS_MAX_COUNT) this.activeShieldsNum = SHIELDS_MAX_COUNT;
        else if (activeShieldsNum < SHIELDS_MIN_COUNT) this.activeShieldsNum = SHIELDS_MIN_COUNT;
        else this.activeShieldsNum = activeShieldsNum;

        newProbability();

        setVisibilityAndAlphaForContainers();
        setRotationForContainers();
        setOmegaForShieldObjects();
        startRotationOmegaTweenForAll();
    }

    public int getActiveShieldsNum() {
        return activeShieldsNum;
    }

    private void newProbability() {
        probability = new Array<BulletsAndShieldContainer>(false,
                gameplayScreen.getBulletsAndShieldContainers(),
                0,
                activeShieldsNum);
        gameplayScreen.getBulletsHandler().setPrevious(null);
        gameplayScreen.getBulletsHandler().setCurrent(null);
    }

    public Array<BulletsAndShieldContainer> getProbability() {
        return probability;
    }
}