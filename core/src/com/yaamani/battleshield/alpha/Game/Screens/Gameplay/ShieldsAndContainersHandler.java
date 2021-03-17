package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.MirrorBulletEffectRecord;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.PlusMinusBulletsRecord;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Arrays;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class ShieldsAndContainersHandler implements Updatable {

    public static final String TAG = ShieldsAndContainersHandler.class.getSimpleName();

    //private GameplayType gameplayType;

    private int activeShieldsNum;

    // Containers with no bullets attached during the current wave.
    private Array<BulletsAndShieldContainer> nonBusyContainers;

    private Timer mirrorControlsTimer;

    //private float baseRotation;


    private Tween d_dizziness_rotationalSpeedTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_bigBoss_rotationalSpeedTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private float dizzinessBaseRotationalSpeed;
    private float dizzinessRotationalSpeedMultiplier = 1;
    private Timer dizzinessRotationalSpeedMultiplierTimer; // For faster dizziness rotation bullet

    private MyTween dizzinessBaseRotationalSpeedSlowMoTween;


    private float[] onStartAngles;
    private float[] onEndAngles;

    private float[] oldRotationDeg;
    private float[] newRotationDeg;

    private PlusMinusBulletsRecord currentRecord;


    private NetworkAndStorageManager networkAndStorageManager;

    private GameplayScreen gameplayScreen;


    public ShieldsAndContainersHandler(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeMirrorControlsTimer();

        initializeD_dizziness_rotationalSpeedTween();
        initializeD_bigBoss_rotationalSpeedTween();

        initializeDizzinessRotationalSpeedMultiplierTimer();

        initializeDizzinessBaseRotationalSpeedSlowMoTween();

    }

    @Override
    public void update(float delta) {
        mirrorControlsTimer.update(delta);

        networkReceivingStuff();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            startMirrorTimer();

        switch (gameplayScreen.getGameplayMode()){
            case DIZZINESS:
                d_dizziness_rotationalSpeedTween.update(delta);
                break;
            case BIG_BOSS:
                d_bigBoss_rotationalSpeedTween.update(delta);
                break;
        }


        if (gameplayScreen.getGameplayMode() == GameplayMode.DIZZINESS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS) {
            dizzinessRotationalSpeedMultiplierTimer.update(delta);

            dizzinessBaseRotationalSpeedSlowMoTween.update(delta);

            gameplayScreen.getContainerOfContainers().rotateBy(delta * dizzinessBaseRotationalSpeed * dizzinessRotationalSpeedMultiplier);
            updateStartingAndEndingAngles();
        }

        networkTransmissionAndStorageStuff();

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

    private void networkReceivingStuff() {
        if (networkAndStorageManager == null) return;

        if (networkAndStorageManager.isConnectionEstablished() | networkAndStorageManager.isLoadControllerValuesModeEnabled())
            if (networkAndStorageManager.isActiveShieldsNumReadyToBeConsumed()) {
                byte newlySentActiveShieldsNum = networkAndStorageManager.consumeActiveShieldsNum();
                if (newlySentActiveShieldsNum != activeShieldsNum)
                    setActiveShieldsNum(newlySentActiveShieldsNum);
            }
    }

    private void networkTransmissionAndStorageStuff() {
        if (networkAndStorageManager == null) return;

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            if (networkAndStorageManager.isConnectionEstablished() | networkAndStorageManager.isSaveControllerValuesModeEnabled())
                networkAndStorageManager.prepareActiveShieldsNumForTransmissionAndStorageIfIamMobile((byte) activeShieldsNum);
    }

    // TODO: [FIX A BUG] Sometimes (only sometimes which is really weird) when the gameplay begins the shields and the bullets won't be displayed (But they do exist, meaning that the bullets reduce the health and the shield can be on and block the bullets). And get displayed after a plus or a minus bullet hit the turret. (Not sure if this is a desktop specific or happens on android too) [PATH TO VIDEO = Junk/Shield and bullets don't appear [BUG].mov] .. It looks like it always happen @ the first run of the program on desktop just after I open android studio

    private void setRotationForContainers_LEGACY() {
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {

            float oldRotationDeg = gameplayScreen.getBulletsAndShieldContainers()[i].getRotation();
            gameplayScreen.getBulletsAndShieldContainers()[i].setOldRotationDeg(oldRotationDeg);

            //Gdx.app.log(TAG, "" + oldRotationDeg);

            float startingAngle;
            if (activeShieldsNum % 2 != 0) startingAngle = 0;
            else startingAngle = 360f / activeShieldsNum / 2f;

            if (i < activeShieldsNum)
                if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.FREE) {
                    gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(startingAngle + i * 360f / activeShieldsNum/* + SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[activeShieldsNum ]*/);
                } else {
                    gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(startingAngle + i * 360f / activeShieldsNum/* + SHIELDS_SHIFT_ANGLES_RESTRICTED_GAMEPLAY[activeShieldsNum]*/);

                }
            else gameplayScreen.getBulletsAndShieldContainers()[i].setNewRotationDeg(360);
        }
    }

    private void setOmegaForShieldObjects_LEGACY() {
        float omegaDeg = 360f / activeShieldsNum;
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            if (i < activeShieldsNum)
                gameplayScreen.getBulletsAndShieldContainers()[i].setNewOmegaDeg(omegaDeg);
            else
                gameplayScreen.getBulletsAndShieldContainers()[i].setNewOmegaDeg(0);

        }
    }

    private void setVisibilityAndAlphaForContainers_LEGACY() {
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

    private void startRotationOmegaTweenForAll() {
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            gameplayScreen.getBulletsAndShieldContainers()[i].startRotationOmegaAlphaTween();
        }
    }

    public void updateStartingAndEndingAngles() {
        if (onStartAngles == null) return;

        float singleShieldAngle = 360f / activeShieldsNum;
        //Gdx.app.log(TAG, "singleShieldAngle = " + singleShieldAngle + " / 2 = " + (singleShieldAngle / 2f));

        float firstBulletsAndShieldContainerRotation = gameplayScreen.getBulletsAndShieldContainers()[0].getRotation();
        float containerOfContainersRotation = gameplayScreen.getContainerOfContainers().getRotation();

        onStartAngles[0] = MyMath.deg_0_to_360(firstBulletsAndShieldContainerRotation + containerOfContainersRotation - (singleShieldAngle / 2f) + 90);
        onEndAngles[0]   = MyMath.deg_0_to_360(firstBulletsAndShieldContainerRotation + containerOfContainersRotation + (singleShieldAngle / 2f) + 90);

        for (int i = 1; i < activeShieldsNum; i++) {
            onStartAngles[i] = MyMath.deg_0_to_360(onStartAngles[i-1] + singleShieldAngle);
            onEndAngles[i] = MyMath.deg_0_to_360(onEndAngles[i-1] + singleShieldAngle);
        }
    }

    public void handleOnShields() {
        //if (gameplayScreen.isRewinding()) return;

        Float[] cAs = {gameplayScreen.getControllerLeft().getAngleDeg(),
                gameplayScreen.getControllerRight().getAngleDeg()}; // cAs is for controllerAngles.

        if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {
            if (((RestrictedController) (gameplayScreen.getControllerLeft())).isMirror()) {
                // swap
                Float cAs0 = cAs[0];
                cAs[0] = cAs[1];
                cAs[1] = cAs0;
            }
        }


        if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {
            float singleShieldAngle = 360f / activeShieldsNum;
            float clampAngle = singleShieldAngle/2f - 2; // -2 is just to make sure that the top most shield is going to be activated.

            if (cAs[0] != null) {
                if (cAs[0] > 0) cAs[0] = MathUtils.clamp(cAs[0], 90 + clampAngle, 180);
                else cAs[0] = MathUtils.clamp(cAs[0], -180, -(90 + clampAngle));
            }

            if (cAs[1] != null)
                cAs[1] = MathUtils.clamp(cAs[1], -(90 - clampAngle), (90 - clampAngle));

        }


        if (cAs[0] != null)
            cAs[0] = MyMath.deg_0_to_360(cAs[0]/* - 90*/);

        if (cAs[1] != null)
            cAs[1] = MyMath.deg_0_to_360(cAs[1]/* - 90*/);



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

        /*if (cAs[0] != null | cAs[1] != null) {
            Gdx.app.log(TAG, cAs[0] + ", " + cAs[1]);
            for (int c = 0; c < activeShieldsNum; c++) {
                Gdx.app.log(TAG, c + ", " + gameplayScreen.getBulletsAndShieldContainers()[c].getRotation() + ", " + onStartAngles[c] + ", " + onEndAngles[c]);
            }
        }*/
    }

    /*private boolean validateOnShieldRotation(BulletsAndShieldContainer container) {

    }*/

    public void startMirrorTimer() {
        mirrorControlsTimer.start();
    }

    public Timer getMirrorControlsTimer() {
        return mirrorControlsTimer;
    }

    public Timer getDizzinessRotationalSpeedMultiplierTimer() {
        return dizzinessRotationalSpeedMultiplierTimer;
    }

    public MyTween getDizzinessBaseRotationalSpeedSlowMoTween() {
        return dizzinessBaseRotationalSpeedSlowMoTween;
    }

    public void startDizzinessBaseRotationalSpeedSlowMoTween() {
        dizzinessBaseRotationalSpeedSlowMoTween.setInitialVal(dizzinessBaseRotationalSpeed);
        dizzinessBaseRotationalSpeedSlowMoTween.setFinalVal(0);
        dizzinessBaseRotationalSpeedSlowMoTween.start();
    }

    public void revertDizzinessBaseRotationalSpeedAfterSlowMo() {
        dizzinessBaseRotationalSpeed = dizzinessBaseRotationalSpeedSlowMoTween.getInitialVal();
    }

    public void pushCurrentPlusMinusRecord() {
        if (currentRecord != null) {
            gameplayScreen.getRewindEngine().pushRewindEvent(currentRecord);
            currentRecord = null;
        }
    }

    public void initializeActiveShields(int activeShieldsNum) {
        int shieldsMaxCount = gameplayScreen.getCurrentShieldsMaxCount();
        int shieldsMinCount = gameplayScreen.getCurrentShieldsMinCount();
        if (activeShieldsNum > shieldsMaxCount) this.activeShieldsNum = shieldsMaxCount;
        else if (activeShieldsNum < shieldsMinCount) this.activeShieldsNum = shieldsMinCount;
        else this.activeShieldsNum = activeShieldsNum;



        // inUse
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            if (i < activeShieldsNum)
                gameplayScreen.getBulletsAndShieldContainers()[i].setInUse(true);
            else
                gameplayScreen.getBulletsAndShieldContainers()[i].setInUse(false);

        }



        resetNonBusyContainers();

        setRotationForContainers_LEGACY();
        setOmegaForShieldObjects_LEGACY();
        setVisibilityAndAlphaForContainers_LEGACY();



        startRotationOmegaTweenForAll();
    }

    public void decrementActiveShieldsNum(BulletsAndShieldContainer toBeDiscardedContainer) {
        int shieldsMinCount = gameplayScreen.getCurrentShieldsMinCount();
        activeShieldsNum--;
        if (activeShieldsNum < shieldsMinCount) {
            activeShieldsNum++;
            return;
        }

        currentRecord = gameplayScreen.getRewindEngine().obtainPlusMinusBulletsRecord();
        currentRecord.plus = false;
        currentRecord.containerIndex = toBeDiscardedContainer.getIndex();

        // toBeDiscardedContainer's stuff.
        toBeDiscardedContainer.setInUse(false);
        setRotationOmegaTweenParameters(toBeDiscardedContainer, toBeDiscardedContainer.getRotation(), toBeDiscardedContainer.getRotation(), 0, 0);

        //Shifting.
        int toBeDiscardedIndex = shiftRight(toBeDiscardedContainer);
        Gdx.app.log(TAG, "toBeDiscardedIndex = " + toBeDiscardedIndex);

        // indexOfTheContainerWithTheNearestBulletToTheShield.
        int indexOfTheContainerWithTheNearestBulletToTheShield = calculateIndexOfTheContainerWithTheNearestBulletToTheShield();
        Gdx.app.log(TAG, "indexOfTheContainerWithTheNearestBulletToTheShield = " + indexOfTheContainerWithTheNearestBulletToTheShield);


        // Rotation, Omega & Alpha.
        populateOldRotationDegArray();
        populateNewRotationDegArray();
        Gdx.app.log(TAG, "oldRotationDeg = " + Arrays.toString(oldRotationDeg));
        Gdx.app.log(TAG, "newRotationDeg = " + Arrays.toString(newRotationDeg));

        int newRotationDegShift = calculateNewRotationDegShift(oldRotationDeg, newRotationDeg, indexOfTheContainerWithTheNearestBulletToTheShield);

        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();

        for (int i = 0; i < activeShieldsNum; i++) {


            float _oldRotationDeg = oldRotationDeg[i];
            float _newRotationDeg = newRotationDeg[MyMath.arrayIndexWrapAround(i+newRotationDegShift, activeShieldsNum)];
            float distance = Math.abs(_oldRotationDeg - _newRotationDeg);
            if (distance > 180) {
                if (_oldRotationDeg > 180)
                    _newRotationDeg += 360;
                else if (_newRotationDeg > 180)
                    _newRotationDeg -= 360f;
            }


            Gdx.app.log(TAG, "i = " + (i));
            setRotationOmegaTweenParameters(
                    allContainers[i],
                    _oldRotationDeg,
                    _newRotationDeg,
                    360f / activeShieldsNum,
                    1
            );
        }

        startRotationOmegaTweenForAll();

        resetNonBusyContainers();

    }

    public void incrementActiveShieldsNum(BulletsAndShieldContainer theContainerWithThePlusAttached) {
        int shieldsMaxCount = gameplayScreen.getCurrentShieldsMaxCount();
        activeShieldsNum++;
        if (activeShieldsNum > shieldsMaxCount) {
            activeShieldsNum--;
            return;
        }

        currentRecord = gameplayScreen.getRewindEngine().obtainPlusMinusBulletsRecord();
        currentRecord.plus = true;
        currentRecord.containerIndex = theContainerWithThePlusAttached.getIndex();

        //Shifting.
        int indexOfTheContainerWithThePlusAttached = shiftLeft(theContainerWithThePlusAttached);
        Gdx.app.log(TAG, "indexOfTheContainerWithThePlusAttached = " + indexOfTheContainerWithThePlusAttached);

        // indexOfTheContainerWithTheNearestBulletToTheShield.
        int indexOfTheContainerWithTheNearestBulletToTheShield = calculateIndexOfTheContainerWithTheNearestBulletToTheShield();
        Gdx.app.log(TAG, "indexOfTheContainerWithTheNearestBulletToTheShield = " + indexOfTheContainerWithTheNearestBulletToTheShield);

        // Rotation, Omega & Alpha.
        populateOldRotationDegArray();
        populateNewRotationDegArray();
        Gdx.app.log(TAG, "oldRotationDeg = " + Arrays.toString(oldRotationDeg));
        Gdx.app.log(TAG, "newRotationDeg = " + Arrays.toString(newRotationDeg));

        int newRotationDegShift = calculateNewRotationDegShift(oldRotationDeg, newRotationDeg, indexOfTheContainerWithTheNearestBulletToTheShield);

        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();

        for (int i = 0; i < activeShieldsNum; i++) {

            allContainers[i].setVisible(true);

            float _oldRotationDeg = i == indexOfTheContainerWithThePlusAttached+1 ? newRotationDeg[i] : oldRotationDeg[i];
            float _newRotationDeg = newRotationDeg[MyMath.arrayIndexWrapAround(i+newRotationDegShift, activeShieldsNum)];
            float distance = Math.abs(_oldRotationDeg - _newRotationDeg);
            if (distance > 180) {
                if (_oldRotationDeg > 180)
                    _newRotationDeg += 360;
                else if (_newRotationDeg > 180)
                    _newRotationDeg -= 360f;
            }


            Gdx.app.log(TAG, "i = " + (i));
            setRotationOmegaTweenParameters(
                    allContainers[i],
                    _oldRotationDeg,
                    _newRotationDeg,
                    360f / activeShieldsNum,
                    1
            );
        }

        startRotationOmegaTweenForAll();

        resetNonBusyContainers();
    }

    private int shiftRight(BulletsAndShieldContainer toBeDiscardedContainer) {
        int toBeDiscardedIndex = toBeDiscardedContainer.getIndex();
        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
        for (int i = toBeDiscardedIndex+1; i < allContainers.length; i++) {
            allContainers[i-1] = allContainers[i];
            allContainers[i-1].setIndex((byte) (i-1));
        }
        allContainers[allContainers.length-1] = toBeDiscardedContainer;
        allContainers[allContainers.length-1].setIndex((byte) (allContainers.length-1));

        return toBeDiscardedIndex;
    }

    private int shiftLeft(BulletsAndShieldContainer theContainerWithThePlusAttached) {
        int indexOfTheContainerWithThePlusAttached = theContainerWithThePlusAttached.getIndex();
        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
        BulletsAndShieldContainer theNewContainerToBeAdded = allContainers[activeShieldsNum-1];
        for (int i = activeShieldsNum-2; i > indexOfTheContainerWithThePlusAttached; i--) {
            allContainers[i+1] = allContainers[i];
            allContainers[i+1].setIndex((byte) (i+1));
        }
        allContainers[indexOfTheContainerWithThePlusAttached+1] = theNewContainerToBeAdded;
        allContainers[indexOfTheContainerWithThePlusAttached+1].setIndex((byte) (indexOfTheContainerWithThePlusAttached+1));

        return indexOfTheContainerWithThePlusAttached;
    }

    private int calculateIndexOfTheContainerWithTheNearestBulletToTheShield() {
        int indexOfTheContainerWithTheNearestBulletToTheShield = 0;

        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
        Bullet nearestBulletToTheShield = allContainers[0].getAttachedBullets().peek();
        for (int i = 1; i < activeShieldsNum; i++) {

            Bullet currentBullet = allContainers[i].getAttachedBullets().peek();
            if (currentBullet != null)
                if (currentBullet.isPlus() | currentBullet.isMinus())
                    try {
                        currentBullet = allContainers[i].getAttachedBullets().get(1);
                    } catch (IndexOutOfBoundsException e) {
                        currentBullet = null;
                    }


            if (nearestBulletToTheShield == null) {
                nearestBulletToTheShield = currentBullet;
                indexOfTheContainerWithTheNearestBulletToTheShield = i;

            } else if (nearestBulletToTheShield.isPlus() | nearestBulletToTheShield.isMinus()) {
                nearestBulletToTheShield = currentBullet;
                indexOfTheContainerWithTheNearestBulletToTheShield = i;

            } else if (currentBullet != null) {
                if (nearestBulletToTheShield.isFake()) {
                    nearestBulletToTheShield = currentBullet;
                } else if (currentBullet.getY() < nearestBulletToTheShield.getY()) { // Closer
                    if (!currentBullet.isFake()) {
                        nearestBulletToTheShield = currentBullet;
                        indexOfTheContainerWithTheNearestBulletToTheShield = i;
                    }
                }
            }
        }

        return indexOfTheContainerWithTheNearestBulletToTheShield;
    }

    private void populateOldRotationDegArray() {
        BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
        for (int i = 0; i < activeShieldsNum; i++) {
            oldRotationDeg[i] = MyMath.deg_0_to_360(allContainers[i].getRotation());
        }
    }

    private void populateNewRotationDegArray() {
        float startingAngle;
        if (activeShieldsNum % 2 != 0) startingAngle = 0;
        else startingAngle = 360f / activeShieldsNum / 2f;

        for (int i = 0; i < activeShieldsNum; i++) {
            newRotationDeg[i] = startingAngle + i * 360f/activeShieldsNum;
        }
    }

    private int calculateNewRotationDegShift(float[] oldRotationDeg, float[] newRotationDeg, int indexOfTheContainerWithTheNearestBulletToTheShield) {
        float _oldRotationDeg = oldRotationDeg[indexOfTheContainerWithTheNearestBulletToTheShield];
        float minDistance = MyMath.distanceBetween2AnglesDeg(_oldRotationDeg, newRotationDeg[0]);
        int i_minDistance = 0;
        for (int i = 1; i < activeShieldsNum; i++) {
            float currentDistance = MyMath.distanceBetween2AnglesDeg(_oldRotationDeg, newRotationDeg[i]);
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                i_minDistance = i;
            }
        }
        return i_minDistance - indexOfTheContainerWithTheNearestBulletToTheShield;
    }

    private void setRotationOmegaTweenParameters(BulletsAndShieldContainer container, float oldRotationDeg, float newRotationDeg, float newOmegaDeg, float newAlpha) {
        container.setNewRotationDeg(newRotationDeg);
        container.setOldRotationDeg(oldRotationDeg);
        Gdx.app.log(TAG, "oldRotationDeg = " + oldRotationDeg + ", newRotationDeg = " + newRotationDeg);
        container.setNewOmegaDeg(newOmegaDeg);
        container.setNewAlpha(newAlpha);
    }

    public void setActiveShieldsNum(int activeShieldsNum) {
        //gameplayScreen.getBulletsHandler().setPreviousActiveShieldsNumber(this.activeShieldsNum);

        int shieldsMaxCount = gameplayScreen.getCurrentShieldsMaxCount();
        int shieldsMinCount = gameplayScreen.getCurrentShieldsMinCount();
        if (activeShieldsNum > shieldsMaxCount) this.activeShieldsNum = shieldsMaxCount;
        else if (activeShieldsNum < shieldsMinCount) this.activeShieldsNum = shieldsMinCount;
        else this.activeShieldsNum = activeShieldsNum;

        resetNonBusyContainers();

        setRotationForContainers_LEGACY();
        setOmegaForShieldObjects_LEGACY();
        setVisibilityAndAlphaForContainers_LEGACY();



        startRotationOmegaTweenForAll();

        //updateStartingAndEndingAngles();

        /*for (BulletsAndShieldContainer container : probability) {
            Gdx.app.log(TAG, "" + container.getRotation());
        }*/
    }

    public int getActiveShieldsNum() {
        return activeShieldsNum;
    }

    public float getDizzinessBaseRotationalSpeed() {
        return dizzinessBaseRotationalSpeed;
    }

    public void resetContainersRotation() {
        gameplayScreen.getContainerOfContainers().setRotation(0);
    }

    /**
     *
     * @return speed * multiplier.
     */
    public float getDizzinessRotationalSpeed() {
        return dizzinessBaseRotationalSpeed*dizzinessRotationalSpeedMultiplier;
    }

    public Tween getD_dizziness_rotationalSpeedTween() {
        return d_dizziness_rotationalSpeedTween;
    }

    public Tween getD_bigBoss_rotationalSpeedTween() {
        return d_bigBoss_rotationalSpeedTween;
    }

    public void resetNonBusyContainers() {
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
        //gameplayScreen.getBulletsHandler().dontAddBusyToNonBusyThisTime();

    }

    public Array<BulletsAndShieldContainer> getNonBusyContainers() {
        return nonBusyContainers;
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
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

                if (!gameplayScreen.isRewinding())
                    initiateMirrorBulletEffect();
                else {
                    if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {
                        ((RestrictedController) gameplayScreen.getControllerLeft()).setMirror(false);
                        ((RestrictedController) gameplayScreen.getControllerRight()).setMirror(false);
                    }
                }


            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (!gameplayScreen.isRewinding()) {
                    if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {
                        ((RestrictedController) gameplayScreen.getControllerLeft()).setMirror(false);
                        ((RestrictedController) gameplayScreen.getControllerRight()).setMirror(false);
                    }

                    MirrorBulletEffectRecord mirrorBulletEffectRecord = gameplayScreen.getRewindEngine().obtainMirrorBulletEffectRecord();
                    gameplayScreen.getRewindEngine().pushRewindEvent(mirrorBulletEffectRecord);

                } else {

                }
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(mirrorControlsTimer);
    }

    public void initiateMirrorBulletEffect() {
        if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {

            ((RestrictedController) gameplayScreen.getControllerLeft()).setMirror(true);
            ((RestrictedController) gameplayScreen.getControllerRight()).setMirror(true);

            gameplayScreen.getMirrorTempProgressBarUI().displayFor(mirrorControlsTimer.getDurationMillis());
        }
    }

    public void initializeNonBusyContainers(int shieldsMaxCount) {
        if (nonBusyContainers == null)
            nonBusyContainers = new Array<>(false, shieldsMaxCount, BulletsAndShieldContainer.class);
    }

    public void initializeOnStartAnglesAndOnEndAngles(int shieldsMaxCount) {
        if (onStartAngles == null)
            onStartAngles = new float[shieldsMaxCount];
        else if (onStartAngles.length < shieldsMaxCount)
            onStartAngles = new float[shieldsMaxCount];

        if (onEndAngles == null)
            onEndAngles = new float[shieldsMaxCount];
        else if (onEndAngles.length < shieldsMaxCount)
            onEndAngles = new float[shieldsMaxCount];

    }

    public void initializeOldRotationDegAndNewRotationDeg(int shieldsMaxCount) {
        if (oldRotationDeg == null)
            oldRotationDeg = new float[shieldsMaxCount];
        else if (oldRotationDeg.length < shieldsMaxCount)
            oldRotationDeg = new float[shieldsMaxCount];

        if (newRotationDeg == null)
            newRotationDeg = new float[shieldsMaxCount];
        else if (newRotationDeg.length < shieldsMaxCount)
            newRotationDeg = new float[shieldsMaxCount];
    }

    private void initializeD_dizziness_rotationalSpeedTween() {

        d_dizziness_rotationalSpeedTween = new Tween(DIZZINESS_LEVEL_TIME*60*1000, D_DIZZINESS_ROTATIONAL_SPEED_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float speed = interpolation.apply(D_DIZZINESS_ROTATIONAL_SPEED_MIN, D_DIZZINESS_ROTATIONAL_SPEED_MAX, percentage);
                dizzinessBaseRotationalSpeed = speed;
                gameplayScreen.getStarsContainer().setBaseRadialVelocity(speed * MathUtils.degRad);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() == GameplayScreen.State.STOPPED)
                    gameplayScreen.getStarsContainer().setBaseRadialVelocity(0);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_dizziness_rotationalSpeedTween);
    }

    private void initializeD_bigBoss_rotationalSpeedTween() {

        d_bigBoss_rotationalSpeedTween = new Tween(BIG_BOSS_LEVEL_TIME*60*1000, D_BIG_BOSS_ROTATIONAL_SPEED_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float speed = interpolation.apply(D_BIG_BOSS_ROTATIONAL_SPEED_MIN, D_BIG_BOSS_ROTATIONAL_SPEED_MAX, percentage);
                dizzinessBaseRotationalSpeed = speed;
                gameplayScreen.getStarsContainer().setBaseRadialVelocity(speed * MathUtils.degRad);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() == GameplayScreen.State.STOPPED)
                    gameplayScreen.getStarsContainer().setBaseRadialVelocity(0);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_bigBoss_rotationalSpeedTween);
    }

    private void initializeDizzinessRotationalSpeedMultiplierTimer() {

        dizzinessRotationalSpeedMultiplierTimer = new Timer(D_DIZZINESS_FASTER_ROTATIONAL_SPEED_BULLET_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                dizzinessRotationalSpeedMultiplier = D_DIZZINESS_FASTER_ROTATIONAL_SPEED_BULLET_MULTIPLIER;
                //TextureRegion r = Assets.instance.gameplayAssets.fasterDizzinessRotationBullet;
                gameplayScreen.getFasterDizzinessRotationTempProgressBarUI().displayFor(dizzinessRotationalSpeedMultiplierTimer.getDurationMillis());
            }

            @Override
            public void onFinish() {
                super.onFinish();

                dizzinessRotationalSpeedMultiplier = 1;
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(dizzinessRotationalSpeedMultiplierTimer);
    }

    private void initializeDizzinessBaseRotationalSpeedSlowMoTween() {
        dizzinessBaseRotationalSpeedSlowMoTween = new MyTween(SLOW_MO_TWEENS_DURATION, SLOW_MO_TWEENS_INTERPOLATION) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                if (gameplayScreen.getState() == GameplayScreen.State.STOPPED) return;

                dizzinessBaseRotationalSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(dizzinessBaseRotationalSpeedSlowMoTween);
    }
}