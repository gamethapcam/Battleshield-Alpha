package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timeline;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LazerAttack extends Group implements Updatable, Resizable {

    private Image lazerGun;
    private Image lazerBeam;
    private Image lazerGlow;
    private Image haloArmor;

    private Tween initialFadeTween;
    private Tween lazerGlowShrinkingTween;
    private Tween lazerBeamShrinkingTween;
    private float lazerBeamVisibilityProbability = 1;
    private Tween lazerBeamVisibilityProbabilityTween;
    private Tween haloArmorBlinkingTween;
    private Tween finalFadeTween;

    private Timeline lazerAttackTimeline;


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

        initializeInitialFadeTween();
        initializeLazerGlowShrinkingTween();
        initializeLazerBeamShrinkingTween();
        initializeLazerBeamVisibilityProbabilityTween();
        initializeHaloArmorBlinkingTween();
        initializeFinalFadeTween();

        initializeLazerAttackTimeline();
    }

    @Override
    public void update(float delta) {
        lazerAttackTimeline.update(delta);

        initialFadeTween.update(delta);
        lazerGlowShrinkingTween.update(delta);
        lazerBeamShrinkingTween.update(delta);
        lazerBeamVisibilityProbabilityTween.update(delta);
        haloArmorBlinkingTween.update(delta);
        finalFadeTween.update(delta);
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
        //lazerGunFadeInTween.start();
        lazerAttackTimeline.start();
    }

    public void onStart() {
        /*LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        if (lazerAttackStuff.getCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack() >= *//*LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR*//*lazerAttackStuff.getCurrentNecessaryNumOfArmorBulletsForTheNextAttack()) {
            haloArmor.addAction(Actions.alpha(1, LAZER_FADE_DURATION));
        }*/
    }

    public void onFinish() {

    }

    public Timeline getLazerAttackTimeline() {
        return lazerAttackTimeline;
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

    private void _affectHealth() {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
        LazerAttackStuff.LazerAttackHealthAffection lazerAttackHealthAffection = lazerAttackStuff.getLazerAttackHealthAffection();
        HealthHandler healthHandler = gameplayScreen.getHealthHandler();

        if (lazerAttackHealthAffection.equals(LazerAttackStuff.LazerAttackHealthAffection.YAMANI)) {

            if (healthHandler.getHealth().getCurrentFinalValue() > 1)
                healthHandler.setHealthValueTo(1 + D_LAZER_YAMANI_HEALTH_AFFECTION_AMOUNT, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else
                healthHandler.affectHealth(D_LAZER_YAMANI_HEALTH_AFFECTION_AMOUNT, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);


        } else if (lazerAttackHealthAffection.equals(LazerAttackStuff.LazerAttackHealthAffection.OMAR)) {

            if (healthHandler.getHealth().getCurrentFinalValue() > 0.5f)
                healthHandler.setHealthValueTo(0.5f, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else if(healthHandler.getHealth().getCurrentFinalValue() > 0.25f)
                healthHandler.setHealthValueTo(0.25f, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
            else
                healthHandler.setHealthValueTo(0, LAZER_BEAM_SHRINKING_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);


        }
    }

    private void _affectTimer() {
        gameplayScreen.getScoreTimerStuff().affectTimer(D_BIG_BOSS_AFFECT_TIMER_LAZER_AMOUNT, LAZER_BEAM_SHRINKING_TWEEN_DURATION, BIG_BOSS_AFFECT_TIMER_LAZER_INTERPOLATION);
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeInitialFadeTween() {
        initialFadeTween = new Tween(LAZER_GUN_FADE_IN_TWEEN_DURATION, Interpolation.linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float aIn = interpolation.apply(percentage);
                lazerGun.setColor(1, 1, 1, aIn);

                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (lazerAttackStuff.didThePlayerCollectAllTheNecessaryArmorBullets())
                    haloArmor.setColor(1, 1, 1, aIn);

                float aOut = interpolation.apply(1, 0, percentage);
                gameplayScreen.getControllerLeft().setColor(1, 1, 1, aOut);
                gameplayScreen.getControllerRight().setColor(1, 1, 1, aOut);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                //lazerBeamShrinkingTween.start();
                //lazerGlowShrinkingTween.start();

                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (lazerAttackStuff.didThePlayerCollectAllTheNecessaryArmorBullets()) {
                    //haloArmorBlinkingTween.start();
                } else {

                    if (gameplayScreen.getGameplayMode() == GameplayMode.LAZER)
                        _affectHealth();
                    else if (gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS)
                        _affectTimer();
                }

            }

            /*@Override
            public String toString() {
                return "lazerGunFadeInTween";
            }*/
        };

        //gameplayScreen.addToFinishWhenStoppingTheGameplay(initialFadeTween);
    }

    private void initializeLazerGlowShrinkingTween() {
        lazerGlowShrinkingTween = new Tween(LAZER_GLOW_SHRINKING_TWEEN_DURATION, LAZER_GLOW_SHRINKING_TWEEN_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();

                if (!gameplayScreen.isRewinding()) {
                    lazerGlow.setHeight(LAZER_GLOW_HEIGHT);

                    calculateLazerGlowHorizontalBounds();
                } else {
                    lazerGlow.setColor(1, 1, 1, 0);
                }
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

            /*@Override
            public String toString() {
                return "lazerGlowShrinkingTween";
            }*/
        };

        //gameplayScreen.addToFinishWhenStoppingTheGameplay(lazerGlowShrinkingTween);
    }

    private void initializeLazerBeamShrinkingTween() {
        lazerBeamShrinkingTween = new Tween(LAZER_BEAM_SHRINKING_TWEEN_DURATION, LAZER_BEAM_SHRINKING_TWEEN_INTERPOLATION) {
            @Override
            public void onStart() {
                super.onStart();
                if (!gameplayScreen.isRewinding()) {

                    lazerBeam.setColor(1, 1, 1, 1);

                    calculateLazerBeamHorizontalBounds();
                } else {
                    lazerBeam.setColor(1, 1, 1, 0);
                }
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float height = interpolation.apply(LAZER_BEAM_HEIGHT, LAZER_BEAM_HEIGHT/4f, percentage);
                //float heightMultiplier = MathUtils.random(LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_LOW, LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_HIGH);
                float heightMultiplier = LAZER_BLINKING_MOTION_INTERPOLATION.apply(LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_LOW, LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_HIGH, percentage);
                lazerBeam.setHeight(height * heightMultiplier);
                lazerBeam.setY(-lazerBeam.getHeight()/2f);

                /*float millisLeft = (1-percentage)*getDurationMillis();
                if (!lazerBeamVisibilityProbabilityTween.isStarted() & millisLeft <= LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_DURATION)
                    lazerBeamVisibilityProbabilityTween.start();*/
            }

            @Override
            public void onFinish() {
                super.onFinish();

                lazerBeam.setColor(1, 1, 1, 0);

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

            }

            /*@Override
            public String toString() {
                return "lazerBeamShrinkingTween";
            }*/
        };
    }

    private void initializeLazerBeamVisibilityProbabilityTween() {
        lazerBeamVisibilityProbabilityTween = new Tween(LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_DURATION, LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_INTERPOLATION) {

            @Override
            public void onStart() {
                super.onStart();

                if (!gameplayScreen.isRewinding())
                    lazerBeamVisibilityProbability = 1;
                /*else
                    lazerBeam.setColor(1, 1, 1, 0);*/

            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                lazerBeamVisibilityProbability = interpolation.apply(1, 0, percentage);
                if (LAZER_BLINKING_MOTION_INTERPOLATION.apply(percentage) > lazerBeamVisibilityProbability)
                    lazerBeam.setColor(1, 1, 1, 0);
                else lazerBeam.setColor(1, 1, 1, 1);
            }

            /*@Override
            public String toString() {
                return "lazerBeamVisibilityProbabilityTween";
            }*/
        };
    }

    private void initializeHaloArmorBlinkingTween() {
        haloArmorBlinkingTween = new Tween(LAZER_HALO_ARMOR_BLINKING_TWEEN_DURATION) {

            @Override
            public void onStart() {
                super.onStart();

                /*LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (!lazerAttackStuff.didThePlayerCollectAllArmorBullets()) {
                    finish();
                    //haloArmorFadeOutTween.finish();
                    haloArmor.setColor(1, 1, 1, 0);
                }*/
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                //float start = (1-lazerBeamVisibilityProbability)/(1-LAZER_HALO_ARMOR_BLINKING_START_ALPHA) + LAZER_HALO_ARMOR_BLINKING_START_ALPHA;
                //float start = 1-lazerBeamVisibilityProbability;
                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (lazerAttackStuff.didThePlayerCollectAllTheNecessaryArmorBullets()) {
                    float a = MathUtils.random(LAZER_HALO_ARMOR_BLINKING_START_ALPHA, 1);
                    haloArmor.setColor(1, 1, 1, a);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();

                //haloArmor.addAction(Actions.alpha(0, LAZER_ALPHA_ACTION_DURATION));
                //haloArmorFadeOutTween.start();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

            }

            /*@Override
            public String toString() {
                return "haloArmorBlinkingTween";
            }*/
        };
    }

    private void initializeFinalFadeTween() {
        finalFadeTween = new Tween((float) (LAZER_FADE_DURATION * MyMath.SECONDS_TO_MILLIS)) {

            @Override
            public void onStart() {
                super.onStart();

                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                lazerAttackStuff.getNextLazerAttackTimer().setPercentage(0);
                lazerAttackStuff.getNextLazerAttackTimer().onUpdate(0);
                lazerAttackStuff.getNextLazerAttackTimer().pause();
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float aOut = interpolation.apply(1, 0, percentage);
                float aIn = interpolation.apply(0, 1, percentage);

                gameplayScreen.getControllerLeft().setColor(1, 1, 1, aIn);
                gameplayScreen.getControllerRight().setColor(1, 1, 1, aIn);

                lazerGun.setColor(1, 1, 1, aOut);

                LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();
                if (lazerAttackStuff.didThePlayerCollectAllTheNecessaryArmorBullets())
                    haloArmor.setColor(1, 1, 1, aOut);

                if (lazerAttackStuff.didThePlayerCollectAllTheNecessaryArmorBullets())
                    lazerAttackStuff.getArmorGlowing().setColor(1, 1, 1, aOut);

                if (lazerAttackStuff.areThereAnyLazerAttacksLeft()) {
                    lazerAttackStuff.getNextLazerAttackTimerText().setColor(1, 1, 1, aIn);
                } else {
                    lazerAttackStuff.getArmorBlack().setColor(1, 1, 1, aOut);
                    lazerAttackStuff.getCollectedArmorBulletsText().setColor(1, 1, 1, aOut);
                }
            }

            /*@Override
            public String toString() {
                return "haloArmorFadeOutTween";
            }*/
        };
    }

    private void initializeLazerAttackTimeline() {

        lazerAttackTimeline = new Timeline(6) {

            @Override
            public void onStart() {
                super.onStart();
                LazerAttack.this.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                LazerAttack.this.onFinish();
            }
        };

        lazerAttackTimeline.addTimer(initialFadeTween, 0);
        lazerAttackTimeline.addTimer(lazerBeamShrinkingTween, initialFadeTween.getDurationMillis());
        lazerAttackTimeline.addTimer(lazerGlowShrinkingTween, initialFadeTween.getDurationMillis());
        lazerAttackTimeline.addTimer(lazerBeamVisibilityProbabilityTween, initialFadeTween.getDurationMillis() + lazerBeamShrinkingTween.getDurationMillis() - lazerBeamVisibilityProbabilityTween.getDurationMillis());
        lazerAttackTimeline.addTimer(haloArmorBlinkingTween, initialFadeTween.getDurationMillis());
        lazerAttackTimeline.addTimer(finalFadeTween, lazerAttackTimeline.getStartTimeOf(haloArmorBlinkingTween) + haloArmorBlinkingTween.getDurationMillis());

        gameplayScreen.addToFinishWhenStoppingTheGameplay(lazerAttackTimeline);
    }
}
