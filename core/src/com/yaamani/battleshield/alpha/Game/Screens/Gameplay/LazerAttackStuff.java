package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;
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
    private boolean lazerAttacking = false;

    private boolean waitingForAllRemainingBulletsToBeCleared;

    private int currentNumOfLazerAttacksThatTookPlace;

    private int currentNumOfSpawnedArmorBulletsForTheNextAttack;

    private Timer test;

    private GameplayScreen gameplayScreen;

    public LazerAttackStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        initializeNextLazerAttackTimer();

        test = new Timer(2000) {
            @Override
            public void onStart() {
                super.onStart();
                Gdx.app.log(TAG, "LAZER LAZER !!!!");
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(0, 0.25f));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(0, 0.25f));
            }

            @Override
            public void onFinish() {
                super.onFinish();

                currentNumOfLazerAttacksThatTookPlace++;

                Gdx.app.log(TAG, "LAZER ENDS !!!!");
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, 0.2f));
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, 0.2f));

                if (currentNumOfLazerAttacksThatTookPlace < LAZER_NUMBER_OF_LAZER_ATTACKS) {
                    nextLazerAttackTimerText.addAction(Actions.alpha(1, 0.25f));
                    nextLazerAttackTimer.start();
                }

                lazerAttacking = false;
                gameplayScreen.getBulletsHandler().newWave();
            }
        };
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        nextLazerAttackTimerText.setHeight(WORLD_SIZE/20f);
        nextLazerAttackTimerText.setPosition(worldWidth/2 - nextLazerAttackTimerText.getWidth()/2f, WORLD_SIZE*0.9f);
    }

    @Override
    public void update(float delta) {
        nextLazerAttackTimer.update(delta);
        waitingForAllRemainingBulletsToBeCleared();
        test.update(delta);
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

        int currentAttackMaxArmorBullets = D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS - currentNumOfLazerAttacksThatTookPlace;

        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < currentAttackMaxArmorBullets) {
            float timestampForTheCurrentBullet = timestampsForArmorBulletsToPrepareForTheNextLazerAttack[currentNumOfSpawnedArmorBulletsForTheNextAttack];
            float currentTime = nextLazerAttackTimer.getPercentage() * nextLazerAttackTimer.getDurationMillis();

            if (currentTime >= timestampForTheCurrentBullet & bulletsHandler.getForcedSpecialBullet() == null) {
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, false);
                Gdx.app.log(TAG, "Forced armor registered at " + currentTime);
            }
        }
    }

    private void waitingForAllRemainingBulletsToBeCleared() {
        if (!waitingForAllRemainingBulletsToBeCleared) return;

        if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(true)) {

            if (Bullet.getCurrentInUseBulletsCount() == 0) {

                if (makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(false)) { // When all bullets get cleared, just make sure once more.
                    waitingForAllRemainingBulletsToBeCleared = false;
                    test.start();
                }
            }
        }
    }

    private boolean makeSureThatTheCorrectNumOfArmorBulletsAreSpawned(boolean waitForTheRightMomentToSpawn) {
        int totalNumOfArmorBulletsThatShouldHaveBeenSpawned = D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS - currentNumOfLazerAttacksThatTookPlace;
        if (currentNumOfSpawnedArmorBulletsForTheNextAttack < totalNumOfArmorBulletsThatShouldHaveBeenSpawned) {

            BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
            if (bulletsHandler.getForcedSpecialBullet() == null)
                bulletsHandler.forceSpecialBullet(WaveBulletsType.SPECIAL_GOOD, SpecialBullet.ARMOR, false);

            if (waitForTheRightMomentToSpawn) {
                if (theRightMomentToSpawn()) {
                    Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                    bulletsHandler.newSingleWave(false);
                }
            } else {
                Gdx.app.log(TAG, "Shortage of armor bullets .... Spawning 1 armor bullet now.");
                bulletsHandler.newSingleWave(false);
            }

            return false;
        }
        return true;
    }

    public boolean theRightMomentToSpawn() {

        BulletsHandler bulletsHandler = gameplayScreen.getBulletsHandler();
        Bullet currentWaveLastBullet = bulletsHandler.getCurrentWaveLastBullet();

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

    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------

    public SimpleText getNextLazerAttackTimerText() {
        return nextLazerAttackTimerText;
    }

    public Timer getNextLazerAttackTimer() {
        return nextLazerAttackTimer;
    }

    public boolean isLazerAttacking() {
        return lazerAttacking;
    }

    public int getCurrentNumOfLazerAttacksThatTookPlace() {
        return currentNumOfLazerAttacksThatTookPlace;
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
                lazerAttacking = true;
                waitingForAllRemainingBulletsToBeCleared = true;

                nextLazerAttackTimerText.addAction(Actions.alpha(0, 0.5f));

                /*float timeLeftForTheLastBulletToDisappear = calculateTheTimeLeftForTheLastBulletToDisappear();
                test.start(timeLeftForTheLastBulletToDisappear);*/



            }
        };

        gameplayScreen.addToFinishWhenLosing(nextLazerAttackTimer);
    }
}
