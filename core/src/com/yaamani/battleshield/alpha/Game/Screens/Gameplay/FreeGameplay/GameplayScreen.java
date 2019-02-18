package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
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

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class GameplayScreen extends AdvancedScreen {

    public static final String TAG = GameplayScreen.class.getSimpleName();

    private Image turret;

    private BulletsAndShieldContainer[] bulletsAndShieldContainers;
    private ShieldsAndContainersHandler shieldsAndContainersHandler;

    private Pool<Bullet> bulletPool;
    private Array<Bullet> activeBullets;
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

    public GameplayScreen(AdvancedStage game, BitmapFont font, final StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        this.starsContainer = starsContainer;

        initializeBulletPool();

        initializeHandlers(game, starsContainer.getRadialTween());

        initializeTurret();
        initializeBulletsAndShieldArray(game);
        //initializeBullets(starsContainer.getRadialTween());

        bulletsHandler.newWave();


        healthBar = new HealthBar(this);

        score = new Score(this, font);

        // HUD ----
        initializeControllers();

        //---------
        state = State.PLAYING;

        gameOverLayer = new GameOverLayer(this, font);


        this.font = font;
    }

    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------
    //----------------------------- Super Class Methods -------------------------------

    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);

        //if (controllerLeft.getAngle() != null) shield.setOmegaDeg(controllerLeft.getAngle() * MathUtils.radiansToDegrees);
        //Gdx.app.log(TAG, "controllerLeft.getAngleDeg() = " + controllerLeft.getAngleDeg());

        shieldsAndContainersHandler.handleOnShields();

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) | Gdx.input.isKeyJustPressed(Input.Keys.PLUS))  {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() + 1);
            bulletsHandler.getRadialTweenStars().start(SpecialBullet.PLUS);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            shieldsAndContainersHandler.setActiveShieldsNum(shieldsAndContainersHandler.getActiveShieldsNum() - 1);
            bulletsHandler.getRadialTweenStars().start(SpecialBullet.MINUS);
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
            bulletsAndShieldContainers[i].resize(width, height, worldWidth, worldHeight);
        }

        Bullet.calculateR(worldWidth, worldHeight);

        healthBar.resize(width, height, worldWidth, worldHeight);

        score.resize(width, height, worldWidth, worldHeight);

        gameOverLayer.resize(width, height, worldWidth, worldHeight);
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

        shieldsAndContainersHandler.setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);

        /*bulletsAndShieldContainers[0].getShield().setDebug(true);
        bulletsAndShieldContainers[0].getShield().getSemiCircle0().setDebug(true);*/
    }

    private void initializeBulletPool() {
        bulletPool = new Pool<Bullet>(BULLETS_POOL_INITIAL_CAPACITY) {
            @Override
            protected Bullet newObject() {
                return new Bullet(GameplayScreen.this, starsContainer.getRadialTween(), getStage().getViewport());
            }

            @Override
            public Bullet obtain() {
                Bullet bullet = super.obtain();
                activeBullets.add(bullet);
                return bullet;
            }
        };

        activeBullets = new Array<Bullet>(false, 40, Bullet.class);
    }

    private void initializeHandlers(AdvancedStage game, StarsContainer.RadialTween radialTweenStars) {
        shieldsAndContainersHandler = new ShieldsAndContainersHandler(this);
        bulletsHandler = new BulletsHandler(game, radialTweenStars, this);
        healthHandler = new HealthHandler(this);
    }

    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------

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

    public Pool<Bullet> getBulletPool() {
        return bulletPool;
    }

    public Array<Bullet> getActiveBullets() {
        return activeBullets;
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

}
