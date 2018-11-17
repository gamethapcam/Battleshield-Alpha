package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class AdvancedStage extends Stage implements Resizable{

    public static final String TAG = AdvancedStage.class.getSimpleName();

    private Array<AdvancedScreen> screens;
    private Array<Transition> transitions;
    private Array<Updatable> updatables;

    private AdvancedScreen currentScreen;
    private Transition transition;

    private boolean updateCamera = false;

    public AdvancedStage() {
        super();
        init();
    }

    public AdvancedStage(Viewport viewport) {
        super(viewport);
        init();
    }

    public AdvancedStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
        init();
        //getRoot().setTransform(false);
    }

    private void init() {
        screens = new Array<AdvancedScreen>(AdvancedScreen.class);
        transitions = new Array<Transition>(Transition.class);
        updatables = new Array<Updatable>(Updatable.class);
    }

    public void pause () {
        if (currentScreen != null) currentScreen.pause();
    }

    public void resume () {
        if (currentScreen != null) currentScreen.resume();
    }

    @Override
    public void dispose () {
        if (currentScreen != null) currentScreen.dispose();
    }

    public void resize (int width, int height, float worldWidth, float worldHeight) {
        this.getViewport().update(width, height, true);
        if (transition.isInProgress()) {
            transition.getIn().resize(width, height, worldWidth, worldHeight);
            if (transition.getOut() != null) transition.getOut().resize(width, height, worldWidth, worldHeight);
        } else
            if (currentScreen != null) currentScreen.resize(width, height, worldWidth, worldHeight);
    }


    public void switchScreens (Transition transition) {
        this.transition = transition;

        if (transition.getOut() != null)
            if (!transition.getOut().equals(currentScreen) & currentScreen != null)
                throw new RuntimeException("When switching screens, the currentScreen must be the same as the out screen of the transition object.");

        currentScreen = transition.getIn();
        transition.onSwitch(getViewport().getWorldWidth(), getViewport().getWorldHeight());
        transition.start();
    }

    public void switchScreens(int transitionIndex) {
        this.transition = transitions.get(transitionIndex);
        switchScreens(transition);
    }

    /** @return the currently active {@link Screen}. */
    public AdvancedScreen getCurrentScreen() {
        return currentScreen;
    }


    public Transition getTransition() {
        return transition;
    }

    @Override
    public void act(float delta) {
        for (Updatable updatable : updatables) updatable.update(delta);

        super.act(delta);
        if (transition != null) if (transition.isInProgress()) transition.update(delta);
    }

    public AdvancedScreen[] getAdvancedScreens() {
        return screens.items;
    }

    public Transition[] getTransitions() {
        return transitions.items;
    }

    public Updatable[] getUpdatables() {
        return updatables.items;
    }

    public void addScreen(AdvancedScreen screen) {
        screens.add(screen);
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }

    /*public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }*/

    //-----------------------------------


    /*@Override
    public void draw() {
        super.draw();
    }*/
}
