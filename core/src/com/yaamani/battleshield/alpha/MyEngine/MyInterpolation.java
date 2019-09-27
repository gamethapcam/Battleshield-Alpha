package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

/**
 * This class needed for MyTween's gradual pause and gradual resume.
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




    public static final Interpolation threeSteps = new Interpolation() {
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
            return (float) (1 + (Math.log(y01/scale)+min)/power);
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

}
