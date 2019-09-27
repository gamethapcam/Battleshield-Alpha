package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * This class has an additional functionality which is pausing gradually. unlike the normal pausing, gradual pausing pauses the tween smoothly with some sort of deceleration.
 *
 * @Author Mahmoud Yamani
 */
public abstract class MyTween extends Tween {

    public static final String TAG = MyTween.class.getSimpleName();

    private MyInterpolation myInterpolation;
    //private MyInterpolationCurrentValueSaved myInterpolationCurrentValueSaved;
    private float initialVal;
    private float finalVal;

    private GradualStuff currentGradualStuff;

    private GradualPausingStuff gradualPausingStuff;
    private boolean isPausingGradually;
    private boolean isPausedNormally;

    private GradualResumingStuff gradualResumingStuff;
    private boolean isResumingGradually;

    private Array<Float> _xPoints, _yPoints;

    public MyTween(float durationMillis, MyInterpolation myInterpolation, Transition transition) {
        super(durationMillis, myInterpolation, transition);
        initializeMyInterpolation(myInterpolation);
        initializeGradualStuff();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, AdvancedStage game) {
        super(durationMillis, myInterpolation, game);
        initializeMyInterpolation(myInterpolation);
        initializeGradualStuff();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, Transition transition, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation, transition);
        initializeMyInterpolation(myInterpolation);
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualStuff();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, AdvancedStage game, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation, game);
        initializeMyInterpolation(myInterpolation);
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualStuff();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation) {
        super(durationMillis, myInterpolation);
        initializeMyInterpolation(myInterpolation);
        initializeGradualStuff();
    }

    public MyTween(float initialVal, float finalVal) {
        super(0);
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualStuff();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation);
        initializeMyInterpolation(myInterpolation);
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualStuff();
    }

    public MyTween(MyInterpolation myInterpolation) {
        super(myInterpolation);
        initializeGradualStuff();
    }

    public MyTween(float durationMillis) {
        super(durationMillis);
    }

    public MyTween() {
        super(0);
    }

    @Deprecated
    @Override
    public void tween(float percentage, Interpolation interpolation) {

    }

    public abstract void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX);


    Array<Double> durations = new Array<Double>(Double.class);
    Array<Double> durationsGradually = new Array<Double>(Double.class);
    @Override
    public void onUpdate(float delta) {
        /*durations.add(1.0, 2.0, 3.0, 4.0);
        Gdx.app.log(TAG, durations.get(0) + ", " + durations.get(1) + ", " + durations.get(2) + ", " + durations.get(3) + ", Med = " + MyMath.medianOfArray(durations.items));*/
        //Gdx.app.log(TAG, "isPausingGradually = " + isPausingGradually + ", isResumingGradually = " + isResumingGradually);

        if (isPausingGradually || isResumingGradually) {
            //long before = System.nanoTime();
            currentGradualStuff.updateGradualTween(delta);
            //durationsGradually.add(MyMath.millisSince(before));
        } else
            if (isStarted() & getPercentage() < 1) {
                //long before = System.nanoTime();
                myTween(myInterpolation, 0, getDurationMillis(), initialVal, finalVal, getPercentage()*getDurationMillis());
                //durations.add(MyMath.millisSince(before));
                /*if (_xPoints == null) _xPoints = new Array<Float>(1000);
                if (_yPoints == null) _yPoints = new Array<Float>(1000);
                _xPoints.add(getPercentage());
                _yPoints.add(myInterpolation.apply(initialVal, finalVal, getPercentage()));*/
            }

    }

    @Override
    public void onFinish() {
        myTween(myInterpolation, 0, getDurationMillis(), initialVal, finalVal, getDurationMillis());
        printXY();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPausedNormally = true;
    }

    private void printXY() {
        /*StringBuilder x = new StringBuilder("[");
        for (int i = 0; i < _xPoints.size; i++) {
            if (i < _xPoints.size-1) {
                x.append(_xPoints.get(i));
                x.append(", ");
            } else
                x.append(_xPoints.get(i));
        }
        x.append("]");
        Gdx.app.log(TAG, _xPoints.size + ", " + x.toString());

        StringBuilder y = new StringBuilder("[");
        for (int i = 0; i < _yPoints.size; i++) {
            if (i < _yPoints.size-1) {
                y.append(_yPoints.get(i));
                y.append(", ");
            } else
                y.append(_yPoints.get(i));
        }
        y.append("]");
        Gdx.app.log(TAG, y.toString());*/
    }

    /**
     *
     * Note:
     * @param gradualPausingDurationMillis
     * @param finalValueAfterGradualPausing
     */
    public void pauseGradually(float gradualPausingDurationMillis, float finalValueAfterGradualPausing) {

        //if()

        if (!isStarted() | isFinished() | isPaused()) return;
        gradualPausingStuff.initPausingGradually(gradualPausingDurationMillis, finalValueAfterGradualPausing);

    }

    /**
     * This function computes finalValueAfterGradualPausing for you, in order to achieve a G of {@link GradualPausingStuff#SEXY_G} or a value as close as possible to it. When G equals
     * @param gradualPausingDurationMillis
     */
    public void pauseGradually(float gradualPausingDurationMillis) {
        //Gdx.app.log(TAG, "Normal : " + MyMath.arrayStatistics(durations.items, false));
        if (!isStarted() | isFinished() | isPaused()) return;
        currentGradualStuff = gradualPausingStuff;
        gradualPausingStuff.initPausingGradually(gradualPausingDurationMillis);
    }

    public void onPausingGradually() {

    }

    public void resumeGradually(float gradualResumingDurationMillis) {
        if (!(isPausingGradually | isPaused())) return;
        currentGradualStuff = gradualResumingStuff;
        gradualResumingStuff.initResumingGradually(gradualResumingDurationMillis);
    }

    public void onResumingGradually() {

    }

    @Override
    public float getPercentage() {
        /*if (isPausingGradually) {
            float currentAppliedValue = ((MyInterpolationCurrentValueSaved)(gradualPausingStuff.myExpOutGradualPausing.getMyInterpolationInOut())).currentAppliedValue;
            float inverse = myInterpolation.inverseFunction(0, getDurationMillis(), initialVal, finalVal, currentAppliedValue) / getDurationMillis();
            return MathUtils.clamp(inverse, 0, 1);
        }*/
        return super.getPercentage();
    }

    //TODO: شوفلك حل ف setPercentage()

    //TODO: Make sure that when pause() is called while pausing gradually, the tween pauses the gradual pausing.
    /*TODO: resume() has 2 special cases:
    *  1) If pause() was called before : resume() resumes the gradual pause.
    *  2) If pause() wasn't called before : resume() finishes the gradual pause and resumes the main tween with the main interpolation.
    *  - Make documentation describing those 2 special cases.*/

    /*TODO: When resumeGradually() is called in the middle of gradual pausing, make resumeGradually() work on the interpolation of the gradual pause not the main interpolation.
        And when pauseGradually() is called in the middle of gradual resuming, make pauseGradually() work on the interpolation of the gradual resume not the main interpolation.
     */


    @Override
    public void onResume() {
        if (isPausedNormally) {
            isPausedNormally = false;
            return;
        }

        if (isPausingGradually)
            isPausingGradually = false;
    }

    public float getInitialVal() {
        return initialVal;
    }

    public void setInitialVal(float initialVal) {
        this.initialVal = initialVal;
    }

    public float getFinalVal() {
        return finalVal;
    }

    public void setFinalVal(float finalVal) {
        this.finalVal = finalVal;
    }

    public MyInterpolation getMyInterpolation() {
        return myInterpolation;
    }

    private void initializeGradualStuff() {
        gradualPausingStuff = new GradualPausingStuff();
        gradualResumingStuff = new GradualResumingStuff();
    }

    private void initializeMyInterpolation(MyInterpolation myInterpolation) {
        this.myInterpolation = myInterpolation;
        //myInterpolationCurrentValueSaved = new MyInterpolationCurrentValueSaved(myInterpolation);
    }

    public float getCurrentAppliedValue() {
        return myInterpolation.apply(0, getDurationMillis(), initialVal, finalVal, getPercentage()*getDurationMillis());
    }




    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////



    public enum GradualType{PAUSING, RESUMING}
    private class GradualStuff {
        protected GradualType gradualType;

        protected MyInterpolationCurrentValueSaved gradualInterpolation;
        protected float gradualDurationMillis;
        protected float gradualCurrentX;

        protected float sx;
        protected float sy;
        protected float ex;
        protected float ey;

        private void updateGradualTween(float delta) {
            gradualCurrentX += delta * MyMath.secondsToMillis;

            if (gradualCurrentX < ex) {

                myTween(gradualInterpolation, sx, ex, sy, ey, gradualCurrentX);

                setPercentageToInverse(gradualInterpolation); //TODO: This calculation maybe expensive to be done every frame. If you have time make it more efficient. Don't set percentage to the inverse every frame. instead, calculate the inverse when getPercentage,  when resuming and when finishing the gradual pause. And don't forget to handle the increase in currentTime (Timer class).

                if(gradualType == GradualType.PAUSING) onPausingGradually();
                else onResumingGradually();
                /*_xPoints.add(gradualCurrentX / gradualDurationMillis);
                _yPoints.add(gradualInterpolation.currentAppliedValue);*/
            } else {
                myTween(gradualInterpolation, sx, ex, sy, ey, ex);
                /*float inverse = getPercentage();
                Gdx.app.log(TAG, "inverseFinalPauseGradual = "+inverse);
                setPercentage(inverse); //TODO: Make setPercentage don't throw an exception.*/
                setPercentageToInverse(gradualInterpolation);
                //printXY();
                //Gdx.app.log(TAG, "gradualPauseDuration = " + gradualPauseDuration);
                if (gradualType == GradualType.PAUSING) {
                    pause();
                    isPausingGradually = false;
                } else {
                    //resume();
                    isResumingGradually = false;
                }
                /*Gdx.app.log(TAG, "Gradual : " + MyMath.arrayStatistics(durationsGradually.items, false));*/

            }
        }

        private void setPercentageToInverse(MyInterpolationCurrentValueSaved gradualInterpolation) {
            float currentAppliedValue = gradualInterpolation.currentAppliedValue;
            //Gdx.app.log(TAG, "currentAppliedValue = " + currentAppliedValue);
            float inverse = myInterpolation.inverseFunction(0, getDurationMillis(), initialVal, finalVal, currentAppliedValue) / getDurationMillis();
            setPercentage(MathUtils.clamp(inverse, 0, 1));
        }
    }



    private class GradualPausingStuff extends GradualStuff{

        private static final int APPROX_SOL_POWER = 7;
        /**
         * When G equals this value, the gradual pause stops very smoothly.
         */
        public static final float SEXY_G = 4.5f;

        //private float gradualPauseDuration;

        private GradualPausingStuff() {
            gradualInterpolation = new MyInterpolationCurrentValueSaved(new MyInterpolation.MyInterpolationOut(new MyInterpolation.MyExp()));
            gradualType = GradualType.PAUSING;
        }

        private void initPausingGradually(float gradualPausingDurationMillis, float finalValueAfterGradualPausing) {

            //gradualPauseDuration = 0;

            gradualDurationMillis = gradualPausingDurationMillis;

            float cx, cy, slope, currentMainValue;

            if (isResumingGradually) {
                gradualCurrentX = gradualResumingStuff.gradualCurrentX;
                cx = gradualCurrentX;
                cy = gradualResumingStuff.gradualInterpolation.currentAppliedValue;
                float r_sx = gradualResumingStuff.sx;
                float r_sy = gradualResumingStuff.sy;
                float r_ex = gradualResumingStuff.ex;
                float r_ey = gradualResumingStuff.ey;
                slope = gradualResumingStuff.gradualInterpolation.slopeAt(r_sx, r_ex, r_sy, r_ey, gradualCurrentX);
                currentMainValue = /*gradualResumingStuff.gradualInterpolation.currentAppliedValue*/cy;
                isResumingGradually = false;
            } else {
                gradualCurrentX = getPercentage() * getDurationMillis();
                cx = gradualCurrentX;
                cy = myInterpolation.apply(initialVal, finalVal, getPercentage());
                slope = myInterpolation.slopeAt(0, getDurationMillis(), initialVal, finalVal, getPercentage() * getDurationMillis());
                currentMainValue = /*myInterpolationCurrentValueSaved.currentAppliedValue*//*getCurrentAppliedValue()*/cy;
            }
            //float currentMainValue = /*myInterpolationCurrentValueSaved.currentAppliedValue*/getCurrentAppliedValue();

            if (slope > 0 & finalValueAfterGradualPausing > finalVal) {
                Gdx.app.log(TAG, "According to initialVal and finalVal, finalValueAfterGradualPausing(" + finalValueAfterGradualPausing + ") can't be greater than finalVal(" + finalVal + "). So, finalValueAfterGradualPausing is now changed to finalVal");
                ey = finalVal;
            } else if (slope < 0 & finalValueAfterGradualPausing < finalVal) {
                Gdx.app.log(TAG, "According to initialVal and finalVal, finalValueAfterGradualPausing(" + finalValueAfterGradualPausing + ") can't be less than finalVal(" + finalVal + "). So, finalValueAfterGradualPausing is now changed to finalVal");
                ey = finalVal;
            } else
                ey = finalValueAfterGradualPausing;


            if (slope > 0 & finalValueAfterGradualPausing < currentMainValue)
                throw new ValueOutOfRangeException("According to initialVal, finalVal and the moment you started the gradual pause, finalValueAfterGradualPausing(" + finalValueAfterGradualPausing + ") must be greater than " + currentMainValue + ". Which is the value of myInterpolation's current applied value.");
            else if (slope < 0 & finalValueAfterGradualPausing > currentMainValue)
                throw new ValueOutOfRangeException("According to initialVal, finalVal and the moment you started the gradual pause, finalValueAfterGradualPausing(" + finalValueAfterGradualPausing + ") must be less than " + currentMainValue + ". Which is the value of myInterpolation's current applied value.");

            ex = cx + gradualDurationMillis;
            float E = ex - cx;
            float F = ey - cy;
            float G = slope*E / F;
            float G01_A_sx = calculateSxSy(cx, cy, E, G);

            //printValues(gradualPausingDurationMillis, cx, cy, slope, E, F, G, G01_A_sx);

            if (F == 0 | G01_A_sx == Float.NEGATIVE_INFINITY) {
                pause();
                return;
            }

            /*_xPoints.clear();
            _yPoints.clear();*/

            isPausingGradually = true;
        }

        private void initPausingGradually(float gradualPausingDurationMillis) {
            //gradualPauseDuration = 0;
            gradualDurationMillis = gradualPausingDurationMillis;
            float cx, cy, slope;

            if (isResumingGradually) {
                gradualCurrentX = gradualResumingStuff.gradualCurrentX;
                cx = gradualCurrentX;
                cy = gradualResumingStuff.gradualInterpolation.currentAppliedValue;
                float r_sx = gradualResumingStuff.sx;
                float r_sy = gradualResumingStuff.sy;
                float r_ex = gradualResumingStuff.ex;
                float r_ey = gradualResumingStuff.ey;
                slope = gradualResumingStuff.gradualInterpolation.slopeAt(r_sx, r_ex, r_sy, r_ey, gradualCurrentX);
                isResumingGradually = false;
            } else {
                gradualCurrentX = getPercentage() * getDurationMillis();
                cx = gradualCurrentX;
                cy = myInterpolation.apply(initialVal, finalVal, getPercentage());
                slope = myInterpolation.slopeAt(0, getDurationMillis(), initialVal, finalVal, getPercentage() * getDurationMillis());
            }
            ex = cx + gradualDurationMillis;
            float G = SEXY_G;
            float E = ex-cx;
            ey = cy + (slope*E)/G;
            if (slope > 0 & ey > finalVal | slope < 0 & ey < finalVal) {
                ey = finalVal;
                G = slope*E / (ey-cy);
            }
            float F = ey - cy;
            float G01_A_sx = calculateSxSy(cx, cy, E, G);

            printValues(gradualPausingDurationMillis, cx, cy, slope, E, ey-cy, G, G01_A_sx);

            if (F == 0 | G01_A_sx == Float.NEGATIVE_INFINITY) {
                pause();
                return;
            }

            isPausingGradually = true;
        }

        private void printValues(float gradualPausingDurationMillis, float cx, float cy, float slope, float E, float F, float G, float G01_A_sx) {
            Gdx.app.log(TAG, "--------------------------------------------------------------------");
            Gdx.app.log(TAG, "durationMillis = " + getDurationMillis());
            Gdx.app.log(TAG, "percentage = " + getPercentage());
            Gdx.app.log(TAG, "initialVal = " + initialVal);
            Gdx.app.log(TAG, "finalVal = " + finalVal);
            Gdx.app.log(TAG, "gradualPausingDurationMillis = " + gradualPausingDurationMillis);
            Gdx.app.log(TAG, "cx = " + cx);
            Gdx.app.log(TAG, "cy = " + cy);
            Gdx.app.log(TAG, "slope = " + slope);
            Gdx.app.log(TAG, "ex = " + ex);
            Gdx.app.log(TAG, "ey = " + ey);
            Gdx.app.log(TAG, "E = " + E);
            Gdx.app.log(TAG, "F = " + F);
            Gdx.app.log(TAG, "G = " + G + ". Make sure that this value gets closer to " + SEXY_G + ". You can change it by tweaking gradualPausingDurationMillis and finalValueAfterGradualPausing.");
            Gdx.app.log(TAG, "sx = " + sx);
            Gdx.app.log(TAG, "A_sx = " + (cx-sx)/(ex-sx));
            Gdx.app.log(TAG, "G01_A_sx = " + G01_A_sx);
            Gdx.app.log(TAG, "sy = " + sy);
            Gdx.app.log(TAG, "--------------------------------------------------------------------");
        }

        private float calculateSxSy(float cx, float cy, float E, float G) {
            ((MyInterpolation.MyExp)((MyInterpolation.MyInterpolationOut)gradualInterpolation.getInterpolation()).getMyInterpolationInOut()).setPower(APPROX_SOL_POWER);
            sx = gradualInterpolation.apply(1, SEXY_G, -E, cx, G); // approximate solution
            ((MyInterpolation.MyExp)((MyInterpolation.MyInterpolationOut)gradualInterpolation.getInterpolation()).getMyInterpolationInOut()).setPower(G);
            float A_sx = (cx-sx)/(ex-sx);
            float G01_A_sx = gradualInterpolation.apply(A_sx);
            sy = (ey*G01_A_sx-cy)/(G01_A_sx-1);
            return G01_A_sx;
        }
    }


    private class GradualResumingStuff extends GradualStuff{
        public static final float defaultS_s = 0.004f; //starting slope

        ResumeGraduallyInterpolation resumeGraduallyInterpolation;

        MyMath.BisectionFunction bisectionFunction;

        private float pauseX;
        private float r_d;
        private float s_s;
        private float oxy_pauseX;

        public GradualResumingStuff() {
            resumeGraduallyInterpolation = new ResumeGraduallyInterpolation();
            gradualInterpolation = new MyInterpolationCurrentValueSaved(new MyInterpolation.MyInterpolationIn(resumeGraduallyInterpolation));
            gradualType = GradualType.RESUMING;

            initializeBisectionFunction();
        }

        private void initResumingGradually(float gradualResumingDurationMillis) {
            gradualDurationMillis = gradualResumingDurationMillis;

            if (isPausingGradually) {
                s_s = gradualPausingStuff.gradualInterpolation.slopeAt(
                        gradualPausingStuff.sx,
                        gradualPausingStuff.ex,
                        gradualPausingStuff.sy,
                        gradualPausingStuff.ey,
                        gradualPausingStuff.gradualCurrentX
                );
                oxy_pauseX = gradualPausingStuff.gradualInterpolation.currentAppliedValue;
                isPausingGradually = false;
            } else {
                if (finalVal - initialVal < 0)
                    s_s = -defaultS_s;
                else s_s = defaultS_s;
            }

            pauseX = getPercentage() *  getDurationMillis();
            oxy_pauseX = myInterpolation.apply(0, getDurationMillis(), initialVal, finalVal, pauseX);
            r_d = gradualDurationMillis;
            sy = oxy_pauseX;
            ex = MyMath.bisectionFalsePos(bisectionFunction, pauseX, getDurationMillis(), 1 / 100f, 4, 2, getDurationMillis(), null, null);
            sx = ex - r_d;
            gradualCurrentX = sx;
            ey = myInterpolation.apply(0, getDurationMillis(), initialVal, finalVal, ex);
            float a = 1 - s_s*(ex-sx)/(ey-sy);

            //((MyInterpolation.MyReciprocal)((MyInterpolation.MyInterpolationIn)gradualInterpolation.getInterpolation()).getMyInterpolationInOut()).setA(a);
            resumeGraduallyInterpolation.myReciprocal.setA(a);

            resume();
            isResumingGradually = true;
        }

        private void initializeBisectionFunction() {
            bisectionFunction = new MyMath.BisectionFunction() {
                @Override
                public float function(float x) {
                    x = MathUtils.clamp(x, 0.001f/* * durationMillis*/, Float.MAX_VALUE);

                    float oxy_x = myInterpolation.apply(0, getDurationMillis(), initialVal, finalVal, x);
                    float d_dx_oxy_x = myInterpolation.slopeAt(0, getDurationMillis(), initialVal, finalVal, x);

                    float p = GradualPausingStuff.SEXY_G;
                    float val1 = (-oxy_pauseX+oxy_x);
                    float val2 = (float) (p/(1-Math.exp(-p)));
                    float val3 = (float) (Math.exp(p)-1);
                    float val4 = 2*r_d;


                    float term1 = val1*val1;
                    float term2 = (-p*val1)/val3 + val4*s_s;
                    float term3 = -val4*d_dx_oxy_x + val2*val1;

                    return term1 + term2 * term3;
                }
            };
        }

        public class ResumeGraduallyInterpolation extends MyInterpolation.MyInterpolationInOut {

            private MyExp myExp;
            private MyReciprocal myReciprocal;

            public ResumeGraduallyInterpolation() {
                myExp = new MyExp(GradualPausingStuff.SEXY_G);
                myReciprocal = new MyReciprocal();
            }

            @Override
            protected float applyIn(float x01) {
                return (myExp.applyIn(x01)+myReciprocal.applyIn(x01))/2f;
            }

            @Override
            protected float applyOut(float x01) {
                return 0;
            }

            @Override
            protected float slopeAtIn(float x01) {
                return (myExp.slopeAtIn(x01)+myReciprocal.slopeAtIn(x01))/2f;
            }

            @Override
            protected float slopeAtOut(float x01) {
                return 0;
            }

            @Override
            protected float inverseFunctionIn(float y01) {
                return 0;
            }

            @Override
            protected float inverseFunctionOut(float y01) {
                return 0;
            }
        }
    }


    private class MyInterpolationCurrentValueSaved extends MyInterpolation {


        private float currentAppliedValue;
        private MyInterpolation interpolation;

        public MyInterpolationCurrentValueSaved(MyInterpolation interpolation) {
            this.interpolation = interpolation;
        }

        @Override
        public float slopeAt(float x01) {
            return interpolation.slopeAt(x01);
        }

        @Override
        public float inverseFunction(float y01) {
            return interpolation.slopeAt(y01);
        }

        @Override
        public float apply(float a) {
            return interpolation.apply(a);
        }

        @Override
        public float apply(float startY, float endY, float x01) {
            currentAppliedValue = super.apply(startY, endY, x01);
            return currentAppliedValue;
        }

        @Override
        public float apply(float startX, float endX, float startY, float endY, float currentX) {
            currentAppliedValue = super.apply(startX, endX, startY, endY, currentX);
            return currentAppliedValue;
        }

        public MyInterpolation getInterpolation() {
            return interpolation;
        }
    }
}
