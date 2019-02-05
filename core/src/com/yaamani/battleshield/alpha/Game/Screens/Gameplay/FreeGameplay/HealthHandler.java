package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Bullet;
import com.yaamani.battleshield.alpha.MyEngine.OneBigSizeBitmapFontTextField;

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
        gameplayScreen.getBulletsHandler().getCurrentBulletsWaveTimer().finish();

        float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
        float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
        int len = gameplayScreen.getActiveBullets().size;
        for (int i = len-1; i >= 0; i--) {
            Bullet bullet = gameplayScreen.getActiveBullets().get(i);
            bullet.stopUsingTheBullet(worldWidth, worldHeight);
            gameplayScreen.getScore().getFadeOutTween().start();
        }

        gameplayScreen.getScore().updateBestScoreButDontRegisterToHardDriveYet();
        gameplayScreen.getGameOverLayer().thePlayerLost();
        gameplayScreen.getStarsContainer().setThetaForRadialTween(5 * MathUtils.degreesToRadians);
    }

    public void newGame() {
        if (gameplayScreen.getState() == GameplayScreen.State.LOST) {
            gameplayScreen.resize(Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    gameplayScreen.getStage().getViewport().getWorldWidth(),
                    gameplayScreen.getStage().getViewport().getWorldHeight());

            gameplayScreen.setState(GameplayScreen.State.PLAYING);
            setHealth(1f);
            gameplayScreen.getScore().resetScore();
            OneBigSizeBitmapFontTextField scoreText = gameplayScreen.getScore().getScoreText();
            scoreText.setColor(scoreText.getColor().r, scoreText.getColor().g, scoreText.getColor().b, 1);
            gameplayScreen.getBulletsHandler().setBulletsPerAttack(BULLETS_DEFAULT_NO_PER_ATTACK);
            gameplayScreen.getBulletsHandler().newWave();
            gameplayScreen.getBulletsHandler().getDecreaseBulletsPerAttackTimer().start();
            gameplayScreen.getGameOverLayer().disappearToStartANewGame();
            gameplayScreen.getStarsContainer().setThetaForRadialTween(0);
            gameplayScreen.getShieldsAndContainersHandler().setActiveShieldsNum(SHIELDS_ACTIVE_DEFAULT);
        }
    }
}