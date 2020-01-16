package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;

public final class MyMath {

    public static final String TAG = MyMath.class.getSimpleName();

    public static final double secondsToMillis = 1000f;

    //The next 2 methods for calculating the aspect ratio. from (https://codereview.stackexchange.com/a/26698)
    private static int gcd(int p, int q) {
        if (q == 0) return p;
        else return gcd(q, p % q);
    }

    public static String ratio(int a, int b) {
        final int gcd = gcd(a,b);
        return a/gcd + ":" + b/gcd;
    }

    public static double millisSince(long startTimeNano) {
        return (TimeUtils.nanoTime() - startTimeNano) * (1/1000000d);
    }

    /**
     * Use this to print the array using System.out.println() or Gdx.app.log().
     * @param arr
     * @return
     */
    public static String arrayToString(int[] arr) {
        StringBuilder string = new StringBuilder();
        string.append('{');
        for(int i = 0, len = arr.length; i < len; i++) {
            string.append(arr[i]);
            if (i <len-1) string.append(", ");
        }
        string.append('}');
        return string.toString();
    }

    /**
     * Use this to print the array using System.out.println() or Gdx.app.log().
     * @param arr
     * @return
     */
    public static String arrayToString(float[] arr) {
        StringBuilder string = new StringBuilder();
        string.append('{');
        for(int i = 0, len = arr.length; i < len; i++) {
            string.append(arr[i]);
            if (i <len-1) string.append(", ");
        }
        string.append('}');
        return string.toString();
    }

    /**
     * Use this to print the array using System.out.println() or Gdx.app.log().
     * @param arr
     * @return
     */
    public static String arrayToString(long[] arr) {
        StringBuilder string = new StringBuilder();
        string.append('{');
        for(int i = 0, len = arr.length; i < len; i++) {
            string.append(arr[i]);
            if (i <len-1) string.append(", ");
        }
        string.append('}');
        return string.toString();
    }

    /**
     * Use this to print the array using System.out.println() or Gdx.app.log().
     * @param arr
     * @return
     */
    public static String arrayToString(double[] arr) {
        StringBuilder string = new StringBuilder();
        string.append('{');
        for(int i = 0, len = arr.length; i < len; i++) {
            string.append(arr[i]);
            if (i <len-1) string.append(", ");
        }
        string.append('}');
        return string.toString();
    }

    /**
     * Use this to print the array using System.out.println() or Gdx.app.log().
     * @param arr
     * @return
     */
    public static <T> String arrayToString(T[] arr) {
        StringBuilder string = new StringBuilder();
        string.append('{');
        for(int i = 0, len = arr.length; i < len; i++) {
            string.append(arr[i]);
            if (i <len-1) string.append(", ");
        }
        string.append('}');
        return string.toString();
    }

    public static double averageOfArray (int[] arr) {
        long sum = 0;
        for (int i = 0, len = arr.length; i < len; i++) {
            sum += arr[i];
        }
        return (double) sum / arr.length;
    }

    public static double averageOfArray (long[] arr) {
        long sum = 0;
        for (int i = 0, len = arr.length; i < len; i++) {
            sum += arr[i];
        }
        return (double) sum / arr.length;
    }

