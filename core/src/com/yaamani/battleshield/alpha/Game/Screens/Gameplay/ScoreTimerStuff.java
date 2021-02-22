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
     * The scoreTimer variable always stores the seconds played this round so far. It doesn't matter whether survival mode is being played or a level is being played.
     */
    private float scoreTimer;
    private float currentBest;
    private boolean playerScoredBest = false;

    private float levelTime; // Not survival

    private Preferences preferences;

    private SimpleText scoreText;

    private Tween fadeOutTween;

    private ScoreMultiplierDifficultyLevelStuff scoreMultiplierDifficultyLevelStuff;

    private MyTween scoreSlowMoTween;
    private Tween scoreTweenStarBullet_ThirdStage;

    private Tween planetsTimerFlashesWhenZero;


    private Tween affectTimerTween;
    private Tween affectTimerColorTween;

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

        initializeScoreSlowMoTween();
        initializeScoreTweenStarBullet_ThirdStage();

        initializePlanetsTimerFlashesWhenZero();

        scoreMultiplierDifficultyLevelStuff = new ScoreMultiplierDifficultyLevelStuff(gameplayScreen);

        initializeAffectTimerTween();
        initializeAffectTimerColorTween();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

        scoreText.setX(SCORE_TXT_MARGIN);
        scoreText.setY(worldHeight - scoreText.getHeight() - SCORE_TXT_MARGIN);

        scoreMultiplierDifficultyLevelStuff.resize(width, height, worldWidth, worldHeight);

    }

    @Override
    public void update(float delta) {
        scoreSlowMoTween.update(delta);
        scoreTweenStarBullet_ThirdStage.update(delta);

        scoreMultiplierDifficultyLevelStuff.update(delta);

        planetsTimerFlashesWhenZero.update(delta);


        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            if (!gameplayScreen.isInStarBulletAnimation() & !gameplayScreen.isInRewindBulletAnimation())
                if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
                    scoreTimer += delta * scoreMultiplierDifficultyLevelStuff.getScoreMultiplierDifficultyLevel();
                else
                    scoreTimer += delta;


        checkBestScore();
        updateBestScoreButDontRegisterToHardDriveYet();

        fadeOutTween.update(delta);

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Preferences preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
            preferences.putFloat(SCORE_BEST_KEY, 0.0f);
            preferences.flush();
            currentBest = 0;
        }*/



        updateCharSequenceForScoreText();


        /*if (Gdx.input.isKeyJustPressed(Input.Keys.H))
            affectTimer(D_T1_AFFECT_TIMER_HEART_AMOUNT);*/

        //updateCharSequenceForScoreText();
    }

    public void updateAffectTimerTweens(float delta) {
        affectTimerTween.update(delta);
        affectTimerColorTween.update(delta);
    }

    public void updateCharSequenceForScoreText() {
        if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL)
            updateCharSequenceForScoreTextWhenSurvival();
        else {
            float secondsLeft = calculateSecondsLeft();
            updateCharSequenceForScoreTextWhenNotSurvival(secondsLeft);

            handleFinishButton(secondsLeft);
        }
    }

    private void updateCharSequenceForScoreTextWhenSurvival() {
        scoreText.setCharSequence("" + roundTo(scoreTimer, 2), true);
    }

    private void updateCharSequenceForScoreTextWhenNotSurvival(float secondsLeft) {
        scoreText.setCharSequence("" + toMinutesDigitalTimeFormat((float) (secondsLeft * SECONDS_TO_MINUTES)), true);
    }

    private void handleFinishButton(float secondsLeft) {
        /*Gdx.app.log(TAG, "secondsLeft = " + secondsLeft +
                ", planetsTimerFlashesWhenZero.isStarted() = " + planetsTimerFlashesWhenZero.isStarted() +
                ", gameplayScreen.getState() = " + gameplayScreen.getState());*/

        if (secondsLeft == 0 & (!planetsTimerFlashesWhenZero.isStarted() | planetsTimerFlashesWhenZero.isPaused()) & gameplayScreen.getState() != GameplayScreen.State.STOPPED) {
            gameplayScreen.showFinishButtonAndRelatedStuff();
        } else if (secondsLeft > 0 & gameplayScreen.getLevelFinishStuff().getFinishText().isVisible()) {
            gameplayScreen.hideFinishButtonAndRelatedStuff();
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
        /*if (scoreTimer > levelTime * MINUTES_TO_SECONDS) {
            scoreTimer = (float) (levelTime * MINUTES_TO_SECONDS);
        }*/
        float secondsLeft = (float) (levelTime * MINUTES_TO_SECONDS - scoreTimer);
        return MathUtils.clamp((float) (secondsLeft), 0, Float.MAX_VALUE);
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

    public void affectTimer(float amountMillis) {
        float durationMillis = Math.abs(amountMillis) * 0.5f/5f;
        affectTimer(amountMillis, durationMillis, AFFECT_TIMER_TWEEN_INTERPOLATION);
    }

    public void affectTimer(float amountMillis, float durationMillis, Interpolation interpolation) {

        //Gdx.app.log(TAG, "" + affectTimerTween.isStarted());

        float previous = 0;
        if (affectTimerTween.isStarted())
            previous = (1-affectTimerTween.getPercentage()) *
                    (affectTimerTweenFinalValue - affectTimerTweenInitialValue);

        affectTimerTween.setDurationMillis(durationMillis);
        affectTimerTween.setInterpolation(interpolation);

        affectTimerTweenInitialValue = scoreTimer;
        affectTimerTweenFinalValue = affectTimerTweenInitialValue +
                (float) ((amountMillis + Math.signum(amountMillis) * affectTimerTween.getDurationMillis()) * MILLIS_TO_SECONDS) +
                previous;

        //Gdx.app.log(TAG, "" + (affectTimerTweenFinalValue - affectTimerTweenInitialValue));
        affectTimerTween.start();


        affectTimerColorTween.setDurationMillis(Math.max(AFFECT_TIMER_COLOR_TWEEN_DURATION, durationMillis));
        affectTimerColorTween.start();
    }

    //public void aff

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

    public Tween getAffectTimerTween() {
        return affectTimerTween;
    }

    public ScoreMultiplierDifficultyLevelStuff getScoreMultiplierDifficultyLevelStuff() {
        return scoreMultiplierDifficultyLevelStuff;
    }

    public void startScoreSlowMoTween() {
        scoreSlowMoTween.setInitialVal(scoreTimer);
        scoreSlowMoTween.setFinalVal(scoreTimer + 0.35f * SLOW_MO_TWEENS_DURATION * (float) MILLIS_TO_SECONDS);
        scoreSlowMoTween.start();
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

    private void initializeScoreSlowMoTween() {
        scoreSlowMoTween = new MyTween(SLOW_MO_TWEENS_DURATION, SLOW_MO_TWEENS_INTERPOLATION_INTEGRATION_OUT) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                //if (gameplayScreen.isInStarBulletAnimation())
                if (gameplayScreen.getState() != GameplayScreen.State.STOPPED)
                    scoreTimer = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(scoreSlowMoTween);
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
            public void onPause() {
                super.onPause();
                scoreText.setColor(SCORE_COLOR.r, SCORE_COLOR.g, SCORE_COLOR.b, 1);
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


    private float affectTimerTweenInitialValue;
    private float affectTimerTweenFinalValue;
    //private boolean affectTimerGreen;
    private void initializeAffectTimerTween() {
        affectTimerTween = new Tween() {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                scoreTimer = interpolation.apply(affectTimerTweenInitialValue, affectTimerTweenFinalValue, percentage);

                /*boolean goodForThePlayer = affectTimerTweenFinalValue > affectTimerTweenInitialValue;
                if (goodForThePlayer)
                    scoreText.setColor(percentage, 1, percentage, 1);
                else
                    scoreText.setColor(1, percentage, percentage, 1);*/

            }
        };
    }

    private void initializeAffectTimerColorTween() {
        affectTimerColorTween = new Tween(AFFECT_TIMER_COLOR_TWEEN_DURATION, AFFECT_TIMER_COLOR_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                boolean goodForThePlayer = affectTimerTweenFinalValue > affectTimerTweenInitialValue;
                float c = interpolation.apply(percentage);
                if (goodForThePlayer) {
                    float g = interpolation.apply(HEALTH_BAR_HEALTH_OVER_FLOW_COLOR.g, 1, percentage);
                    scoreText.setColor(c, g, c, scoreText.getColor().a);
                } else {
                    if (NO_DEATH) return;
                    float r = interpolation.apply(HEALTH_BAR_DANGEROUS_ANGLE_COLOR.r, 1, percentage);
                    scoreText.setColor(r, c, c, scoreText.getColor().a);
                }
            }
        };
    }


    /**
     * <a href="https://www.desmos.com/calculator/hmqa2au4es">https://www.desmos.com/calculator/hmqa2au4es</a>
     *
     */
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


    /**
     * <a href="https://www.desmos.com/calculator/6rsoxhdxha">https://www.desmos.com/calculator/6rsoxhdxha</a>
     *
     */
    public static class AffectTimerInterpolation extends Interpolation {

        @Override
        public float apply(float a) {

            float T7 = 6.81589f     * a*a*a*a*a*a*a;
            float T6 = -23.8556f    * a*a*a*a*a*a;
            float T5 = 33.8071f     * a*a*a*a*a;
            float T4 = -24.8787f    * a*a*a*a;
            float T3 = 6.22445f     * a*a*a;
            float T2 = 3.61423f     * a*a;
            float T1 = -0.671501f   * a;
            float T0 = -0.0279216f;

            float const0 = -0.027921646076f;
            float const1 = 1.05584329215f;

            return ((T0 + T1 + T2 + T3 + T4 + T5 + T6 + T7) - const0) / const1;
        }
    }
}
