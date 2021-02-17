package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.MyEngine.MyFrameBuffer;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.PostProcessingEffect;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

public class TrailWarpPostProcessingEffect extends PostProcessingEffect {

    private ShaderProgram gaussianBlurShader;
    private ShaderProgram bloomStretchShader;


    private MyFrameBuffer hBlurFrameBuffer;
    private MyFrameBuffer vBlurFrameBuffer;
    private int kernelSize;
    private boolean blurBuffersCleared = false;

    private float warpStretchFactor = 0;

    private TextureRegion _temp;

    private Viewport viewport;
    private float inputTextureWidthWorldUnits;
    private float inputTextureHeightWorldUnits;
    private float blurResDivisor;

    public TrailWarpPostProcessingEffect(Viewport viewport, float inputTextureWidthWorldUnits, float inputTextureHeightWorldUnits, float blurResDivisor, int kernelSize) {

        int inputTextureWidthPixelUnits = MyMath.toScreenCoordinates(inputTextureWidthWorldUnits, MyMath.Dimension.X, viewport);
        int inputTextureHeightPixelUnits = MyMath.toScreenCoordinates(inputTextureHeightWorldUnits, MyMath.Dimension.Y, viewport);

        hBlurFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, (int)(inputTextureWidthPixelUnits / blurResDivisor), (int)(inputTextureHeightPixelUnits / blurResDivisor), false);
        vBlurFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, (int)(inputTextureWidthPixelUnits / blurResDivisor), (int)(inputTextureHeightPixelUnits / blurResDivisor), false);
        this.kernelSize = kernelSize;



        initializeStretchWarpShader();
        initializeGaussianBlurShader();

        _temp = new TextureRegion();

        this.viewport = viewport;
        this.inputTextureWidthWorldUnits = inputTextureWidthWorldUnits;
        this.inputTextureHeightWorldUnits = inputTextureHeightWorldUnits;
        this.blurResDivisor = blurResDivisor;
    }

    // ------------------------ super class methods ------------------------
    // ------------------------ super class methods ------------------------
    // ------------------------ super class methods ------------------------

    @Override
    public void draw(Batch batch, TextureRegion inputTextureRegion, float x, float y, float width, float height) {
        Texture blurFrameBufferTexture = renderToBlurFrameBuffers(batch, inputTextureRegion);

        batch.begin();
        drawBloomStretch(batch, inputTextureRegion, blurFrameBufferTexture, x, y, width, height);
    }

    @Override
    public void dispose() {
        gaussianBlurShader.dispose();
        bloomStretchShader.dispose();

        hBlurFrameBuffer.dispose();
        vBlurFrameBuffer.dispose();
    }

    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------
    // ------------------------ methods ------------------------

    private Texture renderToBlurFrameBuffers(Batch batch, TextureRegion originalFrameBufferTexture) {

        blurBuffersCleared = false;

        Texture hBlurFrameBufferTexture = gaussianBlurSinglePass(batch, hBlurFrameBuffer, originalFrameBufferTexture, kernelSize, true);
        hBlurFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        _temp.setTexture(hBlurFrameBufferTexture);
        _temp.setU2(1);
        _temp.setV2(1);

        Texture blurFrameBufferTexture = gaussianBlurSinglePass(batch, vBlurFrameBuffer, _temp, kernelSize, false);
        blurFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        return blurFrameBufferTexture;
    }

    private Texture gaussianBlurSinglePass(Batch batch, MyFrameBuffer out, TextureRegion in, int kernelSize, boolean horizontalPass) {
        boolean batchWasDrawing = batch.isDrawing();
        if (batchWasDrawing)
            batch.end();

        out.begin();

        batch.begin();

        batch.setShader(gaussianBlurShader);
        gaussianBlurShader.setUniformf("sizeInPixelUnits", out.getWidth(), out.getHeight());
        gaussianBlurShader.setUniformi("horizontalPass", horizontalPass ? 1 : 0);
        gaussianBlurShader.setUniformi("kernelSize", kernelSize);


        //Gdx.gl.glClearColor(1, 1, 1, 0f);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //batch.draw(badlogic, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        batch.draw(in.getTexture(),
                0,
                0,
                inputTextureWidthWorldUnits,
                inputTextureHeightWorldUnits,
                in.getRegionX(),
                in.getRegionY(),
                in.getRegionWidth(),
                in.getRegionHeight(),
                false,
                true);

        batch.end();

        if (batchWasDrawing)
            batch.begin();

        out.end();

        return out.getColorBufferTexture();
    }

    private void drawBloomStretch(Batch batch, TextureRegion inputTextureRegion, Texture blurredStars, float x, float y, float width, float height) {
        batch.setShader(bloomStretchShader);

        bloomStretchShader.setUniformf("sizeInPixelUnits", inputTextureRegion.getRegionWidth(), inputTextureRegion.getRegionHeight());

        blurredStars.bind(1);
        bloomStretchShader.setUniformi("u_textureBlurred", 1);
        bloomStretchShader.setUniformf("warpStretchFactor", warpStretchFactor);

        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        batch.draw(inputTextureRegion.getTexture(),
                x,
                y,
                width,
                height,
                inputTextureRegion.getRegionX(),
                inputTextureRegion.getRegionY(),
                inputTextureRegion.getRegionWidth(),
                inputTextureRegion.getRegionHeight(),
                false,
                true);

        batch.setShader(null);
    }

    public void clearBlurBuffers(Batch batch) {

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

    // ------------------------ getters and setters ------------------------
    // ------------------------ getters and setters ------------------------
    // ------------------------ getters and setters ------------------------

    public float getWarpStretchFactor() {
        return warpStretchFactor;
    }

    public void setWarpStretchFactor(float warpStretchFactor) {
        this.warpStretchFactor = warpStretchFactor;
    }

    public int getKernelSize() {
        return kernelSize;
    }

    public void setKernelSize(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    public float getInputTextureWidthWorldUnits() {
        return inputTextureWidthWorldUnits;
    }

    public float getInputTextureHeightWorldUnits() {
        return inputTextureHeightWorldUnits;
    }

    public void setInputTextureWidthWorldUnits(float inputTextureWidthWorldUnits) {
        this.inputTextureWidthWorldUnits = inputTextureWidthWorldUnits;
    }

    public void setInputTextureHeightWorldUnits(float inputTextureHeightWorldUnits) {
        this.inputTextureHeightWorldUnits = inputTextureHeightWorldUnits;
    }

    public float getBlurResDivisor() {
        return blurResDivisor;
    }

    public boolean isBlurBuffersCleared() {
        return blurBuffersCleared;
    }

    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------
    // ------------------------ initializers ------------------------

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
}
