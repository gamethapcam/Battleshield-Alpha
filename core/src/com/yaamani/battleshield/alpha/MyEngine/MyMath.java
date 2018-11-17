package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class MyMath {

    public static final double millisToNano = 1000f;

    //The next 2 methods for calculating the aspect ratio. from (https://codereview.stackexchange.com/a/26698)
    private static int gcd(int p, int q) {
        if (q == 0) return p;
        else return gcd(q, p % q);
    }

    public static String ratio(int a, int b) {
        final int gcd = gcd(a,b);
        return a/gcd + ":" + b/gcd;
    }

    public static long millisSince(long startTimeNano) {
        return TimeUtils.nanosToMillis(TimeUtils.nanoTime() - startTimeNano);
    }

    //https://martin.ankerl.com/2007/02/11/optimized-exponential-functions-for-java/
    public static double exp(double val) {
        final long tmp = (long) (1512775 * val + 1072632447);
        return Double.longBitsToDouble(tmp << 32);
    }

    //https://badlogicgames.com/forum/viewtopic.php?f=11&t=16014&p=69050&hilit=sin+mathutils#p69050
    private static final float PI = MathUtils.PI;
    private static final float MINUS_PI = -PI;
    private static final float DOUBLE_PI = PI * 2f;
    private static final float PI_2 = MathUtils.PI / 2f;
    private static final float MINUS_PI_2 = -PI_2;
    private static final float CONST_1 = 4f / PI;
    private static final float CONST_2 = 4f / (PI * PI);

    public static float sin(float x) {
        if (x < MINUS_PI)
            x += DOUBLE_PI;
        else if (x > PI)
            x -= DOUBLE_PI;

        return (x < 0f) ? (CONST_1 * x + CONST_2 * x * x) : (CONST_1 * x - CONST_2 * x * x);
    }

    public static float cos(float x) {
        if (x < MINUS_PI)
            x += DOUBLE_PI;
        else if (x > PI)
            x -= DOUBLE_PI;

        x += PI_2;

        if (x > PI) x -= DOUBLE_PI;

        return (x < 0f) ? (CONST_1 * x + CONST_2 * x * x) : (CONST_1 * x - CONST_2 * x * x);
    }

    public enum Dimension {X, Y}
    public static float toWorldCoordinates(float screenCoordinates, Dimension dimension, Viewport viewport) {
        float worldCoordinates = 0;
        switch (dimension) {
            case X:
                worldCoordinates = (screenCoordinates / viewport.getScreenWidth()) * viewport.getWorldWidth();
                break;
            case Y:
                worldCoordinates = (screenCoordinates / viewport.getScreenHeight()) * viewport.getWorldHeight();
                break;
        }
        return worldCoordinates;
    }

    public static <T> T chooseFromProbabilityArray(T[] arr, int rangeStartIndex, int rangeEndIndex) {
        if (rangeStartIndex < 0) throw new ArrayIndexOutOfBoundsException("rangeStartIndex can't be less than zero");
        if (rangeEndIndex > arr.length-1) throw new ArrayIndexOutOfBoundsException("rangeEndIndex can't be greater than the array's length - 1");

        return arr[MathUtils.random(rangeStartIndex, rangeEndIndex)];
    }

    public static <T> T chooseFromProbabilityArray(T[] arr) {
        return chooseFromProbabilityArray(arr, 0, arr.length-1);
    }

    public static float roundTo(float val, int numOfDigitsAfterTheDecimalPoint) {
        float multiplier = (float) Math.pow(10, numOfDigitsAfterTheDecimalPoint);
        return MathUtils.round(val*multiplier) / multiplier;
    }
}