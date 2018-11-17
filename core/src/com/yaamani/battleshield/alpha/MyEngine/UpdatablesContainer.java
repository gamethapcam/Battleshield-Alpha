package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.utils.Array;

public class UpdatablesContainer {

    private Array<Updatable> updatables;

    public UpdatablesContainer() {
        updatables = new Array<Updatable>(Updatable.class);
    }

    public void act(float delta) {
        for (Updatable updatable : updatables) updatable.update(delta);
    }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }

    public Updatable[] getUpdatables() {
        return updatables.items;
    }

}
