package com.yaamani.battleshield.alpha.Game.ImprovingControlls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

public class DataMonitoring implements Disposable, Resizable {

    private static final String TAG = DataMonitoring.class.getSimpleName();

    public static final int PLOTTED_POINTS_PER_FRAME = 250;
    public static final float MIN_MAX = 15;
    public static final String LEFT_TAG = "Left";
    public static final String RIGHT_TAG = "\t\t\t\t\t\t\t\tRight";

    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;

    private NetworkAndStorageManager nsm;

    private LineGraph leftStickGraph;
    private LineGraph rightStickGraph;
    private ProgressBar progressBar;

    private SimpleText bulletsPerAttackText;

    private float speed = 1;
    private float currentFrame;


    private boolean paused;

    public DataMonitoring(Viewport viewport, NetworkAndStorageManager networkAndStorageManager, MyBitmapFont myBitmapFont) {
        this.viewport = viewport;
        shapeRenderer = new ShapeRenderer();
        //shapeRenderer.setAutoShapeType(true);
        spriteBatch = new SpriteBatch();

        this.nsm = networkAndStorageManager;

        initializeLeftStickGraph();
        initializeRightStickGraph();
        initializeProgressBar();

        initializeBulletsPerAttackText(myBitmapFont);
    }

    public void render() {


        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);





        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            paused = !paused;


        progressBar.render(shapeRenderer);
        if (progressBar.sliding) {
            paused = true;
            currentFrame = MathUtils.floor(progressBar.percentage*(nsm.getAllLeftStickAngles().size-1));
        } else {
            progressBar.percentage = currentFrame/(nsm.getAllLeftStickAngles().size-1);
        }

