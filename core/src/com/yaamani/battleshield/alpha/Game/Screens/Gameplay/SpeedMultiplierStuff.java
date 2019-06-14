package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyProgressBar;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class SpeedMultiplierStuff implements Resizable, Updatable {

    private GameplayScreen gameplayScreen;

    private SimpleText bulletSpeedMultiplierText;

    private MyProgressBar myProgressBar;
    private Tween myProgressBarTween;

    public SpeedMultiplierStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeBulletSpeedMultiplierText(gameplayScreen.getMyBitmapFont());
        initializeProgressBar();
        initializeMyProgressBarTween();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        bulletSpeedMultiplierTextUpdatePosition();
        progressBarUpdatePosition();
    }

    @Override
    public void update(float delta) {
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
            float bulletSpeed = gameplayScreen.getBulletsHandler().getBulletSpeed();

            /*String currentSpeedStr = bulletSpeedMultiplierText.getCharSequence().substring(1);
            if (!currentSpeedStr.isEmpty()) {
                float currentSpeed;
                currentSpeed = Float.parseFloat(currentSpeedStr);
                if (currentSpeed != bulletSpeed) {
                    bulletSpeedMultiplierText.setCharSequence("x" + bulletSpeed / BULLETS_SPEED_INITIAL, true);

                    bulletSpeedMultiplierTextUpdatePosition();
                    progressBarUpdatePosition();

                    //myProgressBarTween.start();
                }
            } else {
                bulletSpeedMultiplierText.setCharSequence("x" + bulletSpeed / BULLETS_SPEED_INITIAL, true);

                bulletSpeedMultiplierTextUpdatePosition();
                progressBarUpdatePosition();
            }*/

            if (myProgressBarTween.isFinished() & bulletSpeed < BULLETS_SPEED_MULTIPLIER_MAX*BULLETS_SPEED_INITIAL)
                myProgressBar.setPercentage(gameplayScreen.getBulletsHandler().getCurrentSpeedMultiplierTimer().getPercentage()/*(gameplayScreen.getTimePlayedThisTurnSoFar() - gameplayScreen.getBulletsHandler().getSpeedResetTime()) % BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY*/);

            myProgressBarTween.update(delta);
        }
    }

    public void updateCharSequence(float currentSpeedMultiplier) {
        bulletSpeedMultiplierText.setCharSequence("x" + currentSpeedMultiplier, true);

        bulletSpeedMultiplierTextUpdatePosition();
        progressBarUpdatePosition();
    }

    private void bulletSpeedMultiplierTextUpdatePosition() {
        float bulletSpeedMultiplierTextWidth2 = bulletSpeedMultiplierText.getWidth()/2f;
        float bulletSpeedMultiplierTextHeight2 = bulletSpeedMultiplierText.getHeight()/2f;
        Viewport viewport = gameplayScreen.getStage().getViewport();
        bulletSpeedMultiplierText.setPosition(viewport.getWorldWidth()/2f - bulletSpeedMultiplierTextWidth2,
                viewport.getWorldHeight()/2f - bulletSpeedMultiplierTextHeight2);
    }

    private void progressBarUpdatePosition() {
        myProgressBar.setWidth(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_WIDTH);
        myProgressBar.setX(gameplayScreen.getStage().getViewport().getWorldWidth()/2f - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_WIDTH/2f);
        myProgressBar.setY(bulletSpeedMultiplierText.getY() - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TXT_DIFF_Y - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_HEIGHT);
    }

    public Tween getMyProgressBarTween() {
        return myProgressBarTween;
    }

    //---------------------------------------- Initializers ---------------------------------------
    //---------------------------------------- Initializers ---------------------------------------
    //---------------------------------------- Initializers ---------------------------------------

    private void initializeBulletSpeedMultiplierText(MyBitmapFont myBitmapFont) {
        bulletSpeedMultiplierText = new SimpleText(myBitmapFont, "x" + "1.0");
        bulletSpeedMultiplierText.setColor(BULLET_SPEED_MULTIPLIER_TXT_COLOR);
        bulletSpeedMultiplierText.setHeight(BULLET_SPEED_MULTIPLIER_TXT_HEIGHT);

        gameplayScreen.addActor(bulletSpeedMultiplierText);
    }

    private void initializeProgressBar() {
        TextureRegion reg = Assets.instance.gameplayAssets.gameOverBG;

        myProgressBar = new MyProgressBar(reg, 0.5f, 0.6f);
        gameplayScreen.addActor(myProgressBar);
        myProgressBar.setHeight(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_HEIGHT);

        myProgressBar.setBgColor(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_BG_COLOR);
        myProgressBar.setPercentageBarColor(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_PERCENTAGE_BAR_COLOR);
    }

    private void initializeMyProgressBarTween() {
        myProgressBarTween = new Tween(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION) {
            private float progressBarPercentageOnStart;
            private float progressBarPercentageOnFinish;

            @Override
            public void tween(float percentage) {
                myProgressBar.setPercentage(/*fastExp10Out*/fastExp5Out.apply(progressBarPercentageOnStart, progressBarPercentageOnFinish, percentage));
            }

            @Override
            public void onStart() {
                progressBarPercentageOnStart = myProgressBar.getPercentage();
                progressBarPercentageOnFinish = myProgressBarTween.getDurationMillis() / 1000f / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY;
                //setDurationMillis(Math.abs((progressBarPercentageOnFinish-progressBarPercentageOnStart) * BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION));
            }
        };

        myProgressBarTween.finish();
    }
}
