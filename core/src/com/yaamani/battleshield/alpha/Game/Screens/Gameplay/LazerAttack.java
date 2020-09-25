package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LazerAttack extends Group implements Updatable, Resizable {

    private Image lazerGun;
    private Image lazerBeam;
    private Image lazerGlow;
    private Image haloArmor;

    private Tween lazerGunFadeInTween;
    private Tween lazerGlowShrinkingTween;
    private Tween lazerBeamShrinkingTween;
    private float lazerBeamVisibilityProbability = 1;
    private Tween lazerBeamVisibilityProbabilityTween;
    private Tween haloArmorBlinkingTween;




    private GameplayScreen gameplayScreen;

    public LazerAttack(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        gameplayScreen.addActor(this);
        //setVisible(false);

        lazerGun = new Image(Assets.instance.gameplayAssets.lazerGun);
        lazerBeam = new Image(Assets.instance.gameplayAssets.lazerBeam);
        lazerGlow = new Image(Assets.instance.gameplayAssets.lazerGlow);
        haloArmor = new Image(Assets.instance.gameplayAssets.armorHalo);

        addActor(lazerBeam);
        addActor(lazerGun);
        addActor(lazerGlow);
        addActor(haloArmor);

        hide();

        //setRotation(45);

        initializeLazerGunFadeInTween();
        initializeLazerGlowShrinkingTween();
        initializeLazerBeamShrinkingTween();
        initializeLazerBeamVisibilityProbabilityTween();
        initializeHaloArmorBlinkingTween();
    }

    @Override
    public void update(float delta) {
        lazerGunFadeInTween.update(delta);
        lazerGlowShrinkingTween.update(delta);
        lazerBeamShrinkingTween.update(delta);
        lazerBeamVisibilityProbabilityTween.update(delta);
        haloArmorBlinkingTween.update(delta);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        this.setPosition(worldWidth/2f, worldHeight/2f);

        lazerGun.setSize(LAZER_GUN_WIDTH, LAZER_GUN_HEIGHT);
        lazerGun.setPosition(worldWidth/2f - LAZER_GUN_WIDTH - SCORE_TXT_MARGIN, -LAZER_GUN_HEIGHT/2f);

        lazerBeam.setHeight(LAZER_BEAM_HEIGHT);
        lazerBeam.setY(-LAZER_BEAM_HEIGHT/2f);
        calculateLazerBeamHorizontalBounds();

        lazerGlow.setHeight(LAZER_GLOW_HEIGHT);
        lazerGlow.setY(-LAZER_GLOW_HEIGHT/2f);
        calculateLazerGlowHorizontalBounds();

        haloArmor.setSize(LAZER_HALO_ARMOR_WIDTH, LAZER_HALO_ARMOR_HEIGHT);
        haloArmor.setPosition(0, -haloArmor.getHeight()/2f);
    }

    public void hide() {
        lazerGun.setColor(1, 1, 1, 0);
        lazerBeam.setColor(1, 1, 1, 0);
        lazerGlow.setColor(1, 1, 1, 0);
        haloArmor.setColor(1, 1, 1, 0);
    }

    public void start() {
        lazerGunFadeInTween.start();
        onStart();
    }

    public void onStart() {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        if (lazerAttackStuff.getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/lazerAttackStuff.getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()) {
            haloArmor.addAction(Actions.alpha(1, LAZER_ALPHA_ACTION_DURATION));
        }
    }

    public void onFinish() {
        lazerGun.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
    }

    //-------------------------------- Utility ---------------------------------
    //-------------------------------- Utility ---------------------------------
    //-------------------------------- Utility ---------------------------------
    //-------------------------------- Utility ---------------------------------

    private void calculateLazerBeamHorizontalBounds() {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        if (lazerAttackStuff.getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/lazerAttackStuff.getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()) {

            lazerBeam.setWidth(lazerGun.getX() - LAZER_HALO_ARMOR_WIDTH);
            lazerBeam.setX(LAZER_HALO_ARMOR_WIDTH);

        } else {
            lazerBeam.setWidth(lazerGun.getX() - TURRET_RADIUS);
            lazerBeam.setX(TURRET_RADIUS);
        }

    }

    private void calculateLazerGlowHorizontalBounds() {
        lazerGlow.setWidth(lazerBeam.getWidth() * LAZER_BEAM_GLOW_WIDTH_MULTIPLIER);
        lazerGlow.setX(lazerBeam.getX() - (lazerGlow.getWidth()-lazerBeam.getWidth())/2f);
    }

    private void affectHealth() {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        LazerAttackStuff.LazerAttackHealthAffection lazerAttackHealthAffection = lazerAttackStuff.getLazerAttackHealthAffection();
        HealthHandler healthHandler = gameplayScreen.getHealthHandler();

        if (lazerAttackHealthAffection.equals(LazerAttackStuff.LazerAttackHealthAffection.YAMANI)) {

            if (healthHandler.getHealth().getCurrentFinalValue() > 1)
                healthHandler.setHealthValueTo(0.6f, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else
                healthHandler.affectHealthBy(D_LAZER_YAMANI_HEALTH_AFFECTION_AMOUNT, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);


        } else if (lazerAttackHealthAffection.equals(LazerAttackStuff.LazerAttackHealthAffection.OMAR)) {

            if (healthHandler.getHealth().getCurrentFinalValue() > 0.5f)
                healthHandler.setHealthValueTo(0.5f, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else if(healthHandler.getHealth().getCurrentFinalValue() > 0.25f)
                healthHandler.setHealthValueTo(0.25f, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else
                healthHandler.setHealthValueTo(0, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);


        }
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeLazerGunFadeInTween() {
        lazerGunFadeInTween = new Tween(LAZER_GUN_FADE_IN_TWEEN_DURATION, Interpolation.linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float a = interpolation.apply(percentage);
                lazerGun.setColor(1, 1, 1, a);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                lazerBeamShrinkingTween.start();
                lazerGlowShrinkingTween.start();

                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (lazerAttackStuff.getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() >= /*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*/lazerAttackStuff.getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()) {
                    haloArmorBlinkingTween.start();
                } else {
                    affectHealth();
                }

            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(lazerGunFadeInTween);
    }

    private void initializeLazerGlowShrinkingTween() {
        lazerGlowShrinkingTween = new Tween(LAZER_GLOW_SHRINKING_TWEEN_DURATION, LAZER_GLOW_SHRINKING_TWEEN_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();
                lazerGlow.setHeight(LAZER_GLOW_HEIGHT);

                calculateLazerGlowHorizontalBounds();
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float height = interpolation.apply(LAZER_GLOW_HEIGHT, lazerBeam.getHeight(), percentage);
                lazerGlow.setHeight(height);
                lazerGlow.setY(-height/2f);

                final float alphaTweenPercentageStart = 0.75f;
                float alphaPercentage = (percentage-alphaTweenPercentageStart)*(1f/(1-alphaTweenPercentageStart));
                float a = percentage >= alphaTweenPercentageStart ? Interpolation.linear.apply(alphaPercentage) : 1;
                lazerGlow.setColor(1, 1, 1, a);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                lazerGlow.setColor(1, 1, 1, 0);

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;


            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(lazerGlowShrinkingTween);
    }

    private void initializeLazerBeamShrinkingTween() {
        lazerBeamShrinkingTween = new Tween(LAZER_BEAM_SHRINKING_TWEEN_DURATION, LAZER_BEAM_SHRINKING_TWEEN_INTERPOLATION) {
            @Override
            public void onStart() {
                super.onStart();
                lazerBeam.setColor(1, 1, 1, 1);

                calculateLazerBeamHorizontalBounds();
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float height = interpolation.apply(LAZER_BEAM_HEIGHT, LAZER_BEAM_HEIGHT/4f, percentage);
                float heightMultiplier = MathUtils.random(LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_LOW, LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_HIGH);
                lazerBeam.setHeight(height * heightMultiplier);
                lazerBeam.setY(-lazerBeam.getHeight()/2f);

                float millisLeft = (1-percentage)*getDurationMillis();
                if (!lazerBeamVisibilityProbabilityTween.isStarted() & millisLeft <= LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_DURATION)
                    lazerBeamVisibilityProbabilityTween.start();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                lazerBeam.setColor(1, 1, 1, 0);

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                LazerAttack.this.onFinish();
            }
        };
    }

    private void initializeLazerBeamVisibilityProbabilityTween() {
        lazerBeamVisibilityProbabilityTween = new Tween(LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_DURATION, LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();

                lazerBeamVisibilityProbability = 1;
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                lazerBeamVisibilityProbability = interpolation.apply(1, 0, percentage);
                if (MathUtils.random() > lazerBeamVisibilityProbability)
                    lazerBeam.setColor(1, 1, 1, 0);
                else lazerBeam.setColor(1, 1, 1, 1);
            }
        };
    }

    private void initializeHaloArmorBlinkingTween() {
        haloArmorBlinkingTween = new Tween(LAZER_HALO_ARMOR_BLINKING_TWEEN_DURATION) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                //float start = (1-lazerBeamVisibilityProbability)/(1-LAZER_HALO_ARMOR_BLINKING_START_ALPHA) + LAZER_HALO_ARMOR_BLINKING_START_ALPHA;
                //float start = 1-lazerBeamVisibilityProbability;
                float a = MathUtils.random(LAZER_HALO_ARMOR_BLINKING_START_ALPHA, 1);
                haloArmor.setColor(1, 1, 1,a);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                haloArmor.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

            }
        };
    }
}
