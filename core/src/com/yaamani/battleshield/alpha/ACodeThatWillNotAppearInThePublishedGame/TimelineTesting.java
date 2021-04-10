package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Timeline;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import java.util.Arrays;

import javax.swing.GroupLayout;

public class TimelineTesting extends Group {

    private static final String TAG = TimelineTesting.class.getSimpleName();

    public static final float Y_START = 5;
    public static final float Y_FINAL = Y_START + 20;
    public static final Interpolation INTERPOLATION = Interpolation.bounceOut;


    private SimpleText a;
    private SimpleText b;
    private SimpleText c;
    private SimpleText d;

    private Tween t1;
    private Tween t2;
    private Tween t3;
    private Tween t4;

    private boolean rewinding;

    private Timeline timeline;

    public TimelineTesting(MyBitmapFont myBitmapFont) {
        a = new SimpleText(myBitmapFont, "A");
        b = new SimpleText(myBitmapFont, "B");
        c = new SimpleText(myBitmapFont, "C");
        d = new SimpleText(myBitmapFont, "D");

        a.setHeight(15);
        b.setHeight(15);
        c.setHeight(15);
        d.setHeight(15);

        a.setX(0);
        b.setX(20);
        c.setX(40);
        d.setX(60);

        a.setY(Y_START);
        b.setY(Y_START);
        c.setY(Y_START);
        d.setY(Y_START);

        addActor(a);
        addActor(b);
        addActor(c);
        addActor(d);

        t1 = new Tween(1400, INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                a.setY(interpolation.apply(Y_START, Y_FINAL, percentage));
            }

            @Override
            public String toString() {
                return "A";
            }
        };

        t2 = new Tween(1400, INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                b.setY(interpolation.apply(Y_START, Y_FINAL, percentage));
            }

            @Override
            public String toString() {
                return "B";
            }
        };

        t3 = new Tween(1400, INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                c.setY(interpolation.apply(Y_START, Y_FINAL, percentage));
            }

            @Override
            public String toString() {
                return "C";
            }
        };

        t4 = new Tween(1400, INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                d.setY(interpolation.apply(Y_START, Y_FINAL, percentage));
            }

            @Override
            public String toString() {
                return "D";
            }
        };

        timeline = new Timeline(4);
        timeline.addTimer(t1, 0);
        timeline.addTimer(t2, 100);
        timeline.addTimer(t3, 100);
        timeline.addTimer(t4, 300);
        /*Gdx.app.log(TAG, timeline.getTimersAscendingStartTime().toString());
        Gdx.app.log(TAG, timeline.getStartTimeOfEachTimer().toString());*/
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (rewinding)
            delta *= -1;

        t1.update(delta);
        t2.update(delta);
        t3.update(delta);
        t4.update(delta);

        timeline.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.S))
            timeline.start();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.P))
            timeline.pause();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.R))
            timeline.resume();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.F))
            timeline.finish();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.COMMA))
            rewinding = !rewinding;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD))
            timeline.setPercentage(0.6f);

    }
}
