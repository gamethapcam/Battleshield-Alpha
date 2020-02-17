package com.yaamani.battleshield.alpha.Game.Transitions;

import com.badlogic.gdx.math.Interpolation;
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

    private Tween menuTxtAppear;

    public LoadingToMainMenu(AdvancedStage game, AdvancedScreen out, AdvancedScreen in, StarsContainer starsContainer) {
        super(game, out, in);

        ((MainMenuScreen) in).setSurvivalAlpha(0);

        initializePinkToGreyBG();
        initializeStarsFadeIn(starsContainer);
        initializeStarsUpwards(starsContainer);
        initializeEntitiesUpwards();
        initializeLogoFadesOut();
        initializeSurvivalTxtAppear();

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
        pinkToGreyBG = new Tween(PINK_TO_GREY_DURATION, linear, this) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                float r = interpolation.apply(LOADING_BG_COLOR_R, BG_COLOR_GREY, percentage);
                float g = interpolation.apply(LOADING_BG_COLOR_G, BG_COLOR_GREY, percentage);
                float b = interpolation.apply(LOADING_BG_COLOR_B, BG_COLOR_GREY, percentage);
                SolidBG.instance.setColor(r, g, b);
            }
        };
    }

    private void initializeStarsFadeIn(final StarsContainer starsContainer) {
        starsFadeIn = new Tween(STARS_FADE_IN_DURATION, linear, this) {
            Star[] stars = starsContainer.getStars();

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                for (int i = 0; i < stars.length; i++) {
                    stars[i].setAlpha(interpolation.apply(percentage));
                }
            }
        };
    }

    private void initializeEntitiesUpwards() {
        mmEarthEntitiesUpwards = new Tween(MM_EARTH_ENTITIES_UPWARDS_DURATION, fadeOut, this) {
            MyEarthEntity[] earthEntities = ((MainMenuScreen) in).getEarthEntities();

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                for (MyEarthEntity earthEntity : (earthEntities)) {
                    earthEntity.setY(interpolation.apply(earthEntity.getInitialImageY(), earthEntity.getFinalImageY(), percentage));
                }
            }
        };
    }

    private void initializeStarsUpwards(final StarsContainer starsContainer) {
        starsUpwards = new Tween(STARS_UPWARDS_DURATION, fade, this) {
            Vector2 transitionVel = starsContainer.getTransitionVelocity();

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                if (percentage <= 0.5f)
                    transitionVel.set(0, fade.apply(0, STARS_UPWARDS_MAX_TRANSITION, percentage));
                else
                    transitionVel.set(0, fade.apply(STARS_UPWARDS_MAX_TRANSITION, 0, percentage));
            }

            @Override
            public void onStart() {
                mmEarthEntitiesUpwards.start(MM_EARTH_ENTITIES_UPWARDS_DELAY);
                logoFadesOut.start(LOGO_FADES_OUT_DELAY);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                menuTxtAppear.start();
            }
        };
    }

    private void initializeLogoFadesOut() {
        logoFadesOut = new Tween(LOGO_FADES_OUT_DURATION, linear, this) {
            @Override
            public void tween(float percentage, Interpolation interpolation) {
                ((LoadingScreen) out).setLogosAlpha(interpolation.apply(1-percentage));
            }
        };
    }

    private void initializeSurvivalTxtAppear() {
        menuTxtAppear = new Tween(MENU_TXT_APPEAR_DURATION, threePulses, this) {
            @Override
            public void onStart() {
                ((MainMenuScreen) in).setSurvivalVisibility(/*false*/true);
                ((MainMenuScreen) in).setPlanetsVisibility(/*false*/true);
            }

            @Override
            public void tween(float percentage, Interpolation interpolation) {
                ((MainMenuScreen) in).setSurvivalAlpha(interpolation.apply(percentage));
                ((MainMenuScreen) in).setPlanetsAlpha(interpolation.apply(percentage));
            }
        };
    }
}
