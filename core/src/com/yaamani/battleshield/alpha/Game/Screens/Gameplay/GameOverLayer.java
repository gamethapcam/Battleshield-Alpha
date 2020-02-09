package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameOverLayer extends Group implements Resizable {

    private TextureRegion gameOverBG;

    private SimpleText scoreText;
    private SimpleText bestScoreText;
    private SimpleText tapAnyWhereToText;
    private SimpleText startAgainText;

    private Timer tapAnyWhereToPlayAgainTextShowingTimer;

    private GameplayScreen gameplayScreen;

    public GameOverLayer(GameplayScreen gameplayScreen, MyBitmapFont font) {
        gameplayScreen.addActor(this);
        setVisible(false);

        this.gameplayScreen = gameplayScreen;

        gameOverBG = Assets.instance.gameplayAssets.gameOverBG;

        setSize(GAME_OVER_BG_R, GAME_OVER_BG_R);

        initializeScoreText(font);
        initializeNewBestText(font);
        initializeTapAnyWhereToPlayAgainText(font);

        initializeTapAnyWhereToPlayAgainTextShowingTimer();

        //setDebug(true);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        setX(worldWidth / 2f - getWidth() / 2f);
        setY(worldHeight / 2f - getHeight() / 2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        tapAnyWhereToPlayAgainTextShowingTimer.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) | (Gdx.input.isTouched() & tapAnyWhereToPlayAgainTextShowingTimer.isFinished())) {
            gameplayScreen.getHealthHandler().newGame();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(gameOverBG, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        super.draw(batch, parentAlpha);
    }

    public void thePlayerLost() {
        setVisible(true);

        scoreText.setCharSequence(gameplayScreen.getScoreStuff().getScoreText().getCharSequence(), true);

        if (gameplayScreen.getScoreStuff().isPlayerScoredBest())
            bestScoreText.setCharSequence("NEW BEST", true);
        else
            bestScoreText.setCharSequence("Best : " + MyMath.roundTo(gameplayScreen.getScoreStuff().getCurrentBest(), 2), true);
        updatePositions();

        tapAnyWhereToPlayAgainTextShowingTimer.start();
    }

    public void disappearToStartANewGame() {
        setVisible(false);
        tapAnyWhereToText.setVisible(false);
        startAgainText.setVisible(false);
    }

    private void updatePositions() {
        float halfWidth = getWidth() / 2f, halfHeight = getHeight() / 2f;

        float spaceBetween_bestScoreText_and_scoreText = WORLD_SIZE / 80f;
        float h = bestScoreText.getHeight() + scoreText.getHeight() + spaceBetween_bestScoreText_and_scoreText;
        bestScoreText.setPosition(halfWidth - bestScoreText.getWidth() / 2f, halfHeight - h / 2f);
        scoreText.setPosition(halfWidth - scoreText.getWidth() / 2f, bestScoreText.getY() + bestScoreText.getHeight() + spaceBetween_bestScoreText_and_scoreText);

        startAgainText.setPosition(halfWidth - startAgainText.getWidth() / 2f, getHeight() * 0.1f);
        tapAnyWhereToText.setPosition(halfWidth - tapAnyWhereToText.getWidth() / 2f, startAgainText.getY() + startAgainText.getHeight() + GAMEOVER_LAYER_TAP_ANY_WHERE_TO_START_AGAIN_TXT_LINE_SPACING);
    }

    //---------------------------------------- Initializers ----------------------------------------------
    //---------------------------------------- Initializers ----------------------------------------------
    //---------------------------------------- Initializers ----------------------------------------------

    private void initializeScoreText(MyBitmapFont myBitmapFont) {
        scoreText = new SimpleText(myBitmapFont, "");
        scoreText.setHeight(GAMEOVER_LAYER_SCORE_TXT_HEIGHT);
        scoreText.setColor(Color.BLACK);
        addActor(scoreText);
    }

    private void initializeNewBestText(MyBitmapFont myBitmapFont) {
        bestScoreText = new SimpleText(myBitmapFont, "");
        bestScoreText.setHeight(GAMEOVER_LAYER_NEW_BEST_TXT_HEIGHT);
        bestScoreText.setColor(Color.LIME);
        addActor(bestScoreText);
    }

    private void initializeTapAnyWhereToPlayAgainText(MyBitmapFont myBitmapFont) {
        tapAnyWhereToText = new SimpleText(myBitmapFont, "TAP ANY WHERE TO");
        tapAnyWhereToText.setHeight(GAMEOVER_LAYER_TAP_ANY_WHERE_TO_TXT_HEIGHT);
        tapAnyWhereToText.setColor(Color.LIGHT_GRAY);
        addActor(tapAnyWhereToText);

        startAgainText = new SimpleText(myBitmapFont, "START AGAIN");
        startAgainText.setHeight(GAMEOVER_LAYER_START_AGAIN_TXT_HEIGHT);
        startAgainText.setColor(Color.LIGHT_GRAY);
        addActor(startAgainText);

        tapAnyWhereToText.setVisible(false);
        startAgainText.setVisible(false);
    }

    private void initializeTapAnyWhereToPlayAgainTextShowingTimer() {
        tapAnyWhereToPlayAgainTextShowingTimer = new Timer(SCORE_FADE_OUT_TWEEN_DURATION * 2) {
            @Override
            public void onFinish() {
                tapAnyWhereToText.setVisible(true);
                startAgainText.setVisible(true);
            }
        };
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
}