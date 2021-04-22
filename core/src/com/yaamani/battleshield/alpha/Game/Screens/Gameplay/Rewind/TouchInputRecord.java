package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Controller;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;


public class TouchInputRecord extends RewindEngine.RewindEvent {

    public Constants.Direction controllerPosition;

    public boolean touchDown;

    public InputEvent event;
    public float x, y;
    public int pointer;
    public int button;


    public TouchInputRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        Controller controller;
        if (controllerPosition == Constants.Direction.LEFT)
            controller = gameplayScreen.getControllerLeft();
        else
            controller = gameplayScreen.getControllerRight();


        if (touchDown)
            controller.getMyTouchListener().up(event, x, y, pointer, button);
        else
            controller.getMyTouchListener().dragged(event, x, y, pointer);

    }


}
