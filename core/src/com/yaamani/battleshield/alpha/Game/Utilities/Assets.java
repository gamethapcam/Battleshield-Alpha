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
        //this.assetManager.load("a", String.class);
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
    
    public TextureAtlas.AtlasRegion findRegion(TextureAtlas atlas, String name) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        if (region == null)
            Gdx.app.error(TAG, "No Region found by the name \"" + name + "\".");
        return region;
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
                logo = findRegion(atlas, ASSETS_LOGO);
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
        public final TextureRegion survival;
        public final TextureRegion restricted;
        public final TextureRegion free;
        public final TextureRegion t1;

        private MainMenuAssets(TextureAtlas atlas) {
            mountain = findRegion(atlas, ASSETS_MOUNTAIN);
            treeBack = findRegion(atlas, ASSETS_TREE_BACK);
            treeFront = findRegion(atlas, ASSETS_TREE_FRONT);
            frontGrass = findRegion(atlas, ASSETS_FRONT_GRASS);
            tallGrass = findRegion(atlas, ASSETS_TALL_GRASS);
            manyTrees = findRegion(atlas, ASSETS_MANY_TREES);
            start = findRegion(atlas, ASSETS_START);
            survival = findRegion(atlas, ASSETS_SURVIVAL);
            restricted = findRegion(atlas, ASSETS_RESTRICTED);
            free = findRegion(atlas, ASSETS_FREE);
            t1 = findRegion(atlas, ASSETS_T1);
        }

    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class GameplayAssets {

        public final TextureRegion freeControllerBG;
        public final TextureRegion restrictedControllerRightBG;
        public final TextureRegion restrictedControllerLeftBG;
        public final TextureRegion controllerStick;

        public final TextureRegion turret;

        public final TextureRegion bullet;
        public final TextureRegion plusBullet;
        public final TextureRegion minusBullet;
        public final TextureRegion heartBullet;
        public final TextureRegion bombBullet;
        public final TextureRegion starBullet;
        public final TextureRegion questionMarkBullet;
        public final TextureRegion shieldDisablingBullet;
        public final TextureRegion mirrorBullet;
        public final TextureRegion fasterDizzinessRotationBullet;
        public final TextureRegion armorBullet;
        public final TextureRegion twoPortalExits;

        public final TextureRegion armorHalo;
        public final TextureRegion lazerGun;
        public final TextureRegion armorBlack;
        public final TextureRegion armorGlowing;
        public final TextureRegion lazerBeam;
        public final TextureRegion lazerGlow;

        public final TextureRegion portalEntrance;
        public final TextureRegion portalExit;

        public final TextureRegion pauseText;
        public final TextureRegion pauseRestart;
        public final TextureRegion pauseResume;
        public final TextureRegion pauseHome;
        public final TextureRegion pause1;
        public final TextureRegion pause2;
        public final TextureRegion pause3;
        public final TextureRegion pauseSymbol;
        public final TextureRegion dimmingOverlay;

        private final int shieldsArrayLength = (int) ((SHIELDS_SAVING_TO_ANGLE - SHIELDS_SAVING_FROM_ANGLE) / SHIELDS_SKIP_ANGLE_WHEN_SAVING) + 1;
        public final TextureRegion[] shieldsWithVariousAngles = new TextureRegion[shieldsArrayLength];

        public final TextureRegion healthBar;
        /*private final int healthBarArrayLength = (int) ((HEALTH_BAR_SAVING_TO_ANGLE - HEALTH_BAR_SAVING_FROM_ANGLE) / (float) HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING) + 1;
        public final TextureRegion[] healthBarWithVariousAngles = new TextureRegion[healthBarArrayLength];*/

        public final TextureRegion gameOverBG;

        private GameplayAssets(TextureAtlas atlas) {
            //Gdx.app.log(TAG, "" + shieldsArrayLength);
            freeControllerBG = findRegion(atlas, ASSETS_FREE_CONTROLLER_BG);
            restrictedControllerRightBG = findRegion(atlas, ASSETS_RESTRICTED_CONTROLLER_RIGHT_BG);
            restrictedControllerLeftBG = findRegion(atlas, ASSETS_RESTRICTED_CONTROLLER_LEFT_BG);
            controllerStick = findRegion(atlas, ASSETS_CONTROLLER_STICK);

            turret = findRegion(atlas, ASSETS_TURRET);

            bullet = findRegion(atlas, ASSETS_BULLET);
            plusBullet = findRegion(atlas, ASSETS_PLUS_BULLET);
            minusBullet = findRegion(atlas, ASSETS_MINUS_BULLET);
            heartBullet = findRegion(atlas, ASSETS_HEART_BULLET);
            bombBullet = findRegion(atlas, ASSETS_BOMB_BULLET);
            starBullet = findRegion(atlas, ASSETS_STAR_BULLET);
            questionMarkBullet = findRegion(atlas, ASSETS_QUESTION_MARK_BULLET);
            shieldDisablingBullet = findRegion(atlas, ASSETS_SHIELD_DISABLING_BULLET);
            mirrorBullet = findRegion(atlas, ASSETS_MIRROR_BULLET);
            fasterDizzinessRotationBullet = findRegion(atlas, ASSETS_FASTER_DIZZINESS_ROTATION);
            armorBullet = findRegion(atlas, ASSETS_ARMOR_BULLET);
            twoPortalExits = findRegion(atlas, ASSETS_2_PORTAL_EXITS);

            armorHalo = findRegion(atlas, ASSETS_ARMOR_HALO);
            lazerGun = findRegion(atlas, ASSETS_LAZER_GUN);
            armorBlack = findRegion(atlas, ASSETS_ARMOR_BLACK);
            armorGlowing = findRegion(atlas, ASSETS_ARMOR_GLOWING);
            lazerBeam = findRegion(atlas, ASSETS_LAZER_BEAM);
            lazerGlow = findRegion(atlas, ASSETS_LAZER_GLOW);

            portalEntrance = findRegion(atlas, ASSETS_PORTALS_ENTRANCE);
            portalExit = findRegion(atlas, ASSETS_PORTALS_EXIT);

            pauseText = findRegion(atlas, ASSETS_PAUSE_TEXT);
            pauseRestart = findRegion(atlas, ASSETS_PAUSE_RESTART);
            pauseResume = findRegion(atlas, ASSETS_PAUSE_RESUME);
            pauseHome = findRegion(atlas, ASSETS_PAUSE_HOME);
            pause1 = findRegion(atlas, ASSETS_PAUSE_1);
            pause2 = findRegion(atlas, ASSETS_PAUSE_2);
            pause3 = findRegion(atlas, ASSETS_PAUSE_3);
            pauseSymbol = findRegion(atlas, ASSETS_PAUSE_SYMBOL);
            dimmingOverlay = findRegion(atlas, ASSETS_DIMMING_OVERLAY);

            initializeShieldsWithVariousAngles(atlas);

            //initializeHealthBarWithVariousAngles(atlas);
            healthBar = findRegion(atlas, ASSETS_HEALTH_BAR);

            gameOverBG = findRegion(atlas, ASSETS_GAME_OVER_BG);

        }

        private void initializeShieldsWithVariousAngles(TextureAtlas atlas) {
            for (int i = 0; i < shieldsArrayLength; i++) {
                String name = SHIELDS_NAMING_WHEN_SAVING + (int) (SHIELDS_SAVING_FROM_ANGLE + SHIELDS_SKIP_ANGLE_WHEN_SAVING*i);
                shieldsWithVariousAngles[i] = findRegion(atlas, name);
            }
        }

        /*private void initializeHealthBarWithVariousAngles(TextureAtlas atlas) {
            for (int i = 0; i < healthBarArrayLength; i++) {
                String name = HEALTH_BAR_NAMING_WHEN_SAVING + (HEALTH_BAR_SAVING_FROM_ANGLE + HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING*i);
                healthBarWithVariousAngles[i] = findRegion(atlas, name);
            }
        }*/

    }
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public class MutualAssets {

        public final TextureRegion star;
        public final TextureRegion starGlow;

        public final TextureRegion font;

        public final TextureRegion bigCircle;

        private MutualAssets(TextureAtlas atlas) {
            star = findRegion(atlas, ASSETS_STAR);
            starGlow = findRegion(atlas, ASSETS_STAR_GLOW);


            font = findRegion(atlas, ASSETS_FONT);

            bigCircle = findRegion(atlas, ASSETS_BIG_CIRCLE);
        }
    }

}