        if (!paused)
            currentFrame = MathUtils.clamp(currentFrame+speed, 0, nsm.getAllLeftStickAngles().size-1);
        else {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) & Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
                currentFrame = MathUtils.clamp(currentFrame-3f, 0, nsm.getAllLeftStickAngles().size-1);
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) & Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
                currentFrame = MathUtils.clamp(currentFrame+3f, 0, nsm.getAllLeftStickAngles().size-1);
            else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                currentFrame = MathUtils.clamp(currentFrame-0.5f, 0, nsm.getAllLeftStickAngles().size-1);
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                currentFrame = MathUtils.clamp(currentFrame+0.5f, 0, nsm.getAllLeftStickAngles().size-1);
        }

        int currentIndex = MathUtils.floor(currentFrame);
        nsm.setCurrentLeftStickAngle(nsm.getAllLeftStickAngles().get(currentIndex));
        nsm.setCurrentRightStickAngle(nsm.getAllRightStickAngles().get(currentIndex));
        nsm.setCurrentActiveShieldsNum(nsm.getAllActiveShieldsNums().get(currentIndex));








        int start = MathUtils.clamp(currentIndex-PLOTTED_POINTS_PER_FRAME/2, 0, nsm.getAllLeftStickVelocities().size-PLOTTED_POINTS_PER_FRAME-1);
        int len = MathUtils.clamp(PLOTTED_POINTS_PER_FRAME, 0, nsm.getAllLeftStickVelocities().size);

        leftStickGraph.render(shapeRenderer, nsm.getAllLeftStickVelocities(), start, len, currentIndex, LEFT_TAG);
        rightStickGraph.render(shapeRenderer, nsm.getAllRightStickVelocities(), start, len, currentIndex, RIGHT_TAG);


        shapeRenderer.end();




        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();


        bulletsPerAttackText.setCharSequence("" + nsm.getAllBulletsPerAttacks().get(currentIndex), true);
        bulletsPerAttackText.draw(spriteBatch, 1);


        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        if (leftStickGraph != null)
            leftStickGraph.x = worldWidth*.1f;

        if (rightStickGraph != null)
            rightStickGraph.x = worldWidth*(1-.1f) - rightStickGraph.width;

        if (progressBar != null)
            progressBar.width = worldWidth - 2*progressBar.x;

        if (bulletsPerAttackText != null)
            bulletsPerAttackText.setX(worldWidth/2f - bulletsPerAttackText.getWidth()/2f);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private void initializeLeftStickGraph() {
        leftStickGraph = new LineGraph();
        leftStickGraph.width = 40;
        leftStickGraph.height = leftStickGraph.width;
        leftStickGraph.y = Constants.WORLD_SIZE/2f - leftStickGraph.height/2f;
        leftStickGraph.lineWidth = 0.35f;
        leftStickGraph.min = -MIN_MAX;
        leftStickGraph.max = MIN_MAX;
        Gdx.app.log(DataMonitoring.TAG, LEFT_TAG + "(min, max) = (" + leftStickGraph.min + ", " + leftStickGraph.max + ").");
    }

    private void initializeRightStickGraph() {
        rightStickGraph = new LineGraph();
        rightStickGraph.width = 40;
        rightStickGraph.height = rightStickGraph.width;
        rightStickGraph.y = Constants.WORLD_SIZE/2f - rightStickGraph.height/2f;
        rightStickGraph.lineWidth = 0.35f;
        rightStickGraph.min = -MIN_MAX;
        rightStickGraph.max = MIN_MAX;
        Gdx.app.log(DataMonitoring.TAG, RIGHT_TAG + "(min, max) = (" + rightStickGraph.min + ", " + rightStickGraph.max + ").");

    }

    private void initializeProgressBar() {
        progressBar = new ProgressBar();
        progressBar.x = 7;
        progressBar.y = 4;
        progressBar.lineWidth = 0.35f;
        progressBar.sliderRadius = 2f;
    }

    private void initializeBulletsPerAttackText(MyBitmapFont myBitmapFont) {
        bulletsPerAttackText = new SimpleText(myBitmapFont, "6");
        bulletsPerAttackText.setHeight(10);
        bulletsPerAttackText.setY(15);
    }







    public static class LineGraph {

        public float x, y, width, height;
        public float min, max;

        public float lineWidth;

        public void render(ShapeRenderer shapeRenderer, Array<Float> points, int start, int len, int pointOfInterestIndex, String tag) {

            shapeRenderer.rectLine(x, y, x, y+height, lineWidth*2);
            shapeRenderer.rectLine(x, y, x+width, y, lineWidth*2);
            shapeRenderer.rectLine(x, y+(0f-min)/(max-min)*height, x+width, y+(0f-min)/(max-min)*height, 0.1f);


            for (int i = start; i < start+len; i++) {
                Float point = points.get(i);
                if (point == null) continue;

                boolean minMaxChanged = false;

                if (point > max) {
                    max = point;
                    minMaxChanged = true;
                }
                else if (point < min) {
                    min = point;
                    minMaxChanged = true;
                }

                if (minMaxChanged)
                    Gdx.app.log(DataMonitoring.TAG, tag + "(min, max) = (" + min + ", " + max + ").");
            }

            for (int i = start+1; i < start+len; i++) {
                Float point0 = points.get(i-1);
                Float point1 = points.get(i);

                if (point0 == null | point1 == null)
                    continue;


                float horizontalDistanceBetween2Points = width/(len-1);

                float x1 = x+((i-start)-1)*horizontalDistanceBetween2Points;
                float y1 = y+(point0-min)/(max-min)*height;
                float x2 = x+(i-start)*horizontalDistanceBetween2Points;
                float y2 = y+(point1-min)/(max-min)*height;

                shapeRenderer.rectLine(x1, y1, x2, y2, lineWidth);

                if (i == pointOfInterestIndex) {
                    shapeRenderer.setColor(Color.MAGENTA);
                    shapeRenderer.rectLine(x2, y, x2, y2, 0.1f);
                    shapeRenderer.circle(x2, y2, 1f);
                    shapeRenderer.setColor(Color.WHITE);
                }
            }

        }

    }


    public static class ProgressBar {
        public float percentage;

        public float x, y, width;
        public float lineWidth;
        public float sliderRadius;

        public boolean sliding;

        public void render(ShapeRenderer shapeRenderer) {

            mouseInput();

            shapeRenderer.rectLine(x, y, x+width, y, lineWidth);
            shapeRenderer.circle(x+percentage*width, y, sliderRadius, 20);
        }

        private void mouseInput() {
            float windowHeight = Gdx.graphics.getHeight();
            float touchX = (float) Gdx.input.getX()/windowHeight * Constants.WORLD_SIZE;
            float touchY = (float) (windowHeight - Gdx.input.getY())/windowHeight * Constants.WORLD_SIZE;

            if (Gdx.input.justTouched()) {

                if (touchX >= x &
                        touchX <= x+width &
                        touchY >= y-sliderRadius &
                        touchY <= y+sliderRadius)

                    sliding = true;

            } else if (!Gdx.input.isTouched())
                sliding = false;


            if (sliding)
                percentage = MathUtils.clamp((touchX-x)/width, 0, 1);
        }
    }
}
