package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.scenes.scene2d.Group;


public abstract class AdvancedScreen extends Group implements Resizable {

    private AdvancedStage advancedStage;

    public AdvancedScreen(AdvancedStage advancedStage, boolean transform) {
        
        this.advancedStage = advancedStage;
        
        advancedStage.addActor(this);
        advancedStage.addScreen(this);
        this.setVisible(false);
        setTransform(transform);// for performance.  ... if I'm not scaling or rotating then there's no need for transform to be true.
    }


    public void show() {

    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {

    }


    public void pause() {

    }


    public void resume() {

    }


    public void hide() {
        this.setVisible(false);
    }


    public void dispose() {

    }

    public AdvancedStage getAdvancedStage() {
        return advancedStage;
    }

    /*public final void render(float delta) {

    }*/
}
