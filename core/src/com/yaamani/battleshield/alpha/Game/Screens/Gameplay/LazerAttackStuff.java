package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LazerAttackStuff implements Updatable, Resizable {

    private static final String TAG = LazerAttackStuff.class.getSimpleName();

    private Timer nextLazerAttackTimer;
    private SimpleText nextLazerAttackTimerText;

    // Level handling
    private boolean lazerAttacking = false;
    private boolean waitingForAllRemainingBulletsToBeCleared;
    private int currentNumOfLazerAttacksThatTookPlace;
    private int currentNecessaryNumOfArmorBulletsForTheNextAttack;
    private int currentNumOfSpawnedArmorBulletsForTheNextAttack;
    private int currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;

    // How many armor bullets the player collected UI.
    private Image armorBlack;
    private Image armorGlowing;
    private SimpleText collectedArmorBulletsText;


    // The lazer attack itself.
    private LazerAttack lazerAttack;


    public enum LazerAttackHealthAffection {OMAR, YAMANI}
    private LazerAttackHealthAffection lazerAttackHealthAffection;

    //private Timer test;

    private GameplayScreen gameplayScreen;

    public LazerAttackStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        initializeNextLazerAttackTimer();

        currentNumOfLazerAttacksThatTookPlace = 0;
        calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();

        initializeHowManyArmorBulletsThePlayerCollected();

        initializeLazerAttack();

        /*test = new Timer(2000) {
            @Override
            public void onStart() {
                super.onStart();
                Gdx.app.log(TAG, "LAZER LAZER !!!!");
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                currentNumOfLazerAttacksThatTookPlace++;

                Gdx.app.log(TAG, "LAZER ENDS !!!!");
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));

                if (currentNumOfLazerAttacksThatTookPlace < LAZER_NUMBER_OF_LAZER_ATTACKS) {
                    nextLazerAttackTimerText.addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
                    nextLazerAttackTimer.start();
                } else {
                    armorBlack.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                    armorGlowing.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                    collectedArmorBulletsText.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                }

                currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = 0;
                updateCharacterSequenceForCollectedArmorBulletsText();
                armorGlowing.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));

                lazerAttacking = false;
                gameplayScreen.getBulletsHandler().newWave();
            }
        };

        gameplayScreen.addToFinishWhenLosing(test);*/
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        nextLazerAttackTimerText.setHeight(WORLD_SIZE/20f);
        nextLazerAttackTimerText.setPosition(worldWidth/2 - nextLazerAttackTimerText.getWidth()/2f, WORLD_SIZE*0.9f);

        armorBlack.setHeight(LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT);
        armorGlowing.setHeight(LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT);
        collectedArmorBulletsText.setHeight(LAZER_TEXT_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT);

        armorBlack.setWidth(LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_WIDTH);
        armorGlowing.setWidth(LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_WIDTH);

        armorBlack.setPosition(worldWidth/2 - armorBlack.getWidth()/2f, LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y);
        armorGlowing.setPosition(worldWidth/2 - armorGlowing.getWidth()/2f, LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_Y);
        setPositionForCollectedArmorBulletsText();

        lazerAttack.resize(width, height, worldWidth, worldHeight);
    }

    @Override
    public void update(float delta) {
        nextLazerAttackTimer.update(delta);
        waitingForAllRemainingBulletsToBeCleared();
        //test.update(delta);

        lazerAttack.update(delta);
    }


    private void setPositionForCollectedArmorBulletsText() {
        Viewport viewport = gameplayScreen.getStage().getViewport();
        float worldWidth = viewport.getWorldWidth();
        collectedArmorBulletsText.setPosition(worldWidth/2 - collectedArmorBulletsText.getWidth()/2f, LAZER_TEXT_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y);
    }

    private void updateCharacterSequenceForCollectedArmorBulletsText() {
        String charSequence = currentNumOfCollectedArmorBulletsByThePlayerForNextAttack + "/" + /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack();
        collectedArmorBulletsText.setCharSequence(charSequence, true);
        setPositionForCollectedArmorBulletsText();
    }

    /**
     * Milliseconds.
     * @return
     */
    private float calculateTheTimeLeftForTheLastBulletToDisappear() {
        BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
        float distanceFromTheCentreOfTheTurret = bulletsHandler.getCurrentWaveLastBullet().getY();
        return (distanceFromTheCentreOfTheTurret-TURRET_RADIUS) / bulletsHandler.getBulletSpeed() * 1000;
    }

    private void handleArmorBullets() {
        BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();

        float[] timestampsForArmorBulletsToPrepareForTheNextLazerAttack = bulletsHandler.getTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();

        int currentAttackMaxArmorBullets = calculateTotalNumOfArmorBulletsThatShouldBeSpawned();

        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < currentAttackMaxArmorBullets) {
            float timestampForTheCurrentBullet = timestampsForArmorBulletsToPrepareForTheNextLazerAttack[currentNumOfSpawnedArmorBulletsForTheNextAttack];
            float currentTime = nextLazerAttackTimer.getPercentage() * nextLazerAttackTimer.getDurationMillis();

            if (currentTime >= timestampForTheCurrentBullet & bulletsHandler.getForcedSpecialBullet() == null) {
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, 0);
                Gdx.app.log(TAG, "Forced armor registered at " + currentTime);
            }
        }
    }

    private void waitingForAllRemainingBulletsToBeCleared() {
        if (!waitingForAllRemainingBulletsToBeCleared) return;

        if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(true) /*& Bullet.getCurrentInUseBulletsCount() == 0*/) {

            if (Bullet.getCurrentInUseBulletsCount() == 0) {

                if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(false)) { // When all bullets get cleared, just make sure once more.
                    waitingForAllRemainingBulletsToBeCleared = false;
                    //test.start();
                    lazerAttack.start();
                }
            }
        }
    }

    private boolean makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(boolean waitForTheRightMomentToSpawn) {
        int totalNumOfArmorBulletsThatShouldHaveBeenSpawned = calculateTotalNumOfArmorBulletsThatShouldBeSpawned();
        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < totalNumOfArmorBulletsThatShouldHaveBeenSpawned) {

            BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
            if (bulletsHandler.getForcedSpecialBullet() == null)
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, 0);

            if (waitForTheRightMomentToSpawn) {
                if (theRightMomentToSpawn()) {
                    Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                    bulletsHandler.busyToNonBusy();
                    bulletsHandler.newSingleWave(false);
                }
            } else {
                Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                bulletsHandler.busyToNonBusy();
                bulletsHandler.newSingleWave(false);
            }

            return false;
        }
        return true;
    }

    public int calculateTotalNumOfArmorBulletsThatShouldBeSpawned() {
        //return MathUtils.ceil(D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS - currentNumOfLazerAttacksThatTookPlace/2f);
        return /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack();
    }

    public void calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack() {
        currentNecessaryNumOfArmorBulletsForTheNextAttack = LAZER_MIN_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR + currentNumOfLazerAttacksThatTookPlace*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR_INCREMRNT;
    }

    public boolean theRightMomentToSpawn() {

        BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
        Bullet currentWaveLastBullet = bulletsHandler.getCurrentWaveLastBullet();

        if (currentWaveLastBullet.getBulletType() == null)
            return true;


        if (currentWaveLastBullet.getBulletType() == Bullet.BulletType.ORDINARY) {

            if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_ORDINARY_HEIGHT + BULLETS_CLEARANCE_BETWEEN_WAVES)) {
                return true;
            }

        } else {

            if (currentWaveLastBullet.getY() < Bullet.getR() - (BULLETS_SPECIAL_DIAMETER / 2f + BULLETS_SPECIAL_WAVE_LENGTH / 2f + BULLETS_CLEARANCE_BETWEEN_WAVES)) {
                return true;
            }
        }

        return false;
    }

    public void incrementCurrentNumOfSpawnedArmorBulletsForTheNextAttack() {
        currentNumOfSpawnedArmorBulletsForTheNextAttack++;
    }

    public void decrementCurrentNumOfSpawnedArmorBulletsForTheNextAttack() {
        currentNumOfSpawnedArmorBulletsForTheNextAttack--;
    }

    public void incrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() {
        if (currentNumOfCollectedArmorBulletsByThePlayerForNextAttack == /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack())
            return;

        currentNumOfCollectedArmorBulletsByThePlayerForNextAttack++;
        updateCharacterSequenceForCollectedArmorBulletsText();

        if (currentNumOfCollectedArmorBulletsByThePlayerForNextAttack == /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()
                & armorGlowing.getColor().a == 0) {

            armorGlowing.addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
        }
    }

    public void show() {
        nextLazerAttackTimerText.setVisible(true);
        armorBlack.setVisible(true);
        armorGlowing.setVisible(true);
        collectedArmorBulletsText.setVisible(true);

        armorBlack.setColor(1, 1, 1, 1);
        collectedArmorBulletsText.setColor(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1);
        nextLazerAttackTimerText.addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
    }

    public void hide() {
        nextLazerAttackTimerText.setVisible(false);
        armorBlack.setVisible(false);
        armorGlowing.setVisible(false);
        collectedArmorBulletsText.setVisible(false);

        lazerAttack.hide();
    }

    public void resetLazerStuff() {
        lazerAttacking = false;
        waitingForAllRemainingBulletsToBeCleared = false;
        currentNumOfLazerAttacksThatTookPlace = 0;
        calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();
        currentNumOfSpawnedArmorBulletsForTheNextAttack = 0;
        currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = 0;
        updateCharacterSequenceForCollectedArmorBulletsText();
        hide();
        armorGlowing.setColor(1, 1, 1,0);
    }

    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------

    public Timer getNextLazerAttackTimer() {
        return nextLazerAttackTimer;
    }

    public boolean isLazerAttacking() {
        return lazerAttacking;
    }

    public int getCurrentNumOfLazerAttacksThatTookPlace() {
        return currentNumOfLazerAttacksThatTookPlace;
    }

    public int getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() {
        return currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;
    }

    public LazerAttackHealthAffection getLazerAttackHealthAffection() {
        return lazerAttackHealthAffection;
    }

    public void setLazerAttackHealthAffection(LazerAttackHealthAffection lazerAttackHealthAffection) {
        this.lazerAttackHealthAffection = lazerAttackHealthAffection;
    }

    public int getCurrentNecessaryNumOfArmorBulletsForTheNextAttack() {
        return currentNecessaryNumOfArmorBulletsForTheNextAttack;
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeNextLazerAttackTimer() {
        String text = "Next Attack " + MyMath.toMinutesDigitalTimeFormat(LAZER_LAZER_TIMER_DURATION/1000/60);
        nextLazerAttackTimerText = new SimpleText(gameplayScreen.getMyBitmapFont(), text);
        gameplayScreen.addActor(nextLazerAttackTimerText);


        nextLazerAttackTimer = new Timer(LAZER_LAZER_TIMER_DURATION) {
            @Override
            public boolean onUpdate(float delta) {
                String text = "Next Attack " + MyMath.toMinutesDigitalTimeFormat((1-getPercentage())*getDurationMillis()/1000f/60f);
                nextLazerAttackTimerText.setCharSequence(text, true);
                Viewport viewport = gameplayScreen.getStage().getViewport();
                nextLazerAttackTimerText.setX(viewport.getWorldWidth()/2 - nextLazerAttackTimerText.getWidth()/2f);

                /*if (gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet() != null)
                    Gdx.app.log(TAG, "" + gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet().getY());*/

                handleArmorBullets();

                return super.onUpdate(delta);
            }

            @Override
            public void onStart() {
                super.onStart();
                gameplayScreen.getBulletsHandler().calculateTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();
                currentNumOfSpawnedArmorBulletsForTheNextAttack = 0;
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                lazerAttacking = true;
                waitingForAllRemainingBulletsToBeCleared = true;

                nextLazerAttackTimerText.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));

                /*for (BulletsAndShieldContainer c : gameplayScreen.getBulletsAndShieldContainers()) {
                    if (c.getColor().a == 1)
                    c.addAction(Actions);
                }*/

                /*float timeLeftForTheLastBulletToDisappear = calculateTheTimeLeftForTheLastBulletToDisappear();
                test.start(timeLeftForTheLastBulletToDisappear);*/

            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(nextLazerAttackTimer);
    }

    private void initializeHowManyArmorBulletsThePlayerCollected() {
        armorBlack = new Image(Assets.instance.gameplayAssets.armorBlack);
        armorGlowing = new Image(Assets.instance.gameplayAssets.armorGlowing);
        collectedArmorBulletsText = new SimpleText(gameplayScreen.getMyBitmapFont(), "");
        updateCharacterSequenceForCollectedArmorBulletsText();

        gameplayScreen.addActor(armorBlack);
        gameplayScreen.addActor(armorGlowing);
        gameplayScreen.addActor(collectedArmorBulletsText);

        armorGlowing.setColor(1, 1, 1, 0);
        collectedArmorBulletsText.setColor(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1);
    }

    private void initializeLazerAttack() {
        lazerAttack = new LazerAttack(gameplayScreen) {
            @Override
            public void onStart() {
                super.onStart();
                Gdx.app.log(TAG, "LAZER LAZER !!!!");
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
            }

            @Override
            public void onFinish() {
                super.onFinish();

                currentNumOfLazerAttacksThatTookPlace++;

                Gdx.app.log(TAG, "LAZER ENDS !!!!");

                calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();

                gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));

                if (currentNumOfLazerAttacksThatTookPlace < LAZER_NUMBER_OF_LAZER_ATTACKS) {
                    nextLazerAttackTimerText.addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
                    nextLazerAttackTimer.start();
                } else {
                    armorBlack.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                    armorGlowing.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                    collectedArmorBulletsText.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                }

                currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = 0;
                updateCharacterSequenceForCollectedArmorBulletsText();
                armorGlowing.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));

                lazerAttacking = false;
                gameplayScreen.getBulletsHandler().newWave();
            }
        };
    }

}
