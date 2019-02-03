package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

public class Arch extends Actor {

    public static final String TAG = Arch.class.getSimpleName();

    private TextureRegion region;

    private float angle;
    private float innerRadius;
    private AngleIncreaseDirection angleIncreaseDirection;

    private ShaderProgram shaderProgram;

    /**
     *
     * @param region A picture of a filled circle.
     */
    public Arch(TextureRegion region, AngleIncreaseDirection angleIncreaseDirection) {
        this.region = region;
        this.angle = 360 * MathUtils.degRad;
        innerRadius = 0;
        this.angleIncreaseDirection = angleIncreaseDirection;

        setBounds(region.getRegionX(),
                region.getRegionY(),
                region.getRegionWidth(),
                region.getRegionHeight());

        String defaultVertexShader =
                "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +

                "uniform mat4 u_projTrans;\n" +

                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +

                "void main()\n" +
                "{\n" +
                "   v_color = a_color;\n" +
                "   v_texCoords = a_texCoord0;\n" +
                "   gl_Position =  u_projTrans * a_position;\n" +
                "}\n";

        String myFragmentShader = Gdx.files.internal("Arch.fs.glsl").readString()
                /*"#define PI 3.1415926535897932384626433832795\n" +
                        "\n" +
                        "varying vec4 v_color;\n" +
                        "varying vec2 v_texCoords;\n" +
                        "\n" +
                        "uniform sampler2D u_texture;\n" +
                        "uniform int u_angleIncreaseDirection;\n" +
                        "uniform float u_angle;\n" +
                        "uniform float u_innerRadius;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    vec4 color;\n" +
                        "    float currentAngle;\n" +
                        "    if (u_angleIncreaseDirection == 0)\n" +
                        "        currentAngle = atan(v_texCoords.y - 0.5, 0.5 - v_texCoords.x);\n" +
                        "    else\n" +
                        "        currentAngle = atan(0.5 - v_texCoords.x, v_texCoords.y - 0.5);\n" +
                        "\n" +
                        "    float r;\n" +
                        "    if (u_innerRadius > 0.0) {\n" +
                        "        vec2 texCoord = vec2(v_texCoords.x - 0.5, 0.5 - v_texCoords.y);\n" +
                        "        r = sqrt(texCoord.x*texCoord.x + texCoord.y*texCoord.y);\n" +
                        "    }\n" +
                        "\n" +
                        "    if (currentAngle + PI >= u_angle || r < u_innerRadius) color = vec4(1.0, 1.0, 1.0, 0.0);\n" +
                        "    else color = texture2D(u_texture, v_texCoords);" +
                        "    gl_FragColor = v_color * color;\n" +
                        "}"*/;

        shaderProgram = new ShaderProgram(defaultVertexShader, myFragmentShader);

        if (!shaderProgram.isCompiled())
            Gdx.app.error("Shader Compile Error : ", shaderProgram.getLog());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.setShader(shaderProgram);

        shaderProgram.setUniformi("u_angleIncreaseDirection", AngleIncreaseDirection.getIntValue(angleIncreaseDirection));
        shaderProgram.setUniformf("u_angle", angle);
        shaderProgram.setUniformf("u_innerRadius", innerRadius);

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());


        batch.setShader(null);
    }

    public float getAngle() {
        return angle;
    }

    /**
     *
     * @param angle In radians.
     */
    public void setAngle(float angle) {
        this.angle = MathUtils.clamp(angle, 0, MathUtils.PI2);
        Gdx.app.log(TAG, "angle = " + this.angle);
    }

    public void setInnerRadius(float innerRadius) {
        if (innerRadius >= getWidth()/2f)
            throw new ValueOutOfRangeException("innerRadius can't be greater than or equal (arch.getWidth/2f)");
        this.innerRadius = innerRadius/getWidth();
    }

    public float getInnerRadius() {
        return innerRadius*getWidth();
    }

    public TextureRegion getRegion() {
        return region;
    }

    public AngleIncreaseDirection getAngleIncreaseDirection() {
        return angleIncreaseDirection;
    }

    public void setAngleIncreaseDirection(AngleIncreaseDirection angleIncreaseDirection) {
        this.angleIncreaseDirection = angleIncreaseDirection;
    }


    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------


    public enum AngleIncreaseDirection {
        THE_POSITIVE_DIRECTION_OF_THE_X_AXIS,
        CLOCKWISE;

        public static int getIntValue(AngleIncreaseDirection angleIncreaseDirection) {
            if (angleIncreaseDirection == THE_POSITIVE_DIRECTION_OF_THE_X_AXIS) return 0;
            else return 1;
        }
    }
}
