package com.yaamani.battleshield.alpha.MyEngine;

public class SimplestTransition extends Transition {

    public SimplestTransition(AdvancedStage game, AdvancedScreen out, AdvancedScreen in) {
        super(game,  out, in);
    }

    @Override
    public void onUpdate(float delta) {
        end();
    }
}
