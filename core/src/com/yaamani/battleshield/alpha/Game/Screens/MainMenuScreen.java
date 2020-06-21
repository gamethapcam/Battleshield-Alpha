package com.yaamani.battleshield.alpha.Game.Screens;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DifficultyCurveTesting;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;
import com.yaamani.battleshield.alpha.Game.Transitions.MainMenuToGameplay;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;

import java.util.Random;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class MainMenuScreen extends AdvancedScreen {

    public static final String TAG = MainMenuScreen.class.getSimpleName();

    private Image start;
    private Image survival;
    private Image free;
    private Image restricted;

    private SimpleText planets;
    private SimpleText crystal;

    private Array<MyEarthEntity> earthEntities;
    private MyEarthEntity mountain;
    private MyEarthEntity tallGrass;
    private MyEarthEntity backTree;
    private MyEarthEntity frontTree;
    private MyEarthEntity manyTrees;
    private MyEarthEntity frontGrass;
    private MainMenuToGameplay mainMenuToGameplay;
    private GameplayScreen gameplayScreen;

    private MyBitmapFont myBitmapFont;

    //private DifficultyCurveTesting difficultyCurveTesting;


    public MainMenuScreen(final AdvancedStage game, MyBitmapFont myBitmapFont, GameplayScreen gameplayScreen, boolean transform) {
        super(game, transform);

        this.myBitmapFont = myBitmapFont;
        this.gameplayScreen = gameplayScreen;

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

        //difficultyCurveTesting = new DifficultyCurveTesting();
    }


    @Override
    public void act(float delta) {
        if (!isVisible()) return;
        super.act(delta);

        cycleAspectRatios();

        gamePadPooling();
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
                        Gdx.app.log(TAG, "diff = " + diff);
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