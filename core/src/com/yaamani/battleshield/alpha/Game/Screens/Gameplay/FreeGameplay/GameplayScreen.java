package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Bullet;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Controller;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameOverLayer;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.HealthBar;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Score;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.OneBigSizeBitmapFontTextField;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameplayScreen extends AdvancedScreen {

    public static final String TAG = GameplayScreen.class.getSimpleName();

    private Image turret;

    private BulletsAndShieldContainer[] bulletsAndShieldContainers;
    private ShieldsAndContainersHandler shieldsAndContainersHandler;

    private Array<Bullet> bulletBank; //Pool
    private BulletsHandler bulletsHandler;

    private HealthBar healthBar;
    private HealthHandler healthHandler;

    public enum State {PLAYING, LOST}
    private State state;

    private Score score;

    private Controller controllerLeft;
    private Controller controllerRight;

    private GameOverLayer gameOverLayer;

    private StarsContainer starsContainer;

    private BitmapFont font;

    private int rotation;

    public GameplayScreen(AdvancedStage game, BitmapFont font, StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        initializeHandlers(game, starsContainer.getRadialTween());

        initializeTurret();
        initializeBulletsAndShieldArray(game);
        initializeBullets(starsContainer.getRadialTween());

        healthBar = new HealthBar(this, healthHandler);

        score = new Score(this, font);

        // HUD ----
        initializeControllers();

        //---------
        state = State.PLAYING;

        gameOverLayer = new GameOverLayer(this, font);

        this.starsContainer = starsContainer;

        this.font = font;
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

    private void initializeControllers() {
        controllerLeft = new Controller(this,
                new Image(Assets.instance.gameplayAssets.controllerBG),
                new Image(Assets.instance.gameplayAssets.controllerStick),
                ControllerSize.SMALL,
                ControllerPosition.LEFT);

        controllerRight = new Controller(this,
                new Image(Assets.instance.gameplayAssets.controllerBG),
                new Image(Assets.instance.gameplayAssets.controllerStick),
                ControllerSize.SMALL,
                ControllerPosition.RIGHT);

        /*controllerLeft.setDebug(true);
        controllerRight.setDebug(true);*/
    }

    private void initializeBulletsAndShieldArray(AdvancedStage game) {
        bulletsAndShieldContainers = new BulletsAndShieldContainer[SHIELDS_MAX_COUNT];
        for (byte i = 0; i < bulletsAndShieldContainers.length; i++) {
            bulletsAndShieldContainers[i] = new BulletsAndShieldContainer(this, i, game);
        }

        shieldsAndContainersHandler.setActiveShields(SHIELDS_ACTIVE_DEFAULT);
    }

    private void initializeBullets(StarsContainer.RadialTween radialTweenStars) {
        bulletBank = new Array<Bullet>();
        for (int i = 0; i < BULLETS_BANK_INITIAL_CAPACITY; i++) {
            bulletBank.add(new Bullet(this, radialTweenStars, getStage().getViewport()));
        }
        bulletsHandler.newWave();
    }

    private void initializeHandlers(AdvancedStage game, StarsContainer.RadialTween radialTweenStars) {
        shieldsAndContainersHandler = new ShieldsAndContainersHandler(this);
        bulletsHandler = new BulletsHandler(game, radialTweenStars, this);
        healthHandler = new HealthHandler(this);
    }
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    //------------------------------------------------------------
    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);

        //if (controllerLeft.getAngle() != null) shield.setOmegaDeg(controllerLeft.getAngle() * MathUtils.radiansToDegrees);
        //Gdx.app.log(TAG, "controllerLeft.getAngleDeg() = " + controllerLeft.getAngleDeg());

        shieldsAndContainersHandler.handleOnShields();

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) | Gdx.input.isKeyJustPressed(Input.Keys.PLUS))  {
            shieldsAndContainersHandler.setActiveShields(shieldsAndContainersHandler.getActiveShields() + 1);
            bulletsHandler.getRadialTweenStars().start(SpecialBullet.PLUS);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            shieldsAndContainersHandler.setActiveShields(shieldsAndContainersHandler.getActiveShields() - 1);
            bulletsHandler.getRadialTweenStars().start(SpecialBullet.MINUS);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0))
            healthHandler.setHealth(healthHandler.getHealth() + .05f);

        if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE))
            healthHandler.setHealth(healthHandler.getHealth() - .05f);

        if (Gdx.input.getRotation() != rotation) {
            //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
        }
        rotation = Gdx.input.getRotation();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        /*font.draw(batch, "ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                "abcdefghijklmnopqrstuvwxyz\n" +
                "1234567890 \n" +
                "\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*\u0000\u007F", 0, WORLD_SIZE/2f);*/
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        turret.setPosition(worldWidth / 2f - turret.getWidth() / 2f,
                worldHeight / 2f - turret.getHeight() / 2f);

        controllerLeft.resize(width, height, worldWidth, worldHeight);
        controllerRight.resize(width, height, worldWidth, worldHeight);

        for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
            bulletsAndShieldContainers[i].setPosition(worldWidth / 2f, worldHeight / 2f);
            bulletsAndShieldContainers[i].resize(width, height, worldWidth, worldHeight);
        }

        for (int i = 0; i < bulletBank.size; i++) {
            bulletBank.get(i).resize(width, height, worldWidth, worldHeight);
        }

        healthBar.resize(width, height, worldWidth, worldHeight);

        score.resize(width, height, worldWidth, worldHeight);

        gameOverLayer.resize(width, height, worldWidth, worldHeight);
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

    public State getState() {
        return state;
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

    public Array<Bullet> getBulletBank() {
        return bulletBank;
    }

    public void setState(State state) {
        this.state = state;
    }

    public GameOverLayer getGameOverLayer() {
        return gameOverLayer;
    }

    public StarsContainer getStarsContainer() {
        return starsContainer;
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


















    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




















    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------











}
