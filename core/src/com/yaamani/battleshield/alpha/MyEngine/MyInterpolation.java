package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

/**
 * This class needed for MyTween's gradual pausing and gradual resuming.
 * @see MyTween
 * @Author Mahmoud Yamani
 */


public abstract class MyInterpolation extends Interpolation {
    public static final String TAG = MyInterpolation.class.getSimpleName();

    public enum InterpolationType {IN, OUT, IN_OUT}
    protected InterpolationType interpolationType;

    public abstract float slopeAt(float x01);
    //public abstract float xAtWhichSlopeEquals(float slope);
    public abstract float inverseFunction(float y01);


    @Override
    public float apply(float startY, float endY, float x01) {
        return super.apply(startY, endY, x01);
    }

    /**
     *
     * @param startX The start x value of the function.
     * @param endX The end x value of the function.
     * @param startY The start y value of the function.
     * @param endY The end y value of the function.
     * @param currentX is the function input. {startX <= currentX <= endX}.
     * @return
     */
    public float apply(float startX, float endX, float startY, float endY, float currentX) {
        return startY + (endY-startY) * apply((currentX-startX) / (endX-startX));
    }

    /**
     *
     * @param startX The start x value of the function.
     * @param endX The end x value of the function.
     * @param startY The start y value of the function.
     * @param endY The end y value of the function.
     * @param x The x value that we need the slope at. {startX <= x <= endX}.
     * @return
     */
    public float slopeAt(float startX, float endX, float startY, float endY, float x) {
        return (endY-startY) / (endX-startX) * slopeAt((x-startX) / (endX-startX));
    }

    /**
     *
     * @param startX The start x value of the function.
     * @param endX The end x value of the function.
     * @param startY The start y value of the function.
     * @param endY The end y value of the function.
     * @param y The y value that we need its corresponding x. {startY <= y <= endY}.
     * @return
     */
    public float inverseFunction(float startX, float endX, float startY, float endY, float y) {
        return inverseFunction((y-startY) / (endY-startY)) * (endX-startX) + startX;
    }

    public InterpolationType getInterpolationType() {
        return interpolationType;
    }











    public static abstract class MyInterpolationInOut extends MyInterpolation {

        public MyInterpolationInOut() {
            interpolationType = InterpolationType.IN_OUT;
        }

        @Override
        public float apply(float x01) {
            if (x01 <= 0.5f) return applyIn(2f*x01)/2f;
            else return 0.5f+applyOut(2f*(x01-0.5f))/2f;
        }

        @Override
        public float slopeAt(float x01) {
            if (x01 <= 0.5f) return slopeAtIn(2f*x01);
            else return slopeAtOut(2f*(x01-0.5f));

        }

        @Override
        public float inverseFunction(float y01) {
            if (y01 <= 0.5f) return inverseFunctionIn(y01 * 2)/2f;
            return inverseFunctionOut(2*(y01-0.5f))/2f + 0.5f;
        }

        protected abstract float applyIn(float x01);
        protected abstract float applyOut(float x01);

        protected abstract float slopeAtIn(float x01);
        protected abstract float slopeAtOut(float x01);

        protected abstract float inverseFunctionIn(float y01);
        protected abstract float inverseFunctionOut(float y01);
    }

    public static class MyInterpolationOut extends MyInterpolation {
        private final MyInterpolationInOut myInterpolationInOut;

        public MyInterpolationOut(MyInterpolationInOut myInterpolationInOut) {
            this.myInterpolationInOut = myInterpolationInOut;
            interpolationType = InterpolationType.OUT;
        }

        @Override
        public float apply(float a) {
            return myInterpolationInOut.applyOut(a);
        }

        @Override
        public float slopeAt(float x01) {
            return myInterpolationInOut.slopeAtOut(x01);
        }

        @Override
        public float inverseFunction(float y01) {
            return myInterpolationInOut.inverseFunctionOut(y01);
        }

        public MyInterpolationInOut getMyInterpolationInOut() {
            return myInterpolationInOut;
        }
    }

