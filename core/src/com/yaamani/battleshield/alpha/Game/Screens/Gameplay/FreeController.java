package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.*;

public class FreeController extends Controller {

    private Viewport viewport;

    private float bgSize; //Inches

    private Image BG;

    public FreeController(GameplayScreen gameplayScreen, Image BG, Image stick, float bgSize, Constants.Direction controllerPosition) {
        super(gameplayScreen, stick, controllerPosition);

        this.BG = BG;
        addActorBefore(stick, this.BG);

        this.bgSize = bgSize;

        this.viewport = getStage().getViewport();

    }

    @Override
    protected void calculateNewSizeInWorldUnits() {
        float bgDiameterInWorldUnits;

        bgDiameterInWorldUnits = toWorldCoordinates(Gdx.graphics.getPpiX() * bgSize, MyMath.Dimension.X, viewport);

        BG.setSize(bgDiameterInWorldUnits, bgDiameterInWorldUnits);
        stick.setSize(bgDiameterInWorldUnits* CONTROLLER_FREE_STICK_RATIO, bgDiameterInWorldUnits* CONTROLLER_FREE_STICK_RATIO);

    }

    @Override
    protected void calculateNewPositionsInWorldUnits(float marginInWorldUnits) {
        BG.setY(marginInWorldUnits);
        if (getControllerPosition() == Constants.Direction.LEFT) BG.setX(BG.getY());
        else BG.setX(getWidth() - BG.getY() - BG.getWidth());

        centerTheStick();
    }

    @Override
    protected void moveTheStickAccordingToTheAngle() {

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

    @Override
    protected void gamePadPooling(float rightStickFirstAxis, float rightStickSecondAxis, float leftStickFirstAxis, float leftStickSecondAxis) {

        if (gamepadUsingRightOrLeftAxis(rightStickFirstAxis, rightStickSecondAxis, leftStickFirstAxis, leftStickSecondAxis)) moveTheStickAccordingToTheAngle();
        else centerTheStick();

    }


    @Override
    protected void touchDragged(InputEvent event, float x, float y, int pointer) {
        float bgRadius = BG.getWidth()/2f;
        float bgCentrePointX = BG.getX() + bgRadius;
        float bgCentrePointY = BG.getY() + bgRadius;
        float xAccordingToTheCenterToTheBG = x - bgCentrePointX;
        float yAccordingToTheCenterToTheBG = y - bgCentrePointY;

        angle = MathUtils.atan2(yAccordingToTheCenterToTheBG, xAccordingToTheCenterToTheBG);
        //Gdx.app.log(TAG, "" + (angle * MathUtils.radiansToDegrees));

    }

    @Override
    protected void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        centerTheStick();
    }
}
