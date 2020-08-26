package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class Bullet extends Group implements Resizable, Pool.Poolable {

    public static final String TAG = Bullet.class.getSimpleName();

    private static boolean plusOrMinusExists = false;
    private static boolean starExists = false;
    private static boolean fasterDizzinessRotationExists = false;
    private static float R = 0;

    public enum BulletType {ORDINARY, SPECIAL}
    private BulletType bulletType;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;

    private boolean inUse = false;
    private BulletsAndShieldContainer parent;
    private BulletsHandler bulletsHandler;
    private /*AdvancedScreen*/GameplayScreen gameplayScreen;
    private TextureRegion region;

    private Effects effects;
    private BulletEffect currentEffect;

    //private MyTween currentSpeedTweenStars;
    //private StarsContainer.RadialTween radialTweenStars;
    private StarsContainer starsContainer;

    private Viewport viewport;

    private SimpleText type; // Debugging

    private Tween fakeTween; // When the wave is fake.

    /*private Tween bulletMovement;
    private float initialY;
    private float finalY;*/

    public Bullet(/*AdvancedScreen*/GameplayScreen gameplayScreen, /*Tween*/StarsContainer starsContainer, Viewport viewport) {
        this.gameplayScreen = gameplayScreen;
        this.bulletsHandler = gameplayScreen.getBulletsHandler();
        this.bulletPool = bulletsHandler.getBulletPool();
        this.activeBullets = bulletsHandler.getActiveBullets();

        effects = new Effects();

        /*this.radialTweenStars*/this.starsContainer = starsContainer;

        this.viewport = viewport;

        notSpecial(false);

        // setDebug(true);
        // bulletPool.getFree();

        initializeFakeTween();


        initializeType();

        // initializeBulletMovement();

        // setColor(1, 1, 1, 0.2f); //Debugging
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        //calculateR(worldWidth, worldHeight);
    }

    public static float getR() {
        return R;
    }

    public static boolean isPlusOrMinusExists() {
        return plusOrMinusExists;
    }

    public static void setPlusOrMinusExists(boolean plusOrMinusExists) {
        Bullet.plusOrMinusExists = plusOrMinusExists;
    }

    public static boolean isStarExists() {
        return starExists;
    }

    public static void setStarExists(boolean starExists) {
        Bullet.starExists = starExists;
    }

    public static boolean isFasterDizzinessRotationExists() {
        return fasterDizzinessRotationExists;
    }

    public static void setFasterDizzinessRotationExists(boolean fasterDizzinessRotationExists) {
        Bullet.fasterDizzinessRotationExists = fasterDizzinessRotationExists;
    }

    public boolean isInUse() {
        return inUse;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    public Tween getFakeTween() {
        return fakeTween;
    }

    public void attachNotSpecialToBulletsAndShieldContainer(BulletsAndShieldContainer parent, int order) {
        readyToBeAttached(parent);

        setY(getY() + order*(BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_HEIGHT));

        //bulletMovementSetDurationAndStart();
    }

    public void attachSpecialToBulletsAndShieldContainer(BulletsAndShieldContainer parent/*, boolean isDouble, int indexForDoubleWave*/) {
        readyToBeAttached(parent);

        //if (isDouble & indexForDoubleWave == 1) {
            //float totalDistance = gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().getDurationMillis() * BULLETS_SPEED_INITIAL / 1000f;
            //setY(getY() + /*MathUtils.random(0, */totalDistance - BULLETS_CLEARANCE_BETWEEN_WAVES - BULLETS_SPECIAL_DIAMETER/2f/*)*/);
            //return;
        //}

        float additionalDistance = BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_CLEARANCE_BETWEEN_WAVES; //Distance between the bullet and the nearest bullet attached to the previous wave
        setY(getY() - additionalDistance + (additionalDistance + BULLETS_SPECIAL_WAVE_LENGTH)/2f - BULLETS_SPECIAL_DIAMETER);

        //bulletMovementSetDurationAndStart();
    }

    private void readyToBeAttached(BulletsAndShieldContainer parent) {
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
        /*if (currentEffect != effects.fake & currentEffect != effects.ordinaryFake)
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha * 0.65f);
        else
            batch.setColor(color.r, color.g, color.b, parentAlpha * 0.15f);*/

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(region, getX() - getWidth()/2f, getY()/* - getHeight()/2f*/, getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (gameplayScreen.getState() == GameplayScreen.State.PAUSED)
            return;



        if (inUse) {
            /*Gdx.app.log(TAG, "bullet = " + this + ", parentIndex = " + parent.getIndex() + ", parent'sParent = " + (parent.getParent()!=null ? "notNull":"null") + ", getY() = " + getY());*/

            setY(getY() - bulletsHandler.getBulletSpeed() * delta);
            //bulletMovement.update(delta);
            // type.setColor(1, 1, 1, type.getColor().a - 0.001f);
            // type.setColor(1, 1, 1, 0.5f);

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
                    if (currentEffect == effects.minus | currentEffect == effects.plus) plusOrMinusExists = false;
                    return;
                }
            }

            //The bullet hits the turret
            if (getY() <= TURRET_RADIUS)  {
                if (parent.getColor().a >= 0.95f)
                    currentEffect.effect();

                stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
            }

            if (currentEffect == effects.ordinaryFake | currentEffect == effects.fake)
                fakeTween.update(delta);
        }
    }

    private void whenTheShieldStartsToDisappear() {
        if (parent.getColor().a < 0.95f) {
            handlePlusMinusStarExists();
        }
    }

    private void whenTheShieldCompletelyDisappears() {
        if (parent.getColor().a <= 0) {
            stopUsingTheBullet(viewport.getWorldWidth(), viewport.getWorldHeight());
            if (gameplayScreen.getBulletsHandler().getCurrentWaveLastBullet() == this)
                gameplayScreen.getBulletsHandler().nullifyCurrentWaveLastBullet();
        }
    }

    private void correctSpecialBulletsRotation() {
        if (currentEffect != effects.ordinary &
                currentEffect != effects.ordinaryFake &
                getRotation() != -parent.getRotation()) {

            Group containerOfContainers = gameplayScreen.getContainerOfContainers();
            setRotation(-containerOfContainers.getRotation()-parent.getRotation());
        }
    }

    public void  stopUsingTheBullet(float worldWidth, float worldHeight) {
        inUse = false;
        //bulletMovement.finish();
        resetPosition(worldWidth, worldHeight);
        detachFromBulletsAndShieldObject();
        bulletPool.free(this);
        activeBullets.removeValue(this, true);

        handlePlusMinusStarExists();
    }

    private void handlePlusMinusStarExists() {
        if (currentEffect == effects.minus | currentEffect == effects.plus)
            setPlusOrMinusExists(false);
        else if (currentEffect == effects.star)
            setStarExists(false);
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

    public void notSpecial(boolean fake) {
        bulletType = BulletType.ORDINARY;

        setSize(BULLETS_ORDINARY_WIDTH, BULLETS_ORDINARY_HEIGHT);
        setOrigin(0, 0);
        setRotation(0);
        region = Assets.instance.gameplayAssets.bullet;
        if (fake)
            currentEffect = effects.ordinaryFake;
        else
            currentEffect = effects.ordinary;

        if (type != null) {
            type.setVisible(true);
            type.setCharSequence("Ordinary", true);
        }

    }

    public void setSpecial(SpecialBullet specialType, boolean questionMark, boolean fake) {
        bulletType = BulletType.SPECIAL;

        setSize(BULLETS_SPECIAL_DIAMETER, BULLETS_SPECIAL_DIAMETER);
        setOrigin(Align.center);
        setY(getY() - (BULLETS_SPECIAL_DIAMETER - BULLETS_ORDINARY_HEIGHT)/2f);

        if (type != null) type.setVisible(true);

        switch (specialType) {
            case PLUS:
                if (!questionMark) region = Assets.instance.gameplayAssets.plusBullet;
                currentEffect = effects.plus;
                break;
            case MINUS:
                if (!questionMark) region = Assets.instance.gameplayAssets.minusBullet;
                currentEffect = effects.minus;
                break;
            case BOMB:
                if (!questionMark) region = Assets.instance.gameplayAssets.bombBullet;
                currentEffect = effects.bomb;
                break;
            case HEART:
                if (!questionMark) region = Assets.instance.gameplayAssets.heartBullet;
                currentEffect = effects.heart;
                break;
            case STAR:
                if (!questionMark) region = Assets.instance.gameplayAssets.starBullet;
                currentEffect = effects.star;
                break;
            case MIRROR:
                if (!questionMark) region = Assets.instance.gameplayAssets.mirrorBullet;
                currentEffect = effects.mirror;
                break;
            case SHIELD_DISABLING:
                if (!questionMark) region = Assets.instance.gameplayAssets.shieldDisablingBullet;
                currentEffect = effects.shieldDisabling;
                break;
            case FASTER_DIZZINESS_ROTATION:
                if (!questionMark) region = Assets.instance.gameplayAssets.fasterDizzinessRotationBullet;
                currentEffect = effects.fasterDizzinessRotation;
                break;
        }

        // Next 2 lines are for debugging.
        /*if (!questionMark) region = Assets.instance.gameplayAssets.starBullet;
        currentEffect = effects.star;*/

        if (fake)
            currentEffect = effects.fake;

        if (questionMark)
            region = Assets.instance.gameplayAssets.questionMarkBullet;

        if (type != null) type.setCharSequence("" + specialType, true);
    }

    @Override
    public void reset() {

    }

    private void initializeType() {
        type = new SimpleText(gameplayScreen.getMyBitmapFont(), "Bullet");
        type.setBoundsHeight(BULLETS_SPECIAL_DIAMETER/*WORLD_SIZE/2f*/, BULLETS_SPECIAL_DIAMETER, WORLD_SIZE/30f);
        //addActor(type);
    }

    /*private void bulletMovementSetDurationAndStart() {
        initialY = getY();
        finalY = 0;
        bulletMovement.setDurationMillis((initialY - finalY) / (bulletsHandler.getBulletSpeed()) * 1000);
        bulletMovement.start();
    }

    private void initializeBulletMovement() {
        bulletMovement = new Tween(linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                setY(interpolation.apply(initialY, finalY, percentage));
            }
        };

        gameplayScreen.addToPauseWhenPausingFinishWhenLosing(bulletMovement);
    }*/

    private void initializeFakeTween() {
        fakeTween = new Tween(D_CRYSTAL_FAKE_TWEEN_DURATION, CRYSTAL_FAKE_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float a = interpolation.apply(percentage);
                setColor(1, 1, 1, a);
                //Gdx.app.log(Bullet.TAG, "" + a);
            }
        };
    }

    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    @FunctionalInterface
    private interface BulletEffect {
        void effect();
    }

    //------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------

    private class Effects {
        private BulletEffect ordinary;

        private BulletEffect plus;
        private BulletEffect bomb;
        private BulletEffect shieldDisabling;

        private BulletEffect minus;
        private BulletEffect heart;
        private BulletEffect star;

        //Crystal
        private BulletEffect mirror;
        private BulletEffect ordinaryFake; // Does nothing
        private BulletEffect fake; // Does nothing

        //Dizziness
        private BulletEffect fasterDizzinessRotation;



        //private BulletEffect questionMark;

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
                    starsContainer.getRadialTween().start(SpecialBullet.PLUS);
                }
            };

            bomb = new BulletEffect() {
                @Override
                public void effect() {
                    affectHealth(BULLETS_BOMB_AFFECT_HEALTH_BY);
                    if (gameplayScreen.getGameplayMode() == GameplayMode.CRYSTAL)
                        gameplayScreen.getStarsContainer().getGlassCrackPostProcessingEffect().generateCrack();
                }
            };

            shieldDisabling = new BulletEffect() {
                @Override
                public void effect() {
                    parent.getShield().shieldDisablingBullet();
                }
            };




            minus = new BulletEffect() {
                @Override
                public void effect() {
                    ShieldsAndContainersHandler handler = gameplayScreen.getShieldsAndContainersHandler();
                    handler.setActiveShieldsNum(handler.getActiveShieldsNum()-1);
                    plusMinusCommon();
                    /*radialTweenStars*/starsContainer.getRadialTween().start(SpecialBullet.MINUS);
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
                    //bulletsHandler.resetSpeed();
                    //bulletsHandler.decrementCurrentSpeedMultiplier();

                    //bulletsHandler.getCurrentBulletsWaveTimer().pauseFor(STAR_BULLET_TOTAL_DURATION);
                    //bulletsHandler.getBulletSpeedMultiplierTween().pauseFor(STAR_BULLET_TOTAL_DURATION);
                    //bulletsHandler.getBulletsPerAttackNumberTween().pauseFor(STAR_BULLET_TOTAL_DURATION);
                    //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierTween().pauseFor(STAR_BULLET_TOTAL_DURATION);
                    //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().pauseFor(STAR_BULLET_TOTAL_DURATION);

                    bulletsHandler.startStarBulletStages();

                    //starsContainer.getRadialTween().pauseGradually(STAR_BULLET_FIRST_STAGE_DURATION);
                    Bullet.setStarExists(false);
                    gameplayScreen.setInStarBulletAnimation(true);
                    Gdx.app.log(TAG, "STAR BULLET");
                }
            };

            mirror = new BulletEffect() {
                @Override
                public void effect() {
                    gameplayScreen.getShieldsAndContainersHandler().startMirrorTimer();
                    float millis = gameplayScreen.getShieldsAndContainersHandler().getMirrorControlsTimerDuration();
                    TextureRegion r = Assets.instance.gameplayAssets.mirrorBullet;
                    gameplayScreen.displayTempProgressBar(r, millis);
                }
            };

            fake = new BulletEffect() {
                @Override
                public void effect() {
                    // Nothing
                }
            };

            ordinaryFake = new BulletEffect() {
                @Override
                public void effect() {
                    // Nothing
                }
            };

            fasterDizzinessRotation = new BulletEffect() {
                @Override
                public void effect() {
                    gameplayScreen.getShieldsAndContainersHandler().getDizzinessRotationalSpeedMultiplierTimer().start();

                }
            };

            /*questionMark = new BulletEffect() {
                @Override
                public void effect() {

                }
            };*/
        }

        private void affectHealth(float by) {
            float health = gameplayScreen.getHealthHandler().getHealth();
            gameplayScreen.getHealthHandler().setHealth(health + by);
        }

        private void plusMinusCommon() {
            Bullet.setPlusOrMinusExists(false);
            gameplayScreen.getBulletsHandler().startPlusMinusBulletsTween();
        }




    }

}