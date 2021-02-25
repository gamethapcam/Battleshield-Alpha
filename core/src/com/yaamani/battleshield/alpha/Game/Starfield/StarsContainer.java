package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsHandler;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GlassCrackPostProcessingEffect;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.TrailWarpPostProcessingEffect;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyFrameBuffer;
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
    private MyFrameBuffer originalFrameBuffer;

    //private final Vector2 distanceOffset = new Vector2(Wo)
    private Vector2 transitionVelocity = new Vector2(0, 0);
    private Vector2 transitionVelocityMax = new Vector2(0, 0);

    private float currentStarSpeed;
    private float starsSpeedBeforeSlowMo;
    private MyTween currentStarSpeedSlowMoTween; // Star bullet first stage + Rewind slow mo

    private TrailWarpPostProcessingEffect trailWarpPostProcessingEffect;
    private GlassCrackPostProcessingEffect glassCrackPostProcessingEffect;

    private boolean inWarpTrailAnimation = false;
    private boolean inWarpFastForwardAnimation = false;

    private Tween warpStretchFactorTweenStarBullet_SecondStage; //Second stage
    private float warpFastForwardSpeed = 1;
    private Tween warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage; //Third stage


    private RadialTween radialTween;
    private float baseRadialVelocity;
    private float radialVelocity; // rad/sec
    private MyTween radialVelocitySlowMoTween;

    private BulletsHandler bulletsHandler;

    private GameplayScreen gameplayScreen;

    private Texture badlogic;
    private Texture test;

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


        setCurrentStarSpeed(STARS_SPEED);

        originalFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, (viewport.getScreenWidth()), (viewport.getScreenHeight()), false);
        
        trailWarpPostProcessingEffect = new TrailWarpPostProcessingEffect(viewport, viewport.getWorldWidth(), viewport.getWorldHeight(), STAR_BULLET_TRAIL_WARP_BLUR_RESOLUTION_DIVISOR, STAR_BULLET_TRAIL_WARP_BLUR_KERNEL_SIZE);

        glassCrackPostProcessingEffect = new GlassCrackPostProcessingEffect();


        initializeCurrentStarSpeedSlowMoTween();

        initializeRadialTween();

        initializeRadialVelocitySlowMoTween();

        initializeWarpStretchFactorTweenStarBullet_SecondStage();
        initializeWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage();

        badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));
        badlogic.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        //test = new Texture(Gdx.files.internal("test.jpg"));
        //test.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fpss = new Array<>(Float.class);
    }

    @Override
    public void act(float delta) {
        if (gameplayScreen.getState() == GameplayScreen.State.PAUSED) return;

        currentStarSpeedSlowMoTween.update(delta);
        radialVelocitySlowMoTween.update(delta);
        radialTween.update(delta);
        warpStretchFactorTweenStarBullet_SecondStage.update(delta);
        warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage.update(delta);

        //Gdx.app.log(TAG, "" + thetaForRadialTween);

        for (Star star : stars) {
            star.act(delta,
                    transitionVelocity,
                    currentStarSpeed,
                    radialVelocity,
                    inWarpTrailAnimation,
                    inWarpFastForwardAnimation,
                    warpFastForwardSpeed,
                    gameplayScreen);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            calculateTrailWarpStuff();
            inWarpTrailAnimation = !inWarpTrailAnimation;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SEMICOLON)) {
            glassCrackPostProcessingEffect.generateCrack();

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
        //if (gameplayScreen.getState() == GameplayScreen.State.PAUSED) return;

        //super.draw(batch, parentAlpha);
        batch.end();
        Texture starsFrameBufferTexture = renderStarsToOriginalFrameBuffer(batch);

        if (gameplayScreen.getGameplayMode() == GameplayMode.SURVIVAL) {


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
                fpss.add(1 / Gdx.graphics.getDeltaTime());



        } else if (gameplayScreen.getGameplayMode() == GameplayMode.CRYSTAL) {


            glassCrackPostProcessingEffect.draw(batch, starsFrameBufferTexture/*test*//*badlogic*/, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());


        } else {
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

    public void startCurrentStarSpeedSlowMoTween() {
        starsSpeedBeforeSlowMo = currentStarSpeed;
        currentStarSpeedSlowMoTween.setInitialVal(currentStarSpeed);
        currentStarSpeedSlowMoTween.start();
    }

    public void startRadialVelocitySlowMoTween() {
        radialVelocitySlowMoTween.setInitialVal(radialVelocity);
        radialVelocitySlowMoTween.setFinalVal(0);
        radialVelocitySlowMoTween.start();
    }

    public void updateCurrentStarSpeed(float bulletsPerAttack) {
        //this.currentStarSpeed = (D_SURVIVAL_BULLETS_MAX_NUMBER_PER_ATTACK /(float) bulletsPerAttack) * STARS_SPEED;
        setCurrentStarSpeed((D_SURVIVAL_BULLETS_MAX_NUMBER_PER_ATTACK /(float) bulletsPerAttack) * STARS_SPEED);
    }

    public void resetCurrentSpeed() {
        //this.currentStarSpeed = STARS_SPEED;
        setCurrentStarSpeed(STARS_SPEED);
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

    public MyTween getCurrentStarSpeedSlowMoTween() {
        return currentStarSpeedSlowMoTween;
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

    public float getRadialVelocity() {
        return radialVelocity;
    }

    public float getBaseRadialVelocity() {
        return baseRadialVelocity;
    }

    public void setBaseRadialVelocity(float baseRadialVelocity) {
        this.baseRadialVelocity = baseRadialVelocity;
        this.radialVelocity = baseRadialVelocity;
    }

    public MyTween getRadialVelocitySlowMoTween() {
        return radialVelocitySlowMoTween;
    }

    public void setGameplayScreen(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        gameplayScreen.addToFinishWhenStoppingTheGameplay(radialTween);

        gameplayScreen.addToFinishWhenStoppingTheGameplay(currentStarSpeedSlowMoTween);
        //gameplayScreen.addToResumeWhenResumingStarBullet(currentSpeedTweenStarBullet_FirstStage);

        gameplayScreen.addToFinishWhenStoppingTheGameplay(warpStretchFactorTweenStarBullet_SecondStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(warpStretchFactorTweenStarBullet_SecondStage);

        gameplayScreen.addToFinishWhenStoppingTheGameplay(warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage);
        //gameplayScreen.addToResumeWhenResumingStarBullet(warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage);

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

    public GlassCrackPostProcessingEffect getGlassCrackPostProcessingEffect() {
        return glassCrackPostProcessingEffect;
    }

    public void setCurrentStarSpeed(float currentStarSpeed) {
        this.currentStarSpeed = currentStarSpeed;
    }

    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------

    private void initializeCurrentStarSpeedSlowMoTween() {
        currentStarSpeedSlowMoTween = new MyTween(SLOW_MO_TWEENS_DURATION, SLOW_MO_TWEENS_INTERPOLATION, STARS_SPEED, 0) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                //currentStarSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
                setCurrentStarSpeed(myInterpolation.apply(startX, endX, startY, endY, currentX));
            }

            @Override
            public void onFinish() {
                super.onFinish();

                /*if (gameplayScreen.getState() == GameplayScreen.State.STOPPED)
                    resetCurrentSpeed();*/

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
                        radialVelocity = interpolation.apply(baseRadialVelocity, baseRadialVelocity+STARS_POLAR_TWEEN_THETA_MAX, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        radialVelocity = interpolation.apply(baseRadialVelocity, baseRadialVelocity-STARS_POLAR_TWEEN_THETA_MAX, percentage);
                } else { // interpolationOut
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        radialVelocity = interpolation.apply(baseRadialVelocity+STARS_POLAR_TWEEN_THETA_MAX, baseRadialVelocity, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        radialVelocity = interpolation.apply(baseRadialVelocity-STARS_POLAR_TWEEN_THETA_MAX, baseRadialVelocity, percentage);
                }
            }

            @Override
            public void onFinish() {
                radialVelocity = baseRadialVelocity;
                super.onFinish();
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(radialTween);
    }

    private void initializeRadialVelocitySlowMoTween() {
        radialVelocitySlowMoTween = new MyTween(SLOW_MO_TWEENS_DURATION, SLOW_MO_TWEENS_INTERPOLATION) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                //currentStarSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
                radialVelocity = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                /*if (gameplayScreen.getState() == GameplayScreen.State.STOPPED)
                    resetCurrentSpeed();*/

            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentSpeedTweenStarBullet_FirstStage);
    }

    private void initializeWarpStretchFactorTweenStarBullet_SecondStage() {
        warpStretchFactorTweenStarBullet_SecondStage = new Tween(STAR_BULLET_SECOND_STAGE_DURATION, STAR_BULLET_SECOND_STAGE_INTERPOLATION) {



            @Override
            public void tween(float percentage, Interpolation interpolation) {
                trailWarpPostProcessingEffect.setWarpStretchFactor(interpolation.apply(percentage));
                //Gdx.app.log(StarsContainer.this.TAG, "percentage = " + percentage);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                trailWarpPostProcessingEffect.setWarpStretchFactor(0);
                //Gdx.app.log(TAG, "finished.");
            }
        };

        //gameplayScreen.addToPauseWhenPausingFinishWhenLosing(warpStretchFactorTweenStarBullet_SecondStage);
    }

    private void initializeWarpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage() {
        warpFastForwardSpeedAndCurrentStarSpeedTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                warpFastForwardSpeed = interpolation.apply(1, 0, percentage);
                //currentStarSpeed = interpolation.apply(0, starsSpeedBeforeStarBullet, percentage);
                setCurrentStarSpeed(interpolation.apply(0, starsSpeedBeforeSlowMo, percentage));
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
