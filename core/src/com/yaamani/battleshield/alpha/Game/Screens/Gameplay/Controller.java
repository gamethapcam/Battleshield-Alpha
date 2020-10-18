package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;

public abstract class Controller extends Group implements Resizable {

    public final String TAG = Controller.class.getSimpleName();

    protected Image stick;

    private Direction controllerPosition;

    private float marginInWorldUnits;

    protected Float stickAngle;
    protected Float outputAngle;

    private boolean usingTouch;

    private Group outputAngleIndicatorContainer;

    private NetworkAndStorageManager networkAndStorageManager;

    private GameplayScreen gameplayScreen;

    public Controller(GameplayScreen gameplayScreen, Image stick, Direction controllerPosition, NetworkAndStorageManager networkAndStorageManager) {
        setTransform(false);
        gameplayScreen.addActor(this);

        this.stick = stick;
        addActor(this.stick);

        this.controllerPosition = controllerPosition;

        addListener(new MyTouchListener());

        usingTouch = false;

        initializeOutputAngleIndicatorContainer();

        this.networkAndStorageManager = networkAndStorageManager;

        this.gameplayScreen = gameplayScreen;

        //gameplayScreen.addListener(this);

        //setDebug(true);

        /*for (com.badlogic.gdx.controllers.Controller controller : Controllers.getControllers()) {
            Gdx.app.log(TAG, controller.getName());
        }*/
    }

    @Override
    public void act(float delta) {

        networkReceivingStuff();

        super.act(delta);

        if (outputAngle != null) {
            outputAngleIndicatorContainer.setVisible(true);
            outputAngleIndicatorContainer.setRotation(getOutputAngle() * MathUtils.radDeg);
        } else
            outputAngleIndicatorContainer.setVisible(false);

        //if (!usingTouch) gamePadPooling();

        networkTransmissionAndStorageStuff();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        settingBounds(worldWidth, worldHeight);
        calculateMarginInWorldUnits();
        calculateNewSizeInWorldUnits();
        calculateNewPositionsInWorldUnits(marginInWorldUnits);

        if (controllerPosition == Direction.RIGHT) {
            float shiftFromCentre = worldWidth/2f - getX();
            outputAngleIndicatorContainer.setX(shiftFromCentre);
        } else {
            float shiftFromCentre = worldWidth/2f - getWidth();
            outputAngleIndicatorContainer.setX(getWidth() + shiftFromCentre);
        }
    }

    private void networkReceivingStuff() {
        if (networkAndStorageManager.isConnectionEstablished() | networkAndStorageManager.isLoadControllerValuesModeEnabled()) {

            boolean newStickAngleComing = false;
            switch (controllerPosition) {


                case LEFT:
                    if (networkAndStorageManager.isLeftStickAngleReadyToBeConsumed()) {
                        stickAngle = networkAndStorageManager.consumeLeftStickAngle();
                        newStickAngleComing = true;
                    }
                    break;
                case RIGHT:
                    if (networkAndStorageManager.isRightStickAngleReadyToBeConsumed()) {
                        stickAngle = networkAndStorageManager.consumeRightStickAngle();
                        newStickAngleComing = true;
                    }
                    break;
            }

            if (newStickAngleComing) {
                moveTheStickAccordingToTheStickAngle();
                if (this instanceof RestrictedController)
                    ((RestrictedController) this).calculateOutputAngleFromStickAngle();
            }
        }
    }

    private void networkTransmissionAndStorageStuff() {
        if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
            if (networkAndStorageManager.isConnectionEstablished() | networkAndStorageManager.isSaveControllerValuesModeEnabled())
                networkAndStorageManager.prepareStickAngleForTransmissionAndStorageIfIamMobile(stickAngle, controllerPosition);
    }

    private void settingBounds(float worldWidth, float worldHeight) {
        if (controllerPosition == Direction.LEFT) setBounds(0, 0, worldWidth/2f - TURRET_RADIUS, worldHeight);
        else setBounds(worldWidth/2f + TURRET_RADIUS, 0, worldWidth/2f - TURRET_RADIUS, worldHeight);
    }

    private void calculateMarginInWorldUnits() {
        marginInWorldUnits = toWorldCoordinates(Gdx.graphics.getPpiX() * CONTROLLER_MARGIN, MyMath.Dimension.X, getStage().getViewport());
    }

    protected abstract void calculateNewSizeInWorldUnits();

    protected abstract void calculateNewPositionsInWorldUnits(float marginInWorldUnits);

