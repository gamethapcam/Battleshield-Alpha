package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.MyEngine.MyFrameBuffer;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.PostProcessingEffect;

import java.util.Arrays;

public class GlassCrackPostProcessingEffect extends PostProcessingEffect {

    private static final String TAG = GlassCrackPostProcessingEffect.class.getSimpleName();

    /*private MyFrameBuffer hBlurFrameBuffer;
    private MyFrameBuffer vBlurFrameBuffer;*/

    //private Texture refractionCrackMap; // For testing

    private MyFrameBuffer refractionCrackMapFrameBuffer0; //Double buffer
    private MyFrameBuffer refractionCrackMapFrameBuffer1; //Double buffer
    private boolean currentBufferIs0 = true;
    private MyFrameBuffer currentFBO;


    private ShaderProgram refractionShader;
    private ShaderProgram crackAdderAnimatorShader;

    private CrackGenerator crackGenerator;

    private Texture badlogic;

    private float currentCrackGenerationFrame = -1;

    public GlassCrackPostProcessingEffect() {
        //refractionCrackMap = new Texture(Gdx.files.internal("refractionMap.png"));
        initializeRefractionShader();
        initializeCrackAdderAnimatorShader();

        initializeRefractionCrackMapFrameBuffer();

        crackGenerator = new CrackGenerator();

        badlogic = new Texture(Gdx.files.internal("badlogic.jpg"));
    }


    @Override
    public void draw(Batch batch, TextureRegion inputTextureRegion, float x, float y, float width, float height) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {

            refractionCrackMapFrameBuffer0.begin();

//            Gdx.gl.glClearColor(0, 0, 0, 0);
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//            refractionCrackMapFrameBuffer0.end();
//
//
//
//            refractionCrackMapFrameBuffer1.begin();
//
//            Gdx.gl.glClearColor(0, 0, 0, 0);
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//            refractionCrackMapFrameBuffer1.end();

            generateCrack();
        }


        if (currentCrackGenerationFrame >= 0) {

            currentFBO = currentBufferIs0 ? refractionCrackMapFrameBuffer0 : refractionCrackMapFrameBuffer1;

            currentFBO.begin();

            batch.disableBlending();

            batch.begin();

            //Texture t = new Texture(refractionCrackMapFrameBuffer0.getColorBufferTexture().getTextureData());
            /*Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
            Texture t = new Texture(pixmap);*/

            batch.setShader(crackAdderAnimatorShader);

            crackAdderAnimatorShader.setUniformi("u_currentFrame", (int) currentCrackGenerationFrame);
            crackAdderAnimatorShader.setUniformi("u_flip", currentBufferIs0 ? 1 : 0);
            crackAdderAnimatorShader.setUniform2fv("u_lPoints", crackGenerator.lPoints, 0, crackGenerator.lPoints.length);
            crackAdderAnimatorShader.setUniformf("u_smallestAreaSide", crackGenerator.smallestAreaSide);
            crackAdderAnimatorShader.setUniformf("u_mainA", crackGenerator.mainA);
            crackAdderAnimatorShader.setUniformf("u_mainB", crackGenerator.mainB);
            crackAdderAnimatorShader.setUniformf("u_mainC", crackGenerator.mainC);
            crackAdderAnimatorShader.setUniform1fv("u_A", crackGenerator.currentA, 0, crackGenerator.currentA.length);
            crackAdderAnimatorShader.setUniform1fv("u_B", crackGenerator.currentB, 0, crackGenerator.currentB.length);
            crackAdderAnimatorShader.setUniform1fv("u_C", crackGenerator.currentC, 0, crackGenerator.currentC.length);
            crackAdderAnimatorShader.setUniformf("u_c", crackGenerator.c);
            crackAdderAnimatorShader.setUniformf("u_s", crackGenerator.s);


            crackAdderAnimatorShader.setUniform2fv("u_cPoints", crackGenerator.cPoints, 0, crackGenerator.cPoints.length);
            crackAdderAnimatorShader.setUniform1fv("u_a2", crackGenerator.current_a2, 0, crackGenerator.current_a2.length);
            crackAdderAnimatorShader.setUniform1fv("u_a1", crackGenerator.current_a1, 0, crackGenerator.current_a1.length);
            crackAdderAnimatorShader.setUniform1fv("u_a0", crackGenerator.current_a0, 0, crackGenerator.current_a0.length);

            crackAdderAnimatorShader.setUniform1fv("u_C0", crackGenerator.current_C0, 0, crackGenerator.current_C0.length);
            crackAdderAnimatorShader.setUniform1fv("u_C1", crackGenerator.current_C1, 0, crackGenerator.current_C1.length);
            crackAdderAnimatorShader.setUniform1fv("u_C2", crackGenerator.current_C2, 0, crackGenerator.current_C2.length);
            crackAdderAnimatorShader.setUniform1fv("u_C3", crackGenerator.current_C3, 0, crackGenerator.current_C3.length);
            crackAdderAnimatorShader.setUniform1fv("u_C4", crackGenerator.current_C4, 0, crackGenerator.current_C4.length);



            Texture colorBufferTexture = currentBufferIs0 ? refractionCrackMapFrameBuffer1.getColorBufferTexture() : refractionCrackMapFrameBuffer0.getColorBufferTexture();
            //colorBufferTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            batch.draw(colorBufferTexture, x, y, width, height);

            batch.end();

            currentFBO.end();

            currentCrackGenerationFrame--;
            //currentCrackGenerationFrame -= 0.5f;
            //currentCrackGenerationFrame -= 0.05f;

            currentBufferIs0 = !currentBufferIs0;
        }



