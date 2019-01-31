package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Bullet;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;
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
    private SpecialBullet specialBullet;

    public BulletsHandler(AdvancedStage game, StarsContainer.RadialTween radialTweenStars, GameplayScreen gameplayScreen) {
        game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;
        this.bulletPool = gameplayScreen.getBulletPool();

        waveBulletsType = new WaveBulletsType[2];
        waveBulletsType[0] = waveBulletsType[1] = WaveBulletsType.ORDINARY;

        initializePlusMinusBulletsTimer();

        initializeDecreaseBulletsPerAttackTimer();

        resetWaveTimer();

        this.radialTweenStars = radialTweenStars;
    }

    @Override
    public void update(float delta) {
        if (!gameplayScreen.isVisible()) return;

        currentBulletsWaveTimer.update(delta);
        plusMinusBulletsTimer.update(delta);
        decreaseBulletsPerAttackTimer.update(delta);

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            newWave();*/

        //Gdx.app.log(TAG, "" + Bullet.isTherePlusOrMinus());
    }

    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------

    void setBulletsPerAttack(int bulletsPerAttack) {
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

        duration = calculateDuration(waveBulletsType[0]);
        duration1 = 0;

        if (isDouble) {
            duration1 = calculateDuration(waveBulletsType[1]);
        }

        if (currentBulletsWaveTimer == null) initializeCurrentBulletWave(Math.max(duration, duration1));
        else currentBulletsWaveTimer.setDurationMillis(Math.max(duration, duration1));

        currentBulletsWaveTimer.start();
    }

    private float calculateDuration(WaveBulletsType waveBulletsType) {
        if (waveBulletsType == WaveBulletsType.ORDINARY)
            return ((BULLETS_CLEARANCE_BETWEEN_WAVES + (bulletsPerAttack) * (BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_WIDTH)) / BULLETS_SPEED) * 1000;
        else {
            return (BULLETS_SPECIAL_WAVE_LENGTH / BULLETS_SPEED) * 1000;
        }
    }

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
                        !plusMinusBulletsTimer.isFinished()) {
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
                        Bullet.isTherePlusOrMinus() | !plusMinusBulletsTimer.isFinished()) {
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
                newDoubleWave();
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
        Gdx.app.log(TAG, "NEW SINGLE WAVE");

        attachBullets(handlePreviousCurrentContainers(), 0);
    }

    private void newDoubleWave() {
        isDouble = true;
        Gdx.app.log(TAG, "NEW DOUBLE WAVE");
        attachBullets(handlePreviousCurrentContainers(), 0);
        attachBullets(handlePreviousCurrentContainers(), 1);

        /*attachBullets(probability.removeIndex(MathUtils.random(activeShields-1)));
        attachBullets(probability.get(MathUtils.random(activeShields-2)));*/
    }

    private BulletsAndShieldContainer handlePreviousCurrentContainers() {
        int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();
        Array<BulletsAndShieldContainer> probability = gameplayScreen.getShieldsAndContainersHandler().getProbability();

        if (previous == null) {
            int rand = MathUtils.random( activeShieldsNum - 1);
            current = probability.removeIndex(rand);
            //Gdx.app.log(TAG, "" + rand);

        } else {
            int rand = MathUtils.random(activeShieldsNum - 2);
            current = probability.removeIndex(rand);
            probability.insert(activeShieldsNum - 2, previous);
            //Gdx.app.log(TAG, "" + rand);
        }

        previous = current;
        return current/*probability.get(0)*/;
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
        Gdx.app.log(TAG, "NEW ROUND WAVE, " + roundStart + ", " + roundType.toString());
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
        Gdx.app.log(TAG, "CONTINUE ROUND, " + roundTurn);
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
                Gdx.app.log(TAG, "------------------------------------------- Finished -------------------------------------------");
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
                Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
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
                        Gdx.app.log(TAG, "decreaseBulletsPerAttackTimer isFinished");
                    }
            }
        };

        decreaseBulletsPerAttackTimer.start();
    }
}