package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.MyEngine.PostProcessingEffect;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class PortalPostProcessingEffect extends PostProcessingEffect implements Resizable {

    public static final int MAX_PORTAL_POINTS = 3;

    private ShaderProgram portalShader;

    private Matrix3 transToWorldCoordsMat;
    private Matrix3 transToWorldCoordsMatInv;
    private float worldWidth, worldHeight;


    private int lastUsedIndex = -1;
    private float[] portalPointsX;
    private float[] portalPointsY;
    private float[] portalPointsIntensities;

    public PortalPostProcessingEffect() {
        initializePortalShader();

        transToWorldCoordsMat = new Matrix3();
        transToWorldCoordsMatInv = new Matrix3();


        initializePortalPoints();

    }

    public int addPortalPoint(float x, float y, float intensity) {
        lastUsedIndex++;
        portalPointsX[lastUsedIndex] = x;
        portalPointsY[lastUsedIndex] = y;
        portalPointsIntensities[lastUsedIndex] = intensity;
        return lastUsedIndex;
    }

    public void removePortalPoint(int i) {
        if (i > lastUsedIndex)
            throw new ArrayIndexOutOfBoundsException("lastUsedIndex = " + lastUsedIndex + ", i = " + i);

        // Lazy delete (IMPORTANT)
        portalPointsX[i] = -1;
        portalPointsY[i] = -1;
        portalPointsIntensities[i] = -1;
    }

    public void setPortalPoint(int i, float x, float y, float intensity) {
        if (i > lastUsedIndex)
            throw new ArrayIndexOutOfBoundsException("lastUsedIndex = " + lastUsedIndex + ", i = " + i);

        portalPointsX[i] = x;
        portalPointsY[i] = y;
        portalPointsIntensities[i] = intensity;
    }

    public void setPortalPointPosition(int i, float x, float y) {
        if (i > lastUsedIndex)
            throw new ArrayIndexOutOfBoundsException("lastUsedIndex = " + lastUsedIndex + ", i = " + i);

        portalPointsX[i] = x;
        portalPointsY[i] = y;
    }

    public void setPortalPointIntensity(int i, float intensity) {
        if (i > lastUsedIndex)
            throw new ArrayIndexOutOfBoundsException("lastUsedIndex = " + lastUsedIndex + ", i = " + i);

        portalPointsIntensities[i] = intensity;
    }

    public void clearPortalPoints() {
        for (int i = 0; i < lastUsedIndex; i++) {
            portalPointsX[i] = -1;
            portalPointsY[i] = -1;
            portalPointsIntensities[i] = -1;
        }
        lastUsedIndex = -1;
    }

    public int getLastUsedIndex() {
        return lastUsedIndex;
    }

    @Override
    public void draw(Batch batch, TextureRegion inputTextureRegion, float x, float y, float width, float height) {
        batch.begin();
        batch.setShader(portalShader);

        portalShader.setUniformMatrix("u_transToWorldCoordsMat", transToWorldCoordsMat);
        portalShader.setUniformMatrix("u_transToWorldCoordsMatInv", transToWorldCoordsMatInv);
        portalShader.setUniformf("u_worldSize", worldWidth, worldHeight);
        portalShader.setUniformf("u_portalRadius", PORTALS_ENTRANCE_EXIT_DIAMETER * 5f/8f);
        portalShader.setUniform1fv("u_portalPointsWorldCoordinatesX", portalPointsX, 0, portalPointsX.length);
        portalShader.setUniform1fv("u_portalPointsWorldCoordinatesY", portalPointsY, 0, portalPointsY.length);
        portalShader.setUniform1fv("u_portalPointsIntensities", portalPointsIntensities, 0, portalPointsIntensities.length);

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

        batch.end();
        batch.setShader(null);
        batch.begin();
    }

    @Override
    public void dispose() {
        portalShader.dispose();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        transToWorldCoordsMat.idt();
        transToWorldCoordsMat.val[Matrix3.M00] = (worldWidth/worldHeight) * WORLD_SIZE;
        transToWorldCoordsMat.val[Matrix3.M11] = WORLD_SIZE;

        transToWorldCoordsMatInv.set(transToWorldCoordsMat);
        transToWorldCoordsMatInv.inv();
    }





    private void initializePortalShader() {
        String fragmentShader = Gdx.files.internal("Portal.fs.glsl").readString();

        portalShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!portalShader.isCompiled())
            Gdx.app.error("Shader Compile Error : ", portalShader.getLog());
    }

    private void initializePortalPoints() {
        portalPointsX = new float[MAX_PORTAL_POINTS];
        portalPointsY = new float[MAX_PORTAL_POINTS];
        portalPointsIntensities = new float[MAX_PORTAL_POINTS];

        for (int i = 0; i < portalPointsX.length; i++) {
            portalPointsX[i] = -1;
            portalPointsY[i] = -1;
            portalPointsIntensities[i] = -1;
        }
    }
}
