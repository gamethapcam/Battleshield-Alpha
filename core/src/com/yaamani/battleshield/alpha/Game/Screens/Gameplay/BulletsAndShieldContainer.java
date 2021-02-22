package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsAndShieldContainer extends Group implements Resizable {

    public static final String TAG = BulletsAndShieldContainer.class.getSimpleName();

    private Shield shield;
    private byte index;

    private boolean inUse;

    private LinkedList<Bullet> attachedBullets;

   /* private SimpleText rotationText;
    private SimpleText rotationNoMinusText;*/

    /*private int minusBulletsCount = 0;
    private int plusBulletsCount = 0;*/

    // private Array<Bullet> fakeWaveBullets;

    private RotationOmegaAlphaTween rotationOmegaAlphaTween; // When the number of shields is increased or decreased, this tween animate its BulletsAndShieldContainer object to the new omega and the new rotation.

    private float rotationWhenTheWaveStartsHittingDizziness;
    private float rotationWhenTheWaveStopsHittingDizziness;

    private Image portalEntrance;
    private Image portalExit;
    private boolean tweenEntrance; // false = tween exit.
    private boolean fadeIn; // false = fade out.
    private MyTween portalEntranceExitFadeInOutTween;
    private int portalPostProcessingEffectIndex;
    private Affine2 portalTransformationAff;
    private Matrix3 portalTransformationMat;
    private Vector3 portalTransformedVec;

    private GameplayScreen gameplayScreen;

    private SimpleText debugText;

    public BulletsAndShieldContainer(GameplayScreen gameplayScreen, Group containerOfContainers, byte index) {
        shield = new Shield(this, gameplayScreen);
        this.gameplayScreen = gameplayScreen;
        //gameplayScreen.addActor(this);
        containerOfContainers.addActor(this);
        this.index = index;

        attachedBullets = new LinkedList<>();

        initializeRotationOmegaAlphaTween(gameplayScreen);

        initializePortalEntrance();
        initializePortalExit();

        debugText = new SimpleText(gameplayScreen.getMyBitmapFont(), "");
        addActor(debugText);
        debugText.setVisible(true); // Set to true for debugging.
        debugText.setHeight(WORLD_SIZE/ /*20f*/ 45f);
        debugText.setPosition(-debugText.getWidth()/2f, 12f);
        //debugText.setRotation(90);


        /*rotationText = new SimpleText(gameplayScreen.getMyBitmapFont(), "");
        addActor(rotationText);
        rotationText.setBoundsHeight(0, WORLD_SIZE/3f, WORLD_SIZE/40f);
        rotationText.setColor(Color.WHITE);

        rotationNoMinusText = new SimpleText(gameplayScreen.getMyBitmapFont(), "");
        addActor(rotationNoMinusText);
        rotationNoMinusText.setBoundsHeight(0, WORLD_SIZE/3f + rotationText.getHeight()*1.2f, WORLD_SIZE/40f);
        rotationNoMinusText.setColor(Color.WHITE);*/

        initializePortalEntranceExitFadeInOutTween();

        portalTransformationAff = new Affine2();
        portalTransformationMat = new Matrix3();
        portalTransformedVec = new Vector3();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        rotationOmegaAlphaTween.update(delta);
        portalEntranceExitFadeInOutTween.update(delta);

        if (gameplayScreen.getGameplayMode() == GameplayMode.DIZZINESS | gameplayScreen.getGameplayMode() == GameplayMode.BIG_BOSS) {
            float portalEntranceAlpha = portalEntrance.getColor().a;
            float portalExitAlpha = portalExit.getColor().a;

            if (portalEntranceAlpha > 0 | portalExitAlpha > 0) {
                computePortalTransformedVec();
                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointPosition(
                            portalPostProcessingEffectIndex,
                            portalTransformedVec.x,
                            portalTransformedVec.y
                );
            }
        }

        if (debugText.isVisible())
            debugText.setCharSequence(index + ", " + MyMath.roundTo(MyMath.deg_0_to_360(getRotation() + gameplayScreen.getContainerOfContainers().getRotation()/* + 90*/), 2), true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /*rotationText.setCharSequence("" + getRotation(), true);
        rotationNoMinusText.setCharSequence("" + MyMath.deg_0_to_360(getRotation()), true);*/
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        //setPosition(worldWidth / 2f, worldHeight / 2f);
        //setPosition();
    }

    @Override
    @NotNull
    public String toString() {
        //return super.toString();
        return "(" + index + ", " + MyMath.roundTo(MyMath.deg_0_to_360(getRotation() + gameplayScreen.getContainerOfContainers().getRotation()/* + 90*/), 2) +")";
    }

    public Shield getShield() {
        return shield;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public byte getIndex() {
        return index;
    }

    public LinkedList<Bullet> getAttachedBullets() {
        return attachedBullets;
    }

    public RotationOmegaAlphaTween getRotationOmegaAlphaTween() {
        return rotationOmegaAlphaTween;
    }

    public float getRotationWhenTheWaveStartsHittingDizziness() {
        return rotationWhenTheWaveStartsHittingDizziness;
    }

    public void setRotationWhenTheWaveStartsHittingDizziness(float rotationWhenTheWaveStartsHittingDizziness) {
        this.rotationWhenTheWaveStartsHittingDizziness = rotationWhenTheWaveStartsHittingDizziness;
    }

    public float getRotationWhenTheWaveStopsHittingDizziness() {
        return rotationWhenTheWaveStopsHittingDizziness;
    }

    public void setRotationWhenTheWaveStopsHittingDizziness(float rotationWhenTheWaveStopsHittingDizziness) {
        this.rotationWhenTheWaveStopsHittingDizziness = rotationWhenTheWaveStopsHittingDizziness;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    //------------------------------------------------------------

    public void startRotationOmegaAlphaTween() {
        rotationOmegaAlphaTween.start();
    }

    public void setOldRotationDeg(float oldRotationDeg) {
        rotationOmegaAlphaTween.setOldRotationDeg(oldRotationDeg);
    }

    public void setNewRotationDeg(float newRotationDeg) {
        rotationOmegaAlphaTween.setNewRotationDeg(newRotationDeg);
    }

    public void setNewOmegaDeg(float newOmegaDeg) {
        rotationOmegaAlphaTween.setNewOmegaDeg(newOmegaDeg);
    }

    public void setNewAlpha(float newAlpha) {
        rotationOmegaAlphaTween.setNewAlpha(newAlpha);
    }

    public void showPortalEntrance() {
        //portalEntrance.addAction(Actions.alpha(1, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
        tweenEntrance = true;
        fadeIn = true;
        startPortalEntranceExitFadeInOutTween(0);

        computePortalTransformedVec();
        portalPostProcessingEffectIndex = gameplayScreen.getPortalPostProcessingEffect().addPortalPoint(
                portalTransformedVec.x,
                portalTransformedVec.y,
                0
        );
    }

    public void showPortalExit() {
        //portalExit.addAction(Actions.alpha(1, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
        tweenEntrance = false;
        fadeIn = true;
        startPortalEntranceExitFadeInOutTween(0);

        computePortalTransformedVec();
        portalPostProcessingEffectIndex = gameplayScreen.getPortalPostProcessingEffect().addPortalPoint(
                portalTransformedVec.x,
                portalTransformedVec.y,
                0
        );
    }

    public Matrix3 computePortalTransformationMat() {
        Group containerOfContainers = gameplayScreen.getContainerOfContainers();
        portalTransformationAff.idt()
                .preRotate( MyMath.deg_0_to_360(containerOfContainers.getRotation() + getRotation()) )
                .preTranslate(containerOfContainers.getX(), containerOfContainers.getY());
        portalTransformationMat.set(portalTransformationAff);

        //Gdx.app.log(TAG, "portalTransformationMat = \n" + portalTransformationMat.toString());

        return portalTransformationMat;
    }

    public Vector3 computePortalTransformedVec() {
        portalTransformedVec.x = portalEntrance.getX() + portalEntrance.getWidth()/2f;
        portalTransformedVec.y = portalEntrance.getY() + portalEntrance.getWidth()/2f;
        portalTransformedVec.z = 1;


        portalTransformedVec.mul(computePortalTransformationMat());
        //Gdx.app.log(TAG, "(x, y) = " + portalTransformedVec.x + ", " + portalTransformedVec.y);

        return portalTransformedVec;
    }

   /* public float computePortalTransformedX(Matrix4 transformationMat) {
        // Left multiply the vector [getX(), getY(), 1] by the transformationMat matrix.

        final float[] l_mat = transformationMat.val;
        float x = portalEntrance.getX() + portalEntrance.getWidth()/2f;
        float y = portalEntrance.getY() + portalEntrance.getWidth()/2f;
        float z = 1;

        float transformedX = x * l_mat[Matrix4.M00]  +  y * l_mat[Matrix4.M01]  +  z * l_mat[Matrix4.M02]  +  l_mat[Matrix4.M03];
        Gdx.app.log(TAG, "transformedX = " + transformedX);
        return transformedX;
    }

    public float computePortalTransformedY(Matrix4 transformationMat) {
        // Left multiply the vector [getX(), getY(), 1] by the transformationMat matrix.

        final float[] l_mat = transformationMat.val;
        float x = portalEntrance.getX() + portalEntrance.getWidth()/2f;
        float y = portalEntrance.getY() + portalEntrance.getWidth()/2f;
        float z = 1;

        float transformedY = x * l_mat[Matrix4.M10]  +  y * l_mat[Matrix4.M11]  +  z * l_mat[Matrix4.M12]  +  l_mat[Matrix4.M13];
        Gdx.app.log(TAG, "transformedY = " + transformedY);
        return transformedY;
    }*/

    public void hidePortalEntrance() {
        //portalEntrance.addAction(Actions.alpha(0, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
        tweenEntrance = true;
        fadeIn = false;
        startPortalEntranceExitFadeInOutTween(0);
    }

    public void hidePortalExit() {
        //portalExit.addAction(Actions.alpha(0, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
        tweenEntrance = false;
        fadeIn = false;
        startPortalEntranceExitFadeInOutTween(PORTALS_CONTAINER_HIDE_PORTAL_EXIT_DELAY);
    }

    public boolean isPortalEntranceExitFadeInOutTweenStarted() {
        return portalEntranceExitFadeInOutTween.isStarted();
    }

    public void startPortalEntranceExitFadeInOutTween(float delay) {
        if (fadeIn) {
            portalEntranceExitFadeInOutTween.setInitialVal(0);
            portalEntranceExitFadeInOutTween.setFinalVal(1);
        } else {
            portalEntranceExitFadeInOutTween.setInitialVal(1);
            portalEntranceExitFadeInOutTween.setFinalVal(0);
        }
        portalEntranceExitFadeInOutTween.start(delay);
    }

    public void pausePortalEntranceExitFadeInOutTweenGradually(float gradualPausingDurationMillis) {
        portalEntranceExitFadeInOutTween.pauseGradually(gradualPausingDurationMillis);
    }

    public void cleanContainer() {
        attachedBullets.clear();
        //hidePortalEntrance();
        //hidePortalExit();
        portalEntrance.setColor(1, 1, 1, 0);
        portalExit.setColor(1, 1, 1, 0);
    }

    private void initializeRotationOmegaAlphaTween(GameplayScreen gameplayScreen) {
        rotationOmegaAlphaTween = new RotationOmegaAlphaTween(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION, MyInterpolation.myExp10);
        gameplayScreen.addToFinishWhenStoppingTheGameplay(rotationOmegaAlphaTween);
    }

    private void initializePortalEntrance() {
        portalEntrance = new Image(Assets.instance.gameplayAssets.portalEntrance);
        addActor(portalEntrance);

        portalEntrance.setSize(PORTALS_ENTRANCE_EXIT_DIAMETER, PORTALS_ENTRANCE_EXIT_DIAMETER);
        portalEntrance.setX(-portalEntrance.getWidth()/2f);
        portalEntrance.setY(D_PORTALS_ENTRANCE_EXIT_POSITION - portalEntrance.getHeight()/2f);

        //portalEntrance.setVisible(false);
        portalEntrance.setColor(1, 1, 1, 0);
        //portalEntrance.setDebug(true);
    }

    private void initializePortalExit() {
        portalExit = new Image(Assets.instance.gameplayAssets.portalExit);
        addActor(portalExit);

        portalExit.setSize(PORTALS_ENTRANCE_EXIT_DIAMETER, PORTALS_ENTRANCE_EXIT_DIAMETER);
        portalExit.setX(-portalExit.getWidth()/2f);
        portalExit.setY(D_PORTALS_ENTRANCE_EXIT_POSITION - portalExit.getHeight()/2f);

        //portalExit.setVisible(false);
        portalExit.setColor(1, 1, 1, 0);

        //portalExit.setDebug(true);
    }

    private void initializePortalEntranceExitFadeInOutTween() {
        portalEntranceExitFadeInOutTween = new MyTween(PORTALS_CONTAINER_PORTAL_ALPHA_DURATION, PORTALS_CONTAINER_PORTAL_ALPHA_INTERPOLATION) {
            @Override
            public void onStart() {
                super.onStart();

                Timer rewindBulletFirstStage = gameplayScreen.getBulletsHandler().getRewindBulletFirstStage();
                if (rewindBulletFirstStage.isStarted())
                    pauseGradually((1-rewindBulletFirstStage.getPercentage()) * rewindBulletFirstStage.getDurationMillis());
            }

            /*@Override
            public void tween(float percentage, Interpolation interpolation) {

                float alpha = fadeIn ? interpolation.apply(0, 1, percentage) : interpolation.apply(1, 0, percentage);

                if (tweenEntrance)
                    portalEntrance.setColor(1, 1, 1, alpha);
                else
                    portalExit.setColor(1, 1, 1, alpha);

                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointIntensity(
                            portalPostProcessingEffectIndex,
                            alpha
                    );
            }*/

            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {

                float alpha = myInterpolation.apply(startX, endX, startY, endY, currentX);

                if (tweenEntrance)
                    portalEntrance.setColor(1, 1, 1, alpha);
                else
                    portalExit.setColor(1, 1, 1, alpha);

                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointIntensity(
                            portalPostProcessingEffectIndex,
                            alpha
                    );
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!fadeIn) {

                    if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                        gameplayScreen.getPortalPostProcessingEffect().removePortalPoint(portalPostProcessingEffectIndex);

                    if (!tweenEntrance)
                        gameplayScreen.getBulletsHandler().portalIsOver();

                }
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------------------------------------------------

    public class RotationOmegaAlphaTween extends Tween {

        public final String TAG = BulletsAndShieldContainer.class.getSimpleName() + "." + RotationOmegaAlphaTween.class.getSimpleName();

        private float oldRotationDeg;
        private Float newRotationDeg;

        private float oldOmegaDeg;
        private Float newOmegaDeg;

        private float oldAlpha;
        private Float newAlpha;

        private RotationOmegaAlphaTween(float durationMillis, Interpolation interpolation) {
            super(durationMillis, interpolation);

            /*oldRotationDeg = BulletsAndShieldContainer.this.getRotation();*/
            oldOmegaDeg = BulletsAndShieldContainer.this.getShield().getOmegaDeg();
            oldAlpha = newAlpha = 0f;
        }

        @Override
        public void onStart() {
            super.onStart();

            //oldRotationDeg = BulletsAndShieldContainer.this.getRotation();
            //oldOmegaDeg = BulletsAndShieldContainer.this.getShield().getOmegaDeg();
            //oldAlpha = newAlpha = 0f;

        }

        @Override
        public void tween(float percentage, Interpolation interpolation) {
            //Interpolation interpolation = MyInterpolation.myExp10;
            BulletsAndShieldContainer.this.getColor().a = interpolation.apply(oldAlpha, newAlpha, percentage);

            float portalEntranceAlpha = portalEntrance.getColor().a;
            float portalExitAlpha = portalExit.getColor().a;

            if (portalEntranceAlpha > 0) {
                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointIntensity(
                        portalPostProcessingEffectIndex,
                        portalEntranceAlpha
                    );
            } else if (portalExitAlpha > 0) {
                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointIntensity(
                        portalPostProcessingEffectIndex,
                        portalExitAlpha
                    );
            }

            if (newOmegaDeg == null | newRotationDeg == null) return;
            BulletsAndShieldContainer.this.getShield().setOmegaDeg(interpolation.apply(oldOmegaDeg, newOmegaDeg, percentage));
            BulletsAndShieldContainer.this.setRotation(interpolation.apply(oldRotationDeg, newRotationDeg, percentage));


            if (portalEntranceAlpha > 0 | portalExitAlpha > 0) {
                computePortalTransformedVec();
                if (gameplayScreen.getPortalPostProcessingEffect().getLastUsedIndex() >= 0)
                    gameplayScreen.getPortalPostProcessingEffect().setPortalPointPosition(
                            portalPostProcessingEffectIndex,
                            portalTransformedVec.x,
                            portalTransformedVec.y
                    );
            }


            if (index == 0) {//Don't repeat for every BulletsAndShieldContainer
                if (percentage >= 0.6f)
                    gameplayScreen.getShieldsAndContainersHandler().updateStartingAndEndingAngles();
            }
        }

        public void setOldRotationDeg(float oldRotationDeg) {
            this.oldRotationDeg = oldRotationDeg;
        }

        private void setNewRotationDeg(float newRotationDeg) {
            if (this.newRotationDeg != null) this.oldRotationDeg = this.newRotationDeg;
            this.newRotationDeg = newRotationDeg;
        }

        private void setNewOmegaDeg(float newOmegaDeg) {
            if (this.newOmegaDeg != null) this.oldOmegaDeg = this.newOmegaDeg;
            this.newOmegaDeg = newOmegaDeg;
        }

        private void setNewAlpha(float newAlpha) {
            if (this.newAlpha != null) this.oldAlpha = this.newAlpha;
            this.newAlpha = newAlpha;
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (BulletsAndShieldContainer.this.getColor().a == 0) {
                BulletsAndShieldContainer.this.setVisible(false);
                cleanContainer();
            }
        }
    }
}
