package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.utils.Array;

public abstract class Transition implements Updatable {

    private Array<Tween> tweens;

    private boolean inProgress = false;
    private boolean automaticallyEndAfterAllTweensFinish = false;

    protected AdvancedScreen out;
    protected AdvancedScreen in;

    public Transition(AdvancedStage game, AdvancedScreen out, AdvancedScreen in, boolean automaticallyEndAfterAllTweensFinish) {
        this(game, out, in);
        this.automaticallyEndAfterAllTweensFinish = automaticallyEndAfterAllTweensFinish;
    }

    public Transition(AdvancedStage game, AdvancedScreen out, AdvancedScreen in) {
        game.addTransition(this);
        if (out != null) this.out = out;
        this.in = in;
    }

    @Override
    public final void update(float delta) {
        onUpdate(delta);

        if (tweens != null) {
            int numberOfFinishedTweens = 0;

            for (int i = 0; i < tweens.size; i++) {
                tweens.get(i).update(delta);
                if (tweens.get(i).isFinished()) numberOfFinishedTweens++;
            }

            if (numberOfFinishedTweens == tweens.size & automaticallyEndAfterAllTweensFinish) end();
        }
    }

    public void onUpdate(float delta) {

    }

    public void onSwitch(float worldWidth, float worldHeight) {

    }

    void start() {
        inProgress = true;
        in.setVisible(true);
        in.show();
        onStart();
    }

    public void onStart() {

    }

    public final boolean isInProgress() {
        return inProgress;
    }

    protected void end() {
        inProgress = false;
        if (out != null) {
            out.hide();
            out.setVisible(false);
        }
    }

    public AdvancedScreen getOut() {
        return out;
    }

    public AdvancedScreen getIn() {
        return in;
    }

    public boolean isAutomaticallyEndAfterAllTweensFinish() {
        return automaticallyEndAfterAllTweensFinish;
    }

    public void setAutomaticallyEndAfterAllTweensFinish(boolean automaticallyEndAfterAllTweensFinish) {
        this.automaticallyEndAfterAllTweensFinish = automaticallyEndAfterAllTweensFinish;
    }

    public Tween[] getTweens() {
        return tweens.items;
    }

    public void addTween(Tween tween) {
        if (tweens == null) tweens = new Array<Tween>(Tween.class);
        tweens.add(tween);
    }
}
