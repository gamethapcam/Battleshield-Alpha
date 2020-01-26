package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsHandler;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
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
    private FrameBuffer hBlurFrameBuffer;
    private FrameBuffer vBlurFrameBuffer;
    private boolean blurBuffersCleared = false;

    //private final Vector2 distanceOffset = new Vector2(Wo)
    private Vector2 transitionVelocity = new Vector2(0, 0);
    private Vector2 transitionVelocityMax = new Vector2(0, 0);

    private float currentStarSpeed;
    private MyTween currentSpeedTweenStarBullet;

    private boolean inWarpTrailAnimation = false;
    private boolean inWarpFastForwardAnimation = false;

    private ShaderProgram gaussianBlurShader;
    private ShaderProgram bloomStretchShader;
    private float warpStretchFactor = 0;
    private MyTween warpStretchFactorTweenStarBullet;

    public static final int KERNEL_SIZE = 11;

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
            Star star = new Star(this, starRegion, viewport, i);
            stars.add(star);
        }

        currentStarSpeed = STARS_SPEED;

        originalFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (viewport.getScreenWidth()), (viewport.getScreenHeight()), false);
        hBlurFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(viewport.getScreenWidth()/6f), (int)(viewport.getScreenHeight()/6f), false);
        vBlurFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)(viewport.getScreenWidth()/6f), (int)(viewport.getScreenHeight()/6f), false);

        initializeCurrentSpeedTween();

        initializeRadialTween();

        /*originalFrameBuffer.begin();
        getBatch().begin();
        for (Star star : stars) {
            star.draw(getBatch(), 1.0f);
        }

        getBatch().end();
        originalFrameBuffer.end();
        originalFrameBuffer.dispose();*/

        initializeStretchWarpShader();
        initializeGaussianBlurShader();
        initializeWarpStretchFactorTweenStarBullet();

        badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));
        badlogic.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fpss = new Array<>(Float.class);
    }

    @Override
    public void act(float delta) {
        currentSpeedTweenStarBullet.update(delta);
        radialTween.update(delta);
        warpStretchFactorTweenStarBullet.update(delta);

        for (Star star : stars) {
            star.act(delta,
                    transitionVelocity,
                    currentStarSpeed,
                    thetaForRadialTween,
                    inWarpTrailAnimation,
                    inWarpFastForwardAnimation,
                    warpStretchFactor,
                    gameplayScreen);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            calculateWarpStuff();
            inWarpTrailAnimation = !inWarpTrailAnimation;
        }

        //Gdx.app.log(TAG, "" + (int) (Gdx.input.getX()/(float)Gdx.graphics.getWidth() * KERNEL_SIZE));

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            //currentSpeedTweenStarBullet.start();
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
            Texture blurFrameBufferTexture = renderToBlurFrameBuffers(batch, starsFrameBufferTexture);

            batch.begin();
            drawBloomStretch(batch, starsFrameBufferTexture, blurFrameBufferTexture);

        } else {
            if (!blurBuffersCleared)
                clearBlurBuffers(batch);

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
            batch.setShader(null);


        if (inWarpTrailAnimation)
            fpss.add(1/Gdx.graphics.getDeltaTime());

    }


    public void resize(float wWidth, float wHeight) {
        for (Star star : stars) {
            star.resize(wWidth, wHeight);
        }
    }

    @Override
    public void dispose() {
        originalFrameBuffer.dispose();
        hBlurFrameBuffer.dispose();
        vBlurFrameBuffer.dispose();
    }

    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------

    private void calculateWarpStuff() {
        for (Star star : stars) {
            star.calculateWarpStuff();
            star.startTrailWarpTween();
        }
    }

    public void startCurrentSpeedTweenStarBullet() {
        currentSpeedTweenStarBullet.setInitialVal(currentStarSpeed);
        currentSpeedTweenStarBullet.start();
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
            star.draw(batch, Gdx.graphics.getDeltaTime(), inWarpTrailAnimation);
        }

        batch.end();

        originalFrameBuffer.end();

        Texture starsFrameBuffer = originalFrameBuffer.getColorBufferTexture();
        starsFrameBuffer.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return starsFrameBuffer;
    }

    private Texture renderToBlurFrameBuffers(Batch batch, Texture originalFrameBufferTexture) {

        blurBuffersCleared = false;

        Texture hBlurFrameBufferTexture = gaussianBlurSinglePass(batch, hBlurFrameBuffer, originalFrameBufferTexture, KERNEL_SIZE, true);
        hBlurFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture blurFrameBufferTexture = gaussianBlurSinglePass(batch, vBlurFrameBuffer, hBlurFrameBufferTexture/*originalFrameBufferTexture*/, KERNEL_SIZE, false);
        blurFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return blurFrameBufferTexture;
    }

    private Texture gaussianBlurSinglePass(Batch batch, FrameBuffer out, Texture in, int kernelSize, boolean horizontalPass) {
        boolean batchWasDrawing = batch.isDrawing();
        if (batchWasDrawing)
            batch.end();

        out.begin();

        batch.begin();

        batch.setShader(gaussianBlurShader);
        gaussianBlurShader.setUniformf("worldSizeInPixelCoordinates", out.getWidth(), out.getHeight());
        gaussianBlurShader.setUniformi("horizontalPass", horizontalPass ? 1 : 0);
        gaussianBlurShader.setUniformi("kernelSize", kernelSize);


        //Gdx.gl.glClearColor(1, 1, 1, 0f);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //batch.draw(badlogic, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        batch.draw(in,
                0,
                0,
                viewport.getWorldWidth(),
                viewport.getWorldHeight(),
                0,
                0,
                in.getWidth(),
                in.getHeight(),
                false,
                true);

        batch.end();

        if (batchWasDrawing)
            batch.begin();

        out.end();
        
        return out.getColorBufferTexture();
    }

    private void drawBloomStretch(Batch batch, Texture originalStars, Texture blurredStars) {
        batch.setShader(bloomStretchShader);

        bloomStretchShader.setUniformf("worldSizeInPixelCoordinates", originalStars.getWidth(), originalStars.getHeight());

        blurredStars.bind(1);
        bloomStretchShader.setUniformi("u_textureBlurred", 1);
        bloomStretchShader.setUniformf("warpStretchFactor", warpStretchFactor);

        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        batch.draw(originalStars,
                0,
                0,
                viewport.getWorldWidth(),
                viewport.getWorldHeight(),
                0,
                0,
                originalStars.getWidth(),
                originalStars.getHeight(),
                false,
                true);
    }

    private void clearBlurBuffers(Batch batch) {

        hBlurFrameBuffer.begin();
        batch.begin();

        Gdx.gl20.glClearColor(1, 1, 1,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.end();
        hBlurFrameBuffer.end();




        vBlurFrameBuffer.begin();
        batch.begin();

        Gdx.gl20.glClearColor(1, 1, 1,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.end();
        vBlurFrameBuffer.end();




        blurBuffersCleared = true;
    }

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
            calculateWarpStuff();
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
        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(currentSpeedTweenStarBullet);
    }

    public MyTween getWarpStretchFactorTweenStarBullet() {
        return warpStretchFactorTweenStarBullet;
    }

    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------

    private void initializeCurrentSpeedTween() {
        currentSpeedTweenStarBullet = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION, STARS_SPEED, 0) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                currentStarSpeed = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };
    }

    private void initializeRadialTween() {
        radialTween = new RadialTween(STARS_RADIAL_TWEEN_DURATION, exp10) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (percentage < 0.5f) { // interpolationIn
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolation.apply(0, STARS_RADIAL_TWEEN_THETA_MAX, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolation.apply(0, -STARS_RADIAL_TWEEN_THETA_MAX, percentage);
                } else { // interpolationOut
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolation.apply(STARS_RADIAL_TWEEN_THETA_MAX, 0, percentage);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolation.apply(-STARS_RADIAL_TWEEN_THETA_MAX, 0, percentage);
                }
            }

            @Override
            public void onFinish() {
                thetaForRadialTween = 0;
                super.onFinish();
            }
        };
    }

    private void initializeStretchWarpShader() {

        String fragmentShader = Gdx.files.internal("BloomStretch.fs.glsl").readString();

        bloomStretchShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!bloomStretchShader.isCompiled())
            Gdx.app.error("Shader Compile Error : ", bloomStretchShader.getLog());


    }

    private void initializeGaussianBlurShader() {

        String fragmentShader = Gdx.files.internal("GaussianBlur.fs.glsl").readString();

        gaussianBlurShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!gaussianBlurShader.isCompiled())
            Gdx.app.error("Shader Compile Error : ", gaussianBlurShader.getLog());


    }

    private void initializeWarpStretchFactorTweenStarBullet() {
        warpStretchFactorTweenStarBullet = new MyTween(STAR_BULLET_SECOND_STAGE_DURATION,
                STAR_BULLET_SECOND_STAGE_INTERPOLATION,
                0,
                1) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                warpStretchFactor = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };
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
