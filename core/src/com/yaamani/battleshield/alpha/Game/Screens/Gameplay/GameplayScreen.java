package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
//import com.badlogic.gdx.controllers.
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.TempProgressBar;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import java.util.Arrays;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameplayScreen extends AdvancedScreen {

    public static final String TAG = GameplayScreen.class.getSimpleName();

    private Image turret;

    private GameplayControllerType gameplayControllerType;
    private GameplayMode gameplayMode;

    private Integer currentShieldsMaxCount;
    private Integer currentShieldsMinCount;


    private Array<Timer> finishWhenLosing;
    //private Array<Timer> resumeWhenResumingStarBullet;

    private Group containerOfContainers; // Initially created for diseases planet (Dizziness disease).
    private BulletsAndShieldContainer[] bulletsAndShieldContainers;
    private ShieldsAndContainersHandler shieldsAndContainersHandler;

    private BulletsHandler bulletsHandler;

    private HealthHandler healthHandler;


    public enum State {PLAYING, PAUSED, LOST}
    private State state;
    private boolean inStarBulletAnimation = false;

    private Image whiteTextureHidesEveryThingSecondStageStarBullet;
    private Tween whiteTextureHidesEveryThingSecondStageTweenStarBullet;

    private StarsContainer starsContainer;
    private MyBitmapFont myBitmapFont;
    //private BitmapFont font;


    private int rotation;


    // HUD
    private Controller controllerLeft;
    private Controller controllerRight;

    private ScoreTimerStuff scoreTimerStuff;
    private PauseStuff pauseStuff;

    private GameOverLayer gameOverLayer;

    private HealthBar healthBar;

    private TempProgressBar tempProgressBar;

    private LevelFinishStuff levelFinishStuff;




    public GameplayScreen(AdvancedStage game, MyBitmapFont myBitmapFont, final StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        //this.font = font;
        this.myBitmapFont = myBitmapFont;
        this.starsContainer = starsContainer;

        finishWhenLosing = new Array<>(false, PAUSE_WHEN_PAUSING_FINISH_WHEN_LOSING_INITIAL_CAPACITY, Timer.class);
        //resumeWhenResumingStarBullet = new Array<>(false, PAUSE_WHEN_PAUSING_STAR_BULLET_INITIAL_CAPACITY, Timer.class);

        initializeHandlers(game, starsContainer.getRadialTween());

        initializeTurret();

        //initializeBullets(starsContainer.getRadialTween());

        //bulletsHandler.newWave();

        healthBar = new HealthBar(this);


        initializeWhiteTextureHidesEveryThingSecondStageStarBullet();
        initializeWhiteTextureHidesEveryThingSecondStageTweenStarBullet();

        scoreTimerStuff = new ScoreTimerStuff(this, myBitmapFont);

        //initializeControllers();

        levelFinishStuff = new LevelFinishStuff(this);


        //---------
        state = State.PLAYING;

        gameOverLayer = new GameOverLayer(this, myBitmapFont);

        /*Viewport viewport = getStage().getViewport();
        resize(viewport.getScreenWidth(),
                viewport.getScreenHeight(),
                viewport.getWorldWidth(),
                viewport.getWorldHeight());*/

        initializeTempProgressBar();
    }

    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------

    @Override
    public void act(float delta) {
        if (!isVisible()) return;

        pauseStuff.update(delta);

        if (state == State.PAUSED) return;

        super.act(delta);

        scoreTimerStuff.update(delta);

        /*if (getState() == GameplayScreen.State.PLAYING) {
            if (!inStarBulletAnimation) timePlayedThisTurnSoFar += delta;
        }*/



        shieldsAndContainersHandler.update(delta);
        bulletsHandler.update(delta);

        whiteTextureHidesEveryThingSecondStageTweenStarBullet.update(delta);

        levelFinishStuff.update(delta);

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

        for (int i = D_SURVIVAL_BULLETS_MIN_NUMBER_PER_ATTACK + Input.Keys.NUM_0;
             i <= 9 + Input.Keys.NUM_0;
             i++) {
            if (Gdx.input.isKeyJustPressed(i)) bulletsHandler.setBulletsPerAttack(i - Input.Keys.NUM_0);
        }

        for (int i = D_SURVIVAL_BULLETS_MIN_NUMBER_PER_ATTACK + Input.Keys.NUMPAD_0;
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

        scoreTimerStuff.resize(width, height, worldWidth, worldHeight);

        gameOverLayer.resize(width, height, worldWidth, worldHeight);

        whiteTextureHidesEveryThingSecondStageStarBullet.setBounds(-worldWidth, -worldHeight, worldWidth*4, worldHeight*4);

        tempProgressBar.resize(width, height, worldWidth, worldHeight);

        levelFinishStuff.resize(width, height, worldWidth, worldHeight);

        containerOfContainers.setPosition(worldWidth/2f, worldHeight/2f);
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

    private void initializeControllers(GameplayControllerType gameplayControllerType) {
        if (gameplayControllerType == GameplayControllerType.FREE) {
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

    private void initializeBulletsAndShieldArray(int shieldsMaxCount) {
        if (containerOfContainers == null) {
            containerOfContainers = new Group();
            addActorAt(gameOverLayer.getZIndex(), containerOfContainers);
        }

        if (bulletsAndShieldContainers == null)
            bulletsAndShieldContainers = new BulletsAndShieldContainer[shieldsMaxCount];

        for (byte i = 0; i < bulletsAndShieldContainers.length; i++) {

            if (bulletsAndShieldContainers[i] == null)
                bulletsAndShieldContainers[i] = new BulletsAndShieldContainer(this, containerOfContainers, i);
        }

        shieldsAndContainersHandler.setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);


        //containerOfContainers.rotateBy(-10f);

        /*bulletsAndShieldContainers[0].getShield().setDebug(true);
        bulletsAndShieldContainers[0].getShield().getSemiCircle0().setDebug(true);*/
    }

    private void initializeHandlers(AdvancedStage game, StarsContainer.RadialTween radialTweenStars) {
        shieldsAndContainersHandler = new ShieldsAndContainersHandler(this);
        bulletsHandler = new BulletsHandler(game, this);
        healthHandler = new HealthHandler(this);
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

        addToFinishWhenLosing(whiteTextureHidesEveryThingSecondStageTweenStarBullet);
        //addToResumeWhenResumingStarBullet(whiteTextureHidesEveryThingSecondStageTweenStarBullet);
    }

    private void initializeTempProgressBar() {
        tempProgressBar = new TempProgressBar(this, myBitmapFont);
        tempProgressBar.setProgressBarPercentageBarHeightRatio(MY_PROGRESS_BAR_DEFAULT_PERCENTAGE_BAR_HEIGHT_RATIO);
        addActor(tempProgressBar);
    }



    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------


    public GameplayControllerType getGameplayControllerType() {
        return gameplayControllerType;
    }

    public void setGameplayControllerType(GameplayControllerType gameplayControllerType) {
        this.gameplayControllerType = gameplayControllerType;
        //shieldsAndContainersHandler.setGameplayType(gameplayType);
        initializeControllers(gameplayControllerType);
        pauseStuff = new PauseStuff(this, myBitmapFont); // must be called after initializeControllers() because pauseSymbol.addActor() and some other actors must be called after controllerRight.addActor() so that touch gestures for the pauseSymbol have a higher priority for triggering input events.

    }

    public GameplayMode getGameplayMode() {
        return gameplayMode;
    }



    public void setGameplayMode(GameplayMode gameplayMode) {
        this.gameplayMode = gameplayMode;

        if (gameplayMode == GameplayMode.SURVIVAL) {
            //scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().setVisible(true);
            scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().survival();

            bulletsHandler.setCurrentPlanetSpecialBullets(null);

            //bulletsHandler.startSurvivalDifficultyTweens();
            bulletsHandler.getD_survival_bulletsPerAttackNumberTween().start();
            bulletsHandler.getD_survival_bulletSpeedMultiplierTween().start();

            setCurrentShieldsMinMaxCount(SURVIVAL_SHIELDS_MIN_COUNT, SURVIVAL_SHIELDS_MAX_COUNT);


        } else {
            //scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().setVisible(false);

            switch (gameplayMode) {
                case CRYSTAL:
                    scoreTimerStuff.setLevelTime(CRYSTAL_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().crystal();

                    bulletsHandler.setCurrentPlanetSpecialBullets(CRYSTAL_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_CRYSTAL_SPECIAL_BULLETS_PROBABILITY);

                    //bulletsHandler.startCrystalDifficultyTweens();
                    bulletsHandler.getD_crystal_bulletsPerAttackNumberTween().start();
                    bulletsHandler.getD_crystal_bulletSpeedMultiplierTween().start();
                    bulletsHandler.getD_crystal_fakeWaveProbabilityTween().start();

                    setCurrentShieldsMinMaxCount(CRYSTAL_SHIELDS_MIN_COUNT, CRYSTAL_SHIELDS_MAX_COUNT);

                    break;
                case DIZZINESS:
                    scoreTimerStuff.setLevelTime(DIZZINESS_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().dizziness();

                    bulletsHandler.setCurrentPlanetSpecialBullets(DIZZINESS_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_DIZZINESS_SPECIAL_BULLETS_PROBABILITY);

                    //bulletsHandler.startDizzinessDifficultyTweens();
                    bulletsHandler.getD_dizziness_bulletsPerAttackNumberTween().start();
                    bulletsHandler.getD_dizziness_bulletSpeedMultiplierTween().start();
                    shieldsAndContainersHandler.getD_dizziness_rotationalSpeedTween().start();

                    setCurrentShieldsMinMaxCount(DIZZINESS_SHIELDS_MIN_COUNT, DIZZINESS_SHIELDS_MAX_COUNT);

                    break;
            }

        }

        //scoreTimerStuff.gameplayModeStuff(gameplayMode);

        healthHandler.newGame();
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

    public ScoreTimerStuff getScoreTimerStuff() {
        return scoreTimerStuff;
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

    /*public float getTimePlayedThisTurnSoFar() {
        return timePlayedThisTurnSoFar;
    }*/

    public MyBitmapFont getMyBitmapFont() {
        return myBitmapFont;
    }

    public PauseStuff getPauseStuff() {
        return pauseStuff;
    }

    public Timer[] getFinishWhenLosing() {
        return finishWhenLosing.items;
    }

    /*public Timer[] getResumeWhenResumingStarBullet() {
        return resumeWhenResumingStarBullet.items;
    }*/

    public Image getWhiteTextureHidesEveryThingSecondStageStarBullet() {
        return whiteTextureHidesEveryThingSecondStageStarBullet;
    }

    public boolean isInStarBulletAnimation() {
        return inStarBulletAnimation;
    }

    public void setInStarBulletAnimation(boolean inStarBulletAnimation) {
        this.inStarBulletAnimation = inStarBulletAnimation;
    }

    public TempProgressBar getTempProgressBar() {
        return tempProgressBar;
    }

    public LevelFinishStuff getLevelFinishStuff() {
        return levelFinishStuff;
    }

    public Group getContainerOfContainers() {
        return containerOfContainers;
    }

    public Integer getCurrentShieldsMaxCount() {
        return currentShieldsMaxCount;
    }

    public Integer getCurrentShieldsMinCount() {
        return currentShieldsMinCount;
    }

    public void setCurrentShieldsMinMaxCount(Integer currentShieldsMinCount, Integer currentShieldsMaxCount) {
        this.currentShieldsMaxCount = currentShieldsMaxCount;
        this.currentShieldsMinCount = currentShieldsMinCount;

        bulletsHandler.initializeBusyAndNonBusyContainers(currentShieldsMaxCount);
        shieldsAndContainersHandler.initializeNonBusyContainers(currentShieldsMaxCount);
        shieldsAndContainersHandler.initializeOnStartAnglesAndOnEndAngles(currentShieldsMaxCount);
        initializeBulletsAndShieldArray(currentShieldsMaxCount);

    }

    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------

    /*public void resetTimePlayedThisTurnSoFar() {
        timePlayedThisTurnSoFar = 0;
    }*/

    public void addToFinishWhenLosing(Timer timer) {
        finishWhenLosing.add(timer);
    }

    /*public void addToResumeWhenResumingStarBullet(Timer timer) {
        resumeWhenResumingStarBullet.add(timer);
    }*/

    public void startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(boolean reversed, boolean delay) {
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.setReversed(reversed);
        float delayAmount = STAR_BULLET_SECOND_STAGE_DURATION*STAR_BULLET_SECOND_STAGE_WHITE_TEXTURE_HIDES_EVERYTHING_DELAY_PERCENTAGE;
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.start(delay ? delayAmount : 0);
    }

    public void displayTempProgressBar(TextureRegion region, float millis) {
        tempProgressBar.display(region, millis);
    }

    public void displayTempProgressBar(String charSequence, float millis) {
        tempProgressBar.display(charSequence, millis);
    }

    /**
     * Calls {@code setVisible(false)}.
     */
    public void hideTempProgressBar() {
        tempProgressBar.setVisible(false);
    }

    public void onWaitingForFinishButtonToBePressed() {
        scoreTimerStuff.getPlanetsTimerFlashesWhenZero().start();
        levelFinishStuff.getFinishTextTween().start();
        levelFinishStuff.getFinishText().setVisible(true);
        tempProgressBar.positionBottomLeft();
    }
}
