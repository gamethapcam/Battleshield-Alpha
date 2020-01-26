package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;


public class Star {

    private final String TAG = getClass().getSimpleName();

    private int i;

    private StarsContainer starsContainer;

    private TextureRegion region;
    private Viewport viewport;

    private Vector2 initialPosition;
    private Vector2 linearPosition;
    private Vector2 linearVelocityUnity;

    private Vector2 finalPosition;

    //private MyTween linearMovementTween;

    private Tween trailWarpTween;
    private Vector2 velocityWarpAnimationUnity;
    private Vector2 initialPositionTrailWarpAnimation;
    private Vector2 finalPositionTrailWarpAnimation;
    private Vector2 previousPositionTrailWarpAnimation;

    private float radius;
    private float glowRadius;

    private float alpha;

    private float wWidth;
    private float wHeight;

    private float z;

    private Texture texture;
    private int regionX;
    private int regionY;
    private int regionWidth;
    private int regionHeight;


    private Texture badlogic;


    public Star(StarsContainer starsContainer, TextureRegion region, Viewport viewport, int i) {
        this.starsContainer = starsContainer;
        this.region = region;
        this.viewport = viewport;
        this.i = i;

        //initializeLinearMovementTween();

        this.texture = region.getTexture();
        this.regionX = region.getRegionX();
        this.regionY = region.getRegionY();
        this.regionWidth = region.getRegionWidth();
        this.regionHeight = region.getRegionHeight();


        //this.badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));

        finalPosition = new Vector2();

        wWidth = viewport.getWorldWidth();
        wHeight = viewport.getWorldHeight();

        previousPositionTrailWarpAnimation = new Vector2(0, 0);

        generateRandomness(viewport);

        initializeTrailWarpTween();

    }

    public void act(float delta,
                    Vector2 additionalVelocity,
                    float currentStarSpeed,
                    float thetaForRadialTween,
                    boolean inTrailWarpAnimation,
                    boolean inWarpFastForwardAnimation,
                    float warpVelocityMultiplier,
                    GameplayScreen gameplayScreen) {

        if (gameplayScreen.getState() == GameplayScreen.State.PAUSED)
            return;

        //linearMovementTween.update(delta);
        //plusMinusTween.update(delta);
        trailWarpTween.update(delta);

        if (!inTrailWarpAnimation)
            linearPosition.mulAdd(linearVelocityUnity, delta * z * currentStarSpeed);
        /*else
            linearPosition.mulAdd(velocityWarpAnimationUnity, delta * warpVelocityMultiplier);*/


        // additionalVelocity is a parameter by the starsContainer to achieve the cool effect of the intro
        linearPosition.x += additionalVelocity.x * delta * z;
        linearPosition.y += additionalVelocity.y * delta * z;

        radialTweenPlusMinusBullet(delta, thetaForRadialTween);

        whenTheStarExitTheScreenBingItBack(inTrailWarpAnimation);

        if (!inTrailWarpAnimation) {
            finalPosition.x = linearPosition.x;
            finalPosition.y = linearPosition.y;
        } else {
            /*if (finalPositionTrailWarpAnimation.x > initialPositionTrailWarpAnimation.x)
                finalPosition.x = MathUtils.clamp(linearPosition.x, initialPositionTrailWarpAnimation.x, finalPositionTrailWarpAnimation.x);
            else
                finalPosition.x = MathUtils.clamp(linearPosition.x, finalPositionTrailWarpAnimation.x, initialPositionTrailWarpAnimation.x);
            
            if (finalPositionTrailWarpAnimation.y > initialPositionTrailWarpAnimation.y)
                finalPosition.y = MathUtils.clamp(linearPosition.y, initialPositionTrailWarpAnimation.y, finalPositionTrailWarpAnimation.y);
            else
                finalPosition.y = MathUtils.clamp(linearPosition.y, finalPositionTrailWarpAnimation.y, initialPositionTrailWarpAnimation.y);*/
        }
    }


