package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class Score extends SimpleText implements Resizable{

    public static final String TAG = Score.class.getSimpleName();

    private float score;
    private boolean playerScoredBest = false;

    //private SimpleText scoreText;

    private Tween fadeOutTween;

    private Preferences preferences;
    private float currentBest;

    /*private GameplayScreen.ShieldsAndContainersHandler shieldsAndContainersHandler;
    private GameplayScreen.BulletsHandler bulletsHandler;*/

    private GameplayScreen gameplayScreen;


    public Score(GameplayScreen gameplayScreen, MyBitmapFont font) {
        super(font, "0.0");
        this.gameplayScreen = gameplayScreen;
        gameplayScreen.addActor(this);

        setHeight(SCORE_TXT_HEIGHT);

        preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
        loadBestFromHardDrive();
        resetScore();

        initializeFadeOutTween();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

        setX(SCORE_TXT_MARGIN);
        setY(worldHeight - getHeight() - SCORE_TXT_MARGIN);

    }

    @Override
    public void act(float delta) {

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
            score = GameplayScreen.getTimePlayedThisTurnSoFar();
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

    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        setCharSequence("" + roundTo(score, 2), true);
        super.draw(batch, parentAlpha);
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
            setColor(SCORE_BEST_COLOR);
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

    public float getScore() {
        return score;
    }

    public Tween getFadeOutTween() {
        return fadeOutTween;
    }

    private void initializeFadeOutTween() {
        fadeOutTween = new Tween(SCORE_FADE_OUT_TWEEN_DURATION) {
            @Override
            public void tween(float percentage) {
                Color color = getColor();
                setColor(color.r, color.g, color.b, linear.apply(1 - percentage));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setColor(SCORE_COLOR.r, SCORE_COLOR.g, SCORE_COLOR.b, 0.0f);
            }
        };
    }

    //----------------------------------------------------------------------------


}
