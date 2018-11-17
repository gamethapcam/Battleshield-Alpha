package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.OneBigSizeBitmapFontTextField;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameOverLayer extends Actor implements Resizable{

    private TextureRegion gameOverBG;

    private OneBigSizeBitmapFontTextField scoreText;
    private OneBigSizeBitmapFontTextField bestScoreText;
    private OneBigSizeBitmapFontTextField tapAnyWhereToPlayAgainText;

    private Timer tapAnyWhereToPlayAgainTextShowingTimer;

    private GameplayScreen gameplayScreen;

    public GameOverLayer(GameplayScreen gameplayScreen, BitmapFont font) {
        gameplayScreen.addActor(this);
        setVisible(false);

        this.gameplayScreen = gameplayScreen;

        gameOverBG = Assets.instance.gameplayAssets.gameOverBG;

        setSize(GAME_OVER_BG_R, GAME_OVER_BG_R);

        initializeScoreText(1.5f, FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED, font);
        initializeNewBestText(0.7f, FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED, font);
        initializeTapAnyWhereToPlayAgainText(0.6f, FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED, font);
        // TODO: Put the previous 3 scale values in constants class.

        tapAnyWhereToPlayAgainTextShowingTimer = new Timer(SCORE_FADE_OUT_TWEEN_DURATION*2);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        setX(worldWidth/2f - getWidth()/2f);
        setY(worldHeight/2f - getHeight()/2f);
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

        scoreText.draw(batch, getX() + getWidth()/2f - scoreText.getGlyphLayout().width/2f, getHeight()/2f + scoreText.getGlyphLayout().height/2f);
        // TODO: [FIX A BUG] scoreText's 'x' and 'y' coordinates aren't calculated correctly for a few moments, then they're corrected. Perhaps the problem in glyphLayout.width & glyphLayout.height

        bestScoreText.draw(batch, getX() + getWidth()/2f - bestScoreText.getGlyphLayout().width/2f, WORLD_SIZE/2f - scoreText.getGlyphLayout().height/2f);

        if (tapAnyWhereToPlayAgainTextShowingTimer.isFinished())
            tapAnyWhereToPlayAgainText.draw(batch, getX() + getWidth()/2f - tapAnyWhereToPlayAgainText.getGlyphLayout().width/2f, getHeight()*(1f/5f));
    }

    void thePlayerLost() {
        setVisible(true);

        scoreText.setCharSequence(gameplayScreen.getScore().getScoreText().getCharSequence());

        if (gameplayScreen.getScore().isPlayerScoredBest()) bestScoreText.setCharSequence("NEW BEST");
        else bestScoreText.setCharSequence("Best : " + MyMath.roundTo(gameplayScreen.getScore().getCurrentBest(), 2));

        tapAnyWhereToPlayAgainTextShowingTimer.start();
    }

    void disappearToStartANewGame() {
        setVisible(false);
    }


    //---------------------------------------- Initializers ----------------------------------------------
    //---------------------------------------- Initializers ----------------------------------------------
    //---------------------------------------- Initializers ----------------------------------------------

    private void initializeScoreText(float scale, float theResolutionAtWhichTheScaleWasDecided, BitmapFont font) {
        scoreText = new OneBigSizeBitmapFontTextField(font,
                "",
                Color.BLACK,
                0,
                Align.left,
                false,
                null,
                scale,
                theResolutionAtWhichTheScaleWasDecided);
    }

    private void initializeNewBestText(float scale, float theResolutionAtWhichTheScaleWasDecided, BitmapFont font) {
        bestScoreText = new OneBigSizeBitmapFontTextField(font,
                "",
                Color.LIME,
                0,
                Align.left,
                false,
                null,
                scale,
                theResolutionAtWhichTheScaleWasDecided);
    }

    private void initializeTapAnyWhereToPlayAgainText(float scale, float theResolutionAtWhichTheScaleWasDecided, BitmapFont font) {
        tapAnyWhereToPlayAgainText = new OneBigSizeBitmapFontTextField(font,
                "TAP ANY WHERE TO START AGAIN",
                Color.LIGHT_GRAY,
                getWidth()/1.7f,
                Align.center,
                true,
                null,
                scale,
                theResolutionAtWhichTheScaleWasDecided);
    }








    //----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------

}