    public static class MyInterpolationIn extends MyInterpolation {
        private final MyInterpolationInOut myInterpolationInOut;

        public MyInterpolationIn(MyInterpolationInOut myInterpolationInOut) {
            this.myInterpolationInOut = myInterpolationInOut;
            interpolationType = InterpolationType.IN;
        }

        @Override
        public float apply(float a) {
            return myInterpolationInOut.applyIn(a);
        }

        @Override
        public float slopeAt(float x01) {
            return myInterpolationInOut.slopeAtIn(x01);
        }

        @Override
        public float inverseFunction(float y01) {
            return myInterpolationInOut.inverseFunctionIn(y01);
        }

        public MyInterpolationInOut getMyInterpolationInOut() {
            return myInterpolationInOut;
        }
    }
















    public static final MyLinear myLinear = new MyLinear();

    public static final MyExp myExp10 = new MyExp(10);
    public static final MyInterpolationIn myExp10In = new MyInterpolationIn(myExp10);
    public static final MyInterpolationOut myExp10Out = new MyInterpolationOut(myExp10);

    public static final MyExp myExp5 = new MyExp(5);
    public static final MyInterpolationIn myExp5In = new MyInterpolationIn(myExp5);
    public static final MyInterpolationOut myExp5Out = new MyInterpolationOut(myExp5);

    /*public static final MyCircle myCircle = new MyCircle();
    public static final MyInterpolationIn myCircleIn = new MyInterpolationIn(myCircle);
    public static final MyInterpolationOut myCircleOut = new MyInterpolationOut(myCircle);*/

    public static final MyPow myPow2 = new MyPow(2);
    public static final MyInterpolationIn myPow2In = new MyInterpolationIn(myPow2);
    public static final MyInterpolationOut myPow2Out = new MyInterpolationOut(myPow2);

    public static final MyPow myPow3 = new MyPow(3);
    public static final MyInterpolationIn myPow3In = new MyInterpolationIn(myPow3);
    public static final MyInterpolationOut myPow3Out = new MyInterpolationOut(myPow3);

    public static final MyPow myPow4 = new MyPow(4);
    public static final MyInterpolationIn myPow4In = new MyInterpolationIn(myPow4);
    public static final MyInterpolationOut myPow4Out = new MyInterpolationOut(myPow4);

    public static final MyPow myPow5 = new MyPow(5);
    public static final MyInterpolationIn myPow5In = new MyInterpolationIn(myPow5);
    public static final MyInterpolationOut myPow5Out = new MyInterpolationOut(myPow5);

    public static final MySine mySine = new MySine();
    public static final MyInterpolationIn mySineIn = new MyInterpolationIn(mySine);
    public static final MyInterpolationOut mySineOut = new MyInterpolationOut(mySine);

    public static final MyReciprocal myReciprocal_600 = new MyReciprocal(0.600f);
    public static final MyInterpolationIn myReciprocal_600In = new MyInterpolationIn(myReciprocal_600);
    public static final MyInterpolationOut myReciprocal_600Out = new MyInterpolationOut(myReciprocal_600);

    public static final MyReciprocal myReciprocal_750 = new MyReciprocal(0.750f);
    public static final MyInterpolationIn myReciprocal_750In = new MyInterpolationIn(myReciprocal_750);
    public static final MyInterpolationOut myReciprocal_750Out = new MyInterpolationOut(myReciprocal_750);

    public static final MyReciprocal myReciprocal_850 = new MyReciprocal(0.850f);
    public static final MyInterpolationIn myReciprocal_850In = new MyInterpolationIn(myReciprocal_850);
    public static final MyInterpolationOut myReciprocal_850Out = new MyInterpolationOut(myReciprocal_850);

    public static final MyReciprocal myReciprocal_875 = new MyReciprocal(0.875f);
    public static final MyInterpolationIn myReciprocal_875In = new MyInterpolationIn(myReciprocal_875);
    public static final MyInterpolationOut myReciprocal_875Out = new MyInterpolationOut(myReciprocal_875);

