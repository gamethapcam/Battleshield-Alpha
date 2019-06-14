package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class NinePatchActor extends Actor {

    private NinePatch ninePatch;

    public NinePatchActor(NinePatch ninePatch) {
        this.ninePatch = ninePatch;
    }

    public NinePatch getNinePatch() {
        return ninePatch;
    }

    public void setNinePatch(NinePatch ninePatch) {
        this.ninePatch = ninePatch;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        ninePatch.draw(batch,
                getX(),
                getY(),
                getOriginX(),
                getOriginY(),
                getWidth(),
                getHeight(),
                getScaleX(),
                getScaleY(),
                getRotation());
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        ninePatch.setColor(color);
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        ninePatch.setColor(getColor());
    }

    @Override
    public Color getColor() {
        return ninePatch.getColor();
    }
}
