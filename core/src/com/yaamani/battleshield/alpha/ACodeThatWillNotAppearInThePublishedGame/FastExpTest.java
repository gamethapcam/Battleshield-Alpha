package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.math.Interpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

public class FastExpTest {

    public static FastExpTest instance = new FastExpTest();

    private FastExpTest() {

    }

    public final Exp exp10 = new Exp(10);
    public final ExpIn exp10In = new ExpIn(10);
    public final ExpOut exp10Out = new ExpOut(10);


    static public class Exp extends Interpolation {
        final double value, power, min, scale;

        public Exp (double power) {
            this.value = MyMath.exp(1);
            this.power = power;
            min = MyMath.exp( -power);
            scale = 1 / (1 - min);
        }


        @Override
        public float apply(float a) {
            if (a <= 0.5f) return (float) ((MyMath.exp( power * (a * 2 - 1)) - min) * scale / 2);
            return (float) ((2 - (MyMath.exp( -power * (a * 2 - 1)) - min) * scale) / 2);
        }
    }

    static public class ExpIn extends Exp {
        public ExpIn (double power) {
            super(power);
        }

        @Override
        public float apply(float a) {
            return (float) ((MyMath.exp( power * (a - 1)) - min) * scale);
        }
    }

    static public class ExpOut extends Exp {
        public ExpOut (double power) {
            super(power);
        }

        @Override
        public float apply(float a) {
            return (float) (1 - (MyMath.exp( -power * a) - min) * scale);
        }
    }
}
