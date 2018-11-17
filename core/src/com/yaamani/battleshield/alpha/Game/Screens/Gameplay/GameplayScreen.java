package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
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

    private Array<Bullet> bulletBank;
    private BulletsHandler bulletsHandler;

    private HealthHandler healthHandler;
    private HealthBar healthBar;

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
        shieldsAndContainersHandler = new ShieldsAndContainersHandler();
        bulletsHandler = new BulletsHandler(game, radialTweenStars);
        healthHandler = new HealthHandler();
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
            bulletsHandler.radialTweenStars.start(SpecialBullet.PLUS);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            shieldsAndContainersHandler.setActiveShields(shieldsAndContainersHandler.getActiveShields() - 1);
            bulletsHandler.radialTweenStars.start(SpecialBullet.MINUS);
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









    class ShieldsAndContainersHandler {

        private int activeShields;
        private Array<BulletsAndShieldContainer> probability;

        // TODO: [FIX A BUG] Sometimes (only sometimes which is really weird) when the gameplay begins the shields and the bullets won't be displayed (But they do exist, meaning that the bullets reduce the health and the shield can be on and block the bullets). And get displayed after a plus or a minus bullet hit the turret. (Not sure if this is a desktop specific or happens on android too) [PATH TO VIDEO = Junk/Shield and bullets don't appear [BUG].mov] .. It looks like it always happen @ the first run of the program on desktop just after I open android studio
        private void setVisibilityAndAlphaForContainers() {
            for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
                if (i < activeShields) {
                    bulletsAndShieldContainers[i].setVisible(true);
                    bulletsAndShieldContainers[i].setNewAlpha(1);
                } else {
                    bulletsAndShieldContainers[i].setNewAlpha(0);
                    //bulletsAndShieldContainers[i].setVisible(false);
                }
            }
        }

        private void setRotationForContainers() {
            for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
                if (i < activeShields)
                    bulletsAndShieldContainers[i].setNewRotationDeg(i * 360f / activeShields + SHIELDS_SHIFT_ANGLES[activeShields - SHIELDS_MIN_COUNT]);
                else bulletsAndShieldContainers[i].setNewRotationDeg(360);
            }
        }

        private void setOmegaForShieldObjects() {
            float omegaDeg = 360f / activeShields;
            for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
                bulletsAndShieldContainers[i].setNewOmegaDeg(omegaDeg);
            }
        }

        private void startRotationOmegaTweenForAll() {
            for (int i = 0; i < bulletsAndShieldContainers.length; i++) {
                bulletsAndShieldContainers[i].startRotationOmegaAlphaTween();
            }
        }

        private void handleOnShields() {
            Float[] cAs = {controllerLeft.getAngleDegNoNegative(), controllerRight.getAngleDegNoNegative()}; // cAs is for controllerAngles.

            for (int l = 0; l < activeShields; l++)
                bulletsAndShieldContainers[l].getShield().setOn(false);

            for (int i = 0; i < 2; i++) {
                if (cAs[i] == null) continue;

                for (int c = 0; c < activeShields; c++) {
                    float onStartAngle = bulletsAndShieldContainers[c].getRotation() - (360f / activeShields / 2f);
                    float onEndAngle = bulletsAndShieldContainers[c].getRotation() + (360f / activeShields / 2f);
                    if (onStartAngle < 0) onStartAngle += 360f; //To avoid -ve angles.

                    boolean setOnToTrue = false;

                    if (onStartAngle > onEndAngle) {
                        if (cAs[i] >= onStartAngle | cAs[i] <= onEndAngle) setOnToTrue = true;
                    } else if (cAs[i] > onStartAngle & cAs[i] <= onEndAngle) setOnToTrue = true;

                    if (setOnToTrue) {
                        bulletsAndShieldContainers[c].getShield().setOn(true);
                        break;
                    }
                }
            }
        }

        public void setActiveShields(int activeShields) {
            if (activeShields > SHIELDS_MAX_COUNT) this.activeShields = SHIELDS_MAX_COUNT;
            else if (activeShields < SHIELDS_MIN_COUNT) this.activeShields = SHIELDS_MIN_COUNT;
            else this.activeShields = activeShields;

            newProbability();

            shieldsAndContainersHandler.setVisibilityAndAlphaForContainers();
            shieldsAndContainersHandler.setRotationForContainers();
            shieldsAndContainersHandler.setOmegaForShieldObjects();
            shieldsAndContainersHandler.startRotationOmegaTweenForAll();
        }

        public int getActiveShields() {
            return activeShields;
        }

        private void newProbability() {
            probability = new Array<BulletsAndShieldContainer>(false,
                    bulletsAndShieldContainers,
                    0,
                    activeShields);
            bulletsHandler.previous = bulletsHandler.current = null;
        }

        public Array<BulletsAndShieldContainer> getProbability() {
            return probability;
        }
    }








    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------









    public class BulletsHandler implements Updatable {

        public final String TAG = GameplayScreen.TAG + "." + BulletsHandler.class.getSimpleName();

        private Timer currentBulletsWaveTimer; // Just a timer.
        private BulletsAndShieldContainer previous;
        private BulletsAndShieldContainer current;

        private int bulletsPerAttack = BULLETS_DEFAULT_NO_PER_ATTACK;
        private Timer decreaseBulletsPerAttackTimer;

        private Timer plusMinusBulletsTimer;

        private StarsContainer.RadialTween radialTweenStars;

        private int roundStart;
        private Integer roundTurn = null;
        private RoundType roundType;
        private boolean roundTurnPassedActiveShieldsMinusOne;

        private BulletsHandler(AdvancedStage game, StarsContainer.RadialTween radialTweenStars) {
            game.addUpdatable(BulletsHandler.this);

            initializePlusMinusBulletsTimer();

            initializeDecreaseBulletsPerAttackTimer();

            resetWaveTimer();

            this.radialTweenStars = radialTweenStars;
        }

        @Override
        public void update(float delta) {
            if (!GameplayScreen.this.isVisible()) return;

            currentBulletsWaveTimer.update(delta);
            plusMinusBulletsTimer.update(delta);
            decreaseBulletsPerAttackTimer.update(delta);

            //Gdx.app.log(TAG, "" + Bullet.isTherePlusOrMinus());
        }

        void setBulletsPerAttack(int bulletsPerAttack) {
            this.bulletsPerAttack = bulletsPerAttack;
        }

        public int getBulletsPerAttack() {
            return bulletsPerAttack;
        }

        /*public void setPrevious(BulletsAndShieldContainer previous) {
            this.previous = previous;
        }

        public void setCurrent(BulletsAndShieldContainer current) {
            this.current = current;
        }*/

        Timer getCurrentBulletsWaveTimer() {
            return currentBulletsWaveTimer;
        }

        Timer getDecreaseBulletsPerAttackTimer() {
            return decreaseBulletsPerAttackTimer;
        }

        private void setRoundTurn(Integer roundTurn) {
            this.roundTurn = roundTurn;
        }

        void startPlusMinusBulletsTween() {
            plusMinusBulletsTimer.start();
        }

        private void resetWaveTimer() {
            float duration = ((bulletsPerAttack + 1) * (BULLETS_DISTANCE_BETWEEN_TWO + BULLETS_ORDINARY_WIDTH) / BULLETS_SPEED) * 1000;

            if (currentBulletsWaveTimer == null) initializeCurrentBulletWave(duration);
            else currentBulletsWaveTimer.setDurationMillis(duration);

            currentBulletsWaveTimer.start();
        }

        private void attachBullets(BulletsAndShieldContainer parent) {
            WaveBulletsType waveBulletsType = WaveBulletsType.ALL_ORDINARY;
            int specialBulletOrder = 0;
            SpecialBullet specialBullet = null;

            /*if (bulletsHandler.getBulletsPerAttack() > 1)*/ waveBulletsType = MyMath.chooseFromProbabilityArray(WAVE_BULLETS_TYPE_PROBABILITY);
            //else waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_BAD;

            if (waveBulletsType == WaveBulletsType.HAS_A_SPECIAL_GOOD) {
                specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY);;

                if (specialBullet == SpecialBullet.MINUS) {
                    if (shieldsAndContainersHandler.getActiveShields() == SHIELDS_MIN_COUNT | Bullet.isTherePlusOrMinus() | !plusMinusBulletsTimer.isFinished()) {
                        /*waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_BAD;
                        specialBullet = SpecialBullet.PLUS;*/
                        specialBullet = MyMath.chooseFromProbabilityArray(GOOD_BULLETS_PROBABILITY, 0, GOOD_BULLETS_PROBABILITY.length-2);
                    }
                }
            } else if (waveBulletsType == WaveBulletsType.HAS_A_SPECIAL_BAD) {
                specialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY);

                if (specialBullet == SpecialBullet.PLUS) {
                    if (shieldsAndContainersHandler.getActiveShields() == SHIELDS_MAX_COUNT | Bullet.isTherePlusOrMinus() | !plusMinusBulletsTimer.isFinished()) {
                        /*waveBulletsType = WaveBulletsType.HAS_A_SPECIAL_GOOD;
                        specialBullet = SpecialBullet.MINUS;*/
                        specialBullet = MyMath.chooseFromProbabilityArray(BAD_BULLETS_PROBABILITY, 0, BAD_BULLETS_PROBABILITY.length-2);
                    }
                }
            }

            specialBulletOrder = MathUtils.random(bulletsPerAttack - 1);

            if (specialBullet == SpecialBullet.PLUS | specialBullet == SpecialBullet.MINUS) {
                Bullet.setThereIsPlusOrMinus(true);
            }

            //------------------------------------------------------
            int bulletsAttached = 0;
            for (int i = 0; bulletsAttached < bulletsPerAttack; i++) {
                Bullet bullet;
                if (i < bulletBank.size) bullet = bulletBank.get(i);
                else {
                    bullet = new Bullet(GameplayScreen.this, radialTweenStars, getStage().getViewport());
                    bulletBank.add(bullet);
                }

                if (!bullet.isInUse()) {
                    bullet.attachToBulletsAndShieldContainer(parent, bulletsAttached);

                    if (waveBulletsType != WaveBulletsType.ALL_ORDINARY) {
                        if (bulletsAttached == specialBulletOrder) bullet.setSpecial(specialBullet);
                        else bullet.notSpecial();
                    } else bullet.notSpecial();

                    bulletsAttached++;
                }
            }
        }

        private void newWave() {
            //if (!isVisible()) return;
            if (roundTurn != null) {
                continueRoundWave();
                resetWaveTimer();
                return;
            }
            WaveAttackType waveAttackType = MyMath.chooseFromProbabilityArray(WAVE_TYPES_PROBABILITY);
            switch (waveAttackType) {
                case SINGLE:
                    newSingleWave();
                    break;
                case DOUBLE:
                    newDoubleWave();
                    break;
                case ROUND:
                    newRoundWave();
                    break;
            }
            resetWaveTimer();
        }

        //--------------------------------------- Simple waves methods ---------------------------------------------
        //--------------------------------------- Simple waves methods ---------------------------------------------
        //--------------------------------------- Simple waves methods ---------------------------------------------
        private void newSingleWave() {
            Gdx.app.log(bulletsHandler.TAG, "NEW SINGLE WAVE");

            attachBullets(handlePreviousCurrentContainers());
        }

        private void newDoubleWave() {
            Gdx.app.log(bulletsHandler.TAG, "NEW DOUBLE WAVE");
            attachBullets(handlePreviousCurrentContainers());
            attachBullets(handlePreviousCurrentContainers());

            /*attachBullets(probability.removeIndex(MathUtils.random(activeShields-1)));
            attachBullets(probability.get(MathUtils.random(activeShields-2)));*/
        }

        private BulletsAndShieldContainer handlePreviousCurrentContainers() {
            if (previous == null) {
                int rand = MathUtils.random(shieldsAndContainersHandler.getActiveShields() - 1);
                current = shieldsAndContainersHandler.getProbability().removeIndex(rand);
                Gdx.app.log(TAG, "" + rand);

            } else {
                int rand = MathUtils.random(shieldsAndContainersHandler.getActiveShields() - 2);
                current = shieldsAndContainersHandler.getProbability().removeIndex(rand);
                shieldsAndContainersHandler.getProbability().insert(shieldsAndContainersHandler.getActiveShields() - 2, previous);
                Gdx.app.log(TAG, "" + rand);
            }
            previous = current;
            return current;
        }

        //--------------------------------------- Complex waves methods ---------------------------------------------
        //--------------------------------------- Complex waves methods ---------------------------------------------
        //--------------------------------------- Complex waves methods ---------------------------------------------
        private void newRoundWave() {
            roundType = RoundType.values()[MathUtils.random(1)];
            roundStart = MathUtils.random(shieldsAndContainersHandler.getActiveShields() - 1);
            roundTurn = roundStart;
            current = bulletsAndShieldContainers[roundTurn];
            roundTurnPassedActiveShieldsMinusOne = false;
            attachBullets(current);
            Gdx.app.log(TAG, "NEW ROUND WAVE, " + roundStart + ", " + roundType.toString());
        }

        private void continueRoundWave() {
            if (roundType == RoundType.ANTI_CLOCKWISE) {
                roundTurn++;
                if (roundTurn > shieldsAndContainersHandler.getActiveShields() - 1) {
                    roundTurn = 0;
                    roundTurnPassedActiveShieldsMinusOne = true;
                }
                if (roundTurn >= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                    roundTurn = null;
                    if (state == State.PLAYING) newWave();
                } else attachBullets(bulletsAndShieldContainers[roundTurn]);
            } else {
                roundTurn--;
                if (roundTurn < 0) {
                    roundTurn = shieldsAndContainersHandler.getActiveShields() - 1;
                    roundTurnPassedActiveShieldsMinusOne = true;
                }
                if (roundTurn <= roundStart & roundTurnPassedActiveShieldsMinusOne) {
                    roundTurn = null;
                    if (state == State.PLAYING) newWave();
                } else attachBullets(bulletsAndShieldContainers[roundTurn]);
            }
            Gdx.app.log(TAG, "CONTINUE ROUND, " + roundTurn);
        }

        //------------------------------ initializers ------------------------------
        //------------------------------ initializers ------------------------------
        //------------------------------ initializers ------------------------------
        private void initializeCurrentBulletWave(final float duration) {
            currentBulletsWaveTimer = new Timer(duration) {
                @Override
                public void onUpdate(float delta) {
                    //Gdx.app.log(bulletsHandler.TAG, "duration = " + duration);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Gdx.app.log(bulletsHandler.TAG, "------------------------------------------- Finished -------------------------------------------");
                    if (state == State.PLAYING) newWave();
                }
            };
        }

        private void initializePlusMinusBulletsTimer() {
            plusMinusBulletsTimer = new Timer(SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION) {
                @Override
                public void onUpdate(float delta) {

                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Gdx.app.log(TAG, "plusMinusBulletsTimer isFinished");
                }
            };

            plusMinusBulletsTimer.start();
        }

        private void initializeDecreaseBulletsPerAttackTimer() {
            decreaseBulletsPerAttackTimer = new Timer(BULLETS_DECREASE_NO_PER_ATTACK_EVERY) {
                @Override
                public void onUpdate(float delta) {

                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (state == State.PLAYING)
                        if (bulletsPerAttack > BULLETS_MIN_NUMBER_PER_ATTACK) {
                            bulletsPerAttack--;
                            decreaseBulletsPerAttackTimer.start();
                            Gdx.app.log(TAG, "decreaseBulletsPerAttackTimer isFinished");
                        }
                }
            };

            decreaseBulletsPerAttackTimer.start();
        }
    }










    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------










    class HealthHandler {
        private float health; // 0 <= health <= oo

        public HealthHandler() {
            setHealth(1);
        }

        public float getHealth() {
            return health;
        }

        public void setHealth(float health) {
            float correctedHealth = MathUtils.clamp(health, 0, Float.MAX_VALUE);
            this.health = correctedHealth;

            if (healthBar != null) healthBar.drawHealth(correctedHealth);

            if (correctedHealth == 0) {
                playerLost();
            }
        }

        void playerLost() {
            state = State.LOST;
            bulletsHandler.setRoundTurn(null);
            bulletsHandler.getCurrentBulletsWaveTimer().finish();
            for (int i = 0; i < bulletBank.size; i++) {
                Bullet bullet = bulletBank.get(i);
                if (bullet.isInUse()) bullet.detachFromBulletsAndShieldObject();
                score.getFadeOutTween().start();
            }
            score.updateBestScoreButDontRegisterToHardDriveYet();
            gameOverLayer.thePlayerLost();
            starsContainer.setThetaForRadialTween(5 * MathUtils.degreesToRadians);
        }

        void newGame() {
            if (state == State.LOST) {
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());

                state = State.PLAYING;
                healthHandler.setHealth(1f);
                score.resetScore();
                OneBigSizeBitmapFontTextField scoreText = score.getScoreText();
                scoreText.setColor(scoreText.getColor().r, scoreText.getColor().g, scoreText.getColor().b, 1);
                bulletsHandler.setBulletsPerAttack(BULLETS_DEFAULT_NO_PER_ATTACK);
                bulletsHandler.newWave();
                bulletsHandler.getDecreaseBulletsPerAttackTimer().start();
                gameOverLayer.disappearToStartANewGame();
                starsContainer.setThetaForRadialTween(0);
                shieldsAndContainersHandler.setActiveShields(SHIELDS_ACTIVE_DEFAULT);
            }
        }
    }
}
