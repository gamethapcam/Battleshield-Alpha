package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Arch extends Group {

    public static final String TAG = Arch.class.getSimpleName();

    private TextureRegion region;

    private float angle;
    private float innerRadius;
    private float u_innerRadiusRatio;
    private AngleIncreaseDirection angleIncreaseDirection;

    private ShaderProgram shaderProgram;

    private float u_regionCenterX;
    private float u_regionCenterY;

    private float u_radius;

    /**
     *
     * @param region A picture of a filled circle.
     */
    public Arch(TextureRegion region, AngleIncreaseDirection angleIncreaseDirection, float radius) {
        setRegion(region);
        this.angle = 360 * MathUtils.degRad;
        innerRadius = 0;
        this.angleIncreaseDirection = angleIncreaseDirection;
        setRadius(radius);

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

        String myFragmentShader = Gdx.files.internal("Arch.fs.glsl").readString();

        shaderProgram = new ShaderProgram(defaultVertexShader, myFragmentShader);

        if (!shaderProgram.isCompiled())
            Gdx.app.error("Shader Compile Error : ", shaderProgram.getLog());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        super.draw(batch, parentAlpha);

        batch.setShader(shaderProgram);

        shaderProgram.setUniformi("u_angleIncreaseDirection", AngleIncreaseDirection.getIntValue(angleIncreaseDirection));
        shaderProgram.setUniformf("u_angle", angle);
        shaderProgram.setUniformf("u_radius", u_radius);
        shaderProgram.setUniformf("u_innerRadiusRatio", u_innerRadiusRatio);
        shaderProgram.setUniformf("u_regionCenterX", u_regionCenterX);
        shaderProgram.setUniformf("u_regionCenterY", u_regionCenterY);

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
        //Gdx.app.log(TAG, "angle = " + this.angle);
    }

    public void setInnerRadius(float innerRadius) {

        this.innerRadius = innerRadius;
        this.u_innerRadiusRatio = innerRadius/getWidth()/2f;

        checkInnerRadiusValidity();

        innerRadiusChanged();
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    protected void innerRadiusChanged() {

    }

    @Override
    protected void sizeChanged() {
        u_radius = (float) region.getRegionWidth()/(float) region.getTexture().getWidth()/2f;

        checkInnerRadiusValidity();
    }

    private void checkInnerRadiusValidity() {
        if (innerRadius >= getWidth()/2f)
            throw new ValueOutOfRangeException("innerRadius can't be greater than or equal the radius");
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

    public void setRegion(TextureRegion region) {
        this.region = region;

        float centerX = (region.getRegionWidth() + region.getRegionX()*2) / 2f;
        float centerY = (region.getRegionHeight() + region.getRegionY()*2) / 2f;

        u_regionCenterX = centerX / region.getTexture().getWidth(); //ratio
        u_regionCenterY = centerY / region.getTexture().getHeight(); //ratio
    }

    /**
     * setSize(radius*2, radius*2);
     * @param radius
     */
    public void setRadius(float radius) {
        setSize(radius*2, radius*2);
    }

    /**
     * setBounds(x, y, radius*2, radius*2);
     * @param x
     * @param y
     * @param radius
     */
    public void setBounds(float x, float y, float radius) {
        setBounds(x, y, radius*2, radius*2);
    }

    /**
     *
     * @return getWidth()/2f;
     */
    public float getRadius() {
        return getWidth()/2f;
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
