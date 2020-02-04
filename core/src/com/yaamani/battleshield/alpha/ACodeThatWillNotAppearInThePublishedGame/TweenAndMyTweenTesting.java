package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

public class TweenAndMyTweenTesting extends Group implements Resizable {
    private static final String TAG = TweenAndMyTweenTesting.class.getSimpleName();

    private Viewport viewport;
    private Actor testObject;

    private Tween tween;
    private MyTween myTween;

    private float leftPos;
    private float rightPos;

    public TweenAndMyTweenTesting(AdvancedStage parent) {
        viewport = parent.getViewport();
        setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        parent.addActor(this);

        initializeTestObject();
        initializeMyTween();
        initializeTween();

        leftPos = 0;
        rightPos = viewport.getWorldWidth() - testObject.getWidth();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        tween.update(delta);
        myTween.update(delta);

        /*if (myTween.isStarted()) {
            Gdx.app.log(TAG, "testObject.getX() = " + testObject.getX()
                    + "\t\t percentage = " + myTween.getPercentage()
                    + "\t\t currentX = " + myTween.getDurationMillis()*myTween.getPercentage());
        }*/

        tweenControls();
        //myTweenControls();

        //Gdx.app.log(TAG, "getPercentage() = " + myTween.getPercentage());
    }

    private void myTweenControls() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) myTween.start();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.F)) myTween.finish();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) myTween.pauseFor(500);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) myTween.resume();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.G)) myTween.pauseGradually(3000);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            //Gdx.app.log(TAG, "testObject.getX() = " + testObject.getX() + ",\t currentX = " + myTween.getDurationMillis()*myTween.getPercentage());
            myTween.resumeGradually(600);
        }
        else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) myTween.setPercentage(0.6f);
    }

    private void tweenControls() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) tween.start();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.F)) tween.finish();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) tween.pause();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) tween.resume();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) tween.setReversed(true);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) tween.setReversed(false);
        else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            tween.setDurationMillis(7000);
        }
        Gdx.app.log(TAG, "" + tween.isFinished());
    }

    private void initializeTestObject() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.drawPixel(0, 0, Color.MAGENTA.toIntBits());
        testObject = new Image(new Texture(pixmap));
        float l = viewport.getWorldWidth()/30f;
        testObject.setBounds(0, viewport.getWorldHeight()/1.3f - l/2f, l, l);
        addActor(testObject);
    }

    private void initializeTween() {
        tween = new Tween(1700, Interpolation.linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                testObject.setX(interpolation.apply(leftPos, rightPos, percentage));
            }
        };
    }

    private void initializeMyTween() {
        myTween = new MyTween(700, MyInterpolation.myLinear, rightPos, leftPos) {

            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                testObject.setX(myInterpolation.apply(startX, endX, startY, endY, currentX));
            }
        };
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        rightPos = worldWidth - testObject.getWidth();
    }
}