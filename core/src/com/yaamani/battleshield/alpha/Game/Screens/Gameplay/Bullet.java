package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Bullet extends Actor implements Resizable, Pool.Poolable {

    public static final String TAG = Bullet.class.getSimpleName();

    private static boolean thereIsPlusOrMinus = false;
    private static float R = 0;

    private static float speedResetTime = 0;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private boolean inUse = false;
    private BulletsAndShieldContainer parent;
    private /*AdvancedScreen*/GameplayScreen gameplayScreen;
    private TextureRegion region;

    private Effects effects;
    private BulletEffect currentEffect;

    private StarsContainer.RadialTween radialTweenStars;

    private Viewport viewport;

    public Bullet(/*AdvancedScreen*/GameplayScreen gameplayScreen, /*Tween*/StarsContainer.RadialTween radialTweenStars, Viewport viewport) {
        this.gameplayScreen = gameplayScreen;
        this.bulletPool = gameplayScreen.getBulletPool();
        this.activeBullets = gameplayScreen.getActiveBullets();

        effects = new Effects();

        this.radialTweenStars = radialTweenStars;

        this.viewport = viewport;

        notSpecial();
        //setDebug(true);
        //bulletPool.getFree();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        //calculateR(worldWidth, worldHeight);
    }

    public static float getR() {
        return R;
    }

    public static boolean isTherePlusOrMinus() {
        return thereIsPlusOrMinus;
    }

    public static void setThereIsPlusOrMinus(boolean thereIsPlusOrMinus) {
        Bullet.thereIsPlusOrMinus = thereIsPlusOrMinus;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void attachNotSpecialToBulletsAndShieldContainer(BulletsAndShieldContainer parent, int order) {
        attach(parent);

        setY(getY() + order*(BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_HEIGHT));
    }

    public void attachSpecialToBulletsAndShieldContainer(BulletsAndShieldContainer parent, boolean isDouble, int indexForDoubleWave) {
        attach(parent);

        //if (isDouble & indexForDoubleWave == 1) {
            //float totalDistance = gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().getDurationMillis() * BULLETS_SPEED_INITIAL / 1000f;
            //setY(getY() + /*MathUtils.random(0, */totalDistance - BULLETS_CLEARANCE_BETWEEN_WAVES - BULLETS_SPECIAL_DIAMETER/2f/*)*/);
            //return;
        //}

        float additionalDistance = BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_CLEARANCE_BETWEEN_WAVES; //Distance between the bullet and the nearest bullet attached to the previous wave
        setY(getY() - additionalDistance + (additionalDistance + BULLETS_SPECIAL_WAVE_LENGTH)/2f - BULLETS_SPECIAL_DIAMETER);

    }

    private void attach(BulletsAndShieldContainer parent) {
        inUse = true;
        this.parent = parent;
        parent.addActor(this);

        resetPosition(viewport.getWorldWidth(), viewport.getWorldHeight());

        /*float totalDistance = gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().getDurationMillis() * BULLETS_SPEED_INITIAL / 1000f;
        Gdx.app.log(TAG, "totalDistance = " + totalDistance);*/
    }

    private void detachFromBulletsAndShieldObject() {
        if (parent != null) parent.removeActor(this);
        parent = null;
    }

    private void resetPosition(float worldWidth, float worldHeight) {
        if (R == 0)
            calculateR(worldWidth, worldHeight);
        setY(R); // Look @ Non-Finalized Assets/Firing bullets.png
    }

    public static void calculateR(float worldWidth, float worldHeight) {
        //if (getStage() != null) {
            //Viewport viewport = getStage().getViewport();
            //if (viewport != null)
                R = Vector2.dst(0, 0, worldWidth/2f, worldHeight/2f);
        //}
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(region, getX() - getWidth()/2f, getY()/* - getHeight()/2f*/, getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (inUse) {
            setY(getY() - getSpeed() * delta);

            correctSpecialBulletsRotation();

            whenTheShieldStartsToDisappear();

            whenTheShieldCompletelyDisappears();

            if (parent != null) {
                Shield shield;
                shield = parent.getShield();
                // The shield blocked the bullet
                if (getY() <= /*SHIELDS_RADIUS+SHIELDS_THICKNESS*/SHIELDS_RADIUS+SHIELDS_ON_DISPLACEMENT &
                        getY() >= /*SHIELDS_RADIUS-SHIELDS_THICKNESS*/SHIELDS_INNER_RADIUS+SHIELDS_ON_DISPLACEMENT - getHeight() &
                        shield.isOn()) {
                    stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
                    if (currentEffect == effects.minus | currentEffect == effects.plus) thereIsPlusOrMinus = false;
                    return;
                }
            }

            //The bullet hits the turret
            if (getY() < TURRET_RADIUS)  {
                if (parent.getColor().a >= 0.95f)
                    currentEffect.effect();

                stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
            }
        }
    }

    private void whenTheShieldStartsToDisappear() {
        if (parent.getColor().a < 0.95f) {
            if (currentEffect == effects.minus | currentEffect == effects.plus)
                setThereIsPlusOrMinus(false);
        }
    }

    private void whenTheShieldCompletelyDisappears() {
        if (parent.getColor().a <= 0) {
            stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
        }
    }

    private void correctSpecialBulletsRotation() {
        if (currentEffect != effects.ordinary & getRotation() != -parent.getRight())
            setRotation(-parent.getRotation());
    }

    public void stopUsingTheBullet(float worldWidth, float worldHeight) {
        inUse = false;
        resetPosition(worldWidth, worldHeight);
        detachFromBulletsAndShieldObject();
        bulletPool.free(this);
        activeBullets.removeValue(this, true);
    }

    /*private void decideSpecialType() {
        WaveBulletsType bulletType = WAVE_BULLETS_TYPE_PROBABILITY[MathUtils.random(WAVE_BULLETS_TYPE_PROBABILITY.length-1)];
        switch (bulletType) {
            default: // ORDINARY
                notSpecial();
                break;
            case SPECIAL_BAD:
                setSpecial(BAD_BULLETS_PROBABILITY[MathUtils.random(BAD_BULLETS_PROBABILITY.length-1)]);
                break;
            case SPECIAL_GOOD:
                setSpecial(GOOD_BULLETS_PROBABILITY[MathUtils.random(GOOD_BULLETS_PROBABILITY.length-1)]);
                break;
        }
    }*/

    public void notSpecial() {
        setSize(BULLETS_ORDINARY_WIDTH, BULLETS_ORDINARY_HEIGHT);
        setOrigin(0, 0);
        setRotation(0);
        region = Assets.instance.gameplayAssets.bullet;
        currentEffect = effects.ordinary;
    }

    public void setSpecial(SpecialBullet specialType) {
        setSize(BULLETS_SPECIAL_DIAMETER, BULLETS_SPECIAL_DIAMETER);
        setOrigin(Align.center);
        setY(getY() - (BULLETS_SPECIAL_DIAMETER - BULLETS_ORDINARY_HEIGHT)/2f);

        switch (specialType) {
            case PLUS:
                region = Assets.instance.gameplayAssets.plusBullet;
                currentEffect = effects.plus;
                break;
            case MINUS:
                region = Assets.instance.gameplayAssets.minusBullet;
                currentEffect = effects.minus;
                break;
            case BOMB:
                region = Assets.instance.gameplayAssets.bombBullet;
                currentEffect = effects.bomb;
                break;
            case HEART:
                region = Assets.instance.gameplayAssets.heartBullet;
                currentEffect = effects.heart;
                break;
            case STAR:
                region = Assets.instance.gameplayAssets.starBullet;
                currentEffect = effects.star;
                break;
        }
    }

    public static void resetSpeedResetTime() {
        speedResetTime = 0;
    }

    public static void resetSpeed() {
        speedResetTime = GameplayScreen.getTimePlayedThisTurnSoFar();
    }

    public static float getSpeed() {
        int i = (int) /*floor*/ ((GameplayScreen.getTimePlayedThisTurnSoFar() - speedResetTime) / BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY);
        float currentMultiplier = 1 + i * BULLETS_SPEED_MULTIPLIER_INCREMENT;

        if (currentMultiplier <= BULLETS_SPEED_MULTIPLIER_MAX) {
            //Gdx.app.log(TAG, "Speed Multiplier = " + currentMultiplier);
            return BULLETS_SPEED_INITIAL * currentMultiplier;
        }
        //Gdx.app.log(TAG, "Speed Multiplier = " + BULLETS_SPEED_MULTIPLIER_MAX);
        return BULLETS_SPEED_INITIAL * BULLETS_SPEED_MULTIPLIER_MAX;
    }

    @Override
    public void reset() {

    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private interface BulletEffect {
        void effect();
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private class Effects {
        private BulletEffect ordinary;

        private BulletEffect plus;
        private BulletEffect bomb;

        private BulletEffect minus;
        private BulletEffect heart;
        private BulletEffect star;

        private Effects() {

            ordinary = new BulletEffect() {
                @Override
                public void effect() {
                    affectHealth(BULLETS_ORDINARY_AFFECT_HEALTH_BY);
                }
            };

            plus = new BulletEffect() {
                @Override
                public void effect() {
                    ShieldsAndContainersHandler handler = gameplayScreen.getShieldsAndContainersHandler();
                    handler.setActiveShieldsNum(handler.getActiveShieldsNum()+1);
                    plusMinusCommon();
                    radialTweenStars.start(SpecialBullet.PLUS);
                }
            };

            bomb = new BulletEffect() {
                @Override
                public void effect() {
                    affectHealth(BULLETS_BOMB_AFFECT_HEALTH_BY);
                }
            };




            minus = new BulletEffect() {
                @Override
                public void effect() {
                    ShieldsAndContainersHandler handler = gameplayScreen.getShieldsAndContainersHandler();
                    handler.setActiveShieldsNum(handler.getActiveShieldsNum()-1);
                    plusMinusCommon();
                    radialTweenStars.start(SpecialBullet.MINUS);
                }
            };

            heart = new BulletEffect() {
                @Override
                public void effect() {
                    affectHealth(BULLETS_HEART_AFFECT_HEALTH_BY);
                }
            };


            star = new BulletEffect() {
                @Override
                public void effect() {
                    Bullet.resetSpeed();
                    Gdx.app.log(TAG, "STAR BULLET");
                }
            };

        }

        private void affectHealth(float by) {
            float health = gameplayScreen.getHealthHandler().getHealth();
            gameplayScreen.getHealthHandler().setHealth(health + by);
        }

        private void plusMinusCommon() {
            thereIsPlusOrMinus = false;
            gameplayScreen.getBulletsHandler().startPlusMinusBulletsTween();
        }
    }

}