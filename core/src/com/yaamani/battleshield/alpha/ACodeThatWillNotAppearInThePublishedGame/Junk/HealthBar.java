package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.HealthHandler;
import com.yaamani.battleshield.alpha.MyEngine.DebugOrigin;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.resolutionIntoWorldUnits;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthBar extends Actor implements Resizable {

    public static final String TAG = HealthBar.class.getSimpleName();

    private HealthHandler healthHandler;

    private TextureRegion currentRegion;

    private ShapeRenderer shapeRenderer;
    private DebugOrigin debugOrigin;

    public HealthBar(GameplayScreen gameplayScreen, HealthHandler healthHandler) {
        shapeRenderer = new ShapeRenderer();
        debugOrigin = new DebugOrigin(this, shapeRenderer, WORLD_SIZE*0.004f);

        this.healthHandler = healthHandler;

        gameplayScreen.addActor(this);
        drawHealth(healthHandler.getHealth());

//        setDebug(true);
    }

    @Override
    public void act(float delta) {
        //setHealth(health + 0.003f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (healthHandler.getHealth() > 0) batch.draw(currentRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        /*debugOrigin.draw(batch);
        drawXY(batch);*/
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        drawHealth(healthHandler.getHealth());
    }

    public void drawHealth(float correctedHealth) {

        if (correctedHealth > 1) setColor(HEALTH_BAR_HEALTH_OVER_FLOW_COLOR);
        else setColor(Color.WHITE);

        float theta = MathUtils.clamp(MathUtils.ceil(correctedHealth * 360f), 0, 360f);
        if (correctedHealth != 0f) {
            int index = (int) ((theta - HEALTH_BAR_SAVING_FROM_ANGLE) / HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING);
            //currentRegion = Assets.instance.gameplayAssets.healthBarWithVariousAngles[index];

            setSize(resolutionIntoWorldUnits(currentRegion.getRegionWidth(), WORLD_SIZE, 1080),
                    resolutionIntoWorldUnits(currentRegion.getRegionHeight(), WORLD_SIZE, 1080));
        }


        /*Viewport viewport = getStage().getViewport();
        if (theta <= 180f) {
            float L5 = HEALTH_BAR_THICKNESS * MathUtils.sin(MathUtils.degreesToRadians*theta/2f);

            setY(viewport.getWorldHeight()/2f + HEALTH_BAR_RADIUS);
            setX(viewport.getWorldWidth()/2f - L5);

            setOrigin(L5, 0);
            setRotation(-theta/2);
        } else {
            float beta = (180-theta)/2f;
            float L7 = HEALTH_BAR_THICKNESS*MathUtils.sin(MathUtils.degreesToRadians*beta);
            float L8 = HEALTH_BAR_RADIUS+HEALTH_BAR_THICKNESS - HEALTH_BAR_RADIUS*MathUtils.cos(MathUtils.degreesToRadians*beta);
            // TODO : Explain beta, L7 and L8 in the picture.

            setY(viewport.getWorldHeight()/2f + HEALTH_BAR_RADIUS + L7);
            setX(viewport.getWorldWidth()/2f - L8);

            setOrigin(L8, -L7);
            setRotation(-90+beta);
        }*/
    }

    /*public void drawXY(Batch batch) {
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        shapeRenderer.setProjectionMatrix(getStage().getViewport().getCamera().combined);

        shapeRenderer.setColor(1, 0, 1, 0.85f);
        shapeRenderer.circle(getX(), getY(), WORLD_SIZE*0.004f);

        shapeRenderer.end();

        batch.begin();
    }*/
}