    private void radialTweenPlusMinusBullet(float delta, float thetaForRadialTween) {
        if (/*radialTween.isStarted()*/thetaForRadialTween != 0) {
            float xFromTheCenter = linearPosition.x - viewport.getWorldWidth()/2f;
            float yFromTheCenter = linearPosition.y - viewport.getWorldHeight()/2f;

            float r = (float) Math.sqrt(xFromTheCenter*xFromTheCenter + yFromTheCenter*yFromTheCenter);
            float theta = (float) Math.atan2(yFromTheCenter, xFromTheCenter);

            theta += thetaForRadialTween * (STARS_RADIAL_TWEEN_THETA_MINIMUM_MULTIPLIER + z) * delta;

            linearPosition.x = (float) (r*Math.cos(theta)) + viewport.getWorldWidth()/2f;
            linearPosition.y = (float) (r*Math.sin(theta)) + viewport.getWorldHeight()/2f;
        }
    }

    private void whenTheStarExitTheScreenBingItBack(boolean inWarpAnimation) {
        if (!inWarpAnimation) {
            if (linearPosition.x >= wWidth) {
                linearPosition.x = -2 * radius + (linearPosition.x - wWidth);
            } else if (linearPosition.x < -2 * radius) {
                linearPosition.x = wWidth + (linearPosition.x);
            }

            if (linearPosition.y >= wHeight) {
                linearPosition.y = 0 - 2 * radius + (linearPosition.y - wHeight);
            } else if (linearPosition.y < -2 * radius) {
                linearPosition.y = wHeight + (linearPosition.y);
            }
        } else {
            /*if (linearPosition.x >= wWidth | linearPosition.x < -2 * radius | linearPosition.y >= wHeight | linearPosition.y < -2 * radius) {
                linearPosition.x = wWidth/2f * (float) Math.random() + wWidth/4f;
                linearPosition.y = wHeight/2f * (float) Math.random() + wHeight/4f;
                finalPosition.x = linearPosition.x;
                finalPosition.y = linearPosition.y;
                calculateWarpStuff();
            }*/
        }
    }

