package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class DebugOrigin {

    private Actor actor;
    private ShapeRenderer shapeRenderer;
    private float radius;

    public DebugOrigin(Actor actor, ShapeRenderer shapeRenderer, float radius) {
        this.actor = actor;
        this.shapeRenderer = shapeRenderer;
        this.radius = radius;
    }

    public void draw(Batch batch) {
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        shapeRenderer.setProjectionMatrix(actor.getStage().getViewport().getCamera().combined);

        shapeRenderer.setColor(1, 0, 0, 0.85f);
        shapeRenderer.circle(actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY(), radius);
        //shapeRenderer.point(actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY(), 0);

        shapeRenderer.end();

        batch.begin();
    }
}