    public static final MyReciprocal myReciprocal_910 = new MyReciprocal(0.910f);
    public static final MyInterpolationIn myReciprocal_910In = new MyInterpolationIn(myReciprocal_910);
    public static final MyInterpolationOut myReciprocal_910Out = new MyInterpolationOut(myReciprocal_910);

    public static final MyReciprocal myReciprocal_930 = new MyReciprocal(0.930f);
    public static final MyInterpolationIn myReciprocal_930In = new MyInterpolationIn(myReciprocal_930);
    public static final MyInterpolationOut myReciprocal_930Out = new MyInterpolationOut(myReciprocal_930);

    public static final MyReciprocal myReciprocal_950 = new MyReciprocal(0.950f);
    public static final MyInterpolationIn myReciprocal_950In = new MyInterpolationIn(myReciprocal_950);
    public static final MyInterpolationOut myReciprocal_950Out = new MyInterpolationOut(myReciprocal_950);

    public static final MyReciprocal myReciprocal_960 = new MyReciprocal(0.960f);
    public static final MyInterpolationIn myReciprocal_960In = new MyInterpolationIn(myReciprocal_960);
    public static final MyInterpolationOut myReciprocal_960Out = new MyInterpolationOut(myReciprocal_960);

    /*public static final MyReciprocal myReciprocal_970 = new MyReciprocal(0.970f);
    public static final MyInterpolationIn myReciprocal_970In = new MyInterpolationIn(myReciprocal_970);
    public static final MyInterpolationOut myReciprocal_970Out = new MyInterpolationOut(myReciprocal_970);*/





    public static final Interpolation fadeIn = new Interpolation() {
        @Override
        public float apply(float a) {
            return fade.apply(0.5f*a) * 2;
        }
    };

    public static final Interpolation fadeOut = new Interpolation() {
        @Override
        public float apply(float a) {
            return fade.apply(a*0.5f + 0.5f)*2 - 1;
        }
    };




    public static final Interpolation threePulses = new Interpolation() {
        @Override
        public float apply(float a) {
            if (a <= 1f/6f | (a > 2f/6f & a <= 3f/6f) | (a > 4f/6f & a <= 6f/6f)) return 1;
            else return 0;
        }
    };




    public static final Interpolation fastSine = new Interpolation() {
        public float apply (float a) {
            return (1 - MyMath.cos(a * MathUtils.PI)) / 2;
        }
    };

    public static final Interpolation fastSineIn = new Interpolation() {
        public float apply (float a) {
            return 1 - MyMath.cos(a * MathUtils.PI / 2);
        }
    };

    public static final Interpolation fastSineOut = new Interpolation() {
        public float apply (float a) {
            return MyMath.sin(a * MathUtils.PI / 2);
        }
    };








    public static class MyExp extends MyInterpolationInOut {
        float power, min, scale;

        public MyExp() {
        }

        public MyExp(float power) {
            setPower(power);
        }

        @Override
        protected float applyIn(float x01) {
            return ((float)Math.exp(power * (x01 - 1)) - min) * scale;
        }

        @Override
        protected float applyOut(float x01) {
            return (float) (1 - scale*(Math.exp(-power*x01)-min));
        }

        /*@Override
        public float slopeAt(float x01) {
            if (x01 <= 0.5f)
                return slopeAtIn(x01 * 2);
            return slopeAtOut((x01 - 0.5f)*2);
        }*/


        protected float slopeAtIn(float x01) {
            return (float) (power*Math.exp(power*(x01 - 1))*scale);
        }

        protected float slopeAtOut(float x01) {
            return (float) (power*Math.exp(-power*x01)*scale);
        }

        /*protected float xAtWhichSlopeEqualsIn(float slope) {
            return (float) (1 + Math.log(slope*(Math.exp(power) - 1)*Math.exp(-power)/power)/power);
        }

        protected float xAtWhichSlopeEqualsOut(float slope) {
            return (float) (Math.log(power*Math.exp(power)/(slope*(Math.exp(power) - 1)))/power);
        }*/

