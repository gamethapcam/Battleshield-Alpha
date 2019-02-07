package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.resolutionIntoWorldUnits;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Shield extends Actor {

    public static final String TAG = Shield.class.getSimpleName();

    private float omegaDeg;
    private TextureRegion currentRegion;
    private boolean on = false;

    public Shield(BulletsAndShieldContainer bulletsAndShieldContainer) {
        this(90, bulletsAndShieldContainer);
    }

    public Shield(float omegaDeg, BulletsAndShieldContainer bulletsAndShieldContainer) {
        setOmegaDeg(omegaDeg);
        setRotation(-90);
        //setDebug(true);
        bulletsAndShieldContainer.addActor(this);

        //getColor().a = .25f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (on)
            batch.draw(currentRegion, getX() + SHIELDS_ON_DISPLACEMENT, getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        else batch.draw(currentRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

    }

    public float getOmegaDeg() {
        return omegaDeg;
    }

    public void setOmegaDeg(float omegaDeg) {
        float correctedOmega = MathUtils.clamp(omegaDeg, SHIELDS_SAVING_FROM_ANGLE, SHIELDS_SAVING_TO_ANGLE);

        int index = Math.round((correctedOmega - SHIELDS_SAVING_FROM_ANGLE)/SHIELDS_SKIP_ANGLE_WHEN_SAVING);
        this.omegaDeg = index*SHIELDS_SKIP_ANGLE_WHEN_SAVING + SHIELDS_SAVING_FROM_ANGLE;

        currentRegion = Assets.instance.gameplayAssets.shieldsWithVariousAngles[index];

        changeSize(this.omegaDeg);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        if (on) setColor(1, 1, 1, 1);
        else setColor(1, 1, 1, 0.5f);
    }

    private void changeSize(float omegaDeg) {
        /*float L0 = 0.5f*SHIELDS_THICKNESS;
        float phi = L0/(SHIELDS_RADIUS + L0);
        float theta = omegaDeg*MathUtils.degreesToRadians - 2*phi;
        float L1 = 2*(SHIELDS_RADIUS + L0) * MathUtils.sin(0.5f*theta);
        float L2 = (SHIELDS_RADIUS + L0) * MathUtils.cos(0.5f*theta);
        float L3 = SHIELDS_RADIUS + SHIELDS_THICKNESS - L2;

        setWidth(L1 + 2*L0);
        setHeight(L3 + L0);*/
        //Gdx.app.log(TAG, "Before = (" + getWidth() + ", " + getHeight() + ")");

        setSize(resolutionIntoWorldUnits(currentRegion.getRegionWidth(), WORLD_SIZE, 1080),
                resolutionIntoWorldUnits(currentRegion.getRegionHeight(), WORLD_SIZE, 1080)); // Change 1080 based on the targetResolution of the shields' own targetResolution.
        //Gdx.app.log(TAG, "After = (" + getWidth() + ", " + getHeight() + ")");

        float theTipOfTheShield = /*SHIELDS_RADIUS + SHIELDS_THICKNESS*/SHIELDS_RADIUS;
        setX(theTipOfTheShield - getHeight());
        setY(getWidth()/2f);
    }
}
