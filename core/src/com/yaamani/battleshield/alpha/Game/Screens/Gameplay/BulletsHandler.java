package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsHandler implements Updatable {

    public static final String TAG = BulletsHandler.class.getSimpleName();


    private GameplayScreen gameplayScreen;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private boolean plusOrMinusExists = false;
    private boolean bulletCausingSlowMoExists = false;
    private boolean fasterDizzinessRotationExists = false;

    private int currentInUseBulletsCount = 0;

    private Bullet currentWaveLastBullet;

    //private Timer currentBulletsWaveTimer; // Just a timer.

    private Array<BulletsAndShieldContainer> busyContainers; // Containers with bullets attached during the current wave.
    private Array<Boolean> busyContainersIsFake; // Whether a corresponding container (the same index) has a fake wave or not.
    //private boolean dontAddBusyToNonBusyThisTime;
    private BulletsAndShieldContainer[] tempAvailableLeftContainers; // The containers to choose from for the currentWave (Left).
    private BulletsAndShieldContainer[] tempAvailableRightContainers; // The containers to choose from for the currentWave (Right).
    private BulletsAndShieldContainer[] tempAvailableRangeContainers;
    private boolean excludeTop = false/*, excludeTopRight = false, excludeBottomRight = false, excludeBottomLeft = false, excludeTopLeft = false, exclusionBasedOnPreviousWaveCalculated = false*/;
    private BulletsAndShieldContainer firstContainerChosen = null;
    private BulletsAndShieldContainer secondContainerChosen = null;
    private Array<Float> previousBusyContainersRotations;
    private Array<Boolean> previousBusyContainersIsFake;
    private int previousActiveShieldsNumber = SHIELDS_ACTIVE_DEFAULT;


    private Array<BulletsAndShieldContainer> dizzinessLeftContainers; // The containers that can be activated with the left controller (restricted) during the time the bullets of a particular wave hit this container.
    private Array<BulletsAndShieldContainer> dizzinessRightContainers; // The containers that can be activated with the right controller (restricted) during the time the bullets of a particular wave hit this container.
    //private Array<BulletsAndShieldContainer> dizzinessContainersThatChangeControllerDoubleWave;

    private int bulletsPerAttack = D_SURVIVAL_BULLETS_INITIAL_NO_PER_ATTACK;
    //private Timer decreaseBulletsPerAttackTimer;

    private Timer plusMinusBulletsEffectTimer;

    private float currentBulletSpeed;
    private float currentSpeedMultiplier;
    //private Timer currentDifficultyLevelTimer;


    private Tween d_survival_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_survival_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_crystal_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_crystal_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_crystal_fakeWaveProbabilityTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_dizziness_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_dizziness_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_lazer_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_lazer_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_portals_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_portals_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_t1_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_t1_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-

    private Tween d_bigBoss_bulletsPerAttackNumberTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-
    private Tween d_bigBoss_bulletSpeedMultiplierTween; // ->->->->->->->->->->-> Difficulty <-<-<-<-<-<-<-<-<-<-<-<-



    private float[] timestampsForArmorBulletsToPrepareForTheNextLazerAttack = new float[MathUtils.ceil(D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS)];


    private boolean forcedSpecialBulletAsap = false;
    private WaveBulletsType forcedWaveBulletsType = null;
    private SpecialBullet forcedSpecialBullet = null;
    private boolean forcedSpecialBulletQuestionMarkAllowed = false;


    private boolean thereIsAPortal;
    private int remainingTwoExitPortals = 0;


    //private float speedResetTime = 0;
    private MyTween currentBulletSpeedSlowMoTween; // Star bullet first stage + Rewind slow mo
    private MyTween currentBulletSpeedTweenStarBullet_ThirdStage;

    private Timer starBulletFirstStage; // Slow mo
    private Timer starBulletSecondStage; // Stars trails
    private Timer starBulletThirdStage; // Stars warp fast



    private int roundStart;
    private Integer roundTurn = null;
    private RoundType roundType;
    private boolean roundTurnPassedActiveShieldsMinusOne;

    private boolean isDouble;
    private WaveBulletsType[] waveBulletsType;
    private Float angleDoubleRestricted;
    private SpecialBullet[] currentSpecialBullets;
    private boolean questionMark = false;


    private SpecialBullet[] currentPlanetSpecialBullets;
    // private SpecialBulletType[] currentPlanetSpecialBulletsType;
    private float currentPlanetSpecialBulletsProbability;

    private float crystalPlanetFakeWaveProbability;

    /*private final SpecialBullet[] GOOD_BULLETS_PROBABILITY_NO_MINUS;
    private final SpecialBullet[] BAD_BULLETS_PROBABILITY_NO_PLUS;*/


    private boolean stopHandlingNewWave; // Mainly for desktop when it receives coordinates and stuff from android through the network (When we wanted to improve the controls).

    private NetworkAndStorageManager networkAndStorageManager;

    //private Timer isThereDoubleWaveTimer;

    public BulletsHandler(AdvancedStage game, GameplayScreen gameplayScreen) {
        //game.addUpdatable(BulletsHandler.this);

        this.gameplayScreen = gameplayScreen;

        initializeBulletPool();

        waveBulletsType = new WaveBulletsType[2];
        waveBulletsType[0] = waveBulletsType[1] = WaveBulletsType.ORDINARY;
        currentSpecialBullets = new SpecialBullet[2];
        currentSpecialBullets[0] = currentSpecialBullets[1] = null;

        crystalPlanetFakeWaveProbability = D_CRYSTAL_FAKE_WAVE_PROBABILITY_INITIAL;

        //initializeNoMinusNoPlusProbability();

        initializePlusMinusBulletsTimer();

        //initializeIsThereDoubleWaveTimer();

        setCurrentSpeedMultiplier(1);

        initializeDizzinessDoubleWaveArrays();


        initializeD_survival_bulletsPerAttackNumberTween();
        initializeD_survival_bulletSpeedMultiplierTween();

        initializeD_crystal_bulletsPerAttackNumberTween();
        initializeD_crystal_bulletSpeedMultiplierTween();
        initializeD_crystal_fakeWaveProbabilityTween();

        initializeD_dizziness_bulletsPerAttackNumberTween();
        initializeD_dizziness_bulletSpeedMultiplierTween();

        initializeD_lazer_bulletsPerAttackNumberTween();
        initializeD_lazer_bulletsSpeedMultiplierTween();

        initializeD_portals_bulletsPerAttackNumberTween();
        initializeD_portals_bulletsSpeedMultiplierTween();

        initializeD_t1_bulletsPerAttackNumberTween();
        initializeD_t1_bulletsSpeedMultiplierTween();

        initializeD_bigBoss_bulletsPerAttackNumberTween();
        initializeD_bigBoss_bulletsSpeedMultiplierTween();

        //initializeCurrentDifficultLevelTimer();

        initializeCurrentBulletSpeedSlowMoTween();
        initializeCurrentBulletSpeedTweenStarBullet_ThirdStage();

        initializeStarBulletFirstStage();
        initializeStarBulletSecondStage();
        initializeStarBulletThirdStage();

        //initializeRewindBulletFirstStage();

        //resetWaveTimer();

        //currentBulletSpeed = BULLETS_SPEED_INITIAL;
    }

    public int getCurrentInUseBulletsCount() {
        return currentInUseBulletsCount;
    }

    public void incrementCurrentInUseBulletsCount() {
        currentInUseBulletsCount++;
    }

    public void decrementCurrentInUseBulletsCount() {
        currentInUseBulletsCount--;
    }

    public boolean isFasterDizzinessRotationExists() {
        return fasterDizzinessRotationExists;
    }

    public void setFasterDizzinessRotationExists(boolean fasterDizzinessRotationExists) {
        this.fasterDizzinessRotationExists = fasterDizzinessRotationExists;
    }

    public boolean isBulletCausingSlowMoExists() {
        return bulletCausingSlowMoExists;
    }

    public void setBulletCausingSlowMoExists(boolean bulletCausingSlowMoExists) {
        this.bulletCausingSlowMoExists = bulletCausingSlowMoExists;
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
        //plusMinusBulletsTimer.update(delta);
        //decreaseBulletsPerAttackTimer.update(delta);



        switch (gameplayScreen.getGameplayMode()) {
            case SURVIVAL:
                d_survival_bulletsPerAttackNumberTween.update(delta);
                // if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
                //currentDifficultyLevelTimer.update(delta);
                d_survival_bulletSpeedMultiplierTween.update(delta);
                // }
                break;
            case CRYSTAL:
                d_crystal_bulletsPerAttackNumberTween.update(delta);
                d_crystal_bulletSpeedMultiplierTween.update(delta);
                d_crystal_fakeWaveProbabilityTween.update(delta);
                break;
            case DIZZINESS:
                d_dizziness_bulletsPerAttackNumberTween.update(delta);
                d_dizziness_bulletSpeedMultiplierTween.update(delta);
                break;
            case LAZER:
                d_lazer_bulletsPerAttackNumberTween.update(delta);
                d_lazer_bulletSpeedMultiplierTween.update(delta);
                break;
            case PORTALS:
                d_portals_bulletsPerAttackNumberTween.update(delta);
                d_portals_bulletSpeedMultiplierTween.update(delta);
                break;
            case T1:
                d_t1_bulletsPerAttackNumberTween.update(delta);
                d_t1_bulletSpeedMultiplierTween.update(delta);
                break;
            case BIG_BOSS:
                d_bigBoss_bulletsPerAttackNumberTween.update(delta);
                d_bigBoss_bulletSpeedMultiplierTween.update(delta);
                break;
        }

        currentBulletSpeedSlowMoTween.update(delta);
        currentBulletSpeedTweenStarBullet_ThirdStage.update(delta);
        starBulletFirstStage.update(delta);
        starBulletSecondStage.update(delta);
        starBulletThirdStage.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            gameplayScreen.setInRewindBulletAnimation(true);
            gameplayScreen.startSlowMoDeltaFractionTween(false, gameplayScreen.getRewindSlowMoFirstStageOnFinish());
        }


        if (!stopHandlingNewWave)
            handleNewWave();


        networkTransmissionAndStorageStuff();

        //isThereDoubleWaveTimer.update(delta);

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            newWave();*/

        //Gdx.app.log(TAG, "" + isPlusOrMinusExists());
        //Gdx.app.log(TAG, "" + plusMinusBulletsTimer.isFinished());
        //Gdx.app.log(TAG, "Free bullets in pool = " + bulletPool.getFree());
    }

    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------
    //--------------------------------------- Getters And Setters ---------------------------------------------

    public void setBulletsPerAttack(int bulletsPerAttack) {
        this.bulletsPerAttack = bulletsPerAttack;
        if (!currentBulletSpeedSlowMoTween.isStarted())
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

    public boolean isPlusOrMinusExists() {
        return plusOrMinusExists;
    }

    public void setPlusOrMinusExists(boolean plusOrMinusExists) {
        this.plusOrMinusExists = plusOrMinusExists;
    }

    /*public Timer getCurrentBulletsWaveTimer() {
        return currentBulletsWaveTimer;
    }*/

    /*public Timer getDecreaseBulletsPerAttackTimer() {
        return decreaseBulletsPerAttackTimer;
    }*/

    public Tween getD_survival_bulletsPerAttackNumberTween() {
        return d_survival_bulletsPerAttackNumberTween;
    }

    public Tween getD_survival_bulletSpeedMultiplierTween() {
        return d_survival_bulletSpeedMultiplierTween;
    }

    /*public void startSurvivalDifficultyTweens() {
        d_survival_bulletsPerAttackNumberTween.start();
        d_survival_bulletSpeedMultiplierTween.start();
    }*/

    public Tween getD_crystal_bulletsPerAttackNumberTween() {
        return d_crystal_bulletsPerAttackNumberTween;
    }

    public Tween getD_crystal_bulletSpeedMultiplierTween() {
        return d_crystal_bulletSpeedMultiplierTween;
    }

    public Tween getD_crystal_fakeWaveProbabilityTween() {
        return d_crystal_fakeWaveProbabilityTween;
    }

    /*public void startCrystalDifficultyTweens() {
        d_crystal_bulletsPerAttackNumberTween.start();
        d_crystal_bulletSpeedMultiplierTween.start();
        d_crystal_fakeWaveProbabilityTween.start();
    }*/

    public Tween getD_dizziness_bulletsPerAttackNumberTween() {
        return d_dizziness_bulletsPerAttackNumberTween;
    }

    public Tween getD_dizziness_bulletSpeedMultiplierTween() {
        return d_dizziness_bulletSpeedMultiplierTween;
    }

    public Tween getD_lazer_bulletsPerAttackNumberTween() {
        return d_lazer_bulletsPerAttackNumberTween;
    }

    public Tween getD_lazer_bulletSpeedMultiplierTween() {
        return d_lazer_bulletSpeedMultiplierTween;
    }

    public Tween getD_portals_bulletsPerAttackNumberTween() {
        return d_portals_bulletsPerAttackNumberTween;
    }

    public Tween getD_portals_bulletSpeedMultiplierTween() {
        return d_portals_bulletSpeedMultiplierTween;
    }

    public Tween getD_t1_bulletsPerAttackNumberTween() {
        return d_t1_bulletsPerAttackNumberTween;
    }

    public Tween getD_t1_bulletSpeedMultiplierTween() {
        return d_t1_bulletSpeedMultiplierTween;
    }

    public Tween getD_bigBoss_bulletsPerAttackNumberTween() {
        return d_bigBoss_bulletsPerAttackNumberTween;
    }

    public Tween getD_bigBoss_bulletSpeedMultiplierTween() {
        return d_bigBoss_bulletSpeedMultiplierTween;
    }

    /* public BulletsAndShieldContainer getPrevious() {
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
    }*/

    /*public void dontAddBusyToNonBusyThisTime() {
        dontAddBusyToNonBusyThisTime = true;
    }*/

    public void setRoundTurn(Integer roundTurn) {
        this.roundTurn = roundTurn;
    }

    public void startPlusMinusBulletsTween() {
        plusMinusBulletsEffectTimer.start();
    }

    public void updatePlusMinusBulletsTween(float delta) {
        plusMinusBulletsEffectTimer.update(delta);
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

        currentBulletSpeed = D_SURVIVAL_BULLETS_SPEED_INITIAL * newSpeedMultiplier;
    }

    /*public Timer getCurrentDifficultyLevelTimer() {
        return currentDifficultyLevelTimer;
    }*/

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
        //speedResetTime = gameplayScreen.getTimePlayedThisRoundSoFar();
        gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween().start();
        //currentDifficultyLevelTimer.start();
        d_survival_bulletSpeedMultiplierTween.start();
        resetCurrentSpeedMultiplier();
    }

    public void decrementCurrentSpeedMultiplier() {
        gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween().start();
        //currentDifficultyLevelTimer.start();
        d_survival_bulletSpeedMultiplierTween.start();

        if (getCurrentSpeedMultiplier() != 1)
            setCurrentSpeedMultiplier(getCurrentSpeedMultiplier() - D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INCREMENT);
    }

    /*public float getSpeedResetTime() {
        return speedResetTime;
    }*/

    public float getCurrentBulletSpeed() {
        /*int i = (int) *//*floor*//* ((gameplayScreen.getTimePlayedThisRoundSoFar() - speedResetTime) / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY);
        float currentMultiplier = 1 + i * BULLETS_SPEED_MULTIPLIER_INCREMENT;

        if (currentMultiplier <= BULLETS_SPEED_MULTIPLIER_MAX) {
            //Gdx.app.log(TAG, "Speed Multiplier = " + currentMultiplier);
            return BULLETS_SPEED_INITIAL * currentMultiplier;
        }

        //Gdx.app.log(TAG, "Speed Multiplier = " + BULLETS_SPEED_MULTIPLIER_MAX);
        return BULLETS_SPEED_INITIAL * BULLETS_SPEED_MULTIPLIER_MAX;*/
        return currentBulletSpeed;
    }

    public void startCurrentBulletSpeedSlowMoTween() {
        currentBulletSpeedSlowMoTween.setInitialVal(getCurrentBulletSpeed());
        currentBulletSpeedSlowMoTween.setFinalVal(0);
        currentBulletSpeedSlowMoTween.start();
    }

    public void startCurrentBulletSpeedTweenStarBullet_ThirdStage() {
        setCurrentSpeedMultiplier(getCurrentSpeedMultiplier());
        float bulletSpeedAfterStarBullet = getCurrentBulletSpeed();

        currentBulletSpeedTweenStarBullet_ThirdStage.setInitialVal(0);
        currentBulletSpeedTweenStarBullet_ThirdStage.setFinalVal(bulletSpeedAfterStarBullet);
        currentBulletSpeedTweenStarBullet_ThirdStage.start();
    }

    public void startStarBulletStages() {
        starBulletFirstStage.start();
        //starBulletSecondStage.start(STAR_BULLET_FIRST_STAGE_DURATION);
        //starBulletThirdStage.start(STAR_BULLET_FIRST_STAGE_DURATION + STAR_BULLET_SECOND_STAGE_DURATION);

        //getCurrentBulletsWaveTimer().pause();
        //getD_survival_bulletSpeedMultiplierTween().pause();
        //getD_survival_bulletsPerAttackNumberTween().pause();
        gameplayScreen.pauseModeTimers();
    }

    /*public Timer getRewindBulletFirstStage() {
        return rewindBulletFirstStage;
    }*/

    /*public void startRewindBulletStages() {
        Gdx.app.log(TAG, "<< startRewindBulletStages <<");
        rewindBulletFirstStage.start();
    }*/

    public Bullet getCurrentWaveLastBullet() {
        return currentWaveLastBullet;
    }

    public void nullifyCurrentWaveLastBullet() {
        currentWaveLastBullet = null;
    }

    public float[] getTimestampsForArmorBulletsToPrepareForTheNextLazerAttack() {
        return timestampsForArmorBulletsToPrepareForTheNextLazerAttack;
    }

    public SpecialBullet getForcedSpecialBullet() {
        return forcedSpecialBullet;
    }

    /*public void setPreviousActiveShieldsNumber(int previousActiveShieldsNumber) {
        this.previousActiveShieldsNumber = previousActiveShieldsNumber;
    }*/

    public void setStopHandlingNewWave(boolean stopHandlingNewWave) {
        this.stopHandlingNewWave = stopHandlingNewWave;
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
    }

    public boolean isThereAPortal() {
        return thereIsAPortal;
    }

    public void portalIsOn() {
        thereIsAPortal = true;
    }

    public void portalIsOver() {
        thereIsAPortal = false;
        gameplayScreen.getPortalPostProcessingEffect().clearPortalPoints();
    }

    public int getRemainingTwoExitPortals() {
        return remainingTwoExitPortals;
    }

    public void twoExitPortalsBulletEffect() {
        remainingTwoExitPortals = D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES;
    }

    public void cancelTwoExitPortalsBulletEffect() {
        remainingTwoExitPortals = 0;
    }

    public boolean willRewindBulletSlowMoInterfereWithLazerAttack() {
        boolean willRewindBulletSlowMoInterfereWithLazerAttack = false;

        float theTimeItTakesTheRewindBulletToHitTheTurret = (Bullet.getR() - TURRET_RADIUS) / getCurrentBulletSpeed() * 1000;

        Timer nextLazerAttackTimer = gameplayScreen.getLazerAttackStuff().getNextLazerAttackTimer();
        float remainingTimeBeforeLazerAttacks = nextLazerAttackTimer.getDurationMillis()*(1-nextLazerAttackTimer.getPercentage());
        if (remainingTimeBeforeLazerAttacks - theTimeItTakesTheRewindBulletToHitTheTurret < 0)
            willRewindBulletSlowMoInterfereWithLazerAttack = true;

        return willRewindBulletSlowMoInterfereWithLazerAttack;
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

    private void networkTransmissionAndStorageStuff() {
        if (networkAndStorageManager != null)
            if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                if (networkAndStorageManager.isConnectionEstablished() | networkAndStorageManager.isSaveControllerValuesModeEnabled())
                    networkAndStorageManager.prepareBulletsPerAttackForTransmissionAndStorageIfIamMobile((byte) bulletsPerAttack);
    }

    public void forceSpecialBullet(WaveBulletsType waveBulletsType, SpecialBullet specialBullet, boolean questionMarkAllowed) {
        forcedSpecialBulletAsap = true;
        forcedWaveBulletsType = waveBulletsType;
        forcedSpecialBullet = specialBullet;
        forcedSpecialBulletQuestionMarkAllowed = questionMarkAllowed;
    }

    private void consumeForcedSpecialBullet() {
        forcedSpecialBulletAsap = false;
        forcedWaveBulletsType = null;
        forcedSpecialBullet = null;
        forcedSpecialBulletQuestionMarkAllowed = false;
    }

    private void determineTypeThenAttach(BulletsAndShieldContainer parent, int typeIndexForDoubleWave, boolean isFake) {
        if (parent == null)
            return;

        waveBulletsType[typeIndexForDoubleWave] = WaveBulletsType.ORDINARY;
        //int specialBulletOrder = 0;

        if (forcedSpecialBulletAsap & !isFake) {
            waveBulletsType[typeIndexForDoubleWave] = forcedWaveBulletsType;
            currentSpecialBullets[typeIndexForDoubleWave] = forcedSpecialBullet;
            questionMark = false;
            if (forcedSpecialBulletQuestionMarkAllowed) {
                if (MathUtils.random() < FORCED_SPECIAL_BULLET_QUESTION_MARK_PROBABILITY)
                    questionMark = true;
            }
            consumeForcedSpecialBullet();
            //Gdx.app.log(TAG, "currentSpecialBullets = " + Arrays.toString(currentSpecialBullets));
        } else
            currentSpecialBullets[typeIndexForDoubleWave] = determineSpecialBullet(typeIndexForDoubleWave);

        //------------------------------------------------------

        attachBullets(parent, typeIndexForDoubleWave, isFake);
    }


    private void attachBullets(BulletsAndShieldContainer parent, int typeIndexForDoubleWave, boolean isFake) {
        float fakeTweenDelay = 0;
        if (isFake) {
            fakeTweenDelay = (Bullet.getR() / currentBulletSpeed*1000)/1.25f - D_CRYSTAL_FAKE_TWEEN_DURATION;
        }

        Bullet bullet = null;

        if (waveBulletsType[typeIndexForDoubleWave] == WaveBulletsType.ORDINARY) {
            Gdx.app.log(TAG, "Attaching ORDINARY to " + parent.toString());
            for (int i = 0; i < bulletsPerAttack; i++) {
                bullet = bulletPool.obtain();

                // Gdx.app.log(TAG, "obtained (NotSpecial) -> " + bullet.getI());

                bullet.notSpecial(isFake);
                bullet.attachNotSpecialToBulletsAndShieldContainer(parent, i);

                if (isFake)
                    bullet.getFakeTween().start(fakeTweenDelay);


            }
        } else {
            Gdx.app.log(TAG, "Attaching " + currentSpecialBullets[typeIndexForDoubleWave] + " to " + parent.toString());

            bullet = bulletPool.obtain();

            // Gdx.app.log(TAG, "obtained (Special)    -> " + bullet.getI());

            SpecialBullet currentSpecialBullet = currentSpecialBullets[typeIndexForDoubleWave];
            bullet.setSpecial(currentSpecialBullet, questionMark, isFake);
            bullet.attachSpecialToBulletsAndShieldContainer(parent/*, isDouble, typeIndexForDoubleWave*/);

            if (isFake) {
                switch (currentSpecialBullet) {
                    case PLUS:
                    case MINUS:
                        setPlusOrMinusExists(false); // It was set to true @ determineSpecialBullet(int).
                        break;
                    case FASTER_DIZZINESS_ROTATION:
                        setFasterDizzinessRotationExists(false); // It was set to true @ determineSpecialBullet(int).
                        break;
                    case STAR:
                    case REWIND:
                        setBulletCausingSlowMoExists(false); // It was set to true @ determineSpecialBullet(int).
                        break;
                }


                bullet.getFakeTween().start(fakeTweenDelay);
            }

        }


        if (bullet != null) {
            if (typeIndexForDoubleWave == 1 & currentWaveLastBullet != null) {
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


            if (!currentWaveLastBullet.isInUse() & gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                newWave(false, true);

            else {
                if (currentWaveLastBullet.getBulletType() == Bullet.BulletType.ORDINARY) {

                    if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_ORDINARY_HEIGHT + BULLETS_CLEARANCE_BETWEEN_WAVES))
                        newWave(false, true);

                } else {

                    if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_SPECIAL_DIAMETER / 2f + BULLETS_SPECIAL_WAVE_LENGTH / 2f + BULLETS_CLEARANCE_BETWEEN_WAVES))
                        newWave(false, true);

                }
            }

        } else {
            /*for (BulletsAndShieldContainer container:gameplayScreen.getBulletsAndShieldContainers()) {
                Gdx.app.log(TAG, "index = " + container.getIndex() + ", a = " + container.getColor().a);
            }*/
            newWave(false, true);
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
            //Gdx.app.log(TAG, MyMath.arrayToString(GOOD_BULLETS_PROBABILITY))

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

                Gdx.app.log(TAG, "Possible STAR (All must be false in order to make the bullet star) -> " +
                        isPlusOrMinusExists() + ", " +
                        isBulletCausingSlowMoExists() + ", " +
                        !plusMinusBulletsEffectTimer.isFinished());

                if (isPlusOrMinusExists() |
                        isBulletCausingSlowMoExists() |
                        !plusMinusBulletsEffectTimer.isFinished()) {
                    currentSpecialBullet = MyMath.pickRandomElement(GOOD_BULLETS_PROBABILITY, SpecialBullet.QUESTION_MARK, SpecialBullet.STAR);
                } else setBulletCausingSlowMoExists(true);
            }

            if (currentSpecialBullet == SpecialBullet.MINUS) {

                Gdx.app.log(TAG, "Possible MINUS (All must be false in order to make the bullet minus) -> " + (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == gameplayScreen.getCurrentShieldsMinCount()) + ", " +
                        isPlusOrMinusExists() + ", " +
                        isBulletCausingSlowMoExists() + ", " +
                        !plusMinusBulletsEffectTimer.isFinished() + ", " +
                        isDouble);

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == gameplayScreen.getCurrentShieldsMinCount() |
                        isPlusOrMinusExists() |
                        isBulletCausingSlowMoExists() |
                        !plusMinusBulletsEffectTimer.isFinished() |
                        isDouble/* & indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/) {

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

                    if (currentSpecialBullet == SpecialBullet.REWIND) {

                        boolean willRewindBulletSlowMoInterfereWithLazerAttack = willRewindBulletSlowMoInterfereWithLazerAttack();

                        Gdx.app.log(TAG, "Possible REWIND (All must be false in order to make the bullet rewind) -> " +
                                willRewindBulletSlowMoInterfereWithLazerAttack + ", " +
                                isPlusOrMinusExists() + ", " +
                                isBulletCausingSlowMoExists() + ", " +
                                !plusMinusBulletsEffectTimer.isFinished());

                        if (isPlusOrMinusExists() |
                                willRewindBulletSlowMoInterfereWithLazerAttack |
                                isBulletCausingSlowMoExists() |
                                !plusMinusBulletsEffectTimer.isFinished()) {

                            try {
                                currentSpecialBullet = MyMath.pickRandomElement(currentPlanetSpecialBullets, SpecialBullet.REWIND);
                            } catch (IllegalArgumentException e) {
                                currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY_NO_PLUS, SpecialBullet.QUESTION_MARK);
                            }

                        } else setBulletCausingSlowMoExists(true);
                    }

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

                Gdx.app.log(TAG, "Possible PLUS (All must be false in order to make the bullet plus) -> " + (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == gameplayScreen.getCurrentShieldsMaxCount()) + ", " +
                        isPlusOrMinusExists() + ", " +
                        isBulletCausingSlowMoExists() + ", " +
                        !plusMinusBulletsEffectTimer.isFinished() + ", " +
                        isDouble);

                if (gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() == gameplayScreen.getCurrentShieldsMaxCount() |
                        isPlusOrMinusExists() |
                        isBulletCausingSlowMoExists() |
                        !plusMinusBulletsEffectTimer.isFinished() |
                        isDouble /*& indexForDoubleWave == 0)|
                        !isThereDoubleWaveTimer.isFinished(*/) {

                    /*waveBulletsType[indexForDoubleWave] = WaveBulletsType.SPECIAL_GOOD;
                        currentSpecialBullet = SpecialBullet.MINUS;*/
                    //currentSpecialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY, SpecialBullet.PLUS);
                    currentSpecialBullet = MyMath.pickRandomElement(BAD_BULLETS_PROBABILITY_NO_PLUS, SpecialBullet.QUESTION_MARK);
                }
            }
        }

        if (currentSpecialBullet == SpecialBullet.PLUS | currentSpecialBullet == SpecialBullet.MINUS) {
            setPlusOrMinusExists(true);
        } else if (currentSpecialBullet == SpecialBullet.FASTER_DIZZINESS_ROTATION)
            setFasterDizzinessRotationExists(true);


        /*if (!questionMark)
            Gdx.app.log(TAG, "" + currentSpecialBullet);*/

        //return MathUtils.random(bulletsPerAttack - 1);
        return currentSpecialBullet;
    }

    private void busyToNonBusy() {
        /*if (dontAddBusyToNonBusyThisTime) {

            dontAddBusyToNonBusyThisTime = false;

            busyContainers.clear();
            busyContainersIsFake.clear();
            return;
        }*/

        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();
        Iterator<BulletsAndShieldContainer> it = busyContainers.iterator();
        // System.out.print("["+TAG+"] "+ "Busy : ");
        while (it.hasNext()) {
            BulletsAndShieldContainer container = it.next();
            // System.out.print(MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation()) + ", ");
            nonBusyContainers.add(container);
            it.remove();
        }
        // System.out.println();
        busyContainersIsFake.clear();
    }

    private void updatePreviousBusyContainersRotations() {
        previousBusyContainersRotations.clear();
        previousBusyContainersIsFake.clear();

        for (BulletsAndShieldContainer container : busyContainers) {
            previousBusyContainersRotations.add(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
        }

        for (Boolean isFake : busyContainersIsFake) {
            previousBusyContainersIsFake.add(isFake);
        }

        previousActiveShieldsNumber = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();
    }

    public void calculateTimestampsForArmorBulletsToPrepareForTheNextLazerAttack() {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        //int currentLength = D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS - lazerAttackStuff.getCurrentNumOfLazerAttacksThatTookPlace();
        int currentLength = lazerAttackStuff.calculateTotalNumOfArmorBulletsThatShouldBeSpawned();
        for (int i = 0; i < timestampsForArmorBulletsToPrepareForTheNextLazerAttack.length; i++) {
            if (i < currentLength)
                timestampsForArmorBulletsToPrepareForTheNextLazerAttack[i] = MathUtils.random() * LAZER_LAZER_TIMER_DURATION;
            else
                timestampsForArmorBulletsToPrepareForTheNextLazerAttack[i] = Float.MAX_VALUE;
        }
        Arrays.sort(timestampsForArmorBulletsToPrepareForTheNextLazerAttack);
        Gdx.app.log(TAG, "currentLength = " + currentLength + ", timestamps = " + Arrays.toString(timestampsForArmorBulletsToPrepareForTheNextLazerAttack));
    }

    public void newWave(boolean forceSingle, boolean ifSingleConsiderFake) {
        //if (!isVisible()) return;


        /*Gdx.app.log(TAG, "previousBusy = " + previousBusyContainersRotations.toString());
        Gdx.app.log(TAG, "previousBusyIsFake = " + previousBusyContainersIsFake.toString());
        Gdx.app.log(TAG, "previousActiveShieldsNum = " + previousActiveShieldsNumber);*/


        if (forceSingle) {

            newSingleWave(ifSingleConsiderFake);

        } else if (roundTurn != null) {

            continueRoundWave();

        } else {

            WaveAttackType waveAttackType = MyMath.pickRandomElement(WAVE_TYPES_PROBABILITY);

            switch (waveAttackType) {
                case SINGLE:
                    newSingleWave(ifSingleConsiderFake);
                    //newDoubleWave();
                    break;
                case DOUBLE:
                    //newSingleWave();
                    //Gdx.app.log(TAG, "<<<<<<<<<<< Can be double >>>>>>>>>>> " + Bullet.isPlusOrMinusExists() + ", " + plusMinusBulletsTimer.isFinished());

                    if (doubleWaveCondition()) {

                        newDoubleWave();

                    } else {
                        newSingleWave(ifSingleConsiderFake);
                    }

                        //newSingleWave(true);

                    break;
                case ROUND:
                    //newSingleWave();
                    //newDoubleWave();
                    newRoundWave();
                    break;
            }
        }

        //Gdx.app.log(TAG, "Busy = " + busyContainers.toString());
        //Gdx.app.log(TAG, "nonBusy = " + gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers().toString());

        clearWave();

    }

    public void clearWave() {
        updatePreviousBusyContainersRotations();

        isDouble = false;
        firstContainerChosen = null;
        secondContainerChosen = null;

        //busyToNonBusy();
        busyContainers.clear();
        dizzinessLeftContainers.clear();
        dizzinessRightContainers.clear();
        gameplayScreen.getShieldsAndContainersHandler().resetNonBusyContainers();

        falsifyExclusionVars();
    }

    private void falsifyExclusionVars() {
        excludeTop = false;
        /*excludeTopRight = false;
        excludeBottomRight = false;
        excludeBottomLeft = false;
        excludeTopLeft = false;
        exclusionBasedOnPreviousWaveCalculated = false;*/
    }

    private boolean doubleWaveCondition() {
        //return !(gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED & Bullet.isPlusOrMinusExists() & !plusMinusBulletsTimer.isFinished());
        if (gameplayScreen.getBulletsAndShieldContainers()[0].getRotationOmegaAlphaTween().isStarted())
            return false;
        return gameplayScreen.getGameplayControllerType() != GameplayControllerType.RESTRICTED |
                (!plusMinusBulletsEffectTimer.isStarted() & !isPlusOrMinusExists());
    }

    //--------------------------------------- Simple waves methods ---------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------
    //--------------------------------------- Simple waves methods ---------------------------------------

    private void newSingleWave(boolean considerFake) {

        if (gameplayScreen.getGameplayMode() == GameplayMode.DIZZINESS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS)
            dizzinessWave(WaveAttackType.SINGLE);
            //dizzinessSafeSingleWave();
        else
            ordinarySingleWave();



        if (gameplayScreen.getGameplayMode() == GameplayMode.PORTALS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS)
            if (portalCondition())
                transformToPortalWave(firstContainerChosen, 0);

        if (considerFake)
            if (gameplayScreen.getGameplayMode() == GameplayMode.CRYSTAL | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS)
                addFakeWaves(firstContainerChosen);

    }

    private void ordinarySingleWave() {
        Gdx.app.log(TAG, "----- NEW SINGLE WAVE -----");

        firstContainerChosen = chooseContainer(ContainerPositioning.RANDOM, false, null);
        determineTypeThenAttach(firstContainerChosen, 0, false);
    }

    private boolean portalCondition() {
        float p = MathUtils.random();

        Gdx.app.log(TAG, "Possible Portal (All must be true in order to spawn a portal wave) -> " +
                (p < D_PORTALS_PORTAL_PROBABILITY) + ", " +
                !thereIsAPortal + ", " + !isPlusOrMinusExists() + ", " +
                plusMinusBulletsEffectTimer.isFinished());

        return  p < D_PORTALS_PORTAL_PROBABILITY &
                !thereIsAPortal &
                !isPlusOrMinusExists() & plusMinusBulletsEffectTimer.isFinished();
    }

    private void transformToPortalWave(BulletsAndShieldContainer exitContainerAfterBulletsAreAttached, int typeIndexForDoubleWave) {
        if (exitContainerAfterBulletsAreAttached == null)
            return;

        BulletsAndShieldContainer entranceContainer = chooseContainer(ContainerPositioning.RANDOM, false, BulletPortalType.PORTAL_ENTRANCE);
        if (entranceContainer == null)
            return;

        Gdx.app.log(TAG, "-- (PORTAL) --");

        portalIsOn();

        attachBullets(entranceContainer, typeIndexForDoubleWave, false);
        transformToEntranceWave(entranceContainer);


        BulletsAndShieldContainer exitContainer = exitContainerAfterBulletsAreAttached;
        transformToExitWave(exitContainer);

        // Two exit portal bullet
        if (twoExitPortalCondition()) {

            BulletsAndShieldContainer secondExitContainer;
            if (gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS) {
                secondExitContainer = chooseSecondContainer(exitContainer, true);
            } else
                secondExitContainer = chooseSecondContainer(exitContainer, false);

            if (secondExitContainer != null) {
                attachBullets(secondExitContainer, typeIndexForDoubleWave, false);
                transformToExitWave(secondExitContainer);
                isDouble = true;
                remainingTwoExitPortals--;
                gameplayScreen.getTwoExitPortalUI().updateText(remainingTwoExitPortals);
                gameplayScreen.getTwoExitPortalUI().glow();
            }
        }
    }
    
    private boolean twoExitPortalCondition() {
        Gdx.app.log(TAG, "Possible Two Exit Portal wave (All must be true in order to make Two Exit Portal wave) -> " +
                !isDouble + ", " + doubleWaveCondition() + ", " + (remainingTwoExitPortals > 0));

        return !isDouble & doubleWaveCondition() & remainingTwoExitPortals > 0;
    }

    private void transformToEntranceWave(BulletsAndShieldContainer entranceContainerAfterBulletsAreAttached) {
        entranceContainerAfterBulletsAreAttached.showPortalEntrance();

        LinkedList<Bullet> attachedBullets = entranceContainerAfterBulletsAreAttached.getAttachedBullets();
        Bullet last = attachedBullets.getLast();
        last.setBulletPortalRole(BulletPortalRole.CLOSE_ENTRANCE_PORTAL);

        if (!last.isOrdinary())
            last.setBulletPortalType(BulletPortalType.PORTAL_ENTRANCE);
        else {
            int i = bulletsPerAttack;
            Iterator<Bullet> dit = attachedBullets.descendingIterator();
            while (dit.hasNext()) {

                Bullet bullet = dit.next();
                bullet.setBulletPortalType(BulletPortalType.PORTAL_ENTRANCE);

                i--;
                if (i <= 0)
                    break;
            }
        }

        /*gameplayScreen.startSlowMoDeltaFractionTween(false, gameplayScreen.getRewindSlowMoFirstStageOnFinish());
        setBulletCausingSlowMoExists(false);

        gameplayScreen.setInRewindBulletAnimation(true);*/
    }

    private void transformToExitWave(BulletsAndShieldContainer exitContainerAfterBulletsAreAttached) {
        LinkedList<Bullet> attachedBullets = exitContainerAfterBulletsAreAttached.getAttachedBullets();

        Bullet last = attachedBullets.getLast();
        if (!last.isOrdinary() | bulletsPerAttack == 1) {

            last.setBulletPortalType(BulletPortalType.PORTAL_EXIT);
            last.setColor(1, 1, 1, 0);
            last.setBulletPortalRole(BulletPortalRole.OPEN_AND_CLOSE_EXIT_PORTAL);

        } else {

            int i = bulletsPerAttack;
            Iterator<Bullet> dit = attachedBullets.descendingIterator();
            while (dit.hasNext()) {

                Bullet bullet = dit.next();
                bullet.setBulletPortalType(BulletPortalType.PORTAL_EXIT);
                bullet.setColor(1, 1, 1, 0);
                if (i == bulletsPerAttack) // last
                    bullet.setBulletPortalRole(BulletPortalRole.CLOSE_EXIT_PORTAL);
                else if (i == 1) // first
                    bullet.setBulletPortalRole(BulletPortalRole.OPEN_EXIT_PORTAL);

                i--;
                if (i <= 0)
                    break;
            }
        }

    }

    private void newDoubleWave() {

        if (gameplayScreen.getGameplayMode() == GameplayMode.DIZZINESS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS) {
            dizzinessWave(WaveAttackType.DOUBLE);
        } else
            ordinaryDoubleWave();


        if (gameplayScreen.getGameplayMode() == GameplayMode.PORTALS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS)
            if (portalCondition()) {

                BulletsAndShieldContainer chosenExitContainer;
                if (secondContainerChosen != null)
                    chosenExitContainer = MathUtils.random(1) == 0 ? firstContainerChosen : secondContainerChosen;
                else
                    chosenExitContainer = firstContainerChosen;

                Gdx.app.log(TAG, "chosenExitContainer = " + chosenExitContainer.toString());

                transformToPortalWave(chosenExitContainer, chosenExitContainer == firstContainerChosen ? 0 : 1);
            }

        if (gameplayScreen.getGameplayMode() == GameplayMode.CRYSTAL | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS) {
            addFakeWaves(firstContainerChosen);
            if (secondContainerChosen != null)
                addFakeWaves(secondContainerChosen);
        }
    }

    private void ordinaryDoubleWave() {
        isDouble = true;
        Gdx.app.log(TAG, "----- NEW DOUBLE WAVE -----");

        firstContainerChosen = chooseContainer(ContainerPositioning.RANDOM, false, null);
        secondContainerChosen = chooseSecondContainer(firstContainerChosen, false);

        determineTypeThenAttach(firstContainerChosen, 0, false);
        determineTypeThenAttach(secondContainerChosen, 1, false);

    }

    /**
     *
     * @param firstContainer
     * @param dizziness if {@code true}, please refer to {@link #chooseAContainerThatIsAlwaysControlledByOneController(ContainerPositioning)}
     * @return
     */
    private BulletsAndShieldContainer chooseSecondContainer(BulletsAndShieldContainer firstContainer, boolean dizziness) {
        BulletsAndShieldContainer secondContainer = null;
        if (gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED) {

            if (!dizziness) {

                float firstContainerAngleDeg = MyMath.deg_0_to_360(firstContainer.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
                if (firstContainerAngleDeg == 0) { // Top shield (dismiss double wave)
                    //secondContainerChosenDoubleWave = chooseContainer(ContainerPositioning.RANDOM, false);
                } else if (firstContainerAngleDeg > 180) { // Right
                    excludeTop = true;
                    secondContainer = chooseContainer(ContainerPositioning.LEFT, false, null);
                } else { // Left
                    excludeTop = true;
                    secondContainer = chooseContainer(ContainerPositioning.RIGHT, false, null);
                }

            } else {

                float rotationWhenTheWaveStartsHittingDizziness = firstContainer.getRotationWhenTheWaveStartsHittingDizziness();
                float rotationWhenTheWaveStopsHittingDizziness = firstContainer.getRotationWhenTheWaveStopsHittingDizziness();

                Gdx.app.log(TAG, "firstContainer = {" + firstContainer.toString() +
                        ", rotationWhenWaveStartsHitting = " + (rotationWhenTheWaveStartsHittingDizziness - 90) +
                        ", rotationWhenWaveStopsHitting = " + (rotationWhenTheWaveStopsHittingDizziness - 90) + "}");

                if (determineContainerPositioning(rotationWhenTheWaveStartsHittingDizziness) == ContainerPositioning.RIGHT) { // positioning of startHitting should equal positioning of stopsHitting
                    secondContainer = chooseAContainerThatIsAlwaysControlledByOneController(ContainerPositioning.LEFT);
                } else // Left
                    secondContainer = chooseAContainerThatIsAlwaysControlledByOneController(ContainerPositioning.RIGHT);
            }


        } else {
            secondContainer = chooseContainer(ContainerPositioning.RANDOM, false, null);
        }

        return secondContainer;
    }

    /*private boolean portalsDoubleWave() {
        isDouble = true;

        firstContainerChosen = addPortalWave();
        if (firstContainerChosen == null)
            return false;

        Gdx.app.log(TAG, "----- NEW DOUBLE WAVE (PORTAL) -----");

        secondContainerChosen = chooseSecondContainer(firstContainerChosen);
        determineTypeThenAttach(secondContainerChosen, 1, false);

        return true;
    }*/

    /*private BulletsAndShieldContainer firstOpposingContainersDizziness;
    private BulletsAndShieldContainer secondOpposingContainersDizziness;*/
    /*private BulletsAndShieldContainer firstContainersAlwaysControlledByOneControllerDizziness;
    private BulletsAndShieldContainer secondContainersAlwaysControlledByOneControllerDizziness;*/

    private void dizzinessWave(WaveAttackType singleOrDouble) {
        //ordinaryDoubleWave();

        if (isPlusOrMinusExists() | plusMinusBulletsEffectTimer.isStarted()) {

            Gdx.app.log(TAG, "Bullet.isPlusOrMinusExists() = " + isPlusOrMinusExists() +
                    ", plusMinusBulletsTimer.isStarted() = " + plusMinusBulletsEffectTimer.isStarted());

            // This is the only exception to "Wave must always be on one side" rule. We may change that in the future.
            // U can comment the next 2 lines (and keep the third commented) to skip a wave.
            firstContainerChosen = plusMinusExistsSpecial(gameplayScreen.getBulletsAndShieldContainers(), false, null);
            //registerBusy(firstContainerChosen, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
            determineTypeThenAttach(firstContainerChosen, 0, false);
            //dizzinessSafeSingleWave(null);
            return;
        }

        if (isFasterDizzinessRotationExists()) {
            Gdx.app.log(TAG, "- isFasterDizzinessRotationExists() == true -");
            dizzinessSafeSingleWave(null);
            return;
        }

        // If containers change positioning more than one, this won't work. Which means The rotational speed should be relatively slow.

        float afterHowManySecondsTheWaveWillStartHittingTheShield = (Bullet.getR()-SHIELDS_RADIUS) / getCurrentBulletSpeed();
        float afterHowManySecondsTheWaveWillStopHittingTheShield = ((Bullet.getR()-SHIELDS_RADIUS) + getBulletsPerAttack()*(BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_HEIGHT)) / getCurrentBulletSpeed();

        if (willDifficultyChangeDuringDoubleWave(afterHowManySecondsTheWaveWillStopHittingTheShield)) {
            Gdx.app.log(TAG, "- willDifficultyChangeDuringDoubleWave() == true -");
            dizzinessSafeSingleWave(null);
            return;
        }

        if (ifTheFasterDizzinessRotationBulletIsTakingPlace_willItsEffectStopBeforeThisDoubleWaveEnd(afterHowManySecondsTheWaveWillStopHittingTheShield)) {
            Gdx.app.log(TAG, "- ifTheFasterDizzinessRotationBulletIsTakingPlace_willItsEffectStopBeforeThisDoubleWaveEnd() == true -");
            dizzinessSafeSingleWave(null);
            return;
        }


        populateDizzinessLeftAndRightArrays(afterHowManySecondsTheWaveWillStartHittingTheShield, afterHowManySecondsTheWaveWillStopHittingTheShield);

        /*firstOpposingContainersDizziness = null;
        secondOpposingContainersDizziness = null;*/
        firstContainerChosen = null;
        secondContainerChosen = null;

        //dizzinessOpposingContainersDoubleWave();
        firstContainerChosen = chooseAContainerThatIsAlwaysControlledByOneController(/*singleOrDouble*/ContainerPositioning.RANDOM);
        if (singleOrDouble == WaveAttackType.DOUBLE) {
            Gdx.app.log(TAG, "-- Dizziness possible double --");
            secondContainerChosen = chooseSecondContainer(firstContainerChosen, true);
        }


        /*if (firstOpposingContainersDizziness != null & firstContainersAlwaysControlledByOneControllerDizziness != null) {
            int rand = MathUtils.random(1);
            if (rand == 0) {
                isDouble = true;
                attachBullets(firstOpposingContainersDizziness, 0, false);
                attachBullets(secondOpposingContainersDizziness, 1, false);
            } else {
                isDouble = true;
                attachBullets(firstContainersAlwaysControlledByOneControllerDizziness, 0, false);
                attachBullets(secondContainersAlwaysControlledByOneControllerDizziness, 1, false);
            }
        } else if (firstOpposingContainersDizziness != null) {
            isDouble = true;
            attachBullets(firstOpposingContainersDizziness, 0, false);
            attachBullets(secondOpposingContainersDizziness, 1, false);
        } else */if (firstContainerChosen != null) {
            if (singleOrDouble == WaveAttackType.SINGLE) {
                determineTypeThenAttach(firstContainerChosen, 0, false);
            } else {
                isDouble = true;
                determineTypeThenAttach(firstContainerChosen, 0, false);
                determineTypeThenAttach(secondContainerChosen, 1, false);
            }
        } else {
            dizzinessSafeSingleWave(null);
        }


        Gdx.app.log(TAG, "=================================================================================");
    }

    private boolean willDifficultyChangeDuringDoubleWave(float afterHowManySecondsTheWaveWillStopHittingTheShield) {
        float timePassedSinceTheLevelStarts = gameplayScreen.getScoreTimerStuff().getScoreTimer();
        float levelPercentageRightNow = timePassedSinceTheLevelStarts/(DIZZINESS_LEVEL_TIME*60);
        float levelPercentageWhenTheWaveStopsHitting = (timePassedSinceTheLevelStarts+afterHowManySecondsTheWaveWillStopHittingTheShield)/(DIZZINESS_LEVEL_TIME*60);

        float difficultyRightNow = D_DIZZINESS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION.apply(1, D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS, levelPercentageRightNow);
        float difficultyWhenTheWaveStopsHitting = D_DIZZINESS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION.apply(1, D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS, levelPercentageWhenTheWaveStopsHitting);

        return difficultyRightNow != difficultyWhenTheWaveStopsHitting;
    }

    private boolean ifTheFasterDizzinessRotationBulletIsTakingPlace_willItsEffectStopBeforeThisDoubleWaveEnd(float afterHowManySecondsTheWaveWillStopHittingTheShield) {
        Timer dizzinessFasterRotationalSpeedBulletEffectTimer = gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer();
        if (dizzinessFasterRotationalSpeedBulletEffectTimer.isStarted()) {
            float remainingBulletEffectTime =
                    (1-dizzinessFasterRotationalSpeedBulletEffectTimer.getPercentage()) * dizzinessFasterRotationalSpeedBulletEffectTimer.getDurationMillis() / 1000f;
            Gdx.app.log(TAG, "remainingBulletEffectTime = " + remainingBulletEffectTime);
            return remainingBulletEffectTime < afterHowManySecondsTheWaveWillStopHittingTheShield;
        }
        return false;
    }

    private void populateDizzinessLeftAndRightArrays(float afterHowManySecondsTheWaveWillStartHittingTheShield, float afterHowManySecondsTheWaveWillStopHittingTheShield) {

        //dizzinessContainersThatChangeControllerDoubleWave.clear();

        //Gdx.app.log(TAG, "HittingTime (Start, End) = (" + afterHowManySecondsTheWaveWillStartHittingTheShield + ", " + afterHowManySecondsTheWaveWillStopHittingTheShield + ").");

        int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();


        for (int i = 0; i < activeShieldsNum; i++) {

            // If containers change positioning more than one, this won't work. In other words works with relatively slow Rotational speed.

            BulletsAndShieldContainer container = gameplayScreen.getBulletsAndShieldContainers()[i];
            float currentRotation = container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation() + 90;
            float dizzinessRotationalSpeed = gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeed();
            float rotationWhenTheWaveStartsHitting = currentRotation + dizzinessRotationalSpeed*afterHowManySecondsTheWaveWillStartHittingTheShield;
            float rotationWhenTheWaveStopsHitting = currentRotation + dizzinessRotationalSpeed*afterHowManySecondsTheWaveWillStopHittingTheShield;

            container.setRotationWhenTheWaveStartsHittingDizziness(rotationWhenTheWaveStartsHitting);
            container.setRotationWhenTheWaveStopsHittingDizziness(rotationWhenTheWaveStopsHitting);

            ContainerPositioning containerPositioningWhenTheWaveStartsHitting = determineContainerPositioning(rotationWhenTheWaveStartsHitting);
            ContainerPositioning containerPositioningWhenTheWaveStopsHitting = determineContainerPositioning(rotationWhenTheWaveStopsHitting);

            //Gdx.app.log(TAG, i + " --- Rotation & Positioning (Starts, Stops) Hitting = (" + rotationWhenTheWaveStartsHitting + ", " + rotationWhenTheWaveStopsHitting + "), (" + containerPositioningWhenTheWaveStartsHitting + ", " + containerPositioningWhenTheWaveStopsHitting + ").");

            if (containerPositioningWhenTheWaveStartsHitting == ContainerPositioning.RIGHT &
                    containerPositioningWhenTheWaveStopsHitting == ContainerPositioning.RIGHT)
                dizzinessRightContainers.add(container);
            else if (containerPositioningWhenTheWaveStartsHitting == ContainerPositioning.LEFT &
                    containerPositioningWhenTheWaveStopsHitting == ContainerPositioning.LEFT)
                dizzinessLeftContainers.add(container);
            /*else
                dizzinessContainersThatChangeControllerDoubleWave.add(container);*/
        }

        Gdx.app.log(TAG, "dizzinessLeftContainers = " + dizzinessLeftContainers.toString(", "));
        Gdx.app.log(TAG, "dizzinessRightContainers = " + dizzinessRightContainers.toString(", "));
    }

    /**
     *
     * @param rotationDeg Make sure that you add the rotation of {@link GameplayScreen#getContainerOfContainers()} as well as 90 degrees.
     * @return
     */
    private ContainerPositioning determineContainerPositioning(float rotationDeg) {
        rotationDeg = MyMath.deg_0_to_360(rotationDeg);

        if (rotationDeg < 90)
            return ContainerPositioning.RIGHT;
        else if (rotationDeg > 270)
            return ContainerPositioning.RIGHT;
        else return ContainerPositioning.LEFT;
    }

    /*private void dizzinessOpposingContainersDoubleWave() {
        // If 2 containers are 180 degrees apart, they're of course guaranteed to be controlled by different controllers (restricted).

        int len = dizzinessContainersThatChangeControllerDoubleWave.size;
        if (len > 1) {

            for (int i = 0; i < len; i++) {
                for (int j = i+1; j < len; j++) {
                    BulletsAndShieldContainer firstContainer = dizzinessContainersThatChangeControllerDoubleWave.items[i];
                    BulletsAndShieldContainer secondContainer = dizzinessContainersThatChangeControllerDoubleWave.items[j];
                    float rotationFirst = MyMath.deg_0_to_360(firstContainer.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
                    float rotationSecond = MyMath.deg_0_to_360(secondContainer.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
                    if (Math.abs(rotationFirst - rotationSecond) == 180) {
                        firstOpposingContainersDizziness = firstContainer;
                        secondOpposingContainersDizziness = secondContainer;
                        Gdx.app.log(TAG, "------Possible Opposing------ (" + firstContainer.getIndex() + ", " + secondContainer.getIndex() + ").");
                    }
                }
            }

        }
    }*/

    /**
     * {@link #dizzinessLeftContainers} and {@link #dizzinessRightContainers} must be populated first.
     * @param positioning
     */
    private BulletsAndShieldContainer chooseAContainerThatIsAlwaysControlledByOneController(/*WaveAttackType singleOrDouble*/ContainerPositioning positioning) {
        if (dizzinessLeftContainers.size == 0 & dizzinessRightContainers.size == 0) {
            return null;
        } else {
            BulletsAndShieldContainer[] dizzinessLeftContainersItems = dizzinessLeftContainers.items;
            BulletsAndShieldContainer[] dizzinessRightContainersItems = dizzinessRightContainers.items;

            //if (singleOrDouble == WaveAttackType.SINGLE) {

            BulletsAndShieldContainer chosenContainer = null;
            switch (positioning) {
                case RANDOM:
                    if (MathUtils.random(1) == 0) {

                        if (dizzinessLeftContainers.size > 0)
                            chosenContainer = MyMath.pickRandomElement(dizzinessLeftContainersItems, 0, dizzinessLeftContainers.size - 1);
                        else
                            chosenContainer = MyMath.pickRandomElement(dizzinessRightContainersItems, 0, dizzinessRightContainers.size - 1);

                    } else {

                        if (dizzinessRightContainers.size > 0)
                            chosenContainer = MyMath.pickRandomElement(dizzinessRightContainersItems, 0, dizzinessRightContainers.size - 1);
                        else
                            chosenContainer = MyMath.pickRandomElement(dizzinessLeftContainersItems, 0, dizzinessLeftContainers.size - 1);

                    }
                    break;

                case LEFT:
                    if (dizzinessLeftContainers.size > 0)
                        chosenContainer = MyMath.pickRandomElement(dizzinessLeftContainersItems, 0, dizzinessLeftContainers.size - 1);
                    break;

                case RIGHT:
                    if (dizzinessRightContainers.size > 0)
                        chosenContainer = MyMath.pickRandomElement(dizzinessRightContainersItems, 0, dizzinessRightContainers.size - 1);
                    break;
            }

            return registerBusy(chosenContainer, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());

            /*} else {
                firstContainerChosen =
                        MyMath.pickRandomElement(dizzinessLeftContainersItems, 0, dizzinessLeftContainersDoubleWave.size - 1);
                secondContainerChosen =
                        MyMath.pickRandomElement(dizzinessRightContainersItems, 0, dizzinessRightContainersDoubleWave.size - 1);

                registerBusy(firstContainerChosen, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
                registerBusy(secondContainerChosen, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
            }*/

        }
    }

    /**
     * If you can't exactly determine which waves will always be controlled with one controller, this method should be used.
     * It'll choose the top right container (for right controller) and bottom left container (for left controller).
     * That's because the rotation is clock-wise.
     * @param portalType
     */
    private void dizzinessSafeSingleWave(BulletPortalType portalType) {

        Gdx.app.log(TAG, "----- Dizziness Safe Single Wave -----");
        
        if (gameplayScreen.getCurrentShieldsMinCount() < 4)
            Gdx.app.error(TAG, "dizzinessSafeSingleWave() won't work properly!!! NullPointerException is highly probable.");
        
        float fromAngle, toAngle;

        if (MathUtils.random(1) == 0 | isPlusOrMinusExists()) { // Left
            fromAngle = 90;
            toAngle = 180;
        } else { // Right
            fromAngle = 270;
            toAngle = 360;
        }


        //if (firstContainerChosen == null)
            firstContainerChosen = chooseContainer(fromAngle, toAngle, false, portalType);


        determineTypeThenAttach(firstContainerChosen, 0, false);
    }

    /**
     * It chooses the container that will rotate the least amount possible when the effect of a plus bullet or minus bullet takes place.
     * @param containersToChooseFrom
     * @param isFake
     * @param portalType
     * @return
     */
    private BulletsAndShieldContainer plusMinusExistsSpecial(BulletsAndShieldContainer[] containersToChooseFrom, boolean isFake, BulletPortalType portalType) {
        Gdx.app.log(TAG, "!! Plus minus special !!");

        BulletsAndShieldContainer chosenContainer = null;

        if (portalType != BulletPortalType.PORTAL_ENTRANCE) {

            float[] luckyAngles = {0, 0, 0, 0, 135f, 144f, 150f, 154.28572f, 157.5f};
            float[] luckyAngles2 = {0, 0, 0, 0, 225f, 216f, 210f, 205.71428f, 202.5f}; // Both luckyAngles and luckyAngles2 are valid.

            int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();

            float luckyAngle;
            if (MathUtils.random(1) == 0) luckyAngle = luckyAngles[activeShieldsNum];
            else luckyAngle = luckyAngles2[activeShieldsNum];

            BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
            BulletsAndShieldContainer toBeChosen = null;
            for (BulletsAndShieldContainer container : allContainers) {
                float rotation = MyMath.deg_0_to_360(container.getRotation()/* + gameplayScreen.getContainerOfContainers().getRotation()*/);
                if (rotation > luckyAngle - 1f & rotation < luckyAngle + 1f) {
                    toBeChosen = container;
                    break;
                }
            }

            for (BulletsAndShieldContainer container : containersToChooseFrom) {
                if (container == toBeChosen) {
                    chosenContainer = toBeChosen;
                    break;
                }
            }
        }

        return registerBusy(chosenContainer, isFake, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
    }

    /**
     *
     * @param positioning
     * @param isFake
     * @param portalType Pass {@code null} if it isn't a portal wave.
     * @return
     */
    private BulletsAndShieldContainer chooseContainer(ContainerPositioning positioning, boolean isFake, BulletPortalType portalType) {
        BulletsAndShieldContainer chosenContainer = null;

        //int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();
        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();

        boolean wasRandom = false;
        if (positioning == ContainerPositioning.RANDOM) {

            if (isPlusOrMinusExists() | plusMinusBulletsEffectTimer.isStarted())
                chosenContainer = plusMinusExistsSpecial(nonBusyContainers.items, isFake, portalType);

            if (chosenContainer == null) {
                wasRandom = true;

                if (MathUtils.random(1) == 0)
                    positioning = ContainerPositioning.RIGHT;
                else
                    positioning = ContainerPositioning.LEFT;
            } else
                return chosenContainer;

        }

        switch (positioning) {
            case RIGHT:

                chosenContainer = chooseContainerOnTheRight(nonBusyContainers, portalType);

                if (chosenContainer == null) {
                    if (wasRandom)
                        chosenContainer = chooseContainerOnTheLeft(nonBusyContainers, portalType);
                    else return null;
                }

                break;
            case LEFT:

                chosenContainer = chooseContainerOnTheLeft(nonBusyContainers, portalType);

                if (chosenContainer == null) {
                    if (wasRandom)
                        chosenContainer = chooseContainerOnTheRight(nonBusyContainers, portalType);
                    else return null;
                }

                break;
            /*default: // ContainerPositioning.RANDOM
                rand = MathUtils.random(activeShieldsNum - busyContainers.size - 1);
                chosenContainer = nonBusyContainers.removeIndex(rand);
                break;*/
        }


        
        return registerBusy(chosenContainer, isFake, nonBusyContainers);
    }

    private BulletsAndShieldContainer chooseContainer(float fromAngleDeg, float toAngleDeg, boolean isFake, BulletPortalType portalType) {
        BulletsAndShieldContainer chosenContainer = null;

        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();

        //chosenContainer = plusMinusExistsSpecial(nonBusyContainers, portalType);

        //if (chosenContainer == null) {
            int tempAvailableRangeContainersSize = populateTempAvailableRangeContainers(fromAngleDeg, toAngleDeg, nonBusyContainers, portalType);
            if (tempAvailableRangeContainersSize > 0)
                chosenContainer = tempAvailableRangeContainers[MathUtils.random(tempAvailableRangeContainersSize-1)];
        //}

        return registerBusy(chosenContainer, isFake, nonBusyContainers);
    }

    private BulletsAndShieldContainer registerBusy(BulletsAndShieldContainer chosenContainer, boolean isFake, Array<BulletsAndShieldContainer> nonBusyContainers) {

        if (chosenContainer == null)
            return null;

        for (BulletsAndShieldContainer c : busyContainers) {
            if (c == chosenContainer)
                throw new RuntimeException("chosenContainer already exists!! " + chosenContainer.toString());
        }

        nonBusyContainers.removeValue(chosenContainer, true);
        busyContainers.add(chosenContainer);
        busyContainersIsFake.add(isFake);


        if (determineContainerPositioning(chosenContainer.getRotationWhenTheWaveStartsHittingDizziness()) == ContainerPositioning.LEFT) {

            if (!dizzinessLeftContainers.isEmpty())
                dizzinessLeftContainers.removeValue(chosenContainer, true);

        } else {

            if (!dizzinessRightContainers.isEmpty())
                dizzinessRightContainers.removeValue(chosenContainer, true);
        }



        Gdx.app.log(TAG, "busyContainers = " + busyContainers.toString());

        return chosenContainer;
    }

    private BulletsAndShieldContainer chooseContainerOnTheLeft(Array<BulletsAndShieldContainer> nonBusyContainers, BulletPortalType portalType) {
        BulletsAndShieldContainer chosenContainer;

        // Gdx.app.log(TAG, "activeShieldsNum = " + activeShieldsNum);
        int tempAvailableLeftContainersSize = populateTempAvailableLeftContainers(nonBusyContainers, portalType);
        Gdx.app.log(TAG, "tempAvailableLeftContainersSize = " + tempAvailableLeftContainersSize);
        if (tempAvailableLeftContainersSize == 0) return null;
        int rand = MathUtils.random(tempAvailableLeftContainersSize - 1);
        chosenContainer = tempAvailableLeftContainers[rand];

        return chosenContainer;
    }

    private BulletsAndShieldContainer chooseContainerOnTheRight(Array<BulletsAndShieldContainer> nonBusyContainers, BulletPortalType portalType) {
        BulletsAndShieldContainer chosenContainer;

        // Gdx.app.log(TAG, "activeShieldsNum = " + activeShieldsNum);
        int tempAvailableRightContainersSize = populateTempAvailableRightContainers(nonBusyContainers, portalType);
        Gdx.app.log(TAG, "tempAvailableRightContainersSize = " + tempAvailableRightContainersSize);
        if (tempAvailableRightContainersSize == 0) return null;
        int rand = MathUtils.random(tempAvailableRightContainersSize - 1);
        chosenContainer = tempAvailableRightContainers[rand];

        return chosenContainer;
    }

    /**
     *
     * @param nonBusyContainers
     * @param portalType
     * @return The size of {{@link #tempAvailableRightContainers}} after populating it.
     */
    private int populateTempAvailableRightContainers(Array<BulletsAndShieldContainer> nonBusyContainers, BulletPortalType portalType) {
        int size = 0;
        // System.out.print('[' + TAG + "] " + "Non Busy : ");
        for (BulletsAndShieldContainer container : nonBusyContainers) {
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
            // System.out.print(angleDeg + ", ");
        }
        // System.out.println();
        // System.out.print("[" + TAG + "] " + "Temp Right : ");

        //calculateWhatContainersToExcludeBasedOnPreviousWave();

        //calculateWhatToContainersToExcludeToNotToIntroduceConfusionDuoToTopContainerWhenOddNumberOfShields();

        //float singleShieldRange = 360f/previousActiveShieldsNumber + 1;

        for (BulletsAndShieldContainer container : nonBusyContainers) {
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
            if (angleDeg > 180) {
                // System.out.print(angleDeg + ", ");

                /*if (excludeTopRight & (angleDeg >= 360 - singleShieldRange *//*This container is top right*//*))
                    continue;

                if (excludeBottomRight & (angleDeg > 180 & angleDeg < 180f + singleShieldRange *//*This container is bottom right*//*))
                    continue;*/

                if (portalType == BulletPortalType.PORTAL_ENTRANCE)
                    if (!container.getAttachedBullets().isEmpty())
                        if (container.getAttachedBullets().peekLast().getY() > D_PORTALS_ENTRANCE_EXIT_POSITION - PORTALS_ENTRANCE_EXIT_DIAMETER/2f)
                            continue;


                //if (container.)

                tempAvailableRightContainers[size++] = container;
            }
        }
        // System.out.println();
        return size;
    }

    /**
     *
     * @param nonBusyContainers
     * @param portalType
     * @return The size of {{@link #tempAvailableLeftContainers}} after populating it.
     */
    private int populateTempAvailableLeftContainers(Array<BulletsAndShieldContainer> nonBusyContainers, BulletPortalType portalType) {
        int size = 0;
        // System.out.print('[' + TAG + "] " + "Non Busy : ");
        for (BulletsAndShieldContainer container : nonBusyContainers) {
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
            // System.out.print(angleDeg + ", ");
        }
        // System.out.println();
        // System.out.print("[" + TAG + "] " + "Temp Left : ");

        //calculateWhatContainersToExcludeBasedOnPreviousWave();

        //calculateWhatToContainersToExcludeToNotToIntroduceConfusionDuoToTopContainerWhenOddNumberOfShields();

        //float singleShieldRange = 360f/previousActiveShieldsNumber + 1;

        for (BulletsAndShieldContainer container : nonBusyContainers) {
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
            if (angleDeg < 180) {
                // System.out.print(angleDeg + ", ");

                /*if (excludeTopLeft & (angleDeg > 0 & angleDeg <= singleShieldRange *//*This container is top left*//*))
                    continue;

                if (excludeBottomLeft & (angleDeg > 180f - singleShieldRange & angleDeg < 180f *//*This container is bottom left*//*))
                    continue;*/

                /*if (excludeTop)*/
                    if (excludeTop & (angleDeg <= 1 /*This container is top*/))
                        continue;

                if (portalType == BulletPortalType.PORTAL_ENTRANCE)
                    if (!container.getAttachedBullets().isEmpty())
                        if (container.getAttachedBullets().peekLast().getY() > D_PORTALS_ENTRANCE_EXIT_POSITION - PORTALS_ENTRANCE_EXIT_DIAMETER/2f)
                            continue;

                tempAvailableLeftContainers[size++] = container;
            }
        }
        // System.out.println();

        return size;
    }

    private int populateTempAvailableRangeContainers(float fromAngleDeg, float toAngleDeg, Array<BulletsAndShieldContainer> nonBusyContainers, BulletPortalType portalType) {
        int size = 0;

        for (BulletsAndShieldContainer container : nonBusyContainers) {
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
            if (angleDeg >= fromAngleDeg & angleDeg <= toAngleDeg) {

                if (portalType == BulletPortalType.PORTAL_ENTRANCE)
                    if (!container.getAttachedBullets().isEmpty())
                        if (container.getAttachedBullets().peekLast().getY() > D_PORTALS_ENTRANCE_EXIT_POSITION - BULLETS_SPECIAL_DIAMETER)
                            continue;

                tempAvailableRangeContainers[size++] = container;
            }
        }

        return size;
    }

    /*private void calculateWhatContainersToExcludeBasedOnPreviousWave() {
        if (exclusionBasedOnPreviousWaveCalculated)
            return;

        float singleShieldRange = 360f/previousActiveShieldsNumber + 1;

        for (int i = 0; i < previousBusyContainersRotations.size; i++) {
            if (previousBusyContainersIsFake.get(i))
                continue;


            float rotation = *//*MyMath.deg_0_to_360(previousBusyContainersRotations.get(i).getRotation() + gameplayScreen.getContainerOfContainers().getRotation())*//*previousBusyContainersRotations.get(i);
            if (rotation == 0f) { // This previous container was top.

                excludeTopLeft = true;

                excludeTopRight = true;

                Gdx.app.log(TAG, "Top left and top right should be excluded.");

            } else if (rotation < 0f + singleShieldRange) { // This previous container was top left.

                excludeTopRight = true;

                Gdx.app.log(TAG, "Top right should be excluded.");

            } else if (rotation > 180f - singleShieldRange & rotation < 180f) { // This previous container was bottom left.

                excludeBottomRight = true;

                Gdx.app.log(TAG, "Bottom right should be excluded.");

            } else if (rotation > 180f & rotation < 180f + singleShieldRange) { // This previous container was bottom right.

                excludeBottomLeft = true;

                Gdx.app.log(TAG, "Bottom left should be excluded.");

            } else if (rotation > 360 - singleShieldRange) { // This previous container was top right.

                excludeTopLeft = true;

                Gdx.app.log(TAG, "Top left should be excluded.");
            }
        }

        exclusionBasedOnPreviousWaveCalculated = true;
    }*/

    /*private void calculateWhatToContainersToExcludeToNotToIntroduceConfusionDuoToTopContainerWhenOddNumberOfShields() {
        for (int i = 0; i < busyContainers.size; i++) {
            if (busyContainersIsFake.get(i))
                continue;

            float singleShieldRange = 360f/previousActiveShieldsNumber + 1;

            BulletsAndShieldContainer container = busyContainers.get(i);
            float angleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());

            if (angleDeg <= 1) {

                excludeTopRight = true;
                excludeTopLeft = true;

                Gdx.app.log(TAG, "Top right and top left should be excluded (When DOUBLE).");

            } else if (previousActiveShieldsNumber % 2 == 1 &
                    (angleDeg <= singleShieldRange | angleDeg >= 360 - singleShieldRange)) {

                excludeTop = true;

                Gdx.app.log(TAG, "Top should be excluded (When DOUBLE).");
            }
        }
    }*/

    private void addFakeWaves(BulletsAndShieldContainer container) {
        if (container == null)
            return;

        if (MathUtils.random() > crystalPlanetFakeWaveProbability) return;


        ContainerPositioning positioning;
        float containerAngleDeg = MyMath.deg_0_to_360(container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
        if (containerAngleDeg == 0)
            positioning = ContainerPositioning.RANDOM;
        else if (containerAngleDeg > 180) // Right
            positioning = ContainerPositioning.RIGHT;
        else // Left
            positioning = ContainerPositioning.LEFT;

        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();
        int tempNonBusyContainersSize;
        switch (positioning) {
            case RIGHT:
                tempNonBusyContainersSize = populateTempAvailableRightContainers(nonBusyContainers, null);
                break;
            case LEFT:
                tempNonBusyContainersSize = populateTempAvailableLeftContainers(nonBusyContainers, null);
                break;
            default:
                tempNonBusyContainersSize = nonBusyContainers.size;
                break;
        }

        //Gdx.app.log(TAG, "tempNonBusyContainersSize = " + tempNonBusyContainersSize);

        int numOfFakeWaves;

        try {
            D_CRYSTAL_NUMBER_OF_FAKE_WAVES_PROBABILITY.setN(tempNonBusyContainersSize);
            numOfFakeWaves = (int) D_CRYSTAL_NUMBER_OF_FAKE_WAVES_PROBABILITY.apply(1, tempNonBusyContainersSize, MathUtils.random());
        } catch (ValueOutOfRangeException e) {
            /*if (tempNonBusyContainersSize == 0)
                numOfFakeWaves = 0;
            else // == 1*/
                numOfFakeWaves = 1;
        }

        Gdx.app.log(TAG, "tempNonBusyContainersSize = " + tempNonBusyContainersSize + ", numOfFakeWaves = " + numOfFakeWaves);

        if (tempNonBusyContainersSize >= 1)
            for (int i = 0; i < numOfFakeWaves; i++) {
                BulletsAndShieldContainer c = chooseContainer(positioning, true, null);
                if (c != null)
                    determineTypeThenAttach(c, 1, true); // The parameter indexForDoubleWave must be 1 to correctly calculate the position of the wave.
            }
    }

    /*private BulletsAndShieldContainer chooseContainer() {
        Array<BulletsAndShieldContainer> nonBusyContainers = gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers();

        if (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED & angleDoubleRestricted != null) {
            chooseContainerOnTheOtherSide(nonBusyContainers);

            angleDoubleRestricted = null;

        } else {
            chooseRandomContainer(nonBusyContainers);

            angleDoubleRestricted = null;

            if (isDouble & gameplayScreen.getGameplayControllerType() == GameplayControllerType.RESTRICTED)
                //angleDoubleRestricted = MyMath.deg_0_to_360(current.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
                angleDoubleRestricted = MyMath.deg_0_to_360(busyContainers.peek().getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
        }

        *//*for (BulletsAndShieldContainer container : nonBusyContainers) {
            Gdx.app.log(TAG, "" + container.getRotation() + gameplayScreen.getContainerOfContainers().getRotation());
        }*//*

        //Gdx.app.log(TAG, "" + angleDoubleRestricted);

        // previous = current;
        // return current/*nonBusyContainers.get(0)*/;/*
        return busyContainers.peek();
    }*/

    /*private void chooseContainerOnTheOtherSide(Array<BulletsAndShieldContainer> nonBusyContainers) {
        // If the current gameplay is restricted and the current wave is double, you must choose one container on the left (rotation > 180) and the other on the right (rotation < 180) (This is because of the restrictions of the controls). But if one container is chosen on the top (rotation = 0), the other container can be any other one.
        if (angleDoubleRestricted == 0)
            chooseRandomContainer(nonBusyContainers);
        else if (angleDoubleRestricted < 180) {

            for (int i = 0; i < nonBusyContainers.size; i++) {
                if (MyMath.deg_0_to_360(nonBusyContainers.get(i).getRotation() + gameplayScreen.getContainerOfContainers().getRotation()) > 180) {
                    busyContainers.add(nonBusyContainers.removeIndex(i));
                    // if (previous != null) nonBusyContainers.add(previous);
                    return;
                }
            }

        } else {

            for (int i = 0; i < nonBusyContainers.size; i++) {
                if (MyMath.deg_0_to_360(nonBusyContainers.get(i).getRotation() + gameplayScreen.getContainerOfContainers().getRotation()) < 180) {
                    busyContainers.add(nonBusyContainers.removeIndex(i));
                    // if (previous != null) nonBusyContainers.add(previous);
                    return;
                }
            }

        }
    }*/

    /*private void chooseRandomContainer(Array<BulletsAndShieldContainer> nonBusyContainers) {
        int activeShieldsNum = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum();

        *//*if (previous == null) {
            int rand = MathUtils.random(activeShieldsNum - 1);
            busyContainers.add(nonBusyContainers.removeIndex(rand));
            //Gdx.app.log(TAG, "" + rand);

        } else {
            int rand = MathUtils.random(activeShieldsNum - 2);
            busyContainers.add(nonBusyContainers.removeIndex(rand));
            //nonBusyContainers.insert(activeShieldsNum - 2, previous);
            nonBusyContainers.add(previous);
            //Gdx.app.log(TAG, "" + rand);
        }*//*

        int rand = MathUtils.random(activeShieldsNum - busyContainers.size - 1);
        busyContainers.add(nonBusyContainers.removeIndex(rand));
    }*/

    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------
    //--------------------------------------- Complex waves methods ---------------------------------------------

    private void newRoundWave() {
        roundType = RoundType.values()[MathUtils.random(1)];
        roundStart = MathUtils.random(gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1);
        roundTurn = roundStart;
        BulletsAndShieldContainer current = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
        registerBusy(current, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());

        roundTurnPassedActiveShieldsMinusOne = false;
        //attachBullets(current, 0);
        determineTypeThenAttach(current, 0, false);
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
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave(false, true);
            } else {
                BulletsAndShieldContainer c = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
                registerBusy(c, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
                determineTypeThenAttach(c, 0, false);
            }
        } else {
            roundTurn--;
            if (roundTurn < 0) {
                roundTurn = gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum() - 1;
                roundTurnPassedActiveShieldsMinusOne = true;
            }
            if (roundTurn <= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                roundTurn = null;
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) newWave(false, true);
            } else {
                BulletsAndShieldContainer c = gameplayScreen.getBulletsAndShieldContainers()[roundTurn];
                registerBusy(c, false, gameplayScreen.getShieldsAndContainersHandler().getNonBusyContainers());
                determineTypeThenAttach(c, 0, false);
            }
        }
        //Gdx.app.log(TAG, "CONTINUE ROUND, " + roundTurn);
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeBulletPool() {
        bulletPool = new Pool<Bullet>(BULLETS_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {

            private int instantiatedObjects = 0;

            @Override
            protected Bullet newObject() {
                Bullet newBullet = new Bullet(gameplayScreen, gameplayScreen.getStarsContainer()/*.getRadialTween()*/, gameplayScreen.getStage().getViewport(), instantiatedObjects);;
                instantiatedObjects++;
                return newBullet;
            }

            @Override
            public void free(Bullet object) {
                if (!object.isInUse())
                    Gdx.app.error(TAG, "Potentially freeing a bullet more than once. i = " + object.getI() + ".");
                super.free(object);
                //Gdx.app.log(TAG, "free -> " + object.getI());
                //Gdx.app.log(TAG, "Free bullets in pool = " + getFree());
            }

            @Override
            public Bullet obtain() {
                Bullet bullet = super.obtain();
                //activeBullets.add(bullet);
                //Gdx.app.log(TAG, "obtain -> " + bullet.getI());

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
        plusMinusBulletsEffectTimer = new Timer(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION) {

            @Override
            public void onFinish() {
                super.onFinish();
                //Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
                gameplayScreen.getShieldsAndContainersHandler().pushCurrentPlusMinusRecord();
            }
        };

        plusMinusBulletsEffectTimer.start();

        gameplayScreen.addToFinishWhenStoppingTheGameplay(plusMinusBulletsEffectTimer);
    }

    private void initializeD_survival_bulletsPerAttackNumberTween() {
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



        d_survival_bulletsPerAttackNumberTween = new Tween(D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL * D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS * 1000,
                D_SURVIVAL_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_SURVIVAL_BULLETS_MAX_NUMBER_PER_ATTACK, D_SURVIVAL_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        // d_survival_bulletsPerAttackNumberTween.start();

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_survival_bulletsPerAttackNumberTween);
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

    private void initializeD_survival_bulletSpeedMultiplierTween() {

        d_survival_bulletSpeedMultiplierTween = new Tween(D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL * D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS * 1000,
                D_SURVIVAL_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INITIAL, D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        // d_survival_bulletSpeedMultiplierTween.start();

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_survival_bulletSpeedMultiplierTween);
    }

    private void initializeD_crystal_bulletsPerAttackNumberTween() {

        d_crystal_bulletsPerAttackNumberTween = new Tween(CRYSTAL_LEVEL_TIME*60*1000, D_CRYSTAL_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_CRYSTAL_BULLETS_INITIAL_NO_PER_ATTACK, D_CRYSTAL_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
                // Gdx.app.log(TAG, "getBulletsPerAttack() = " + getBulletsPerAttack());
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_crystal_bulletsPerAttackNumberTween);
    }

    private void initializeD_crystal_bulletSpeedMultiplierTween() {

        d_crystal_bulletSpeedMultiplierTween = new Tween(CRYSTAL_LEVEL_TIME*60*1000, D_CRYSTAL_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_INITIAL, D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_crystal_bulletSpeedMultiplierTween);
    }

    private void initializeD_crystal_fakeWaveProbabilityTween() {

        d_crystal_fakeWaveProbabilityTween = new Tween(CRYSTAL_LEVEL_TIME*60*1000, D_CRYSTAL_FAKE_WAVE_PROBABILITY_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                crystalPlanetFakeWaveProbability = interpolation.apply(D_CRYSTAL_FAKE_WAVE_PROBABILITY_INITIAL, D_CRYSTAL_FAKE_WAVE_PROBABILITY_MAX, percentage);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_crystal_fakeWaveProbabilityTween);
    }

    private void initializeD_dizziness_bulletsPerAttackNumberTween() {

        d_dizziness_bulletsPerAttackNumberTween = new Tween(DIZZINESS_LEVEL_TIME*60*1000, D_DIZZINESS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_DIZZINESS_BULLETS_INITIAL_NO_PER_ATTACK, D_DIZZINESS_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_dizziness_bulletsPerAttackNumberTween);

    }

    private void initializeD_dizziness_bulletSpeedMultiplierTween() {

        d_dizziness_bulletSpeedMultiplierTween = new Tween(DIZZINESS_LEVEL_TIME*60*1000, D_DIZZINESS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_INITIAL, D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_dizziness_bulletSpeedMultiplierTween);
    }

    private void initializeD_lazer_bulletsPerAttackNumberTween() {

        d_lazer_bulletsPerAttackNumberTween = new Tween(LAZER_LEVEL_TIME*60*1000, D_LAZER_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_LAZER_BULLETS_INITIAL_NO_PER_ATTACK, D_LAZER_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_lazer_bulletsPerAttackNumberTween);

    }

    private void initializeD_lazer_bulletsSpeedMultiplierTween() {
        d_lazer_bulletSpeedMultiplierTween = new Tween(LAZER_LEVEL_TIME*60*1000, D_LAZER_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_LAZER_BULLETS_SPEED_MULTIPLIER_INITIAL, D_LAZER_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_lazer_bulletSpeedMultiplierTween);
    }

    private void initializeD_portals_bulletsPerAttackNumberTween() {
        d_portals_bulletsPerAttackNumberTween = new Tween(PORTALS_LEVEL_TIME*60*1000, D_PORTALS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_PORTALS_BULLETS_INITIAL_NO_PER_ATTACK, D_PORTALS_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_portals_bulletsPerAttackNumberTween);
    }

    private void initializeD_portals_bulletsSpeedMultiplierTween() {
        d_portals_bulletSpeedMultiplierTween = new Tween(PORTALS_LEVEL_TIME*60*1000, D_PORTALS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_PORTALS_BULLETS_SPEED_MULTIPLIER_INITIAL, D_PORTALS_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_portals_bulletSpeedMultiplierTween);
    }

    private void initializeD_t1_bulletsPerAttackNumberTween() {
        d_t1_bulletsPerAttackNumberTween = new Tween(T1_LEVEL_TIME*60*1000, D_T1_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_T1_BULLETS_INITIAL_NO_PER_ATTACK, D_T1_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_t1_bulletsPerAttackNumberTween);
    }

    private void initializeD_t1_bulletsSpeedMultiplierTween() {
        d_t1_bulletSpeedMultiplierTween = new Tween(T1_LEVEL_TIME*60*1000, D_T1_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_T1_BULLETS_SPEED_MULTIPLIER_INITIAL, D_T1_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_t1_bulletSpeedMultiplierTween);
    }

    private void initializeD_bigBoss_bulletsPerAttackNumberTween() {
        d_bigBoss_bulletsPerAttackNumberTween = new Tween(BIG_BOSS_LEVEL_TIME*60*1000, D_BIG_BOSS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setBulletsPerAttack((int) interpolation.apply(D_BIG_BOSS_BULLETS_INITIAL_NO_PER_ATTACK, D_BIG_BOSS_BULLETS_MIN_NUMBER_PER_ATTACK, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_bigBoss_bulletsPerAttackNumberTween);
    }

    private void initializeD_bigBoss_bulletsSpeedMultiplierTween() {
        d_bigBoss_bulletSpeedMultiplierTween = new Tween(BIG_BOSS_LEVEL_TIME*60*1000, D_BIG_BOSS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setCurrentSpeedMultiplier(interpolation.apply(D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_INITIAL, D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_MAX, percentage));
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(d_bigBoss_bulletSpeedMultiplierTween);
    }

    private void initializeCurrentBulletSpeedSlowMoTween() {
        currentBulletSpeedSlowMoTween = new MyTween(SLOW_MO_TWEENS_DURATION, SLOW_MO_TWEENS_INTERPOLATION) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                if (gameplayScreen.getState() == GameplayScreen.State.STOPPED) return;

                currentBulletSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(currentBulletSpeedSlowMoTween);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentBulletSpeedTweenStarBullet_FirstStage);
    }

    private void initializeCurrentBulletSpeedTweenStarBullet_ThirdStage() {
        currentBulletSpeedTweenStarBullet_ThirdStage = new MyTween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                if (gameplayScreen.getState() == GameplayScreen.State.STOPPED) return;

                currentBulletSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(currentBulletSpeedTweenStarBullet_ThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentBulletSpeedTweenStarBullet_ThirdStage);
    }

    private void initializeStarBulletFirstStage() {
        starBulletFirstStage = new Timer(SLOW_MO_TWEENS_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

                gameplayScreen.getStarsContainer().startCurrentStarSpeedSlowMoTween();
                startCurrentBulletSpeedSlowMoTween();
                gameplayScreen.getScoreTimerStuff().startScoreSlowMoTween();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
                    starBulletSecondStage.start();

                }
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(starBulletFirstStage);
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

                if (gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
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

        gameplayScreen.addToFinishWhenStoppingTheGameplay(starBulletSecondStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(starBulletSecondStage);
    }

    private void initializeStarBulletThirdStage() {
        starBulletThirdStage = new Timer(STAR_BULLET_THIRD_STAGE_DURATION) {
            @Override
            public void onStart() {
                super.onStart();
                gameplayScreen.startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(true, false);
                gameplayScreen.getScoreTimerStuff().startScoreTweenStarBullet_ThirdStage();
                //gameplayScreen.getStarsContainer().setInWarpFastForwardAnimation(true);
                startCurrentBulletSpeedTweenStarBullet_ThirdStage();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                gameplayScreen.setInStarBulletAnimation(false);
                gameplayScreen.getStarsContainer().setInWarpFastForwardAnimation(false);

                if (gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
                    //getCurrentBulletsWaveTimer().resume();
                    getD_survival_bulletSpeedMultiplierTween().resume();
                    getD_survival_bulletsPerAttackNumberTween().resume();

                    // Uncomment if you ever decided to make the star bullet available in crystal planet.
                    // getD_crystal_bulletsPerAttackNumberTween().pause();
                    // getD_crystal_bulletSpeedMultiplierTween().pause();
                    // getD_crystal_fakeWaveProbabilityTween().pause();

                    gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getSurvival_scoreMultiplierTween().resume();
                    gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween().resume();
                }
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(starBulletThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(starBulletThirdStage);
    }

    /*private void initializeRewindBulletFirstStage() {
        rewindBulletFirstStage = new Timer(SLOW_MO_TWEENS_DURATION) {
            @Override
            public void onStart() {
                super.onStart();

//                gameplayScreen.getStarsContainer().startCurrentStarSpeedSlowMoTween();
//                gameplayScreen.getStarsContainer().startRadialVelocitySlowMoTween();
//                startCurrentBulletSpeedSlowMoTween();
//                gameplayScreen.getShieldsAndContainersHandler().startDizzinessBaseRotationalSpeedSlowMoTween();
//                if (thereIsAPortal) {
//                    for (int i = 0; i < gameplayScreen.getShieldsAndContainersHandler().getActiveShieldsNum(); i++) {
//                        BulletsAndShieldContainer container = gameplayScreen.getBulletsAndShieldContainers()[i];
//                        if (container.isPortalEntranceExitFadeInOutTweenStarted())
//                            container.pausePortalEntranceExitFadeInOutTweenGradually(SLOW_MO_TWEENS_DURATION);
//                    }
//                }
                gameplayScreen.getSlowMoDeltaFractionTween().setReversed(false);
                gameplayScreen.getSlowMoDeltaFractionTween().start();

//                if (gameplayScreen.getScoreTimerStuff().getAffectTimerTween().isStarted())
//                    gameplayScreen.getScoreTimerStuff().getAffectTimerTween().finish();
//                gameplayScreen.getScoreTimerStuff().startScoreSlowMoTween();



            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
                    gameplayScreen.setRewinding(true);



                    gameplayScreen.getShieldsAndContainersHandler().getMirrorControlsTimer().pause();
                    if (gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer().isStarted()) {
                        gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer().pause();
                    }
                    //gameplayScreen.getLazerAttackStuff().getNextLazerAttackTimer().pause();

                    gameplayScreen.pauseModeTimers();




//                    float starSpeed = gameplayScreen.getStarsContainer().getCurrentStarSpeedSlowMoTween().getInitialVal();
//                    gameplayScreen.getStarsContainer().setCurrentStarSpeed(starSpeed);
//                    float baseRadialVelocity = gameplayScreen.getStarsContainer().getBaseRadialVelocity();
//                    gameplayScreen.getStarsContainer().setBaseRadialVelocity(baseRadialVelocity);
//
//                    currentBulletSpeed = currentBulletSpeedSlowMoTween.getInitialVal();
//
//                    gameplayScreen.getShieldsAndContainersHandler().revertDizzinessBaseRotationalSpeedAfterSlowMo();
                    gameplayScreen.getSlowMoDeltaFractionTween().setReversed(true);
                    gameplayScreen.getSlowMoDeltaFractionTween().start();
                }
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(rewindBulletFirstStage);
    }*/

    public void initializeBusyAndNonBusyContainers(int shieldsMaxCount) {
        if (busyContainers == null) {
            busyContainers = new Array<>(true, shieldsMaxCount, BulletsAndShieldContainer.class);
            busyContainersIsFake = new Array<>(true, shieldsMaxCount, Boolean.class);
        }

        if (previousBusyContainersRotations == null) {
            previousBusyContainersRotations = new Array<>(true, shieldsMaxCount, Float.class);
            previousBusyContainersIsFake = new Array<>(true, shieldsMaxCount, Boolean.class);
        }
    }

    public void initializeTempAvailableContainers(int shieldsMaxCount) {
        if (tempAvailableLeftContainers == null)
            tempAvailableLeftContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];
        else if (tempAvailableLeftContainers.length < shieldsMaxCount)
            tempAvailableLeftContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];

        if (tempAvailableRightContainers == null)
            tempAvailableRightContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];
        else if (tempAvailableRightContainers.length < shieldsMaxCount)
            tempAvailableRightContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];

        if (tempAvailableRangeContainers == null)
            tempAvailableRangeContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];
        else if (tempAvailableRangeContainers.length < shieldsMaxCount)
            tempAvailableRangeContainers = new BulletsAndShieldContainer[shieldsMaxCount /*/2*/];

    }

    private void initializeDizzinessDoubleWaveArrays() {
        /*dizzinessLeftContainersDoubleWave = new BulletsAndShieldContainer[MathUtils.ceil(DISEASES_SHIELDS_MAX_COUNT/2f)];
        dizzinessRightContainersDoubleWave = new BulletsAndShieldContainer[MathUtils.ceil(DISEASES_SHIELDS_MAX_COUNT/2f)];*/
        dizzinessLeftContainers = new Array<>(false, DIZZINESS_SHIELDS_MAX_COUNT, BulletsAndShieldContainer.class);
        dizzinessRightContainers = new Array<>(false, DIZZINESS_SHIELDS_MAX_COUNT, BulletsAndShieldContainer.class);
        //dizzinessContainersThatChangeControllerDoubleWave = new Array<>(false, DIZZINESS_SHIELDS_MAX_COUNT, BulletsAndShieldContainer.class);
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