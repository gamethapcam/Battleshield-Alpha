package com.yaamani.battleshield.alpha.Game.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.Game.SolidBG;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LoadingScreen extends AdvancedScreen {

    //private Image bg;
    private SolidBG solidBG;
    private Image logo;

    public LoadingScreen(AdvancedStage game, boolean transform) {
        super(game, transform);

        solidBG.instance.setColor(LOADING_BG_COLOR_R, LOADING_BG_COLOR_G, LOADING_BG_COLOR_B);

        //bg = new Image(Assets.instance.loadingScreenAssets.bg);
        //bg.setLayoutEnabled(false); //for performance.
        //addActor(bg);

        logo = new Image(Assets.instance.loadingScreenAssets.logo);
        //logo.setLayoutEnabled(false);//for performance.
        addActor(logo);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        super.resize(width, height, worldWidth, worldHeight);

        /*bg.setSize(worldWidth, worldHeight);
        bg.setPosition(0, 0);*/

        logo.setSize(LOGO_WIDTH, LOGO_HEIGHT);
        logo.setPosition(worldWidth/2f - logo.getWidth()/2f, worldHeight/2f - logo.getHeight()/2f);
    }

    public void setLogosAlpha(float a) {
        logo.setColor(1, 1, 1, a);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
