package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LevelFinishStuff implements Resizable, Updatable {

    private static final String TAG = LevelFinishStuff.class.getSimpleName();

    private GameplayScreen gameplayScreen;

    private SimpleText finishText;
    private Tween finishTextTween;

    private SimpleText congratsText; //To be changed

    private NetworkAndStorageManager networkAndStorageManager;

    public LevelFinishStuff(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;

        initializeFinishText();
        initializeFinishTextTween();

        initializeCongratsText();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        calculateFinishTextPosition(worldWidth, worldHeight);
        congratsText.setPosition(worldWidth/2f - congratsText.getWidth()/2f, WORLD_SIZE * 0.2f);
    }

    @Override
    public void update(float delta) {
        finishTextTween.update(delta);
    }




    private void calculateFinishTextPosition(float worldWidth, float worldHeight) {
        finishText.setPosition(worldWidth/2f - finishText.getWidth()/2f, worldHeight/2f - finishText.getHeight()/2f);
    }





    public SimpleText getFinishText() {
        return finishText;
    }

    public Tween getFinishTextTween() {
        return finishTextTween;
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
    }






    private void initializeFinishText() {
        finishText = new SimpleText(gameplayScreen.getMyBitmapFont(), "Finish");
        finishText.setHeight(LEVEL_FINISH_FINISH_TEXT_FINAL_HEIGHT);
        finishText.setColor(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1.f);
        gameplayScreen.addActor(finishText);
        finishText.setVisible(false);


        finishText.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                Gdx.app.log(TAG, "Clicked finish.");
                finishText.setVisible(false);

                congratsText.setVisible(true);
                int len = gameplayScreen.getBulletsHandler().getActiveBullets().size;
                float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
                float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
                for (int i = len-1; i >= 0; i--) {
                    Bullet bullet = gameplayScreen.getBulletsHandler().getActiveBullets().get(i);
                    bullet.stopUsingTheBullet(worldWidth, worldHeight, true);
                }

                if (networkAndStorageManager != null)
                    if (networkAndStorageManager.isSaveControllerValuesModeEnabled())
                        networkAndStorageManager.saveTheMostRecentEntries();
            }
        });
    }

    private void initializeFinishTextTween() {
        finishTextTween = new Tween(LEVEL_FINISH_FINISH_TEXT_TWEEN_DURATION, LEVEL_FINISH_FINISH_TEXT_TWEEN_INTERPOLATION) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float a = interpolation.apply(LEVEL_FINISH_FINISH_TEXT_INITIAL_ALPHA, LEVEL_FINISH_FINISH_TEXT_FINAL_ALPHA, percentage);
                finishText.setColor(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, a);

                float height = interpolation.apply(LEVEL_FINISH_FINISH_TEXT_INITIAL_HEIGHT, LEVEL_FINISH_FINISH_TEXT_FINAL_HEIGHT, percentage);
                finishText.setHeight(height);
                float worldWidth = gameplayScreen.getStage().getViewport().getWorldWidth();
                float worldHeight = gameplayScreen.getStage().getViewport().getWorldHeight();
                calculateFinishTextPosition(worldWidth, worldHeight);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING)
                    finishTextTween.start();
            }
        };

        gameplayScreen.addToFinishWhenStoppingTheGameplay(finishTextTween);
    }

    private void initializeCongratsText() {
        congratsText = new SimpleText(gameplayScreen.getMyBitmapFont(), "Congrats");
        congratsText.setHeight(WORLD_SIZE / 6f);
        congratsText.setColor(1, 1, 1, 1.f);
        gameplayScreen.addActor(congratsText);
        congratsText.setVisible(false);
    }
}
