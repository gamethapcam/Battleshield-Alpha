package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;


public class JunkAdvancedScreen implements Screen {
    private Array<Layer> layers;

    @Override
    public void show() {
        for (Layer layer : layers) if (layer.isVisible()) layer.show();
    }

    @Override
    public void render(float delta) {
        for (Layer layer : layers) if (layer.isVisible()) layer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        for (Layer layer : layers) if (layer.isVisible()) layer.resize(width, height);
    }

    @Override
    public void pause() {
        for (Layer layer : layers) if (layer.isVisible()) layer.pause();
    }

    @Override
    public void resume() {
        for (Layer layer : layers) if (layer.isVisible()) layer.resume();
    }

    @Override
    public void hide() {
        for (Layer layer : layers) if (layer.isVisible()) layer.hide();
    }

    @Override
    public void dispose() {
        for (Layer layer : layers) layer.dispose();
    }

    public Array<Layer> getLayers() {
        return layers;
    }

    /**
     * Add the specified layer and show it above all previous layers.
     * All input interactions will now affect the specified layer only, as it's now the top one.
     * @return The index of the added layer.
     */
    public int addLayer(Layer layer) {
        layers.add(layer);
        layer.setIndex(layers.size-1);
        layer.setParentScreen(this);
        if (layer.isVisible()) {
            layer.show();
            layer.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        return layers.size-1;
    }

    /**
     * Removes and returns the layer of the specified index.
     */
    public Layer removeLayer(int index) {
        Layer removedLayer = layers.removeIndex(index);
        removedLayer.hide();
        removedLayer.dispose();
        return removedLayer;
    }

    /**
     * Removes and returns the specified layer.
     */

    public Layer removeLayer(Layer layer) {
        return removeLayer(layer.getIndex());
    }
}
