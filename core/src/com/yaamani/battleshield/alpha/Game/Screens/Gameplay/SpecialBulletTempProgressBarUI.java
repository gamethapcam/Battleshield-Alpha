package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyProgressBar;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class SpecialBulletTempProgressBarUI extends Group implements Resizable {

    private static final String TAG = SpecialBulletTempProgressBarUI.class.getSimpleName();

    private Image specialBulletImage;
    private MyProgressBar progressBar;

    private Tween tween;

    public SpecialBulletTempProgressBarUI(TextureRegion specialBulletRegion) {

        initializeSpecialBulletImage(specialBulletRegion);
        initializeProgressBar();

        initializeTween();

        setVisible(false);

        calculateSizeAndPositionForAll();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tween.update(delta);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        calculateSizeAndPositionForAll();
    }

    private void calculateSizeAndPositionForAll() {
        progressBar.setBounds(0, 0, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_PROGRESS_BAR_HEIGHT);

        specialBulletImage.setPosition(0, progressBar.getY() + progressBar.getHeight() + SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_MARGIN_BETWEEN_IMAGE_AND_PROGRESS_BAR);
        specialBulletImage.setSize(SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH);
    }

    private void initializeSpecialBulletImage(TextureRegion specialBulletRegion) {
        specialBulletImage = new Image(specialBulletRegion);
        addActor(specialBulletImage);
    }

    private void initializeProgressBar() {
        TextureRegion reg = Assets.instance.mutualAssets.bigCircle;
        progressBar = new MyProgressBar(reg, 1);
        progressBar.setBgColor(Color.BLACK);
        progressBar.setPercentageBarColor(Color.WHITE);
        progressBar.setBounds(0, 0, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_PROGRESS_BAR_HEIGHT);
        addActor(progressBar);
    }

    private void initializeTween() {
        tween = new Tween() {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                progressBar.setPercentage(interpolation.apply(percentage));
                SpecialBulletTempProgressBarUI.this.onTween(percentage, interpolation);
            }

            @Override
            public void onStart() {
                super.onStart();
                setVisible(true);
                SpecialBulletTempProgressBarUI.this.onTweenStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setVisible(false);
                SpecialBulletTempProgressBarUI.this.onTweenFinish();
            }
        };
    }

    public void displayFor(float millis) {
        onStartingToDisplay();
        tween.setDurationMillis(millis);
        tween.start();
    }

    /**
     * Called when calling {@link SpecialBulletTempProgressBarUI#displayFor(float)}.
     */
    public void onStartingToDisplay() {

    }

    /**
     * Called when the tween of the progress bar starts.
     */
    public void onTweenStart() {

    }

    /**
     * Called inside {@link Tween#tween(float, Interpolation)}.
     * @param percentage
     * @param interpolation
     */
    public void onTween(float percentage, Interpolation interpolation) {

    }

    /**
     * Called when the tween of the progress bar finishes.
     */
    public void onTweenFinish() {

    }

    public Tween getTween() {
        return tween;
    }

    @Override
    public float getWidth() {
        return SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH;
    }

    @Override
    public float getHeight() {
        return specialBulletImage.getY() + specialBulletImage.getHeight();
    }

    @Deprecated
    @Override
    public void setSize(float width, float height) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }

    @Deprecated
    @Override
    public void sizeBy(float size) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }

    @Deprecated
    @Override
    public void sizeBy(float width, float height) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }
    @Deprecated
    @Override
    public void setWidth(float width) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }

    @Deprecated
    @Override
    public void setBounds(float x, float y, float width, float height) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }
    @Deprecated
    @Override
    public void setHeight(float height) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }
}
