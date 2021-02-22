package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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

public class ScoreMultiplierDifficultyLevelStuff implements Resizable, Updatable {

    private GameplayScreen gameplayScreen;

    private SimpleText scoreMultiplierText;

    private MyProgressBar myProgressBar;
    private Tween myProgressBarTween;

    //private Tween scoreMultiplier;

    private float scoreMultiplierDifficultyLevel = 1;

    private Tween survival_scoreMultiplierTween; // The higher the difficulty the higher the score multiplier.
    private Tween crystal_difficultyLevelTween;
    private Tween dizziness_difficultyLevelTween;
    private Tween lazer_difficultyLevelTween;
    private Tween portals_difficultyLevelTween;
    private Tween t1_difficultyLevelTween;
    private Tween bigBoss_difficultyLevelTween;


    public ScoreMultiplierDifficultyLevelStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeScoreMultiplierText(gameplayScreen.getMyBitmapFont());
        initializeProgressBar();
        initializeMyProgressBarTween();
        initializeSurvival_scoreMultiplierTween();
        initializeCrystal_difficultyLevelTween();
        initializeDizziness_difficultyLevelTween();
        initializeLazer_difficultyLevelTween();
        initializePortals_difficultyLevelTween();
        initializeT1_difficultyLevelTween();
        initializeBigBoss_difficultyLevelTween();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        bulletSpeedMultiplierTextUpdatePosition();
        progressBarUpdatePosition();
    }

    @Override
    public void update(float delta) {
         if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {

            /*if (myProgressBarTween.isFinished()) {
                if (scoreMultiplier < SCORE_MULTIPLIER_MAX)
                    myProgressBar.setPercentage(gameplayScreen.getBulletsHandler().getCurrentDifficultyLevelTimer().getPercentage());
                else myProgressBar.setPercentage(1);
            }*/

            myProgressBarTween.update(delta);
            switch (gameplayScreen.getGameplayMode()) {
                case SURVIVAL:
                    survival_scoreMultiplierTween.update(delta);
                    break;
                case CRYSTAL:
                    crystal_difficultyLevelTween.update(delta);
                    break;
                case DIZZINESS:
                    dizziness_difficultyLevelTween.update(delta);
                    break;
                case LAZER:
                    lazer_difficultyLevelTween.update(delta);
                    break;
                case PORTALS:
                    portals_difficultyLevelTween.update(delta);
                    break;
                case T1:
                    t1_difficultyLevelTween.update(delta);
                    break;
                case BIG_BOSS:
                    bigBoss_difficultyLevelTween.update(delta);
                    break;
            }
        }
    }

    private void updateCharSequence(String prefix, float currentMultiplier) {
        scoreMultiplierText.setCharSequence(prefix + "x" + currentMultiplier, true);

        bulletSpeedMultiplierTextUpdatePosition();
        progressBarUpdatePosition();
    }

    private void bulletSpeedMultiplierTextUpdatePosition() {
        float bulletSpeedMultiplierTextWidth2 = scoreMultiplierText.getWidth()/2f;
        float bulletSpeedMultiplierTextHeight2 = scoreMultiplierText.getHeight()/2f;
        Viewport viewport = gameplayScreen.getStage().getViewport();
        /*scoreMultiplierText.setPosition(viewport.getWorldWidth()/2f - bulletSpeedMultiplierTextWidth2,
                viewport.getWorldHeight()/2f - bulletSpeedMultiplierTextHeight2 / 1.7f);*/
        scoreMultiplierText.setPosition(SCORE_TXT_MARGIN, gameplayScreen.getScoreTimerStuff().getScoreText().getY() - SCORE_TXT_MARGIN - bulletSpeedMultiplierTextHeight2);
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

    public float getScoreMultiplierDifficultyLevel() {
        return scoreMultiplierDifficultyLevel;
    }

    public Tween getSurvival_scoreMultiplierTween() {
        return survival_scoreMultiplierTween;
    }

    public Tween getCrystal_difficultyLevelTween() {
        return crystal_difficultyLevelTween;
    }

    public Tween getDizziness_difficultyLevelTween() {
        return dizziness_difficultyLevelTween;
    }

    public Tween getLazer_difficultyLevelTween() {
        return lazer_difficultyLevelTween;
    }

    public Tween getPortals_difficultyLevelTween() {
        return portals_difficultyLevelTween;
    }

    public Tween getT1_difficultyLevelTween() {
        return t1_difficultyLevelTween;
    }

    public Tween getBigBoss_difficultyLevelTween() {
        return bigBoss_difficultyLevelTween;
    }

    public void setVisible(boolean visible) {
        scoreMultiplierText.setVisible(visible);
        myProgressBar.setVisible(visible);
    }

    /*public void gameplayModeStuff(GameplayMode gameplayMode) {
        switch (gameplayMode) {
            case SURVIVAL:
                survival();
                break;
            case CRYSTAL:
                crystal();
                break;
        }

        myProgressBarTween.start();
    }*/

    public void startMyProgressBarTween() {
        myProgressBarTween.start();
    }

    public void survival() {
        updateCharSequence("", 1.0f);

        myProgressBarTween.setDurationMillis(D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL * 1000 * D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS);
        myProgressBarTween.setInterpolation(SURVIVAL_SCORE_MULTIPLIER_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void crystal() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(CRYSTAL_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_CRYSTAL_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void dizziness() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(DIZZINESS_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_DIZZINESS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void lazer() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(LAZER_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_LAZER_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void portals() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(PORTALS_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_PORTALS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void t1() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(T1_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_T1_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

    }

    public void bigBoss() {
        updateCharSequence(DIFFICULTY_PREFIX, 1);

        myProgressBarTween.setDurationMillis(BIG_BOSS_LEVEL_TIME*60*1000);
        myProgressBarTween.setInterpolation(D_BIG_BOSS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION);

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

        myProgressBarTween = new Tween() {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                myProgressBar.setPercentage(interpolation.apply(percentage));
                //Gdx.app.log(TAG, "" + myProgressBar.getPercentage());
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        // myProgressBarTween.finish();
        // myProgressBarTween.start();

        gameplayScreen.addToFinishWhenStoppingTheGameplay(myProgressBarTween);
    }

    private void initializeSurvival_scoreMultiplierTween() {
        survival_scoreMultiplierTween = new Tween(D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL * 1000 * D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS, SURVIVAL_SCORE_MULTIPLIER_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float newScoreMultiplier = interpolation.apply(SCORE_MULTIPLIER_MIN, SCORE_MULTIPLIER_MAX, percentage);
                // if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    if (newScoreMultiplier != scoreMultiplierDifficultyLevel) {
                        scoreMultiplierDifficultyLevel = newScoreMultiplier;
                        updateCharSequence("", newScoreMultiplier);
                    }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        // survival_scoreMultiplierTween.start();

        gameplayScreen.addToFinishWhenStoppingTheGameplay(survival_scoreMultiplierTween);

    }

    private void initializeCrystal_difficultyLevelTween() {
        crystal_difficultyLevelTween = new Tween(CRYSTAL_LEVEL_TIME*60*1000, D_CRYSTAL_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(crystal_difficultyLevelTween);
    }

    private void initializeDizziness_difficultyLevelTween() {
        dizziness_difficultyLevelTween = new Tween(DIZZINESS_LEVEL_TIME*60*1000, D_DIZZINESS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(dizziness_difficultyLevelTween);
    }

    private void initializeLazer_difficultyLevelTween() {
        lazer_difficultyLevelTween = new Tween(LAZER_LEVEL_TIME*60*1000, D_LAZER_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(lazer_difficultyLevelTween);
    }

    private void initializePortals_difficultyLevelTween() {
        portals_difficultyLevelTween = new Tween(PORTALS_LEVEL_TIME*60*1000, D_PORTALS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(portals_difficultyLevelTween);
    }

    private void initializeT1_difficultyLevelTween() {
        t1_difficultyLevelTween = new Tween(T1_LEVEL_TIME*60*1000, D_T1_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_T1_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(t1_difficultyLevelTween);
    }

    private void initializeBigBoss_difficultyLevelTween() {
        bigBoss_difficultyLevelTween = new Tween(BIG_BOSS_LEVEL_TIME*60*1000, D_BIG_BOSS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                int newDifficultyLevel = MathUtils.round(interpolation.apply(1, D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS, percentage));
                if (newDifficultyLevel != scoreMultiplierDifficultyLevel) {
                    scoreMultiplierDifficultyLevel = newDifficultyLevel;
                    updateCharSequence(DIFFICULTY_PREFIX, newDifficultyLevel);
                }
            }

            @Override
            public void onFinish() {
                // Don't call super.
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(bigBoss_difficultyLevelTween);

    }


    /**
     * <a href="https://www.desmos.com/calculator/sid9ootsqs">https://www.desmos.com/calculator/sid9ootsqs</a>
     *
     */
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
