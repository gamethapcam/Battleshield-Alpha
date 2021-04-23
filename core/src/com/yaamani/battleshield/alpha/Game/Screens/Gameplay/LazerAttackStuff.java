package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.LazerAttackRecord;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.NextLazerAttackTimerRecord;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.RewindEngine;
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
        nextLazerAttackTimerText.setPosition(worldWidth/2 - nextLazerAttackTimerText.getWidth()/2f, WORLD_SIZE*0.04f);

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

        if (waitingForAllRemainingBulletsToBeCleared) {

            if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned() /*& gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount() == 0*/) {

                if (gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount() == 0) {

                    if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned()) { // When all bullets get cleared, just make sure once more.
                        //test.start();
                        lazerAttack.start();
                    }
                }
            }
        }
        //test.update(delta);

        lazerAttack.update(delta);
    }


    private void setPositionForCollectedArmorBulletsText() {
        Viewport viewport = gameplayScreen.getStage().getViewport();
        float worldWidth = viewport.getWorldWidth();
        collectedArmorBulletsText.setPosition(worldWidth/2 - collectedArmorBulletsText.getWidth()/2f, LAZER_TEXT_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y);
    }

    public void updateCharacterSequenceForCollectedArmorBulletsText() {
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
        return (distanceFromTheCentreOfTheTurret-TURRET_RADIUS) / bulletsHandler.getCurrentBulletSpeed() * 1000;
    }

    private void handleArmorBullets() {
        BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();

        float[] timestampsForArmorBulletsToPrepareForTheNextLazerAttack = bulletsHandler.getTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();

        int currentAttackMaxArmorBullets = calculateTotalNumOfArmorBulletsThatShouldBeSpawned();

        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < currentAttackMaxArmorBullets) {
            if (currentNumOfSpawnedArmorBulletsForTheNextAttack < 0)
                currentNumOfSpawnedArmorBulletsForTheNextAttack = 0;
            float timestampForTheCurrentBullet = timestampsForArmorBulletsToPrepareForTheNextLazerAttack[currentNumOfSpawnedArmorBulletsForTheNextAttack];
            float currentTime = nextLazerAttackTimer.getPercentage() * nextLazerAttackTimer.getDurationMillis();

            if (currentTime >= timestampForTheCurrentBullet & bulletsHandler.getForcedSpecialBullet() == null) {
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, false);
                Gdx.app.log(TAG, "Forced armor registered at " + currentTime);
            }
        }
    }

    private boolean makeSureThatTheCorrectNumOfArmorBulletsAreSpawned() {
        int totalNumOfArmorBulletsThatShouldHaveBeenSpawned = calculateTotalNumOfArmorBulletsThatShouldBeSpawned();
        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < totalNumOfArmorBulletsThatShouldHaveBeenSpawned) {

            Gdx.app.log(TAG, "makeSureThatTheCorrectNumOfArmorBulletsAreSpawned()");
            
            BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
            if (bulletsHandler.getForcedSpecialBullet() == null)
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, false);

            /*if (waitForTheRightMomentToSpawn) {
                if (theRightMomentToSpawn()) {
                    Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                    bulletsHandler.newWave(true, false);
                }
            } else {
                Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                bulletsHandler.newWave(true, false);
            }*/

            if (bulletsHandler.handleNewWave(true, false))
                Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");


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

    /*public boolean theRightMomentToSpawn() {

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
    }*/

    public void incrementCurrentNumOfSpawnedArmorBulletsForTheNextAttack(int containerI) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        Gdx.app.log(TAG, ")))))))))<<<<<<-----currentNumOfSpawnedArmorBulletsForTheNextAttack++ (" + containerI + "), <- " + stack[2].getMethodName() + "() <- " + stack[3].getMethodName() + "() <- " + stack[4].getMethodName() + "() <- " + stack[5].getMethodName() + "().");
        currentNumOfSpawnedArmorBulletsForTheNextAttack++;
    }

    public void decrementCurrentNumOfSpawnedArmorBulletsForTheNextAttack(int containerI) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        Gdx.app.log(TAG, ")))))))))<<<<<<-----currentNumOfSpawnedArmorBulletsForTheNextAttack-- (" + containerI + "), <- " + stack[2].getMethodName() + "() <- " + stack[3].getMethodName() + "() <- " + stack[4].getMethodName() + "() <- " + stack[5].getMethodName() + "().");
        currentNumOfSpawnedArmorBulletsForTheNextAttack--;
    }

    public boolean incrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack(int containerI) {
        if (currentNumOfCollectedArmorBulletsByThePlayerForNextAttack >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack())
            return false;

        Gdx.app.log(TAG, ")))))))))√√√√√incrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack++ (" + containerI + ")√√√√√√√√√√");

        currentNumOfCollectedArmorBulletsByThePlayerForNextAttack++;
        updateCharacterSequenceForCollectedArmorBulletsText();

        if (currentNumOfCollectedArmorBulletsByThePlayerForNextAttack >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()
                & armorGlowing.getColor().a == 0) {

            armorGlowing.addAction(Actions.alpha(1, LAZER_FADE_DURATION));
        }

        return true;
    }

    public void decrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack(int containerI) {
        if (armorGlowing.getColor().a > 0)
            //armorGlowing.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
            armorGlowing.setColor(1, 1, 1, 0);

        Gdx.app.log(TAG, ")))))))))√√√√√incrementCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack-- (" + containerI + ")√√√√√√√√√√");

        currentNumOfCollectedArmorBulletsByThePlayerForNextAttack--;
        updateCharacterSequenceForCollectedArmorBulletsText();
    }

    public void show() {
        nextLazerAttackTimerText.setVisible(true);
        armorBlack.setVisible(true);
        armorGlowing.setVisible(true);
        collectedArmorBulletsText.setVisible(true);

        armorBlack.setColor(1, 1, 1, 1);
        collectedArmorBulletsText.setColor(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1);
        nextLazerAttackTimerText.addAction(Actions.alpha(1, LAZER_FADE_DURATION));
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

    public boolean didThePlayerCollectAllTheNecessaryArmorBullets() {
        return getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/getCurrentNecessaryNumOfArmorBulletsForTheNextAttack();
    }

    public boolean areThereAnyLazerAttacksLeft() {
        return currentNumOfLazerAttacksThatTookPlace+1 < LAZER_NUMBER_OF_LAZER_ATTACKS;
    }

    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------

    public Image getArmorBlack() {
        return armorBlack;
    }

    public Image getArmorGlowing() {
        return armorGlowing;
    }

    public SimpleText getNextLazerAttackTimerText() {
        return nextLazerAttackTimerText;
    }

    public Timer getNextLazerAttackTimer() {
        return nextLazerAttackTimer;
    }

    public SimpleText getCollectedArmorBulletsText() {
        return collectedArmorBulletsText;
    }

    public void setLazerAttacking(boolean lazerAttacking) {
        this.lazerAttacking = lazerAttacking;
    }

    public boolean isLazerAttacking() {
        return lazerAttacking;
    }

    public int getCurrentNumOfLazerAttacksThatTookPlace() {
        return currentNumOfLazerAttacksThatTookPlace;
    }

    public void setCurrentNumOfLazerAttacksThatTookPlace(int currentNumOfLazerAttacksThatTookPlace) {
        this.currentNumOfLazerAttacksThatTookPlace = currentNumOfLazerAttacksThatTookPlace;
    }

    public int getCurrentNecessaryNumOfArmorBulletsForTheNextAttack() {
        return currentNecessaryNumOfArmorBulletsForTheNextAttack;
    }

    public void setCurrentNecessaryNumOfArmorBulletsForTheNextAttack(int currentNecessaryNumOfArmorBulletsForTheNextAttack) {
        this.currentNecessaryNumOfArmorBulletsForTheNextAttack = currentNecessaryNumOfArmorBulletsForTheNextAttack;
    }

    public int getCurrentNumOfSpawnedArmorBulletsForTheNextAttack() {
        return currentNumOfSpawnedArmorBulletsForTheNextAttack;
    }

    public void setCurrentNumOfSpawnedArmorBulletsForTheNextAttack(int currentNumOfSpawnedArmorBulletsForTheNextAttack) {
        this.currentNumOfSpawnedArmorBulletsForTheNextAttack = currentNumOfSpawnedArmorBulletsForTheNextAttack;
    }

    public int getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() {
        return currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;
    }

    public void setCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack(int currentNumOfCollectedArmorBulletsByThePlayerForNextAttack) {
        this.currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;
    }

    public LazerAttackHealthAffection getLazerAttackHealthAffection() {
        return lazerAttackHealthAffection;
    }

    public void setLazerAttackHealthAffection(LazerAttackHealthAffection lazerAttackHealthAffection) {
        this.lazerAttackHealthAffection = lazerAttackHealthAffection;
    }

    public void setWaitingForAllRemainingBulletsToBeCleared(boolean waitingForAllRemainingBulletsToBeCleared) {
        this.waitingForAllRemainingBulletsToBeCleared = waitingForAllRemainingBulletsToBeCleared;
    }

    public LazerAttack getLazerAttack() {
        return lazerAttack;
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
            public void onStart() {
                super.onStart();
                if (gameplayScreen.isRewinding()) {
                    nextLazerAttackTimerText.setColor(1, 1, 1, 0);
                } else {
                    nextLazerAttackTimerText.setColor(1, 1, 1, 1);
                    gameplayScreen.getBulletsHandler().calculateTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();
                    currentNumOfSpawnedArmorBulletsForTheNextAttack = 0;
                }
            }

            boolean textAlphaSetTo1 = false;

            @Override
            public boolean onUpdate(float delta) {
                /*if (gameplayScreen.isRewinding())
                    Gdx.app.log(TAG, "HERE!!!");*/
                
                String text = "Next Attack " + MyMath.toMinutesDigitalTimeFormat((1-getPercentage())*getDurationMillis()/1000f/60f);
                nextLazerAttackTimerText.setCharSequence(text, true);
                Viewport viewport = gameplayScreen.getStage().getViewport();
                nextLazerAttackTimerText.setX(viewport.getWorldWidth()/2 - nextLazerAttackTimerText.getWidth()/2f);

                /*if (gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet() != null)
                    Gdx.app.log(TAG, "" + gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet().getY());*/

                handleArmorBullets();

                float textFadeOutCurrentTime = getDurationMillis() - getCurrentTime();
                if (textFadeOutCurrentTime <= LAZER_FADE_DURATION*MyMath.SECONDS_TO_MILLIS) {
                    float percentage = (float) (textFadeOutCurrentTime/(LAZER_FADE_DURATION*MyMath.SECONDS_TO_MILLIS));
                    float aOut = percentage;
                    nextLazerAttackTimerText.setColor(1, 1, 1, aOut);
                    textAlphaSetTo1 = false;
                } else if (nextLazerAttackTimerText.getColor().a < 1 & !textAlphaSetTo1) {
                    nextLazerAttackTimerText.setColor(1, 1, 1, 1);
                    textAlphaSetTo1 = true;
                }

                return super.onUpdate(delta);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                // When you add something here, don't forget to add the opposite of it to NextLazerAttackTimerRecord.

                lazerAttacking = true;
                waitingForAllRemainingBulletsToBeCleared = true;

                nextLazerAttackTimerText.setColor(1, 1, 1, 0);

                //nextLazerAttackTimerText.addAction(Actions.alpha(0, LAZER_FADE_DURATION));

                if (!gameplayScreen.isRewinding()) {
                    RewindEngine rewindEngine = gameplayScreen.getRewindEngine();
                    NextLazerAttackTimerRecord nextLazerAttackTimerRecord = rewindEngine.obtainNextLazerAttackTimerRecord();
                    rewindEngine.pushRewindEvent(nextLazerAttackTimerRecord);
                }

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

                if (!gameplayScreen.isRewinding())
                    waitingForAllRemainingBulletsToBeCleared = false;
                else
                    waitingForAllRemainingBulletsToBeCleared = true;
                //gameplayScreen.getControllerLeft().addAction(Actions.alpha(0, LAZER_FADE_DURATION));
                //gameplayScreen.getControllerRight().addAction(Actions.alpha(0, LAZER_FADE_DURATION));
            }

            @Override
            public void onFinish() {
                super.onFinish();

                Gdx.app.log(TAG, "LAZER ENDS !!!!");

                if (!gameplayScreen.isRewinding()) {
                    LazerAttackRecord lazerAttackRecord = gameplayScreen.getRewindEngine().obtainLazerAttackRecord();
                    lazerAttackRecord.currentNumOfLazerAttacksThatTookPlace = currentNumOfLazerAttacksThatTookPlace;
                    lazerAttackRecord.currentNecessaryNumOfArmorBulletsForTheNextAttack = currentNecessaryNumOfArmorBulletsForTheNextAttack;
                    lazerAttackRecord.currentNumOfSpawnedArmorBulletsForTheNextAttack = currentNumOfSpawnedArmorBulletsForTheNextAttack;
                    lazerAttackRecord.currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;
                    gameplayScreen.getRewindEngine().pushRewindEvent(lazerAttackRecord);
                }

                calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();

                /*gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, LAZER_FADE_DURATION));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, LAZER_FADE_DURATION));*/

                currentNumOfCollectedArmorBulletsByThePlayerForNextAttack = 0;
                updateCharacterSequenceForCollectedArmorBulletsText();
                //armorGlowing.addAction(Actions.alpha(0, LAZER_FADE_DURATION));

                currentNumOfLazerAttacksThatTookPlace++;

                /*gameplayScreen.getBulletsHandler().calculateTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();
                currentNumOfSpawnedArmorBulletsForTheNextAttack = 0;*/

                if (areThereAnyLazerAttacksLeft()) {
                    //nextLazerAttackTimerText.addAction(Actions.alpha(1, LAZER_FADE_DURATION));
                    nextLazerAttackTimer.start();
                } else {
                    /*armorBlack.addAction(Actions.alpha(0, LAZER_FADE_DURATION));
                    armorGlowing.addAction(Actions.alpha(0, LAZER_FADE_DURATION));
                    collectedArmorBulletsText.addAction(Actions.alpha(0, LAZER_FADE_DURATION));*/
                }

                lazerAttacking = false;
                //gameplayScreen.getBulletsHandler().newWave(false, true);
                gameplayScreen.getBulletsHandler().nullifyCurrentWaveLastBullet(); // Creates new wave.

            }
        };
    }

}
