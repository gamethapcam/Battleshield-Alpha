package com.yaamani.battleshield.alpha.Game.Utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public final class Assets implements Disposable, AssetErrorListener, Updatable {

    public static final String TAG = Assets.class.getSimpleName();
    public static final Assets instance = new Assets();
    private AssetManager assetManager;

    public LoadingScreenAssets loadingScreenAssets;
    public MainMenuAssets mainMenuAssets;
    public GameplayAssets gameplayAssets;
    public MutualAssets mutualAssets;


    private Assets() {

    }

    public void init(AssetManager assetManager, AdvancedStage stage){
        this.assetManager = assetManager;
        this.assetManager.setErrorListener(this);
        stage.addUpdatable(this);

        this.assetManager.load(ASSETS_LOGO_ALONE, Texture.class);
        this.assetManager.finishLoading();

        loadingScreenAssets = new LoadingScreenAssets();
        this.assetManager.load(ASSETS_ALL, TextureAtlas.class);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset: " + asset.fileName, throwable);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    private boolean update = true;
    @Override
    public void update(float delta) {
        if (update) this.assetManager.update();
        //Gdx.app.log(TAG, "Loading Assets Progress = " + assetManager.getPercentage());
        if (assetManager.isLoaded(ASSETS_ALL) & update) {
            update = false;
            TextureAtlas atlas = assetManager.get(ASSETS_ALL);
            mainMenuAssets = new MainMenuAssets(atlas);
            mutualAssets = new MutualAssets(atlas);
            gameplayAssets = new GameplayAssets(atlas);
        }
        loadingScreenAssets.update(delta);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class LoadingScreenAssets implements Updatable {

        public TextureRegion logo;

        private LoadingScreenAssets() {
            Texture logoTexture = assetManager.get(ASSETS_LOGO_ALONE);

            logo = new TextureRegion(logoTexture, logoTexture.getWidth(), logoTexture.getHeight());
        }

        @Override
        public void update(float delta) {
            if (assetManager.isLoaded(ASSETS_ALL) & update) {
                TextureAtlas atlas = assetManager.get(ASSETS_ALL);
                logo = atlas.findRegion(ASSETS_LOGO);
            }
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class MainMenuAssets {

        public final TextureRegion mountain;
        public final TextureRegion treeBack;
        public final TextureRegion treeFront;
        public final TextureRegion frontGrass;
        public final TextureRegion tallGrass;
        public final TextureRegion manyTrees;
        public final TextureRegion start;
        public final TextureRegion restricted;
        public final TextureRegion free;

        private MainMenuAssets(TextureAtlas atlas) {
            mountain = atlas.findRegion(ASSETS_MOUNTAIN);
            treeBack = atlas.findRegion(ASSETS_TREE_BACK);
            treeFront = atlas.findRegion(ASSETS_TREE_FRONT);
            frontGrass = atlas.findRegion(ASSETS_FRONT_GRASS);
            tallGrass = atlas.findRegion(ASSETS_TALL_GRASS);
            manyTrees = atlas.findRegion(ASSETS_MANY_TREES);
            start = atlas.findRegion(ASSETS_START);
            restricted = atlas.findRegion(ASSETS_RESTRICTED);
            free = atlas.findRegion(ASSETS_FREE);
        }

    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class GameplayAssets {

        public final TextureRegion controllerBG;
        public final TextureRegion controllerStick;

        public final TextureRegion turret;

        public final TextureRegion bullet;
        public final TextureRegion plusBullet;
        public final TextureRegion minusBullet;
        public final TextureRegion heartBullet;
        public final TextureRegion bombBullet;

        private final int shieldsArrayLength = (int) ((SHIELDS_SAVING_TO_ANGLE - SHIELDS_SAVING_FROM_ANGLE) / SHIELDS_SKIP_ANGLE_WHEN_SAVING) + 1;
        public final TextureRegion[] shieldsWithVariousAngles = new TextureRegion[shieldsArrayLength];

        private final int healthBarArrayLength = (int) ((HEALTH_BAR_SAVING_TO_ANGLE - HEALTH_BAR_SAVING_FROM_ANGLE) / (float) HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING) + 1;
        public final TextureRegion[] healthBarWithVariousAngles = new TextureRegion[healthBarArrayLength];

        public final TextureRegion gameOverBG;

        private GameplayAssets(TextureAtlas atlas) {
            //Gdx.app.log(TAG, "" + shieldsArrayLength);
            controllerBG = atlas.findRegion(ASSETS_CONTROLLER_BG);
            controllerStick = atlas.findRegion(ASSETS_CONTROLLER_STICK);

            turret = atlas.findRegion(ASSETS_TURRET);

            bullet = atlas.findRegion(ASSETS_BULLET);
            plusBullet = atlas.findRegion(ASSETS_PLUS_BULLET);
            minusBullet = atlas.findRegion(ASSETS_MINUS_BULLET);
            heartBullet = atlas.findRegion(ASSETS_HEART_BULLET);
            bombBullet = atlas.findRegion(ASSETS_BOMB_BULLET);

            initializeShieldsWithVariousAngles(atlas);

            initializeHealthBarWithVariousAngles(atlas);

            gameOverBG = atlas.findRegion(ASSETS_GAME_OVER_BG);
        }

        private void initializeShieldsWithVariousAngles(TextureAtlas atlas) {
            for (int i = 0; i < shieldsArrayLength; i++) {
                String name = SHIELDS_NAMING_WHEN_SAVING + (int) (SHIELDS_SAVING_FROM_ANGLE + SHIELDS_SKIP_ANGLE_WHEN_SAVING*i);
                shieldsWithVariousAngles[i] = atlas.findRegion(name);
            }
        }

        private void initializeHealthBarWithVariousAngles(TextureAtlas atlas) {
            for (int i = 0; i < healthBarArrayLength; i++) {
                String name = HEALTH_BAR_NAMING_WHEN_SAVING + (HEALTH_BAR_SAVING_FROM_ANGLE + HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING*i);
                healthBarWithVariousAngles[i] = atlas.findRegion(name);
            }
        }

    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class MutualAssets {

        public final TextureRegion star;

        public final TextureRegion font;

        private MutualAssets(TextureAtlas atlas) {
            star = atlas.findRegion(ASSETS_STAR);
            font = atlas.findRegion(ASSETS_FONT);
        }
    }

}
