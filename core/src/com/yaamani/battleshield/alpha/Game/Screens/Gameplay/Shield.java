package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.RoundedArch;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Shield extends RoundedArch {

    public static final String TAG = Shield.class.getSimpleName();

    private float T, L0, phi;

    private boolean on = false;

    public Shield(/*TextureRegion region, float radius, float innerRadius, */BulletsAndShieldContainer bulletsAndShieldContainer) {
        super(Assets.instance.gameplayAssets.gameOverBG,
                AngleIncreaseDirection.THE_POSITIVE_DIRECTION_OF_THE_X_AXIS,
                SHIELDS_RADIUS);

        setInnerRadius(SHIELDS_INNER_RADIUS);
        bulletsAndShieldContainer.addActor(this);

        setPosition(-getRadius(), -getRadius());

        setOrigin(Align.center);
        //setDebug(true);

        //setAngle(90*MathUtils.degRad);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        updateT_L0_phi();
    }

    @Override
    protected void innerRadiusChanged() {
        updateT_L0_phi();
    }

    private void updateT_L0_phi() {
        T = (getRadius() + getInnerRadius())/2f - getInnerRadius();
        L0 = 0.5f * T;
        phi = L0/(getInnerRadius()+L0);
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on) {
            setColor(1, 1, 1, 1);
            setX(-getRadius() + SHIELDS_ON_DISPLACEMENT);
        }
        else {
            setColor(1, 1, 1, 0.5f);
            setX(-getRadius());
        }
    }

    public boolean isOn() {
        return on;
    }

    public float getOmegaDeg() {
        //updateT_L0_phi();
        return (getAngle() + 2*phi) * MathUtils.radDeg;
    }

    public void setOmegaDeg(float omegaDeg) {
        //updateT_L0_phi();
        setAngle(omegaDeg*MathUtils.degRad - SHIELDS_OMEGA_SETTER_PHI_MULTIPLIER*phi);

        //Gdx.app.log(TAG, "angleDeg = " + getAngle()*MathUtils.radDeg);

        //changeSize(this.omegaDeg);

        setRotation(-getAngle()*MathUtils.radDeg/2f);
    }
}
