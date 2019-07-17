package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AdvancedApplicationAdapter implements ApplicationListener {

    protected AdvancedStage game;

    //private int rotation;

    //private Timer resizeAfterOrientationChange;

    private boolean orientationChanges;
    private int fixedWidth;
    private int currentWidth, currentHeight;

    private Input.Orientation orientation;

    /**
     *
     * @param viewport
     * @param batch
     * @param orientationChanges False if the game is always going to be in landscape mode or portrait mode only (AndroidManifest.xml -> android:screenOrientation = Landscape or Portrait or sensorLandscape or sensorPortrait ....). And true if change from portrait to landscape is allowed (AndroidManifest.xml -> android:screenOrientation = fullSensor, Sensor .....)
     */
    public void initializeStage(Viewport viewport, SpriteBatch batch, boolean orientationChanges) {
        game = new AdvancedStage(viewport, batch);
        Gdx.input.setInputProcessor(game);

        this.orientationChanges = orientationChanges;
        if (Gdx.app.getType() == Application.ApplicationType.Android) fixedWidth = Gdx.graphics.getWidth();
        currentWidth = Gdx.graphics.getWidth();
        currentHeight = Gdx.graphics.getHeight();

    }

    @Override
    public void resize(int width, int height) {
        if (Gdx.app.getType() == Application.ApplicationType.Android &
                !orientationChanges &
                width != this.fixedWidth) return;

        game.resize(width, height, game.getViewport().getWorldWidth(), game.getViewport().getWorldHeight());
    }

    @Override
    public void render() {
        if (Gdx.app.getType() == Application.ApplicationType.Android &
                !orientationChanges &
                Gdx.graphics.getWidth() != fixedWidth) { // The game looks stretched out in some cases like exactly after unlocking the phone. So, draw a black color instead.

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            return;
        }

        basicDrawing();

        if (orientationChanges) orientationChangeDetection();

    }

    private void basicDrawing() {
        game.act(Gdx.graphics.getDeltaTime());
        game.draw();
    }

    @Override
    public void pause() {
        game.pause();

    }

    @Override
    public void resume() {
        game.resume();
    }

    @Override
    public void dispose() {
        game.dispose();
    }

    private void orientationChangeDetection() {

        int rotation = Gdx.input.getRotation();
        Input.Orientation currentOrientation;
        if((Gdx.input.getNativeOrientation() == Input.Orientation.Portrait && (rotation == 90 || rotation == 270)) || //First case, the normal phone
                (Gdx.input.getNativeOrientation() == Input.Orientation.Landscape && (rotation == 0 || rotation == 180))) { //Second case, the landscape device
            currentOrientation = Input.Orientation.Landscape;
        } else
            currentOrientation = Input.Orientation.Portrait;

        if (orientation != null) {
            if (orientation != currentOrientation) {
                //swap
                int temp = currentWidth;
                currentWidth = currentHeight;
                currentHeight = temp;

                resize(currentWidth, currentHeight);
            }
        }
        orientation = currentOrientation;
    }
}

