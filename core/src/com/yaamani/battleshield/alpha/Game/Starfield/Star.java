package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
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

    private GameplayScreen gameplayScreen;

    private TextureRegion region;
    private Viewport viewport;

    private Vector2 initialPosition;
    private Vector2 linearPosition;
    private Vector2 linearVelocityUnity;

    private Vector2 previousFinalPosition;
    private Vector2 finalPosition;

    private boolean motionBlur;
    private Vector2 linearVelocityUnityMotionBlur;

    //private MyTween linearMovementTween;

    private Tween trailWarpTweenStarBullet_SecondStage;
    private Vector2 warpVelocityUnity;
    private Vector2 initialPositionTrailWarpAnimation;
    private Vector2 finalPositionTrailWarpAnimation;
    private Vector2 previousPositionTrailWarpAnimation;

    private float radius;
    //private float glowRadius;

    private float originalRadius;
    private float fastForwardWarpMinRadius;
    private float fastForwardWarpMaxRadius;
    private float fastForwardWarpVelocityMultiplier;
    private Tween fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage; //

    private float alpha;

    private float wWidth;
    private float wHeight;
    private float maxR;

    private float z;

    private Texture texture;
    private int regionX;
    private int regionY;
    private int regionWidth;
    private int regionHeight;


    private Texture badlogic;


    public Star(/*GameplayScreen gameplayScreen, */TextureRegion region, Viewport viewport, int i) {
        //this.gameplayScreen = gameplayScreen;
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


        previousFinalPosition = new Vector2();
        finalPosition = new Vector2();

        motionBlur = STARS_MOTION_BLUR;
        linearVelocityUnityMotionBlur = new Vector2();

        wWidth = viewport.getWorldWidth();
        wHeight = viewport.getWorldHeight();

        previousPositionTrailWarpAnimation = new Vector2(0, 0);

        generateRandomness(viewport);

        initializeTrailWarpTweenStarBullet_SecondStage();
        initializeFastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage();

    }

    public void act(float delta,
                    Vector2 additionalVelocity,
                    float currentStarSpeed,
                    float thetaForRadialTween,
                    boolean inTrailWarpAnimation,
                    boolean inWarpFastForwardAnimation,
                    float warpFastForwardSpeed,
                    GameplayScreen gameplayScreen) {

        if (gameplayScreen.getState() == GameplayScreen.State.PAUSED)
            return;

        previousFinalPosition.x = finalPosition.x;
        previousFinalPosition.y = finalPosition.y;


        /*if (radius/STARS_MAX_RADIUS < 0.45f)
            alpha = MathUtils.random();*/

        float r = 0;

        //linearMovementTween.update(delta);
        //plusMinusTween.update(delta);
        trailWarpTweenStarBullet_SecondStage.update(delta);
        fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage.update(delta);

        if (!inTrailWarpAnimation) {
            //if (!inWarpFastForwardAnimation)
                linearPosition.mulAdd(linearVelocityUnity, delta * z * currentStarSpeed);
            //else
            if (inWarpFastForwardAnimation) {

                r = linearPosition.dst(wWidth/2f, wHeight/2f);

                float rRatio = calculateRadiusFastForwardWarp(r);

                linearPosition.mulAdd(warpVelocityUnity, delta * (STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_POLAR_INTERPOLATION.apply(rRatio)+0.1f) * fastForwardWarpVelocityMultiplier * warpFastForwardSpeed);

                /*if (i == 0)
                    Gdx.app.log(TAG, "inWarpFastForwardAnimation");*/
            }
        }

        /*if (radius > STARS_MAX_RADIUS | radius < STARS_MIN_RADIUS)
            Gdx.app.log(TAG, "" + i
                    + ", maxRadius = " + fastForwardWarpMaxRadius
                    + ", minRadius = " + fastForwardWarpMinRadius
                    + ", originalRadius = " + originalRadius
                    + ", radius = " + radius);*/

        // additionalVelocity is a parameter by the gameplayScreen to achieve the cool effect of the intro
        linearPosition.x += additionalVelocity.x * delta * z;
        linearPosition.y += additionalVelocity.y * delta * z;

        radialTweenPlusMinusBullet(delta, thetaForRadialTween);

        whenTheStarExitTheScreenBingItBack(inTrailWarpAnimation, r, inWarpFastForwardAnimation);



        finalPosition.x = linearPosition.x;
        finalPosition.y = linearPosition.y;
    }


    private void radialTweenPlusMinusBullet(float delta, float thetaForRadialTween) {
        if (/*radialTween.isStarted()*/thetaForRadialTween != 0) {
            float xFromTheCenter = linearPosition.x - viewport.getWorldWidth()/2f;
            float yFromTheCenter = linearPosition.y - viewport.getWorldHeight()/2f;

            float r = (float) Math.sqrt(xFromTheCenter*xFromTheCenter + yFromTheCenter*yFromTheCenter);
            float theta = (float) Math.atan2(yFromTheCenter, xFromTheCenter);

            theta += thetaForRadialTween * (STARS_POLAR_TWEEN_THETA_MINIMUM_MULTIPLIER + z) * delta;

            linearPosition.x = (float) (r*Math.cos(theta)) + viewport.getWorldWidth()/2f;
            linearPosition.y = (float) (r*Math.sin(theta)) + viewport.getWorldHeight()/2f;
        }
    }

    private void whenTheStarExitTheScreenBingItBack(boolean inTrailWarpAnimation, float r, boolean inWarpFastForwardAnimation) {

        //Gdx.app.log(TAG, "" + inTrailWarpAnimation + ", " + inWarpFastForwardAnimation);

        if (!inTrailWarpAnimation & !inWarpFastForwardAnimation) {
            if (linearPosition.x >= wWidth) {
                linearPosition.x = -2 * radius + (linearPosition.x - wWidth);
                overwritePreviousPosition();
            } else if (linearPosition.x < -2 * radius) {
                linearPosition.x = wWidth + (linearPosition.x);
                overwritePreviousPosition();
            }

            if (linearPosition.y >= wHeight) {
                linearPosition.y = 0 - 2 * radius + (linearPosition.y - wHeight);
                overwritePreviousPosition();
            } else if (linearPosition.y < -2 * radius) {
                linearPosition.y = wHeight + (linearPosition.y);
                overwritePreviousPosition();
            }
        } else if (inWarpFastForwardAnimation) {


            if (linearPosition.x >= wWidth | linearPosition.x < -2 * radius | linearPosition.y >= wHeight | linearPosition.y < -2 * radius) {
            //if (r >= maxR) {

                float newR = STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_POLAR_INTERPOLATION.apply(maxR, maxR/10f, MathUtils.random());

                calculateRadiusFastForwardWarp(newR);

                linearPosition.x = /*wWidth/2f*/  wWidth/2f + newR * warpVelocityUnity.x;
                linearPosition.y = /*wHeight/2f*/wHeight/2f + newR * warpVelocityUnity.y;


                overwritePreviousPosition();
            }
        }
    }

    private void overwritePreviousPosition() {
        previousFinalPosition.x = linearPosition.x;
        previousFinalPosition.y = linearPosition.y;
    }

    private float calculateRadiusFastForwardWarp(float r) {
        float rRatio = MathUtils.clamp(r/(wWidth/2f), 0, 1);

        radius = STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_POLAR_INTERPOLATION.apply(fastForwardWarpMinRadius, fastForwardWarpMaxRadius, rRatio);

        /*if (gameplayScreen.getState() == GameplayScreen.State.LOST)
            Gdx.app.log(TAG, "calculateRadiusFastForwardWarp(r);");*/

        return rRatio;
    }

    private void generateRandomness(Viewport viewport) {
        Random random = new Random();

        originalRadius = MathUtils.lerp(STARS_MIN_RADIUS, STARS_MAX_RADIUS, random.nextFloat());
        radius = originalRadius;
        //Gdx.app.log(TAG, "" + radius + ",\t" + radius/STARS_MAX_RADIUS);
        //glowRadius = radius * 17;

        initialPosition = new Vector2(random.nextFloat() * wWidth, random.nextFloat() * wHeight);
        linearPosition = new Vector2(initialPosition);
        z = random.nextFloat();
        //linearVelocity = new Vector2(0.5f* STAR_MAX_SPEED + velocityRandom/2.0f * STAR_MAX_SPEED,velocityRandom * STAR_MAX_SPEED /3.0f);
        linearVelocityUnity = new Vector2(MathUtils.cos(STARS_MOVING_ANGLE), MathUtils.sin(STARS_MOVING_ANGLE));

        alpha = 0;

        finalPosition.x = linearPosition.x;
        finalPosition.y = linearPosition.y;

        previousFinalPosition.x = finalPosition.x;
        previousFinalPosition.y = finalPosition.y;

        /*if (i == 0) {;
            //Gdx.app.log(TAG, "Star0 " + "-------------------------------------------- Spawned --------------------------------------------");
            //radius = 100;
        }*/
    }

    public void resize(float wWidth, float wHeight) {

        Vector2 positionRatio = new Vector2(/*finalPosition.x*/linearPosition.x/this.wWidth, /*finalPosition.y*/linearPosition.y/this.wHeight);

        this.wWidth = wWidth;
        this.wHeight = wHeight;

        this.maxR = (float) Math.sqrt(wWidth/2f*wWidth/2f + wHeight/2f*wHeight/2f);

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

    void setGameplayScreen(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        gameplayScreen.addToFinishWhenLosing(trailWarpTweenStarBullet_SecondStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(trailWarpTweenStarBullet_SecondStage);

        gameplayScreen.addToFinishWhenLosing(fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage);
    }

    public void startTrailWarpTween() {
        trailWarpTweenStarBullet_SecondStage.start();
    }

    void calculateTrailWarpStuff() {

        float distanceFromCentre = calculateWarpVelocityVector();

        float cos = warpVelocityUnity.x, sin = warpVelocityUnity.y;


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

    private float calculateWarpVelocityVector() {
        if (warpVelocityUnity == null)
            warpVelocityUnity = new Vector2();

        float xDistanceFromCentre = finalPosition.x - wWidth/2f;
        float yDistanceFromCentre = finalPosition.y - wHeight/2f;
        float distanceFromCentre = (float) Math.sqrt(xDistanceFromCentre*xDistanceFromCentre + yDistanceFromCentre*yDistanceFromCentre);

        float cos = xDistanceFromCentre / distanceFromCentre;
        float sin = yDistanceFromCentre / distanceFromCentre;

        warpVelocityUnity.x = cos;
        warpVelocityUnity.y = sin;

        return distanceFromCentre;
    }

    private void calculateFastForwardWarpStuff() {

        /*if (i == STARS_COUNT-1) {
            Gdx.app.log(TAG, "" + (STARS_COUNT-1));
        }*/

        /*float divisionFactor = 2f;

        finalPosition.x = linearPosition.x = wWidth/2f - wWidth/(divisionFactor*2) + MathUtils.random() * wWidth/divisionFactor;
        finalPosition.y = linearPosition.y = wHeight/2f - wHeight/(divisionFactor*2) + MathUtils.random() * wHeight/divisionFactor;*/

        //originalRadius = radius;
        fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage.start();

        float theta = MathUtils.random() * MathUtils.PI2;
        float r = STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_POLAR_INTERPOLATION.apply(MathUtils.random()) * wWidth/2f;

        float cos = MathUtils.cos(theta);
        float sin = MathUtils.sin(theta);

        finalPosition.x = linearPosition.x = wWidth/2f + r*cos;
        finalPosition.y = linearPosition.y = wHeight/2f + r*sin;

        warpVelocityUnity.x = cos;
        warpVelocityUnity.y = sin;
        //calculateWarpVelocityVector();

        //fastForwardWarpMaxRadius = MathUtils.lerp(STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MIN, STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MAX, MathUtils.random());
        fastForwardWarpVelocityMultiplier = MathUtils.lerp(STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MIN, STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MAX, MathUtils.random());
        //gameplayScreen.getStarsContainer().setThetaForRadialTween(MathUtils.PI2 * 0.4f);

        if(i == 0)
            Gdx.app.log(TAG, "calculateFastForwardWarpStuff();");
    }

    private float randFunc(float rand) {
        return 0.5f+((float)Math.pow(2*rand-1, 7))/2f;
    }

    public void draw(Batch batch, float delta, boolean inWarpTrailAnimation, boolean inWarpFastForwardAnimation) {
        batch.setColor(1f, 1f, 1f, alpha);
        if (!inWarpTrailAnimation/* & !inWarpFastForwardAnimation*/) {

            if (!motionBlur)
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

            else { // Extremely inefficient. Try drawing a rectangle from the previous position to the current one.
                float dst = Vector2.dst(previousFinalPosition.x, previousFinalPosition.y, finalPosition.x, finalPosition.y);
                float velocity = dst / delta;
                linearVelocityUnityMotionBlur.x = (finalPosition.x - previousFinalPosition.x) / dst;
                linearVelocityUnityMotionBlur.y = (finalPosition.y - previousFinalPosition.y) / dst;

                //Gdx.app.log(TAG, "velocity = " + velocity);
                //batch.setColor(1f, 1f, 1f, alpha-MathUtils.clamp(velocity/200f, 0, 0.1f));
                for (int i = 0; i < velocity; i+=1) {
                    batch.draw(
                            texture,
                            previousFinalPosition.x + delta*i*linearVelocityUnityMotionBlur.x,
                            previousFinalPosition.y + delta*i*linearVelocityUnityMotionBlur.y,
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
            }

        }
        //batch.draw(badlogic, finalPosition.x, finalPosition.y, radius*2, radius*2);
            /*batch.draw(
                    glowRegion,
                    finalPosition.x - delta*i*warpVelocityUnity.x - glowRadius,
                    finalPosition.y - delta*i*warpVelocityUnity.y - glowRadius,
                    glowRadius*2,
                    glowRadius*2);*/
        else {

            float warpVelocity = MathUtils.clamp((Vector2.dst(finalPosition.x, finalPosition.y, previousPositionTrailWarpAnimation.x, previousPositionTrailWarpAnimation.y) / delta), 1, Float.MAX_VALUE);

            for (int i = 0; i < warpVelocity; i+=5) { // not i++ is for performance.

                batch.draw(
                        texture,
                        finalPosition.x - delta*i* warpVelocityUnity.x,
                        finalPosition.y - delta*i* warpVelocityUnity.y,
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

    // --------------------------- initializers ---------------------------

    private void initializeTrailWarpTweenStarBullet_SecondStage() {
        trailWarpTweenStarBullet_SecondStage = new Tween(STAR_BULLET_SECOND_STAGE_DURATION, STAR_BULLET_SECOND_STAGE_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();
                previousPositionTrailWarpAnimation.x = finalPosition.x;
                previousPositionTrailWarpAnimation.y = finalPosition.y;
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (initialPositionTrailWarpAnimation == null | finalPositionTrailWarpAnimation == null)
                    return;

                if (gameplayScreen.getState() == GameplayScreen.State.LOST) return;

                linearPosition.x = interpolation.apply(initialPositionTrailWarpAnimation.x, finalPositionTrailWarpAnimation.x, percentage);
                linearPosition.y = interpolation.apply(initialPositionTrailWarpAnimation.y, finalPositionTrailWarpAnimation.y, percentage);

                /*if  (i == 0)
                    if (percentage == 1)
                        Gdx.app.log(TAG, "tween(1);");*/
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.LOST)
                    calculateFastForwardWarpStuff();

                /*if  (i == 0)
                    Gdx.app.log(TAG, "finish();");*/
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(trailWarpTweenStarBullet_SecondStage);
    }

    private void initializeFastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage() {
        fastForwardWarpMaxRadiusMinRadiusTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {

                if (gameplayScreen.getState() == GameplayScreen.State.LOST) return;

                fastForwardWarpMinRadius = interpolation.apply(STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MIN, originalRadius, percentage);
                fastForwardWarpMaxRadius = interpolation.apply(STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MAX, originalRadius, percentage);

                /*if (i == 0) {
                    Gdx.app.log(Star.this.TAG, "" + percentage);
                }*/

            }

            @Override
            public void onFinish() {
                super.onFinish();

                radius = originalRadius;
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






    public static class TrailWarpInterpolation extends Interpolation {

        private float m;
        private float p;

        public TrailWarpInterpolation(float m, float p) {
            this.m = m;
            this.p = p;
        }

        @Override
        public float apply(float a) {
            /*float c = 1/(1+m);
            return c * (m*a + (float) Math.pow(a, p));*/

            float c3 = -(float)Math.sqrt(m/3);
            float c2 = c3*c3*c3;
            float c1 = -1f/(-c2 - (1-c3)*(1-c3)*(1-c3) + m + 1);

            return c1 * (c2 - (a+c3)*(a+c3)*(a+c3) + m*a + (float)Math.pow(a, p));
        }
    }
}
