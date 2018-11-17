package com.yaamani.battleshield.alpha.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.LoadingScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Transitions.LoadingToMainMenu;
import com.yaamani.battleshield.alpha.Game.Transitions.MainMenuToGameplay;
import com.yaamani.battleshield.alpha.MyEngine.SimplestTransition;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.MyEngine.Timer;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.*;

public class BattleshieldGame extends AdvancedApplicationAdapter {

    public static final String TAG = BattleshieldGame.class.getSimpleName();

    private StarsContainer starsContainer;

    private LoadingScreen loadingScreen;
    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;

    private SimplestTransition loadingAppear;
    private LoadingToMainMenu loadingToMainMenu;
    private MainMenuToGameplay mainMenuToGameplay;

    private BitmapFont font;

    @Override
	public void create () {
        initializeStage(new ExtendViewport(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE*MAX_ASPECT_RATIO_SUPPORTED, WORLD_SIZE),
                        new SpriteBatch(),
                false);
        game.getBatch().enableBlending();
        game.getRoot().setTransform(false); // for performance. ... if I'm not scaling or rotating then there's no need for transform to be true.

        Assets.instance.init(new AssetManager(), game);

        initializeLoadingScreen();

        saveProgrammaticallyGeneratedTextures(1080);
    }

    @Override
    public void render () {
        cycleAspectRatios();

        initializeWhenAssetsReady();

        SolidBG.instance.draw();

        super.render();

        //Gdx.app.log("FPS =", "" + 1f/Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (width != 0 && height != 0) {
            String title = "Battleshield ALPHA | Aspect ratio = " + MyMath.ratio(width, height);
            float ratio = (float) width / (float) height;

            if (!(ratio == 4f/3f || ratio == 16f/9f || ratio == 19.5f/9f || ratio == 21.5f/9f)) Gdx.graphics.setTitle(title);
        }

        if (starsContainer != null)
            starsContainer.resize(game.getViewport().getWorldWidth(), game.getViewport().getWorldHeight());
    }

    @Override
    public void pause() {
        super.pause();
        //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameplayScreen != null)
            if (gameplayScreen.getScore() != null)
                    gameplayScreen.getScore().registerBestScoreToHardDrive();
    }

    @Override
    public void resume() {
        super.resume();
        //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        if (starsContainer != null) starsContainer.dispose();
        //gameplayScreen.getScore().registerBestScoreToHardDrive();
        super.dispose();
        Assets.instance.dispose();
    }

    public MainMenuToGameplay getMainMenuToGameplay() {
        return mainMenuToGameplay;
    }

    // ------------------ privates ------------------

    private void cycleAspectRatios() {
	    int height = 630;
	    int width = 0;

	    String note = "";

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            width = (int) (height * (4f/3f)); // Narrowest
            note = " (NARROWEST)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            width = (int) (height * (16f/9f)); // Most common
            note = " (MOST COMMON)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            width = (int) (height * (19.5f/9f)); // Widest
            note = "=19.5:9 (WIDEST Till Now)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            width = (int) (height * (21.5f/9f)); // Widest
            note = "=21.5:9 (WIDEST Supported)";
        }

        String title = "Battleshield ALPHA | Aspect ratio = " + MyMath.ratio(width, height);

        if (width == 0) return;

        Gdx.graphics.setWindowedMode(width	, height);
        Gdx.graphics.setTitle(title + note);

    }

    private void initializeLoadingScreen() {
        loadingScreen = new LoadingScreen(game, false);
        loadingAppear = new SimplestTransition(game, null, loadingScreen);
        game.switchScreens(loadingAppear);
    }

    private boolean initialized = false;
    private void initializeWhenAssetsReady() {
        if (initialized) return;
        if (Assets.instance.getAssetManager().isLoaded(ASSETS_ALL)) {
            initialized = true;

            initializeFont();

            initializeStarsContainer();

            mainMenuScreen = new MainMenuScreen(game, false);
            gameplayScreen = new GameplayScreen(game, font, starsContainer, false);

            starsContainer.setBulletsHandler(gameplayScreen.getBulletsHandler());

            loadingToMainMenu = new LoadingToMainMenu(game, loadingScreen, mainMenuScreen, starsContainer);
            mainMenuToGameplay = new MainMenuToGameplay(game, mainMenuScreen, gameplayScreen);
            mainMenuScreen.setMainMenuToGameplay(mainMenuToGameplay);

            game.switchScreens(loadingToMainMenu);

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private void initializeStarsContainer() {
        starsContainer = new StarsContainer(game);
        game.addActor(starsContainer);
    }

    private void initializeFont() {
        font = new BitmapFont(Gdx.files.internal(ASSETS_FONT_FNT_INTERNAL), Assets.instance.mutualAssets.font);
        font.setUseIntegerPositions(false); // When scaling to very small sizes, calling this func and passing false is a necessity (I SPENT ABOUT 4 HOURS GOOGLING TO KNOW THIS)
        //font.getData().setScale(0.14f);
    }

    private void saveProgrammaticallyGeneratedTextures(int targetResolution) {
        /*saveShieldsWithVariousAngles(SHIELDS_SAVING_FROM_ANGLE,
                SHIELDS_SAVING_TO_ANGLE,
                SHIELDS_SKIP_ANGLE_WHEN_SAVING,
                SHIELDS_FREE_ANGLE,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/ShieldsWithVariousAngles",
                targetResolution);*/

        //saveTurret(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay");

        //saveBullet(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/");

        /*saveHealthBarWithVariousAngles(HEALTH_BAR_SAVING_FROM_ANGLE,
                HEALTH_BAR_SAVING_TO_ANGLE,
                HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING,
                targetResolution,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/HealthBarWithVariousAngles");*/
    }
}