        protected float inverseFunctionIn(float y01) {
            if (y01 == 0) return 0;
            return (float) (1 + (Math.log((y01/scale)+min))/power);
        }

        protected float inverseFunctionOut(float y01) {
            return (float) (Math.log(min+(1-y01)/scale)/-power);
        }

        public float getPower() {
            return power;
        }

        void setPower(float power) {
            this.power = power;
            min = (float)Math.exp(-power);
            scale = 1 / (1 - min);
        }

        private static float expApproximate(float x) {
            float xp6 = x+6;
            return 0.0024787523f * (
                    1+xp6*(
                            1+xp6*(
                                    0.5f+xp6*(
                                            0.16666667f+xp6*(
                                                    0.041666668f+xp6*(
                                                            0.008333334f+xp6*(
                                                                    0.0013888889f+xp6*(
                                                                            1.984127E-4f+xp6*(
                                                                                    2.4801588E-5f+xp6*(
                                                                                            2.7557319E-6f+xp6*(
                                                                                                    2.755732E-7f+xp6*(
                                                                                                            2.5052108E-8f+xp6*(
                                                                                                                    2.0876756E-9f+xp6*(
                                                                                                                            1.6059044E-10f+xp6*(
                                                                                                                                    1.1470746E-11f
                                                                                                                            )
                                                                                                                    )
                                                                                                            )
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
        }
    }







    public static class MyLinear extends MyInterpolation {

        @Override
        public float slopeAt(float x01) {
            return 1;
        }

        /*@Override
        public float xAtWhichSlopeEquals(float slope) {
            if (slope != 1)
                Gdx.app.log(TAG, "linear function always has a slope of 1 not " + slope);
            return 0;
        }*/

        @Override
        public float inverseFunction(float y01) {
            return y01;
        }

        @Override
        public float apply(float a) {
            return a;
        }
    }




    /*public static class MyCircle extends MyInterpolationInOut {
        @Override
        public float applyIn(float x01) {
            return (float) (-Math.sqrt(-Math.pow(x01, 2) + 1) + 1);
        }

        @Override
        public float applyOut(float x01) {
            return (float) Math.sqrt(-Math.pow(x01 - 1, 2) + 1);
        }

        @Override
        public float slopeAtIn(float x01) {
            return (float) (x01/Math.sqrt(-Math.pow(x01, 2) + 1));
        }

        @Override
        public float slopeAtOut(float x01) {
            return (float) ((-x01 + 1)/Math.sqrt(-Math.pow(x01 - 1, 2) + 1));
        }

        @Override
        public float inverseFunctionIn(float y01) {
            return (float) Math.sqrt(-y01*(y01 - 2));
        }

        @Override
        public float inverseFunctionOut(float y01) {
            return (float) (-Math.sqrt(-(y01 - 1)*(y01 + 1)) + 1);
        }
    }*/


    public static class MyPow extends MyInterpolationInOut {

        final int power;
        final int evenOddNeg1;
        final int evenOdd0;
        final int _evenOddNeg1;
        final int _evenOdd0;

        public MyPow (int power) {
            this.power = power;
            evenOddNeg1 = (int) Math.pow(-1, 1+power);
            evenOdd0 = (power+1)%2;
            _evenOddNeg1 = (int) Math.pow(-1, power);
            _evenOdd0 = (power)%2;
        }

        @Override
        protected float applyIn(float x01) {
            return (float) Math.pow(x01, power);
        }

        @Override
        protected float applyOut(float x01) {
            return (float) (1+ evenOddNeg1 *Math.pow(x01-1, power));
        }

        @Override
        protected float slopeAtIn(float x01) {
            return (float) (power*Math.pow(x01, power-1));
        }

        @Override
        protected float slopeAtOut(float x01) {
            return (float) (evenOddNeg1 *power*Math.pow(x01-1, power-1));
        }

        @Override
        protected float inverseFunctionIn(float y01) {
            return (float) Math.pow(y01, 1f/power);
        }

        @Override
        protected float inverseFunctionOut(float y01) {
            return 2*_evenOdd0+_evenOddNeg1*(2 * evenOdd0 + evenOddNeg1 * (1 + (float) Math.pow((2*_evenOdd0+_evenOddNeg1*y01-1) / evenOddNeg1, 1d/power)));
        }
    }



    public static class MySine extends MyInterpolationInOut {

        final float pi_2 = MathUtils.PI/2f;
        final float _2_pi = 2f/MathUtils.PI;

        @Override
        protected float applyIn(float x01) {
            return 1-MathUtils.cos(pi_2 * x01);
        }

        @Override
        protected float applyOut(float x01) {
            return MathUtils.sin(pi_2 * x01);
        }

        @Override
        protected float slopeAtIn(float x01) {
            return pi_2 * MathUtils.sin(pi_2 * x01);
        }

        @Override
        protected float slopeAtOut(float x01) {
            return pi_2 * MathUtils.cos(pi_2 * x01);
        }

        @Override
        protected float inverseFunctionIn(float y01) {
            return (float) (_2_pi * Math.acos(1-y01));
        }

        @Override
        protected float inverseFunctionOut(float y01) {
            return (float) (_2_pi * Math.asin(y01));
        }
    }




    public static class MyReciprocal extends MyInterpolationInOut {

        float a;
        float b;

        public MyReciprocal() {
            setA(0.5f);
        }

        public MyReciprocal(float a) {
            setA(a);
        }

        @Override
        protected float applyIn(float x01) {
            return x01*(-a + 1)/(-a*x01 + 1);
        }

        @Override
        protected float applyOut(float x01) {
            return x01*(b + 1)/(b*x01 + 1);
        }

        @Override
        protected float slopeAtIn(float x01) {
            return -(a - 1)/(a*a*x01*x01-2*a*x01+1);
        }

        @Override
        protected float slopeAtOut(float x01) {
            return (b + 1)/(b*b*x01*x01+2*b*x01+1);
        }

        @Override
        protected float inverseFunctionIn(float y01) {
            return y01/(a*y01 - a + 1);
        }

        @Override
        protected float inverseFunctionOut(float y01) {
            return y01/(-b*y01 + b + 1);
        }

        public float getA() {
            return a;
        }

        void setA(float a) {
            this.a = a;
            b = -1 + 1f/(-a + 1);
        }
    }






    // Difficulty curves (https://www.desmos.com/calculator/3m1wdcgaqp)

    /**
     * https://www.desmos.com/calculator/3m1wdcgaqp
     */
    public static abstract class StepsCurve extends Interpolation {

        protected int n;

        /**
         *
         * @param n total number of steps or intervals.
         */
        public StepsCurve(int n) {
            this.n = n;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            if (n < 2)
                throw new ValueOutOfRangeException("n must be greater than or equal to 2.");
            this.n = n;
        }


        /*public int getInterval(float a) {
            if (a < 0 | a > 1)
                throw new ValueOutOfRangeException("a must be greater than or equal to 0 and less than or equal to 1.");
            //return MathUtils.clamp(MathUtils.floor(n*a), 0, n-1);
            return (int) MathUtils.clamp(apply(a)/apply(1) * n, 0, n-1);
        }*/

        /**
         * 0 <= interval <= n-1
         * @param a percentage (0 <= a <= 1)
         * @return
         */
        public abstract int getInterval(float a);


        public abstract float getIntervalStartTime(float a);
        public abstract float getIntervalEndTime(float a);


        public float getIntervalStartTime(float overallStartTime, float overallEndTime, float x01) {
            return overallStartTime + (overallEndTime - overallStartTime) * getIntervalStartTime(x01);
        }

        public float getIntervalEndTime(float overallStartTime, float overallEndTime, float x01) {
            return overallStartTime + (overallEndTime - overallStartTime) * getIntervalEndTime(x01);
        }

    }

    /**
     * https://www.desmos.com/calculator/3m1wdcgaqp
     */
    public static class ConstantLinearTimeLinearOutput extends StepsCurve {

        public ConstantLinearTimeLinearOutput(int n) {
            super(n);
        }

        public float apply(float a) {
            return MathUtils.clamp(MathUtils.floor(n*a) / (n-1f), 0, 1);
        }

        @Override
        public int getInterval(float a) {
            return MathUtils.clamp(MathUtils.floor(n*a), 0, n-1);
        }

        @Override
        public float getIntervalStartTime(float a) {
            return apply(a) * (1-1f/n);
        }

        @Override
        public float getIntervalEndTime(float a) {
            return getIntervalStartTime(a) + 1f/n;
        }

    }

    /**
     * https://www.desmos.com/calculator/3m1wdcgaqp
     */
    public static abstract class CustomScaleSteps extends StepsCurve {

        protected MyInterpolation timeScale;
        protected MyInterpolation outputScale;

        protected ConstantLinearTimeLinearOutput constantLinearTimeLinearOutput;

        protected boolean useInverseFunctionTimeScale = false;
        protected boolean useInverseFunctionOutputScale = false;

        /**
         *
         * @param n total number of steps or intervals.
         * @param timeScale
         * @param outputScale
         */
        public CustomScaleSteps(int n, MyInterpolation timeScale, MyInterpolation outputScale) {
            super(n);
            this.timeScale = timeScale;
            this.outputScale = outputScale;
            constantLinearTimeLinearOutput = new ConstantLinearTimeLinearOutput(n);
        }

        @Override
        public int getInterval(float a) {
            return MathUtils.round(constantLinearTimeLinearOutput.apply(0, n-1, timeScale.apply(a)));
        }

        public Interpolation getTimeScale() {
            return timeScale;
        }

        public void setTimeScale(MyInterpolation timeScale) {
            this.timeScale = timeScale;
        }

        public MyInterpolation getOutputScale() {
            return outputScale;
        }

        public void setOutputScale(MyInterpolation outputScale) {
            this.outputScale = outputScale;
        }

        public boolean isUseInverseFunctionTimeScale() {
            return useInverseFunctionTimeScale;
        }

        public void setUseInverseFunctionTimeScale(boolean useInverseFunctionTimeScale) {
            this.useInverseFunctionTimeScale = useInverseFunctionTimeScale;
        }

        public boolean isUseInverseFunctionOutputScale() {
            return useInverseFunctionOutputScale;
        }

        public void setUseInverseFunctionOutputScale(boolean useInverseFunctionOutputScale) {
            this.useInverseFunctionOutputScale = useInverseFunctionOutputScale;
        }
    }

    /**
     * https://www.desmos.com/calculator/3m1wdcgaqp
     */
    public static class ConstantCustomScaleSteps extends CustomScaleSteps {

        public ConstantCustomScaleSteps(int n, MyInterpolation timeScale, MyInterpolation outputScale) {
            super(n, timeScale, outputScale);
        }

        @Override
        public float apply(float a) {
            float x = useInverseFunctionTimeScale ? timeScale.inverseFunction(a) : timeScale.apply(a);
            float steps = constantLinearTimeLinearOutput.apply(x);
            return useInverseFunctionOutputScale ? outputScale.inverseFunction(steps) : outputScale.apply(steps);
        }

        @Override
        public float getIntervalStartTime(float a) {
            float val1 = useInverseFunctionTimeScale ? timeScale.inverseFunction(a) : timeScale.apply(a);
            float val2 = (1-1f/n) * constantLinearTimeLinearOutput.apply(val1);
            return useInverseFunctionTimeScale ? timeScale.apply(val2) : timeScale.inverseFunction(val2);
        }

        @Override
        public float getIntervalEndTime(float a) {
            float val1 = useInverseFunctionTimeScale ? timeScale.inverseFunction(a) : timeScale.apply(a);
            float val2 = (1-1f/n) * constantLinearTimeLinearOutput.apply(val1);
            float val3 = val2 + 1f/n;
            return useInverseFunctionTimeScale ? timeScale.apply(val3) : timeScale.inverseFunction(val3);
        }
    }

    public static class ConstantExponentialInTimeLinearOutput extends ConstantCustomScaleSteps {

        public ConstantExponentialInTimeLinearOutput(int n, float p) {
            super(n, new MyInterpolationIn(new MyExp(p)), myLinear);
        }
    }

    public static class ConstantExponentialOutTimeLinearOutput extends ConstantCustomScaleSteps {

        public ConstantExponentialOutTimeLinearOutput(int n, float p) {
            super(n, new MyInterpolationOut(new MyExp(p)), myLinear);
        }
    }

    public static class ConstantLinearTimeExponentialInOutput extends ConstantCustomScaleSteps {

        public ConstantLinearTimeExponentialInOutput(int n, float p) {
            super(n, myLinear, new MyInterpolationIn(new MyExp(p)));
        }
    }

    public static class ConstantLinearTimeExponentialOutOutput extends ConstantCustomScaleSteps {

        public ConstantLinearTimeExponentialOutOutput(int n, float p) {
            super(n, myLinear, new MyInterpolationOut(new MyExp(p)));
        }
    }

    public static class ConstantInverseExponentialInTimeExponentialInOutput extends ConstantCustomScaleSteps {

        public ConstantInverseExponentialInTimeExponentialInOutput(int n, float p) {
            super(n, new MyInterpolationIn(new MyExp(p)), new MyInterpolationIn(new MyExp(p)));
            setUseInverseFunctionOutputScale(true);
        }
    }

    public static class ConstantInverseExponentialOutTimeExponentialOutOutput extends ConstantCustomScaleSteps {

        public ConstantInverseExponentialOutTimeExponentialOutOutput(int n, float p) {
            super(n, new MyInterpolationOut(new MyExp(p)), new MyInterpolationOut(new MyExp(p)));
            setUseInverseFunctionOutputScale(true);
        }
    }

    /**
     * https://www.desmos.com/calculator/3m1wdcgaqp
     */
    public static class RepeatedCurveCustomScaleSteps extends CustomScaleSteps {

        /**
         * how much the function will drop each interval. 0 <= drop <= 1. 0 = no drop at all. 1 = drop from the max y value to the min y value.
         */
        protected float drop;
        protected MyInterpolation toBeRepeatedCurve;
        /**
         *
         * @param n total number of intervals.
         * @param drop how much the function will drop each interval. 0 <= drop <= 1. 0 = no drop at all. 1 = drop from the max y value to the min y value.
         * @param toBeRepeatedCurve
         * @param timeScale
         * @param outputScale
         */
        public RepeatedCurveCustomScaleSteps(int n, float drop, MyInterpolation toBeRepeatedCurve, MyInterpolation timeScale, MyInterpolation outputScale) {
            super(n, timeScale, outputScale);
            setDrop(drop);
            this.toBeRepeatedCurve = toBeRepeatedCurve;
        }

        public RepeatedCurveCustomScaleSteps(int n, MyInterpolation toBeRepeatedCurve, MyInterpolation timeScale, MyInterpolation outputScale) {
            super(n, timeScale, outputScale);
            setDrop(drop);
            this.toBeRepeatedCurve = toBeRepeatedCurve;
        }

        public RepeatedCurveCustomScaleSteps(int n, MyInterpolation toBeRepeatedCurve) {
            super(n, myLinear, myLinear);
            this.drop = 0;
            this.toBeRepeatedCurve = toBeRepeatedCurve;
        }

        protected float calculateStartC(float a) {
            float val = useInverseFunctionTimeScale ? timeScale.inverseFunction(a) : timeScale.apply(a);
            return (1-1f/n) * constantLinearTimeLinearOutput.apply(val);
        }

        protected float calculateEndC(float startC) {
            return startC + 1f/n;
        }

        protected float calculateCurrentStartX(float startC) {
            return useInverseFunctionTimeScale ? timeScale.apply(startC) : timeScale.inverseFunction(startC);
        }

        protected float calculateCurrentEndX(float endC) {
            return useInverseFunctionTimeScale ? timeScale.apply(endC) : timeScale.inverseFunction(endC);
        }

        protected float calculateLowerY(float startC) {
            return (1- drop) * startC;
        }

        protected float calculateUpperY(float endC) {
            return calculateLowerY(endC)+ drop;
        }

        protected float calculateStartY(float startC) {
            float val = calculateLowerY(startC);
            return useInverseFunctionOutputScale ? outputScale.inverseFunction(val) : outputScale.apply(val);
        }

        protected float calculateEndY(float endC) {
            float val = calculateUpperY(endC);
            return useInverseFunctionOutputScale ? outputScale.inverseFunction(val) : outputScale.apply(val);
        }

        @Override
        public float apply(float a) {
            float startC = calculateStartC(a);
            float endC = calculateEndC(startC);

            float currentStartX = calculateCurrentStartX(startC);
            float currentEndX = calculateCurrentEndX(endC);
            float currentStartY = calculateStartY(startC);
            float currentEndY = calculateEndY(endC);

            return toBeRepeatedCurve.apply(currentStartX, currentEndX, currentStartY, currentEndY, a);
        }

        @Override
        public float getIntervalStartTime(float a) {
            float startC = calculateStartC(a);
            float currentStartX = calculateCurrentStartX(startC);

            return currentStartX;
        }

        @Override
        public float getIntervalEndTime(float a) {
            float startC = calculateStartC(a);
            float endC = calculateEndC(startC);
            float currentEndX = calculateCurrentEndX(endC);

            return currentEndX;
        }

        public float getDrop() {
            return drop;
        }

        public void setDrop(float drop) {
            if (drop < 0 | drop > 1)
                throw new ValueOutOfRangeException("drop must be greater than or equal 0 and less than or equal 1 (0 <= drop <= 1).");
            this.drop = drop;
        }

        public MyInterpolation getToBeRepeatedCurve() {
            return toBeRepeatedCurve;
        }

        public void setToBeRepeatedCurve(MyInterpolation toBeRepeatedCurve) {
            this.toBeRepeatedCurve = toBeRepeatedCurve;
        }
    }

    public static class LinearCurvesLinearTimeLinearOutput extends RepeatedCurveCustomScaleSteps {

        public LinearCurvesLinearTimeLinearOutput(int n, float drop) {
            super(n, drop, myLinear, myLinear, myLinear);
        }
    }

    public static class LinearCurvesExponentialInTimeLinearOutput extends RepeatedCurveCustomScaleSteps {

        public LinearCurvesExponentialInTimeLinearOutput(int n, float drop, float p) {
            super(n, drop, myLinear, new MyInterpolationIn(new MyExp(p)), myLinear);
        }
    }

    public static class LinearCurvesExponentialOutTimeLinearOutput extends RepeatedCurveCustomScaleSteps {

        public LinearCurvesExponentialOutTimeLinearOutput(int n, float drop, float p) {
            super(n, drop, myLinear, new MyInterpolationOut(new MyExp(p)), myLinear);
        }
    }

    public static class LinearCurvesLinearTimeExponentialInOutput extends RepeatedCurveCustomScaleSteps {

        public LinearCurvesLinearTimeExponentialInOutput(int n, float drop, float p) {
            super(n, drop, myLinear, myLinear, new MyInterpolationIn(new MyExp(p)));
        }
    }

    public static class LinearCurvesLinearTimeExponentialOutOutput extends RepeatedCurveCustomScaleSteps {

        public LinearCurvesLinearTimeExponentialOutOutput(int n, float drop, float p) {
            super(n, drop, myLinear, myLinear, new MyInterpolationOut(new MyExp(p)));
        }
    }


    public static class ExponentialInCurvesLinearTimeLinearOutput extends RepeatedCurveCustomScaleSteps {

        public ExponentialInCurvesLinearTimeLinearOutput(int n, float drop, float p) {
            super(n, drop, new MyInterpolationIn(new MyExp(p)), myLinear, myLinear);
        }

    }
}
