package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class ShieldsAndContainersHandler implements Updatable {

    public static final String TAG = ShieldsAndContainersHandler.class.getSimpleName();

    //private GameplayType gameplayType;

    private GameplayScreen gameplayScreen;

    private int activeShieldsNum;

    // Containers with no bullets attached during the current wave.
    private Array<BulletsAndShieldContainer> nonBusyContainers = new Array<>(false, SHIELDS_MAX_COUNT, BulletsAndShieldContainer.class);;

    private Timer mirrorControlsTimer;

    private float baseRotation;


    private float[] onStartAngles;
    private float[] onEndAngles;

    public ShieldsAndContainersHandler(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeMirrorControlsTimer();

        onStartAngles = new float[SHIELDS_MAX_COUNT];
        onEndAngles = new float[SHIELDS_MAX_COUNT];
    }

    @Override
    public void update(float delta) {
        mirrorControlsTimer.update(delta);

        if (gameplayScreen.getBulletsAndShieldContainers()[0].getRotationOmegaAlphaTween().isStarted())
            updateStartingAndEndingAngles();

        if (gameplayScreen.getGameplayMode() == GameplayMode.DISEASES) {
            gameplayScreen.getContainerOfContainers().rotateBy(delta * DISEASED_DIZZINESS_ROTATION_SPEED);
            updateStartingAndEndingAngles();
        }

        /*if (gameplayScreen.getGameplayMode() == GameplayMode.DISEASES) {
            baseRotation -= 0.2f;

            for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
                BulletsAndShieldContainer container = gameplayScreen.getBulletsAndShieldContainers()[i];

                //if(!container.getRotationOmegaAlphaTween().isStarted()) {
                    if (i < activeShieldsNum)
                        container.setRotation(baseRotation + i * 360f / activeShieldsNum);
                    else
                        container.setRotation(baseRotation + 360f);
                //}
            }
        }*/
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

            float oldRotationDeg = gameplayScreen.getBulletsAndShieldContainers()[i].getRotation();
            gameplayScreen.getBulletsAndShieldContainers()[i].setOldRotationDeg(oldRotationDeg);

            Gdx.app.log(TAG, "" + oldRotationDeg);

            if (i < activeShieldsNum)
                if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.FREE) {
                    gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(/*baseRotation + */i * 360f / activeShieldsNum + SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[activeShieldsNum - SHIELDS_MIN_COUNT]);
                } else {
                    gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(/*baseRotation + */i * 360f / activeShieldsNum + SHIELDS_SHIFT_ANGLES_RESTRICTED_GAMEPLAY[activeShieldsNum - SHIELDS_MIN_COUNT]);

                }
            else gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(/*baseRotation + */360);
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

    public void updateStartingAndEndingAngles() {
        float singleShieldAngle = 360f / activeShieldsNum;
        //Gdx.app.log(TAG, "singleShieldAngle = " + singleShieldAngle + " / 2 = " + (singleShieldAngle / 2f));

        float firstBulletsAndShieldContainerRotation = gameplayScreen.getBulletsAndShieldContainers()[0].getRotation();
        float containerOfContainersRotation = gameplayScreen.getContainerOfContainers().getRotation();

        onStartAngles[0] = MyMath.deg_0_to_360(firstBulletsAndShieldContainerRotation + containerOfContainersRotation - (singleShieldAngle / 2f));
        onEndAngles[0]   = MyMath.deg_0_to_360(firstBulletsAndShieldContainerRotation + containerOfContainersRotation + (singleShieldAngle / 2f));

        for (int i = 1; i < activeShieldsNum; i++) {
            onStartAngles[i] = MyMath.deg_0_to_360(onStartAngles[i-1] + singleShieldAngle);
            onEndAngles[i] = MyMath.deg_0_to_360(onEndAngles[i-1] + singleShieldAngle);
        }
    }

    public void handleOnShields() {
        Float[] cAs = {gameplayScreen.getControllerLeft().getAngleDeg(),
                gameplayScreen.getControllerRight().getAngleDeg()}; // cAs is for controllerAngles.

        if (cAs[0] != null)
            cAs[0] = MyMath.deg_0_to_360(cAs[0] - 90);

        if (cAs[1] != null)
            cAs[1] = MyMath.deg_0_to_360(cAs[1] - 90);



        for (int l = 0; l < activeShieldsNum; l++)
            gameplayScreen.getBulletsAndShieldContainers()[l].getShield().setOn(false);


        for (int i = 0; i < 2; i++) {
            if (cAs[i] == null) continue;

            for (int c = 0; c < activeShieldsNum; c++) {

                //Gdx.app.log(TAG, "" + gameplayScreen.getContainerOfContainers().getRotation());

                //float onStartAngle = MyMath.deg_0_to_360(gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() - (360f / activeShieldsNum / 2f));
                //float onEndAngle = MyMath.deg_0_to_360(gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + (360f / activeShieldsNum / 2f));

                /*onStartAngles[c] = MyMath.deg_0_to_360(gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() - (360f / activeShieldsNum / 2f));
                onEndAngles[c] = MyMath.deg_0_to_360(gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + (360f / activeShieldsNum / 2f));*/

                //if (onStartAngle < 0) onStartAngle += 360f; //To avoid -ve angles.
                //Gdx.app.log(TAG,c + ", " + gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + ", " + onStartAngles[c] + ", " + onEndAngles[c]);

                boolean inBounds = false;

                if (onStartAngles[c] > onEndAngles[c]) {
                    if (cAs[i] >= onStartAngles[c] | cAs[i] <= onEndAngles[c]) inBounds = true;
                } else if (cAs[i] >= onStartAngles[c] & cAs[i] <= onEndAngles[c]) inBounds = true;

                if (inBounds) {
                    //Gdx.app.log(TAG,gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + ", " + onStartAngle + ", " + onEndAngle + ", " + cAs[i]);
                    BulletsAndShieldContainer container = gameplayScreen.getBulletsAndShieldContainers()[c];
                    container.getShield().setOn(true);
                    break;

                    /*float rotation = MyMath.deg_0_to_360(container.getRotation());
                    if (i == 0) { // left controller
                        if (rotation >= 0 & rotation <= 180) {
                            container.getShield().setOn(true);
                        } else {
                            int index = MathUtils.clamp(c-1, 0, activeShieldsNum);
                            gameplayScreen.getBulletsAndShieldContainers()[index].getShield().setOn(true);
                        }
                    } else { // right controller
                        if (rotation >= 180 & rotation <= 360) {
                            container.getShield().setOn(true);
                        }else {
                            int index = MathUtils.clamp(c+1, 0, activeShieldsNum);
                            gameplayScreen.getBulletsAndShieldContainers()[index].getShield().setOn(true);
                        }
                    }
                    break;*/
                }
            }
        }

        if (cAs[0] != null | cAs[1] != null) {
            Gdx.app.log(TAG, cAs[0] + ", " + cAs[1]);
            for (int c = 0; c < activeShieldsNum; c++) {
                Gdx.app.log(TAG, c + ", " + gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + ", " + onStartAngles[c] + ", " + onEndAngles[c]);
            }
        }
    }

    /*private boolean validateOnShieldRotation(BulletsAndShieldContainer container) {

    }*/

    public void startMirrorTimer() {
        mirrorControlsTimer.start();
    }

    public float getMirrorControlsTimerDuration() {
        return mirrorControlsTimer.getDurationMillis();
    }

    public void setActiveShieldsNum(int activeShieldsNum) {
        if (activeShieldsNum > SHIELDS_MAX_COUNT) this.activeShieldsNum = SHIELDS_MAX_COUNT;
        else if (activeShieldsNum < SHIELDS_MIN_COUNT) this.activeShieldsNum = SHIELDS_MIN_COUNT;
        else this.activeShieldsNum = activeShieldsNum;

        updateNonBusyContainer();

        setVisibilityAndAlphaForContainers();
        setRotationForContainers();
        setOmegaForShieldObjects();
        startRotationOmegaTweenForAll();

        //updateStartingAndEndingAngles();

        /*for (BulletsAndShieldContainer container : probability) {
            Gdx.app.log(TAG, "" + container.getRotation());
        }*/
    }

    public int getActiveShieldsNum() {
        return activeShieldsNum;
    }

    private void updateNonBusyContainer() {
        /*nonBusyContainers = new Array<>(false,
                gameplayScreen.getBulletsAndShieldContainers(),
                0,
                activeShieldsNum);*/

        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
        nonBusyContainers.size = activeShieldsNum;
        for (int i = 0; i < activeShieldsNum; i++) {
            nonBusyContainers.set(i, allContainers[i]);
        }
        /*gameplayScreen.getBulletsHandler().setPrevious(null);
        gameplayScreen.getBulletsHandler().setCurrent(null);*/
        gameplayScreen.getBulletsHandler().clearBusyContainers();
    }

    public Array<BulletsAndShieldContainer> getNonBusyContainers() {
        return nonBusyContainers;
    }

    /*public GameplayType getGameplayType() {
        return gameplayType;
    }

    void setGameplayType(GameplayType gameplayType) {
        this.gameplayType = gameplayType;
    }*/

    private void initializeMirrorControlsTimer() {
        mirrorControlsTimer = new Timer(Constants.D_CRYSTAL_MIRROR_CONTROLS_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                ((RestrictedController) gameplayScreen.getControllerLeft()).setMirror(true);
                ((RestrictedController) gameplayScreen.getControllerRight()).setMirror(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                ((RestrictedController) gameplayScreen.getControllerLeft()).setMirror(false);
                ((RestrictedController) gameplayScreen.getControllerRight()).setMirror(false);
            }
        };
    }
}