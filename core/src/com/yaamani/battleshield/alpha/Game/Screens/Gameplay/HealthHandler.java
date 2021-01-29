package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.TweenedFloat;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Iterator;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthHandler implements Updatable {

    private static final String TAG = HealthHandler.class.getSimpleName();

    private GameplayScreen gameplayScreen;

    private NetworkAndStorageManager networkAndStorageManager;

    //private float health; // 0 <= health <= oo
    private TweenedFloat health;

    public HealthHandler(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        health = new TweenedFloat(1) {
            @Override
            public void onTween(float value) {

                if (gameplayScreen.getState() != GameplayScreen.State.PLAYING) return;

                if (gameplayScreen.getHealthBar() != null)
                    gameplayScreen.getHealthBar().setAngle(value*MathUtils.PI2);

                if (this.getCurrentFinalValue() <= 0) {
                    if (value <= 0)
                        playerLost();
                }
            }
        };
        gameplayScreen.addToFinishWhenStoppingTheGameplay(health.getTweenObj());

        //setHealth(1);
    }

    @Override
    public void update(float delta) {
        health.update(delta);
    }

    public TweenedFloat getHealth() {
        return health;
    }

    public void setHealthValueTo(float health) {
        setHealthValueTo(health, HEALTH_BAR_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
    }

    public void setHealthValueTo(float health, float durationMillis, Interpolation interpolation) {
        float correctedHealth = MathUtils.clamp(health, 0, Float.MAX_VALUE);
        //this.health = correctedHealth;
        this.health.tween(correctedHealth, durationMillis, interpolation);
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
    }

    public void affectHealthBy(float by, float durationMillis, Interpolation interpolation) {
        float healthFinishedValue = health.getCurrentFinalValue();
        setHealthValueTo(healthFinishedValue + by, durationMillis, interpolation);
    }

    public void affectHealthBy(float by) {
        affectHealthBy(by, HEALTH_BAR_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
    }

    public void stopTheGameplay() {
        gameplayScreen.setState(GameplayScreen.State.STOPPED);
        gameplayScreen.getBulletsHandler().setRoundTurn(null);
        //gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().finish();

        float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
        float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
        /*int len = gameplayScreen.getBulletsHandler().getActiveBullets().size;
        for (int i = len-1; i >= 0; i--) {
            Bullet bullet = gameplayScreen.getBulletsHandler().getActiveBullets().get(i);
            bullet.stopUsingTheBullet(worldWidth, worldHeight, true);
        }*/
        Iterator<Bullet> it = gameplayScreen.getBulletsHandler().getActiveBullets().iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            bullet.stopUsingTheBullet(worldWidth, worldHeight, false);
            it.remove();
        }

        Bullet.setPlusOrMinusExists(false);
        Bullet.setCurrentPlusOrMinusBullet(null);
        Bullet.setStarExists(false);
        gameplayScreen.setInStarBulletAnimation(false);

        Timer[] pauseWhenPausingFinishWhenStoppingTheGameplay = gameplayScreen.getFinishWhenStoppingTheGameplay();
        for (int i = 0; i < pauseWhenPausingFinishWhenStoppingTheGameplay.length; i++) {
            if (pauseWhenPausingFinishWhenStoppingTheGameplay[i] != null)
                pauseWhenPausingFinishWhenStoppingTheGameplay[i].finish();
        }

        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setColor(1, 1, 1, 0);
        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setVisible(false);

        gameplayScreen.hideTempProgressBar();

        gameplayScreen.getLevelFinishStuff().getFinishText().setVisible(false);

        gameplayScreen.getShieldsAndContainersHandler().resetContainersRotation();

        gameplayScreen.getLazerAttackStuff().resetLazerStuff();

        gameplayScreen.getBulletsHandler().portalIsOver();


        if (networkAndStorageManager != null)
            if (networkAndStorageManager.isSaveControllerValuesModeEnabled())
                networkAndStorageManager.saveTheMostRecentEntries();


        Gdx.app.log(TAG, "");
    }

    public void playerLost() {
        stopTheGameplay();


        gameplayScreen.getScoreTimerStuff().getFadeOutTween().start();

        gameplayScreen.getPauseStuff().getPauseSymbolFadesOutWhenLosing().start();



        //gameplayScreen.getScoreStuff().updateBestScoreButDontRegisterToHardDriveYet();
        gameplayScreen.getGameOverLayer().thePlayerLost();



        gameplayScreen.getStarsContainer().setBaseRadialVelocity(-5 * MathUtils.degreesToRadians);



        if (gameplayScreen != null)
            if (gameplayScreen.getScoreTimerStuff() != null)
                gameplayScreen.getScoreTimerStuff().registerBestScoreToHardDrive();


        Gdx.app.log(TAG, "XXXXXXXXXXXXXXXXXXXXXXXX Gameplay stopped XXXXXXXXXXXXXXXXXXXXXXXX");
        Gdx.app.log(TAG, "XXXXXXXXXXXXXXXXXXXXXXXX Gameplay stopped XXXXXXXXXXXXXXXXXXXXXXXX");
        Gdx.app.log(TAG, "XXXXXXXXXXXXXXXXXXXXXXXX Gameplay stopped XXXXXXXXXXXXXXXXXXXXXXXX");

    }

    public void newGame() {
        if (gameplayScreen.getState() == GameplayScreen.State.STOPPED) {

            gameplayScreen.setState(GameplayScreen.State.PLAYING);

            gameplayScreen.setGameplayMode(gameplayScreen.getGameplayMode());

            gameplayScreen.resize(Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    gameplayScreen.getStage().getViewport().getWorldWidth(),
                    gameplayScreen.getStage().getViewport().getWorldHeight());

            setHealthValueTo(1f);


            //gameplayScreen.getScoreTimerStuff().resetScore();
            //gameplayScreen.getBulletsHandler().resetSpeedResetTime();
            gameplayScreen.getScoreTimerStuff().resetScore();
            SimpleText scoreText = gameplayScreen.getScoreTimerStuff().getScoreText();
            scoreText.setColor(scoreText.getColor().r, scoreText.getColor().g, scoreText.getColor().b, 1);
            SimpleText scoreMultiplierText = gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getScoreMultiplierText();
            scoreMultiplierText.setColor(scoreMultiplierText.getColor().r, scoreMultiplierText.getColor().g, scoreMultiplierText.getColor().b, 1);
            gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getMyProgressBar().setAlpha(1);
            Image pauseSymbol = gameplayScreen.getPauseStuff().getPauseSymbol();
            pauseSymbol.setColor(pauseSymbol.getColor().r, pauseSymbol.getColor().g, pauseSymbol.getColor().b, 1);
            gameplayScreen.getBulletsHandler().setBulletsPerAttack(D_SURVIVAL_BULLETS_INITIAL_NO_PER_ATTACK);
            gameplayScreen.getBulletsHandler().nullifyCurrentWaveLastBullet();
            gameplayScreen.getBulletsHandler().resetCurrentSpeedMultiplier();
            gameplayScreen.getBulletsHandler().newWave(false, true);
            gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().startMyProgressBarTween();
            /*switch (gameplayScreen.getGameplayMode()) {
                case SURVIVAL:
                    gameplayScreen.getBulletsHandler().startSurvivalDifficultyTweens();
                    gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().survival();
                    break;
                case CRYSTAL:
                    gameplayScreen.getBulletsHandler().startCrystalDifficultyTweens();
                    gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().crystal();
                    break;
            }*/
            //gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getSurvival_scoreMultiplierTween().start();
            //gameplayScreen.getScoreTimerStuff().getScoreMultiplierDifficultyLevelStuff().getMyProgressBarTween().start();
            gameplayScreen.getGameOverLayer().disappearToStartANewGame();
            gameplayScreen.getStarsContainer().setBaseRadialVelocity(0);
            gameplayScreen.getStarsContainer().resetCurrentSpeed();
            //gameplayScreen.getShieldsAndContainersHandler().setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);

            gameplayScreen.getStarsContainer().getGlassCrackPostProcessingEffect().clearRefractionBuffers();

            gameplayScreen.getTempProgressBar().positionCentre();
            // gameplayScreen.showTempProgressBar();

            gameplayScreen.getLevelFinishStuff().getFinishText().setVisible(false);


            if (gameplayScreen.getControllerLeft().getColor().a < 1)
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, 0.2f));
            if (gameplayScreen.getControllerRight().getColor().a < 1)
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, 0.2f));


            BulletsAndShieldContainer[] allContainers = gameplayScreen.getBulletsAndShieldContainers();
            for (BulletsAndShieldContainer container : allContainers) {
                container.cleanContainer();
            }

        }
    }
}