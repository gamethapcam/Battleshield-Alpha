package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
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

public class scoreMultiplierStuff implements Resizable, Updatable {

    private GameplayScreen gameplayScreen;

    private SimpleText scoreMultiplierText;

    private MyProgressBar myProgressBar;
    private Tween myProgressBarTween;

    //private Tween scoreMultiplier;

    private float scoreMultiplier = 1;

    private Tween scoreMultiplierTween;

    public scoreMultiplierStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeScoreMultiplierText(gameplayScreen.getMyBitmapFont());
        initializeProgressBar();
        initializeMyProgressBarTween();
        initializeScoreMultiplierTween();
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
            //gameplayScreen.getBulletsHandler().getCurrentSpeedMultiplier()

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

            if (myProgressBarTween.isFinished()) {
                if (bulletSpeed < BULLETS_SPEED_MULTIPLIER_MAX * BULLETS_SPEED_INITIAL)
                    myProgressBar.setPercentage(gameplayScreen.getBulletsHandler().getCurrentDifficultyLevelTimer().getPercentage()/*(gameplayScreen.getTimePlayedThisTurnSoFar() - gameplayScreen.getBulletsHandler().getSpeedResetTime()) % BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY*/);
                else myProgressBar.setPercentage(1);
            }

            myProgressBarTween.update(delta);
            scoreMultiplierTween.update(delta);
        }
    }

    private void updateCharSequence(float currentMultiplier) {
        scoreMultiplierText.setCharSequence("x" + currentMultiplier, true);

        bulletSpeedMultiplierTextUpdatePosition();
        progressBarUpdatePosition();
    }

    private void bulletSpeedMultiplierTextUpdatePosition() {
        float bulletSpeedMultiplierTextWidth2 = scoreMultiplierText.getWidth()/2f;
        float bulletSpeedMultiplierTextHeight2 = scoreMultiplierText.getHeight()/2f;
        Viewport viewport = gameplayScreen.getStage().getViewport();
        /*scoreMultiplierText.setPosition(viewport.getWorldWidth()/2f - bulletSpeedMultiplierTextWidth2,
                viewport.getWorldHeight()/2f - bulletSpeedMultiplierTextHeight2 / 1.7f);*/
        scoreMultiplierText.setPosition(SCORE_TXT_MARGIN, gameplayScreen.getScoreStuff().getScoreText().getY() - SCORE_TXT_MARGIN - bulletSpeedMultiplierTextHeight2);
    }

    private void progressBarUpdatePosition() {
        myProgressBar.setWidth(/*BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_WIDTH*/scoreMultiplierText.getWidth());
        //myProgressBar.setX(gameplayScreen.getStage().getViewport().getWorldWidth()/2f - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_WIDTH/2f);
        myProgressBar.setX(scoreMultiplierText.getX());
        myProgressBar.setY(scoreMultiplierText.getY() - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TXT_DIFF_Y - BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_HEIGHT);
    }

    public Tween getMyProgressBarTween() {
        return myProgressBarTween;
    }

    public SimpleText getScoreMultiplierText() {
        return scoreMultiplierText;
    }

    public MyProgressBar getMyProgressBar() {
        return myProgressBar;
    }

    public float getScoreMultiplier() {
        return scoreMultiplier;
    }

    public Tween getScoreMultiplierTween() {
        return scoreMultiplierTween;
    }

    //---------------------------------------- Initializers ---------------------------------------
    //---------------------------------------- Initializers ---------------------------------------
    //---------------------------------------- Initializers ---------------------------------------

    private void initializeScoreMultiplierText(MyBitmapFont myBitmapFont) {
        scoreMultiplierText = new SimpleText(myBitmapFont, "x" + "1.0");
        scoreMultiplierText.setColor(BULLET_SPEED_MULTIPLIER_TXT_COLOR);
        scoreMultiplierText.setHeight(BULLET_SPEED_MULTIPLIER_TXT_HEIGHT);

        gameplayScreen.addActor(scoreMultiplierText);
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
        myProgressBarTween = new Tween(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION, fadeOut) {
            private float progressBarPercentageOnStart;
            private float progressBarPercentageOnFinish;

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    myProgressBar.setPercentage(/*myExp10Out*/interpolation.apply(progressBarPercentageOnStart, progressBarPercentageOnFinish, percentage));
            }

            @Override
            public void onStart() {
                progressBarPercentageOnStart = myProgressBar.getPercentage();
                progressBarPercentageOnFinish = myProgressBarTween.getDurationMillis() / 1000f / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY;
                //setDurationMillis(Math.abs((progressBarPercentageOnFinish-progressBarPercentageOnStart) * BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION));
            }
        };

        myProgressBarTween.finish();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(myProgressBarTween);
    }

    private void initializeScoreMultiplierTween() {
        scoreMultiplierTween = new Tween(BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL * 1000 * BULLETS_NUMBER_OF_DIFFICULTY_LEVELS, SCORE_MULTIPLIER_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float newScoreMultiplier = interpolation.apply(SCORE_MULTIPLIER_MIN, SCORE_MULTIPLIER_MAX, percentage);
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    if (newScoreMultiplier != scoreMultiplier) {
                        scoreMultiplier = newScoreMultiplier;
                        updateCharSequence(newScoreMultiplier);
                    }
            }
        };

        scoreMultiplierTween.start();

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(scoreMultiplierTween);

    }
}
