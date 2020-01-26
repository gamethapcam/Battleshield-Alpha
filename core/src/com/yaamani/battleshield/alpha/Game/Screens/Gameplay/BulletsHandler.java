package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsHandler implements Updatable {

    public final String TAG = GameplayScreen.TAG + "." + BulletsHandler.class.getSimpleName();

    private GameplayScreen gameplayScreen;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private Timer currentBulletsWaveTimer; // Just a timer.
    private BulletsAndShieldContainer previous;
    private BulletsAndShieldContainer current;

    private int bulletsPerAttack = BULLETS_DEFAULT_NO_PER_ATTACK;
    private Timer decreaseBulletsPerAttackTimer;

    private Timer plusMinusBulletsTimer;

    private float currentBulletSpeed;
    private float currentSpeedMultiplier;
    private Timer currentSpeedMultiplierTimer;
    //private float speedResetTime = 0;
    private MyTween currentBulletSpeedTweenStarBullet;
    private Timer starBulletFirstStage;
    private Timer starBulletSecondStage;
    private Timer starBulletThirdStage;

    private int roundStart;
    private Integer roundTurn = null;
    private RoundType roundType;
    private boolean roundTurnPassedActiveShieldsMinusOne;

    private boolean isDouble;
    private WaveBulletsType[] waveBulletsType;
    private Float angleDoubleRestricted;
    private SpecialBullet currentSpecialBullet;
    private boolean questionMark = false;

    /*private final SpecialBullet[] GOOD_BULLETS_PROBABILITY_NO_MINUS;
    private final SpecialBullet[] BAD_BULLETS_PROBABILITY_NO_PLUS;*/


    //private Timer isThereDoubleWaveTimer;

    public BulletsHandler(AdvancedStage game, GameplayScreen gameplayScreen) {
        game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;

        initializeBulletPool();

        waveBulletsType = new WaveBulletsType[2];
        waveBulletsType[0] = waveBulletsType[1] = WaveBulletsType.ORDINARY;

        //initializeNoMinusNoPlusProbability();

        initializePlusMinusBulletsTimer();

        initializeDecreaseBulletsPerAttackTimer();

        //initializeIsThereDoubleWaveTimer();

        setCurrentSpeedMultiplier(1);
        initializeCurrentSpeedMultiplierTimer();

        initializeCurrentBulletSpeedTweenStarBullet();
        initializeStarBulletFirstStage();
        initializeStarBulletSecondStage();
        initializeStarBulletThirdStage();

        resetWaveTimer();

        //currentBulletSpeed = BULLETS_SPEED_INITIAL;
    }

    @Override
    public void update(float delta) {
        if (!gameplayScreen.isVisible()) return;

        /*Gdx.app.log(TAG, "isStarted() = " + currentBulletsWaveTimer.isStarted() +
                ", isPaused() = " + currentBulletsWaveTimer.isPaused() +
                ", isInPauseDelay() = " + currentBulletsWaveTimer.isInPauseDelay() +
                ", isInStartDelay() = " + currentBulletsWaveTimer.isInStartDelay() +
                ", percentage = " + currentBulletsWaveTimer.getPercentage());*/

        currentBulletsWaveTimer.update(delta);
        plusMinusBulletsTimer.update(delta);
        decreaseBulletsPerAttackTimer.update(delta);
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            currentSpeedMultiplierTimer.update(delta);
        currentBulletSpeedTweenStarBullet.update(delta);
        starBulletFirstStage.update(delta);
        starBulletSecondStage.update(delta);
        starBulletThirdStage.update(delta);

        //isThereDoubleWaveTimer.update(delta);

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            newWave();*/

        //Gdx.app.log(TAG, "" + Bullet.isPlusOrMinusExists());
        //Gdx.app.log(TAG, "Free bullets in pool = " + bulletPool.getFree());
    }

    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------

    public void setBulletsPerAttack(int bulletsPerAttack) {
        this.bulletsPerAttack = bulletsPerAttack;
        if (!currentBulletSpeedTweenStarBullet.isStarted())
            gameplayScreen.getStarsContainer().updateCurrentStarSpeed(bulletsPerAttack);
    }

    public int getBulletsPerAttack() {
        return bulletsPerAttack;
    }

    public Pool<Bullet> getBulletPool() {
        return bulletPool;
    }

    public Array<Bullet> getActiveBullets() {
        return activeBullets;
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

    public float getCurrentSpeedMultiplier() {
        return currentSpeedMultiplier;
    }

    public void setCurrentSpeedMultiplier(float currentSpeedMultiplier) {
        this.currentSpeedMultiplier = currentSpeedMultiplier;
        if (gameplayScreen != null)
            if (gameplayScreen.getSpeedMultiplierStuff() != null)
                gameplayScreen.getSpeedMultiplierStuff().updateCharSequence(currentSpeedMultiplier);

        currentBulletSpeed = BULLETS_SPEED_INITIAL * currentSpeedMultiplier;
    }

    public Timer getCurrentSpeedMultiplierTimer() {
        return currentSpeedMultiplierTimer;
    }

    /*public void resetSpeedResetTime() {
        speedResetTime = 0;
    }*/

    public void resetCurrentSpeedMultiplier() {
        setCurrentSpeedMultiplier(1);
    }

    public void resetSpeed() {
        //speedResetTime = gameplayScreen.getTimePlayedThisTurnSoFar();
        gameplayScreen.getSpeedMultiplierStuff().getMyProgressBarTween().start();
        currentSpeedMultiplierTimer.start();
        resetCurrentSpeedMultiplier();
    }

    /*public float getSpeedResetTime() {
        return speedResetTime;
    }*/

    public float getBulletSpeed() {
        /*int i = (int) *//*floor*//* ((gameplayScreen.getTimePlayedThisTurnSoFar() - speedResetTime) / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY);
        float currentMultiplier = 1 + i * BULLETS_SPEED_MULTIPLIER_INCREMENT;

        if (currentMultiplier <= BULLETS_SPEED_MULTIPLIER_MAX) {
            //Gdx.app.log(TAG, "Speed Multiplier = " + currentMultiplier);
            return BULLETS_SPEED_INITIAL * currentMultiplier;
        }

        //Gdx.app.log(TAG, "Speed Multiplier = " + BULLETS_SPEED_MULTIPLIER_MAX);
        return BULLETS_SPEED_INITIAL * BULLETS_SPEED_MULTIPLIER_MAX;*/
        return currentBulletSpeed;
    }

    public void startCurrentBulletSpeedTweenStarBullet() {
        currentBulletSpeedTweenStarBullet.setInitialVal(getBulletSpeed());
        currentBulletSpeedTweenStarBullet.start();
    }

    public void startStarBulletStages() {
        starBulletFirstStage.start();
        starBulletSecondStage.start(STAR_BULLET_FIRST_STAGE_DURATION);
        starBulletThirdStage.start(STAR_BULLET_FIRST_STAGE_DURATION + STAR_BULLET_SECOND_STAGE_DURATION);
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

        if (currentBulletsWaveTimer == null)
            initializeCurrentBulletWave(Math.max(duration, duration1));
        else currentBulletsWaveTimer.setDurationMillis(Math.max(duration, duration1));

        //Gdx.app.log(TAG, "duration = " + duration + ", duration1 = " + duration1);
        //Gdx.app.log(TAG, "waveTimer Duration = " + currentBulletsWaveTimer.getDurationMillis());
        currentBulletsWaveTimer.start();
    }

    private float calculateWaveTimerDuration(WaveBulletsType waveBulletsType) {
        if (waveBulletsType == WaveBulletsType.ORDINARY)
            return ((BULLETS_CLEARANCE_BETWEEN_WAVES + (bulletsPerAttack) * (BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_HEIGHT)) / getBulletSpeed()) * 1000;
        else {
            return (BULLETS_SPECIAL_WAVE_LENGTH / getBulletSpeed()) * 1000;
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
        //int specialBulletOrder = 0;

        //specialBulletOrder = determineSpecialBullet(indexForDoubleWave);
        currentSpecialBullet = determineSpecialBullet(indexForDoubleWave);

        //------------------------------------------------------

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.ORDINARY) {
            for (int i = 0; i < bulletsPerAttack; i++) {
                Bullet bullet = bulletPool.obtain();
                bullet.notSpecial();
                bullet.attachNotSpecialToBulletsAndShieldContainer(parent, i);
            }
        } else {
            Bullet bullet = bulletPool.obtain();
            bullet.setSpecial(currentSpecialBullet, questionMark);
            bullet.attachSpecialToBulletsAndShieldContainer(parent/*, isDouble, indexForDoubleWave*/);
        }

    }

    private /*int*/SpecialBullet determineSpecialBullet(int indexForDoubleWave) {
        SpecialBullet currentSpecialBullet = null;

        /*if (bulletsHandler.getBulletsPerAttack() > 1)*/
        waveBulletsType[indexForDoubleWave] = MyMath.chooseFromProbabilityArray(WAVE_BULLETS_TYPE_PROBABILITY);
        if (!isDouble | (/*isDouble &*/ indexForDoubleWave == 1)) resetWaveTimer();
        //else waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_GOOD) {
            currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY);
            //Gdx.app.log(TAG, MyMath.arrayToString(GOOD_BULLETS_PROBABILITY));

            if (currentSpecialBullet == SpecialBullet.QUESTION_MARK) {
                currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK);
                questionMark = true;
                Gdx.app.log(TAG, "Question Mark (" + currentSpecialBullet + ").");
            } else {
                questionMark = false;
                Gdx.app.log(TAG, "" + currentSpecialBullet);
            }

            if (currentSpecialBullet == SpecialBullet.STAR) {

                Gdx.app.log(TAG, "Possible STAR -> " +
                        Bullet.isPlusOrMinusExists() + ", " +
                        Bullet.isStarExists() + ", " +
                        !plusMinusBulletsTimer.isFinished());

                if (Bullet.isPlusOrMinusExists() |
                        Bullet.isStarExists() |
                        !plusMinusBulletsTimer.isFinished()) {
                    currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);
                } else Bullet.setStarExists(true);
            }

            if (currentSpecialBullet == SpecialBullet.MINUS) {

                Gdx.app.log(TAG, "Possible MINUS -> " + (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MIN_COUNT) + ", " +
                        Bullet.isPlusOrMinusExists() + ", " +
                        Bullet.isStarExists() + ", " +
                        !plusMinusBulletsTimer.isFinished() + ", " +
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED));

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MIN_COUNT |
                        Bullet.isPlusOrMinusExists() |
                        Bullet.isStarExists() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED/* & indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;
                        currentSpecialBullet = SpecialBullet.PLUS;*/

                    //currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY, SpecialBullet.MINUS);
                    currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY_NO_MINUS, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);
                }
            }
        } else if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_BAD) {
            currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY);

            if (currentSpecialBullet == SpecialBullet.QUESTION_MARK) {
                currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK);
                questionMark = true;
                Gdx.app.log(TAG, "Question Mark (" + currentSpecialBullet + ").");
            } else {
                questionMark = false;
                Gdx.app.log(TAG, "" + currentSpecialBullet);
            }

            if (currentSpecialBullet == SpecialBullet.PLUS) {

                Gdx.app.log(TAG, "Possible PLUS -> " + (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MAX_COUNT) + ", " +
                        Bullet.isPlusOrMinusExists() + ", " +
                        Bullet.isStarExists() + ", " +
                        !plusMinusBulletsTimer.isFinished() + ", " +
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED));

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MAX_COUNT |
                        Bullet.isPlusOrMinusExists() |
                        Bullet.isStarExists() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayType() == GameplayType.RESTRICTED /*& indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_GOOD;
                        currentSpecialBullet = SpecialBullet.MINUS;*/
                    //currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY, SpecialBullet.PLUS);
                    currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY_NO_PLUS, SpecialBullet.QUESTION_MARK);
                }
            }
        }

        if (currentSpecialBullet == SpecialBullet.PLUS | currentSpecialBullet == SpecialBullet.MINUS) {
            Bullet.setPlusOrMinusExists(true);
        }

        /*if (!questionMark)
            Gdx.app.log(TAG, "" + currentSpecialBullet);*/

        //return MathUtils.random(bulletsPerAttack - 1);
        return currentSpecialBullet;
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
                //Gdx.app.log(TAG, "<<<<<<<<<<< Can be double >>>>>>>>>>> " + Bullet.isPlusOrMinusExists() + ", " + plusMinusBulletsTimer.isFinished());

                if (gameplayScreen.getGameplayType() == GameplayType.RESTRICTED & Bullet.isPlusOrMinusExists() & plusMinusBulletsTimer.isFinished()) {
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

    //--------------------------------------- Simple waves methods ---------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------

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
                angleDoubleRestricted = MyMath.deg_0_to_360(current.getRotation());
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
                if (MyMath.deg_0_to_360(probability.get(i).getRotation()) > 180) {
                    current = probability.removeIndex(i);
                    if (previous != null) probability.add(previous);
                    return;
                }
            }

        } else {

            for (int i = 0; i < probability.size; i++) {
                if (MyMath.deg_0_to_360(probability.get(i).getRotation()) < 180) {
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
            int rand = MathUtils.random(activeShieldsNum - 1);
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

    private void initializeBulletPool() {
        bulletPool = new Pool<Bullet>(BULLETS_POOL_INITIAL_CAPACITY) {
            @Override
            protected Bullet newObject() {
                return new Bullet(gameplayScreen, gameplayScreen.getStarsContainer()/*.getRadialTween()*/, gameplayScreen.getStage().getViewport());
            }

            @Override
            public void free(Bullet object) {
                super.free(object);
                //Gdx.app.log(TAG, "Free bullets in pool = " + getFree());
            }

            @Override
            public Bullet obtain() {
                Bullet bullet = super.obtain();
                activeBullets.add(bullet);
                return bullet;
            }
        };

        activeBullets = new Array<Bullet>(false, 40, Bullet.class);
    }

    private void initializeCurrentBulletWave(final float duration) {
        currentBulletsWaveTimer = new Timer(duration) {

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "------------------------------------------- Finished -------------------------------------------");
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            }
        };
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentBulletsWaveTimer);
    }

    private void initializePlusMinusBulletsTimer() {
        plusMinusBulletsTimer = new Timer(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION) {

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
            }
        };

        plusMinusBulletsTimer.start();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(plusMinusBulletsTimer);
    }

    private void initializeDecreaseBulletsPerAttackTimer() {
        decreaseBulletsPerAttackTimer = new Timer(BULLETS_DECREASE_NO_PER_ATTACK_EVERY) {

            @Override
            public void onFinish() {
                super.onFinish();
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    if (bulletsPerAttack > BULLETS_MIN_NUMBER_PER_ATTACK) {
                        setBulletsPerAttack(bulletsPerAttack-1);
                        decreaseBulletsPerAttackTimer.start();
                        //Gdx.app.log(TAG, "decreaseBulletsPerAttackTimer isFinished");
                    }
            }
        };

        decreaseBulletsPerAttackTimer.start();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(decreaseBulletsPerAttackTimer);
    }

    private void initializeCurrentSpeedMultiplierTimer() {
        currentSpeedMultiplierTimer = new Timer(BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY * 1000f) {
            @Override
            public void onFinish() {
                //float nextSpeedMultiplier = getBulletSpeed() / BULLETS_SPEED_INITIAL;
                float nextSpeedMultiplier = MyMath.roundTo(currentSpeedMultiplier + BULLETS_SPEED_MULTIPLIER_INCREMENT, 5);
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING &
                        nextSpeedMultiplier <= BULLETS_SPEED_MULTIPLIER_MAX) {
                    setCurrentSpeedMultiplier(nextSpeedMultiplier);
                    //currentSpeedMultiplier = nextSpeedMultiplier;
                    //currentSpeedMultiplier = MyMath.roundTo(currentSpeedMultiplier, 5);
                    //gameplayScreen.getSpeedMultiplierStuff().updateCharSequence(currentSpeedMultiplier);
                }
                if (currentSpeedMultiplier < BULLETS_SPEED_MULTIPLIER_MAX) {
                    gameplayScreen.getSpeedMultiplierStuff().getMyProgressBarTween().start();
                    start();
                }
            }
        };

        currentSpeedMultiplierTimer.start();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentSpeedMultiplierTimer);
    }

    private void initializeCurrentBulletSpeedTweenStarBullet() {
        currentBulletSpeedTweenStarBullet = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION, BULLETS_SPEED_INITIAL, 0) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                currentBulletSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentBulletSpeedTweenStarBullet);
    }

    private void initializeStarBulletFirstStage() {
        starBulletFirstStage = new Timer(STAR_BULLET_FIRST_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                startCurrentBulletSpeedTweenStarBullet();
                gameplayScreen.getStarsContainer().startCurrentSpeedTweenStarBullet();
                gameplayScreen.startScoreTimePlayedThisTurnSoFarTweenStarBullet();
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(starBulletFirstStage);
    }

    private void initializeStarBulletSecondStage() {
        starBulletSecondStage = new Timer(STAR_BULLET_SECOND_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                gameplayScreen.getStarsContainer().setInWarpTrailAnimation(true);
                gameplayScreen.getStarsContainer().getWarpStretchFactorTweenStarBullet().start();
                gameplayScreen.startWhiteTextureHidesEveryThingSecondStageTweenStarBullet();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                //gameplayScreen.getStarsContainer().setInWarpTrailAnimation(false);
                Array<Float> fpss = gameplayScreen.getStarsContainer().getFpss();
                //Gdx.app.log(TAG, ""+MyMath.arrayToString(fpss.items));
                //if (fpss.size > 0)
                Gdx.app.log(TAG, MyMath.arrayStatistics(fpss.items, false));

                //gameplayScreen.getStarsContainer().clearBlurBuffers();
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(starBulletSecondStage);
    }

    private void initializeStarBulletThirdStage() {
        starBulletThirdStage = new Timer(STAR_BULLET_THIRD_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(starBulletThirdStage);
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

    /*private void initializeNoMinusNoPlusProbability() {
        GOOD_BULLETS_PROBABILITY_NO_MINUS = new SpecialBullet[GOOD_BULLETS_PROBABILITY.length];
    }*/
}