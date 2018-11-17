package com.yaamani.battleshield.alpha.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class SolidBG {
    private float r;
    private float g;
    private float b;

    public static SolidBG instance = new SolidBG();

    private SolidBG() {
        setColor(0, 0, 0);
    }

    void draw() {
        Gdx.gl.glClearColor(r, g, b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
