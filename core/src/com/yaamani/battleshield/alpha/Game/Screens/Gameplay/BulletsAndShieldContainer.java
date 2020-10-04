package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BulletsAndShieldContainer extends Group implements Resizable {

    public static final String TAG = BulletsAndShieldContainer.class.getSimpleName();

    private Shield shield;
    private byte index;

   /* private SimpleText rotationText;
    private SimpleText rotationNoMinusText;*/

    /*private int minusBulletsCount = 0;
    private int plusBulletsCount = 0;*/

    // private Array<Bullet> fakeWaveBullets;

    private RotationOmegaAlphaTween rotationOmegaAlphaTween; // When the number of shields is increased or decreased, this tween animate its BulletsAndShieldContainer object to the new omega and the new rotation.

    private PortalWaveType portalWaveType;
    private Image portalEntrance;
    private Image portalExit;
    private Bullet currentAttachedWaveFirstBullet;
    private Bullet currentAttachedWaveLastBullet;
    //private Tween portalTween;

    private GameplayScreen gameplayScreen;

    private SimpleText debugText;

    public BulletsAndShieldContainer(GameplayScreen gameplayScreen, Group containerOfContainers, byte index) {
        shield = new Shield(this, gameplayScreen);
        this.gameplayScreen = gameplayScreen;
        //gameplayScreen.addActor(this);
        containerOfContainers.addActor(this);
        this.index = index;

        initializePortalEntrance();
        initializePortalExit();
        //initializePortalTween();

        initializeRotationOmegaAlphaTween(gameplayScreen);

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
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        rotationOmegaAlphaTween.update(delta);





        if (portalWaveType != null)
            switch (portalWaveType) {

                case PORTAL_ENTRANCE:

                    if (portalEntrance.getColor().a == 1 & currentAttachedWaveLastBullet.getY() > - D_PORTALS_ENTRANCE_EXIT_POSITION) {
                        portalEntrance.addAction(Actions.alpha(0, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
                    }

                    break;

                case PORTAL_EXIT:

                    if (portalExit.getColor().a == 0 & currentAttachedWaveFirstBullet.getY() > - D_PORTALS_ENTRANCE_EXIT_POSITION - BULLETS_ORDINARY_HEIGHT) {
                        portalExit.addAction(Actions.alpha(1, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
                    }
                    else if (portalExit.getColor().a == 1 & currentAttachedWaveLastBullet.getY() > - D_PORTALS_ENTRANCE_EXIT_POSITION) {
                        portalExit.addAction(Actions.alpha(0, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
                    }

                    break;

            }





        if (debugText.isVisible())
            debugText.setCharSequence(index + ", " + MyMath.roundTo(MyMath.deg_0_to_360(getRotation() + gameplayScreen.getContainerOfContainers().getRotation() + 90), 2), true);

        /*if (shield.isOn())
            portalEntrance.setVisible(true);
        else
            portalEntrance.setVisible(false);*/
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

    public Shield getShield() {
        return shield;
    }

    public byte getIndex() {
        return index;
    }

    public RotationOmegaAlphaTween getRotationOmegaAlphaTween() {
        return rotationOmegaAlphaTween;
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

    public PortalWaveType getPortalWaveType() {
        return portalWaveType;
    }

    public void setPortalWaveType(PortalWaveType portalWaveType) {
        this.portalWaveType = portalWaveType;

        if (portalWaveType == PortalWaveType.PORTAL_ENTRANCE) {
            //portalEntrance.setVisible(true);
            portalEntrance.addAction(Actions.alpha(1, PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION));
        }
    }

    public Bullet getCurrentAttachedWaveFirstBullet() {
        return currentAttachedWaveFirstBullet;
    }

    public void setCurrentAttachedWaveFirstBullet(Bullet currentAttachedWaveFirstBullet) {
        this.currentAttachedWaveFirstBullet = currentAttachedWaveFirstBullet;
    }

    public Bullet getCurrentAttachedWaveLastBullet() {
        return currentAttachedWaveLastBullet;
    }

    public void setCurrentAttachedWaveLastBullet(Bullet currentAttachedWaveLastBullet) {
        this.currentAttachedWaveLastBullet = currentAttachedWaveLastBullet;
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    private void initializeRotationOmegaAlphaTween(GameplayScreen gameplayScreen) {
        rotationOmegaAlphaTween = new RotationOmegaAlphaTween(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION, MyInterpolation.myExp10);
        gameplayScreen.addToFinishWhenStoppingTheGameplay(rotationOmegaAlphaTween);
    }

    private void initializePortalEntrance() {
        portalEntrance = new Image(Assets.instance.gameplayAssets.portalEntrance);
        addActor(portalEntrance);

        portalEntrance.setSize(PORTALS_ENTRANCE_EXIT_WIDTH, PORTALS_ENTRANCE_EXIT_HEIGHT);
        portalEntrance.setX(-portalEntrance.getWidth()/2f);
        portalEntrance.setY(D_PORTALS_ENTRANCE_EXIT_POSITION - portalEntrance.getHeight()/2f);

        //portalEntrance.setVisible(false);
        portalEntrance.setColor(1, 1, 1, 0);
        //portalEntrance.setDebug(true);
    }

    private void initializePortalExit() {
        portalExit = new Image(Assets.instance.gameplayAssets.portalExit);
        addActor(portalExit);

        portalExit.setSize(PORTALS_ENTRANCE_EXIT_WIDTH, PORTALS_ENTRANCE_EXIT_HEIGHT);
        portalExit.setX(-portalExit.getWidth()/2f);
        portalExit.setY(D_PORTALS_ENTRANCE_EXIT_POSITION - portalExit.getHeight()/2f);

        //portalExit.setVisible(false);
        portalExit.setColor(1, 1, 1, 0);

        //portalExit.setDebug(true);
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

            if (newOmegaDeg == null | newRotationDeg == null) return;
            BulletsAndShieldContainer.this.getShield().setOmegaDeg(interpolation.apply(oldOmegaDeg, newOmegaDeg, percentage));
            BulletsAndShieldContainer.this.setRotation(interpolation.apply(oldRotationDeg, newRotationDeg, percentage));

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
            if (BulletsAndShieldContainer.this.getColor().a == 0)
                BulletsAndShieldContainer.this.setVisible(false);
        }
    }
}
