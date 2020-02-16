package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class ScoreStuff implements Resizable, Updatable {

    public static final String TAG = ScoreStuff.class.getSimpleName();

    private float score;
    private float currentBest;
    private boolean playerScoredBest = false;

    private Preferences preferences;

    private SimpleText scoreText;

    private Tween fadeOutTween;

    private scoreMultiplierStuff scoreMultiplierStuff;

    private MyTween scoreTweenStarBullet_FirstStage;
    private Tween scoreTweenStarBullet_ThirdStage;

    /*private GameplayScreen.ShieldsAndContainersHandler shieldsAndContainersHandler;
    private GameplayScreen.BulletsHandler bulletsHandler;*/
    private GameplayScreen gameplayScreen;


    public ScoreStuff(GameplayScreen gameplayScreen, MyBitmapFont font) {
        this.gameplayScreen = gameplayScreen;

        scoreText = new SimpleText(font, "0.0");
        gameplayScreen.addActor(scoreText);
        scoreText.setHeight(SCORE_TXT_HEIGHT);

        preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
        loadBestFromHardDrive();
        resetScore();

        initializeFadeOutTween();

        initializeScoreTweenStarBullet_FirstStage();
        initializeScoreTweenStarBullet_ThirdStage();

        scoreMultiplierStuff = new scoreMultiplierStuff(gameplayScreen);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

        scoreText.setX(SCORE_TXT_MARGIN);
        scoreText.setY(worldHeight - scoreText.getHeight() - SCORE_TXT_MARGIN);

        scoreMultiplierStuff.resize(width, height, worldWidth, worldHeight);

    }

    @Override
    public void update(float delta) {
        scoreTweenStarBullet_FirstStage.update(delta);
        scoreTweenStarBullet_ThirdStage.update(delta);

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
            scoreMultiplierStuff.update(delta);

            if (!gameplayScreen.isInStarBulletAnimation()) score += delta * scoreMultiplierStuff.getScoreMultiplier();
            
            /*score += activeShields*delta;
            score += BULLETS_MAX_NUMBER_PER_ATTACK / (float) bulletsPerAttack * delta;*/
            checkBestScore();
        }

        fadeOutTween.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Preferences preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
            preferences.putFloat(SCORE_BEST_KEY, 0.0f);
            preferences.flush();
            currentBest = 0;
        }

        scoreText.setCharSequence("" + roundTo(score, 2), true);
    }

    public void updateBestScoreButDontRegisterToHardDriveYet() {
        if (playerScoredBest) {
            currentBest = score;
        }
    }

    public void registerBestScoreToHardDrive() {
        if (currentBest > preferences.getFloat(SCORE_BEST_KEY)) {
            preferences.putFloat(SCORE_BEST_KEY, score);
            preferences.flush();
        }
    }

    private void checkBestScore() {
        if ((currentBest == 0.0 | currentBest < score) & !playerScoredBest) {
            playerScoredBest = true;
            scoreText.setColor(SCORE_BEST_COLOR);
        }
    }

    public void resetScore() {
        score = 0;
        playerScoredBest = false;
    }

    private void loadBestFromHardDrive() {
        currentBest = preferences.getFloat(SCORE_BEST_KEY);
    }

    public float getCurrentBest() {
        return currentBest;
    }

    public boolean isPlayerScoredBest() {
        return playerScoredBest;
    }

    public SimpleText getScoreText() {
        return scoreText;
    }

    public float getScore() {
        return score;
    }

    public Tween getFadeOutTween() {
        return fadeOutTween;
    }

    public scoreMultiplierStuff getScoreMultiplierStuff() {
        return scoreMultiplierStuff;
    }

    public void startScoreTweenStarBullet_FirstStage() {
        scoreTweenStarBullet_FirstStage.setInitialVal(score);
        scoreTweenStarBullet_FirstStage.setFinalVal(score + 0.35f * STAR_BULLET_FIRST_STAGE_DURATION * (float) millisToSeconds);
        scoreTweenStarBullet_FirstStage.start();
    }

    public void startScoreTweenStarBullet_ThirdStage() {
        timePlayedSoFarStarBulletThirdStageInitialValue = score;
        timePlayedSoFarStarBulletThirdStageFinalValue = score + STAR_BULLET_SCORE_BONUS * scoreMultiplierStuff.getScoreMultiplier();
        scoreTweenStarBullet_ThirdStage.start();
    }

    //----------------------------------------------------------------------------

    private void initializeFadeOutTween() {
        fadeOutTween = new Tween(SCORE_FADE_OUT_TWEEN_DURATION, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                Color scoreTextColor = scoreText.getColor();
                Color scoreMultiplierTextColor = scoreMultiplierStuff.getScoreMultiplierText().getColor();
                //Color scoreMultiplierProgressBarColor = scoreMultiplierStuff.getMyProgressBar().getColor();

                float alpha = interpolation.apply(1 - percentage);

                scoreText.setColor(scoreTextColor.r, scoreTextColor.g, scoreTextColor.b, alpha);
                scoreMultiplierStuff.getScoreMultiplierText().setColor(scoreMultiplierTextColor.r, scoreMultiplierTextColor.g, scoreMultiplierTextColor.b, alpha);
                scoreMultiplierStuff.getMyProgressBar().setAlpha(alpha);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                scoreText.setColor(SCORE_COLOR.r, SCORE_COLOR.g, SCORE_COLOR.b, 0.0f);
            }
        };
    }

    private void initializeScoreTweenStarBullet_FirstStage() {
        scoreTweenStarBullet_FirstStage = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION_INTEGRATION_OUT) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                if (gameplayScreen.isInStarBulletAnimation())
                    score = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(scoreTweenStarBullet_FirstStage);
    }


    private float timePlayedSoFarStarBulletThirdStageInitialValue;
    private float timePlayedSoFarStarBulletThirdStageFinalValue;
    private void initializeScoreTweenStarBullet_ThirdStage() {
        scoreTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_SCORE_INTERPOLATION/*MyInterpolation.myLinear*/) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (gameplayScreen.isInStarBulletAnimation())
                    score = interpolation.apply(timePlayedSoFarStarBulletThirdStageInitialValue, timePlayedSoFarStarBulletThirdStageFinalValue, percentage);
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(scoreTweenStarBullet_ThirdStage);
    }

}
