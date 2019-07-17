package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.Arch;
import com.yaamani.battleshield.alpha.MyEngine.RoundedArch;

import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;
import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.resolutionIntoWorldUnits;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class RestrictedController extends Controller {

    public static final String TAG = RestrictedController.class.getSimpleName();

    //private RoundedArch bg;
    private Image bg;
    //private RoundedArch[] shieldsRepresentation;

    private float archRadius; //Inches
    private float archInnerRadiusRatio;
    private float archAngle;

    public RestrictedController(GameplayScreen gameplayScreen, Image stick, float archRadius, float archInnerRadiusRatio, float archAngle, Constants.Direction controllerPosition) {
        super(gameplayScreen, stick, controllerPosition);

        this.archRadius = archRadius;
        this.archInnerRadiusRatio = archInnerRadiusRatio;
        this.archAngle = archAngle;

        initializeBg();

        //bg.setDebug(true);

        //initializeShieldsRepresentation();

        moveTheStickAccordingToTheAngle();
    }

    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------
    //------------------------------ super class methods ------------------------------

    @Override
    public void act(float delta) {
        super.act(delta);

        //if (getControllerPosition() == Direction.RIGHT) bg.setX(bg.getX()-0.1f);a
        //bg.rotateBy(0.4f);
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
    protected void moveTheStickAccordingToTheAngle() {
        if (angle == null) {
            stick.setY(-getStage().getViewport().getWorldHeight()); //Hide the stick
            return;
        }

        float stickRadius = stick.getWidth()/2f;

        float bgRadius = /*bg.getWidth()/2f*/ toWorldCoordinates(CONTROLLER_RESTRICTED_ARCH_RADIUS * Gdx.graphics.getPpiX(), Dimension.X, getStage().getViewport());
        float bgCentrePointY = /*bg.getY() + bgRadius*/ bg.getY() + bg.getHeight()/2f;
        float bgCentrePointX;
        if (getControllerPosition() == Direction.RIGHT) {
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() + bg.getWidth() - bgRadius;

            stick.setPosition((bgRadius-stickRadius/2f)* MathUtils.cos(angle) - stickRadius + bgCentrePointX,
                    (bgRadius-stickRadius/2f)*MathUtils.sin(angle) - stickRadius + bgCentrePointY);
        }
        else {
            bgCentrePointX = /*bg.getX() + bgRadius*/ bg.getX() - bg.getWidth() + bgRadius;

            stick.setPosition((bgRadius-stickRadius/2f)* MathUtils.cos(angle) + stickRadius + bgCentrePointX,
                    (bgRadius-stickRadius/2f)*MathUtils.sin(angle) - stickRadius + bgCentrePointY);
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

        angle = MathUtils.atan2(yAccordingToTheCenterToTheBG, xAccordingToTheCenterToTheBG);
        angleCorrection();
    }

    @Override
    protected void gamePadPooling(float rightStickFirstAxis, float rightStickSecondAxis, float leftStickFirstAxis, float leftStickSecondAxis) {
        if (!gamepadUsingRightOrLeftAxis(rightStickFirstAxis, rightStickSecondAxis, leftStickFirstAxis, leftStickSecondAxis)) {
            angle = null;
            moveTheStickAccordingToTheAngle();
            return;
        }

        if (getControllerPosition() == Direction.RIGHT) {
            angle = MathUtils.atan2(rightStickSecondAxis, rightStickFirstAxis);
            angle = MathUtils.clamp(angle, -MathUtils.PI/2f * 0.9999f, MathUtils.PI/2f * 0.9999f);
        } else {
            angle = MathUtils.atan2(leftStickSecondAxis, leftStickFirstAxis);
            if (angle > 0) {
                angle = MathUtils.clamp(angle, MathUtils.PI/2f * 1.00001f, MathUtils.PI);
            }
            else angle = MathUtils.clamp(angle, -MathUtils.PI, -MathUtils.PI/2f);
        }

        moveTheStickAccordingToTheAngle();
    }

    @Override
    protected void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        angle = null;
        moveTheStickAccordingToTheAngle();
    }

    @Override
    public Float getAngle() {
        if (!isUsingTouch()) // using gamepad
            return super.getAngle();


        if (angle == null) {
            return angle;
        }


        if (getControllerPosition() == Direction.RIGHT) {
            if (angle >= 0)
                return MathUtils.lerp(0, MathUtils.PI / 2f * 0.9999f, angle / (archAngle / 2f));
            return MathUtils.lerp(0, -MathUtils.PI / 2f * 0.9999f, -angle / (archAngle / 2f));
        } else {
            if (angle >= 0)
                return MathUtils.lerp(MathUtils.PI/2f * 1.00001f, MathUtils.PI, (angle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
            return MathUtils.lerp(-MathUtils.PI/2f, -MathUtils.PI, (-angle - (MathUtils.PI - archAngle/2f)) / (archAngle/2f));
        }
    }

    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------
    //------------------------------ utility methods ------------------------------

    /*private void calculateRadiusInnerRadius(RoundedArch roundedArch) {
        Viewport viewport = getStage().getViewport();
        roundedArch.setRadius(toWorldCoordinates(Gdx.graphics.getPpiX() * archRadius, Dimension.X, viewport));
    }*/

    private void angleCorrection() {
        if (getControllerPosition() == Direction.RIGHT)
            angle = MathUtils.clamp(angle, -archAngle/2f, archAngle/2f);
        else {
            if (angle > 0)
                angle = MathUtils.clamp(angle, MathUtils.PI-archAngle/2f, MathUtils.PI);
            else angle = MathUtils.clamp(angle, -MathUtils.PI, -(MathUtils.PI-archAngle/2f));
        }

        Gdx.app.log(TAG, "angle = " + angle*MathUtils.radDeg
                + ", getAngle() = " + getAngle()*MathUtils.radDeg
                + ", progress = " + (angle - (MathUtils.PI - archAngle/2f)) / (archAngle / 2f));
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
