package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Layer class is just a screen with a built-in stage.
 */
public abstract class Layer extends Stage implements Screen {
    private JunkAdvancedScreen parentScreen;
    private boolean visible = true;
    private int index;

    public Layer() {
        super();
        //initialize();
    }

    public Layer(Viewport viewport) {
        super(viewport);
        //initialize();
    }

    public Layer(Viewport viewport, Batch batch) {
        super(viewport, batch);
        //initialize();
    }

    /*private void initialize() {

    }*/




    public abstract void buildStage();




    @Override
    public void show() {
        setVisible(true);

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        setVisible(false);
    }

    @Override
    public void dispose() {
        super.dispose();
    }





    void setParentScreen(JunkAdvancedScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public boolean isVisible() {
        return visible;
    }

    private void setVisible(boolean visible) {
        this.visible = visible;

        Array<Layer> parentLayers = parentScreen.getLayers();

        if (visible && index == parentLayers.size-1) Gdx.input.setInputProcessor(this);
        else Gdx.input.setInputProcessor(parentLayers.peek());
    }
}