        batch.enableBlending();

        /*batch.begin();

        batch.setShader(null);

        batch.draw(currentFBO.getColorBufferTexture(), x, y, width, height);*/



        batch.begin();

        batch.setShader(refractionShader);

        Texture colorBufferTexture = currentFBO.getColorBufferTexture();
        //colorBufferTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        colorBufferTexture.bind(1);
        //refractionCrackMap.bind(1);
        refractionShader.setUniformi("u_refractionCrackMap", 1);
        Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);

        refractionShader.setUniformi("u_flip", currentBufferIs0 ? 1 : 0);


        batch.draw(inputTextureRegion, x, y, width, height);

        batch.setShader(null);


        //batch.end();
    }

    public void generateCrack() {
        crackGenerator.generateCrack();
        currentCrackGenerationFrame = CrackGenerator.NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS-2;
    }

    public void clearRefractionBuffers() {
        refractionCrackMapFrameBuffer0.begin();

        //Gdx.gl.glClearColor(.5f, .5f, 0, 0);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        refractionCrackMapFrameBuffer0.end();



        refractionCrackMapFrameBuffer1.begin();

        //Gdx.gl.glClearColor(.5f, .5f, 0, 0);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        refractionCrackMapFrameBuffer1.end();
    }

    @Override
    public void dispose() {
        //refractionCrackMap.dispose();
        refractionCrackMapFrameBuffer0.dispose();
        refractionShader.dispose();
        crackAdderAnimatorShader.dispose();
        badlogic.dispose();
    }

    private void initializeRefractionShader() {
        String fragmentShader = Gdx.files.internal("Refraction.fs.glsl").readString();

        refractionShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!refractionShader.isCompiled())
            Gdx.app.error("refractionShader Compile Error : ", refractionShader.getLog());
    }

    private void initializeCrackAdderAnimatorShader() {
        String fragmentShader = Gdx.files.internal("crackAdderAnimator.fs.glsl").readString();

        crackAdderAnimatorShader = new ShaderProgram(AdvancedApplicationAdapter.DEFAULT_VERTEX_SHADER, fragmentShader);

        if (!crackAdderAnimatorShader.isCompiled()) {
            Gdx.app.error("crackAdderAnimatorShader Compile Error : ", crackAdderAnimatorShader.getLog());
        }
    }

    private void initializeRefractionCrackMapFrameBuffer() {

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Pixmap.Format f = Pixmap.Format.RGBA8888;

        refractionCrackMapFrameBuffer0 = new MyFrameBuffer(f, w, h, false);
        refractionCrackMapFrameBuffer1 = new MyFrameBuffer(f, w, h, false);


        clearRefractionBuffers();


        currentFBO = refractionCrackMapFrameBuffer0;
    }















    private static class CrackGenerator {
        public static final int NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS = 6;
        public static final float LINE_POINT_HALF_WIDTH_DIVIDER = 2.65f;
        public static final int NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS = 4;
        public static final float CURVE_POINT_HALF_WIDTH_DIVIDER = 1.65f;

        enum FinalPointPositioning {LEFT, MIDDLE, RIGHT}
        static final FinalPointPositioning[] FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_LEFT = {
                FinalPointPositioning.LEFT,
                FinalPointPositioning.LEFT,
                FinalPointPositioning.LEFT,
                FinalPointPositioning.LEFT,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.RIGHT
        };

        static final FinalPointPositioning[] FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_MIDDLE = {
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.LEFT,
                FinalPointPositioning.LEFT
        };

        static final FinalPointPositioning[] FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_RIGHT = {
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.RIGHT,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.MIDDLE,
                FinalPointPositioning.LEFT
        };

        float xi, yi, xf, yf;
        float[] lPoints;
        float mainA, mainB, mainC;
        int smallestAreaSide;
        float[] currentA;
        float[] currentB;
        float[] currentC;
        float c, s; // cos & sin


        float[] cPoints;
        float[] current_a2;
        float[] current_a1;
        float[] current_a0;
        float[] current_C0;
        float[] current_C1;
        float[] current_C2;
        float[] current_C3;
        float[] current_C4;

        private float c_initial; // TODO:

        public CrackGenerator() {

            lPoints = new float[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS*2];

            currentA = new float[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS -1];
            currentB = new float[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS -1];
            currentC = new float[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS -1];

            cPoints = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS*2];

            current_a2 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_a1 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_a0 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];

            current_C0 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_C1 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_C2 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_C3 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];
            current_C4 = new float[NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -2];

        }

        static float randomU() {
            float r = MathUtils.random();
            return MyInterpolation.myPow5.apply(r);
        }

        static MyInterpolation.MyExp myExpM = new MyInterpolation.MyExp(2);
        static MyInterpolation.MyInterpolationIn myExpInM = new MyInterpolation.MyInterpolationIn(myExpM);
        static MyInterpolation.MyInterpolationOut myExpOutM = new MyInterpolation.MyInterpolationOut(myExpM);
        static float randomM() {
            float r = MathUtils.random();

            if (r < 0.25f)
                return myExpInM.apply(4*r)/4;
            else if (r > 0.75f)
                return 0.75f+ myExpInM.apply(4*(r-0.75f))/4;
            return 0.25f+ myExpOutM.apply(2*r-0.5f)/2;
        }

        static MyInterpolation.MyExp myExpWitchHat = new MyInterpolation.MyExp(5);
        static MyInterpolation.MyInterpolationIn myExpInWitchHat = new MyInterpolation.MyInterpolationIn(myExpWitchHat);
        static MyInterpolation.MyInterpolationOut myExpOutWitchHat = new MyInterpolation.MyInterpolationOut(myExpWitchHat);
        static float randomWitchHat() {
            float r = MathUtils.random();
            if (r <= 0.5f)
                return myExpOutWitchHat.apply(2*r)/2;
            return myExpInWitchHat.apply(2*r-1)/2;
        }

        private void mainLinePoints() {
            boolean bottom_i = MathUtils.randomBoolean(); // true = bottom, false = top
            xi = randomM();
            yi = bottom_i ? 0 : 1;

            FinalPointPositioning finalPointPositioning;
            if (xi < 0.4) { // left.
                finalPointPositioning = MyMath.pickRandomElement(FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_LEFT);
            } else if (xi < 0.6f) { // middle.
                finalPointPositioning = MyMath.pickRandomElement(FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_MIDDLE);
            } else { // right.
                finalPointPositioning = MyMath.pickRandomElement(FINAL_POINT_POSITIONING_PROBABILITY_ARRAY_WHEN_XI_RIGHT);
            }


            switch (finalPointPositioning) {
                case LEFT:
                    xf = 0;
                    yf = MathUtils.random();
                    break;
                case MIDDLE:
                    yf = bottom_i ? 1 : 0;
                    xf = MathUtils.random();
                    break;
                default: // RIGHT
                    xf = 1;
                    yf = MathUtils.random();
                    break;
            }


            //Always make sure that the initial point (xi, yi) is the one @ the bottom. This ensures a positive distance for a point on the right hand side of the line and a negative distance otherwise.
            if (!bottom_i) {
                float tempX = xi;
                float tempY = yi;

                xi = xf;
                yi = yf;

                xf = tempX;
                yf = tempY;
            }

            smallestAreaSide = smallestArea(xi, yi, xf, yf);

            lPoints[0] = xi;
            lPoints[1] = yi;


            lPoints[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS*2-1] = yf;
            lPoints[NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS*2-2] = xf;

            mainA = yi - yf;
            mainB = xf - xi;
            mainC = xi*yf - xf*yi;
            float sqrt_a2_b2 = (float) Math.sqrt(mainA*mainA + mainB*mainB);
            mainA /= sqrt_a2_b2;
            mainB /= sqrt_a2_b2;
            mainC /= sqrt_a2_b2;

            float hypotenuse = Vector2.dst(xi, yi, xf, yf);
            c = /*Math.abs*/(xf-xi) / hypotenuse;
            s = /*Math.abs*/(yf-yi) / hypotenuse;
        }

        private void intermediateLinePoints() {

            float pointWindow_halfWidth_L = (xf-xi)/ NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS /LINE_POINT_HALF_WIDTH_DIVIDER;
            float pointWindow_halfHeight_L = (yf-yi)/ NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS /LINE_POINT_HALF_WIDTH_DIVIDER;
            for (int i = 1; i < NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS -1; i++) {
                float factor = (float) i/ NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS;
                float xc = MathUtils.lerp(xi, xf, factor);
                float yc = MathUtils.lerp(yi, yf, factor);

                xc += MyInterpolation.myLinear.apply(-pointWindow_halfWidth_L, pointWindow_halfWidth_L, randomWitchHat());
                yc += MyInterpolation.myLinear.apply(-pointWindow_halfHeight_L, pointWindow_halfHeight_L, randomWitchHat());


                lPoints[i*2] = xc;
                lPoints[i*2 + 1] = yc;
            }

            for (int i = 1; i < NUMBER_OF_RANDOMLY_GENERATED_LINE_POINTS; i++) {
                float A, B, C, sqrt_a2_b2;

                float currentX, currentY;
                float previousX, previousY;

                currentX = lPoints[2*i];
                currentY = lPoints[2*i + 1];
                previousX = lPoints[2*i - 2];
                previousY = lPoints[2*i - 1];

                A = previousY - currentY;
                B = currentX - previousX;
                C = previousX*currentY - currentX*previousY;
                sqrt_a2_b2 = (float) Math.sqrt(A*A + B*B);
                A /= sqrt_a2_b2;
                B /= sqrt_a2_b2;
                C /= sqrt_a2_b2;

                currentA[i-1] = A;
                currentB[i-1] = B;
                currentC[i-1] = C;
            }
        }

        /**
         * <p>The line created by (xi, yi) & (xf, yf) divides the screen into 2 areas. A smaller one and a bigger one. If the smallest area is on the right hand side of the line, this function returns 1. And if the smallest area on the left hand side, the function will return -1.</p>
         *
         * <p>This function assumes yf > yi.</p>
         *
         *
         * @param xi
         * @param yi
         * @param xf
         * @param yf
         * @return
         */
        private int smallestArea(float xi, float yi, float xf, float yf) {
            if (yi == 0 & yf == 1) { //2 trapeziums
                float area = (xi+xf) / 2f;
                if (area < 0.5f)
                    return -1;
                return 1;
            }

            if (yi == 0) {
                if (xf == 0)
                    return -1;
                return 1;
            }

            //if (yf == 1) {
                if (xi == 0)
                    return -1;
                return 1;
            //}
        }

        private void curvePoints() {

            float pointWindow_halfWidth_C = (xf-xi)/ NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS /CURVE_POINT_HALF_WIDTH_DIVIDER;
            float pointWindow_halfHeight_C = (yf-yi)/ NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS /CURVE_POINT_HALF_WIDTH_DIVIDER;
            cPoints[0] = xi + MyInterpolation.myLinear.apply(-pointWindow_halfWidth_C, pointWindow_halfWidth_C, randomWitchHat());
            cPoints[1] = yi;

            cPoints[(NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS)*2 - 2] = (xf==0 | xf==1) ? xf : xf+MyInterpolation.myLinear.apply(-pointWindow_halfWidth_C, pointWindow_halfWidth_C, randomWitchHat());
            cPoints[(NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS)*2 - 1] = (yf==0 | yf==1) ? yf : yf+MyInterpolation.myLinear.apply(-pointWindow_halfHeight_C, pointWindow_halfHeight_C, randomWitchHat());


		/*pointWindow_halfWidth_C = (xf-xi)/ NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS /1.65f;
		pointWindow_halfHeight_C = (yf-yi)/ NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS /1.65f;*/
            for (int i = 1; i < NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS -1; i++) {
                float factor = (float) i/ NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS;
                float xc = MathUtils.lerp(xi, xf, factor);
                float yc = MathUtils.lerp(yi, yf, factor);

                xc += MyInterpolation.myLinear.apply(-pointWindow_halfWidth_C, pointWindow_halfWidth_C, randomWitchHat());
                yc += MyInterpolation.myLinear.apply(-pointWindow_halfHeight_C, pointWindow_halfHeight_C, randomWitchHat());


                cPoints[i*2] = xc;
                cPoints[i*2 + 1] = yc;
            }


            for (int i = 0; i < NUMBER_OF_RANDOMLY_GENERATED_CURVE_POINTS-2; i++) {
                float x0 = cPoints[(i*2)      ];
                float y0 = cPoints[(i*2) + 1  ];
                float x1 = cPoints[(i+1)*2    ];
                float y1 = cPoints[(i+1)*2 + 1];
                float x2 = cPoints[(i+2)*2    ];
                float y2 = cPoints[(i+2)*2 + 1];

                MyMath.threePointsPolynomialInterpolation(x0, y0, x1, y1, x2, y2);
                float a2 = MyMath.threePointsPolynomialInterpolationResult.a2;
                float a1 = MyMath.threePointsPolynomialInterpolationResult.a1;
                float a0 = MyMath.threePointsPolynomialInterpolationResult.a0;
                current_a2[i] = a2;
                current_a1[i] = a1;
                current_a0[i] = a0;

                float C0, C1, C2, C3, C4;
                C0 = 2*a2*s;
                C1 = -1/(C0*s);
                C2 = a1*s;
                C3 = C2 + c;
                C4 = a0*s;

                current_C0[i] = C0;
                current_C1[i] = C1;
                current_C2[i] = C2;
                current_C3[i] = C3;
                current_C4[i] = C4;
            }

            c_initial = MathUtils.random() * 1000.f;
        }

        private void printAll() {
            Gdx.app.log(TAG, "\n");
            /*Gdx.app.log(TAG, "currentX_L = " + Arrays.toString(currentX_L));
            Gdx.app.log(TAG, "currentY_L = " + Arrays.toString(currentY_L));*/
            Gdx.app.log(TAG, "lPoints = " + Arrays.toString(lPoints));
            Gdx.app.log(TAG, "currentA = " + Arrays.toString(currentA));
            Gdx.app.log(TAG, "currentB = " + Arrays.toString(currentB));
            Gdx.app.log(TAG, "currentC = " + Arrays.toString(currentC));
            Gdx.app.log(TAG, "c = " + c + ", s = " + s);
            Gdx.app.log(TAG, "smallestArea = " + smallestAreaSide);
            /*Gdx.app.log(TAG, "currentX_C = " + Arrays.toString(currentX_C));
            Gdx.app.log(TAG, "currentY_C = " + Arrays.toString(currentY_C));*/
            Gdx.app.log(TAG, "current_a2 = " + Arrays.toString(current_a2));
            Gdx.app.log(TAG, "current_a1 = " + Arrays.toString(current_a1));
            Gdx.app.log(TAG, "current_a0 = " + Arrays.toString(current_a0));
        }

        void generateCrack() {
            mainLinePoints();
            intermediateLinePoints();
            curvePoints();

            printAll();
        }


    }
}
