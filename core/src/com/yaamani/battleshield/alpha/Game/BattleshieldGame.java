package com.yaamani.battleshield.alpha.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.LoadingScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Transitions.LoadingToMainMenu;
import com.yaamani.battleshield.alpha.Game.Transitions.MainMenuToGameplay;
import com.yaamani.battleshield.alpha.Game.Utilities.AndroidPermissionHandler;
import com.yaamani.battleshield.alpha.Game.Utilities.OnPermissionResult;
import com.yaamani.battleshield.alpha.MyEngine.InstaBugs;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.SimplestTransition;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedApplicationAdapter;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.saveShieldsWithVariousAngles;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class BattleshieldGame extends AdvancedApplicationAdapter implements OnPermissionResult {

    public static final String TAG = BattleshieldGame.class.getSimpleName();

    private AndroidPermissionHandler androidPermissionHandler;

    private StarsContainer starsContainer;

    private LoadingScreen loadingScreen;
    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;

    private SimplestTransition loadingAppear;
    private LoadingToMainMenu loadingToMainMenu;
    private MainMenuToGameplay mainMenuToGameplay;
    private SimplestTransition gameplayToMainMenu;

    private NetworkAndStorageManager networkAndStorageManager;
    private Image saving;


    private MyBitmapFont myBitmapFont;
    private BitmapFont font;

    //private TweenAndMyTweenTesting tweenAndMyTweenTesting;

    private boolean destroyed;


    public BattleshieldGame() {

    }

    public BattleshieldGame(AndroidPermissionHandler androidPermissionHandler) {
        this.androidPermissionHandler = androidPermissionHandler;
    }

    @Override
	public void create () {


        InstaBugs.INSTANCE.initialize();


        initializeStage(new ExtendViewport(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE*MAX_ASPECT_RATIO_SUPPORTED, WORLD_SIZE),
                        new SpriteBatch(),
                false);
        game.getBatch().enableBlending();
        game.getRoot().setTransform(false); // for performance. ... if I'm not scaling or rotating then there's no need for transform to be true.

        Assets.instance.init(new AssetManager(), game);

        initializeLoadingScreen();


        //Gdx.app.log(TAG, "" + ((BULLETS_ORDINARY_HEIGHT+BULLETS_DISTANCE_BETWEEN_TWO)*D_CRYSTAL_BULLETS_INITIAL_NO_PER_ATTACK/D_SURVIVAL_BULLETS_SPEED_INITIAL));

        //saveProgrammaticallyGeneratedTextures(1080);

        //tweenAndMyTweenTesting = new TweenAndMyTweenTesting(game);

        /*Gdx.app.log(TAG, "" + MyMath.arrayToString(MyMath.gaussianOffsetsLinearlySampledTexture(11)));
        Gdx.app.log(TAG, "" + MyMath.arrayToString(MyMath.gaussianWeightsLinearlySampledTexture(11)));
        Gdx.app.log(TAG, "" + MyMath.arrayToString(MyMath.gaussianWeights(11)));*/

        //new ResumeGraduallyTesting().compareFunctions(MyInterpolation.myExp10, 1000, 10, 50, 100, 1, 4, 0.001f, 5, 2);

        destroyed = false;

        //throw new RuntimeException("Intentional Exception");
    }

    @Override
    public void render () {
        //cycleAspectRatios();

        initializeWhenAssetsReady();

        //if (gameplayScreen != null) {
            //Gdx.app.log(TAG, "" + gameplayScreen.getStarsContainer().isInWarpTrailAnimation());
            //if (!gameplayScreen.getStarsContainer().isInWarpTrailAnimation())
                SolidBG.instance.draw();
        //} else
            //SolidBG.instance.draw();

        if (networkAndStorageManager != null) {

            networkAndStorageManager.render();

            if (networkAndStorageManager.isSaveControllerValuesModeEnabled()) {
                if (networkAndStorageManager.isCurrentlyWritingToStorage())
                    saving.setVisible(true);
                else
                    saving.setVisible(false);
            }
        }



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

        if (networkAndStorageManager != null)
            networkAndStorageManager.resize(width, height, game.getViewport().getWorldWidth(), game.getViewport().getWorldHeight());

        //tweenAndMyTweenTesting.resize(width, height, game.getViewport().getWorldWidth(), game.getViewport().getWorldHeight());
    }



    @Override
    public void pause() {
        super.pause();
        //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        /*if (gameplayScreen != null)
            if (gameplayScreen.getScoreStuff() != null)
                    gameplayScreen.getScoreStuff().registerBestScoreToHardDrive();*/
    }

    @Override
    public void resume() {
        super.resume();
        //resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        /*if (gameplayScreen != null)
            if (gameplayScreen.getScoreStuff() != null)
                gameplayScreen.getScoreStuff().registerBestScoreToHardDrive();*/


        if (starsContainer != null) starsContainer.dispose();
        //gameplayScreen.getScoreTimerStuff().registerBestScoreToHardDrive();
        super.dispose();
        if (networkAndStorageManager != null) networkAndStorageManager.dispose();
        Assets.instance.dispose();

        //Gdx.app.log(TAG, "totalAttachedBullets = " + Bullet.totalAttachedBullets);

        destroyed = true;
        Gdx.app.log(TAG, "DESTROYED !!!!!!!!! DESTROYED !!!!!!!!! DESTROYED !!!!!!!!! DESTROYED !!!!!!!!!");
    }

    public MainMenuToGameplay getMainMenuToGameplay() {
        return mainMenuToGameplay;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    // ------------------ privates ------------------

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

            gameplayScreen = new GameplayScreen(game, myBitmapFont, starsContainer, false);
            mainMenuScreen = new MainMenuScreen(game, androidPermissionHandler, myBitmapFont, gameplayScreen, starsContainer, false);

            starsContainer.setBulletsHandler(gameplayScreen.getBulletsHandler());
            starsContainer.setGameplayScreen(gameplayScreen);

            loadingToMainMenu = new LoadingToMainMenu(game, loadingScreen, mainMenuScreen, starsContainer);
            mainMenuToGameplay = new MainMenuToGameplay(game, mainMenuScreen, gameplayScreen);
            gameplayToMainMenu = new SimplestTransition(game, gameplayScreen, mainMenuScreen);

            mainMenuScreen.setMainMenuToGameplay(mainMenuToGameplay);
            gameplayScreen.setGameplayToMainMenu(gameplayToMainMenu);

            game.switchScreens(loadingToMainMenu);

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            networkAndStorageManager = new NetworkAndStorageManager(mainMenuScreen, gameplayScreen, myBitmapFont);
            mainMenuScreen.setNetworkAndStorageManager(networkAndStorageManager);
            gameplayScreen.setNetworkAndStorageManager(networkAndStorageManager);

            saving = new Image(Assets.instance.mutualAssets.bigCircle);
            saving.setSize(5, 5);
            saving.setPosition(2, 2);
            saving.setVisible(false);
            game.addActor(saving);
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

        myBitmapFont = new MyBitmapFont(Gdx.files.internal(ASSETS_FONT_FNT_INTERNAL), Assets.instance.mutualAssets.font);
    }

    private void saveProgrammaticallyGeneratedTextures(int targetResolution) {
        saveShieldsWithVariousAngles(SHIELDS_SAVING_FROM_ANGLE,
                SHIELDS_SAVING_TO_ANGLE,
                SHIELDS_SKIP_ANGLE_WHEN_SAVING,
                SHIELDS_FREE_ANGLE,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Unpacked Assets/1080p/Gameplay/ShieldsWithVariousAngles",
                targetResolution);

        //saveRestrictedControllerBG(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Unpacked Assets/1080p/Gameplay");

        //saveTurret(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay");

        //saveBullet(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/");

        /*saveHealthBarWithVariousAngles(HEALTH_BAR_SAVING_FROM_ANGLE,
                HEALTH_BAR_SAVING_TO_ANGLE,
                HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING,
                targetResolution,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/HealthBarWithVariousAngles");*/

        Gdx.app.log(TAG, "Programmatically Generated Textures Saved.");
    }

    @Override
    public void permissionGranted(int requestCode) {
        mainMenuScreen.permissionGranted(requestCode);
    }

    @Override
    public void permissionDenied(int requestCode) {
        mainMenuScreen.permissionDenied(requestCode);
    }
}
