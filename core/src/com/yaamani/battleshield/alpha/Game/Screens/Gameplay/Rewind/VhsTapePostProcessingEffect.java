package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.MyEngine.PostProcessingEffect;

public class VhsTapePostProcessingEffect extends PostProcessingEffect {

    private ShaderProgram vhsTapeShader;

    public VhsTapePostProcessingEffect() {
        initializeVhsTapeShader();
    }

    @Override
    public void draw(Batch batch, TextureRegion inputTextureRegion, float x, float y, float width, float height) {
        batch.begin();
        batch.setShader(vhsTapeShader);

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
    }

    @Override
    public void dispose() {
        vhsTapeShader.dispose();
    }

    private void initializeVhsTapeShader() {
        String fragmentShader = Gdx.files.internal("VhsTape.fs.glsl").readString();

        vhsTapeShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!vhsTapeShader.isCompiled())
            Gdx.app.error("Shader Compile Error : ", vhsTapeShader.getLog());

    }
}
