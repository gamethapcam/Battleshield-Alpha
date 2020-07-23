package com.yaamani.battleshield.alpha.MyEngine;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_HEIGHT;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_HEIGHT;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_TOP_MARGIN;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_WIDTH;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.SCORE_TXT_MARGIN;

/**
 * <p>A very fast way to display a temp progress bar for a certain amount of time.</p>
 * <p>You can call {@link TempProgressBar#display(String, float)} to display the progress bar with a text on top of it.
 * Or you can call {@link TempProgressBar#display(TextureRegion, float)} to display the progress bar with an image on top of it.</p>
 * <p>You should call {@link #setProgressBarHeight(float, float)} immediately after initialization & {@link #setBounds(float, float, float, float)}.</p>
 */
public class TempProgressBar extends Group implements Resizable {

    private static final String TAG = TempProgressBar.class.getSimpleName();


    private GameplayScreen gameplayScreen;


    public enum Alignment {LEFT, CENTER, RIGHT}
    private Alignment textOrImageAlignment;
    private Image image;

    private SimpleText text;
    private MyProgressBar progressBar;
    private float progressBarTopMargin;

    private Actor tempActor; // text or image

    private TextureRegion tempTextureRegion;
    private String tempCharSequence;
    private Tween tween;

    private boolean centre = true; // if false -> bottom left

    //private boolean reverse;

    public TempProgressBar(GameplayScreen gameplayScreen, MyBitmapFont myBitmapFont, Alignment textOrImageAlignment) {
        setVisible(false);

        this.gameplayScreen = gameplayScreen;

        this.textOrImageAlignment = textOrImageAlignment;


        image = new Image(new TextureRegionDrawable());
        text = new SimpleText(myBitmapFont, " ");
        TextureRegion reg = Assets.instance.mutualAssets.bigCircle;
        progressBar = new MyProgressBar(reg, 0);
        progressBar.setBgColor(Color.BLACK);
        progressBar.setPercentageBarColor(Color.WHITE);

        addActor(progressBar);
        progressBar.setY(0);

        initializeTween();
    }

    public TempProgressBar(GameplayScreen gameplayScreen, MyBitmapFont myBitmapFont) {
        this(gameplayScreen, myBitmapFont, Alignment.CENTER);
    }

    @Override
    public void act(float delta) {
        tween.update(delta);
        super.act(delta);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        setWidth(GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_WIDTH);
        setHeight(GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_HEIGHT);
        if (centre) {
            setX(worldWidth / 2 - getWidth() / 2);
            setY(worldHeight / 2 - getHeight() / 2);
        } else {
            setX(SCORE_TXT_MARGIN);
            setY(SCORE_TXT_MARGIN);
        }
        setProgressBarHeight(GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_HEIGHT, GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_TOP_MARGIN);
    }

