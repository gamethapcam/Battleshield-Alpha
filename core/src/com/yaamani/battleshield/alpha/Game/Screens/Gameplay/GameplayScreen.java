package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyTween;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyMath.millisToSeconds;

public class GameplayScreen extends AdvancedScreen {

    public static final String TAG = GameplayScreen.class.getSimpleName();

    private Image turret;

    private GameplayType gameplayType;

    private Array<Timer> pauseWhenPausingFinishWhenLosing;

    private BulletsAndShieldContainer[] bulletsAndShieldContainers;
    private ShieldsAndContainersHandler shieldsAndContainersHandler;

    private BulletsHandler bulletsHandler;

    private HealthBar healthBar;
    private HealthHandler healthHandler;

    public enum State {PLAYING, PAUSED, LOST}
    private State state;
    private boolean inStarBulletAnimation = false;

    private float timePlayedThisTurnSoFar;
    private Score score;
    private MyTween timePlayedThisTurnSoFarTweenStarBullet_FirstStage;
    private Tween timePlayedThisTurnSoFarTweenStarBullet_ThirdStage;

    private Image whiteTextureHidesEveryThingSecondStageStarBullet;
    private Tween whiteTextureHidesEveryThingSecondStageTweenStarBullet;

    private PauseStuff pauseStuff;

    private SpeedMultiplierStuff speedMultiplierStuff;

    private Controller controllerLeft;
    private Controller controllerRight;

    private GameOverLayer gameOverLayer;

    private StarsContainer starsContainer;

    private MyBitmapFont myBitmapFont;
    //private BitmapFont font;

    private int rotation;

