package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.FreeGameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.Arch;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class HealthBar extends Arch implements Resizable {

    public HealthBar(GameplayScreen gameplayScreen) {
        super(Assets.instance.gameplayAssets.turret,
                AngleIncreaseDirection.CLOCKWISE,
                HEALTH_BAR_RADIUS);

        gameplayScreen.addActor(this);

        setInnerRadius(HEALTH_BAR_INNER_RADIUS);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        setPosition(worldWidth / 2f - getWidth() / 2f, worldHeight / 2f - getHeight() / 2f);
    }

    @Override
    public void setAngle(float angle) {
        if (angle > MathUtils.PI2) setColor(HEALTH_BAR_HEALTH_OVER_FLOW_COLOR);
        else if (angle <= HEALTH_BAR_DANGEROUS_ANGLE) setColor(HEALTH_BAR_DANGEROUS_ANGLE_COLOR);
        else setColor(HEALTH_BAR_COLOR);

        super.setAngle(angle);
    }
}
