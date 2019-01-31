package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay.ShieldsAndContainersHandler;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Bullet extends Actor implements Resizable, Pool.Poolable {

    public static final String TAG = Bullet.class.getSimpleName();

    private static boolean thereIsPlusOrMinus = false;
    private static float R = 0;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private boolean inUse = false;
    private BulletsAndShieldContainer parent;
    private GameplayScreen gameplayScreen;
    private TextureRegion region;

    private Effects effects;
    private BulletEffect currentEffect;

    private StarsContainer.RadialTween radialTweenStars;

    private Viewport viewport;

    public Bullet(GameplayScreen gameplayScreen, StarsContainer.RadialTween radialTweenStars, Viewport viewport) {
        this.gameplayScreen = gameplayScreen;
        this.bulletPool = gameplayScreen.getBulletPool();
        this.activeBullets = gameplayScreen.getActiveBullets();

        effects = new Effects();

        this.radialTweenStars = radialTweenStars;

        this.viewport = viewport;

        notSpecial();
        //setDebug(true);
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

        setX(getX() + order*(BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_WIDTH));
    }

    public void attachSpecialToBulletsAndShieldContainer(BulletsAndShieldContainer parent, boolean isDouble, int indexForDoubleWave) {
        attach(parent);

        //if (isDouble & indexForDoubleWave == 1) {
            //float totalDistance = gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().getDurationMillis() * BULLETS_SPEED / 1000f;
            //setX(getX() + /*MathUtils.random(0, */totalDistance - BULLETS_CLEARANCE_BETWEEN_WAVES - BULLETS_SPECIAL_DIAMETER/2f/*)*/);
            //return;
        //}

        float additionalDistance = BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_CLEARANCE_BETWEEN_WAVES; //Distance between the bullet and the nearest bullet attached to the previous wave
        setX(getX() - additionalDistance + (additionalDistance + BULLETS_SPECIAL_WAVE_LENGTH)/2f - BULLETS_SPECIAL_DIAMETER);

    }

    private void attach(BulletsAndShieldContainer parent) {
        inUse = true;
        this.parent = parent;
        parent.addActor(this);

        resetPosition(viewport.getWorldWidth(), viewport.getWorldHeight());

        /*float totalDistance = gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().getDurationMillis() * BULLETS_SPEED / 1000f;
        Gdx.app.log(TAG, "totalDistance = " + totalDistance);*/
    }

    private void detachFromBulletsAndShieldObject() {
        if (parent != null) parent.removeActor(this);
        parent = null;
    }

    private void resetPosition(float worldWidth, float worldHeight) {
        if (R == 0)
            calculateR(worldWidth, worldHeight);
        setX(R); // Look @ Non-Finalized Assets/Firing bullets.png
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
        batch.draw(region, getX(), getY() - getHeight()/2f, getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (inUse) {
            setX(getX() - BULLETS_SPEED * delta);

            correctSpecialBulletsRotation();

            whenTheShieldStartsToDisappear();

            whenTheShieldCompletelyDisappears();

            if (parent != null) {
                Shield shield;
                shield = parent.getShield();
                // The shield blocked the bullet
                if (getX() <= SHIELDS_RADIUS+SHIELDS_THICKNESS+SHIELDS_ON_DISPLACEMENT &
                        getX() >= SHIELDS_RADIUS-SHIELDS_THICKNESS+SHIELDS_ON_DISPLACEMENT - getWidth() &
                        shield.isOn()) {
                    stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
                    if (currentEffect == effects.minus | currentEffect == effects.plus) thereIsPlusOrMinus = false;
                    return;
                }
            }

            //The bullet hit the turret
            if (getX() < TURRET_RADIUS)  {
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
        setOrigin(getWidth()/2f, getHeight()/2f);
        setX(getX() - (BULLETS_SPECIAL_DIAMETER - BULLETS_ORDINARY_WIDTH)/2f);

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
        }
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