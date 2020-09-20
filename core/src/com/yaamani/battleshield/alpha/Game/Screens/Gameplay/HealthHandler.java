package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.TweenedFloat;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Iterator;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthHandler implements Updatable {

    private GameplayScreen gameplayScreen;

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
        gameplayScreen.addToFinishWhenLosing(health.getTweenObj());

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

    public void affectHealthBy(float by, float durationMillis, Interpolation interpolation) {
        float healthFinishedValue = health.getCurrentFinalValue();
        setHealthValueTo(healthFinishedValue + by, durationMillis, interpolation);
    }

    public void affectHealthBy(float by) {
        affectHealthBy(by, HEALTH_BAR_TWEEN_DURATION, HEALTH_BAR_TWEEN_INTERPOLATION);
    }

    public void playerLost() {
        gameplayScreen.setState(GameplayScreen.State.LOST);
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


        gameplayScreen.getScoreTimerStuff().getFadeOutTween().start();

        gameplayScreen.getPauseStuff().getPauseSymbolFadesOutWhenLosing().start();

        Bullet.setPlusOrMinusExists(false);
        Bullet.setStarExists(false);
        gameplayScreen.setInStarBulletAnimation(false);

        //gameplayScreen.getScoreStuff().updateBestScoreButDontRegisterToHardDriveYet();
        gameplayScreen.getGameOverLayer().thePlayerLost();

        Timer[] pauseWhenPausingFinishWhenLosing = gameplayScreen.getFinishWhenLosing();
        for (int i = 0; i < pauseWhenPausingFinishWhenLosing.length; i++) {
            if (pauseWhenPausingFinishWhenLosing[i] != null)
                pauseWhenPausingFinishWhenLosing[i].finish();
        }

        gameplayScreen.getStarsContainer().setBaseRadialVelocity(-5 * MathUtils.degreesToRadians);

        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setColor(1, 1, 1, 0);
        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setVisible(false);

        gameplayScreen.hideTempProgressBar();

        if (gameplayScreen != null)
            if (gameplayScreen.getScoreTimerStuff() != null)
                gameplayScreen.getScoreTimerStuff().registerBestScoreToHardDrive();


        gameplayScreen.getLevelFinishStuff().getFinishText().setVisible(false);

        gameplayScreen.getLazerAttackStuff().resetLazerStuff();
    }

    public void newGame() {
        if (gameplayScreen.getState() == GameplayScreen.State.LOST) {
            gameplayScreen.resize(Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    gameplayScreen.getStage().getViewport().getWorldWidth(),
                    gameplayScreen.getStage().getViewport().getWorldHeight());

            gameplayScreen.setState(GameplayScreen.State.PLAYING);
            setHealthValueTo(1f);


            gameplayScreen.getScoreTimerStuff().resetScore();
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
            gameplayScreen.getBulletsHandler().newWave();
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

            gameplayScreen.setGameplayMode(gameplayScreen.getGameplayMode());

            if (gameplayScreen.getControllerLeft().getColor().a < 1)
                gameplayScreen.getControllerLeft().addAction(Actions.alpha(1, 0.2f));
            if (gameplayScreen.getControllerRight().getColor().a < 1)
                gameplayScreen.getControllerRight().addAction(Actions.alpha(1, 0.2f));


        }
    }
}