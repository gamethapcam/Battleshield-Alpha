package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsHandler;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.TrailWarpPostProcessingEffect;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class StarsContainer extends Group implements Disposable{

    public final String TAG = this.getClass().getSimpleName();

    private TextureRegion starRegion;
    //private TextureRegion glowRegion;

    private Array<Star> stars;

    private Viewport viewport;
    private FrameBuffer originalFrameBuffer;

    //private final Vector2 distanceOffset = new Vector2(Wo)
    private Vector2 transitionVelocity = new Vector2(0, 0);
    private Vector2 transitionVelocityMax = new Vector2(0, 0);

    private float currentStarSpeed;
    private float starsSpeedBeforStarBullet;
    private MyTween currentSpeedTweenStarBullet_FirstStage; //First stage

    private TrailWarpPostProcessingEffect trailWarpPostProcessingEffect;

    private boolean inWarpTrailAnimation = false;
    private boolean inWarpFastForwardAnimation = false;

    private Tween warpStretchFactorTweenStarBullet_SecondStage; //Second stage
    private float warpFastForwardSpeed = 1;
    private Tween warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage; //Third stage


    private RadialTween radialTween;
    private float thetaForRadialTween; // Rad

    private BulletsHandler bulletsHandler;

    private GameplayScreen gameplayScreen;

    private Texture badlogic;

    private Array<Float> fpss;

    public StarsContainer(AdvancedStage game) {
        setTransform(false);

        viewport = game.getViewport();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        //game.addUpdatable(this);

        starRegion = Assets.instance.mutualAssets.star/*Assets.instance.gameplayAssets.gameOverBG*/;
        //glowRegion = Assets.instance.mutualAssets.starGlow;
        stars = new Array<Star>(false, STARS_COUNT, Star.class);
        for (int i = 0; i < STARS_COUNT; i++) {
            Star star = new Star(/*gameplayScreen, */starRegion, viewport, i);
            stars.add(star);
        }

        currentStarSpeed = STARS_SPEED;

        originalFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (viewport.getScreenWidth()), (viewport.getScreenHeight()), false);
        
        trailWarpPostProcessingEffect = new TrailWarpPostProcessingEffect(viewport, viewport.getWorldWidth(), viewport.getWorldHeight(), STAR_BULLET_TRAIL_WARP_BLUR_RESOLUTION_DIVISOR, STAR_BULLET_TRAIL_WARP_BLUR_KERNEL_SIZE);


        initializeCurrentSpeedTweenStarBullet_FirstStage();

        initializeRadialTween();

        initializeWarpStretchFactorTweenStarBullet_SecondStage();
        initializeWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage();

        badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));
        badlogic.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fpss = new Array<>(Float.class);
    }

    @Override
    public void act(float delta) {
        currentSpeedTweenStarBullet_FirstStage.update(delta);
        radialTween.update(delta);
        warpStretchFactorTweenStarBullet_SecondStage.update(delta);
        warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage.update(delta);

        //Gdx.app.log(TAG, "" + thetaForRadialTween);

        for (Star star : stars) {
            star.act(delta,
                    transitionVelocity,
                    currentStarSpeed,
                    thetaForRadialTween,
                    inWarpTrailAnimation,
                    inWarpFastForwardAnimation,
                    warpFastForwardSpeed,
                    gameplayScreen);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            calculateTrailWarpStuff();
            inWarpTrailAnimation = !inWarpTrailAnimation;
        }

        //Gdx.app.log(TAG, "" + (int) (Gdx.input.getX()/(float)Gdx.graphics.getWidth() * KERNEL_SIZE));

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            //currentSpeedTweenStarBullet_FirstStage.start();
            radialTween.pauseGradually(STAR_BULLET_FIRST_STAGE_DURATION, 0);

            Gdx.app.log(TAG, "" + radialTween.isPausingGradually());
        }*/
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);
        batch.end();
        Texture starsFrameBufferTexture = renderStarsToOriginalFrameBuffer(batch);

        if (inWarpTrailAnimation) {
            trailWarpPostProcessingEffect.draw(batch, starsFrameBufferTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        } else {
            if (!trailWarpPostProcessingEffect.isBlurBuffersCleared())
                trailWarpPostProcessingEffect.clearBlurBuffers(batch);

            batch.begin();
            batch.draw(starsFrameBufferTexture,
                    0,
                    0,
                    viewport.getWorldWidth(),
                    viewport.getWorldHeight(),
                    0,
                    0,
                    starsFrameBufferTexture.getWidth(),
                    starsFrameBufferTexture.getHeight(),
                    false,
                    true);
        }

        if (inWarpTrailAnimation)
            fpss.add(1/Gdx.graphics.getDeltaTime());

    }


    public void resize(float wWidth, float wHeight) {
        for (Star star : stars) {
            star.resize(wWidth, wHeight);
        }

        trailWarpPostProcessingEffect.setInputTextureWidthWorldUnits(viewport.getWorldWidth());
        trailWarpPostProcessingEffect.setInputTextureHeightWorldUnits(viewport.getWorldHeight());
    }

    @Override
    public void dispose() {
        originalFrameBuffer.dispose();
        trailWarpPostProcessingEffect.dispose();
    }

    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------

    private void calculateTrailWarpStuff() {
        for (Star star : stars) {
            star.calculateTrailWarpStuff();
            star.startTrailWarpTween();
        }
    }

    public void startCurrentSpeedTweenStarBullet() {
        starsSpeedBeforStarBullet = currentStarSpeed;
        currentSpeedTweenStarBullet_FirstStage.setInitialVal(currentStarSpeed);
        currentSpeedTweenStarBullet_FirstStage.start();
    }

    public void updateCurrentStarSpeed(float bulletsPerAttack) {
        this.currentStarSpeed = (BULLETS_MAX_NUMBER_PER_ATTACK/(float) bulletsPerAttack) * STARS_SPEED;
    }

    public void resetCurrentSpeed() {
        this.currentStarSpeed = STARS_SPEED;
    }

    private Texture renderStarsToOriginalFrameBuffer(Batch batch) {
        originalFrameBuffer.begin();

        batch.begin();

        if (!inWarpTrailAnimation) {
            Gdx.gl.glClearColor(1, 1, 1, 0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        //batch.draw(badlogic, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        for (Star star : stars) {
            star.draw(batch, Gdx.graphics.getDeltaTime(), inWarpTrailAnimation, inWarpFastForwardAnimation);
        }

        batch.end();

        originalFrameBuffer.end();

        Texture starsFrameBuffer = originalFrameBuffer.getColorBufferTexture();
        starsFrameBuffer.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return starsFrameBuffer;
    }

    /*private void calculateFastForwardWarpStuff() {
        for (Star star : stars) {
            star.calculateFastForwardWarpStuff();
        }
    }*/

    // ------------------------ Getters & Setters ------------------------
    // ------------------------ Getters & Setters ------------------------
    // ------------------------ Getters & Setters ------------------------

    public boolean isInWarpTrailAnimation() {
        return inWarpTrailAnimation;
    }

    public Array<Float> getFpss() {
        return fpss;
    }

    public void setInWarpTrailAnimation(boolean inWarpTrailAnimation) {
        this.inWarpTrailAnimation = inWarpTrailAnimation;
        if (inWarpTrailAnimation)
            calculateTrailWarpStuff();
    }

    public boolean isInWarpFastForwardAnimation() {
        return inWarpFastForwardAnimation;
    }

    public void setInWarpFastForwardAnimation(boolean inWarpFastForwardAnimation) {
        this.inWarpFastForwardAnimation = inWarpFastForwardAnimation;
        /*if (inWarpFastForwardAnimation)
            calculateFastForwardWarpStuff();*/
    }

    public void setTransitionVelocity(Vector2 transitionVelocity) {
        this.transitionVelocity = transitionVelocity;
    }

    public Vector2 getTransitionVelocity() {
        return transitionVelocity;
    }

    public Star[] getStars() {
        return stars.items;
    }

    public RadialTween getRadialTween() {
        return radialTween;
    }

    public void setBulletsHandler(BulletsHandler bulletsHandler) {
        this.bulletsHandler = bulletsHandler;
    }

    public float getThetaForRadialTween() {
        return thetaForRadialTween;
    }

    public void setThetaForRadialTween(float thetaForRadialTween) {
        this.thetaForRadialTween = thetaForRadialTween;
    }

    public void setGameplayScreen(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(radialTween);
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentSpeedTweenStarBullet_FirstStage);
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(warpStretchFactorTweenStarBullet_SecondStage);
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage);

        for (Star star : stars) {
            star.setGameplayScreen(gameplayScreen);
        }
    }

    public Tween getWarpStretchFactorTweenStarBullet_SecondStage() {
        return warpStretchFactorTweenStarBullet_SecondStage;
    }

    public Tween getWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage() {
        return warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage;
    }

    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------

    private void initializeCurrentSpeedTweenStarBullet_FirstStage() {
        currentSpeedTweenStarBullet_FirstStage = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION, STARS_SPEED, 0) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                currentStarSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentSpeedTweenStarBullet_FirstStage);
    }

    private void initializeRadialTween() {
        radialTween = new RadialTween(STARS_POLAR_TWEEN_DURATION, exp10) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (percentage < 0.5f) { // interpolationIn
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolation.apply(0, STARS_POLAR_TWEEN_THETA_MAX, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolation.apply(0, -STARS_POLAR_TWEEN_THETA_MAX, percentage);
                } else { // interpolationOut
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolation.apply(STARS_POLAR_TWEEN_THETA_MAX, 0, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolation.apply(-STARS_POLAR_TWEEN_THETA_MAX, 0, percentage);
                }
            }

            @Override
            public void onFinish() {
                thetaForRadialTween = 0;
                super.onFinish();
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(radialTween);
    }

    private void initializeWarpStretchFactorTweenStarBullet_SecondStage() {
        warpStretchFactorTweenStarBullet_SecondStage = new Tween(STAR_BULLET_SECOND_STAGE_DURATION, STAR_BULLET_SECOND_STAGE_INTERPOLATION) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                trailWarpPostProcessingEffect.setWarpStretchFactor(interpolation.apply(percentage));
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(warpStretchFactorTweenStarBullet_SecondStage);
    }

    private void initializeWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage() {
        warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                warpFastForwardSpeed = interpolation.apply(1, 0, percentage);
                currentStarSpeed = interpolation.apply(0, starsSpeedBeforStarBullet, percentage);
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(warpStretchFactorTweenStarBullet_SecondStage);
    }





















    public abstract static class RadialTween extends Tween {

        private SpecialBullet specialBullet;

        public RadialTween(float durationMillis, Interpolation interpolation) {
            super(durationMillis, interpolation);
        }

        public void start(SpecialBullet specialBullet) {
            this.specialBullet = specialBullet;
            super.start();
        }

        public void start(float delay, SpecialBullet specialBullet) {
            this.specialBullet = specialBullet;
            super.start(delay);
        }

        public SpecialBullet getSpecialBullet() {
            return specialBullet;
        }
    }


}
