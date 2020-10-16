package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;

import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class RestrictedController extends Controller {

    public static final String TAG = RestrictedController.class.getSimpleName();

    //private RoundedArch bg;
    private Image bg;
    //private RoundedArch[] shieldsRepresentation;

    private float archRadius; //Inches
    private float archInnerRadiusRatio;
    private float archAngle;

    private boolean mirror;

    private SimpleText outputAngleText; //Debugging

    public RestrictedController(GameplayScreen gameplayScreen, Image stick, float archRadius, float archInnerRadiusRatio, float archAngle, Direction controllerPosition, NetworkAndStorageManager networkAndStorageManager) {
        super(gameplayScreen, stick, controllerPosition, networkAndStorageManager);

        this.archRadius = archRadius;
        this.archInnerRadiusRatio = archInnerRadiusRatio;
        this.archAngle = archAngle;

        initializeBg();

        //bg.setDebug(true);

        //initializeShieldsRepresentation();

        moveTheStickAccordingToTheStickAngle();



        outputAngleText = new SimpleText(gameplayScreen.getMyBitmapFont(), "");
        addActor(outputAngleText);
        outputAngleText.setVisible(false); // Set to true for debugging.
        outputAngleText.setHeight(WORLD_SIZE/20f);
        outputAngleText.setPosition(0, 0);
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------

    @Override
    public void act(float delta) {
        super.act(delta);

        if (outputAngleText.isVisible())
            outputAngleText.setCharSequence("" + ((getOutputAngle() != null) ? getOutputAngle() * MathUtils.radDeg : ""), true);

        //if (getControllerPosition() == Direction.RIGHT) bg.setX(bg.getX()-0.1f);a
        //bg.rotateBy(0.4f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /*if (mirror)
            batch.setColor(Color.BLUE);*/

        super.draw(batch, parentAlpha);

        /*if (mirror)
            batch.setColor(Color.WHITE);*/

    }

    @Override
    protected void calculateNewSizeInWorldUnits() {
        //calculateRadiusInnerRadius(bg);
        setBgSize();

        float T = CONTROLLER_RESTRICTED_ARCH_RADIUS - CONTROLLER_RESTRICTED_ARCH_RADIUS*CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO;
        float stickDiameter = (toWorldCoordinates(T * Gdx.graphics.getPpiX(), Dimension.X, getStage().getViewport())) / CONTROLLER_FREE_STICK_RATIO;

        stick.setSize(stickDiameter, stickDiameter);
    }

    @Override
    protected void calculateNewPositionsInWorldUnits(float marginInWorldUnits) {
        //bg.setY(marginInWorldUnits-(bg.getRadius() - bg.getRadius()*MathUtils.sin(archAngle/2f)));

        Viewport viewport = getStage().getViewport();
        bg.setY(viewport.getWorldHeight()/2f - bg.getHeight()/2f);

        if (getControllerPosition() == Direction.RIGHT) {
            bg.setX(getWidth() - bg.getWidth() - marginInWorldUnits/*0*/);
        } else {
            //bg.setRotation(0);
            bg.setX(marginInWorldUnits);

            //bg.setPosition(0, 0);
            //bg.setRotation(180);
        }
    }

    @Override
    protected void moveTheStickAccordingToTheStickAngle() {
        if (stickAngle == null) {
            stick.setY(-getStage().getViewport().getWorldHeight()); //Hide the stick
            return;
        }

        float stickRadius = stick.getWidth()/2f;

        float bgRadius = /*bg.getWidth()/2f*/ toWorldCoordinates(CONTROLLER_RESTRICTED_ARCH_RADIUS * Gdx.graphics.getPpiX(), Dimension.X, getStage().getViewport());
        float bgCentrePointY = /*bg.getY() + bgRadius*/ bg.getY() + bg.getHeight()/2f;
        float bgCentrePointX;
        if (getControllerPosition() == Direction.RIGHT) {
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() + bg.getWidth() - bgRadius;

            stick.setPosition((bgRadius-stickRadius/2f)* MathUtils.cos(stickAngle) - stickRadius + bgCentrePointX,
                    (bgRadius-stickRadius/2f)*MathUtils.sin(stickAngle) - stickRadius + bgCentrePointY);
        }
        else {
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() - bg.getWidth() + bgRadius;

            stick.setPosition((bgRadius-stickRadius/2f)* MathUtils.cos(stickAngle) + stickRadius + bgCentrePointX,
                    (bgRadius-stickRadius/2f)*MathUtils.sin(stickAngle) - stickRadius + bgCentrePointY);
        }

    }

    @Override
    protected void touchDragged(InputEvent event, float x, float y, int pointer) {
        float bgRadius = /*bg.getWidth()/2f*/ toWorldCoordinates(CONTROLLER_RESTRICTED_ARCH_RADIUS * Gdx.graphics.getPpiX(), Dimension.X, getStage().getViewport());

        float bgCentrePointX;
        if (getControllerPosition() == Direction.RIGHT)
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() + bg.getWidth() - bgRadius;
        else
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() - bg.getWidth() + bgRadius;


        float bgCentrePointY = /*bg.getY() + bgRadius*/ bg.getY() + bg.getHeight()/2f;
        float xAccordingToTheCenterToTheBG = x - bgCentrePointX;
        float yAccordingToTheCenterToTheBG = y - bgCentrePointY;

        stickAngle = MathUtils.atan2(yAccordingToTheCenterToTheBG, xAccordingToTheCenterToTheBG);
        stickAngleCorrection();
        calculateOutputAngleFromStickAngle();
    }

    @Override
    protected void gamePadPooling(float rightStickFirstAxis, float rightStickSecondAxis, float leftStickFirstAxis, float leftStickSecondAxis) {
        if (!gamePadUsingRightOrLeftAxis(rightStickFirstAxis, rightStickSecondAxis, leftStickFirstAxis, leftStickSecondAxis)) {
            stickAngle = null;
            moveTheStickAccordingToTheStickAngle();
            return;
        }

        if (getControllerPosition() == Direction.RIGHT) {
            stickAngle = MathUtils.atan2(rightStickSecondAxis, rightStickFirstAxis);
            stickAngle = MathUtils.clamp(stickAngle, -MathUtils.PI/2f * 0.9999f, MathUtils.PI/2f * 0.9999f);
        } else {
            stickAngle = MathUtils.atan2(leftStickSecondAxis, leftStickFirstAxis);
            if (stickAngle > 0) {
                stickAngle = MathUtils.clamp(stickAngle, MathUtils.PI/2f * 1.00001f, MathUtils.PI);
            }
            else stickAngle = MathUtils.clamp(stickAngle, -MathUtils.PI, -MathUtils.PI/2f);
        }

        calculateOutputAngleFromStickAngle();

        moveTheStickAccordingToTheStickAngle();
    }

    @Override
    protected void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        stickAngle = null;
        outputAngle = null;
        moveTheStickAccordingToTheStickAngle();
    }

    @Override
    public Float getOutputAngle() {
        Float oa = super.getOutputAngle();
        //return oa;

        if (oa == null) return null;

        if (mirror) {
            if (getControllerPosition() == Direction.LEFT)
                return Math.signum(oa) * MathUtils.PI - oa;
            else {
                if (oa >= 0)
                    return MathUtils.PI - oa;
                else
                    return -MathUtils.PI - oa;
            }
        } else
            return oa;
    }

    /*@Override
    public Float getOutputAngle() {
        if (!isUsingTouch()) // using gamepad
            return super.getOutputAngle();


        if (outputAngle == null) {
            return outputAngle;
        }


        if (getControllerPosition() == Direction.RIGHT) {
            if (outputAngle >= 0)
                return MathUtils.lerp(0, MathUtils.PI / 2f * 0.9999f, outputAngle / (archAngle / 2f));
            return MathUtils.lerp(0, -MathUtils.PI / 2f * 0.9999f, -outputAngle / (archAngle / 2f));
        } else {
            if (outputAngle >= 0)
                return MathUtils.lerp(MathUtils.PI/2f * 1.00001f, MathUtils.PI, (outputAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
            return MathUtils.lerp(-MathUtils.PI/2f, -MathUtils.PI, (-outputAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
        }
    }*/

    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------

    /*private void calculateRadiusInnerRadius(RoundedArch roundedArch) {
        Viewport viewport = getStage().getViewport();
        roundedArch.setRadius(toWorldCoordinates(Gdx.graphics.getPpiX() * archRadius, Dimension.X, viewport));
    }*/

    private void stickAngleCorrection() {
        if (getControllerPosition() == Direction.RIGHT)
            stickAngle = MathUtils.clamp(stickAngle, -archAngle/2f, archAngle/2f);
        else {
            if (stickAngle > 0)
                stickAngle = MathUtils.clamp(stickAngle, MathUtils.PI-archAngle/2f, MathUtils.PI);
            else stickAngle = MathUtils.clamp(stickAngle, -MathUtils.PI, -(MathUtils.PI-archAngle/2f));
        }

       /* Gdx.app.log(TAG, "angle = " + angle*MathUtils.radDeg
                + ", getAngle() = " + getAngle()*MathUtils.radDeg
                + ", progress = " + (angle - (MathUtils.PI - archAngle/2f)) / (archAngle / 2f));*/
    }

    public void calculateOutputAngleFromStickAngle() {

        if (stickAngle == null) {
            outputAngle = null;
            return;
        }

        if (getControllerPosition() == Direction.RIGHT) {

            calculateOutputAngleFromStickAngleRight();

        } else {

            calculateOutputAngleFromStickAngleLeft();

        }
    }

    private void calculateOutputAngleFromStickAngleRight() {
        if (stickAngle >= 0)
            outputAngle = MathUtils.lerp(0, (90 - CONTROLLER_RESTRICTED_90_SHIFT) * MathUtils.degRad, stickAngle / (archAngle / 2f));
            //outputAngle = stickAngle / (archAngle / 2f) * MathUtils.PI / 2f;
        else
            outputAngle = MathUtils.lerp(0, -(90 - CONTROLLER_RESTRICTED_90_SHIFT) * MathUtils.degRad, -stickAngle / (archAngle / 2f));
        //outputAngle = -stickAngle / (archAngle / 2f) * -MathUtils.PI / 2f;
    }

    private void calculateOutputAngleFromStickAngleLeft() {
        if (stickAngle >= 0)
            //outputAngle = MathUtils.lerp(MathUtils.PI/2f * 1.25f, MathUtils.PI * 1.25f, (stickAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
            outputAngle = MathUtils.lerp((90 + CONTROLLER_RESTRICTED_90_SHIFT) * MathUtils.degRad, MathUtils.PI, (stickAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
        else
            //outputAngle = MathUtils.lerp(-MathUtils.PI/2f * 1.25f, -MathUtils.PI * 1.25f, (-stickAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
            outputAngle = MathUtils.lerp(-(90 + CONTROLLER_RESTRICTED_90_SHIFT) * MathUtils.degRad, -MathUtils.PI, (-stickAngle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
    }

    private void setBgSize() {
        float R = CONTROLLER_RESTRICTED_ARCH_RADIUS;
        float T = R - R * CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO;
        float theta = CONTROLLER_RESTRICTED_ARCH_ANGLE - 2*(T/2f / (R+T/2f));

        float widthInInches = R + 3f*T/2f - (R + T/2f) * (float) Math.cos(theta/2f);
        float heightInInches = (float) (2*(R + T/2f)*Math.sin(theta/2f)) + T;

        bg.setSize(
                toWorldCoordinates(widthInInches * Gdx.graphics.getPpiX(), Dimension.X, getStage().getViewport()),
                toWorldCoordinates(heightInInches * Gdx.graphics.getPpiY(), Dimension.Y, getStage().getViewport())
        );
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeBg() {
        /*bg = new RoundedArch(Assets.instance.gameplayAssets.freeControllerBG, Arch.AngleIncreaseDirection.THE_POSITIVE_DIRECTION_OF_THE_X_AXIS, 0, archInnerRadiusRatio);
        calculateRadiusInnerRadius(bg);
        bg.setAngle(archAngle);
        bg.setOrigin(bg.getWidth()/2f, bg.getHeight()/2f);
        if (getControllerPosition() == Direction.RIGHT)
            bg.setRotation(-archAngle/2f * MathUtils.radDeg);
        else bg.setRotation(180 - archAngle/2f * MathUtils.radDeg);*/

        if (getControllerPosition() == Direction.LEFT)
            bg = new Image(Assets.instance.gameplayAssets.restrictedControllerLeftBG);
        else if (getControllerPosition() == Direction.RIGHT)
            bg = new Image(Assets.instance.gameplayAssets.restrictedControllerRightBG);

        setBgSize();

        //Gdx.app.log(TAG, "bg.getWidth() = " + bg.getWidth() + ", bg.getHeight() = " + bg.getHeight());

        addActorBefore(stick, bg);
    }

    private void initializeShieldsRepresentation() {

    }
}