    protected abstract void moveTheStickAccordingToTheStickAngle();


    protected abstract void touchDragged(InputEvent event, float x, float y, int pointer);

    protected abstract void touchUp(InputEvent event, float x, float y, int pointer, int button);

    protected void gamePadPooling() {
        if (Controllers.getControllers().size == 0) return;

        com.badlogic.gdx.controllers.Controller gamePad = Controllers.getControllers().peek();

        float rightStickFirstAxis  = MyMath.roundTo(gamePad.getAxis(0), 2);
        float rightStickSecondAxis = MyMath.roundTo(gamePad.getAxis(1), 2) * -1;
        float leftStickFirstAxis   = MyMath.roundTo(gamePad.getAxis(2), 2);
        float leftStickSecondAxis  = MyMath.roundTo(gamePad.getAxis(3), 2) * -1;

        Gdx.app.log(TAG, "rightStick = (" + rightStickFirstAxis + ", " + rightStickSecondAxis + ")" +
                "leftStick = (" + leftStickFirstAxis + ", " + leftStickSecondAxis + ")");

        if (getControllerPosition() == Constants.Direction.RIGHT) {
            outputAngle = MathUtils.atan2(rightStickSecondAxis, rightStickFirstAxis);
        } else
            outputAngle = MathUtils.atan2(leftStickSecondAxis, leftStickFirstAxis);

        gamePadPooling(rightStickFirstAxis, rightStickSecondAxis, leftStickFirstAxis, leftStickSecondAxis);
    }

    protected abstract void gamePadPooling(float rightStickFirstAxis, float rightStickSecondAxis, float leftStickFirstAxis, float leftStickSecondAxis);

    protected boolean gamePadUsingRightOrLeftAxis(float rightStickFirstAxis, float rightStickSecondAxis, float leftStickFirstAxis, float leftStickSecondAxis) {
        if (usingTouch) return false;
        return ((rightStickFirstAxis != 0 | rightStickSecondAxis != 0) & getControllerPosition() == Constants.Direction.RIGHT |
                (leftStickFirstAxis != 0 | leftStickSecondAxis != 0) & getControllerPosition() == Constants.Direction.LEFT);
    }

    public Float getOutputAngle() {
        /*if (outputAngle >= 0)
            Gdx.app*/
        return outputAngle;
    }

    public Float getAngleDeg() {
        if (getOutputAngle() == null) return null;
        //Gdx.app.log(TAG, "" + getOutputAngle()*MathUtils.radiansToDegrees);
        return getOutputAngle()*MathUtils.radiansToDegrees;
    }

    /*public Float getAngleDegNoNegative() {
        if (angle == null) return null;
        if (angle < 0) return 360f + getAngleDeg();
        return getAngleDeg();
    }*/

    public Direction getControllerPosition() {
        return controllerPosition;
    }

    protected boolean isUsingTouch() {
        return usingTouch;
    }

    /*public void setControllerSize(float controllerSize) {
        this.controllerSize = controllerSize;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
    }*/




    private void initializeOutputAngleIndicatorContainer() {
        outputAngleIndicatorContainer = new Group();
        Image outputAngleIndicator = new Image(Assets.instance.mutualAssets.bigCircle);
        outputAngleIndicator.setSize(CONTROLLER_OUTPUT_ANGLE_INDICATOR_SIZE, CONTROLLER_OUTPUT_ANGLE_INDICATOR_SIZE);
        //outputAngleIndicator.setColor(Color.SCARLET);
        outputAngleIndicatorContainer.addActor(outputAngleIndicator);
        outputAngleIndicator.setX((HEALTH_BAR_RADIUS+SHIELDS_INNER_RADIUS+SHIELDS_ON_DISPLACEMENT)/2f /*- outputAngleIndicator.getWidth()/2f*/);
        outputAngleIndicator.setY(-outputAngleIndicator.getHeight()/2f);
        addActor(outputAngleIndicatorContainer);
        outputAngleIndicatorContainer.setY(WORLD_SIZE/2f);
    }










    private class MyTouchListener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            usingTouch = true;

            touchDragged(event, x, y, pointer);

            if (pointer <= 1) return true;
            else return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            Controller.this.touchDragged(event, x, y, pointer);
            moveTheStickAccordingToTheStickAngle();
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            usingTouch = false;
            Controller.this.touchUp(event, x, y, pointer, button);
            //moveTheStickAccordingToTheAngle();
        }
    }
}
