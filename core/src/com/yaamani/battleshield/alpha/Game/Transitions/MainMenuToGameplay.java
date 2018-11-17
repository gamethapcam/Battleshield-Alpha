package com.yaamani.battleshield.alpha.Game.Transitions;

import com.badlogic.gdx.Gdx;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.Transition;

public class MainMenuToGameplay extends Transition {
    public MainMenuToGameplay(AdvancedStage game, AdvancedScreen out, AdvancedScreen in) {
        super(game, out, in);

        //setAutomaticallyEndAfterAllTweensFinish(true);
    }

    @Override
    public void onUpdate(float delta) {
        end();
    }

    @Override
    public void onSwitch(float worldWidth, float worldHeight) {
        in.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldWidth, worldHeight);
    }
}
