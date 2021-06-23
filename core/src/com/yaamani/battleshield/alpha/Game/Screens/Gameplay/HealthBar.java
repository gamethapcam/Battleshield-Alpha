package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Arch;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthBar extends Arch implements Resizable {

    public HealthBar(GameplayScreen gameplayScreen) {
        super(Assets.instance.gameplayAssets.healthBar,
                AngleIncreaseDirection.CLOCKWISE,
                HEALTH_BAR_RADIUS, 0);

        gameplayScreen.getAllGameplayStuff().addActor(this);

        setInnerRadiusRatio(HEALTH_BAR_INNER_RADIUS_RATIO);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        setPosition(worldWidth / 2f - getWidth() / 2f, worldHeight / 2f - getHeight() / 2f);
    }

    @Override
    public void setAngle(float angleRad) {
        if (angleRad > MathUtils.PI2) setColor(HEALTH_BAR_HEALTH_OVER_FLOW_COLOR);
        else if (angleRad <= HEALTH_BAR_DANGEROUS_ANGLE) setColor(HEALTH_BAR_DANGEROUS_ANGLE_COLOR);
        else setColor(HEALTH_BAR_COLOR);

        super.setAngle(angleRad);
    }
}
