package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.STARS_MAX_RADIUS;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.WORLD_SIZE;

public class DifficultyCurveTesting {

    public static final String TAG = DifficultyCurveTesting.class.getSimpleName();

    private MyInterpolation.LinearCurvesLinearTimeExponentialOutOutput _dummy;

    private float startX;
    private float startY;
    private float endX;
    private float endY;

    public DifficultyCurveTesting() {
        _dummy = new MyInterpolation.LinearCurvesLinearTimeExponentialOutOutput(6, 0.254f, 3f);


        startX = WORLD_SIZE/4f;
        startY = WORLD_SIZE/4f;
        endX = startX + WORLD_SIZE/2f;
        endY = startY + WORLD_SIZE/2f;


        float a = 0.9f;
        Gdx.app.log(TAG, "a = " + a);
        Gdx.app.log(TAG, "apply(a) = " + _dummy.apply(a));
        Gdx.app.log(TAG, "getInterval(a) = " + _dummy.getInterval(a));
        Gdx.app.log(TAG, "getIntervalStartTime(a)                                   = " + _dummy.getIntervalStartTime(a));
        Gdx.app.log(TAG, "getIntervalStartTime(overallStartTime, overallEndTime, a) = " + _dummy.getIntervalStartTime(startX, endX, a));
        Gdx.app.log(TAG, "getIntervalEndTime(a)                                     = " + _dummy.getIntervalEndTime(a));
        Gdx.app.log(TAG, "getIntervalEndTime(overallStartTime, overallEndTime, a)   = " + _dummy.getIntervalEndTime(startX, endX, a));

    }

    public void draw(Batch batch) {
        batch.setColor(Color.MAGENTA);

        int num = 3500;
        //Gdx.app.log(TAG, "-----------");
        for (int i = 0; i <= num; i++) {
            batch.draw(Assets.instance.mutualAssets.star,
                    (startX) + i*((endX-startX)/num),
                    _dummy.apply((startY), endY, (float)i/num),
                    STARS_MAX_RADIUS,
                    STARS_MAX_RADIUS);
        }
        //Gdx.app.log(TAG, "-----------");

        batch.setColor(Color.LIME);

        batch.draw(Assets.instance.mutualAssets.star,
                (startX),
                (startY),
                STARS_MAX_RADIUS*1,
                STARS_MAX_RADIUS*1);

        batch.draw(Assets.instance.mutualAssets.star,
                (endX),
                (endY),
                STARS_MAX_RADIUS*1,
                STARS_MAX_RADIUS*1);
    }
}
