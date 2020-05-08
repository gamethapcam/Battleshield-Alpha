package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthHandler {

    private GameplayScreen gameplayScreen;

    private float health; // 0 <= health <= oo

    public HealthHandler(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        setHealth(1);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        float correctedHealth = MathUtils.clamp(health, 0, Float.MAX_VALUE);
        this.health = correctedHealth;

        if (gameplayScreen.getHealthBar() != null)
            gameplayScreen.getHealthBar().setAngle(correctedHealth*MathUtils.PI2);

        if (correctedHealth == 0) {
            playerLost();
        }
    }

    public void playerLost() {
        gameplayScreen.setState(GameplayScreen.State.LOST);
        gameplayScreen.getBulletsHandler().setRoundTurn(null);
        //gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().finish();

        float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
        float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
        int len = gameplayScreen.getBulletsHandler().getActiveBullets().size;
        for (int i = len-1; i >= 0; i--) {
            Bullet bullet = gameplayScreen.getBulletsHandler().getActiveBullets().get(i);
            bullet.stopUsingTheBullet(worldWidth, worldHeight);
        }

        gameplayScreen.getScoreStuff().getFadeOutTween().start();
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

        gameplayScreen.getStarsContainer().setThetaForRadialTween(5 * MathUtils.degreesToRadians);

        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setColor(1, 1, 1, 0);
        gameplayScreen.getWhiteTextureHidesEveryThingSecondStageStarBullet().setVisible(false);


        if (gameplayScreen != null)
            if (gameplayScreen.getScoreStuff() != null)
                gameplayScreen.getScoreStuff().registerBestScoreToHardDrive();
    }

    public void newGame() {
        if (gameplayScreen.getState() == GameplayScreen.State.LOST) {
            gameplayScreen.resize(Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    gameplayScreen.getStage().getViewport().getWorldWidth(),
                    gameplayScreen.getStage().getViewport().getWorldHeight());

            gameplayScreen.setState(GameplayScreen.State.PLAYING);
            setHealth(1f);
            gameplayScreen.getScoreStuff().resetScore();
            //gameplayScreen.getBulletsHandler().resetSpeedResetTime();
            gameplayScreen.getScoreStuff().resetScore();
            SimpleText scoreText = gameplayScreen.getScoreStuff().getScoreText();
            scoreText.setColor(scoreText.getColor().r, scoreText.getColor().g, scoreText.getColor().b, 1);
            SimpleText scoreMultiplierText = gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierText();
            scoreMultiplierText.setColor(scoreMultiplierText.getColor().r, scoreMultiplierText.getColor().g, scoreMultiplierText.getColor().b, 1);
            gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBar().setAlpha(1);
            Image pauseSymbol = gameplayScreen.getPauseStuff().getPauseSymbol();
            pauseSymbol.setColor(pauseSymbol.getColor().r, pauseSymbol.getColor().g, pauseSymbol.getColor().b, 1);
            gameplayScreen.getBulletsHandler().setBulletsPerAttack(BULLETS_DEFAULT_NO_PER_ATTACK);
            gameplayScreen.getBulletsHandler().resetCurrentSpeedMultiplier();
            gameplayScreen.getBulletsHandler().newWave();
            //gameplayScreen.getBulletsHandler().getDecreaseBulletsPerAttackTimer().start();
            gameplayScreen.getBulletsHandler().getBulletsPerAttackNumberDifficultyTween().start();
            //gameplayScreen.getBulletsHandler().getCurrentDifficultyLevelTimer().start();
            gameplayScreen.getBulletsHandler().getBulletSpeedMultiplierDifficultyTween().start();
            //gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().start();
            gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierTween().start();
            gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBarTween().start();
            gameplayScreen.getGameOverLayer().disappearToStartANewGame();
            gameplayScreen.getStarsContainer().setThetaForRadialTween(0);
            gameplayScreen.getStarsContainer().resetCurrentSpeed();
            gameplayScreen.getShieldsAndContainersHandler().setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);

        }
    }
}