    public GameplayScreen(AdvancedStage game, MyBitmapFont myBitmapFont, final StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        //this.font = font;
        this.myBitmapFont = myBitmapFont;
        this.starsContainer = starsContainer;

        pauseWhenPausingFinishWhenLosing = new Array<>(false, PAUSE_WHEN_PAUSING_FINISH_WHEN_LOSING_INITIAL_CAPACITY, Timer.class);

        initializeHandlers(game, starsContainer.getRadialTween());

        initializeTurret();
        initializeBulletsAndShieldArray();
        initializeTimePlayedThisTurnSoFarTweenStarBullet_FirstStage();
        initializeTimePlayedThisTurnSoFarTweenStarBullet_ThirdStage();

        //initializeBullets(starsContainer.getRadialTween());

        //bulletsHandler.newWave();

        healthBar = new HealthBar(this);

        speedMultiplierStuff = new SpeedMultiplierStuff(this);

        initializeWhiteTextureHidesEveryThingSecondStageStarBullet();
        initializeWhiteTextureHidesEveryThingSecondStageTweenStarBullet();

        score = new Score(this, myBitmapFont);

        //initializeControllers();

        //---------
        state = State.PLAYING;

        gameOverLayer = new GameOverLayer(this, myBitmapFont);

        /*Viewport viewport = getStage().getViewport();
        resize(viewport.getScreenWidth(),
                viewport.getScreenHeight(),
                viewport.getWorldWidth(),
                viewport.getWorldHeight());*/
    }

    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------

    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);

        pauseStuff.update(delta);

        if (getState() == GameplayScreen.State.PLAYING) {
            speedMultiplierStuff.update(delta);
            if (!inStarBulletAnimation) timePlayedThisTurnSoFar += delta;
        }

        timePlayedThisTurnSoFarTweenStarBullet_FirstStage.update(delta);
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.update(delta);
        timePlayedThisTurnSoFarTweenStarBullet_ThirdStage.update(delta);


        //if (controllerLeft.getAngle() != null) shield.setOmegaDeg(controllerLeft.getAngle() * MathUtils.radiansToDegrees);
        //Gdx.app.log(TAG, "controllerLeft.getAngleDeg() = " + controllerLeft.getAngleDeg());

        shieldsAndContainersHandler.handleOnShields();

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) | Gdx.input.isKeyJustPressed(Input.Keys.PLUS))  {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() + 1);
            starsContainer.getRadialTween().start(SpecialBullet.PLUS);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() - 1);
            starsContainer.getRadialTween().start(SpecialBullet.MINUS);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            healthHandler.setHealth(healthHandler.getHealth() + .05f);

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            healthHandler.setHealth(healthHandler.getHealth() - .05f);

        for (int i = BULLETS_MIN_NUMBER_PER_ATTACK + Input.Keys.NUM_0;
             i <= 9 + Input.Keys.NUM_0;
             i++) {
            if (Gdx.input.isKeyJustPressed(i)) bulletsHandler.setBulletsPerAttack(i - Input.Keys.NUM_0);
        }

        for (int i = BULLETS_MIN_NUMBER_PER_ATTACK + Input.Keys.NUMPAD_0;
             i <= 9 + Input.Keys.NUMPAD_0;
             i++) {
            if (Gdx.input.isKeyJustPressed(i)) bulletsHandler.setBulletsPerAttack(i - Input.Keys.NUMPAD_0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0) | Gdx.input.isKeyPressed(Input.Keys.NUMPAD_0))
            bulletsHandler.setBulletsPerAttack(10);

        if (Gdx.input.getRotation() != rotation) {
            //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
        }
        rotation = Gdx.input.getRotation();

        /*healthBar.setOrigin(healthBar.getRadius(), healthBar.getRadius());
        healthBar.setRotation(healthBar.getRotation() + 1);*/

        //bulletsAndShieldContainers[0].getShield().rotateBy(0.1f);

        gamePadPooling();
    }

    private void gamePadPooling() {
        if (Controllers.getControllers().size == 0) return;

        com.badlogic.gdx.controllers.Controller gamePad = Controllers.getControllers().peek();

        if (gamePad.getButton(5))
            healthHandler.setHealth(healthHandler.getHealth() + .05f);

        if (gamePad.getButton(7))
            healthHandler.setHealth(healthHandler.getHealth() - .05f);

        if (gamePad.getButton(4)) {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() + 1);
            starsContainer.getRadialTween().start(SpecialBullet.PLUS);
        }

        if (gamePad.getButton(6)) {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() - 1);
            starsContainer.getRadialTween().start(SpecialBullet.MINUS);
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        turret.setPosition(worldWidth / 2f - turret.getWidth() / 2f,
                worldHeight / 2f - turret.getHeight() / 2f);

        if (controllerLeft != null) controllerLeft.resize(width, height, worldWidth, worldHeight);
        if (controllerRight != null) controllerRight.resize(width, height, worldWidth, worldHeight);

        pauseStuff.resize(width, height, worldWidth, worldHeight);

        for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
            bulletsAndShieldContainers[i].resize(width, height, worldWidth, worldHeight);
        }

        Bullet.calculateR(worldWidth, worldHeight);

        healthBar.resize(width, height, worldWidth, worldHeight);

        score.resize(width, height, worldWidth, worldHeight);

        gameOverLayer.resize(width, height, worldWidth, worldHeight);

        speedMultiplierStuff.resize(width, height, worldWidth, worldHeight);

        whiteTextureHidesEveryThingSecondStageStarBullet.setBounds(-worldWidth, -worldHeight, worldWidth*4, worldHeight*4);
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeTurret() {
        turret = new Image(Assets.instance.gameplayAssets.turret);
        turret.setBounds(0, 0, TURRET_RADIUS * 2, TURRET_RADIUS * 2);
        addActor(turret);
        turret.setColor(1, 1, 1, 1f);
    }

    private void initializeControllers(GameplayType gameplayType) {
        if (gameplayType == GameplayType.FREE) {
            controllerLeft = new FreeController(this,
                    new Image(Assets.instance.gameplayAssets.freeControllerBG),
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_FREE_SMALL_SIZE,
                    Direction.LEFT);

            controllerRight = new FreeController(this,
                    new Image(Assets.instance.gameplayAssets.freeControllerBG),
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_FREE_SMALL_SIZE,
                    Direction.RIGHT);
        } else {
            controllerLeft = new RestrictedController(this,
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_RESTRICTED_ARCH_RADIUS,
                    CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO,
                    CONTROLLER_RESTRICTED_ARCH_ANGLE,
                    Direction.LEFT);

            controllerRight = new RestrictedController(this,
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_RESTRICTED_ARCH_RADIUS,
                    CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO,
                    CONTROLLER_RESTRICTED_ARCH_ANGLE,
                    Direction.RIGHT);
        }

        /*controllerLeft.setDebug(true);
        controllerRight.setDebug(true);*/
    }

    private void initializeBulletsAndShieldArray() {
        bulletsAndShieldContainers = new BulletsAndShieldContainer[SHIELDS_MAX_COUNT];
        for (byte i = 0; i < bulletsAndShieldContainers.length; i++) {
            bulletsAndShieldContainers[i] = new BulletsAndShieldContainer(this, i);
        }

        shieldsAndContainersHandler.setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);

        /*bulletsAndShieldContainers[0].getShield().setDebug(true);
        bulletsAndShieldContainers[0].getShield().getSemiCircle0().setDebug(true);*/
    }

    private void initializeHandlers(AdvancedStage game, StarsContainer.RadialTween radialTweenStars) {
        shieldsAndContainersHandler = new ShieldsAndContainersHandler(this);
        bulletsHandler = new BulletsHandler(game, this);
        healthHandler = new HealthHandler(this);
    }

    private void initializeTimePlayedThisTurnSoFarTweenStarBullet_FirstStage() {
        timePlayedThisTurnSoFarTweenStarBullet_FirstStage = new MyTween(STAR_BULLET_FIRST_STAGE_DURATION, STAR_BULLET_FIRST_STAGE_INTERPOLATION_INTEGRATION_OUT) {
            @Override
            public void myTween(MyInterpolation myInterpolation, float startX, float endX, float startY, float endY, float currentX, float percentage) {
                timePlayedThisTurnSoFar = myInterpolation.apply(startX, endX, startY, endY, currentX);
            }
        };

        addToPauseWhenPausingFinishWhenLosing(timePlayedThisTurnSoFarTweenStarBullet_FirstStage);
    }


    private float timePlayedSoFarStarBulletThirdStageInitialValue;
    private float timePlayedSoFarStarBulletThirdStageFinalValue;
    private void initializeTimePlayedThisTurnSoFarTweenStarBullet_ThirdStage() {
        timePlayedThisTurnSoFarTweenStarBullet_ThirdStage = new Tween(STAR_BULLET_THIRD_STAGE_DURATION, STAR_BULLET_THIRD_STAGE_SCORE_INTERPOLATION/*MyInterpolation.myLinear*/) {

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                timePlayedThisTurnSoFar = interpolation.apply(timePlayedSoFarStarBulletThirdStageInitialValue, timePlayedSoFarStarBulletThirdStageFinalValue, percentage);
            }
        };

        addToPauseWhenPausingFinishWhenLosing(timePlayedThisTurnSoFarTweenStarBullet_ThirdStage);
    }
    
    private void initializeWhiteTextureHidesEveryThingSecondStageStarBullet() {
        whiteTextureHidesEveryThingSecondStageStarBullet = new Image(Assets.instance.gameplayAssets.gameOverBG);
        whiteTextureHidesEveryThingSecondStageStarBullet.setVisible(false);
        whiteTextureHidesEveryThingSecondStageStarBullet.setColor(1, 1, 1, 0);
        addActor(whiteTextureHidesEveryThingSecondStageStarBullet);
    }
    
    private void initializeWhiteTextureHidesEveryThingSecondStageTweenStarBullet() {
        float duration = STAR_BULLET_SECOND_STAGE_DURATION*(1-STAR_BULLET_SECOND_STAGE_WHITE_TEXTURE_HIDES_EVERYTHING_DELAY_PERCENTAGE);

        whiteTextureHidesEveryThingSecondStageTweenStarBullet = new Tween(duration, Interpolation.linear) {

            @Override
            public void onStart() {
                super.onStart();
                //whiteTextureHidesEveryThingSecondStageStarBullet.setVisible(true);
            }

            public void tween(float percentage, Interpolation interpolation) {
                float alpha = interpolation.apply(0, 1, percentage);
                whiteTextureHidesEveryThingSecondStageStarBullet.setColor(1, 1, 1, alpha);

                if (alpha == 0)
                    whiteTextureHidesEveryThingSecondStageStarBullet.setVisible(false);
                else
                    whiteTextureHidesEveryThingSecondStageStarBullet.setVisible(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                /*if (!isReversed() & getPercentage() != 1) {
                    tween(0, getInterpolation());
                }*/
            }
        };
        
        addToPauseWhenPausingFinishWhenLosing(whiteTextureHidesEveryThingSecondStageTweenStarBullet);
    }


    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------


    public GameplayType getGameplayType() {
        return gameplayType;
    }

    public void setGameplayType(GameplayType gameplayType) {
        this.gameplayType = gameplayType;
        //shieldsAndContainersHandler.setGameplayType(gameplayType);
        initializeControllers(gameplayType);
        pauseStuff = new PauseStuff(this, myBitmapFont); // must be called after initializeControllers() because pauseSymbol.addActor() and some other actors must be called after controllerRight.addActor() so that touch gestures for the pauseSymbol have a higher priority for triggering input events.

    }

    public ShieldsAndContainersHandler getShieldsAndContainersHandler() {
        return shieldsAndContainersHandler;
    }

    public BulletsHandler getBulletsHandler() {
        return bulletsHandler;
    }

    public HealthHandler getHealthHandler() {
        return healthHandler;
    }

    public HealthBar getHealthBar() {
        return healthBar;
    }

    public Score getScore() {
        return score;
    }

    public BulletsAndShieldContainer[] getBulletsAndShieldContainers() {
        return bulletsAndShieldContainers;
    }

    public Controller getControllerLeft() {
        return controllerLeft;
    }

    public Controller getControllerRight() {
        return controllerRight;
    }

    public GameOverLayer getGameOverLayer() {
        return gameOverLayer;
    }

    public StarsContainer getStarsContainer() {
        return starsContainer;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public float getTimePlayedThisTurnSoFar() {
        return timePlayedThisTurnSoFar;
    }

    public MyBitmapFont getMyBitmapFont() {
        return myBitmapFont;
    }

    public PauseStuff getPauseStuff() {
        return pauseStuff;
    }

    public SpeedMultiplierStuff getSpeedMultiplierStuff() {
        return speedMultiplierStuff;
    }

    public Timer[] getPauseWhenPausingFinishWhenLosing() {
        return pauseWhenPausingFinishWhenLosing.items;
    }

    public Image getWhiteTextureHidesEveryThingSecondStageStarBullet() {
        return whiteTextureHidesEveryThingSecondStageStarBullet;
    }

    public boolean isInStarBulletAnimation() {
        return inStarBulletAnimation;
    }

    public void setInStarBulletAnimation(boolean inStarBulletAnimation) {
        this.inStarBulletAnimation = inStarBulletAnimation;
    }

    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------

    public void resetTimePlayedThisTurnSoFar() {
        timePlayedThisTurnSoFar = 0;
    }

    public void addToPauseWhenPausingFinishWhenLosing(Timer timer) {
        pauseWhenPausingFinishWhenLosing.add(timer);
    }

    public void startScoreTimePlayedThisTurnSoFarTweenStarBullet_FirstStage() {
        timePlayedThisTurnSoFarTweenStarBullet_FirstStage.setInitialVal(timePlayedThisTurnSoFar);
        timePlayedThisTurnSoFarTweenStarBullet_FirstStage.setFinalVal(timePlayedThisTurnSoFar + 0.35f * STAR_BULLET_FIRST_STAGE_DURATION * (float) millisToSeconds);
        timePlayedThisTurnSoFarTweenStarBullet_FirstStage.start();
    }

    public void startScoreTimePlayedThisTurnSoFarTweenStarBullet_ThirdStage() {
        timePlayedSoFarStarBulletThirdStageInitialValue = timePlayedThisTurnSoFar;
        timePlayedSoFarStarBulletThirdStageFinalValue = timePlayedThisTurnSoFar + STAR_BULLET_SCORE_BONUS;
        timePlayedThisTurnSoFarTweenStarBullet_ThirdStage.start();
    }

    public void startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(boolean reversed, boolean delay) {
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.setReversed(reversed);
        float delayAmount = STAR_BULLET_SECOND_STAGE_DURATION*STAR_BULLET_SECOND_STAGE_WHITE_TEXTURE_HIDES_EVERYTHING_DELAY_PERCENTAGE;
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.start(delay ? delayAmount : 0);
    }
















    public static class TimePlayedSoFarStarBulletThirdStageInterpolation extends Interpolation {

        @Override
        public float apply(float a) {

            float T8 = 26.7301674743f   * a*a*a*a*a*a*a*a;
            float T7 = -149.801712332f  * a*a*a*a*a*a*a;
            float T6 = 338.685260991f   * a*a*a*a*a*a;
            float T5 = -393.442712938f  * a*a*a*a*a;
            float T4 = 247.824289679f   * a*a*a*a;
            float T3 = -82.702738645f   * a*a*a;
            float T2 = 13.1655331616f   * a*a;
            float T1 = 0.540199230611f  * a;

            return (T8 + T7 + T6 + T5 + T4 + T3 + T2 + T1) / 0.998286620477f;
        }
    }
}
