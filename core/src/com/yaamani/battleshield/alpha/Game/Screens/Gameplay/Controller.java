package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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

    public Controller(GameplayScreen gameplayScreen, Image stick, Direction controllerPosition) {
        setTransform(false);
        gameplayScreen.addActor(this);

        this.stick = stick;
        addActor(this.stick);

        this.controllerPosition = controllerPosition;

        addListener(new MyTouchListener());

        usingTouch = false;

        //gameplayScreen.addListener(this);

        //setDebug(true);

        /*for (com.badlogic.gdx.controllers.Controller controller : Controllers.getControllers()) {
            Gdx.app.log(TAG, controller.getName());
        }*/
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!usingTouch) gamePadPooling();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        settingBounds(worldWidth, worldHeight);
        calculateMarginInWorldUnits();
        calculateNewSizeInWorldUnits();
        calculateNewPositionsInWorldUnits(marginInWorldUnits);
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
    }
*/





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
