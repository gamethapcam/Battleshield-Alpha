package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;


/**
 * <p>Adding an actor here means placing it at the most right position.</p>
 * <p>Removing an actor shifts (all the actor to its right) to the left.</p>
 */
public class RowOfActors extends Group implements Resizable {

    private static final String TAG = RowOfActors.class.getSimpleName();

    private float marginBetweenActors;

    public RowOfActors(float marginBetweenActors) {
        this.marginBetweenActors = marginBetweenActors;
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        for (Actor actor : getChildren()) {
            if (actor instanceof Resizable)
                ((Resizable) actor).resize(width, height, worldWidth, worldHeight);
        }

        calculatePositionsForAllActors();
    }

    private void calculatePositionsForAllActors() {
        Actor previousActor = null;
        for (Actor actor : getChildren()) {

            if (previousActor == null) {
                actor.setPosition(0, 0);
            } else {
                actor.setPosition(previousActor.getX() + previousActor.getWidth() + marginBetweenActors, 0);
            }

            previousActor = actor;
        }
    }

    @Override
    protected void childrenChanged() {
        calculatePositionsForAllActors();
    }

    public float getMarginBetweenActors() {
        return marginBetweenActors;
    }

    public void setMarginBetweenActors(float marginBetweenActors) {
        this.marginBetweenActors = marginBetweenActors;
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

    @Override
    public float getWidth() {
        Actor[] actors = getChildren().items;
        Actor last = actors[actors.length-1];
        return last.getX() + last.getWidth();
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

    /**
     *
     * @return the height of the highest item.
     */
    @Override
    public float getHeight() {
        Actor[] actors = getChildren().items;
        if (actors.length == 0)
            return 0;

        Actor highestActor = null;
        for (Actor actor : actors) {
            if (highestActor == null) {
                highestActor = actor;
                continue;
            }

            if (actor.getHeight() > highestActor.getHeight())
                highestActor = actor;
        }

        return highestActor.getHeight();
    }

    @Deprecated
    @Override
    public void setHeight(float height) {
        Gdx.app.error(TAG, Thread.currentThread().getStackTrace()[1].getMethodName() + " is not supported!!");
    }
}
