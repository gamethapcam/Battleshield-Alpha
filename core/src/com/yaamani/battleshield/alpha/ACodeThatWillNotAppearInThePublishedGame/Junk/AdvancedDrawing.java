package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class AdvancedDrawing {

    private ShaderProgram maskShader;

    public void drawMasked(Batch batch, TextureRegion region,
                           float x, float y,
                           float originX, float originY,
                           float width, float height,
                           float scaleX, float scaleY,
                           float rotation, Texture mask) {

        if (maskShader == null) maskShader = createMaskShader();
        batch.setShader(maskShader);
        mask.bind(1);
        maskShader.setUniformi("u_mask", 1);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
        batch.setShader(null);
    }

    private ShaderProgram createMaskShader () {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";

        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n"
                + "uniform sampler2D u_mask; // A grayscale texture. White pixels will be visible in the original texture and the black pixels will be completely transparent.\n" //
                + "void main()\n"//
                + "{\n"
                + "   vec4 color = vec4(texture2D(u_texture, v_texCoords).rgb, texture2D(u_mask, v_texCoords).r); // It's a grayscale texture. Which means that red, green and blue values are equal. I just used the red.\n" //
                + "   gl_FragColor = v_color * color;\n" //
                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    public void dispose() {
        if (maskShader !=null) maskShader.dispose();
    }
}
