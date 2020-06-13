package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
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

    /**
     * The score variable always stores the seconds played this turn so far. It doesn't matter whether survival mode is being played or a level is being played.
     */
    private float score;
    private float currentBest;
    private boolean playerScoredBest = false;

    private Preferences preferences;

    private SimpleText scoreText;

    private Tween fadeOutTween;

    private ScoreMultiplierStuff scoreMultiplierStuff;

    private MyTween scoreTweenStarBullet_FirstStage;
    private Tween scoreTweenStarBullet_ThirdStage;

    private Tween planetsTimerFlashesWhenZero;

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

        initializePlanetsTimerFlashesWhenZero();

        scoreMultiplierStuff = new ScoreMultiplierStuff(gameplayScreen);
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

        scoreMultiplierStuff.update(delta);

        planetsTimerFlashesWhenZero.update(delta);

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            if (!gameplayScreen.isInStarBulletAnimation()) score += delta * scoreMultiplierStuff.getScoreMultiplier();

        checkBestScore();

        fadeOutTween.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Preferences preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
            preferences.putFloat(SCORE_BEST_KEY, 0.0f);
            preferences.flush();
            currentBest = 0;
        }

        setCharSequenceForScoreText();
    }

    private void setCharSequenceForScoreText() {
        if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            // If SURVIVAL -> display the score variable.
            scoreText.setCharSequence("" + roundTo(score, 2), true);
        else {
            // If not SURVIVAL -> display a timer counting down.
            float levelTime = 0;

            switch (gameplayScreen.getGameplayMode()) {
                case CRYSTAL:
                    levelTime = CRYSTAL_LEVEL_TIME;
                    break;
            }

            float secondsLeft = MathUtils.clamp((float) (levelTime * MINUTES_TO_SECONDS - score), 0, Float.MAX_VALUE);
            scoreText.setCharSequence("" + toMinutesDigitalTimeFormat((float) (secondsLeft * SECONDS_TO_MINUTES)), true);

            if (secondsLeft == 0 & !planetsTimerFlashesWhenZero.isStarted() & gameplayScreen.getState() != GameplayScreen.State.LOST)
                planetsTimerFlashesWhenZero.start();
        }
    }

    private void checkBestScore() {
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING & gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            if ((currentBest == 0.0 | currentBest < score) & !playerScoredBest) {
                playerScoredBest = true;
                scoreText.setColor(SCORE_BEST_COLOR);
            }
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

    public ScoreMultiplierStuff getScoreMultiplierStuff() {
        return scoreMultiplierStuff;
    }

    public void startScoreTweenStarBullet_FirstStage() {
        scoreTweenStarBullet_FirstStage.setInitialVal(score);
        scoreTweenStarBullet_FirstStage.setFinalVal(score + 0.35f * STAR_BULLET_FIRST_STAGE_DURATION * (float) MILLIS_TO_SECONDS);
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

                if (gameplayScreen.getGameplayMode() != GameplayMode.SURVIVAL)
                    return;

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

                planetsTimerFlashesWhenZero.finish();
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

        gameplayScreen.addToFinishWhenLosing(scoreTweenStarBullet_FirstStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(scoreTweenStarBullet_FirstStage);
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

        gameplayScreen.addToFinishWhenLosing(scoreTweenStarBullet_ThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(scoreTweenStarBullet_ThirdStage);
    }

    private void initializePlanetsTimerFlashesWhenZero() {
        planetsTimerFlashesWhenZero = new Tween(PLANETS_TIMER_FLASHES_WHEN_ZERO_DURATION * (float) SECONDS_TO_MILLIS, PLANETS_TIMER_FLASHES_WHEN_ZERO_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float a = interpolation.apply(percentage);

                if (fadeOutTween.isStarted())
                    a *= 1 - fadeOutTween.getPercentage();

                scoreText.setColor(SCORE_COLOR.r, SCORE_COLOR.g, SCORE_COLOR.b, a);    
            }

            @Override
            public void onFinish() {
                super.onFinish();

                scoreText.setColor(SCORE_COLOR.r, SCORE_COLOR.g, SCORE_COLOR.b, 0);

                if (!fadeOutTween.isFinished())
                    start();
            }
        };

        //gameplayScreen.addToFinishWhenLosing(planetsTimerFlashesWhenZero);
    }














    public static class TimePlayedSoFarStarBulletThirdStageInterpolation extends Interpolation {

        @Override
        public float apply(float a) {

            float T8 = 26.7301674743f   * a*a*a*a*a*a*a*a;
            float T7 = -149.801712332f  * a*a*a*a*a*a*a;
            float T6 = 338.685260991f   * a*a*a*a*a*a;
            float T5 = -393.442712938f  * a*a*a*a*a;
            float T4 = 247.824289679f   * a*a*a*a;
            float T3 = -82.702738645f   * a*a*a;
            float T2 = 13.1655331616f   * a*a;
            float T1 = 0.540199230611f  * a;

            return (T8 + T7 + T6 + T5 + T4 + T3 + T2 + T1) / 0.998286620477f;
        }
    }
}
