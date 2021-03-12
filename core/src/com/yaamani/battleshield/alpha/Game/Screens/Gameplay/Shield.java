package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.resolutionIntoWorldUnits;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Shield extends Actor {

    public static final String TAG = Shield.class.getSimpleName();

    private float omegaDeg;
    private TextureRegion currentRegion;
    private boolean on = false;

    private Timer shieldDisabledTimer;

    private GameplayScreen gameplayScreen;

    public Shield(BulletsAndShieldContainer bulletsAndShieldContainer, GameplayScreen gameplayScreen) {
        this(gameplayScreen, 90, bulletsAndShieldContainer);
        initializeShieldDisabledTimer(gameplayScreen);
    }

    public Shield(GameplayScreen gameplayScreen, float omegaDeg, BulletsAndShieldContainer bulletsAndShieldContainer) {
        this.gameplayScreen = gameplayScreen;
        setOmegaDeg(omegaDeg);
        //setOrigin(0, 0);
        //setRotation(-90);
        //setDebug(true);
        bulletsAndShieldContainer.addActor(this);

        //getColor().a = .25f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        if (on)
            batch.draw(currentRegion, getX(), getY() + SHIELDS_ON_DISPLACEMENT, getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        else batch.draw(currentRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        shieldDisabledTimer.update(delta);
    }

    float getOmegaDeg() {
        return omegaDeg;
    }

    public void setOmegaDeg(float omegaDeg) {

        float minOmega = 360f / gameplayScreen.getCurrentShieldsMaxCount();
        float maxOmega = 360f / gameplayScreen.getCurrentShieldsMinCount();

        float correctedOmega = MathUtils.clamp(omegaDeg, minOmega, maxOmega);

        int index = Math.round((correctedOmega - minOmega)/SHIELDS_SKIP_ANGLE_WHEN_SAVING);
        this.omegaDeg = index*SHIELDS_SKIP_ANGLE_WHEN_SAVING + minOmega;

        currentRegion = Assets.instance.gameplayAssets.shieldsWithVariousAngles[index];

        changeSize(this.omegaDeg);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if (!shieldDisabledTimer.isFinished())
            this.on = false;
        else
            this.on = on;

        if (on) setColor(1, 1, 1, 1);
        else setColor(0.5f, 0.5f, 0.5f, 1f);
    }

    public void shieldDisablingBullet() {
        shieldDisabledTimer.start();
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

        float theTipOfTheShield = SHIELDS_RADIUS + (SHIELDS_RADIUS - SHIELDS_INNER_RADIUS);
        /*setY(theTipOfTheShield - getHeight());
        setX(getWidth()/2f);*/
        setX(theTipOfTheShield - getWidth());
        setY(-getHeight()/2f);

        setOrigin(-getX(), -getY());
        setRotation(90);
    }

    private void initializeShieldDisabledTimer(GameplayScreen gameplayScreen) {
        shieldDisabledTimer = new Timer(SHIELD_DISABLED_DURATION) {
            @Override
            public void onStart() {
                if (gameplayScreen.isRewinding()) {
                    setVisible(true);
                } else {
                    setVisible(false);
                    setOn(false);
                }
            }

            @Override
            public boolean onUpdate(float delta) {
                if (getColor().a == 0)
                    finish();
                return false;
            }

            @Override
            public void onFinish() {
                setVisible(true);
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(shieldDisabledTimer);
        shieldDisabledTimer.finish();
    }
}
