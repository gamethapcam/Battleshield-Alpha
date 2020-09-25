package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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

public class ScoreTimerStuff implements Resizable, Updatable {

    public static final String TAG = ScoreTimerStuff.class.getSimpleName();

    /**
     * The scoreTimer variable always stores the seconds played this turn so far. It doesn't matter whether survival mode is being played or a level is being played.
     */
    private float scoreTimer;
    private float currentBest;
    private boolean playerScoredBest = false;

    private float levelTime; // Not survival

    private Preferences preferences;

    private SimpleText scoreText;

    private Tween fadeOutTween;

    private ScoreMultiplierDifficultyLevelStuff scoreMultiplierDifficultyLevelStuff;

    private MyTween scoreTweenStarBullet_FirstStage;
    private Tween scoreTweenStarBullet_ThirdStage;

    private Tween planetsTimerFlashesWhenZero;

    /*private GameplayScreen.ShieldsAndContainersHandler shieldsAndContainersHandler;
    private GameplayScreen.BulletsHandler bulletsHandler;*/
    private GameplayScreen gameplayScreen;


    public ScoreTimerStuff(GameplayScreen gameplayScreen, MyBitmapFont font) {
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

        scoreMultiplierDifficultyLevelStuff = new ScoreMultiplierDifficultyLevelStuff(gameplayScreen);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

        scoreText.setX(SCORE_TXT_MARGIN);
        scoreText.setY(worldHeight - scoreText.getHeight() - SCORE_TXT_MARGIN);

        scoreMultiplierDifficultyLevelStuff.resize(width, height, worldWidth, worldHeight);

    }

    @Override
    public void update(float delta) {
        scoreTweenStarBullet_FirstStage.update(delta);
        scoreTweenStarBullet_ThirdStage.update(delta);

        scoreMultiplierDifficultyLevelStuff.update(delta);

        planetsTimerFlashesWhenZero.update(delta);

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            if (!gameplayScreen.isInStarBulletAnimation())
                if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
                    scoreTimer += delta * scoreMultiplierDifficultyLevelStuff.getScoreMultiplierDifficultyLevel();
                else
                    scoreTimer += delta;

        checkBestScore();

        fadeOutTween.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Preferences preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
            preferences.putFloat(SCORE_BEST_KEY, 0.0f);
            preferences.flush();
            currentBest = 0;
        }



        if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            updateCharSequenceForScoreTextWhenSurvival();
        else {
            float secondsLeft = calculateSecondsLeft();
            updateCharSequenceForScoreTextWhenNotSurvival(secondsLeft);

            handleFinishButton(secondsLeft);
        }


