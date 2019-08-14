package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;

public abstract class MyTween extends Tween {

    public static final String TAG = MyTween.class.getSimpleName();

    public enum PauseResumeGraduallyInterpolationMode {EXP5, EXP10}

    private MyInterpolation myInterpolation;
    private float initialVal;
    private float finalVal;

    private boolean isPausingGradually;
    private PauseResumeGraduallyInterpolationMode pauseResumeGraduallyInterpolationMode;
    private GradualInterpolation gradualInterpolation;
    private float percentageWhenStartingToPauseGradually;
    private float pauseDurationMillis;
    private float pausingGraduallyCurrentTime;



    private Array<Float> _xPoints, _yPoints;

    public MyTween(float durationMillis, MyInterpolation myInterpolation, Transition transition) {
        super(durationMillis, myInterpolation, transition);
        this.myInterpolation = myInterpolation;
        initializeGradualInterpolation();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, AdvancedStage game) {
        super(durationMillis, myInterpolation, game);
        this.myInterpolation = myInterpolation;
        initializeGradualInterpolation();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, Transition transition, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation, transition);
        this.myInterpolation = myInterpolation;
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualInterpolation();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, AdvancedStage game, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation, game);
        this.myInterpolation = myInterpolation;
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualInterpolation();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation) {
        super(durationMillis, myInterpolation);
        this.myInterpolation = myInterpolation;
        initializeGradualInterpolation();
    }

    public MyTween(float initialVal, float finalVal) {
        super(0);
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualInterpolation();
    }

    public MyTween(float durationMillis, MyInterpolation myInterpolation, float initialVal, float finalVal) {
        super(durationMillis, myInterpolation);
        this.myInterpolation = myInterpolation;
        this.initialVal = initialVal;
        this.finalVal = finalVal;
        initializeGradualInterpolation();
    }

    public MyTween(MyInterpolation myInterpolation) {
        super(myInterpolation);
        initializeGradualInterpolation();
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

    public abstract void myTween(float percentage, Interpolation interpolation, float initialVal, float finalVal);


    @Override
    public void onUpdate(float delta) {
        if (isPausingGradually)
            pausingGradually(delta);
        else
            if (isStarted() & getPercentage() < 1) {
                myTween(getPercentage(), myInterpolation, initialVal, finalVal);
                /*if (_xPoints == null) _xPoints = new Array<Float>(1000);
                if (_yPoints == null) _yPoints = new Array<Float>(1000);
                _xPoints.add(getPercentage());
                _yPoints.add(myInterpolation.apply(initialVal, finalVal, getPercentage()));*/
            }

    }

    @Override
    public void onFinish() {
        printXY();
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

    public void pauseGradually(float pauseDurationMillis, PauseResumeGraduallyInterpolationMode pauseInterpolationMode) {
        pauseResumeGraduallyInterpolationMode = pauseInterpolationMode;
        percentageWhenStartingToPauseGradually = getPercentage();
        isPausingGradually = true;
        this.pauseDurationMillis = pauseDurationMillis;
        pausingGraduallyCurrentTime = 0/*percentageWhenStartingToPauseGradually * pauseDurationMillis*/;

        float s = myInterpolation.slopeAt(percentageWhenStartingToPauseGradually);
        gradualInterpolation.init(s);

        /*_xPoints.clear();
        _yPoints.clear();*/

        Gdx.app.log(TAG, "main = " + myInterpolation.apply(initialVal, finalVal, percentageWhenStartingToPauseGradually)
                + ", gradual = " + gradualInterpolation.apply(initialVal, finalVal, 0));
    }

    private void pausingGradually(float delta) {
        setPercentage(percentageWhenStartingToPauseGradually);
        pausingGraduallyCurrentTime += delta * MyMath.secondsToMillis;

        if (pausingGraduallyCurrentTime <= pauseDurationMillis) {
            myTween(/*((*/pausingGraduallyCurrentTime / pauseDurationMillis/*) - percentageWhenStartingToPauseGradually) / (1-percentageWhenStartingToPauseGradually)*/, gradualInterpolation, initialVal, finalVal);
            /*_xPoints.add(pausingGraduallyCurrentTime / pauseDurationMillis);
            _yPoints.add(gradualInterpolation.currentAppliedValue);*/
        } else {
            float i = myInterpolation.inverseFunction(initialVal, finalVal, 1);
            Gdx.app.log(TAG, "inverseFinalPauseGradual = "+i);
            setPercentage(i); //TODO: Inverse
            pause();
            isPausingGradually = false;
            printXY();
        }

        onPausingGradually();
    }

    public void onPausingGradually() {

    }

    @Override
    public float getPercentage() { // TODO: If called when pausing gradually return the inverse.
        if (isPausingGradually)
            return myInterpolation.inverseFunction(initialVal, finalVal, gradualInterpolation.currentAppliedValue);
        return super.getPercentage();
    }

    //TODO: شوفلك حل ف setPercentage()

    //TODO: Make sure that when pause() is called while pausing gradually, the tween pauses the gradual pausing.
    /*TODO: resume() has 2 special cases:
    *  1) If pause() was called before : resume() resumes the gradual pause.
    *  2) If pause() wasn't called before : resume() finishes the gradual pause and resumes the main tween with the main interpolation.
    *  - Make documentation describing those 2 special cases.*/

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

    private void initializeGradualInterpolation() {
        gradualInterpolation = new GradualInterpolation();
    }






    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////







    private class GradualInterpolation extends Interpolation {

        private MyInterpolation gradualInterpolationItself;
        private float sx;
        private float mypg;
        private float gsx;

        private float currentAppliedValue;

        private GradualInterpolation() {

        }

        private void init(float s) {

            if(s > 0) {
                if (pauseResumeGraduallyInterpolationMode == PauseResumeGraduallyInterpolationMode.EXP5)
                    gradualInterpolationItself = MyInterpolation.fastExp5Out;
                else if (pauseResumeGraduallyInterpolationMode == PauseResumeGraduallyInterpolationMode.EXP10)
                    gradualInterpolationItself = MyInterpolation.fastExp10Out;
            } else {
                if (pauseResumeGraduallyInterpolationMode == PauseResumeGraduallyInterpolationMode.EXP5)
                    gradualInterpolationItself = MyInterpolation.fastExp5In;
                else if (pauseResumeGraduallyInterpolationMode == PauseResumeGraduallyInterpolationMode.EXP10)
                    gradualInterpolationItself = MyInterpolation.fastExp10In;
            }

            sx = gradualInterpolationItself.xAtWhichSlopeEquals(myInterpolation.slopeAt(percentageWhenStartingToPauseGradually));
            mypg = myInterpolation.apply(initialVal, finalVal, percentageWhenStartingToPauseGradually);
            gsx = gradualInterpolationItself.apply(sx);

            /*Gdx.app.log(TAG, "percentageWhenStartingToPauseGradually = " + percentageWhenStartingToPauseGradually
                    + ", slope = " + myInterpolation.slopeAt(percentageWhenStartingToPauseGradually)
                    + ", sx = " + sx
                    + ", mypg = " + mypg
                    + ", gsx = " + gsx);*/
        }

        @Override
        public float apply(float a) {
            currentAppliedValue = apply(0, 1, a);
            return currentAppliedValue;
        }

        @Override
        public float apply(float start, float end, float a) {
            currentAppliedValue = (end - start) * (gradualInterpolationItself.apply(sx + a*(1-percentageWhenStartingToPauseGradually)/*sx-percentageWhenStartingToPauseGradually+a*/) - gsx) + mypg;
            //Gdx.app.log(TAG, "currentAppliedValue = " + currentAppliedValue);
            return currentAppliedValue;
        }
    }

}