    private void initializeTween() {
        tween = new Tween() {
            // boolean changed = false;
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                // Gdx.app.log(TempProgressBar.TAG, "" + interpolation.apply(percentage));
                progressBar.setPercentage(interpolation.apply(percentage));

                /*if (percentage >= 0.5f & !changed) {
                    setBounds(getX(), getY(), getWidth() * 5, getHeight());
                    changed = true;
                }*/
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setVisible(false);
                removeActor(tempActor);
                tempActor = null;
                tempTextureRegion = null;
                tempCharSequence = null;
            }
        };
    }

    public void display(TextureRegion region, float millis) {
        setVisible(true);
        tempActor = image;
        addActor(image);


        tempTextureRegion = region;
        ((TextureRegionDrawable) (image.getDrawable())).setRegion(region);
        calculateImageBounds(region);

        startTween(millis);

    }

    public void display(String charSequence, float millis) {
        setVisible(true);
        tempActor = text;
        addActor(text);


        tempCharSequence = charSequence;
        calculateTextBounds(charSequence);

        startTween(millis);
    }

    private void startTween(float millis) {
        tween.setDurationMillis(millis);
        tween.start();
    }

    private void calculateImageBounds(TextureRegion region) {
        float regionAspectRatio = (float) region.getRegionWidth() / region.getRegionHeight();
        float overallHeight = getHeight();
        float overallWidth = getWidth();
        float imageHeight = overallHeight - progressBar.getHeight() - progressBarTopMargin;
        float imageWidth = regionAspectRatio*imageHeight;
        if (imageWidth > overallWidth) imageWidth = overallWidth;
        image.setHeight(imageHeight);
        image.setWidth(imageWidth);

        switch (textOrImageAlignment) {
            case LEFT:
                image.setX(0);
                break;
            case RIGHT:
                image.setX(overallWidth-imageWidth);
                break;
            case CENTER:
                image.setX(overallWidth/2-imageWidth/2);
                break;
        }
    }

    private void calculateTextBounds(String charSequence) {
        text.lockAspectRatio();
        float overallHeight = getHeight();
        float overallWidth = getWidth();
        float textHeight = overallHeight - progressBar.getHeight() - progressBarTopMargin;
        text.setCharSequence(charSequence, true);
        text.setHeight(textHeight);
        if (text.getWidth() > overallWidth)
            text.unlockAspectRatio(overallWidth, overallHeight);

        switch (textOrImageAlignment) {
            case LEFT:
                text.setX(0);
                break;
            case RIGHT:
                text.setX(overallWidth-text.getWidth());
                break;
            case CENTER:
                text.setX(overallWidth/2-text.getWidth()/2);
                break;
        }
    }

    /**
     * @throws ValueOutOfRangeException When the height passed is greater than the overall height.
     * @param progressBarHeight
     * @param progressBarTopMargin It's basically the space between the progress bar and the image or the text.
     */
    public void setProgressBarHeight(float progressBarHeight, float progressBarTopMargin) {
        if (progressBarHeight > getHeight())
            throw new ValueOutOfRangeException("The height of the progress bar can't be greater than the overall height.");

        progressBar.setHeight(progressBarHeight);
        image.setY(progressBarHeight + progressBarTopMargin);
        text.setY(progressBarHeight + progressBarTopMargin);
    }

    /**
     * Calls {@link MyProgressBar#setBgColor(Color)}.
     * @param color
     */
    public void setProgressBarBgColor(Color color) {
        progressBar.setBgColor(color);
    }

    /**
     * Calls {@link MyProgressBar#setPercentageBarColor(Color)}
     * @param color
     */
    public void setProgressBarPercentageBarColor(Color color) {
        progressBar.setPercentageBarColor(color);
    }

    /**
     * Calls {@link MyProgressBar#setPercentageBarHeightRatio(float)}.
     * @param percentageBarHeightRatio
     */
    public void setProgressBarPercentageBarHeightRatio(float percentageBarHeightRatio) {
        progressBar.setPercentageBarHeightRatio(percentageBarHeightRatio);
    }

    public void setTextColor(Color color) {
        text.setColor(color);
    }

    private void updateImageOrTextBounds() {
        if (tempTextureRegion != null)
            calculateImageBounds(tempTextureRegion);
        if (tempCharSequence != null)
            calculateTextBounds(tempCharSequence);
    }

    public void positionCentre() {
        centre = true;

        float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
        float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
        setX(worldWidth / 2 - getWidth() / 2);
        setY(worldHeight / 2 - getHeight() / 2);
    }

    public void positionBottomLeft() {
        centre = false;

        setX(SCORE_TXT_MARGIN);
        setY(SCORE_TXT_MARGIN);
    }

    /**
     * The width of the progress bar is always equal to the overall width.
     * @param width
     */
    @Override
    public void setWidth(float width) {
        super.setWidth(width);

        progressBar.setWidth(width);

        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);

        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);

        progressBar.setWidth(width);

        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);

        progressBar.setWidth(width);


        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

    @Override
    public void sizeBy(float size) {
        super.sizeBy(size);

        progressBar.setWidth(getWidth());

        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

    @Override
    public void sizeBy(float width, float height) {
        super.sizeBy(width, height);

        progressBar.setWidth(getWidth());

        if (tween.isStarted()) {
            updateImageOrTextBounds();
        }
    }

}
