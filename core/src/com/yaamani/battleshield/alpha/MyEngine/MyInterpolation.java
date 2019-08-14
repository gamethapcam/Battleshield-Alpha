package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public abstract class MyInterpolation extends Interpolation {

    public static final String TAG = MyInterpolation.class.getSimpleName();

    public abstract float slopeAt(float x01);
    public abstract float xAtWhichSlopeEquals(float slope);
    public abstract float inverseFunction(float y);

    public float inverseFunction(float start, float end, float y) {
        return inverseFunction((y - start)/(end - start));
    }

    /*
    /**
     * Recommended to be used only with in or out interpolation only.
     * @param startX Used when you need start the function from a certain x value.
     * @param startY Output starting value (a = 0).
     * @param endY Output ending value (a = 1).
     * @param a The percentage which is always the from 0 to 1.
     * @return
     */
    /*float apply(float startX, float startY, float endY, float a){
        return apply(startY, endY, (1-startX)*a + startX);
    }*/

    public static final MyLinear myLinear = new MyLinear();

    public static final FastExp fastExp10 = new FastExp(10);
    public static final FastExpIn fastExp10In = new FastExpIn(10);
    public static final FastExpOut fastExp10Out = new FastExpOut(10);

    public static final FastExp fastExp5 = new FastExp(5);
    public static final FastExpIn fastExp5In = new FastExpIn(5);
    public static final FastExpOut fastExp5Out = new FastExpOut(5);



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











    public static class FastExp extends MyInterpolation {
        final float power, min, scale;

        public FastExp (float power) {
            this.power = power;
            min = (float)MyMath.exp(-power);
            scale = 1 / (1 - min);
        }

        public float apply (float a) {
            if (a <= 0.5f) return ((float)MyMath.exp(power * (a * 2 - 1)) - min) * scale / 2;
            return (2 - ((float)MyMath.exp(-power * (a * 2 - 1)) - min) * scale) / 2;
        }

        @Override
        public float slopeAt(float x01) {
            if (x01 <= 0.5f)
                return slopeAtIn(x01 * 2);
            return slopeAtOut((x01 - 0.5f)*2);
        }

        @Override
        public float xAtWhichSlopeEquals(float slope) {
            if (slope <= 0.5f)
                return xAtWhichSlopeEqualsIn(slope * 2);
            return xAtWhichSlopeEqualsOut((slope - 0.5f)*2);
        }

        @Override
        public float inverseFunction(float y) {
            if (y <= 0.5f)
                return inverseFunctionIn(y * 2);
            return inverseFunctionOut((y - 0.5f)*2);
        }

        protected float slopeAtIn(float x01) {
            return (float) (power*Math.exp(power*(x01 - 1))/(1 - Math.exp(-power)));
        }

        protected float slopeAtOut(float x01) {
            return (float) (power*Math.exp(-power*x01)/(1 - Math.exp(-power)));
        }

        protected float xAtWhichSlopeEqualsIn(float slope) {
            return (float) (1 + Math.log(slope*(Math.exp(power) - 1)*Math.exp(-power)/power)/power);
        }

        protected float xAtWhichSlopeEqualsOut(float slope) {
            return (float) (Math.log(power*Math.exp(power)/(slope*(Math.exp(power) - 1)))/power);
        }

        protected float inverseFunctionIn(float y) {
            return (float) (1 + Math.log((y*(Math.exp(power) - 1) + 1)*Math.exp(-power))/power);
        }

        protected float inverseFunctionOut(float y) {
            return (float) (Math.log(Math.exp(power)/(-y*Math.exp(power) + y + Math.exp(power)))/power);
        }
    }

    public static class FastExpIn extends FastExp {
        public FastExpIn (float power) {
            super(power);
        }

        public float apply (float a) {
            return ((float)MyMath.exp(power * (a - 1)) - min) * scale;
        }

        @Override
        public float slopeAt(float x01) {
            return slopeAtIn(x01);
        }

        @Override
        public float xAtWhichSlopeEquals(float slope) {
            return xAtWhichSlopeEqualsIn(slope);
        }

        @Override
        public float inverseFunction(float y) {
            return inverseFunctionIn(y);
        }
    }

    public static class FastExpOut extends FastExp {
        public FastExpOut (float power) {
            super(power);
        }

        public float apply (float a) {
            return 1 - ((float)MyMath.exp(-power * a) - min) * scale;
        }

        @Override
        public float slopeAt(float x01) {
            return slopeAtOut(x01);
        }

        @Override
        public float xAtWhichSlopeEquals(float slope) {
            return xAtWhichSlopeEqualsOut(slope);
        }

        @Override
        public float inverseFunction(float y) {
            return inverseFunctionOut(y);
        }
    }






    public static class MyLinear extends MyInterpolation {

        @Override
        public float slopeAt(float x01) {
            return 1;
        }

        @Override
        public float xAtWhichSlopeEquals(float slope) {
            if (slope != 1)
                Gdx.app.log(TAG, "linear function always has a slope of 1 not " + slope);
            return 0;
        }

        @Override
        public float inverseFunction(float y) {
            return y;
        }

        @Override
        public float apply(float a) {
            return a;
        }
    }
}
