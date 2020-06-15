package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Iterator;
import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsHandler implements Updatable {

    public final String TAG = BulletsHandler.class.getSimpleName();

    private GameplayScreen gameplayScreen;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private Bullet currentWaveLastBullet;

    //private Timer currentBulletsWaveTimer; // Just a timer.
    private Array<BulletsAndShieldContainer> busyContainers; // Containers with bullets attached during the current wave.
    private BulletsAndShieldContainer previous;
    private BulletsAndShieldContainer current;

    private int bulletsPerAttack = BULLETS_DEFAULT_NO_PER_ATTACK;
    //private Timer decreaseBulletsPerAttackTimer;
    private Tween bulletsPerAttackNumberDifficultyTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Timer plusMinusBulletsTimer;

    private float currentBulletSpeed;
    private float currentSpeedMultiplier;
    //private Timer currentDifficultyLevelTimer;
    private Tween bulletSpeedMultiplierDifficultyTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    //private float speedResetTime = 0;
    private MyTween currentBulletSpeedTweenStarBullet_FirstStage;
    private MyTween currentBulletSpeedTweenStarBullet_ThirdStage;

    private Timer starBulletFirstStage; // Bullets Slow mo
    private Timer starBulletSecondStage; // Stars trails
    private Timer starBulletThirdStage; // Stars warp fast

    private int roundStart;
    private Integer roundTurn = null;
    private RoundType roundType;
    private boolean roundTurnPassedActiveShieldsMinusOne;

    private boolean isDouble;
    private WaveBulletsType[] waveBulletsType;
    private Float angleDoubleRestricted;
    private SpecialBullet currentSpecialBullet;
    private boolean questionMark = false;


    private SpecialBullet[] currentPlanetSpecialBullets;
    // private SpecialBulletType[] currentPlanetSpecialBulletsType;
    private float currentPlanetSpecialBulletsProbability;


    /*private final SpecialBullet[] GOOD_BULLETS_PROBABILITY_NO_MINUS;
    private final SpecialBullet[] BAD_BULLETS_PROBABILITY_NO_PLUS;*/


    //private Timer isThereDoubleWaveTimer;

    public BulletsHandler(AdvancedStage game, GameplayScreen gameplayScreen) {
        //game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;

        initializeBulletPool();

        waveBulletsType = new WaveBulletsType[2];
        waveBulletsType[0] = waveBulletsType[1] = WaveBulletsType.ORDINARY;

        busyContainers = new Array<>(false, 8, BulletsAndShieldContainer.class);

        //initializeNoMinusNoPlusProbability();

        initializePlusMinusBulletsTimer();

        initializeBulletsPerAttackNumberTween();

        //initializeIsThereDoubleWaveTimer();

        setCurrentSpeedMultiplier(1);
        initializeCurrentSpeedMultiplierTimer();

        //initializeCurrentDifficultLevelTimer();

        initializeCurrentBulletSpeedTweenStarBullet_FirstStage();
        initializeCurrentBulletSpeedTweenStarBullet_ThirdStage();

        initializeStarBulletFirstStage();
        initializeStarBulletSecondStage();
        initializeStarBulletThirdStage();

        //resetWaveTimer();

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

        //currentBulletsWaveTimer.update(delta);
        plusMinusBulletsTimer.update(delta);
        //decreaseBulletsPerAttackTimer.update(delta);
        bulletsPerAttackNumberDifficultyTween.update(delta);
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
            //currentDifficultyLevelTimer.update(delta);
            bulletSpeedMultiplierDifficultyTween.update(delta);
        }
        currentBulletSpeedTweenStarBullet_FirstStage.update(delta);
        currentBulletSpeedTweenStarBullet_ThirdStage.update(delta);
        starBulletFirstStage.update(delta);
        starBulletSecondStage.update(delta);
        starBulletThirdStage.update(delta);

        handleNewWave();

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
        if (!currentBulletSpeedTweenStarBullet_FirstStage.isStarted())
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

    /*public Timer getCurrentBulletsWaveTimer() {
        return currentBulletsWaveTimer;
    }*/

    /*public Timer getDecreaseBulletsPerAttackTimer() {
        return decreaseBulletsPerAttackTimer;
    }*/

    public Tween getBulletsPerAttackNumberDifficultyTween() {
        return bulletsPerAttackNumberDifficultyTween;
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

    public void clearBusyContainers() {
        busyContainers.clear();
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

    public void setCurrentSpeedMultiplier(float newSpeedMultiplier) {
        this.currentSpeedMultiplier = newSpeedMultiplier;
        /*if (gameplayScreen != null) {
            if (gameplayScreen.getScoreStuff() != null)
                if (gameplayScreen.getScoreStuff().getScoreMultiplierStuff() != null)
                    gameplayScreen.getScoreStuff().getScoreMultiplierStuff().updateCharSequence(newSpeedMultiplier);
        }*/

        currentBulletSpeed = BULLETS_SPEED_INITIAL * newSpeedMultiplier;
    }

    /*public Timer getCurrentDifficultyLevelTimer() {
        return currentDifficultyLevelTimer;
    }*/

    public Tween getBulletSpeedMultiplierDifficultyTween() {
        return bulletSpeedMultiplierDifficultyTween;
    }

    /*public SpecialBullet[] getCurrentPlanetSpecialBullets() {
        return currentPlanetSpecialBullets;
    }

    public SpecialBulletType[] getCurrentPlanetSpecialBulletsType() {
        return currentPlanetSpecialBulletsType;
    }*/

    void setCurrentPlanetSpecialBullets(SpecialBullet[] currentPlanetSpecialBullets) {
        this.currentPlanetSpecialBullets = currentPlanetSpecialBullets;
    }

    /*void setCurrentPlanetSpecialBulletsType(SpecialBulletType[] currentPlanetSpecialBulletsType) {
        this.currentPlanetSpecialBulletsType = currentPlanetSpecialBulletsType;
    }*/

    public void setCurrentPlanetSpecialBulletsProbability(float currentPlanetSpecialBulletsProbability) {
        this.currentPlanetSpecialBulletsProbability = currentPlanetSpecialBulletsProbability;
    }

    /*public void resetSpeedResetTime() {
        speedResetTime = 0;
    }*/

    public void resetCurrentSpeedMultiplier() {
        setCurrentSpeedMultiplier(1);
    }

    public void resetSpeed() {
        //speedResetTime = gameplayScreen.getTimePlayedThisTurnSoFar();
        gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().start();
        //currentDifficultyLevelTimer.start();
        bulletSpeedMultiplierDifficultyTween.start();
        resetCurrentSpeedMultiplier();
    }

    public void decrementCurrentSpeedMultiplier() {
        gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().start();
        //currentDifficultyLevelTimer.start();
        bulletSpeedMultiplierDifficultyTween.start();

        if (getCurrentSpeedMultiplier() != 1)
            setCurrentSpeedMultiplier(getCurrentSpeedMultiplier() - BULLETS_SPEED_MULTIPLIER_INCREMENT);
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

    public void startCurrentBulletSpeedTweenStarBullet_FirstStage() {
        currentBulletSpeedTweenStarBullet_FirstStage.setInitialVal(getBulletSpeed());
        currentBulletSpeedTweenStarBullet_FirstStage.setFinalVal(0);
        currentBulletSpeedTweenStarBullet_FirstStage.start();
    }

    public void startCurrentBulletSpeedTweenStarBullet_ThirdStage() {
        setCurrentSpeedMultiplier(getCurrentSpeedMultiplier());
        float bulletSpeedAfterStarBullet = getBulletSpeed();

        currentBulletSpeedTweenStarBullet_ThirdStage.setInitialVal(0);
        currentBulletSpeedTweenStarBullet_ThirdStage.setFinalVal(bulletSpeedAfterStarBullet);
        currentBulletSpeedTweenStarBullet_ThirdStage.start();
    }

    public void startStarBulletStages() {
        starBulletFirstStage.start();
        //starBulletSecondStage.start(STAR_BULLET_FIRST_STAGE_DURATION);
        //starBulletThirdStage.start(STAR_BULLET_FIRST_STAGE_DURATION + STAR_BULLET_SECOND_STAGE_DURATION);

        //getCurrentBulletsWaveTimer().pause();
        getBulletSpeedMultiplierDifficultyTween().pause();
        getBulletsPerAttackNumberDifficultyTween().pause();
        gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierTween().pause();
        gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().pause();
    }

    public Bullet getCurrentWaveLastBullet() {
        return currentWaveLastBullet;
    }

    public void nullifyCurrentWaveLastBullet() {
        currentWaveLastBullet = null;
    }

    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------

    /*private void resetWaveTimer() {
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
    }*/


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

        Bullet bullet = null;

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.ORDINARY) {
            for (int i = 0; i < bulletsPerAttack; i++) {
                bullet = bulletPool.obtain();

                bullet.notSpecial();
                bullet.attachNotSpecialToBulletsAndShieldContainer(parent, i);
            }
        } else {
            bullet = bulletPool.obtain();
            bullet.setSpecial(currentSpecialBullet, questionMark);
            bullet.attachSpecialToBulletsAndShieldContainer(parent/*, isDouble, indexForDoubleWave*/);
        }



        if (bullet != null) {
            if (indexForDoubleWave == 1 & currentWaveLastBullet != null) {
                if (currentWaveLastBullet.getY() < bullet.getY())
                    currentWaveLastBullet = bullet;
            } else
                currentWaveLastBullet = bullet;
        }

    }

    private void handleNewWave() {
        // If all containers are transparent -> return
        for (int i = 0; i < gameplayScreen.getBulletsAndShieldContainers().length; i++) {
            if (gameplayScreen.getBulletsAndShieldContainers()[i].getColor().a == 1)
                break;

            if (i == gameplayScreen.getBulletsAndShieldContainers().length - 1)
                return;
        }

        if (currentWaveLastBullet != null) {
            if (currentWaveLastBullet.getBulletType() == Bullet.BulletType.ORDINARY) {

                if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_ORDINARY_HEIGHT + BULLETS_CLEARANCE_BETWEEN_WAVES))
                    newWave();

            } else {

                if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_SPECIAL_DIAMETER / 2f + BULLETS_SPECIAL_WAVE_LENGTH / 2f + BULLETS_CLEARANCE_BETWEEN_WAVES))
                    newWave();

            }
        } else {
            /*for (BulletsAndShieldContainer container:gameplayScreen.getBulletsAndShieldContainers()) {
                Gdx.app.log(TAG, "index = " + container.getIndex() + ", a = " + container.getColor().a);
            }*/
            newWave();
            //Gdx.app.log(TAG, "null");
        }
    }

    private /*int*/SpecialBullet determineSpecialBullet(int indexForDoubleWave) {
        SpecialBullet currentSpecialBullet = null;


        /*if (bulletsHandler.getBulletsPerAttack() > 1)*/
        waveBulletsType[indexForDoubleWave] = MyMath.pickRandomElement(WAVE_BULLETS_TYPE_PROBABILITY);
        //if (!isDouble | (/*isDouble &*/ indexForDoubleWave == 1)) resetWaveTimer();
        //else waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;

        if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_GOOD) {
            if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
                currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY);
            else {
                currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY, SpecialBullet.STAR);
            }
            //Gdx.app.log(TAG, MyMath.arrayToString(GOOD_BULLETS_PROBABILITY));

            if (currentSpecialBullet == SpecialBullet.QUESTION_MARK) {

                if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
                    currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK);
                else
                    currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);

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
                    currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);
                } else Bullet.setStarExists(true);
            }

            if (currentSpecialBullet == SpecialBullet.MINUS) {

                Gdx.app.log(TAG, "Possible MINUS -> " + (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MIN_COUNT) + ", " +
                        Bullet.isPlusOrMinusExists() + ", " +
                        Bullet.isStarExists() + ", " +
                        !plusMinusBulletsTimer.isFinished() + ", " +
                        (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED));

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MIN_COUNT |
                        Bullet.isPlusOrMinusExists() |
                        Bullet.isStarExists() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED/* & indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_BAD;
                        currentSpecialBullet = SpecialBullet.PLUS;*/

                    //currentSpecialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY, SpecialBullet.MINUS);
                    currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY_NO_MINUS, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);
                }
            }
        } else if (waveBulletsType[indexForDoubleWave] == WaveBulletsType.SPECIAL_BAD) {

            if (gameplayScreen.getGameplayMode() != GameplayMode.SURVIVAL) {
                Random random = new Random();
                if (random.nextFloat() <= currentPlanetSpecialBulletsProbability) {
                    currentSpecialBullet = MyMath.pickRandomElement(currentPlanetSpecialBullets);
                    return currentSpecialBullet;
                }
            }

            currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY);

            if (currentSpecialBullet == SpecialBullet.QUESTION_MARK) {
                if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
                    currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK);
                else {
                    Random random = new Random();
                    if (random.nextFloat() <= currentPlanetSpecialBulletsProbability) {
                        currentSpecialBullet = MyMath.pickRandomElement(currentPlanetSpecialBullets);
                    } else
                        currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK);
                }

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
                        (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED));

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == SHIELDS_MAX_COUNT |
                        Bullet.isPlusOrMinusExists() |
                        Bullet.isStarExists() |
                        !plusMinusBulletsTimer.isFinished() |
                        (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED /*& indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/)) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_GOOD;
                        currentSpecialBullet = SpecialBullet.MINUS;*/
                    //currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY, SpecialBullet.PLUS);
                    currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY_NO_PLUS, SpecialBullet.QUESTION_MARK);
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



        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();
        Iterator<BulletsAndShieldContainer> it = busyContainers.iterator();
        while (it.hasNext()) {
            BulletsAndShieldContainer container = it.next();
            nonBusyContainers.add(container);
            it.remove();
        }




        if (roundTurn != null) {
            continueRoundWave();
            //resetWaveTimer();
            return;
        }

        WaveAttackType waveAttackType = MyMath.pickRandomElement(WAVE_TYPES_PROBABILITY);

        switch (waveAttackType) {
            case SINGLE:
                newSingleWave();
                //newDoubleWave();
                break;
            case DOUBLE:
                //newSingleWave();
                //Gdx.app.log(TAG, "<<<<<<<<<<< Can be double >>>>>>>>>>> " + Bullet.isPlusOrMinusExists() + ", " + plusMinusBulletsTimer.isFinished());

                if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED & Bullet.isPlusOrMinusExists() & plusMinusBulletsTimer.isFinished()) {
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
        Gdx.app.log(TAG, "NEW SINGLE WAVE");

        attachBullets(chooseContainer(), 0);
    }

    private void newDoubleWave() {
        isDouble = true;
        Gdx.app.log(TAG, "NEW DOUBLE WAVE");
        attachBullets(chooseContainer(), 0);
        attachBullets(chooseContainer(), 1);
        //resetIsThereDoubleWaveTimer();

        /*attachBullets(probability.removeIndex(MathUtils.random(activeShields-1)));
        attachBullets(probability.get(MathUtils.random(activeShields-2)));*/
    }

    private BulletsAndShieldContainer chooseContainer() {

        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();

        if (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED & angleDoubleRestricted != null) {
            chooseContainerOnTheOtherSide(nonBusyContainers);

            angleDoubleRestricted = null;

        } else {
            chooseRandomContainer(nonBusyContainers);

            angleDoubleRestricted = null;

            if (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED)
                //angleDoubleRestricted = MyMath.deg_0_to_360(current.getRotation());
                angleDoubleRestricted = MyMath.deg_0_to_360(busyContainers.peek().getRotation());
        }

        /*for (BulletsAndShieldContainer container : nonBusyContainers) {
            Gdx.app.log(TAG, "" + container.getRotation());
        }*/

        //Gdx.app.log(TAG, "" + angleDoubleRestricted);

        // previous = current;
        // return current/*nonBusyContainers.get(0)*/;
        return busyContainers.peek();
    }

    private void chooseContainerOnTheOtherSide(Array<BulletsAndShieldContainer> nonBusyContainers) {
        // If the current gameplay is restricted and the current wave is double, you must choose one container on the left (rotation > 180) and the other on the right (rotation < 180) (This is because of the restrictions of the controls). But if one container is chosen on the top (rotation = 0), the other container can be any other one.
        if (angleDoubleRestricted == 0)
            chooseRandomContainer(nonBusyContainers);
        else if (angleDoubleRestricted < 180) {

            for (int i = 0; i < nonBusyContainers.size; i++) {
                if (MyMath.deg_0_to_360(nonBusyContainers.get(i).getRotation()) > 180) {
                    busyContainers.add(nonBusyContainers.removeIndex(i));
                    // if (previous != null) nonBusyContainers.add(previous);
                    return;
                }
            }

        } else {

            for (int i = 0; i < nonBusyContainers.size; i++) {
                if (MyMath.deg_0_to_360(nonBusyContainers.get(i).getRotation()) < 180) {
                    busyContainers.add(nonBusyContainers.removeIndex(i));
                    // if (previous != null) nonBusyContainers.add(previous);
                    return;
                }
            }

        }
    }

    private void chooseRandomContainer(Array<BulletsAndShieldContainer> nonBusyContainers) {
        int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();

        /*if (previous == null) {
            int rand = MathUtils.random(activeShieldsNum - 1);
            busyContainers.add(nonBusyContainers.removeIndex(rand));
            //Gdx.app.log(TAG, "" + rand);

        } else {
            int rand = MathUtils.random(activeShieldsNum - 2);
            busyContainers.add(nonBusyContainers.removeIndex(rand));
            //nonBusyContainers.insert(activeShieldsNum - 2, previous);
            nonBusyContainers.add(previous);
            //Gdx.app.log(TAG, "" + rand);
        }*/

        int rand = MathUtils.random(activeShieldsNum - busyContainers.size - 1);
        busyContainers.add(nonBusyContainers.removeIndex(rand));
    }

    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------

    private void newRoundWave() {
        roundType = RoundType.values()[MathUtils.random(1)];
        roundStart = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1);
        roundTurn = roundStart;
        //current = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
        busyContainers.add(gameplayScreen.getBulletsAndShieldContainers()[roundTurn]);
        roundTurnPassedActiveShieldsMinusOne = false;
        //attachBullets(current, 0);
        attachBullets(busyContainers.peek(), 0);
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

    /*private void initializeCurrentBulletWave(final float duration) {
        currentBulletsWaveTimer = new Timer(duration) {

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "------------------------------------------- Finished -------------------------------------------");
                //if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave();
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentBulletsWaveTimer);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentBulletsWaveTimer);
    }*/

    private void initializePlusMinusBulletsTimer() {
        plusMinusBulletsTimer = new Timer(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION) {

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
            }
        };

        plusMinusBulletsTimer.start();

        gameplayScreen.addToFinishWhenLosing(plusMinusBulletsTimer);
    }

    private void initializeBulletsPerAttackNumberTween() {
        /*decreaseBulletsPerAttackTimer = new Timer(BULLETS_DECREASE_NUMBER_PER_ATTACK_EVERY * 1000) {

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

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(decreaseBulletsPerAttackTimer);*/



        bulletsPerAttackNumberDifficultyTween = new Tween(BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL * BULLETS_NUMBER_OF_DIFFICULTY_LEVELS * 1000,
                BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(BULLETS_MAX_NUMBER_PER_ATTACK, BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        bulletsPerAttackNumberDifficultyTween.start();

        gameplayScreen.addToFinishWhenLosing(bulletsPerAttackNumberDifficultyTween);
    }

    /*private void initializeCurrentDifficultLevelTimer() {
        currentDifficultyLevelTimer = new Timer(BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY * 1000f) {
            @Override
            public void onFinish() {
                //float nextSpeedMultiplier = getBulletSpeed() / BULLETS_SPEED_INITIAL;
                *//*float nextSpeedMultiplier = MyMath.roundTo(currentSpeedMultiplier + BULLETS_SPEED_MULTIPLIER_INCREMENT, 5);
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING &
                        nextSpeedMultiplier <= BULLETS_SPEED_MULTIPLIER_MAX) {
                    setCurrentSpeedMultiplier(nextSpeedMultiplier);
                    //currentSpeedMultiplier = nextSpeedMultiplier;
                    //currentSpeedMultiplier = MyMath.roundTo(currentSpeedMultiplier, 5);
                    //gameplayScreen.getSpeedMultiplierStuff().updateCharSequence(currentSpeedMultiplier);
                }*//*
                if (currentSpeedMultiplier < BULLETS_SPEED_MULTIPLIER_MAX) {
                    gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().start();
                    start();
                }
            }
        };

        currentDifficultyLevelTimer.start();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentDifficultyLevelTimer);
    }*/

    private void initializeCurrentSpeedMultiplierTimer() {

        bulletSpeedMultiplierDifficultyTween = new Tween(BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL * BULLETS_NUMBER_OF_DIFFICULTY_LEVELS * 1000,
                BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(BULLETS_SPEED_MULTIPLIER_INITIAL, BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        bulletSpeedMultiplierDifficultyTween.start();

        gameplayScreen.addToFinishWhenLosing(bulletSpeedMultiplierDifficultyTween);
    }

    private void initializeCurrentBulletSpeedTweenStarBullet_FirstStage() {
        currentBulletSpeedTweenStarBullet_FirstStage = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                if (gameplayScreen.getState() == GameplayScreen.State.LOST) return;

                currentBulletSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenLosing(currentBulletSpeedTweenStarBullet_FirstStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentBulletSpeedTweenStarBullet_FirstStage);
    }

    private void initializeCurrentBulletSpeedTweenStarBullet_ThirdStage() {
        currentBulletSpeedTweenStarBullet_ThirdStage = new MyTween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                if (gameplayScreen.getState() == GameplayScreen.State.LOST) return;

                currentBulletSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenLosing(currentBulletSpeedTweenStarBullet_ThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentBulletSpeedTweenStarBullet_ThirdStage);
    }

    private void initializeStarBulletFirstStage() {
        starBulletFirstStage = new Timer(STAR_BULLET_FIRST_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                startCurrentBulletSpeedTweenStarBullet_FirstStage();
                gameplayScreen.getStarsContainer().startCurrentSpeedTweenStarBullet();
                gameplayScreen.getScoreStuff().startScoreTweenStarBullet_FirstStage();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.LOST) {
                    starBulletSecondStage.start();

                }
            }
        };

        gameplayScreen.addToFinishWhenLosing(starBulletFirstStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(starBulletFirstStage);
    }

    private void initializeStarBulletSecondStage() {
        starBulletSecondStage = new Timer(STAR_BULLET_SECOND_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                gameplayScreen.getStarsContainer().setInWarpTrailAnimation(true);
                gameplayScreen.getStarsContainer().getWarpStretchFactorTweenStarBullet_SecondStage().start();

                gameplayScreen.startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(false, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.LOST) {
                    StarsContainer starsContainer = gameplayScreen.getStarsContainer();

                    //gameplayScreen.getStarsContainer().setInWarpTrailAnimation(false);
                    Array<Float> fpss = starsContainer.getFpss();
                    //Gdx.app.log(TAG, ""+MyMath.arrayToString(fpss.items));
                    //if (fpss.size > 0)
                    Gdx.app.log(TAG, MyMath.arrayStatistics(fpss.items, false));

                    fpss.clear();

                    gameplayScreen.getStarsContainer().setInWarpTrailAnimation(false);
                    starsContainer.setInWarpFastForwardAnimation(true);
                    starsContainer.getWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage().start();

                    starBulletThirdStage.start();
                }
            }
        };

        gameplayScreen.addToFinishWhenLosing(starBulletSecondStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(starBulletSecondStage);
    }

    private void initializeStarBulletThirdStage() {
        starBulletThirdStage = new Timer(STAR_BULLET_THIRD_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();
                gameplayScreen.startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(true, false);
                gameplayScreen.getScoreStuff().startScoreTweenStarBullet_ThirdStage();
                //gameplayScreen.getStarsContainer().setInWarpFastForwardAnimation(true);
                startCurrentBulletSpeedTweenStarBullet_ThirdStage();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                gameplayScreen.setInStarBulletAnimation(false);
                gameplayScreen.getStarsContainer().setInWarpFastForwardAnimation(false);

                if (gameplayScreen.getState() != GameplayScreen.State.LOST) {
                    //getCurrentBulletsWaveTimer().resume();
                    getBulletSpeedMultiplierDifficultyTween().resume();
                    getBulletsPerAttackNumberDifficultyTween().resume();
                    gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierTween().resume();
                    gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().resume();
                }
            }
        };

        gameplayScreen.addToFinishWhenLosing(starBulletThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(starBulletThirdStage);
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