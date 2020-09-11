package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LazerAttackStuff implements Updatable, Resizable {

    private static final String TAG = LazerAttackStuff.class.getSimpleName();

    private Timer nextLazerAttackTimer;
    private SimpleText nextLazerAttackTimerText;
    private boolean lazerAttacking = false;

    int currentNumOfLazerAttacksThatTookPlace;

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

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeNextLazerAttackTimer() {
        String text = "Next Attack " + MyMath.toMinutesDigitalTimeFormat(LAZER_LEVEL_TIME/(LAZER_NUMBER_OF_LAZER_ATTACKS+1));
        nextLazerAttackTimerText = new SimpleText(gameplayScreen.getMyBitmapFont(), text);
        gameplayScreen.addActor(nextLazerAttackTimerText);


        nextLazerAttackTimer = new Timer(LAZER_LEVEL_TIME*60*1000/(LAZER_NUMBER_OF_LAZER_ATTACKS+1)) {
            @Override
            public boolean onUpdate(float delta) {
                String text = "Next Attack " + MyMath.toMinutesDigitalTimeFormat((1-getPercentage())*getDurationMillis()/1000f/60f);
                nextLazerAttackTimerText.setCharSequence(text, true);
                Viewport viewport = gameplayScreen.getStage().getViewport();
                nextLazerAttackTimerText.setX(viewport.getWorldWidth()/2 - nextLazerAttackTimerText.getWidth()/2f);

                /*if (gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet() != null)
                    Gdx.app.log(TAG, "" + gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet().getY());*/

                return super.onUpdate(delta);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                lazerAttacking = true;

                nextLazerAttackTimerText.addAction(Actions.alpha(0, 0.5f));

                float timeLeftForTheLastBulletToDisappear = calculateTheTimeLeftForTheLastBulletToDisappear();
                test.start(timeLeftForTheLastBulletToDisappear);
            }
        };

        gameplayScreen.addToFinishWhenLosing(nextLazerAttackTimer);
    }
}
