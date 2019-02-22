package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;

public class Controller extends Group implements Resizable {

    public final String TAG = Controller.class.getSimpleName();

    private Viewport viewport;

    private Image BG;
    private Image stick;

    private ControllerSize controllerSize;
    private Direction controllerPosition;

    private Float angle;

    public Controller(GameplayScreen gameplayScreen, Image BG, Image stick, ControllerSize controllerSize, Direction controllerPosition) {
        setTransform(false);
        gameplayScreen.addActor(this);

        this.BG = BG;
        this.stick = stick;
        addActor(this.BG);
        addActor(this.stick);

        this.controllerSize = controllerSize;
        this.controllerPosition = controllerPosition;

        this.viewport = getStage().getViewport();

        addListener(new MyTouchListener());
        //gameplayScreen.addListener(this);

        //setDebug(true);

        /*for (com.badlogic.gdx.controllers.Controller controller : Controllers.getControllers()) {
            Gdx.app.log(TAG, controller.getName());
        }*/
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        gamePadPooling();

    }

    private void gamePadPooling() {
        if (Controllers.getControllers().size == 0) return;

        com.badlogic.gdx.controllers.Controller gamePad = Controllers.getControllers().peek();

        float rightStickFirstAxis  = MyMath.roundTo(gamePad.getAxis(0), 2);
        float rightStickSecondAxis = MyMath.roundTo(gamePad.getAxis(1), 2) * -1;
        float leftStickFirstAxis   = MyMath.roundTo(gamePad.getAxis(2), 2);
        float leftStickSecondAxis  = MyMath.roundTo(gamePad.getAxis(3), 2) * -1;

        Gdx.app.log(TAG, "rightStick = (" + rightStickFirstAxis + ", " + rightStickSecondAxis + ")" +
                "leftStick = (" + leftStickFirstAxis + ", " + leftStickSecondAxis + ")");

        if (controllerPosition == Direction.RIGHT) {
            angle = MathUtils.atan2(rightStickSecondAxis, rightStickFirstAxis);
        } else
            angle = MathUtils.atan2(leftStickSecondAxis, leftStickFirstAxis);

        if ((rightStickFirstAxis != 0 | rightStickSecondAxis != 0) & controllerPosition == Direction.RIGHT |
                (leftStickFirstAxis != 0 | leftStickSecondAxis != 0) & controllerPosition == Direction.LEFT)
            moveTheStickAccordingToTheAngle();
        else centerTheStick();
    }

    public Controller(GameplayScreen gameplayScreen, Image BG, Image stick, Direction controllerPosition) {
        this(gameplayScreen, BG, stick, CONTROLLER_DEFAULT_SIZE, controllerPosition);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        settingBounds(worldWidth, worldHeight);
        calculateNewSizeInWorldUnits(controllerSize);
    }

    private void settingBounds(float worldWidth, float worldHeight) {
        if (controllerPosition == Direction.LEFT) setBounds(0, 0, worldWidth/2f, worldHeight);
        else setBounds(worldWidth/2f, 0, worldWidth, worldHeight);

    }

    private void calculateNewSizeInWorldUnits(ControllerSize controllerSize) {
        float bgDiameterInWorldUnits;
        float marginInWorldUnits = toWorldCoordinates(Gdx.graphics.getPpiX() * CONTROLLER_MARGIN, Dimension.X, viewport);

        if (controllerSize == ControllerSize.LARGE)
            bgDiameterInWorldUnits = toWorldCoordinates(Gdx.graphics.getPpiX() * CONTROLLER_LARGE_SIZE, Dimension.X, viewport);
        else
            bgDiameterInWorldUnits = toWorldCoordinates(Gdx.graphics.getPpiX() * CONTROLLER_SMALL_SIZE, Dimension.X, viewport);

        BG.setSize(bgDiameterInWorldUnits, bgDiameterInWorldUnits);
        stick.setSize(bgDiameterInWorldUnits*CONTROLLER_STICK_RATIO, bgDiameterInWorldUnits*CONTROLLER_STICK_RATIO);

        calculateNewPositionsInWorldUnits(marginInWorldUnits);
    }

    private void calculateNewPositionsInWorldUnits(float marginInWorldUnits) {
        BG.setY(marginInWorldUnits);
        if (controllerPosition == Direction.LEFT) BG.setX(BG.getY());
        else BG.setX(getWidth()/2f - BG.getY() - BG.getWidth());

        centerTheStick();
    }

    private void moveTheStickAccordingToTheAngle() {

        float bgRadius = BG.getWidth()/2f;
        float bgCentrePointX = BG.getX() + bgRadius;
        float bgCentrePointY = BG.getY() + bgRadius;

        float stickRadius = stick.getWidth()/2f;
        stick.setPosition(bgRadius*MyMath.cos(angle) - stickRadius + bgCentrePointX,
                bgRadius*MyMath.sin(angle) - stickRadius + bgCentrePointY);
    }

    private void centerTheStick() {
        float bgRadius = BG.getWidth()/2f;
        float stickRadius = stick.getWidth()/2f;
        stick.setPosition(BG.getX() + bgRadius - stickRadius, BG.getY() + bgRadius - stickRadius);
        angle = null;
    }

    public Float getAngle() {
        return angle;
    }

    public Float getAngleDeg() {
        if (angle == null) return null;
        return angle*MathUtils.radiansToDegrees;
    }

    /*public Float getAngleDegNoNegative() {
        if (angle == null) return null;
        if (angle < 0) return 360f + getAngleDeg();
        return getAngleDeg();
    }*/

    public ControllerSize getControllerSize() {
        return controllerSize;
    }

    public void setControllerSize(ControllerSize controllerSize) {
        this.controllerSize = controllerSize;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
    }








    private class MyTouchListener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (pointer <= 1) return true;
            else return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
                float bgRadius = BG.getWidth()/2f;
                float bgCentrePointX = BG.getX() + bgRadius;
                float bgCentrePointY = BG.getY() + bgRadius;
                float xAccordingToTheCenterToTheBG = x - bgCentrePointX;
                float yAccordingToTheCenterToTheBG = y - bgCentrePointY;

                angle = MathUtils.atan2(yAccordingToTheCenterToTheBG, xAccordingToTheCenterToTheBG);
                //Gdx.app.log(TAG, "" + (angle * MathUtils.radiansToDegrees));

                moveTheStickAccordingToTheAngle();

        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            centerTheStick();
        }
    }











}
