package com.yaamani.battleshield.alpha.Game.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.Game.ImprovingControlls.NetworkAndStorageManager;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.LazerAttackStuff;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.SpecialBulletTempProgressBarUI;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.TwoExitPortalUI;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.Game.Utilities.AndroidPermissionHandler;
import com.yaamani.battleshield.alpha.Game.Utilities.OnPermissionResult;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.Game.Transitions.MainMenuToGameplay;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.RowOfActors;

import java.util.Arrays;
import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class MainMenuScreen extends AdvancedScreen implements OnPermissionResult {

    public static final String TAG = MainMenuScreen.class.getSimpleName();

    private AdvancedStage game;

    private AndroidPermissionHandler androidPermissionHandler;

    private Image start;
    private Image survival;
    private Image free;
    private Image restricted;

    private SimpleText planets;
    private SimpleText crystal;
    private Image t1;
    private SimpleText dizziness;
    private SimpleText lazer;
    private SimpleText portals;

    private SimpleText lazerOmar;
    private SimpleText lazerYamani;

    private SimpleText connectToDesktopServer;
    private SimpleText connectToAndroidClient;
    private SimpleText connecting;
    private SimpleText failedTapToTryAgain;
    private SimpleText connected;

    private SimpleText saveControllersAngles;
    private SimpleText loadControllersAngles;

    private Array<MyEarthEntity> earthEntities;
    private MyEarthEntity mountain;
    private MyEarthEntity tallGrass;
    private MyEarthEntity backTree;
    private MyEarthEntity frontTree;
    private MyEarthEntity manyTrees;
    private MyEarthEntity frontGrass;
    private MainMenuToGameplay mainMenuToGameplay;
    private GameplayScreen gameplayScreen;

    private NetworkAndStorageManager networkAndStorageManager;

    private StarsContainer starsContainer;

    private MyBitmapFont myBitmapFont;

    //private DifficultyCurveTesting difficultyCurveTesting;

    /*private Image i1;
    private Image i2;
    private Image i3;
    private Image i4;
    private RowOfActors rowOfActors;
    private boolean add = true;*/
    //private SpecialBulletTempProgressBarUI specialBulletTempProgressBarUI;
    //private TwoExitPortalUI twoExitPortalUI;


    public MainMenuScreen(final AdvancedStage game, AndroidPermissionHandler androidPermissionHandler, MyBitmapFont myBitmapFont, GameplayScreen gameplayScreen, StarsContainer starsContainer, boolean transform) {
        super(game, transform);

        this.game = game;
        this.androidPermissionHandler = androidPermissionHandler;
        this.myBitmapFont = myBitmapFont;
        this.gameplayScreen = gameplayScreen;
        this.starsContainer = starsContainer;

        Random random = new Random();

        earthEntities = new Array<MyEarthEntity>(false, 6, MyEarthEntity.class);

        mountain = new MyEarthEntity(Assets.instance.mainMenuAssets.mountain,
                MM_TILEABLE_WIDTH,
                MM_MOUNTAIN_HEIGHT,
                randomInitialX(random, MM_TILEABLE_WIDTH, true),
                MM_MOUNTAIN_INITIAL_Y,
                MM_MOUNTAIN_FINAL_Y,
                MM_MOUNTAIN_X_MOVING_AMOUNT,
                true,
                earthEntities);

        tallGrass = new MyEarthEntity(Assets.instance.mainMenuAssets.tallGrass,
                MM_TILEABLE_WIDTH,
                MM_TALL_GRASS_HEIGHT,
                randomInitialX(random, MM_TILEABLE_WIDTH, true),
                MM_TALL_GRASS_INITIAL_Y,
                MM_TALL_GRASS_FINAL_Y,
                MM_TALL_GRASS_X_MOVING_AMOUNT,
                true,
                earthEntities);

        float worldWidth = getStage().getViewport().getWorldWidth();
        float backTreeInitialX = randomInitialX(random, worldWidth*0.25f, false);
        backTree = new MyEarthEntity(Assets.instance.mainMenuAssets.treeBack,
                MM_BACK_TREE_WIDTH,
                MM_BACK_TREE_HEIGHT,
                backTreeInitialX,
                MM_BACK_TREE_INITIAL_Y,
                MM_BACK_TREE_FINAL_Y,
                MM_BACK_TREE_X_MOVING_AMOUNT,
                false,
                earthEntities);

        manyTrees = new MyEarthEntity(Assets.instance.mainMenuAssets.manyTrees,
                MM_TILEABLE_WIDTH,
                MM_MANY_TREES_HEIGHT,
                randomInitialX(random, MM_TILEABLE_WIDTH, true),
                MM_MANY_TREES_INITIAL_Y,
                MM_MANY_TREES_FINAL_Y,
                MM_MANY_TREES_X_MOVING_AMOUNT,
                true,
                earthEntities);

        float frontTreeInitialX = MathUtils.lerp(worldWidth*0.5f, worldWidth*0.75f, random.nextFloat());
        frontTree = new MyEarthEntity(Assets.instance.mainMenuAssets.treeFront,
                MM_FRONT_TREE_WIDTH,
                MM_FRONT_TREE_HEIGHT,
                frontTreeInitialX,
                MM_FRONT_TREE_INITIAL_Y,
                MM_FRONT_TREE_FINAL_Y,
                MM_FRONT_TREE_X_MOVING_AMOUNT,
                false,
                earthEntities);

        frontGrass = new MyEarthEntity(Assets.instance.mainMenuAssets.frontGrass,
                MM_TILEABLE_WIDTH,
                MM_FRONT_GRASS_HEIGHT,
                randomInitialX(random, MM_TILEABLE_WIDTH, true),
                MM_FRONT_GRASS_INITIAL_Y,
                MM_FRONT_GRASS_FINAL_Y,
                MM_FRONT_GRASS_X_MOVING_AMOUNT,
                true,
                earthEntities);

        initializeSurvival();
        initializeRestricted(game);
        initializeFree(game);
        initializePlanets();
        initializeCrystal(game);
        initializeT1(game);
        initializeDizziness(game);
        initializeLazer(game);
        initializeLazerOmar(game);
        initializeLazerYamani(game);
        initializePortals(game);

        //start.setLayoutEnabled(false, earthEntities); //for performance.

        addActor(mountain);
        addActor(tallGrass);
        addActor(backTree);
        addActor(manyTrees);
        addActor(frontTree);
        addActor(frontGrass);
        //addActor(start);
        addActor(survival);
        addActor(restricted);
        addActor(free);
        addActor(planets);
        addActor(crystal);
        addActor(t1);
        addActor(dizziness);
        addActor(lazer);
        addActor(portals);
        addActor(lazerOmar);
        addActor(lazerYamani);

        initializeConnectToDesktopServer();
        initializeConnectToAndroidClient();
        initializeConnecting();
        initializeFailedTapToTryAgain();
        initializeConnected();

        initializeSaveControllersAngles();
        initializeLoadControllersAngles();

        //difficultyCurveTesting = new DifficultyCurveTesting();

        /*i1 = new Image(Assets.instance.gameplayAssets.minusBullet);
        i2 = new Image(Assets.instance.gameplayAssets.plusBullet);
        i3 = new Image(Assets.instance.gameplayAssets.rewindBullet);
        i4 = new Image(Assets.instance.gameplayAssets.starBullet);
        i1.setSize(5, 5);
        i2.setSize(5, 5);
        i3.setSize(5, 5);
        i4.setSize(5, 5);
        rowOfActors = new RowOfActors(2);
        addActor(rowOfActors);*/
        /*specialBulletTempProgressBarUI = new SpecialBulletTempProgressBarUI(Assets.instance.gameplayAssets.minusBullet);
        addActor(specialBulletTempProgressBarUI);*/
        /*twoExitPortalUI = new TwoExitPortalUI(myBitmapFont, Assets.instance.gameplayAssets.twoExitPortal, Assets.instance.gameplayAssets.twoExitPortalGlow);
        addActor(twoExitPortalUI);*/
    }


    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);

        //cycleAspectRatios();

        //gamePadPooling();

        /*if (Gdx.input.isKeyJustPressed(Input.Keys.A))
            add = true;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.D))
            add = false;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            if (add) rowOfActors.addActor(i1);
            else rowOfActors.removeActor(i1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            if (add) rowOfActors.addActor(i2);
            else rowOfActors.removeActor(i2);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            if (add) rowOfActors.addActor(i3);
            else rowOfActors.removeActor(i3);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            if (add) rowOfActors.addActor(i4);
            else rowOfActors.removeActor(i4);
        }*/

        /*Gdx.app.log(TAG, "" + survival.getActions().size);
        if (Gdx.input.isKeyJustPressed(Input.Keys.A))
            survival.addAction(Actions.moveBy(-5, 0, 0.25f));*/

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        //difficultyCurveTesting.draw(batch);
    }

    private void gamePadPooling() {
        if (Controllers.getControllers().size == 0) return;

        com.badlogic.gdx.controllers.Controller gamePad = Controllers.getControllers().peek();

        if (gamePad.getButton(0))
            startRestricted(getAdvancedStage());

        if (gamePad.getButton(1))
            startFree(getAdvancedStage());
    }

    @Override
    public void show() {
        super.show();
        this.starsContainer.resetCurrentSpeed();

        restricted.setVisible(false);
        free.setVisible(false);

        crystal.setVisible(false);
        t1.setVisible(false);
        dizziness.setVisible(false);
        lazer.setVisible(false);
        portals.setVisible(false);

        survival.setVisible(true);
        planets.setVisible(true);
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        super.resize(width, height, worldWidth, worldHeight);
        survival.setSize(MM_SURVIVAL_TXT_WIDTH, MM_SURVIVAL_TXT_HEIGHT);

        if (survival != null) {
            survival.setPosition(getStage().getViewport().getWorldWidth() - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - survival.getWidth(),
                    survival.getY());
            //_dummy.setFinalVal(survival.getX());
            //Gdx.app.log(TAG, "" + survival.getX());
        }

        TextureRegion _restricted = Assets.instance.mainMenuAssets.restricted;
        restricted.setSize(MM_SURVIVAL_TXT_HEIGHT * _restricted.getRegionWidth() / _restricted.getRegionHeight(), MM_SURVIVAL_TXT_HEIGHT);
        if (restricted != null) restricted.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - restricted.getWidth());

        TextureRegion _free = Assets.instance.mainMenuAssets.free;
        free.setSize(MM_SURVIVAL_TXT_HEIGHT * _free.getRegionWidth() / _free.getRegionHeight(), MM_SURVIVAL_TXT_HEIGHT);
        if (free != null) free.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - free.getWidth());

        if (planets != null) {
            planets.setHeight(MM_PLANETS_TXT_HEIGHT);
            planets.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - planets.getWidth());
        }

        if (crystal != null) {
            crystal.setHeight(MM_CRYSTAL_TXT_HEIGHT);
            crystal.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - crystal.getWidth());
        }

        TextureRegion _t1 = Assets.instance.mainMenuAssets.t1;
        t1.setSize(MM_T1_TXT_HEIGHT * _t1.getRegionWidth() / _t1.getRegionHeight(), MM_T1_TXT_HEIGHT);
        if (t1 != null) t1.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - t1.getWidth());

        if (dizziness != null) {
            dizziness.setHeight(MM_DIZZINESS_TXT_HEIGHT);
            dizziness.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - dizziness.getWidth());
        }

        if (lazer != null) {
            lazer.setHeight(MM_LAZER_TXT_HEIGHT);
            lazer.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - lazer.getWidth());
        }

        if (portals != null) {
            portals.setHeight(MM_PORTALS_TXT_HEIGHT);
            portals.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - portals.getWidth());
        }

        if (lazerOmar != null) {
            lazerOmar.setHeight(MM_CRYSTAL_TXT_HEIGHT);
            lazerOmar.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - lazerOmar.getWidth());
        }

        if (lazerYamani != null) {
            lazerYamani.setHeight(MM_CRYSTAL_TXT_HEIGHT);
            lazerYamani.setX(worldWidth - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT - lazerYamani.getWidth());
        }
    }

    private void cycleAspectRatios() {
        int height = 630;
        int width = 0;

        String note = "";

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) | Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            width = (int) (height * (4f/3f)); // Narrowest
            note = " (NARROWEST)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) | Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            width = (int) (height * (16f/9f)); // Most common
            note = " (MOST COMMON)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) | Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            width = (int) (height * (19.5f/9f)); // Widest
            note = "=19.5:9 (WIDEST Till Now)";
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) | Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            width = (int) (height * (21.5f/9f)); // Widest
            note = "=21.5:9 (WIDEST Supported)";
        }

        String title = "Battleshield ALPHA | Aspect ratio = " + MyMath.ratio(width, height);

        if (width == 0) return;

        Gdx.graphics.setWindowedMode(width	, height);
        Gdx.graphics.setTitle(title + note);

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
        networkAndStorageManager.dispose();
        super.dispose();
    }



    private void initializeSurvival() {
        survival = new Image(Assets.instance.mainMenuAssets.survival) {
            @Override
            public void draw(Batch batch, float parentAlpha) {

                /*Color color = getColor();
                batch.setColor(1, 1, 1, alpha * parentAlpha);*/

                //Gdx.app.log(TAG, "" + batch.getBlendSrcFunc() + ", " + batch.getBlendDstFunc());
                batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };

        //game.setKeyboardFocus(survival);
        survival.setVisible(false);

        survival.setPosition(getStage().getViewport().getWorldWidth() - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT,
                MM_SURVIVAL_TXT_FINAL_Y /*WORLD_SIZE*0.2f*/);

        //survival.setBounds(survival.getX(), survival.getY(), survival.getWidth(), survival.getHeight());

        survival.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //game.switchScreens(mainMenuToGameplay);
                survival.setVisible(false);
                free.setVisible(true);
                restricted.setVisible(true);
                planets.setVisible(false);
                //game.switchScreens(new SimplestTransition(game, game.getAdvancedScreens()[2], new ExperimentsScreen(game, false)));
            }

        });
    }

    private void initializeRestricted(final AdvancedStage game) {
        restricted = new Image(Assets.instance.mainMenuAssets.restricted) {
            @Override
            public void draw(Batch batch, float parentAlpha) {

                /*Color color = getColor();
                batch.setColor(1, 1, 1, alpha * parentAlpha);*/

                //Gdx.app.log(TAG, "" + batch.getBlendSrcFunc() + ", " + batch.getBlendDstFunc());
                batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };

        game.setKeyboardFocus(restricted);
        restricted.setVisible(/*true*/false);

        restricted.setY(MM_RESTRICTED_TXT_FINAL_Y);

        restricted.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                startRestricted(game);
            }
        });
    }

    private void startRestricted(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.SURVIVAL);
        //gameplayScreen.setState(GameplayScreen.State.PLAYING);
        game.switchScreens(mainMenuToGameplay);
        //game.switchScreens(new SimplestTransition(game, game.getAdvancedScreens()[2], new ExperimentsScreen(game, false)));
    }

    private void initializeFree(final AdvancedStage game) {
        free = new Image(Assets.instance.mainMenuAssets.free) {
            @Override
            public void draw(Batch batch, float parentAlpha) {

                /*Color color = getColor();
                batch.setColor(1, 1, 1, alpha * parentAlpha);*/

                //Gdx.app.log(TAG, "" + batch.getBlendSrcFunc() + ", " + batch.getBlendDstFunc());
                batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };

        //game.setKeyboardFocus(free);
        free.setVisible(/*true*/false);

        free.setY(MM_FREE_TXT_FINAL_Y);

        /*free.setPosition(getStage().getViewport().getWorldWidth() - MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT,
                MM_FREE_TXT_FINAL_Y);

        free.setBounds(free.getX(), free.getY(), 2*free.getImageWidth(), free.getImageHeight());*/

        free.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                startFree(game);
            }
        });
    }

    private void startFree(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.FREE);
        gameplayScreen.setGameplayMode(GameplayMode.SURVIVAL);
        game.switchScreens(mainMenuToGameplay);
        //game.switchScreens(new SimplestTransition(game, game.getAdvancedScreens()[2], new ExperimentsScreen(game, false)));
    }

    private void initializePlanets() {
        planets = new SimpleText(myBitmapFont, "PLANETS");

        planets.setVisible(false);

        planets.setY(MM_PLANETS_TXT_FINAL_Y);

        planets.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        planets.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        //super.touchUp(event, x, y, pointer, button);
                        survival.setVisible(false);
                        planets.setVisible(false);
                        crystal.setVisible(true);
                        t1.setVisible(true);
                        dizziness.setVisible(true);
                        lazer.setVisible(true);
                        portals.setVisible(true);
                    }
                }
        );
    }

    private void initializeCrystal(final AdvancedStage game) {
        crystal = new SimpleText(myBitmapFont, "CRYSTAL");

        crystal.setVisible(false);

        crystal.setY(MM_CRYSTAL_TXT_FINAL_Y);

        crystal.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        crystal.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        //super.touchUp(event, x, y, pointer, button);
                        startCrystal(game);
                    }
                }
        );
    }

    private void startCrystal(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.CRYSTAL);
        game.switchScreens(mainMenuToGameplay);
        //game.switchScreens(new SimplestTransition(game, game.getAdvancedScreens()[2], new ExperimentsScreen(game, false)));
    }

    private void initializeT1(final AdvancedStage game) {
        t1 = new Image(Assets.instance.mainMenuAssets.t1) {
            @Override
            public void draw(Batch batch, float parentAlpha) {

                /*Color color = getColor();
                batch.setColor(1, 1, 1, alpha * parentAlpha);*/

                //Gdx.app.log(TAG, "" + batch.getBlendSrcFunc() + ", " + batch.getBlendDstFunc());
                batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_COLOR, GL20.GL_ONE_MINUS_SRC_COLOR);
                super.draw(batch, parentAlpha);
                batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
        };


        t1.setVisible(false);

        t1.setY(MM_T1_TXT_FINAL_Y);

        t1.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startT1(game);
                    }
                }
        );
    }


    private void startT1(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.T1);
        game.switchScreens(mainMenuToGameplay);
    }


    private void initializeDizziness(AdvancedStage game) {
        dizziness = new SimpleText(myBitmapFont, "DIZZINESS");

        dizziness.setVisible(false);

        dizziness.setY(MM_DIZZINESS_FINAL_Y);

        dizziness.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        dizziness.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        //Gdx.app.log(TAG, "HI");
                        startDizziness(game);
                    }
                }
        );
    }

    private void startDizziness(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.DIZZINESS);
        game.switchScreens(mainMenuToGameplay);
    }

    private void initializeLazer(final AdvancedStage game) {
        lazer = new SimpleText(myBitmapFont, "LAZER");

        lazer.setVisible(false);

        lazer.setY(MM_LAZER_FINAL_Y);

        lazer.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        lazer.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        //startLazer(game, );
                        lazerOmar.setVisible(true);
                        lazerYamani.setVisible(true);
                        crystal.setVisible(false);
                        dizziness.setVisible(false);
                        t1.setVisible(false);
                        lazer.setVisible(false);
                        portals.setVisible(false);
                    }
                }
        );
    }

    private void initializeLazerOmar(final AdvancedStage game) {
        lazerOmar = new SimpleText(myBitmapFont, "OMAR");

        lazerOmar.setVisible(false);

        lazerOmar.setY(MM_SURVIVAL_TXT_FINAL_Y);

        lazerOmar.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        lazerOmar.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startLazer(game, LazerAttackStuff.LazerAttackHealthAffection.OMAR);
                    }
                }
        );
    }

    private void initializeLazerYamani(final AdvancedStage game) {
        lazerYamani = new SimpleText(myBitmapFont, "YAMANI");

        lazerYamani.setVisible(false);

        lazerYamani.setY(MM_PLANETS_TXT_FINAL_Y);

        lazerYamani.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        lazerYamani.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startLazer(game, LazerAttackStuff.LazerAttackHealthAffection.YAMANI);
                    }
                }
        );
    }

    private void startLazer(final AdvancedStage game, LazerAttackStuff.LazerAttackHealthAffection lazerAttackHealthAffection) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.LAZER);
        gameplayScreen.getLazerAttackStuff().setLazerAttackHealthAffection(lazerAttackHealthAffection);
        game.switchScreens(mainMenuToGameplay);
    }

    private void initializePortals(final AdvancedStage game) {
        portals = new SimpleText(myBitmapFont, "PORTALS");

        portals.setVisible(false);

        portals.setY(MM_PORTALS_FINAL_Y);

        portals.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);

        portals.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        //Gdx.app.log(TAG, "Uncomment startPortals(game);");
                        startPortals(game);
                    }
                }
        );
    }

    private void startPortals(final AdvancedStage game) {
        gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
        gameplayScreen.setGameplayMode(GameplayMode.PORTALS);
        game.switchScreens(mainMenuToGameplay);
    }

    private void initializeConnectToDesktopServer() {
        connectToDesktopServer = new SimpleText(myBitmapFont, "Connect To Desktop Server");
        if (Gdx.app.getType() == Application.ApplicationType.Android | Gdx.app.getType() == Application.ApplicationType.iOS) {
            addActor(connectToDesktopServer);
            connectToDesktopServer.setVisible(true);
        }
        connectToDesktopServer.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        connectToDesktopServer.setHeight(5);
        connectToDesktopServer.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        networkAndStorageManager.connect();
                        connectToDesktopServer.setVisible(false);
                        connecting.setVisible(true);
                    }
                }
        );
    }

    private void initializeConnectToAndroidClient() {
        connectToAndroidClient = new SimpleText(myBitmapFont, "Connect To Android Client");
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            addActor(connectToAndroidClient);
            connectToAndroidClient.setVisible(true);
        }
        connectToAndroidClient.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        connectToAndroidClient.setHeight(5);
        connectToAndroidClient.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        networkAndStorageManager.connect();
                        connectToAndroidClient.setVisible(false);
                        connecting.setVisible(true);

                        loadControllersAngles.setVisible(false);
                    }
                }
        );
    }

    private void initializeConnecting() {
        connecting = new SimpleText(myBitmapFont, "Connecting...");

        addActor(connecting);
        connecting.setVisible(false);

        connecting.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        connecting.setHeight(5);
    }

    private void initializeFailedTapToTryAgain() {
        failedTapToTryAgain = new SimpleText(myBitmapFont, "Failed .. Tap To Try Again");

        addActor(failedTapToTryAgain);
        failedTapToTryAgain.setVisible(false);

        failedTapToTryAgain.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        failedTapToTryAgain.setHeight(5);
        failedTapToTryAgain.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        networkAndStorageManager.connect();
                        failedTapToTryAgain.setVisible(false);
                        connecting.setVisible(true);
                    }
                }
        );
    }

    private void initializeConnected() {
        connected = new SimpleText(myBitmapFont, "Connected");

        addActor(connected);
        connected.setVisible(false);

        connected.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        connected.setHeight(5);
    }

    public void connectionFailed() {
        connecting.setVisible(false);
        failedTapToTryAgain.setVisible(true);
    }

    public void connectionEstablished() {
        connecting.setVisible(false);
        connected.setVisible(true);

        startNetworkReceiverValuesLoaderGameplayMode();
    }

    private void startNetworkReceiverValuesLoaderGameplayMode() {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            gameplayScreen.setGameplayControllerType(GameplayControllerType.RESTRICTED);
            gameplayScreen.setGameplayMode(GameplayMode.NETWORK_RECEIVER_VALUES_LOADER);
            game.switchScreens(mainMenuToGameplay);
        }
    }

    private void initializeSaveControllersAngles() {
        saveControllersAngles = new SimpleText(myBitmapFont, "Save Controller Angles");

        if (Gdx.app.getType() == Application.ApplicationType.Android | Gdx.app.getType() == Application.ApplicationType.iOS)
            addActor(saveControllersAngles);

        saveControllersAngles.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        saveControllersAngles.setHeight(5);
        saveControllersAngles.setY(WORLD_SIZE - saveControllersAngles.getHeight());

        saveControllersAngles.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);


                        if (androidPermissionHandler.isWriteToExternalStoragePermissionGranted())
                            turnOnSaveControllerAngles();
                        else
                            androidPermissionHandler.requestWriteToExternalStoragePermission();


                    }
                }
        );
    }

    private void turnOnSaveControllerAngles() {
        networkAndStorageManager.setSaveControllerValuesModeEnabled(true);
        saveControllersAngles.setColor(Color.LIME);
    }

    private void initializeLoadControllersAngles() {
        loadControllersAngles = new SimpleText(myBitmapFont, "Load Controller Angles");

        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
            addActor(loadControllersAngles);

        loadControllersAngles.setColor(1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1-BG_COLOR_GREY, 1);
        loadControllersAngles.setHeight(5);
        loadControllersAngles.setY(WORLD_SIZE - loadControllersAngles.getHeight());

        loadControllersAngles.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        connectToAndroidClient.setVisible(false);

                        //String filePath = System.console().readLine("Please enter file path: ");


                        networkAndStorageManager.enableLoadControllerValuesMode();


                        startNetworkReceiverValuesLoaderGameplayMode();
                    }
                }
        );
    }

    public void setSurvivalAlpha(float a) {
        survival.setColor(a, a, a, a);
    }

    public void setSurvivalVisibility(boolean visibility) {
        survival.setVisible(visibility);
    }

    public void setPlanetsAlpha(float a) {
        Color c = planets.getColor();
        planets.setColor(c.r, c.g, c.b, a);
    }

    public void setPlanetsVisibility(boolean visibility) {
        planets.setVisible(visibility);
    }

    private float randomInitialX(Random random, float width, boolean tileable) {
        if (tileable) return random.nextFloat()* width *2 - width;
        else return random.nextFloat()* width;
    }

    public void setMainMenuToGameplay(MainMenuToGameplay mainMenuToGameplay) {
        this.mainMenuToGameplay = mainMenuToGameplay;
    }

    public MyEarthEntity[] getEarthEntities() {
        return earthEntities.items;
    }

    public void setNetworkAndStorageManager(NetworkAndStorageManager networkAndStorageManager) {
        this.networkAndStorageManager = networkAndStorageManager;
    }





    @Override
    public void permissionGranted(int requestCode) {
        if (requestCode == AndroidPermissionHandler.REQUEST_WRITE_TO_EXTERNAL_STORAGE)
            turnOnSaveControllerAngles();
    }

    @Override
    public void permissionDenied(int requestCode) {

    }




















    public class MyEarthEntity extends EarthEntity {

        private float initialImageY;
        private float finalImageY;

        public MyEarthEntity(TextureRegion region,
                             float imageWidth,
                             float imageHeight,
                             float initialImageX,
                             float initialImageY,
                             float finalImageY,
                             float xMovingAmount,
                             boolean tileable,
                             Array<MyEarthEntity> earthEntities) {
            super(region, imageWidth, imageHeight, initialImageX, initialImageY, xMovingAmount, tileable);

            this.initialImageY = initialImageY;
            this.finalImageY = finalImageY;
            earthEntities.add(this);
        }

        public float getInitialImageY() {
            return initialImageY;
        }

        public float getFinalImageY() {
            return finalImageY;
        }
    }

    public class EarthEntity extends Group { // A moving tileable Image (doesn't have to be tileable)

        private Image image1;
        private Image image2;
        private float xMovingAmount;
        private float imageWidth;
        private boolean tileable;
        private Image toBeChanged; // the image that its position will be changed to be behind or in front of the other image in order to have an endless moving effect.

        public EarthEntity(TextureRegion region,
                           float imageWidth,
                           float imageHeight,
                           float initialImageX,
                           float initialImageY,
                           float xMovingAmount,
                           boolean tileable) {

            this.image1 = new Image(region);
            this.xMovingAmount = xMovingAmount;
            this.imageWidth = imageWidth;
            this.tileable = tileable;

            this.image1.setSize(this.imageWidth, imageHeight);
            this.image1.setPosition(initialImageX, initialImageY);
            this.addActor(image1);

            if (tileable) {
                this.image2 = new Image(region);
                this.addActor(image2);
                this.image2.setSize(this.imageWidth, imageHeight);

                if (initialImageX >= 0)
                    this.image2.setPosition(initialImageX - imageWidth, initialImageY);
                else this.image2.setPosition(initialImageX + imageWidth, initialImageY);

                if (xMovingAmount > 0) {
                    if (image1.getX() > image2.getX()) toBeChanged = image1;
                    else toBeChanged = image2;
                } else {
                    if (image1.getX() > image2.getX()) toBeChanged = image2;
                    else toBeChanged = image1;
                }
                //image2.setLayoutEnabled(false); //for performance.
                //toBeChanged.setLayoutEnabled(false); //for performance.
            }
            setTransform(false); //for performance.
            //image1.setLayoutEnabled(false); //for performance.
        }

        @Override
        public void act(float delta) {
            super.act(delta);

            image1.setX(image1.getX() + xMovingAmount*delta);

            if (tileable) {
                image2.setX(image2.getX() + xMovingAmount*delta);
                if (xMovingAmount > 0) {
                    float diff = toBeChanged.getX() - imageWidth;
                    //Gdx.app.log(TAG, "diff = " + diff);
                    if (diff >= 0) {
                        //Gdx.app.log(TAG, "diff = " + diff);
                        toBeChanged.setX(-imageWidth + diff);
                        cycleToBeChanged();
                    }
                } else {
                    float diff = toBeChanged.getX() + imageWidth;
                    if (diff <= 0) {
                        toBeChanged.setX(imageWidth + diff);
                        cycleToBeChanged();
                    }
                }
            } else {
                if (xMovingAmount > 0) {
                    if (image1.getX() > getStage().getViewport().getWorldWidth())
                        image1.setX(-imageWidth);
                } else {
                    if (image1.getX() < -imageWidth)
                        image1.setX(getStage().getViewport().getWorldWidth());
                }
            }
        }

        private void cycleToBeChanged() {
            if (toBeChanged.equals(image1)) toBeChanged = image2;
            else toBeChanged = image1;
        }

        @Override
        public void setY(float y) {
            image1.setY(y);
            if (tileable) image2.setY(y);
        }

        @Override
        @Deprecated
        public void setX(float x) {

        }

        @Override
        @Deprecated
        public void setX(float x, int alignment) {

        }

        @Override
        @Deprecated
        public void setY(float y, int alignment) {

        }

        @Override
        @Deprecated
        public void setPosition(float x, float y) {

        }

        @Override
        @Deprecated
        public void setPosition(float x, float y, int alignment) {

        }

        @Override
        @Deprecated
        public void setRotation(float degrees) {

        }
    }

}