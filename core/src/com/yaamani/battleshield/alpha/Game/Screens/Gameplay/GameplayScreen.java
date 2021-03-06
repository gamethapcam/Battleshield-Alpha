package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
//import com.badlogic.gdx.controllers.
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.BulletEffectRecord;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.RewindEngine;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind.VhsTapePostProcessingEffect;
import com.yaamani.battleshield.alpha.Game.SolidBG;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyFrameBuffer;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.RowOfActors;
import com.yaamani.battleshield.alpha.MyEngine.SimplestTransition;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;

import java.util.HashMap;
import java.util.Iterator;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameplayScreen extends AdvancedScreen {

    public static final String TAG = GameplayScreen.class.getSimpleName();

    private Image turret;
    private float currentTurretRadius;

    private GameplayControllerType gameplayControllerType;
    private GameplayMode gameplayMode;
    private HashMap<GameplayMode, Array<Timer>> modeTimers;

    private Integer currentShieldsMaxCount;
    private Integer currentShieldsMinCount;


    private Array<Timer> finishWhenStoppingTheGameplay;
    //private Array<Timer> resumeWhenResumingStarBullet;

    private Group containerOfContainers; // Initially created for diseases planet (Dizziness disease).
    private BulletsAndShieldContainer[] bulletsAndShieldContainers;
    private ShieldsAndContainersHandler shieldsAndContainersHandler;

    public enum DamageType {HEALTH, TIME}
    private DamageType damageType;

    private BulletsHandler bulletsHandler;

    private HealthHandler healthHandler;


    public enum State {PLAYING, PAUSED, STOPPED}
    private State state;

    private boolean inStarBulletAnimation = false;

    private Image whiteTextureHidesEveryThingSecondStageStarBullet;
    private Tween whiteTextureHidesEveryThingSecondStageTweenStarBullet;

    private StarsContainer starsContainer;
    private MyBitmapFont myBitmapFont;
    //private BitmapFont font;


    //private int rotation;


    private MyFrameBuffer originalGameplayFrameBuffer;
    private PortalPostProcessingEffect portalPostProcessingEffect;
    private MyFrameBuffer portalFrameBuffer;
    private VhsTapePostProcessingEffect vhsTapePostProcessingEffect;
    private MyFrameBuffer vhsTapeFrameBuffer;


    private float slowMoDeltaFraction = 1;
    private Tween slowMoDeltaFractionTween;
    private Finishable slowMoDeltaFractionTweenOnFinish;
    private Tween rewindNegativeDeltaFractionTween;
    private Tween rewindNegativeToPositiveDeltaFractionTween;

    /**
     * Includes the slow mo stage that happens before the rewinding itself.
     */
    private boolean inRewindBulletAnimation = false;
    private boolean rewinding = false;
    private Finishable rewindSlowMoFirstStageOnFinish;
    private RewindEngine rewindEngine;


    private LazerAttackStuff lazerAttackStuff;





    // HUD
    private Controller controllerLeft;
    private Controller controllerRight;

    private ScoreTimerStuff scoreTimerStuff;
    private PauseStuff pauseStuff;

    private GameOverLayer gameOverLayer;

    private HealthBar healthBar;

    private RowOfActors specialBulletUI;
    //private TempProgressBar tempProgressBar;
    private SpecialBulletTempProgressBarUI mirrorTempProgressBarUI;
    //private SpecialBulletTempProgressBarUI rewindTempProgressBarUI;
    private SpecialBulletTempProgressBarUI fasterDizzinessRotationTempProgressBarUI;
    private TwoExitPortalUI twoExitPortalUI;

    private LevelFinishStuff levelFinishStuff;



    private Group allGameplayStuff;
    private Group allHudStuff;



    private SimpleText currentInUseNumText; // For debugging
    private SimpleText activeBulletsText; // For debugging


    private SimplestTransition gameplayToMainMenu;

    private NetworkAndStorageManager networkAndStorageManager;





    private Texture badlogic;

    public GameplayScreen(AdvancedStage game, MyBitmapFont myBitmapFont, final StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        //this.font = font;
        this.myBitmapFont = myBitmapFont;
        this.starsContainer = starsContainer;

        allGameplayStuff = new Group();
        allHudStuff = new Group();
        addActor(allGameplayStuff);
        addActor(allHudStuff);

        finishWhenStoppingTheGameplay = new Array<>(false, FINISH_WHEN_STOPPING_THE_GAMEPLAY_INITIAL_CAPACITY, Timer.class);
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

        lazerAttackStuff = new LazerAttackStuff(this);
        //---------
        state = State.PLAYING;

        gameOverLayer = new GameOverLayer(this, myBitmapFont);

        /*Viewport viewport = getStage().getViewport();
        resize(viewport.getScreenWidth(),
                viewport.getScreenHeight(),
                viewport.getWorldWidth(),
                viewport.getWorldHeight());*/

        initializeModeTimers();

        initializeSlowMoDeltaFractionTween();
        initializeRewindNegativeDeltaFractionTween();
        initializeRewindNegativeToPositiveDeltaFractionTween();

        initializeRewindSlowMoFirstStageOnFinish();

        rewindEngine = new RewindEngine(this);


        //initializeTempProgressBar();
        initializeMirrorTempProgressBarUI();
        //initializeRewindTempProgressBarUI();
        initializeFasterDizzinessRotationTempProgressBarUI();
        initializeSpecialBulletUI();
        initializeTwoExitPortalUI();


        activeBulletsText = new SimpleText(myBitmapFont, "");
        allHudStuff.addActor(activeBulletsText);
        activeBulletsText.setVisible(false); // Set to true for debugging.
        activeBulletsText.setHeight(WORLD_SIZE/22f);

        currentInUseNumText = new SimpleText(myBitmapFont, "");
        allHudStuff.addActor(currentInUseNumText);
        currentInUseNumText.setVisible(false); // Set to true for debugging.
        currentInUseNumText.setHeight(WORLD_SIZE/22f);
        currentInUseNumText.setY(WORLD_SIZE/22f);


        originalGameplayFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, getStage().getViewport().getScreenWidth(), getStage().getViewport().getScreenHeight(), false);
        portalFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, getStage().getViewport().getScreenWidth(), getStage().getViewport().getScreenHeight(), false);
        vhsTapeFrameBuffer = new MyFrameBuffer(Pixmap.Format.RGBA8888, getStage().getViewport().getScreenWidth(), getStage().getViewport().getScreenHeight(), false);

        portalPostProcessingEffect = new PortalPostProcessingEffect();
        vhsTapePostProcessingEffect = new VhsTapePostProcessingEffect();

        badlogic = new Texture("badlogic.jpg");
    }

    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------


    @Override
    public void show() {
        super.show();
        allGameplayStuff.addActorAt(0, starsContainer);
        //starsContainer.remove();
    }

    @Override
    public void hide() {
        super.hide();
        getStage().getRoot().addActorAt(0, starsContainer);
    }

    @Override
    public void act(float delta) {
        if (!isVisible()) return;

        //Gdx.app.log(TAG, "FPS = " + 1f/delta);

        pauseStuff.update(delta);

        if (state == State.PAUSED) return;




        slowMoDeltaFractionTween.update(delta);
        rewindNegativeDeltaFractionTween.update(delta);
        rewindNegativeToPositiveDeltaFractionTween.update(delta);
        delta = slowMoDeltaFraction*delta;

        super.act(delta);




        debuggingControls();
        //gamePadPooling();


        //if (Gdx.input.getRotation() != rotation) {
            //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
        //}
        //rotation = Gdx.input.getRotation();

        rewindEngine.update(delta);


        lazerAttackStuff.update(delta);

        healthHandler.update(delta);

        shieldsAndContainersHandler.update(delta);

        shieldsAndContainersHandler.handleOnShields();

        debuggingText();

        whiteTextureHidesEveryThingSecondStageTweenStarBullet.update(delta);




        //Gdx.app.log(TAG, "free bullets = " + bulletsHandler.getBulletPool().getFree());
        //Gdx.app.log(TAG, "" + bulletsHandler.getCurrentWaveLastBullet());

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.A))
            mirrorTempProgressBarUI.displayFor(1000);*/

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            bulletsHandler.startStarBulletStages();
            bulletsHandler.setBulletCausingSlowMoExists(false);
            setInStarBulletAnimation(true);
        }*/



        if (!lazerAttackStuff.isLazerAttacking()) {
            scoreTimerStuff.update(delta);

            bulletsHandler.update(delta);

            levelFinishStuff.update(delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Pool<Bullet> bulletPool = getBulletsHandler().getBulletPool();
            Bullet b = bulletPool.obtain();
            b.getEffects().getRewind().effect();
            bulletPool.free(b);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            Pool<Bullet> bulletPool = getBulletsHandler().getBulletPool();
            Bullet b = bulletPool.obtain();
            b.getEffects().getTwoExitPortal().effect();
            bulletPool.free(b);
        }

        bulletsHandler.updatePlusMinusBulletsTween(delta);
        scoreTimerStuff.updateAffectTimerTweens(delta);
        scoreTimerStuff.updateCharSequenceForScoreText();
    }

    private void debuggingText() {
        if (currentInUseNumText.isVisible())
            currentInUseNumText.setCharSequence("currentInUse = " + getBulletsHandler().getCurrentInUseBulletsCount() + ",                                           active.size = " + bulletsHandler.getActiveBullets().size, true);

        if (activeBulletsText.isVisible()) {
            StringBuilder activeBullets = new StringBuilder();
            Iterator<Bullet> it = bulletsHandler.getActiveBullets().iterator();
            while (it.hasNext()) {
                Bullet bullet = it.next();
                activeBullets.append(bullet.getI()).append(", ");
            }
            activeBulletsText.setCharSequence("activeBullets = " + activeBullets.toString(), true);
        }
    }

    private void debuggingControls() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) | Gdx.input.isKeyJustPressed(Input.Keys.PLUS))  {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() + 1);
            starsContainer.getRadialTween().start(SpecialBullet.PLUS);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() - 1);
            starsContainer.getRadialTween().start(SpecialBullet.MINUS);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            healthHandler.affectHealth(+.05f);

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            healthHandler.affectHealth(-.05f);

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
    }

    private void gamePadPooling() {
        if (Controllers.getControllers().size == 0) return;

        com.badlogic.gdx.controllers.Controller gamePad = Controllers.getControllers().peek();

        if (gamePad.getButton(5))
            healthHandler.affectHealth(+.05f);

        if (gamePad.getButton(7))
            healthHandler.affectHealth(-.05f);

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


        //Gdx.app.log(TAG, "RUNNINGRUNNINGRUNNINGRUNNINGRUNNINGRUNNINGRUNNINGRUNNINGRUNNINGRUNNING");

        batch.end();

        originalGameplayFrameBuffer.begin();
        batch.begin();

        SolidBG.instance.draw();

        batch.setColor(1, 1, 1, 1);
        removeActor(allHudStuff);
        addActor(allGameplayStuff);
        super.draw(batch, parentAlpha);
        batch.end();
        originalGameplayFrameBuffer.end();

        //Texture gameplayFrameBufferTexture = originalGameplayFrameBuffer.getColorBufferTexture();
        //gameplayFrameBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        //batch.begin();

        Texture currentGameplayTexture = originalGameplayFrameBuffer.getColorBufferTexture();;



        batch.setColor(1, 1, 1, 1);

        //TODO: Render portal to another framebuffer

        if (bulletsHandler.isThereAPortal()) {

            portalFrameBuffer.begin();

            portalPostProcessingEffect.draw(batch,
                    currentGameplayTexture,
                    0,
                    0,
                    getStage().getViewport().getWorldWidth(),
                    getStage().getViewport().getWorldHeight());

            portalFrameBuffer.end();


            currentGameplayTexture = portalFrameBuffer.getColorBufferTexture();

        } /*else {


        }*/


        if (isInRewindBulletAnimation()) {
            vhsTapeFrameBuffer.begin();

            vhsTapePostProcessingEffect.draw(batch,
                    currentGameplayTexture,
                    0,
                    0,
                    getStage().getViewport().getWorldWidth(),
                    getStage().getViewport().getWorldHeight());

            vhsTapeFrameBuffer.end();

            currentGameplayTexture = vhsTapeFrameBuffer.getColorBufferTexture();

        }



        batch.begin();

        batch.draw(currentGameplayTexture,
                0,
                0,
                getStage().getViewport().getWorldWidth(),
                getStage().getViewport().getWorldHeight(),
                0,
                0,
                currentGameplayTexture.getWidth(),
                currentGameplayTexture.getHeight(),
                false,
                true);

        removeActor(allGameplayStuff);
        addActor(allHudStuff);
        super.draw(batch, parentAlpha);


        clearChildren();
        addActor(allGameplayStuff);
        addActor(allHudStuff); // The HUD must be in front.


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

        specialBulletUI.resize(width, height, worldWidth, worldHeight);
        //tempProgressBar.resize(width, height, worldWidth, worldHeight);

        levelFinishStuff.resize(width, height, worldWidth, worldHeight);

        containerOfContainers.setPosition(worldWidth/2f, worldHeight/2f);

        lazerAttackStuff.resize(width, height, worldWidth, worldHeight);

        portalPostProcessingEffect.resize(width, height, worldWidth, worldHeight);
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeTurret() {
        turret = new Image(Assets.instance.gameplayAssets.turret);

        setCurrentTurretRadius(TURRET_RADIUS);
        allGameplayStuff.addActor(turret);
        turret.setColor(1, 1, 1, 1f);
    }

    private void initializeControllers(GameplayControllerType gameplayControllerType) {
        if (gameplayControllerType == GameplayControllerType.FREE) {
            controllerLeft = new FreeController(this,
                    new Image(Assets.instance.gameplayAssets.freeControllerBG),
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_FREE_SMALL_SIZE,
                    Direction.LEFT,
                    networkAndStorageManager);

            controllerRight = new FreeController(this,
                    new Image(Assets.instance.gameplayAssets.freeControllerBG),
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_FREE_SMALL_SIZE,
                    Direction.RIGHT,
                    networkAndStorageManager);
        } else {
            controllerLeft = new RestrictedController(this,
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_RESTRICTED_ARCH_RADIUS,
                    CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO,
                    CONTROLLER_RESTRICTED_ARCH_ANGLE,
                    Direction.LEFT,
                    networkAndStorageManager);

            controllerRight = new RestrictedController(this,
                    new Image(Assets.instance.gameplayAssets.controllerStick),
                    CONTROLLER_RESTRICTED_ARCH_RADIUS,
                    CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO,
                    CONTROLLER_RESTRICTED_ARCH_ANGLE,
                    Direction.RIGHT,
                    networkAndStorageManager);
        }

        /*controllerLeft.setDebug(true);
        controllerRight.setDebug(true);*/
    }

    private void initializeModeTimers() {
        modeTimers = new HashMap<>();

        Array<Timer> survivalTimers = new Array<>(false, 4, Timer.class);
        survivalTimers.add(bulletsHandler.getD_survival_bulletsPerAttackNumberTween());
        survivalTimers.add(bulletsHandler.getD_survival_bulletSpeedMultiplierTween());
        survivalTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getSurvival_scoreMultiplierTween());
        survivalTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.SURVIVAL, survivalTimers);

        Array<Timer> crystalTimers = new Array<>(false, 5, Timer.class);
        crystalTimers.add(bulletsHandler.getD_crystal_bulletsPerAttackNumberTween());
        crystalTimers.add(bulletsHandler.getD_crystal_bulletSpeedMultiplierTween());
        crystalTimers.add(bulletsHandler.getD_crystal_fakeWaveProbabilityTween());
        crystalTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getCrystal_difficultyLevelTween());
        crystalTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.CRYSTAL, crystalTimers);

        Array<Timer> dizzinessTimers = new Array<>(false, 5, Timer.class);
        dizzinessTimers.add(bulletsHandler.getD_dizziness_bulletsPerAttackNumberTween());
        dizzinessTimers.add(bulletsHandler.getD_dizziness_bulletSpeedMultiplierTween());
        dizzinessTimers.add(shieldsAndContainersHandler.getD_dizziness_rotationalSpeedTween());
        dizzinessTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getDizziness_difficultyLevelTween());
        dizzinessTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.DIZZINESS, dizzinessTimers);

        Array<Timer> lazerTimers = new Array<>(false, 5, Timer.class);
        lazerTimers.add(lazerAttackStuff.getNextLazerAttackTimer());
        lazerTimers.add(bulletsHandler.getD_lazer_bulletsPerAttackNumberTween());
        lazerTimers.add(bulletsHandler.getD_lazer_bulletSpeedMultiplierTween());
        lazerTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getLazer_difficultyLevelTween());
        lazerTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.LAZER, lazerTimers);

        Array<Timer> portalsTimers = new Array<>(false, 4, Timer.class);
        portalsTimers.add(bulletsHandler.getD_portals_bulletsPerAttackNumberTween());
        portalsTimers.add(bulletsHandler.getD_portals_bulletSpeedMultiplierTween());
        portalsTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getPortals_difficultyLevelTween());
        portalsTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.PORTALS, portalsTimers);

        Array<Timer> t1Timers = new Array<>(false, 4, Timer.class);
        t1Timers.add(bulletsHandler.getD_t1_bulletsPerAttackNumberTween());
        t1Timers.add(bulletsHandler.getD_t1_bulletSpeedMultiplierTween());
        t1Timers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getT1_difficultyLevelTween());
        t1Timers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.T1, t1Timers);

        Array<Timer> bigBossTimers = new Array<>(false, 6, Timer.class);
        bigBossTimers.add(shieldsAndContainersHandler.getD_bigBoss_rotationalSpeedTween());
        bigBossTimers.add(lazerAttackStuff.getNextLazerAttackTimer());
        bigBossTimers.add(bulletsHandler.getD_bigBoss_bulletsPerAttackNumberTween());
        bigBossTimers.add(bulletsHandler.getD_bigBoss_bulletSpeedMultiplierTween());
        bigBossTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getBigBoss_difficultyLevelTween());
        bigBossTimers.add(scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween());
        modeTimers.put(GameplayMode.BIG_BOSS, bigBossTimers);
    }

    private void initializeBulletsAndShieldArray(int shieldsMaxCount) {
        if (containerOfContainers == null) {
            containerOfContainers = new Group();
            allGameplayStuff.addActorAt(gameOverLayer.getZIndex(), containerOfContainers);
        }

        if (bulletsAndShieldContainers == null)
            bulletsAndShieldContainers = new BulletsAndShieldContainer[shieldsMaxCount];
        else if (bulletsAndShieldContainers.length < shieldsMaxCount)
            bulletsAndShieldContainers = new BulletsAndShieldContainer[shieldsMaxCount];

        for (byte i = 0; i < bulletsAndShieldContainers.length; i++) {

            if (bulletsAndShieldContainers[i] == null)
                bulletsAndShieldContainers[i] = new BulletsAndShieldContainer(this, containerOfContainers, i);
        }

        shieldsAndContainersHandler.initializeActiveShields(SHIELDS_ACTIVE_DEFAULT);
        //shieldsAndContainersHandler.setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);


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
        allGameplayStuff.addActor(whiteTextureHidesEveryThingSecondStageStarBullet);
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

        addToFinishWhenStoppingTheGameplay(whiteTextureHidesEveryThingSecondStageTweenStarBullet);
        //addToResumeWhenResumingStarBullet(whiteTextureHidesEveryThingSecondStageTweenStarBullet);
    }

    private void initializeSlowMoDeltaFractionTween() {
        slowMoDeltaFractionTween = new Tween(SLOW_MO_TWEENS_DURATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                slowMoDeltaFraction = interpolation.apply(1, 0, percentage);
            }

            @Override
            public void onFinish() {
                super.onFinish();

                if (getState() == State.STOPPED) return;

                slowMoDeltaFractionTweenOnFinish.onFinish();
            }
        };
    }

    private void initializeRewindSlowMoFirstStageOnFinish() {
        rewindSlowMoFirstStageOnFinish = new Finishable() {
            @Override
            public void onFinish() {
                setRewinding(true);
                rewindNegativeDeltaFractionTween.start();
            }
        };
    }

    private void initializeRewindNegativeDeltaFractionTween() {
        rewindNegativeDeltaFractionTween = new Tween(SLOW_MO_TWEENS_DURATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                slowMoDeltaFraction = interpolation.apply(0, REWIND_SPEED, percentage);
            }
        };
    }

    private void initializeRewindNegativeToPositiveDeltaFractionTween() {
        rewindNegativeToPositiveDeltaFractionTween = new Tween(SLOW_MO_TWEENS_DURATION/2f) {

            boolean passedZero = false;

            @Override
            public void onStart() {
                super.onStart();
                passedZero = false;
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                slowMoDeltaFraction = interpolation.apply(REWIND_SPEED, 1, percentage);
                if (slowMoDeltaFraction >= 0 & !passedZero) {
                    passedZero = true;
                    setRewinding(false);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setInRewindBulletAnimation(false);
            }
        };
    }

    private void initializeSpecialBulletUI() {
        specialBulletUI = new RowOfActors(SPECIAL_BULLET_UI_MARGIN_BETWEEN_ACTORS);
        allHudStuff.addActor(specialBulletUI);
        specialBulletUI.setPosition(SPECIAL_BULLET_UI_X, SPECIAL_BULLET_UI_Y);
    }

    /*private void initializeTempProgressBar() {
        tempProgressBar = new TempProgressBar(this, myBitmapFont);
        tempProgressBar.setProgressBarPercentageBarHeightRatio(MY_PROGRESS_BAR_DEFAULT_PERCENTAGE_BAR_HEIGHT_RATIO);
        addActor(tempProgressBar);
    }*/

    private void initializeMirrorTempProgressBarUI() {
        TextureRegion region = Assets.instance.gameplayAssets.mirrorBullet;
        mirrorTempProgressBarUI = new SpecialBulletTempProgressBarUI(region) {
            @Override
            public void onStartingToDisplay() {
                if (!getTween().isStarted()) {
                    specialBulletUI.addActor(mirrorTempProgressBarUI);
                }
            }

            @Override
            public void onTweenStart() {
                super.onTweenStart();
                if (isRewinding()) {
                    if (!getTween().isStarted()) {
                        onTweenFinish();
                        setVisible(false);
                    }
                }
            }

            @Override
            public void onTween(float percentage, Interpolation interpolation) {
                /*if (isInRewindBulletAnimation() & !isTweenPaused())
                    pauseTween();*/
            }

            @Override
            public void onTweenFinish() {
                specialBulletUI.removeActor(mirrorTempProgressBarUI);
            }
        };
    }

    /*private void initializeRewindTempProgressBarUI() {
        TextureRegion region = Assets.instance.gameplayAssets.rewindBullet;
        rewindTempProgressBarUI = new SpecialBulletTempProgressBarUI(region) {
            @Override
            public void onStartingToDisplay() {
                if (!getTween().isStarted())
                    specialBulletUI.addActor(rewindTempProgressBarUI);
            }

            @Override
            public void onTween(float percentage, Interpolation interpolation) {
                *//*if (isInRewindBulletAnimation() & !isTweenPaused())
                    pauseTween();*//*
            }

            @Override
            public void onTweenFinish() {
                specialBulletUI.removeActor(rewindTempProgressBarUI);
            }
        };
    }*/

    private void initializeFasterDizzinessRotationTempProgressBarUI() {
        TextureRegion region = Assets.instance.gameplayAssets.fasterDizzinessRotationBullet;
        fasterDizzinessRotationTempProgressBarUI = new SpecialBulletTempProgressBarUI(region) {
            @Override
            public void onStartingToDisplay() {
                if (!getTween().isStarted())
                    specialBulletUI.addActor(fasterDizzinessRotationTempProgressBarUI);
            }

            @Override
            public void onTweenStart() {
                super.onTweenStart();
                if (isRewinding()) {
                    if (!getTween().isStarted()) {
                        onTweenFinish();
                        setVisible(false);
                    }
                }
            }

            @Override
            public void onTween(float percentage, Interpolation interpolation) {
                /*if (isInRewindBulletAnimation() & !isTweenPaused())
                    pauseTween();*/
            }

            @Override
            public void onTweenFinish() {
                specialBulletUI.removeActor(fasterDizzinessRotationTempProgressBarUI);
            }
        };
    }

    private void initializeTwoExitPortalUI() {
        twoExitPortalUI = new TwoExitPortalUI(myBitmapFont, Assets.instance.gameplayAssets.twoExitPortal, Assets.instance.gameplayAssets.twoExitPortalGlow);
        twoExitPortalUI.setVisible(false);
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

    public HashMap<GameplayMode, Array<Timer>> getModeTimers() {
        return modeTimers;
    }

    public GameplayMode getGameplayMode() {
        return gameplayMode;
    }

    private void setCurrentTurretRadius(float radius) {
        currentTurretRadius = radius;
        turret.setBounds(0, 0, currentTurretRadius * 2, currentTurretRadius * 2);
    }

    public void setGameplayMode(GameplayMode gameplayMode) {
        this.gameplayMode = gameplayMode;


        if (networkAndStorageManager != null) {
            if (networkAndStorageManager.isSaveControllerValuesModeEnabled())
                networkAndStorageManager.getWriteToStorageTimer().start();
        }


        if (gameplayMode == GameplayMode.SURVIVAL) {
            //scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().setVisible(true);
            scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().survival();

            bulletsHandler.setCurrentPlanetSpecialBullets(null);

            lazerAttackStuff.hide();

            setDamageType(DamageType.HEALTH);

            //bulletsHandler.startSurvivalDifficultyTweens();

            bulletsHandler.setStopHandlingNewWave(false);
            setCurrentShieldsMinMaxCount(SURVIVAL_SHIELDS_MIN_COUNT, SURVIVAL_SHIELDS_MAX_COUNT);


        } else {
            //scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().setVisible(false);

            switch (gameplayMode) {
                case CRYSTAL:
                    scoreTimerStuff.setLevelTime(CRYSTAL_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().crystal();

                    bulletsHandler.setCurrentPlanetSpecialBullets(CRYSTAL_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_CRYSTAL_SPECIAL_BULLETS_PROBABILITY);

                    lazerAttackStuff.hide();

                    setDamageType(DamageType.HEALTH);

                    //bulletsHandler.startCrystalDifficultyTweens();

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(CRYSTAL_SHIELDS_MIN_COUNT, CRYSTAL_SHIELDS_MAX_COUNT);
                    break;

                case DIZZINESS:
                    scoreTimerStuff.setLevelTime(DIZZINESS_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().dizziness();

                    bulletsHandler.setCurrentPlanetSpecialBullets(DIZZINESS_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_DIZZINESS_SPECIAL_BULLETS_PROBABILITY);

                    lazerAttackStuff.hide();

                    setDamageType(DamageType.HEALTH);

                    //bulletsHandler.startDizzinessDifficultyTweens();

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(DIZZINESS_SHIELDS_MIN_COUNT, DIZZINESS_SHIELDS_MAX_COUNT);
                    break;

                case LAZER:
                    scoreTimerStuff.setLevelTime(LAZER_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().lazer();

                    bulletsHandler.setCurrentPlanetSpecialBullets(null); // A custom algorithm for this planet's special bullet.

                    lazerAttackStuff.show();
                    lazerAttackStuff.calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();

                    setDamageType(DamageType.HEALTH);

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(LAZER_SHIELDS_MIN_COUNT, LAZER_SHIELDS_MAX_COUNT);
                    break;

                case PORTALS:
                    scoreTimerStuff.setLevelTime(PORTALS_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().portals();

                    bulletsHandler.setCurrentPlanetSpecialBullets(PORTALS_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_PORTALS_SPECIAL_BULLETS_PROBABILITY);

                    lazerAttackStuff.hide();

                    setDamageType(DamageType.HEALTH);

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(PORTALS_SHIELDS_MIN_COUNT, PORTALS_SHIELDS_MAX_COUNT);
                    break;

                case T1:
                    scoreTimerStuff.setLevelTime(T1_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().t1();

                    bulletsHandler.setCurrentPlanetSpecialBullets(T1_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_T1_SPECIAL_BULLETS_PROBABILITY);

                    lazerAttackStuff.hide();

                    setDamageType(DamageType.TIME);

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(T1_SHIELDS_MIN_COUNT, T1_SHIELDS_MAX_COUNT);
                    break;

                case BIG_BOSS:
                    scoreTimerStuff.setLevelTime(BIG_BOSS_LEVEL_TIME);
                    scoreTimerStuff.getScoreMultiplierDifficultyLevelStuff().bigBoss();

                    bulletsHandler.setCurrentPlanetSpecialBullets(BIG_BOSS_SPECIAL_BULLETS);
                    bulletsHandler.setCurrentPlanetSpecialBulletsProbability(D_BIG_BOSS_SPECIAL_BULLETS_PROBABILITY);

                    lazerAttackStuff.show();
                    lazerAttackStuff.calculateCurrentNecessaryNumOfArmorBulletsForTheNextAttack();

                    setDamageType(DamageType.TIME);

                    bulletsHandler.setStopHandlingNewWave(false);
                    setCurrentShieldsMinMaxCount(BIG_BOSS_SHIELDS_MIN_COUNT, BIG_BOSS_SHIELDS_MAX_COUNT);
                    break;

                case NETWORK_RECEIVER_VALUES_LOADER:

                    scoreTimerStuff.setLevelTime(Float.MAX_VALUE);

                    lazerAttackStuff.hide();

                    setDamageType(DamageType.HEALTH);

                    bulletsHandler.setStopHandlingNewWave(true);
                    setCurrentShieldsMinMaxCount(3, SHIELDS_UNIVERSAL_MAX_COUNT);
                    break;
            }

        }

        //scoreTimerStuff.gameplayModeStuff(gameplayMode);
        Array<Timer> timers = modeTimers.get(gameplayMode);
        if (timers != null)
            for (Timer timer : timers)
                timer.start();

        healthHandler.newGame();
    }

    public ShieldsAndContainersHandler getShieldsAndContainersHandler() {
        return shieldsAndContainersHandler;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
        if (damageType == DamageType.HEALTH) {
            healthBar.setVisible(true);
            setCurrentTurretRadius(TURRET_RADIUS);
        } else {
            healthBar.setVisible(false);
            setCurrentTurretRadius(HEALTH_BAR_RADIUS * HEALTH_BAR_INNER_RADIUS_RATIO);
        }
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

    /*public float getTimePlayedThisRoundSoFar() {
        return timePlayedThisRoundSoFar;
    }*/

    public MyBitmapFont getMyBitmapFont() {
        return myBitmapFont;
    }

    public PauseStuff getPauseStuff() {
        return pauseStuff;
    }

    public Timer[] getFinishWhenStoppingTheGameplay() {
        return finishWhenStoppingTheGameplay.items;
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

    /*public TempProgressBar getTempProgressBar() {
        return tempProgressBar;
    }*/

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
        bulletsHandler.initializeTempAvailableContainers(currentShieldsMaxCount);
        shieldsAndContainersHandler.initializeNonBusyContainers(currentShieldsMaxCount);
        shieldsAndContainersHandler.initializeOnStartAnglesAndOnEndAngles(currentShieldsMaxCount);
        shieldsAndContainersHandler.initializeOldRotationDegAndNewRotationDeg(currentShieldsMaxCount);
        initializeBulletsAndShieldArray(currentShieldsMaxCount);

    }

    public PortalPostProcessingEffect getPortalPostProcessingEffect() {
        return portalPostProcessingEffect;
    }

    public RewindEngine getRewindEngine() {
        return rewindEngine;
    }

    public boolean isInRewindBulletAnimation() {
        return inRewindBulletAnimation;
    }

    public void setInRewindBulletAnimation(boolean inRewindBulletAnimation) {
        this.inRewindBulletAnimation = inRewindBulletAnimation;
    }

    public Tween getRewindNegativeToPositiveDeltaFractionTween() {
        return rewindNegativeToPositiveDeltaFractionTween;
    }

    public boolean isRewinding() {
        return rewinding;
    }

    public void setRewinding(boolean rewinding) {
        this.rewinding = rewinding;
        if (rewinding) {
            bulletsHandler.setStopHandlingNewWave(true);
            rewindEngine.startRewinding();
        } else {
            bulletsHandler.setStopHandlingNewWave(false);
            for (Timer timer : modeTimers.get(getGameplayMode())) {
                if (!timer.isStarted())
                    timer.start();
            }
            slowMoDeltaFraction = 1; // Just to make sure it's 1
        }
    }

    public Finishable getRewindSlowMoFirstStageOnFinish() {
        return rewindSlowMoFirstStageOnFinish;
    }

    public LazerAttackStuff getLazerAttackStuff() {
        return lazerAttackStuff;
    }

    public RowOfActors getSpecialBulletUI() {
        return specialBulletUI;
    }

    public SpecialBulletTempProgressBarUI getMirrorTempProgressBarUI() {
        return mirrorTempProgressBarUI;
    }

    /*public SpecialBulletTempProgressBarUI getRewindTempProgressBarUI() {
        return rewindTempProgressBarUI;
    }*/

    public SpecialBulletTempProgressBarUI getFasterDizzinessRotationTempProgressBarUI() {
        return fasterDizzinessRotationTempProgressBarUI;
    }

    public TwoExitPortalUI getTwoExitPortalUI() {
        return twoExitPortalUI;
    }

    public SimplestTransition getGameplayToMainMenu() {
        return gameplayToMainMenu;
    }

    public void setGameplayToMainMenu(SimplestTransition gameplayToMainMenu) {
        this.gameplayToMainMenu = gameplayToMainMenu;
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
        shieldsAndContainersHandler.setNetworkAndStorageManager(networkAndStorageManager);
        healthHandler.setNetworkAndStorageManager(networkAndStorageManager);
        levelFinishStuff.setNetworkAndStorageManager(networkAndStorageManager);
        bulletsHandler.setNetworkAndStorageManager(networkAndStorageManager);
    }

    public Group getAllGameplayStuff() {
        return allGameplayStuff;
    }

    public Group getAllHudStuff() {
        return allHudStuff;
    }

    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------
    //------------------------------ Other methods ------------------------------

    /*public void resetTimePlayedThisRoundSoFar() {
        timePlayedThisRoundSoFar = 0;
    }*/

    public void addToFinishWhenStoppingTheGameplay(Timer timer) {
        finishWhenStoppingTheGameplay.add(timer);
    }

    /*public void addToResumeWhenResumingStarBullet(Timer timer) {
        resumeWhenResumingStarBullet.add(timer);
    }*/

    public void pauseModeTimers() {
        Array<Timer> timers = getModeTimers().get(getGameplayMode());
        for (Timer timer : timers)
            timer.pause();
    }

    public void startWhiteTextureHidesEveryThingSecondStageTweenStarBullet(boolean reversed, boolean delay) {
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.setReversed(reversed);
        float delayAmount = STAR_BULLET_SECOND_STAGE_DURATION*STAR_BULLET_SECOND_STAGE_WHITE_TEXTURE_HIDES_EVERYTHING_DELAY_PERCENTAGE;
        whiteTextureHidesEveryThingSecondStageTweenStarBullet.start(delay ? delayAmount : 0);
    }

    /*public void displayTempProgressBar(TextureRegion region, float millis) {
        tempProgressBar.display(region, millis);
    }

    public void displayTempProgressBar(String charSequence, float millis) {
        tempProgressBar.display(charSequence, millis);
    }*/

    public void displayTwoExitPortalUI() {
        if (!isRewinding()) {
            BulletEffectRecord twoExitPortalBulletEffectRecord = getRewindEngine().obtainBulletEffectRecord(BulletEffectRecord.BulletEffectRecordType.TWO_EXIT_PORTAL);
            if (!twoExitPortalUI.isVisible())
                twoExitPortalBulletEffectRecord.val = D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES+1;
            else
                twoExitPortalBulletEffectRecord.val = D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES;
            getRewindEngine().pushRewindEvent(twoExitPortalBulletEffectRecord);
        }

        twoExitPortalUI.setVisible(true);
        twoExitPortalUI.resetText();
        if (twoExitPortalUI.getParent() == null)
            specialBulletUI.addActor(twoExitPortalUI);
    }

    public void stopDisplayingTwoExitPortalUI() {
        twoExitPortalUI.setVisible(false);
        specialBulletUI.removeActor(twoExitPortalUI);

        if (!isRewinding()) {
            BulletEffectRecord twoExitPortalBulletEffectRecord = getRewindEngine().obtainBulletEffectRecord(BulletEffectRecord.BulletEffectRecordType.TWO_EXIT_PORTAL);
            twoExitPortalBulletEffectRecord.val = -1;
            getRewindEngine().pushRewindEvent(twoExitPortalBulletEffectRecord);
        }
    }

    /**
     * Calls {@code setVisible(false)}.
     */
    /*public void hideTempProgressBar() {
        tempProgressBar.setVisible(false);
    }*/

    public void showFinishButtonAndRelatedStuff() {
        scoreTimerStuff.getPlanetsTimerFlashesWhenZero().start();
        levelFinishStuff.getFinishTextTween().start();
        levelFinishStuff.getFinishText().setVisible(true);
        //tempProgressBar.positionBottomLeft();
    }

    public void hideFinishButtonAndRelatedStuff() {
        scoreTimerStuff.getPlanetsTimerFlashesWhenZero().pause();
        levelFinishStuff.getFinishTextTween().pause();
        levelFinishStuff.getFinishText().setVisible(false);
        //tempProgressBar.positionCentre();
    }

    public void startSlowMoDeltaFractionTween(boolean reversed, Finishable onFinish) {
        slowMoDeltaFractionTweenOnFinish = onFinish;
        slowMoDeltaFractionTween.setReversed(reversed);
        slowMoDeltaFractionTween.start();
    }

    public void finishSlowMoDeltaFractionTween() {
        slowMoDeltaFractionTweenOnFinish = null;
        slowMoDeltaFractionTween.setReversed(false);
        slowMoDeltaFractionTween.finish();
    }

    public void revertSlowMoDeltaFractionTo1() {
        slowMoDeltaFraction = 1;
    }









    // --------------------------------------------------------
    // --------------------------------------------------------
    // --------------------------------------------------------

    public interface Finishable {
        void onFinish();
    }
}