    public static double averageOfArray (float[] arr) {
        double sum = 0;
        for (int i = 0, len = arr.length; i < len; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    public static double averageOfArray (double[] arr) {
        double sum = 0;
        for (int i = 0, len = arr.length; i < len; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    public static double averageOfArray(Number[] arr) {
        double sum = 0;
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            if (arr[i] == null) {
                i--;
                len--;
            } else
                sum += arr[i].doubleValue();
        }
        return sum / len;
    }


    public static double medianOfArray (int[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        if (arr.length % 2 == 0)
            return (arr[arr.length / 2] + arr[arr.length / 2 - 1]) / 2.0;
        else
            return arr[arr.length / 2];
    }

    public static double medianOfArray (long[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        if (arr.length % 2 == 0)
            return (arr[arr.length / 2] + arr[arr.length / 2 - 1]) / 2.0;
        else
            return arr[arr.length / 2];
    }

    public static double medianOfArray (float[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        if (arr.length % 2 == 0)
            return (arr[arr.length / 2] + arr[arr.length / 2 - 1]) / 2.0;
        else
            return arr[arr.length / 2];
    }

    public static double medianOfArray (double[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        if (arr.length % 2 == 0)
            return (arr[arr.length / 2] + arr[arr.length / 2 - 1]) / 2.0;
        else
            return arr[arr.length / 2];
    }

    public static double medianOfArray (Number[] arr, boolean sorted) {
        int arrLengthExcludingNulls = arr.length;
        Number[] arrExcludingNulls = new Number[arr.length];
        try {
            if (!sorted) Arrays.sort(arr);
            else
                for (int i = 0, len = arr.length; i < len; i++) {
                    arr[i].doubleValue(); // Throws NullPointerException
                }
        } catch (NullPointerException e) {
            for (int i = 0, j = 0, len = arr.length; i < len; i++) {
                if (arr[i] == null) {
                    arrLengthExcludingNulls--;
                    continue;
                }
                arrExcludingNulls[j] = arr[i];
                j++;
            }
            arr = arrExcludingNulls;
            if (!sorted) Arrays.sort(arrExcludingNulls, 0, arrLengthExcludingNulls-1);
        }

        return medianOfArray(arr, arrLengthExcludingNulls);
    }

    private static double medianOfArray(Number[] arr, int arrLengthExcludingNulls) {
        if (arrLengthExcludingNulls % 2 == 0)
            return (arr[arrLengthExcludingNulls / 2].doubleValue() + arr[arrLengthExcludingNulls / 2 - 1].doubleValue()) / 2.0;
        else
            return arr[arrLengthExcludingNulls / 2].doubleValue();
    }

    public static int maxOfArray(int[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[arr.length - 1];
    }

    public static int minOfArray(int[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[0];
    }

    public static long maxOfArray(long[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[arr.length - 1];
    }

    public static long minOfArray(long[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[0];
    }

    public static float maxOfArray(float[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[arr.length - 1];
    }

    public static float minOfArray(float[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[0];
    }

    public static double maxOfArray(double[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[arr.length - 1];
    }

    public static double minOfArray(double[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return arr[0];
    }

    public static Number maxOfArray(Number[] arr, boolean sorted) {
        int arrLengthExcludingNulls = arr.length;
        Number[] arrExcludingNulls = new Number[arr.length];
        try {
            if (!sorted) Arrays.sort(arr);
            else
                for (int i = 0, len = arr.length; i < len; i++) {
                    arr[i].doubleValue(); // Throws NullPointerException
                }
        } catch (NullPointerException e) {
            for (int i = 0, j = 0, len = arr.length; i < len; i++) {
                if (arr[i] == null) {
                    arrLengthExcludingNulls--;
                    continue;
                }
                arrExcludingNulls[j] = arr[i];
                j++;
            }
            arr = arrExcludingNulls;
            if (!sorted) Arrays.sort(arrExcludingNulls, 0, arrLengthExcludingNulls-1);
        }
        return maxOfArray(arr, arrLengthExcludingNulls);
    }

    public static Number minOfArray(Number[] arr, boolean sorted) {
        int arrLengthExcludingNulls = arr.length;
        Number[] arrExcludingNulls = new Number[arr.length];
        try {
            if (!sorted) Arrays.sort(arr);
            else
                for (int i = 0, len = arr.length; i < len; i++) {
                    arr[i].doubleValue(); // Throws NullPointerException
                }
        } catch (NullPointerException e) {
            for (int i = 0, j = 0, len = arr.length; i < len; i++) {
                if (arr[i] == null) {
                    arrLengthExcludingNulls--;
                    continue;
                }
                arrExcludingNulls[j] = arr[i];
                j++;
            }
            arr = arrExcludingNulls;
            if (!sorted) Arrays.sort(arrExcludingNulls, 0, arrLengthExcludingNulls-1);
        }
        return minOfArray(arr);
    }

    private static Number maxOfArray(Number[] arr, int arrLengthExcludingNulls) {
        return arr[arrLengthExcludingNulls - 1];
    }

    private static Number minOfArray(Number[] arr) {
        return arr[0];
    }

    public static String arrayStatistics(int[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return "Avg = " + MyMath.averageOfArray(arr) + ", Median = " + MyMath.medianOfArray(arr, true) + ", Max = " + MyMath.maxOfArray(arr, true) + ", Min = " + MyMath.minOfArray(arr, true);
    }

    public static String arrayStatistics(long[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return "Avg = " + MyMath.averageOfArray(arr) + ", Median = " + MyMath.medianOfArray(arr, true) + ", Max = " + MyMath.maxOfArray(arr, true) + ", Min = " + MyMath.minOfArray(arr, true);
    }

    public static String arrayStatistics(float[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return "Avg = " + MyMath.averageOfArray(arr) + ", Median = " + MyMath.medianOfArray(arr, true) + ", Max = " + MyMath.maxOfArray(arr, true) + ", Min = " + MyMath.minOfArray(arr, true);
    }

    public static String arrayStatistics(double[] arr, boolean sorted) {
        if (!sorted) Arrays.sort(arr);
        return "Avg = " + MyMath.averageOfArray(arr) + ", Median = " + MyMath.medianOfArray(arr, true) + ", Max = " + MyMath.maxOfArray(arr, true) + ", Min = " + MyMath.minOfArray(arr, true);
    }

    public static String arrayStatistics(Number[] arr, boolean sorted) {
        int arrLengthExcludingNulls = arr.length;
        Number[] arrExcludingNulls = new Number[arr.length];
        try {
            if (!sorted) Arrays.sort(arr);
            else
                for (int i = 0, len = arr.length; i < len; i++) {
                    arr[i].doubleValue(); // Throws NullPointerException
                }
        } catch (NullPointerException e) {
            for (int i = 0, j = 0, len = arr.length; i < len; i++) {
                if (arr[i] == null) {
                    arrLengthExcludingNulls--;
                    continue;
                }
                arrExcludingNulls[j] = arr[i];
                j++;
            }
            arr = arrExcludingNulls;
            if (!sorted) Arrays.sort(arrExcludingNulls, 0, arrLengthExcludingNulls-1);
        }

        return "Avg = " + MyMath.averageOfArray(arr) + ", Median = " + MyMath.medianOfArray(arr, arrLengthExcludingNulls) + ", Max = " + MyMath.maxOfArray(arr, arrLengthExcludingNulls) + ", Min = " + MyMath.minOfArray(arr);
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

    public static float deg_0_to_360(float deg) {
        if (deg < 0) return deg_0_to_360(deg + 360);
        if (deg > 360) return deg_0_to_360(deg - 360);
        return deg;
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

    public static <T> T chooseFromProbabilityArray(T[] arr, T... itemsToExclude) {
        T[] tempArr = arr.clone();
        int arrLen = arr.length, exLen = itemsToExclude.length;

        for (int i = 0; i < arrLen; i++) {
            for (int j = 0; j < exLen; j++) {
                if (tempArr[i] == itemsToExclude[j]) {
                    // delete arr[i]
                    tempArr[i] = arr[arrLen-1]; // replace it with the last element
                    arrLen--;
                    i--;
                    break;
                }
            }
        }

        return chooseFromProbabilityArray(tempArr, 0, arrLen-1);
    }

    public static float roundTo(float val, int numOfDigitsAfterTheDecimalPoint) {

        float multiplier = (float) Math.pow(10, numOfDigitsAfterTheDecimalPoint);
        return MathUtils.round(val*multiplier) / multiplier;
    }

    public interface BisectionFunction {
        float function(float x);
    }

    /**
     * <p>This implementation of good old bisection method always outputs a solution even when it should output failure.</p>
     * <p>This function will only work properly and as expected if you're trying to solve a function that has a unique solution within the given bounds.</p>
     * <p>So, don't use it purely to solve mathematical problems.
     * Use it only in your game after you came up with your perfect mathematical model that ensures a unique solution with in the bounds.</p>
     * <p>The reason it doesn't output failure is I don't want the failure of it to interrupt or crash the game at any given moment.</p>
     * <p>If the function you're trying to solve have 2 solutions with in the given bounds, it'll (most likely) output the higher in value.
     * If it has more than 2, it'll output one of them. Which one ?, you might ask, this only depends on the upper and lower bounds.</p>
     * <p>If both {@code function(lower bound)} and {@code function(upper bound)} have the same sign (This should output failure),
     * it'll continue to iterate blindly until it reaches the max number of iterations or reaches the min absolute error.
     * And it'll probably output a very wrong solution or maybe a very close one. If the solution is wrong, it'll produce a bug and bugs are always fun.
     * Actually not always, but most of the time.</p>
     * <p>If it exceeds the max number of iterations while it didn't reach the min absolute error, it'll output the most recent solution it calculated.</p>
     *
     * @param function The function you're trying to solve. f(x) = 0.
     * @param xl The lower bound.
     * @param xu The upper bound.
     * @param minAbsError
     * @param maxIterations
     * @param iterationsCount The value of the passed variable will replaced by the number of iterations that actually happened. If you don't want to know, pass null.
     * @param finalAbsError The value of the passed variable will replaced by the value of the final abs err. If you don't want to know, pass null.
     * @return
     */
    public static float bisection(BisectionFunction function, float xl, float xu, float minAbsError, int maxIterations, Integer iterationsCount, Float finalAbsError) {
        float f_xu = function.function(xu);
        if (f_xu < 0) {
            float temp = xl; //swap
            xl = xu;
            xu = temp;
        }
        // xu is always the point that makes function(xu) > 0.

        float pSol = 0; // previous
        float sol = 0;
        float abs_Error = 0;
        int i;
        for (i = 0; i < maxIterations; i++) {
            pSol = sol;

            sol = (xl + xu) / 2f;

            if (i > 1) {
                abs_Error = Math.abs((sol-pSol)/sol);
                if (abs_Error <= minAbsError)
                    break; // Success
            }

            float f_sol = function.function(sol);

            if (f_sol < 0)
                xl = sol;
            else if (f_sol > 0)
                xu = sol;
            else
                break; // An exact solution found.
        }

        if (iterationsCount != null) iterationsCount = i;
        if (finalAbsError != null) finalAbsError = abs_Error;
        return sol;
    }

    /**
     * <p>This implementation of the false position method always outputs a solution even when it should output failure.</p>
     * <p>This function will only work properly and as expected if you're trying to solve a function that has a unique solution within the given bounds.</p>
     * <p>So, don't use it purely to solve mathematical problems.
     * Use it only in your game after you came up with your perfect mathematical model that ensures a unique solution with in the bounds.</p>
     * <p>The reason it doesn't output failure is I don't want the failure of it to interrupt or crash the game at any given moment.</p>
     * <p>If the function you're trying to solve have 2 solutions with in the given bounds, it'll (most likely) return the higher in value.
     * If it has more than 2, it'll output one of them. Which one ?, you might ask, this only depends on the upper and lower bounds.</p>
     * <p>If both function(lower bound) and function(upper bound) have the same sign (This should output failure), it'll return the value of {@code failureOutput}.
     * And it'll probably output a very wrong solution or maybe a very close one. If the solution is wrong, it'll produce a bug and bugs are always fun.
     * Actually not always, but most of the time.</p>
     * <p>If it exceeds the max number of iterations while it didn't reach the min absolute error, it'll output the most recent solution it calculated.</p>
     * <p><b><i>TIP : Swapping the upper and lower bounds will (most likely) result in a different solution if there're multiple ones.</i></b></p>
     * <p><b><i>TIP : The function starts searching for the root starting from the upper bound.
     * So, If you know that the root is closer to one of the bounds, pass this bound to the function as the upper one.
     * And this will a noticeable difference to the performance.</i></b></p>
     *
     * @param function The function you're trying to solve. f(x) = 0.
     * @param xl The lower bound.
     * @param xu The upper bound.
     * @param minAbsError
     * @param maxIterations
     * @param lines The number of lines that linearly interpolate the function in the given bounds.
     *              Setting it to 2 or 3 is preferred as it decreases the time of the calculations significantly
     *              especially if the function you're trying to solve is a highly non-linear one like : exp(200*(x-1))-0.04.
     * @param failureOutput When there's no solution with in the given bounds, the function returns this value.
     * @param iterationsCount The value of the passed variable will replaced by the number of iterations that actually happened. If you don't want to know, pass null.
     * @param finalAbsError The value of the passed variable will replaced by the value of the final abs err. If you don't want to know, pass null.
     * @return
     */
    public static float bisectionFalsePos(BisectionFunction function, float xl, float xu, float minAbsError, int maxIterations, int lines, float failureOutput, MutableInteger iterationsCount, MutableFloat finalAbsError) {
        if (maxIterations <= 0) maxIterations = 1;
        if (lines <= 0) lines = 2;

        float originalXu = xu;

        float pSol; // previous
        float sol = 0;
        float abs_Error = 0;

        float f_xu = function.function(xu);
        int i;
        for (i = 0; i < maxIterations; i++) {
            pSol = sol;

            float f_xl = 0;
            float inc = (xu - xl)/lines;
            int j;
            for (j = 0; j < lines; j++) {
                float currentXl = xu-inc;
                f_xl = function.function(currentXl);
                float d = f_xl - f_xu;
                if (d == 0 | f_xl*f_xu > 0) { // If both points are on the same horizontal line. OR both have the same sign.
                    xu = currentXl;
                    f_xu = f_xl;
                } else {
                    sol = xu - (f_xu * (currentXl - xu)) / d;
                    xl = currentXl;
                    break;
                }
            }

            if (j == lines) { // The function doesn't intersect the x axis (No solution)
                /*xu = originalXu;
                xl = originalXl;
                sol = (xu + xl) / 2;*/
                if (i == 0)
                    return failureOutput;
            }

            if (i > 1) {
                abs_Error = Math.abs((sol-pSol)/sol);
                if (abs_Error <= minAbsError)
                    break; // Success
            }

            float f_sol = function.function(sol);

            if (f_sol*f_xl < 0)
                xu = sol;
            else if (f_sol*f_xu < 0)
                xl = sol;
            else
                break; // An exact solution found.
        }

        if (iterationsCount != null) iterationsCount.myInt = i;
        if (finalAbsError != null) finalAbsError.myFloat = abs_Error;
        return sol;
    }


    public static class MutableInteger {
        public int myInt;

        public MutableInteger(int myInt) {
            this.myInt = myInt;
        }
    }

    public static class MutableFloat {
        public float myFloat;

        public MutableFloat(float myFloat) {
            this.myFloat = myFloat;
        }
    }

    public static class MutableLong {
        public long myLong;

        public MutableLong(long myLong) {
            this.myLong = myLong;
        }
    }

    public static class MutableDouble {
        public double myDouble;

        public MutableDouble(double myDouble) {
            this.myDouble = myDouble;
        }
    }
}