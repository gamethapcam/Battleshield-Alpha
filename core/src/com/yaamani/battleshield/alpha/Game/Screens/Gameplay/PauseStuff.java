package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;
import com.yaamani.battleshield.alpha.MyEngine.Timer;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import static com.badlogic.gdx.math.Interpolation.linear;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class PauseStuff implements Resizable, Updatable {

    public static final String TAG = PauseStuff.class.getSimpleName();

    private Image pauseSymbol;
    private Group resumeBounds;
    private Image pauseText;
    private Image dimmingOverlay;

    private GestureDetector doubleTapToResume;

    private MyBitmapFont myBitmapFont;
    private Image _3;
    private Image _2;
    private Image _1;

    private Tween pauseSymbolFadesOutWhenLosing;
    //private Timer resumeAfterCountDown; // 3 seconds
    private Tween _3Tween;
    private Tween _2Tween;
    private Tween _1Tween;

    private GameplayScreen gameplayScreen;

    public PauseStuff(GameplayScreen gameplayScreen, MyBitmapFont myBitmapFont) {
        this.gameplayScreen = gameplayScreen;
        this.myBitmapFont = myBitmapFont;

        dimmingOverlay = new Image(Assets.instance.gameplayAssets.dimmingOverlay);

        initializePauseText();

        initializeDoubleTapToResume();

        initializeResumeBounds();

        initializePauseSymbol();

        initializePauseSymbolFadesOutWhenLosing();

        //initializeResumeAfterCountDown();

        initialize_3_2_1_SimpleText();

        initialize_3_2_1_Tween();
    }

    //------------------------------ Super class And Implemented Interfaces ------------------------------
    //------------------------------ Super class And Implemented Interfaces ------------------------------
    //------------------------------ Super class And Implemented Interfaces ------------------------------
    //------------------------------ Super class And Implemented Interfaces ------------------------------

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        pauseSymbol.setPosition(worldWidth - pauseSymbol.getWidth() - PAUSE_SYMBOL_MARGIN_RIGHT,
                worldHeight - pauseSymbol.getHeight() - PAUSE_SYMBOL_MARGIN_UP);
        pauseText.setPosition(worldWidth / 2f - pauseText.getWidth() / 2f, worldHeight / 2f - pauseText.getHeight() / 2f);

        dimmingOverlay.setBounds(0, 0, worldWidth, worldHeight);
        resumeBounds.setBounds(0, 0, worldWidth, worldHeight);

        _3.setPosition(worldWidth / 2f - _3.getWidth() / 2f, worldHeight / 2f - _3.getHeight() / 2f);
        _2.setPosition(worldWidth / 2f - _2.getWidth() / 2f, worldHeight / 2f - _2.getHeight() / 2f);
        _1.setPosition(worldWidth / 2f - _1.getWidth() / 2f, worldHeight / 2f - _1.getHeight() / 2f);
    }

    @Override
    public void update(float delta) {
        pauseSymbolFadesOutWhenLosing.update(delta);
        //resumeAfterCountDown.update(delta);
        _3Tween.update(delta);
        _2Tween.update(delta);
        _1Tween.update(delta);
    }

    //------------------------------ Utility ------------------------------
    //------------------------------ Utility ------------------------------
    //------------------------------ Utility ------------------------------
    //------------------------------ Utility ------------------------------

    private void pauseTheGame() {
        if (pauseSymbol.getColor().a != 1/* | gameplayScreen.getState() != GameplayScreen.State.PLAYING*/) return;

        _3Tween.finish();
        _2Tween.finish();
        _1Tween.finish();

        gameplayScreen.setState(GameplayScreen.State.PAUSED);

        /*Timer[] pauseWhenPausingFinishWhenLosing = gameplayScreen.getPauseWhenPausingFinishWhenLosing();
        for (Timer timer : pauseWhenPausingFinishWhenLosing) {
            if (timer != null)
                timer.pause();
        }*/

        gameplayScreen.addActor(resumeBounds);
    }

    private void resumeTheGame() {
        //gameplayScreen.setState(GameplayScreen.State.PLAYING);

        //resumeAfterCountDown.start();
        _3Tween.start();
        gameplayScreen.removeActor(resumeBounds);

        /*if (gameplayScreen.isInStarBulletAnimation()) {

            Timer[] resumeWhenResumingStarBullet = gameplayScreen.getResumeWhenResumingStarBullet();
            for (Timer timer : resumeWhenResumingStarBullet) {
                if (timer != null)
                    timer.resume();
            }

        } else {

            Timer[] pauseWhenPausingFinishWhenLosing = gameplayScreen.getPauseWhenPausingFinishWhenLosing();
            for (Timer timer : pauseWhenPausingFinishWhenLosing) {
                if (timer != null)
                    timer.resume();
            }

        }*/


        //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierText().setVisible(true);
        //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBar().setVisible(true);
    }

    private void _3_2_1_Tween(Actor text, float percentage, Interpolation interpolation) {
        Color color = text.getColor();
        if (percentage < 0.3f)
            text.setColor(color.r, color.g, color.b, interpolation.apply(percentage) * 10f / 3f);
    }

    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------
    //------------------------------ Getters And Setters ------------------------------

    public Tween getPauseSymbolFadesOutWhenLosing() {
        return pauseSymbolFadesOutWhenLosing;
    }

    public Image getPauseSymbol() {
        return pauseSymbol;
    }

    public GestureDetector getDoubleTapToResume() {
        return doubleTapToResume;
    }

    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializePauseText() {
        pauseText = new Image(Assets.instance.gameplayAssets.pauseText);
        pauseText.setSize(PAUSE_TEXT_WIDTH, PAUSE_TEXT_HEIGHT);
    }

    private void initializeDoubleTapToResume() {
        doubleTapToResume = new GestureDetector(new GestureDetector.GestureAdapter() {
            @Override
            public boolean tap(float x, float y, int count, int button) {
                if (gameplayScreen.getState() == GameplayScreen.State.PLAYING & count == 2)
                    pauseTheGame();
                return false;
            }
        });

        InputMultiplexer inputMultiplexer = new InputMultiplexer(doubleTapToResume, gameplayScreen.getStage());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initializeResumeBounds() {
        resumeBounds = new Group();
        resumeBounds.addActor(dimmingOverlay);
        resumeBounds.addActor(pauseText);
        resumeBounds.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeTheGame();
                //resumeAfterCountDown.start();
                //_3Tween.start();
                //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getScoreMultiplierText().setVisible(false);
                //gameplayScreen.getScoreStuff().getScoreMultiplierStuff().getMyProgressBar().setVisible(false);
                //gameplayScreen.removeActor(resumeBounds);
            }
        });
    }

    private void initializePauseSymbol() {
        pauseSymbol = new Image(Assets.instance.gameplayAssets.pauseSymbol);
        pauseSymbol.setSize(PAUSE_SYMBOL_WIDTH, PAUSE_SYMBOL_HEIGHT);
        gameplayScreen.addActor(pauseSymbol);

        pauseSymbol.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseTheGame();
            }
        });
    }

    private void initializePauseSymbolFadesOutWhenLosing() {
        pauseSymbolFadesOutWhenLosing = new Tween(SCORE_FADE_OUT_TWEEN_DURATION, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                Color color = pauseSymbol.getColor();
                pauseSymbol.setColor(color.r, color.g, color.b, interpolation.apply(1 - percentage));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Color color = pauseSymbol.getColor();
                pauseSymbol.setColor(color.r, color.g, color.b, 0);
            }
        };
    }

    /*private void initializeResumeAfterCountDown() {
        resumeAfterCountDown = new Timer(3000) {
            @Override
            public void onFinish() {
                super.onFinish();
                resumeTheGame();
            }
        };
    }*/

    private void initialize_3_2_1_SimpleText() {
        TextureRegion _3TextureRegion = Assets.instance.gameplayAssets.pause3;
        TextureRegion _2TextureRegion = Assets.instance.gameplayAssets.pause2;
        TextureRegion _1TextureRegion = Assets.instance.gameplayAssets.pause1;

        _3 = new Image(_3TextureRegion);
        _2 = new Image(_2TextureRegion);
        _1 = new Image(_1TextureRegion);

        _3.setSize(PAUSE_3_2_1_HEIGHT*(float) _3TextureRegion.getRegionWidth()/_3TextureRegion.getRegionHeight(), PAUSE_3_2_1_HEIGHT);
        _2.setSize(PAUSE_3_2_1_HEIGHT*(float) _2TextureRegion.getRegionWidth()/_2TextureRegion.getRegionHeight(), PAUSE_3_2_1_HEIGHT);
        _1.setSize(PAUSE_3_2_1_HEIGHT*(float) _1TextureRegion.getRegionWidth()/_1TextureRegion.getRegionHeight(), PAUSE_3_2_1_HEIGHT);

        /*Color color = new Color(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1);
        _3.setColor(color);
        _2.setColor(color);
        _1.setColor(color);*/

        gameplayScreen.addActor(_3);
        gameplayScreen.addActor(_2);
        gameplayScreen.addActor(_1);

        _3.setVisible(false);
        _2.setVisible(false);
        _1.setVisible(false);
    }

    private void initialize_3_2_1_Tween() {
        _3Tween = new Tween(1000, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                _3_2_1_Tween(_3, percentage, linear);
            }

            @Override
            public void onStart() {
                super.onStart();
                _3.setVisible(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                _3.setVisible(false);
                _2Tween.start();
            }
        };

        // -------------------

        _2Tween = new Tween(1000, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                _3_2_1_Tween(_2, percentage, interpolation);
            }

            @Override
            public void onStart() {
                super.onStart();
                _2.setVisible(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                _2.setVisible(false);
                _1Tween.start();
            }
        };

        // -------------------

        _1Tween = new Tween(1000, linear) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                _3_2_1_Tween(_1, percentage, linear);
            }

            @Override
            public void onStart() {
                super.onStart();
                _1.setVisible(true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                _1.setVisible(false);

                gameplayScreen.setState(GameplayScreen.State.PLAYING);
            }
        };
    }


    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------


}




