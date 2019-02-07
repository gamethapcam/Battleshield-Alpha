package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class RoundedArch extends Arch {

    private Arch semiCircle0;
    private Arch semiCircle1;

    /**
     * @param region A picture of a filled semiCircle0.
     */
    public RoundedArch(TextureRegion region, AngleIncreaseDirection angleIncreaseDirection, float radius) {
        super(region, angleIncreaseDirection, radius);

        semiCircle0 = new Arch(region, AngleIncreaseDirection.CLOCKWISE, radius);
        semiCircle1 = new Arch(region, AngleIncreaseDirection.CLOCKWISE, radius);
        addActor(semiCircle0);
        addActor(semiCircle1);
        semiCircle0.setAngle(MathUtils.PI);
        semiCircle1.setAngle(MathUtils.PI);

        semiCircle0.setRotation(180);

        /*semiCircle0.setDebug(true);
        semiCircle1.setDebug(true);*/

        calculateCirclesDimensions();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        calculateCirclesDimensions();
    }

    private void calculateCirclesDimensions() {
        if (semiCircle0 == null | semiCircle1 == null) return;

        Arch[] semiCircles = {semiCircle0, semiCircle1};

        float diameter = getRadius() - getInnerRadius();
        for (int i = 0; i < semiCircles.length; i++) {
            semiCircles[i].setSize(diameter, diameter);
            semiCircles[i].setOrigin(Align.center);
        }

        float originPolar_r = (getRadius() + getInnerRadius())/2f;

        if (getAngleIncreaseDirection() == AngleIncreaseDirection.CLOCKWISE) {
            semiCircle0.setPosition(getRadius() - semiCircle0.getRadius(), getHeight() - semiCircle0.getHeight());

            semiCircle1.setPosition(getRadius() + originPolar_r * MathUtils.sin(getAngle()) - semiCircle1.getRadius(),
                    getRadius() + originPolar_r * MathUtils.cos(getAngle()) - semiCircle1.getRadius());

            semiCircle1.setRotation(-getAngle() * MathUtils.radDeg);
        } else {
            semiCircle0.setPosition(getWidth() - semiCircle0.getWidth(), getHeight()/2f - semiCircle0.getRadius());
            semiCircle0.setRotation(-90);

            semiCircle1.setPosition(getRadius() + originPolar_r * MathUtils.cos(getAngle()) - semiCircle1.getRadius(),
                    getRadius() + originPolar_r * MathUtils.sin(getAngle()) - semiCircle1.getRadius());

            semiCircle1.setRotation(getAngle() * MathUtils.radDeg);
            semiCircle1.rotateBy(90);
        }
    }

    @Override
    protected void innerRadiusChanged() {
        calculateCirclesDimensions();
    }

    @Override
    public void setAngle(float angle) {
        super.setAngle(angle);

        calculateCirclesDimensions();
    }

    /*public Arch getSemiCircle0() {
        return semiCircle0;
    }*/
}