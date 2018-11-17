package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;


public class Star {

    private final String TAG = getClass().getSimpleName();

    private int i;

    private TextureRegion region;
    private Viewport viewport;

    private Vector2 initialPosition;
    private Vector2 linearPosition;
    private Vector2 linearVelocity;

    private Vector2 finalPosition;

    private float radius;

    private float alpha;

    private float wWidth;
    private float wHeight;

    private float velocityRandom;

    private Texture texture;
    private int regionX;
    private int regionY;
    private int regionWidth;
    private int regionHeight;

    public Star(TextureRegion region, Viewport viewport, int i) {
        this.region = region;
        this.viewport = viewport;
        this.i = i;

        this.texture = region.getTexture();
        this.regionX = region.getRegionX();
        this.regionY = region.getRegionY();
        this.regionWidth = region.getRegionWidth();
        this.regionHeight = region.getRegionHeight();

        finalPosition = new Vector2();

        wWidth = viewport.getWorldWidth();
        wHeight = viewport.getWorldHeight();

        generateRandomness(viewport);
    }

    public void act(float delta, Vector2 additionalVelocity, StarsContainer.RadialTween radialTween, float thetaForRadialTween, int bulletsPerAttack) { // additionalVelocity is a parameter by the starsContainer to achieve the cool effect of the intro
        linearPosition.mulAdd(linearVelocity, delta * (BULLETS_MAX_NUMBER_PER_ATTACK/(float) bulletsPerAttack));

        linearPosition.x += additionalVelocity.x * delta*velocityRandom;
        linearPosition.y += additionalVelocity.y * delta*velocityRandom;

        if (/*radialTween.isStarted()*/thetaForRadialTween != 0) {
            float xFromTheCenter = linearPosition.x - viewport.getWorldWidth()/2f;
            float yFromTheCenter = linearPosition.y - viewport.getWorldHeight()/2f;

            float r = (float) Math.sqrt(xFromTheCenter*xFromTheCenter + yFromTheCenter*yFromTheCenter);
            float theta = (float) Math.atan2(yFromTheCenter, xFromTheCenter);

            theta += thetaForRadialTween * (STARS_RADIAL_TWEEN_THETA_MINIMUM_MULTIPLIER + velocityRandom) * delta;

            linearPosition.x = (float) (r*Math.cos(theta)) + viewport.getWorldWidth()/2f;
            linearPosition.y = (float) (r*Math.sin(theta)) + viewport.getWorldHeight()/2f;
        }

        if (linearPosition.x >= wWidth) {
            linearPosition.x = -2*radius + (linearPosition.x - wWidth);
        } else if (linearPosition.x < -2*radius) {
            linearPosition.x = wWidth + (linearPosition.x);
        }

        if (linearPosition.y >= wHeight) {
            linearPosition.y = 0 - 2*radius + (linearPosition.y - wHeight);
        } else if (linearPosition.y < -2*radius) {
            linearPosition.y = wHeight + (linearPosition.y);
        }

        finalPosition.x = linearPosition.x;
        finalPosition.y = linearPosition.y;
    }


    public void draw(Batch batch) {
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(
                texture,
                finalPosition.x,
                finalPosition.y,
                0,
                0,
                radius*2,
                radius*2,
                1,
                1,
                0,
                regionX,
                regionY,
                regionWidth,
                regionHeight,
                false,
                false);
    }

    private void generateRandomness(Viewport viewport) {
        Random random = new Random();

        radius = random.nextFloat() * STAR_MAX_RADIUS;

        initialPosition = new Vector2(random.nextFloat() * wWidth, random.nextFloat() * wHeight);
        linearPosition = new Vector2(initialPosition);
        velocityRandom = random.nextFloat();
        linearVelocity = new Vector2(0.5f* STAR_MAX_SPEED + velocityRandom/2.0f * STAR_MAX_SPEED /*radius/STAR_MAX_RADIUS * STAR_MAX_SPEED*/,
                /*0.5f*STAR_MAX_SPEED + random.nextFloat()/2 * STAR_MAX_SPEED*//*radius/STAR_MAX_RADIUS * STAR_MAX_SPEED/3f*/velocityRandom * STAR_MAX_SPEED /3.0f);

        alpha = 0;


        if (i == 0) {;
            //Gdx.app.log(TAG, "Star0 " + "-------------------------------------------- Spawned --------------------------------------------");
            //radius = 100;
        }
    }

    public void resize(float wWidth, float wHeight) {

        Vector2 positionRatio = new Vector2(/*finalPosition.x*/linearPosition.x/this.wWidth, /*finalPosition.y*/linearPosition.y/this.wHeight);

        this.wWidth = wWidth;
        this.wHeight = wHeight;

        /*finalPosition.x*/linearPosition.x = positionRatio.x * wWidth;
        /*finalPosition.y*/linearPosition.y = positionRatio.y * wHeight;
    }


    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