    public void draw(Batch batch, float delta, boolean inWarpTrailAnimation) {
        batch.setColor(1f, 1f, 1f, alpha);
        if (!inWarpTrailAnimation) {
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
            //batch.draw(badlogic, finalPosition.x, finalPosition.y, radius*2, radius*2);
            /*batch.draw(
                    glowRegion,
                    finalPosition.x - delta*i*velocityWarpAnimationUnity.x - glowRadius,
                    finalPosition.y - delta*i*velocityWarpAnimationUnity.y - glowRadius,
                    glowRadius*2,
                    glowRadius*2);*/
        else {

            //if (i == 0) {
                float warpVelocity = MathUtils.clamp((Vector2.dst(finalPosition.x, finalPosition.y, previousPositionTrailWarpAnimation.x, previousPositionTrailWarpAnimation.y) / delta), 1, Float.MAX_VALUE);
                //starsContainer.setWarpStretchFactor(warpVelocity);
            //}
            /*int times;
            times = MathUtils.clamp((int) (Vector2.dst(finalPosition.x, finalPosition.y, previousPositionTrailWarpAnimation.x, previousPositionTrailWarpAnimation.y) / delta), 1, Integer.MAX_VALUE);*/
            /*if (velocityWarpAnimationUnity.x != 0)
                times =  ((finalPosition.x - previousPositionTrailWarpAnimation.x) / velocityWarpAnimationUnity.x);
            else 
                times =  ((finalPosition.y - previousPositionTrailWarpAnimation.y) / velocityWarpAnimationUnity.y);
                
            //times = MathUtils.clamp((times), 1, Integer.MAX_VALUE);*/

            /*if (i == 0) {
                Gdx.app.log(TAG, "" + times);
            }*/


            for (int i = 0; i < /*times*/warpVelocity; i+=5) { // not i++ is for performance.
                /*batch.draw(
                        glowRegion.getTexture(),
                        finalPosition.x + radius - glowRadius - delta*i*velocityWarpAnimationUnity.x,
                        finalPosition.y + radius - glowRadius - delta*i*velocityWarpAnimationUnity.y,
                        0,
                        0,
                        glowRadius*2,
                        glowRadius*2,
                        1,
                        1,
                        0,
                        glowRegion.getRegionX(),
                        glowRegion.getRegionY(),
                        glowRegion.getRegionWidth(),
                        glowRegion.getRegionHeight(),
                        false,
                        false);*/

                batch.draw(
                        texture,
                        finalPosition.x - delta*i*velocityWarpAnimationUnity.x,
                        finalPosition.y - delta*i*velocityWarpAnimationUnity.y,
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

            previousPositionTrailWarpAnimation.x = finalPosition.x;
            previousPositionTrailWarpAnimation.y = finalPosition.y;
        }
    }

    private void generateRandomness(Viewport viewport) {
        Random random = new Random();

        radius = random.nextFloat() * STARS_MAX_RADIUS;
        glowRadius = radius * 17;

        initialPosition = new Vector2(random.nextFloat() * wWidth, random.nextFloat() * wHeight);
        linearPosition = new Vector2(initialPosition);
        z = random.nextFloat();
        //linearVelocity = new Vector2(0.5f* STAR_MAX_SPEED + velocityRandom/2.0f * STAR_MAX_SPEED,velocityRandom * STAR_MAX_SPEED /3.0f);
        linearVelocityUnity = new Vector2(MathUtils.cos(STARS_MOVING_ANGLE), MathUtils.sin(STARS_MOVING_ANGLE));

        alpha = 0;

        /*if (i == 0) {;
            //Gdx.app.log(TAG, "Star0 " + "-------------------------------------------- Spawned --------------------------------------------");
            //radius = 100;
        }*/
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

    public Vector2 getFinalPosition() {
        return finalPosition;
    }

    public Vector2 getPreviousPositionTrailWarpAnimation() {
        return previousPositionTrailWarpAnimation;
    }

    public void startTrailWarpTween() {
        trailWarpTween.start();
    }

    void calculateWarpStuff() {
        if (velocityWarpAnimationUnity == null)
            velocityWarpAnimationUnity = new Vector2();

        float xDistanceFromCentre = finalPosition.x - wWidth/2f;
        float yDistanceFromCentre = finalPosition.y - wHeight/2f;
        float distanceFromCentre = (float) Math.sqrt(xDistanceFromCentre*xDistanceFromCentre + yDistanceFromCentre*yDistanceFromCentre);

        float cos = xDistanceFromCentre / distanceFromCentre;
        float sin = yDistanceFromCentre / distanceFromCentre;

        velocityWarpAnimationUnity.x = cos;
        velocityWarpAnimationUnity.y = sin;

        
        
        if (initialPositionTrailWarpAnimation == null)
            initialPositionTrailWarpAnimation = new Vector2();
        
        initialPositionTrailWarpAnimation.x = finalPosition.x;
        initialPositionTrailWarpAnimation.y = finalPosition.y;

        if (finalPositionTrailWarpAnimation == null)
            finalPositionTrailWarpAnimation = new Vector2();

        float finalDistanceFromCentre = distanceFromCentre + STAR_BULLET_TRAIL_WARP_TOTAL_DISTANCE;
        finalPositionTrailWarpAnimation.x = finalDistanceFromCentre * cos + wWidth/2f;
        finalPositionTrailWarpAnimation.y = finalDistanceFromCentre * sin + wHeight/2f;
    }

    // --------------------------- initializers ---------------------------

    private void initializeTrailWarpTween() {
        trailWarpTween = new Tween(STAR_BULLET_SECOND_STAGE_DURATION, STAR_BULLET_SECOND_STAGE_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();
                previousPositionTrailWarpAnimation.x = finalPosition.x;
                previousPositionTrailWarpAnimation.y = finalPosition.y;
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                finalPosition.x = interpolation.apply(initialPositionTrailWarpAnimation.x, finalPositionTrailWarpAnimation.x, percentage);
                finalPosition.y = interpolation.apply(initialPositionTrailWarpAnimation.y, finalPositionTrailWarpAnimation.y, percentage);
            }

        };
    }

    /*private void initializeLinearMovementTween() {
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
    }*/

    /*private void calculateLinearMovementValues() {
        // A line can be defined by 2 points s = (sx, sy) and e = (ex, ey). ** 's' for starting point and 'e' for end point.
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
