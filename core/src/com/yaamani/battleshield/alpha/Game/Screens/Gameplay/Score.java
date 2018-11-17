package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.yaamani.battleshield.alpha.MyEngine.OneBigSizeBitmapFontTextField;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class Score extends Actor implements Resizable{

    public static final String TAG = Score.class.getSimpleName();

    private float score;
    private boolean playerScoredBest = false;

    private OneBigSizeBitmapFontTextField scoreText;

    private Tween fadeOutTween;

    private Preferences preferences;
    private float currentBest;

    /*private GameplayScreen.ShieldsAndContainersHandler shieldsAndContainersHandler;
    private GameplayScreen.BulletsHandler bulletsHandler;*/

    private GameplayScreen gameplayScreen;


    public Score(GameplayScreen gameplayScreen, BitmapFont font) {
        this.gameplayScreen = gameplayScreen;
        gameplayScreen.addActor(this);

        initializeScoreText(font);

        preferences = Gdx.app.getPreferences(SCORE_PREFERENCES_NAME);
        loadBestFromHardDrive();
        resetScore();

        initializeFadeOutTween();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

        setX(worldWidth / 40f);
        setY(worldHeight - getX());
        //setX(worldWidth/2f);
        //setY(worldHeight/2f);
    }

    @Override
    public void act(float delta) {

        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING) {
            score += delta;
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
        scoreText.setCharSequence("" + roundTo(score, 2));
        scoreText.draw(batch, getX(), getY());
        /*font.draw(batch, "+ (Total Shields No.) / Sec.", _x, _y+10);
        font.draw(batch, "+ (Bullets per wave)", _x, _y+13);*/
    }

    void updateBestScoreButDontRegisterToHardDriveYet() {
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

    void resetScore() {
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

    public OneBigSizeBitmapFontTextField getScoreText() {
        return scoreText;
    }

    private void initializeFadeOutTween() {
        fadeOutTween = new Tween(SCORE_FADE_OUT_TWEEN_DURATION) {
            @Override
            public void tween(float percentage) {
                scoreText.setColor(scoreText.getColor().r,
                        scoreText.getColor().g,
                        scoreText.getColor().b,
                        linear.apply(1 - percentage));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                scoreText.setColor(SCORE_COLOR.r,
                        SCORE_COLOR.g,
                        SCORE_COLOR.b,
                        0.0f);
            }
        };
    }

    //----------------------------------------------------------------------------

    private void initializeScoreText(BitmapFont font) {
        scoreText = new OneBigSizeBitmapFontTextField(font,
                "0.0",
                SCORE_COLOR,
                0,
                Align.left,
                false,
                null,
                SCORE_FONT_SCALE,
                FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED);
    }
}
