package com.yaamani.battleshield.alpha.Game.Transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.yaamani.battleshield.alpha.Game.Screens.LoadingScreen;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen;
import com.yaamani.battleshield.alpha.Game.Starfield.Star;
import com.yaamani.battleshield.alpha.Game.Starfield.StarsContainer;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.Game.SolidBG;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.Transition;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.Game.Screens.MainMenuScreen.MyEarthEntity;

import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class LoadingToMainMenu extends Transition {

    public static final String TAG = LoadingToMainMenu.class.getSimpleName();

    private Tween pinkToGreyBG;
    private Tween starsFadeIn;
    private Tween starsUpwards;
    private Tween mmEarthEntitiesUpwards; //mm = main menu
    private Tween logoFadesOut;
    private Tween startTxtAppear;

    public LoadingToMainMenu(AdvancedStage game, AdvancedScreen out, AdvancedScreen in, StarsContainer starsContainer) {
        super(game, out, in);

        ((MainMenuScreen) in).setStartsAlpha(0);

        initializePinkToGreyBG();
        initializeStarsFadeIn(starsContainer);
        initializeStarsUpwards(starsContainer);
        initializeEntitiesUpwards();
        initializeLogoFadesOut();
        initializeStartTxtAppear();

        setAutomaticallyEndAfterAllTweensFinish(true);
    }

    @Override
    public void onStart() {
        pinkToGreyBG.start(WATCH_OUR_LOGO_DELAY);
        starsFadeIn.start(WATCH_OUR_LOGO_DELAY);
        starsUpwards.start(WATCH_OUR_LOGO_DELAY);
    }

    //--------
    private void initializePinkToGreyBG() {
        pinkToGreyBG = new Tween(PINK_TO_GREY_DURATION, this) {
            @Override
            public void tween(float percentage) {
                float r = linear.apply(LOADING_BG_COLOR_R, BG_COLOR_GREY, percentage);
                float g = linear.apply(LOADING_BG_COLOR_G, BG_COLOR_GREY, percentage);
                float b = linear.apply(LOADING_BG_COLOR_B, BG_COLOR_GREY, percentage);
                SolidBG.instance.setColor(r, g, b);
            }
        };
    }

    private void initializeStarsFadeIn(final StarsContainer starsContainer) {
        starsFadeIn = new Tween(STARS_FADE_IN_DURATION, this) {
            Star[] stars = starsContainer.getStars();

            @Override
            public void tween(float percentage) {
                for (int i = 0; i < stars.length; i++) {
                    stars[i].setAlpha(linear.apply(percentage));
                }
            }
        };
    }

    private void initializeEntitiesUpwards() {
        mmEarthEntitiesUpwards = new Tween(MM_EARTH_ENTITIES_UPWARDS_DURATION, this) {
            MyEarthEntity[] earthEntities = ((MainMenuScreen) in).getEarthEntities();

            @Override
            public void tween(float percentage) {
                for (MyEarthEntity earthEntity : (earthEntities)) {
                    earthEntity.setY(fadeOut.apply(earthEntity.getInitialImageY(), earthEntity.getFinalImageY(), percentage));
                }
            }
        };
    }

    private void initializeStarsUpwards(final StarsContainer starsContainer) {
        starsUpwards = new Tween(STARS_UPWARDS_DURATION, this) {
            Vector2 transitionVel = starsContainer.getTransitionVelocity();

            @Override
            public void tween(float percentage) {
                if (percentage <= 0.5f)
                    transitionVel.set(0, fade.apply(0, STARS_UPWARDS_MAX_TRANSITION, percentage * 2));
                else
                    transitionVel.set(0, fade.apply(STARS_UPWARDS_MAX_TRANSITION, 0, (percentage - 0.5f) * 2f));
            }

            @Override
            public void onStart() {
                mmEarthEntitiesUpwards.start(MM_EARTH_ENTITIES_UPWARDS_DELAY);
                logoFadesOut.start(LOGO_FADES_OUT_DELAY);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                startTxtAppear.start();
            }
        };
    }

    private void initializeLogoFadesOut() {
        logoFadesOut = new Tween(LOGO_FADES_OUT_DURATION, this) {
            @Override
            public void tween(float percentage) {
                ((LoadingScreen) out).setLogosAlpha(linear.apply(1-percentage));
            }
        };
    }

    private void initializeStartTxtAppear() {
        startTxtAppear = new Tween(START_TXT_APPEAR_DURATION, this) {
            @Override
            public void onStart() {
                ((MainMenuScreen) in).setStartVisibility(true);
            }

            @Override
            public void tween(float percentage) {
                ((MainMenuScreen) in).setStartsAlpha(threeSteps.apply(percentage));
            }
        };
    }
}
