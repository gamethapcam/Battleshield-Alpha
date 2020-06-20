package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyProgressBar;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class ScoreMultiplierStuff implements Resizable, Updatable {

    private GameplayScreen gameplayScreen;

    private SimpleText scoreMultiplierText;

    private MyProgressBar myProgressBar;
    private Tween myProgressBarTween;

    //private Tween scoreMultiplier;

    private float scoreMultiplier = 1;

    private Tween scoreMultiplierTween; // The higher the difficulty the higher the score multiplier.

    public ScoreMultiplierStuff(GameplayScreen gameplayScreen) {
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
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING & gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL) {

            /*if (myProgressBarTween.isFinished()) {
                if (scoreMultiplier < SCORE_MULTIPLIER_MAX)
                    myProgressBar.setPercentage(gameplayScreen.getBulletsHandler().getCurrentDifficultyLevelTimer().getPercentage());
                else myProgressBar.setPercentage(1);
            }*/

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

    public void setVisible(boolean visible) {
        scoreMultiplierText.setVisible(visible);
        myProgressBar.setVisible(visible);
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
        TextureRegion reg = Assets.instance.mutualAssets.bigCircle;

        myProgressBar = new MyProgressBar(reg, 0.5f, MY_PROGRESS_BAR_DEFAULT_PERCENTAGE_BAR_HEIGHT_RATIO);
        gameplayScreen.addActor(myProgressBar);
        myProgressBar.setHeight(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_HEIGHT);

        myProgressBar.setBgColor(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_BG_COLOR);
        myProgressBar.setPercentageBarColor(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_PERCENTAGE_BAR_COLOR);
    }

    private void initializeMyProgressBarTween() {
        /*myProgressBarTween = new Tween(BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION, fadeOut) {
            private float progressBarPercentageOnStart;
            private float progressBarPercentageOnFinish;

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    myProgressBar.setPercentage(interpolation.apply(progressBarPercentageOnStart, progressBarPercentageOnFinish, percentage));
            }

            @Override
            public void onStart() {
                if (scoreMultiplier >= SCORE_MULTIPLIER_MAX-SCORE_MULTIPLIER_INCREMENT) finish();
                progressBarPercentageOnStart = myProgressBar.getPercentage();
                progressBarPercentageOnFinish = myProgressBarTween.getDurationMillis() / 1000f / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY;
            }
        };*/

        myProgressBarTween = new Tween(BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL * 1000 * BULLETS_NUMBER_OF_DIFFICULTY_LEVELS, SCORE_MULTIPLIER_PROGRESS_BAR_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                myProgressBar.setPercentage(interpolation.apply(percentage));
                //Gdx.app.log(TAG, "" + myProgressBar.getPercentage());
            }
        };

        //myProgressBarTween.finish();
        myProgressBarTween.start();

        gameplayScreen.addToFinishWhenLosing(myProgressBarTween);
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

        gameplayScreen.addToFinishWhenLosing(scoreMultiplierTween);

    }

















    public static class ProgressBarTweenSingleDifficultyInterpolation extends MyInterpolation {

        private float x0;
        private float p;

        private float m;
        private float c;

        public ProgressBarTweenSingleDifficultyInterpolation(float x0, float p) {
            if (x0 > 1 | x0 < 0) throw new ValueOutOfRangeException("0 <= x0 <= 1.");
            this.x0 = x0;
            this.p = p;

            m = 1f/(1-x0);
            c = (float) (1 / Math.pow(x0, p));
        }


        @Override
        public float apply(float a) {
            if (a > x0)
                return 1 + m*(a-1);
            else return (float) (c * Math.pow(x0 - a, p));
        }

        @Override
        public float slopeAt(float x01) {
            throw new UnsupportedOperationException();
        }

        @Override
        public float inverseFunction(float y01) {
            throw new UnsupportedOperationException();

        }
    }

    public static class ProgressBarTweenInterpolation extends RepeatedCurveCustomScaleIntervals {

        ProgressBarTweenSingleDifficultyInterpolation progressBarTweenSingleDifficultyInterpolation;


        public ProgressBarTweenInterpolation(int n, float x0, float p, MyInterpolation timeScale, MyInterpolation outputScale) {
            super(n, 1, myLinear, timeScale, outputScale);
            progressBarTweenSingleDifficultyInterpolation = new ProgressBarTweenSingleDifficultyInterpolation(x0, p);
        }

        @Override
        public float apply(float a) {
            if (getInterval(a) == n-1)
                return 1;
            else if (getInterval(a) == 0) {
                toBeRepeatedCurve = myLinear;
                return super.apply(a);
            } else {
                toBeRepeatedCurve = progressBarTweenSingleDifficultyInterpolation;
                return super.apply(a);
            }
        }
    }
}
