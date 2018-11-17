package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public abstract class MyInterpolation extends Interpolation {

    public static final FastExp fastExp10 = new FastExp(10);
    public static final FastExpIn fastExp10In = new FastExpIn(10);
    public static final FastExpOut fastExp10Out = new FastExpOut(10);




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











    public static class FastExp extends Interpolation {
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
    }

    public static class FastExpIn extends FastExp {
        public FastExpIn (float power) {
            super(power);
        }

        public float apply (float a) {
            return ((float)MyMath.exp(power * (a - 1)) - min) * scale;
        }
    }

    public static class FastExpOut extends FastExp {
        public FastExpOut (float power) {
            super(power);
        }

        public float apply (float a) {
            return 1 - ((float)MyMath.exp(-power * a) - min) * scale;
        }
    }
}
