package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;

public class MyTweenTesting extends Group {
    private static final String TAG = MyTweenTesting.class.getSimpleName();

    private Viewport viewport;
    private Actor testObject;
    private MyTween tween;

    public MyTweenTesting(AdvancedStage parent) {
        viewport = parent.getViewport();
        setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        parent.addActor(this);

        initializeTestObject();
        initializeTween();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tween.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) tween.start();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.F)) tween.finish();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) tween.pause();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) tween.resume();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.G)) tween.pauseGradually(1000);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.H)) tween.resumeGradually(1000);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) tween.setPercentage(0.6f);

        //Gdx.app.log(TAG, "getPercentage() = " + tween.getPercentage());
    }

    private void initializeTestObject() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.drawPixel(0, 0, Color.WHITE.toIntBits());
        testObject = new Image(new Texture(pixmap));
        float l = viewport.getWorldWidth()/30f;
        testObject.setBounds(0, viewport.getWorldHeight()/1.3f - l/2f, l, l);
        addActor(testObject);
    }

    private void initializeTween() {
        float initialVal = 0;
        float finalVal = viewport.getWorldWidth() - testObject.getWidth();
        tween = new MyTween(3000, MyInterpolation.myLinear, initialVal, finalVal) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                testObject.setX(myInterpolation.apply(startX, endX, startY, endY, currentX));
            }
        };
    }
}