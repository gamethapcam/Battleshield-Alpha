package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsHandler implements Updatable {

    public final String TAG = GameplayScreen.TAG + "." + BulletsHandler.class.getSimpleName();

    private GameplayScreen gameplayScreen;

    private Pool<Bullet> bulletPool;

    private Timer currentBulletsWaveTimer; // Just a timer.
    private BulletsAndShieldContainer previous;
    private BulletsAndShieldContainer current;

    private int bulletsPerAttack = BULLETS_DEFAULT_NO_PER_ATTACK;
    private Timer decreaseBulletsPerAttackTimer;

    private Timer plusMinusBulletsTimer;

    private StarsContainer.RadialTween radialTweenStars;

    private int roundStart;
    private Integer roundTurn = null;
    private RoundType roundType;
    private boolean roundTurnPassedActiveShieldsMinusOne;

    private boolean isDouble;
    private WaveBulletsType[] waveBulletsType;
    private Float angleDoubleRestricted;
    private SpecialBullet specialBullet;

    //private Timer isThereDoubleWaveTimer;

    public BulletsHandler(AdvancedStage game, StarsContainer.RadialTween radialTweenStars, GameplayScreen gameplayScreen) {
        game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;
        this.radialTweenStars = radialTweenStars;
        this.bulletPool = gameplayScreen.getBulletPool();

        waveBulletsType = new WaveBulletsType[2];
        waveBulletsType[0] = waveBulletsType[1] = WaveBulletsType.ORDINARY;

        initializePlusMinusBulletsTimer();

        initializeDecreaseBulletsPerAttackTimer();

        //initializeIsThereDoubleWaveTimer();

        resetWaveTimer();

    }

    @Override
    public void update(float delta) {
        if (!gameplayScreen.isVisible()) return;

        currentBulletsWaveTimer.update(delta);
        plusMinusBulletsTimer.update(delta);
        decreaseBulletsPerAttackTimer.update(delta);
        //isThereDoubleWaveTimer.update(delta);

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            newWave();*/

        //Gdx.app.log(TAG, "" + Bullet.isTherePlusOrMinus());
        //Gdx.app.log(TAG, "Free bullets in pool = " + bulletPool.getFree());
    }

    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------

    public void setBulletsPerAttack(int bulletsPerAttack) {
        this.bulletsPerAttack = bulletsPerAttack;
    }

    public int getBulletsPerAttack() {
        return bulletsPerAttack;
    }

    /*public void setPrevious(BulletsAndShieldContainer previous) {
        this.previous = previous;
    }

    public void setCurrent(BulletsAndShieldContainer current) {
        this.current = current;
    }*/

    public StarsContainer.RadialTween getRadialTweenStars() {
        return radialTweenStars;
    }

    public Timer getCurrentBulletsWaveTimer() {
        return currentBulletsWaveTimer;
    }

    public Timer getDecreaseBulletsPerAttackTimer() {
        return decreaseBulletsPerAttackTimer;
    }

    public BulletsAndShieldContainer getPrevious() {
        return previous;
    }

    public BulletsAndShieldContainer getCurrent() {
        return current;
    }

    public void setPrevious(BulletsAndShieldContainer previous) {
        this.previous = previous;
    }

    public void setCurrent(BulletsAndShieldContainer current) {
        this.current = current;
    }

    public void setRoundTurn(Integer roundTurn) {
        this.roundTurn = roundTurn;
    }

    public void startPlusMinusBulletsTween() {
        plusMinusBulletsTimer.start();
    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    private void resetWaveTimer() {
        float duration, duration1;

        duration = calculateWaveTimerDuration(waveBulletsType[0]);
        duration1 = 0;

        if (isDouble) {
            duration1 = calculateWaveTimerDuration(waveBulletsType[1]);
        }

        if (currentBulletsWaveTimer == null) initializeCurrentBulletWave(Math.max(duration, duration1));
        else currentBulletsWaveTimer.setDurationMillis(Math.max(duration, duration1));

        //Gdx.app.log(TAG, "waveTimer Duration = " + currentBulletsWaveTimer.getDurationMillis());
        currentBulletsWaveTimer.start();
    }

    private float calculateWaveTimerDuration(WaveBulletsType waveBulletsType) {
        if (waveBulletsType == WaveBulletsType.ORDINARY)
            return ((BULLETS_CLEARANCE_BETWEEN_WAVES + (bulletsPerAttack) * (BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_HEIGHT)) / Bullet.getSpeed()) * 1000;
        else {
            return (BULLETS_SPECIAL_WAVE_LENGTH / Bullet.getSpeed()) * 1000;
        }
    }

    /*private void resetIsThereDoubleWaveTimer() {
        isThereDoubleWaveTimer.setDurationMillis(currentBulletsWaveTimer.getDurationMillis() - BULLETS_CLEARANCE_BETWEEN_WAVES + (Bullet.getR() - SHIELDS_RADIUS)/BULLETS_SPEED_INITIAL*1000);
        // Because of the duration of this Timer depends on the currentBulletsWaveTimer duration, you must call this function after resetWaveTimer().

        Gdx.app.log(TAG, "doubleWaveTimer Duration = " + isThereDoubleWaveTimer.getDurationMillis());
        isThereDoubleWaveTimer.start();
    }*/

    private void attachBullets(BulletsAndShieldContainer parent, int indexForDoubleWave) {
        waveBulletsType[indexForDoubleWave] = WaveBulletsType.ORDINARY;
        int specialBulletOrder = 0;
        specialBullet = null;

        specialBulletOrder = determineType(indexForDoubleWave);

        //------------------------------------------------------

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.ORDINARY) {
            for (int i = 0; i < bulletsPerAttack; i++) {
                Bullet bullet = bulletPool.obtain();
                bullet.notSpecial();
                bullet.attachNotSpecialToBulletsAndShieldContainer(parent, i);
            }
        } else {
            Bullet bullet = bulletPool.obtain();
            bullet.setSpecial(specialBullet);
            bullet.attachSpecialToBulletsAndShieldContainer(parent, isDouble, indexForDoubleWave);
        }

    }

    private int determineType(int indexForDoubleWave) {
        /*if (bulletsHandler.getBulletsPerAttack() > 1)*/
        waveBulletsType[indexForDoubleWave] = MyMath.chooseFromProbabilityArray(WAVE_BULLETS_TYPE_PROBABILITY);
        if (!isDouble | (/*isDouble &*/ indexForDoubleWave == 1)) resetWaveTimer();
        //else waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_GOOD) {
            specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY);


            if (specialBullet == SpecialBullet.MINUS) {
                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MIN_COUNT |
                        Bullet.isTherePlusOrMinus() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED/* & indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;
                        specialBullet = SpecialBullet.PLUS;*/
                    specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY,
                            0,
                            GOOD_BULLETS_PROBABILITY.length - 2);
                }
            }
        } else if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_BAD) {
            specialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY);


            if (specialBullet == SpecialBullet.PLUS) {
                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MAX_COUNT |
                        Bullet.isTherePlusOrMinus() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED /*& indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_GOOD;
                        specialBullet = SpecialBullet.MINUS;*/
                    specialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY,
                            0,
                            BAD_BULLETS_PROBABILITY.length - 2);
                }
            }
        }

        if (specialBullet == SpecialBullet.PLUS | specialBullet == SpecialBullet.MINUS) {
            Bullet.setThereIsPlusOrMinus(true);
        }

        return MathUtils.random(bulletsPerAttack - 1);
    }

    public void newWave() {
        //if (!isVisible()) return;
        isDouble = false;

        if (roundTurn != null) {
            continueRoundWave();
            //resetWaveTimer();
            return;
        }
        WaveAttackType waveAttackType = MyMath.chooseFromProbabilityArray(WAVE_TYPES_PROBABILITY);

        switch (waveAttackType) {
            case SINGLE:
                newSingleWave();
                //newDoubleWave();
                break;
            case DOUBLE:
                //newSingleWave();
                //Gdx.app.log(TAG, "<<<<<<<<<<< Can be double >>>>>>>>>>> " + Bullet.isTherePlusOrMinus() + ", " + plusMinusBulletsTimer.isFinished());

                if (gameplayScreen.getGameplayType() == GameplayType.RESTRICTED & Bullet.isTherePlusOrMinus() & plusMinusBulletsTimer.isFinished()) {
                    if (MathUtils.random(1) == 0) newRoundWave();
                    else newSingleWave();
                } else newDoubleWave();

                break;
            case ROUND:
                //newSingleWave();
                //newDoubleWave();
                newRoundWave();
                break;
        }
        //resetWaveTimer();
    }

    //--------------------------------------- Simple waves methods ---------------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------------

    private void newSingleWave() {
        //Gdx.app.log(TAG, "NEW SINGLE WAVE");

        attachBullets(handlePreviousCurrentContainers(), 0);
    }

    private void newDoubleWave() {
        isDouble = true;
        //Gdx.app.log(TAG, "NEW DOUBLE WAVE");
        attachBullets(handlePreviousCurrentContainers(), 0);
        attachBullets(handlePreviousCurrentContainers(), 1);
        //resetIsThereDoubleWaveTimer();

        /*attachBullets(probability.removeIndex(MathUtils.random(activeShields-1)));
        attachBullets(probability.get(MathUtils.random(activeShields-2)));*/
    }

    private BulletsAndShieldContainer handlePreviousCurrentContainers() {

        Array<BulletsAndShieldContainer> probability = gameplayScreen.getShieldsAndContainersHandler().getProbability();

        if (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED & angleDoubleRestricted != null) {
            chooseContainerOnTheOtherSide(probability);

            angleDoubleRestricted = null;

        } else {
            chooseRandomContainer(probability);

            angleDoubleRestricted = null;

            if (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED)
                angleDoubleRestricted = current.getRotation();
        }

        /*for (BulletsAndShieldContainer container : probability) {
            Gdx.app.log(TAG, "" + container.getRotation());
        }*/

        previous = current;
        return current/*probability.get(0)*/;
    }

    private void chooseContainerOnTheOtherSide(Array<BulletsAndShieldContainer> probability) {
        // If the current gameplay is restricted and the current wave is double, you must choose one container on the left (rotation > 180) and the other on the right (rotation < 180) (This is because of the restrictions of the controls). But if one container is chosen on the top (rotation = 0), the other container can be any other one.
        if (angleDoubleRestricted == 0)
            chooseRandomContainer(probability);
        else if (angleDoubleRestricted < 180) {

            for (int i = 0; i < probability.size; i++) {
                if (probability.get(i).getRotation() > 180) {
                    current = probability.removeIndex(i);
                    if (previous != null) probability.add(previous);
                    return;
                }
            }

        } else {

            for (int i = 0; i < probability.size; i++) {
                if (probability.get(i).getRotation() < 180) {
                    current = probability.removeIndex(i);
                    if (previous != null) probability.add(previous);
                    return;
                }
            }

        }
    }

    private void chooseRandomContainer(Array<BulletsAndShieldContainer> probability) {
        int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();

        if (previous == null) {
            int rand = MathUtils.random( activeShieldsNum - 1);
            current = probability.removeIndex(rand);
            //Gdx.app.log(TAG, "" + rand);

        } else {
            int rand = MathUtils.random(activeShieldsNum - 2);
            current = probability.removeIndex(rand);
            //probability.insert(activeShieldsNum - 2, previous);
            probability.add(previous);
            //Gdx.app.log(TAG, "" + rand);
        }
    }

    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------

    private void newRoundWave() {
        roundType = RoundType.values()[MathUtils.random(1)];
        roundStart = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1);
        roundTurn = roundStart;
        current = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
        roundTurnPassedActiveShieldsMinusOne = false;
        attachBullets(current, 0);
        //Gdx.app.log(TAG, "NEW ROUND WAVE, " + roundStart + ", " + roundType.toString());
    }

    private void continueRoundWave() {
        if (roundType == RoundType.ANTI_CLOCKWISE) {
            roundTurn++;
            if (roundTurn > gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1) {
                roundTurn = 0;
                roundTurnPassedActiveShieldsMinusOne = true;
            }
            if (roundTurn >= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                roundTurn = null;
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            } else attachBullets(gameplayScreen.getBulletsAndShieldContainers()[roundTurn], 0);
        } else {
            roundTurn--;
            if (roundTurn < 0) {
                roundTurn = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1;
                roundTurnPassedActiveShieldsMinusOne = true;
            }
            if (roundTurn <= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                roundTurn = null;
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            } else attachBullets(gameplayScreen.getBulletsAndShieldContainers()[roundTurn], 0);
        }
        //Gdx.app.log(TAG, "CONTINUE ROUND, " + roundTurn);
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeCurrentBulletWave(final float duration) {
        currentBulletsWaveTimer = new Timer(duration) {
            @Override
            public void onUpdate(float delta) {
                //Gdx.app.log(bulletsHandler.TAG, "duration = " + duration);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "------------------------------------------- Finished -------------------------------------------");
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            }
        };
    }

    private void initializePlusMinusBulletsTimer() {
        plusMinusBulletsTimer = new Timer(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION) {
            @Override
            public void onUpdate(float delta) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
            }
        };

        plusMinusBulletsTimer.start();
    }

    private void initializeDecreaseBulletsPerAttackTimer() {
        decreaseBulletsPerAttackTimer = new Timer(BULLETS_DECREASE_NO_PER_ATTACK_EVERY) {
            @Override
            public void onUpdate(float delta) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    if (bulletsPerAttack > BULLETS_MIN_NUMBER_PER_ATTACK) {
                        bulletsPerAttack--;
                        decreaseBulletsPerAttackTimer.start();
                        //Gdx.app.log(TAG, "decreaseBulletsPerAttackTimer isFinished");
                    }
            }
        };

        decreaseBulletsPerAttackTimer.start();
    }

    /*private void initializeIsThereDoubleWaveTimer() {
        isThereDoubleWaveTimer = new Timer(0 *//*It doesn't matter. The duration will be changed during the gameplay*//*) {
            @Override
            public void onFinish() {
                Gdx.app.log(TAG, "isThereDoubleWaveTimer FINISHED");
            }
        };

        isThereDoubleWaveTimer.finish();
    }*/
}