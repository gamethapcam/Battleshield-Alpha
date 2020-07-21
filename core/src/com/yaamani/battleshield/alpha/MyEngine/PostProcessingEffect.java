package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public abstract class PostProcessingEffect implements Disposable {

    private TextureRegion _temp;

    public PostProcessingEffect() {
        _temp = new TextureRegion();
    }

    public void draw(Batch batch, Texture inputTexture, float x, float y, float width, float height) {
        _temp.setTexture(inputTexture);
        _temp.setU2(1);
        _temp.setV2(1);

        draw(batch, _temp, x, y, width, height);
    }

    public abstract void draw(Batch batch, TextureRegion inputTextureRegion, float x, float y, float width, float height);

}
