package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
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

    public BulletsHandler(AdvancedStage game, StarsContainer.RadialTween radialTweenStars, GameplayScreen gameplayScreen) {
        game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;
        this.bulletPool = gameplayScreen.getBulletPool();

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

        //Gdx.app.log(TAG, "" + Bullet.isTherePlusOrMinus());
    }

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

    Timer getCurrentBulletsWaveTimer() {
        return currentBulletsWaveTimer;
    }

    Timer getDecreaseBulletsPerAttackTimer() {
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

    private void resetWaveTimer(/*WaveBulletsType waveBulletsType*/) {
        float duration;
        //if (waveBulletsType == WaveBulletsType.ALL_ORDINARY)
            duration = ((bulletsPerAttack + 1) * (BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_WIDTH) / BULLETS_SPEED) * 1000;
        /*else {
            duration = ;
        }*/

        if (currentBulletsWaveTimer == null) initializeCurrentBulletWave(duration);
        else currentBulletsWaveTimer.setDurationMillis(duration);

        currentBulletsWaveTimer.start();
    }

    private void attachBullets(BulletsAndShieldContainer parent) {
        WaveBulletsType waveBulletsType = WaveBulletsType.ALL_ORDINARY;
        int specialBulletOrder = 0;
        SpecialBullet specialBullet = null;

        specialBulletOrder = determineType(waveBulletsType, specialBullet);

        //------------------------------------------------------
        /*int bulletsAttached = 0;
        for (int i = 0; bulletsAttached < bulletsPerAttack; i++) {
            Bullet bullet;

            if (i < gameplayScreen.getBulletBank().size)
                bullet = gameplayScreen.getBulletBank().get(i);
            else {
                bullet = new Bullet(gameplayScreen, radialTweenStars, gameplayScreen.getStage().getViewport());
                gameplayScreen.getBulletBank().add(bullet);
            }

            if (!bullet.isInUse()) {
                bullet.attachToBulletsAndShieldContainer(parent, bulletsAttached);

                if (waveBulletsType != WaveBulletsType.ALL_ORDINARY) {
                    if (bulletsAttached == specialBulletOrder) bullet.setSpecial(specialBullet);
                    else bullet.notSpecial();
                } else bullet.notSpecial();

                bulletsAttached++;
            }
        }*/

        for (int i = 0; i < bulletsPerAttack; i++) {
            Bullet bullet = bulletPool.obtain();
            bullet.attachToBulletsAndShieldContainer(parent, i);

            if (waveBulletsType != WaveBulletsType.ALL_ORDINARY) {
                if (i == specialBulletOrder) bullet.setSpecial(specialBullet);
                else bullet.notSpecial();
            } else bullet.notSpecial();
        }
    }

    private int determineType(WaveBulletsType waveBulletsType, SpecialBullet specialBullet) {
        /*if (bulletsHandler.getBulletsPerAttack() > 1)*/
        waveBulletsType = MyMath.chooseFromProbabilityArray(WAVE_BULLETS_TYPE_PROBABILITY);
        //else waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_BAD;

        if (waveBulletsType == WaveBulletsType.HAS_A_SPECIAL_GOOD) {
            specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY);


            if (specialBullet == SpecialBullet.MINUS) {
                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShields() == SHIELDS_MIN_COUNT |
                        Bullet.isTherePlusOrMinus() |
                        !plusMinusBulletsTimer.isFinished()) {
                        /*waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_BAD;
                        specialBullet = SpecialBullet.PLUS;*/
                    specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY,
                            0,
                            GOOD_BULLETS_PROBABILITY.length - 2);
                }
            }
        } else if (waveBulletsType == WaveBulletsType.HAS_A_SPECIAL_BAD) {
            specialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY);


            if (specialBullet == SpecialBullet.PLUS) {
                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShields() == SHIELDS_MAX_COUNT |
                        Bullet.isTherePlusOrMinus() | !plusMinusBulletsTimer.isFinished()) {
                        /*waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_GOOD;
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
        if (roundTurn != null) {
            continueRoundWave();
            resetWaveTimer();
            return;
        }
        WaveAttackType waveAttackType = MyMath.chooseFromProbabilityArray(WAVE_TYPES_PROBABILITY);
        switch (waveAttackType) {
            case SINGLE:
                newSingleWave();
                break;
            case DOUBLE:
                newDoubleWave();
                break;
            case ROUND:
                newRoundWave();
                break;
        }
        resetWaveTimer();
    }

    //--------------------------------------- Simple waves methods ---------------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------------
    private void newSingleWave() {
        Gdx.app.log(TAG, "NEW SINGLE WAVE");

        attachBullets(handlePreviousCurrentContainers());
    }

    private void newDoubleWave() {
        Gdx.app.log(TAG, "NEW DOUBLE WAVE");
        attachBullets(handlePreviousCurrentContainers());
        attachBullets(handlePreviousCurrentContainers());

        /*attachBullets(probability.removeIndex(MathUtils.random(activeShields-1)));
        attachBullets(probability.get(MathUtils.random(activeShields-2)));*/
    }

    private BulletsAndShieldContainer handlePreviousCurrentContainers() {
        if (previous == null) {
            int rand = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 1);
            current = gameplayScreen.getShieldsAndContainersHandler().getProbability().removeIndex(rand);
            Gdx.app.log(TAG, "" + rand);

        } else {
            int rand = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 2);
            current = gameplayScreen.getShieldsAndContainersHandler().getProbability().removeIndex(rand);
            gameplayScreen.getShieldsAndContainersHandler().getProbability().insert(gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 2, previous);
            Gdx.app.log(TAG, "" + rand);
        }
        previous = current;
        return current;
    }

    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------
    private void newRoundWave() {
        roundType = RoundType.values()[MathUtils.random(1)];
        roundStart = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 1);
        roundTurn = roundStart;
        current = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
        roundTurnPassedActiveShieldsMinusOne = false;
        attachBullets(current);
        Gdx.app.log(TAG, "NEW ROUND WAVE, " + roundStart + ", " + roundType.toString());
    }

    private void continueRoundWave() {
        if (roundType == RoundType.ANTI_CLOCKWISE) {
            roundTurn++;
            if (roundTurn > gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 1) {
                roundTurn = 0;
                roundTurnPassedActiveShieldsMinusOne = true;
            }
            if (roundTurn >= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                roundTurn = null;
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            } else attachBullets(gameplayScreen.getBulletsAndShieldContainers()[roundTurn]);
        } else {
            roundTurn--;
            if (roundTurn < 0) {
                roundTurn = gameplayScreen.getShieldsAndContainersHandler().getActiveShields() - 1;
                roundTurnPassedActiveShieldsMinusOne = true;
            }
            if (roundTurn <= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                roundTurn = null;
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            } else attachBullets(gameplayScreen.getBulletsAndShieldContainers()[roundTurn]);
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