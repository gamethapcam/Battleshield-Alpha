package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
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

    public BulletsAndShieldContainer(GameplayScreen gameplayScreen, Group containerOfContainers, byte index) {
        shield = new Shield(this, gameplayScreen);
        //gameplayScreen.addActor(this);
        containerOfContainers.addActor(this);
        this.index = index;

        initializeRotationOmegaAlphaTween(gameplayScreen);

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

    private void initializeRotationOmegaAlphaTween(GameplayScreen gameplayScreen) {
        rotationOmegaAlphaTween = new RotationOmegaAlphaTween(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION, MyInterpolation.myExp10);
        gameplayScreen.addToFinishWhenLosing(rotationOmegaAlphaTween);
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
