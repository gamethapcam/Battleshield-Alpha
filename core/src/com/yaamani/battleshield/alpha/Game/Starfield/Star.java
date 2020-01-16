package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;

import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;


public class Star {

    private final String TAG = getClass().getSimpleName();

    private int i;

    private TextureRegion region;
    private Viewport viewport;

    private Vector2 initialPosition;
    private Vector2 linearPosition;
    private Vector2 linearVelocityUnity;

    private Vector2 finalPosition;

    private MyTween linearMovementTween;
    /**
     * linear movement + polar movement.
     */
    private MyTween plusMinusTween;

    private float radius;

    private float alpha;

    private float wWidth;
    private float wHeight;

    private float z;

    private Texture texture;
    private int regionX;
    private int regionY;
    private int regionWidth;
    private int regionHeight;

    public Star(TextureRegion region, Viewport viewport, int i) {
        this.region = region;
        this.viewport = viewport;
        this.i = i;

        initializeLinearMovementTween();

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

    public void act(float delta, Vector2 additionalVelocity, float currentSpeed, float thetaForRadialTween, int bulletsPerAttack, GameplayScreen gameplayScreen) {

        if (gameplayScreen.getState() == GameplayScreen.State.PAUSED)
            return;

        //linearMovementTween.update(delta);
        //plusMinusTween.update(delta);

        linearPosition.mulAdd(linearVelocityUnity, delta * (BULLETS_MAX_NUMBER_PER_ATTACK/(float) bulletsPerAttack) * z * currentSpeed);

        // additionalVelocity is a parameter by the starsContainer to achieve the cool effect of the intro
        linearPosition.x += additionalVelocity.x * delta* z;
        linearPosition.y += additionalVelocity.y * delta* z;

        if (/*radialTween.isStarted()*/thetaForRadialTween != 0) {
            float xFromTheCenter = linearPosition.x - viewport.getWorldWidth()/2f;
            float yFromTheCenter = linearPosition.y - viewport.getWorldHeight()/2f;

            float r = (float) Math.sqrt(xFromTheCenter*xFromTheCenter + yFromTheCenter*yFromTheCenter);
            float theta = (float) Math.atan2(yFromTheCenter, xFromTheCenter);

            theta += thetaForRadialTween * (STARS_RADIAL_TWEEN_THETA_MINIMUM_MULTIPLIER + z) * delta;

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

        radius = random.nextFloat() * STARS_MAX_RADIUS;

        initialPosition = new Vector2(random.nextFloat() * wWidth, random.nextFloat() * wHeight);
        linearPosition = new Vector2(initialPosition);
        z = random.nextFloat();
        //linearVelocity = new Vector2(0.5f* STAR_MAX_SPEED + velocityRandom/2.0f * STAR_MAX_SPEED,velocityRandom * STAR_MAX_SPEED /3.0f);
        linearVelocityUnity = new Vector2(MathUtils.cos(STARS_MOVING_ANGLE), MathUtils.sin(STARS_MOVING_ANGLE));

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

    // --------------------------- initializers ---------------------------

    private void initializeLinearMovementTween() {
        linearMovementTween = new MyTween(MyInterpolation.myLinear) {
            float cosStarsMovingAngle = MathUtils.cos(STARS_MOVING_ANGLE);
            float sinStarsMovingAngle = MathUtils.sin(STARS_MOVING_ANGLE);

            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                float distanceCoveredSoFar = myInterpolation.apply(startX, endX, startY, endY, currentX);
                linearPosition.x = cosStarsMovingAngle * distanceCoveredSoFar;
                linearPosition.y = sinStarsMovingAngle * distanceCoveredSoFar;
            }
        };
    }

    /*private void calculateLinearMovementValues() {
        // A line can be defined by 2 points s = (sx, sy) and e = (ex, ey). ** 's' for starting point and 'e' for end point.km
        // We can define the end point as: e = s + d*u.
        // Where d is the distance between the 2 points and u is a unit vector describing the direction from the starting point to the end point.
        // Then the distance d1, which is the distance between the current position and the line y=screenHeight following the direction u, equals: (screenHeight-currentPosition.y)/u.y.
        // And the distance d2, which is the distance between the current position and the line x=screenWidth following the direction u, equals: (screenWidth-currentPosition.x)/u.x.

        float d1 = (wHeight-linearPosition.y)/linearVelocityUnity.y;
        float d2 = (wWidth-linearPosition.x)/linearVelocityUnity.x;
        float d3 = (0-linearPosition.y)/linearVelocityUnity.y;
        float d4 = (0-linearPosition.x)/linearVelocityUnity.x;

        float[] distances = {d1, d2, d3, d4};

        float positiveDistance1 = 0, positiveDistance2 = 0;
        for(int i = 0; i < distances.length; i++) {
            if (distances[i] > 0) {
                if (positiveDistance1 == 0)
                    positiveDistance1 = distances[i];
                else {
                    positiveDistance2 = distances[i];
                    break;
                }
            }
        }

        float chosenDistance = Math.min(positiveDistance1, positiveDistance2);

        linearMovementTween.setInitialVal(0);
        linearMovementTween.setFinalVal(chosenDistance);
        linearMovementTween.setDurationMillis();
    }*/
}
