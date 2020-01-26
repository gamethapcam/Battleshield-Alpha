package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;

public class ResumeGraduallyTesting {
    public static final String TAG = ResumeGraduallyTesting.class.getSimpleName();

    private MyInterpolation myInterpolation;
    private float durationMillis, initialVal, finalVal;

    private MyMath.BisectionFunction[] functionsS_s;
    private MyMath.BisectionFunction[] functions0S_s;
    private MyMath.BisectionFunction[] functionsRE;

    private float pauseX;
    private float r_d; //duration
    private float oxy_pauseX;
    private float s_s; //starting slope

    private static final float SEXY_G = 4.5f;


    public ResumeGraduallyTesting() {
        initializeFunctions();
    }

    public void compareFunctions(MyInterpolation _myInterpolation, float _durationMillis, float _initialVal, float _finalVal, float _r_d, float _s_s, int sampleSize, float minAbsError, int maxIterations, int lines) {
        myInterpolation = _myInterpolation;
        durationMillis = _durationMillis;
        initialVal = _initialVal;
        finalVal = _finalVal;
        r_d = _r_d;

        MyMath.BisectionFunction[] functions;
        /*if (_s_s == 0)
            functions = functions0S_s;
        else {
            functions = functionsS_s;
            if (initialVal > finalVal) s_s = -_s_s;
            else s_s = _s_s;
        }*/
        s_s = _s_s;
        functions = functionsRE;



        float[] samplesX = new float[sampleSize];
        double[][] timeOfEachSample = new double[functions.length][sampleSize];
        double[][] solutionsY = new double[functions.length][sampleSize];
        int[][] iterationsOfEachSample = new int[functions.length][sampleSize];
        float[][] finalErrOfEachSample = new float[functions.length][sampleSize];

        for(int i = 0; i < functions.length; i++) {
            for (int j = 0; j < sampleSize; j++) {
                pauseX = j*(durationMillis/(sampleSize-1));
                if (i == 0) samplesX[j] = pauseX;
                oxy_pauseX = myInterpolation.apply(0, durationMillis, initialVal, finalVal, pauseX);

                MyMath.MutableInteger currentIterations = new MyMath.MutableInteger(0);
                MyMath.MutableFloat currentFinalAbsErr = new MyMath.MutableFloat(0);
                long startTime = System.nanoTime();
                float sol = MyMath.bisectionFalsePos(functions[i], pauseX, durationMillis, minAbsError, maxIterations, lines, durationMillis, currentIterations, currentFinalAbsErr);
                double time = MyMath.millisSince(startTime);

                timeOfEachSample[i][j] = time;
                solutionsY[i][j] = sol;
                iterationsOfEachSample[i][j] = currentIterations.myInt;
                finalErrOfEachSample[i][j] = currentFinalAbsErr.myFloat;
            }
            /*Gdx.app.log(TAG, "Solutions" + i + " : " + MyMath.arrayToString(solutionsY[i]));
            Gdx.app.log(TAG, "Time" + i + " : " + MyMath.arrayToString(timeOfEachSample[i]));
            Gdx.app.log(TAG, MyMath.arrayStatistics(timeOfEachSample[i], false));*/
        }
        Gdx.app.log(TAG, MyMath.arrayToString(samplesX) + "\n\n");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Solutions" + i + " : " + MyMath.arrayToString(solutionsY[i]));
        }
        Gdx.app.log(TAG, "============================================================================================");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Time" + i + " : " + MyMath.arrayToString(timeOfEachSample[i]));
        }
        Gdx.app.log(TAG, "--------------------------------------------------------------------------------------------");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Time statistics : " + MyMath.arrayStatistics(timeOfEachSample[i], false));
        }
        Gdx.app.log(TAG, "============================================================================================");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Iterations" + i + " : " + MyMath.arrayToString(iterationsOfEachSample[i]));
        }
        Gdx.app.log(TAG, "--------------------------------------------------------------------------------------------");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Iterations' statistics : " + MyMath.arrayStatistics(iterationsOfEachSample[i], false));
        }
        Gdx.app.log(TAG, "============================================================================================");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Final Abs Err" + i + " : " + MyMath.arrayToString(finalErrOfEachSample[i]));
        }
        Gdx.app.log(TAG, "--------------------------------------------------------------------------------------------");

        for (int i = 0; i < functions.length; i++) {
            Gdx.app.log(TAG, "Err statistics : " + MyMath.arrayStatistics(finalErrOfEachSample[i], false));
        }
        Gdx.app.log(TAG, "============================================================================================");


    }

    private void initializeFunctions() {
        MyMath.BisectionFunction function0 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                x = MathUtils.clamp(x, 0.001f/* * durationMillis*/, Float.MAX_VALUE);

                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float val1 = -oxy_pauseX+oxy_x;
                float val2 = s_s*r_d*r_d;

                return (val1*val1)/val2 - d_dx_oxy_x;
            }
        };

        MyMath.BisectionFunction function1 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                x = MathUtils.clamp(x, 0.001f/* * durationMillis*/, Float.MAX_VALUE);

                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float val1 = -oxy_pauseX+oxy_x;
                float val2 = s_s*r_d*r_d;

                return val1*val1 - val2*d_dx_oxy_x;
            }
        };

        functionsS_s = new MyMath.BisectionFunction[2];
        functionsS_s[0] = function0;
        functionsS_s[1] = function1;


        MyMath.BisectionFunction function0S_s0 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float val1 = -oxy_pauseX+oxy_x;
                float val2 = MathUtils.PI/(2*r_d);

                return val1*val2 - d_dx_oxy_x;
            }
        };

        MyMath.BisectionFunction function0S_s1 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float val1 = -oxy_pauseX+oxy_x;
                float val2 = (2*r_d)/MathUtils.PI;

                return val1 - val2*d_dx_oxy_x;
            }
        };

        functions0S_s = new MyMath.BisectionFunction[2];
        functions0S_s[0] = function0S_s0;
        functions0S_s[1] = function0S_s1;

        MyMath.BisectionFunction functionRE0 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                x = MathUtils.clamp(x, 0.001f/* * durationMillis*/, Float.MAX_VALUE);

                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float p = SEXY_G;
                float val1 = (-oxy_pauseX+oxy_x);
                float val2 = (float) (p/(1-Math.exp(-p)));
                float val3 = (float) (Math.exp(p)-1);
                float val4 = 2*r_d;

                float term1 = val3*val1*val1;
                float term2 = (-p*val1) + val4*s_s*val3;
                float term3 = -val4*d_dx_oxy_x + val2*val1;

                return term1 + term2 * term3;
            }
        };


        MyMath.BisectionFunction functionRE1 = new MyMath.BisectionFunction() {
            @Override
            public float function(float x) {
                x = MathUtils.clamp(x, 0.001f/* * durationMillis*/, Float.MAX_VALUE);

                float oxy_x = myInterpolation.apply(0, durationMillis, initialVal, finalVal, x);
                float d_dx_oxy_x = myInterpolation.slopeAt(0, durationMillis, initialVal, finalVal, x);

                float p = SEXY_G;
                float val1 = (-oxy_pauseX + oxy_x);
                float val2 = (float) (p / (1 - Math.exp(-p)));
                float val3 = (float) (Math.exp(p) - 1);
                float val4 = 2 * r_d;


                float term1 = val1 * val1;
                float term2 = (-p * val1) / val3 + val4 * s_s;
                float term3 = -val4 * d_dx_oxy_x + val2 * val1;

                return term1 + term2 * term3;
            }

        };

        functionsRE = new MyMath.BisectionFunction[2];
        functionsRE[0] = functionRE0;
        functionsRE[1] = functionRE1;
    }
}