        //updateCharSequenceForScoreText();
    }

    private void updateCharSequenceForScoreTextWhenSurvival() {
        scoreText.setCharSequence("" + roundTo(scoreTimer, 2), true);
    }

    private void updateCharSequenceForScoreTextWhenNotSurvival(float secondsLeft) {
        scoreText.setCharSequence("" + toMinutesDigitalTimeFormat((float) (secondsLeft * SECONDS_TO_MINUTES)), true);
    }

    private void handleFinishButton(float secondsLeft) {
        if (secondsLeft == 0 & !planetsTimerFlashesWhenZero.isStarted() & gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
            gameplayScreen.onWaitingForFinishButtonToBePressed();
        }
    }

    /*private void updateCharSequenceForScoreText() {
        if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            // If SURVIVAL -> display the score variable.
            scoreText.setCharSequence("" + roundTo(scoreTimer, 2), true);
        else {
            // If not SURVIVAL -> display a timer counting down.

            float secondsLeft = MathUtils.clamp((float) (levelTime * MINUTES_TO_SECONDS - scoreTimer), 0, Float.MAX_VALUE);
            scoreText.setCharSequence("" + toMinutesDigitalTimeFormat((float) (secondsLeft * SECONDS_TO_MINUTES)), true);

            if (secondsLeft == 0 & !planetsTimerFlashesWhenZero.isStarted() & gameplayScreen.getState() != GameplayScreen.State.LOST) {
                gameplayScreen.onWaitingForFinishButtonToBePressed();
            }
        }
    }*/

    private float calculateSecondsLeft() {
        return MathUtils.clamp((float) (levelTime * MINUTES_TO_SECONDS - scoreTimer), 0, Float.MAX_VALUE);
    }

    private void checkBestScore() {
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING & gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            if ((currentBest == 0.0 | currentBest < scoreTimer) & !playerScoredBest) {
                playerScoredBest = true;
                scoreText.setColor(SCORE_BEST_COLOR);
            }
    }

    public void updateBestScoreButDontRegisterToHardDriveYet() {
        if (playerScoredBest) {
            currentBest = scoreTimer;
        }
    }

    public void registerBestScoreToHardDrive() {
        if (currentBest > preferences.getFloat(SCORE_BEST_KEY)) {
            preferences.putFloat(SCORE_BEST_KEY, scoreTimer);
            preferences.flush();
        }
    }

    public void resetScore() {
        scoreTimer = 0;
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

    public float getScoreTimer() {
        return scoreTimer;
    }

    public Tween getFadeOutTween() {
        return fadeOutTween;
    }

    public Tween getPlanetsTimerFlashesWhenZero() {
        return planetsTimerFlashesWhenZero;
    }

    public ScoreMultiplierDifficultyLevelStuff getScoreMultiplierDifficultyLevelStuff() {
        return scoreMultiplierDifficultyLevelStuff;
    }

    public void startScoreTweenStarBullet_FirstStage() {
        scoreTweenStarBullet_FirstStage.setInitialVal(scoreTimer);
        scoreTweenStarBullet_FirstStage.setFinalVal(scoreTimer + 0.35f * STAR_BULLET_FIRST_STAGE_DURATION * (float) MILLIS_TO_SECONDS);
        scoreTweenStarBullet_FirstStage.start();
    }

    public void startScoreTweenStarBullet_ThirdStage() {
        timePlayedSoFarStarBulletThirdStageInitialValue = scoreTimer;
        timePlayedSoFarStarBulletThirdStageFinalValue = scoreTimer + STAR_BULLET_SCORE_BONUS * scoreMultiplierDifficultyLevelStuff.getScoreMultiplierDifficultyLevel();
        scoreTweenStarBullet_ThirdStage.start();
    }

    /*public void gameplayModeStuff(GameplayMode gameplayMode) {
        scoreMultiplierDifficultyLevelStuff.gameplayModeStuff(gameplayMode);
    }*/

    public void setLevelTime(float levelTime) {
        this.levelTime = levelTime;
    }

    //----------------------------------------------------------------------------

    private void initializeFadeOutTween() {
        fadeOutTween = new Tween(SCORE_FADE_OUT_TWEEN_DURATION, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {

                /*if (gameplayScreen.getGameplayMode() != GameplayMode.SURVIVAL)
                    return;*/

                Color scoreTextColor = scoreText.getColor();
                Color scoreMultiplierTextColor = scoreMultiplierDifficultyLevelStuff.getScoreMultiplierText().getColor();
                //Color scoreMultiplierProgressBarColor = scoreMultiplierStuff.getMyProgressBar().getColor();

                float alpha = interpolation.apply(1 - percentage);

                scoreText.setColor(scoreTextColor.r, scoreTextColor.g, scoreTextColor.b, alpha);
                scoreMultiplierDifficultyLevelStuff.getScoreMultiplierText().setColor(scoreMultiplierTextColor.r, scoreMultiplierTextColor.g, scoreMultiplierTextColor.b, alpha);
                scoreMultiplierDifficultyLevelStuff.getMyProgressBar().setAlpha(alpha);
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
                    scoreTimer = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(scoreTweenStarBullet_FirstStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(scoreTweenStarBullet_FirstStage);
    }


    private float timePlayedSoFarStarBulletThirdStageInitialValue;
    private float timePlayedSoFarStarBulletThirdStageFinalValue;
    private void initializeScoreTweenStarBullet_ThirdStage() {
        scoreTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_SCORE_INTERPOLATION/*MyInterpolation.myLinear*/) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (gameplayScreen.isInStarBulletAnimation())
                    scoreTimer = interpolation.apply(timePlayedSoFarStarBulletThirdStageInitialValue, timePlayedSoFarStarBulletThirdStageFinalValue, percentage);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(scoreTweenStarBullet_ThirdStage);
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
