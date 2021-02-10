package com.yaamani.battleshield.alpha.Game.Utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreMultiplierDifficultyLevelStuff;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreTimerStuff;
import com.yaamani.battleshield.alpha.Game.Starfield.Star;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;

public final class Constants {


    public static final boolean NO_DEATH = false;
    public static final boolean DISMISS_STAR = true;




    public static final float WORLD_SIZE = 100.0f/*300.0f*/;
    // WORLD_SIZE will always be equal to viewport.getWorldHeight() for landscape apps.
    // And will always be equal to viewport.getWorldWidth() for portrait apps.

    public static final float MAX_ASPECT_RATIO_SUPPORTED = 21.5f/9f;

    public static final int MAX_RESOLUTION_SUPPORTED = 2880; // 5K (iPads are 4k already)

    //public static final float RESIZE_AFTER_ORIENTATION_CHANGE_DURATION = 150;



    public static final float WATCH_OUR_LOGO_DELAY = /*1000*/0;

    public static final float LOADING_BG_COLOR_R = 223f/255f;

    public static final float LOADING_BG_COLOR_G = 89f/255f;

    public static final float LOADING_BG_COLOR_B = 121f/255f;

    public static final float LOGO_HEIGHT = WORLD_SIZE * 0.71295486f;

    public static final float LOGO_WIDTH = LOGO_HEIGHT * 0.7984279f;

    public static final float STARS_UPWARDS_MAX_TRANSITION = WORLD_SIZE * 2f;


    public static final boolean STARS_MOTION_BLUR = false;

    public static final float PINK_TO_GREY_DURATION = 50/*0*/;

    public static final float STARS_FADE_IN_DURATION = 30/*0*/;

    public static final float STARS_UPWARDS_DURATION = 50/*00*/;

    public static final float MM_EARTH_ENTITIES_UPWARDS_DURATION = STARS_UPWARDS_DURATION / 2f;

    public static final float MM_EARTH_ENTITIES_UPWARDS_DELAY = (1-MM_EARTH_ENTITIES_UPWARDS_DURATION/STARS_UPWARDS_DURATION) * STARS_UPWARDS_DURATION;

    public static final float LOGO_FADES_OUT_DURATION = STARS_UPWARDS_DURATION / 5f;

    public static final float LOGO_FADES_OUT_DELAY = 0.3f*STARS_UPWARDS_DURATION;

    public static final float MENU_TXT_APPEAR_DURATION = 250;



    public static final float BG_COLOR_GREY = 0.1f;

    public static final float MM_MOUNTAIN_HEIGHT = WORLD_SIZE * 0.37535648f; // MM = Main menu

    public static final float MM_TALL_GRASS_HEIGHT = WORLD_SIZE * 0.44257037f;

    public static final float MM_BACK_TREE_HEIGHT = WORLD_SIZE * 0.62533241f;

    public static final float MM_BACK_TREE_WIDTH = MM_BACK_TREE_HEIGHT * 0.58798358f;

    public static final float MM_MANY_TREES_HEIGHT = WORLD_SIZE * 0.44246296f;

    public static final float MM_FRONT_TREE_HEIGHT = WORLD_SIZE * 0.55037315f;

    public static final float MM_FRONT_TREE_WIDTH = MM_FRONT_TREE_HEIGHT * 0.8927243f;

    public static final float MM_FRONT_GRASS_HEIGHT = WORLD_SIZE * 0.21616019f;

    public static final float MM_START_TXT_HEIGHT = WORLD_SIZE * 0.1f;

    public static final float MM_START_TXT_WIDTH = MM_START_TXT_HEIGHT * 472.625f/108.484f;

    public static final float MM_SURVIVAL_TXT_HEIGHT = WORLD_SIZE * 0.1f;

    public static final float MM_SURVIVAL_TXT_WIDTH = MM_SURVIVAL_TXT_HEIGHT * 711.352f/108.484f;

    public static final float MM_PLANETS_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_CRYSTAL_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_DIZZINESS_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_LAZER_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_PORTALS_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_BIG_BOSS_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_T1_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;
    //-------
    public static final float MM_MOUNTAIN_INITIAL_Y = -MM_MOUNTAIN_HEIGHT;

    public static final float MM_TALL_GRASS_INITIAL_Y = -MM_TALL_GRASS_HEIGHT - MM_MOUNTAIN_HEIGHT/2f;

    public static final float MM_BACK_TREE_INITIAL_Y = -MM_BACK_TREE_HEIGHT - MM_TALL_GRASS_HEIGHT/2f;

    public static final float MM_MANY_TREES_INITIAL_Y = -MM_MANY_TREES_HEIGHT - MM_TALL_GRASS_HEIGHT/1.4f;

    public static final float MM_FRONT_TREE_INITIAL_Y = -MM_FRONT_TREE_HEIGHT - MM_MANY_TREES_HEIGHT;

    public static final float MM_FRONT_GRASS_INITIAL_Y = -MM_FRONT_GRASS_HEIGHT - MM_MANY_TREES_HEIGHT;

    //public static final float MM_START_TXT_INITIAL_Y = -MM_START_TXT_HEIGHT;

    public static final float MM_MOUNTAIN_FINAL_Y = WORLD_SIZE * 0.18711296f;

    public static final float MM_TALL_GRASS_FINAL_Y = 0;

    public static final float MM_BACK_TREE_FINAL_Y = 0;

    public static final float MM_MANY_TREES_FINAL_Y = 0;

    public static final float MM_FRONT_TREE_FINAL_Y = WORLD_SIZE * 0.024197222f;

    public static final float MM_FRONT_GRASS_FINAL_Y = 0;

    public static final float MM_START_TXT_X_MARGIN_FROM_RIGHT = /*MM_START_TXT_WIDTH + */WORLD_SIZE * 0.12592593f;

    public static final float MM_START_TXT_FINAL_Y = WORLD_SIZE * 0.80679167f;

    public static final float MM_SURVIVAL_TXT_X_MARGIN_FROM_RIGHT = /*MM_START_TXT_WIDTH + */WORLD_SIZE * 0.12592593f;

    public static final float MM_SURVIVAL_TXT_FINAL_Y = WORLD_SIZE * 0.80679167f;

    public static final float MM_RESTRICTED_TXT_FINAL_Y = MM_SURVIVAL_TXT_FINAL_Y;

    public static final float MM_FREE_TXT_FINAL_Y = WORLD_SIZE * 0.67679167f;

    public static final float MM_PLANETS_TXT_FINAL_Y = MM_FREE_TXT_FINAL_Y;

    public static final float MM_CRYSTAL_TXT_FINAL_Y = MM_SURVIVAL_TXT_FINAL_Y;

    public static final float MM_T1_TXT_FINAL_Y = MM_FREE_TXT_FINAL_Y;

    public static final float MM_DIZZINESS_FINAL_Y = MM_FREE_TXT_FINAL_Y - 13f;

    public static final float MM_LAZER_FINAL_Y = MM_DIZZINESS_FINAL_Y - 13f;

    public static final float MM_PORTALS_FINAL_Y = MM_LAZER_FINAL_Y - 13f;

    public static final float MM_BIG_BOSS_FINAL_Y = MM_PORTALS_FINAL_Y - 13f;
    //-------
    public static final float MM_MOUNTAIN_X_MOVING_AMOUNT = WORLD_SIZE * 0.02f;

    public static final float MM_TALL_GRASS_X_MOVING_AMOUNT = WORLD_SIZE * 0.025f;

    public static final float MM_BACK_TREE_X_MOVING_AMOUNT = WORLD_SIZE * 0.03f;

    public static final float MM_MANY_TREES_X_MOVING_AMOUNT = WORLD_SIZE * 0.035f;

    public static final float MM_FRONT_TREE_X_MOVING_AMOUNT = WORLD_SIZE * 0.04f;

    public static final float MM_FRONT_GRASS_X_MOVING_AMOUNT = WORLD_SIZE * 0.045f;
    //-------
    public static final float MM_TILEABLE_WIDTH = WORLD_SIZE * MAX_ASPECT_RATIO_SUPPORTED;
    //-------


    public enum GameplayControllerType {FREE, RESTRICTED}
    public enum GameplayMode {SURVIVAL, CRYSTAL, DIZZINESS, LAZER, PORTALS, T1, BIG_BOSS, NETWORK_RECEIVER_VALUES_LOADER}


    public enum Direction {RIGHT, LEFT}
    //Controller size refers to the diameter of the BG.

    public static final float CONTROLLER_FREE_LARGE_SIZE = 0.4f; //Inches

    public static final float CONTROLLER_FREE_SMALL_SIZE = 0.25f; //Inches

    public static final float CONTROLLER_MARGIN = 0.45f; //Inches

    public static final float CONTROLLER_FREE_STICK_RATIO = 0.5f;

    public static final float CONTROLLER_RESTRICTED_ARCH_RADIUS = 1f; //Inches

    public static final float CONTROLLER_RESTRICTED_ARCH_INNER_RADIUS_RATIO = 0.93f;

    public static final float CONTROLLER_RESTRICTED_ARCH_ANGLE = 50f*MathUtils.degRad;

    public static final float CONTROLLER_RESTRICTED_SAVING_PPI = 50;

    public static final float CONTROLLER_RESTRICTED_90_SHIFT = 20;

    public static final float CONTROLLER_OUTPUT_ANGLE_INDICATOR_SIZE = WORLD_SIZE / 100f;



    public static final float TURRET_RADIUS = WORLD_SIZE / 15f;

    public static final int D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS = 6;

    public static final float D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL = 50; //sec

    public static final float BULLETS_DIFFICULTY_INCREASE_DURATION = 300f; //sec

    public static final MyInterpolation D_SURVIVAL_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_SURVIVAL_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;

    public static final int D_SURVIVAL_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_SURVIVAL_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_SURVIVAL_BULLETS_MAX_NUMBER_PER_ATTACK = D_SURVIVAL_BULLETS_MIN_NUMBER_PER_ATTACK + (D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_SURVIVAL_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_SURVIVAL_BULLETS_INITIAL_NO_PER_ATTACK = D_SURVIVAL_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final float BULLETS_DECREASE_NUMBER_PER_ATTACK_EVERY = D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL; //sec

    //public static final Interpolation BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS);
    public static final Interpolation D_SURVIVAL_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS,
            D_SURVIVAL_DIFFICULTY_TIME_SCALE,
            D_SURVIVAL_DIFFICULTY_OUTPUT_SCALE
    );


    public static final int SHIELDS_UNIVERSAL_MAX_COUNT = 8;



    public static final float D_SURVIVAL_BULLETS_SPEED_INITIAL = WORLD_SIZE / 3f; // per sec

    public static final float D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INITIAL = 1f;

    public static final float D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_MAX = D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_SURVIVAL_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final float BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY = D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL; //sec

    /*public static final Interpolation BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ExponentialInCurvesLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS, 0.1f, 5);*/
    public static final Interpolation D_SURVIVAL_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.RepeatedCurveCustomScaleIntervals(
            D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS,
            0.06f,
            new MyInterpolation.MyInterpolationIn.MyInterpolationIn(new MyInterpolation.MyExp(3)),
            D_SURVIVAL_DIFFICULTY_TIME_SCALE,
            D_SURVIVAL_DIFFICULTY_OUTPUT_SCALE
    );

    public static final int SURVIVAL_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int SURVIVAL_SHIELDS_MIN_COUNT = /*3*/4;





    public static final float BULLETS_ORDINARY_HEIGHT = WORLD_SIZE / 27.69230769f;

    public static final float BULLETS_ORDINARY_WIDTH_RATIO = 1f/3f;

    public static final float BULLETS_ORDINARY_WIDTH = BULLETS_ORDINARY_HEIGHT*BULLETS_ORDINARY_WIDTH_RATIO;

    public static final float BULLETS_SPECIAL_DIAMETER = WORLD_SIZE * 4f/80f;

    public static final float BULLETS_DISTANCE_BETWEEN_TWO = WORLD_SIZE / 60f;

    public static final float BULLETS_SPECIAL_WAVE_LENGTH = (BULLETS_ORDINARY_HEIGHT + BULLETS_DISTANCE_BETWEEN_TWO) * /*BULLETS_MIN_NUMBER_PER_ATTACK*/5;

    public static final float BULLETS_CLEARANCE_BETWEEN_WAVES = 1.5f * (BULLETS_ORDINARY_HEIGHT + BULLETS_DISTANCE_BETWEEN_TWO);

    public static final int BULLETS_POOL_INITIAL_CAPACITY = 60;

    public enum WaveAttackType {SINGLE, DOUBLE, ROUND}

    public enum RoundType {CLOCKWISE, ANTI_CLOCKWISE}

    public static final WaveAttackType[] WAVE_TYPES_PROBABILITY = {/*WaveAttackType.SINGLE,*/
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,

            WaveAttackType.DOUBLE,
            WaveAttackType.DOUBLE,

            //WaveAttackType.ROUND
    };

    public enum ContainerPositioning{RANDOM, RIGHT, LEFT}

    public enum WaveBulletsType {ORDINARY, SPECIAL_GOOD, SPECIAL_BAD}

    public enum SpecialBulletType {GOOD, BAD}

    public static final WaveBulletsType[] WAVE_BULLETS_TYPE_PROBABILITY = {
            WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,

            WaveBulletsType.SPECIAL_GOOD,

            WaveBulletsType.SPECIAL_BAD
    };
    public enum SpecialBullet {
        MINUS, HEART, STAR, ARMOR /*Lazer*/, // Good
        PLUS, BOMB, SHIELD_DISABLING, MIRROR /*Crystal*/, FASTER_DIZZINESS_ROTATION /*Dizziness*/, TWO_EXIT_PORTAL /*Portals*/, REWIND /*T1*/, // Bad
        QUESTION_MARK

    }

    public static final SpecialBullet[] GOOD_BULLETS_PROBABILITY = {
            DISMISS_STAR ? SpecialBullet.HEART : SpecialBullet.STAR,

            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,
            SpecialBullet.HEART,

            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,

            /*SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,
            SpecialBullet.MINUS,*/

            SpecialBullet.QUESTION_MARK,
            SpecialBullet.QUESTION_MARK,
            SpecialBullet.QUESTION_MARK,
            SpecialBullet.QUESTION_MARK
    };

    public static final SpecialBullet[] GOOD_BULLETS_PROBABILITY_NO_MINUS = MyMath.cloneAndReplace(GOOD_BULLETS_PROBABILITY, SpecialBullet.MINUS, SpecialBullet.HEART);

    public static final SpecialBullet[] BAD_BULLETS_PROBABILITY = {
            SpecialBullet.BOMB,
            SpecialBullet.BOMB,
            SpecialBullet.BOMB,
            SpecialBullet.BOMB,

            SpecialBullet.PLUS,
            SpecialBullet.PLUS,
            SpecialBullet.PLUS,
            SpecialBullet.PLUS,

            SpecialBullet.SHIELD_DISABLING,
            SpecialBullet.SHIELD_DISABLING,

            SpecialBullet.QUESTION_MARK,
            SpecialBullet.QUESTION_MARK
    };

    public static final SpecialBullet[] BAD_BULLETS_PROBABILITY_NO_PLUS = MyMath.cloneAndReplace(BAD_BULLETS_PROBABILITY, SpecialBullet.PLUS, SpecialBullet.BOMB);

    public static final float FORCED_SPECIAL_BULLET_QUESTION_MARK_PROBABILITY = 0.18f;

    public static final float BULLETS_ORDINARY_AFFECT_HEALTH_BY = NO_DEATH ? 0 : -0.04f;

    public static final float BULLETS_BOMB_AFFECT_HEALTH_BY = NO_DEATH ? 0 : -0.2f;

    public static final float BULLETS_HEART_AFFECT_HEALTH_BY = +0.07f;






    //public static final float SHIELDS_RADIUS = /*TURRET_RADIUS * 1.1f*/ TURRET_RADIUS * 1.3f;

    //public static final float SHIELDS_THICKNESS = WORLD_SIZE / 90f;

    public static final float SHIELDS_INNER_RADIUS = TURRET_RADIUS * 1.15f/*1.25f*/;

    public static final float SHIELDS_RADIUS = SHIELDS_INNER_RADIUS + WORLD_SIZE / 90f;

    public static final float SHIELDS_INNER_RADIUS_RATIO = SHIELDS_INNER_RADIUS/SHIELDS_RADIUS;

    public static final float SHIELDS_OMEGA_SETTER_PHI_MULTIPLIER = 10f;

    public static final float SHIELDS_FREE_ANGLE = 10f; //Deg

    public static final Color SHIELDS_COLOR = Color.WHITE;

    public static final float SHIELDS_SKIP_ANGLE_WHEN_SAVING = 3f; //Deg

    public static final String SHIELDS_NAMING_WHEN_SAVING = "Shields";

    public static final float SHIELDS_SAVING_FROM_ANGLE = 360f/ SURVIVAL_SHIELDS_MAX_COUNT;

    public static final float SHIELDS_SAVING_TO_ANGLE = 360f/ SURVIVAL_SHIELDS_MIN_COUNT;

    public static final int SHIELDS_ACTIVE_DEFAULT = 6;

    public static final float SHIELDS_ON_DISPLACEMENT = WORLD_SIZE / 100f;

    public static final float[] SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY = {0, 0, 0, 60f, 0, /*18f*/360f/5f/2f, 0, /*-12.85714286f*/0, 0};
    //SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[0] is the shift angle when the number of shields is 0, SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[1] is the shift angle when the number of shields is 1 and so on. It doesn't make sense to have 0 or 1 or 2 shields but this just makes the code simpler and more generic.

    //public static final float[] SHIELDS_SHIFT_ANGLES_RESTRICTED_GAMEPLAY = {0, 0, 0, 90f, 45f, 18f, 0, -12.85714286f, 0};
    public static final float[] SHIELDS_SHIFT_ANGLES_RESTRICTED_GAMEPLAY = {0, 0, 0, -60f, -45f, 0, 0+360f/6f/2f, 360f/7f, 360f/7f+360f/8f/3f};

    public static final float SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION = 650/*3000*/;

    public static final float SHIELD_DISABLED_DURATION = 2000;



    public static final float GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_WIDTH = TURRET_RADIUS*2 * 0.5f;

    public static final float GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_HEIGHT = TURRET_RADIUS*2 * 0.5f;

    public static final float GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_HEIGHT = TURRET_RADIUS/5;

    public static final float GAMEPLAY_SCREEN_TEMP_PROGRESS_BAR_PROGRESS_BAR_TOP_MARGIN = TURRET_RADIUS/10;



    //public static final float HEALTH_BAR_RADIUS = TURRET_RADIUS * 1.1f;

    //public static final float HEALTH_BAR_THICKNESS = TURRET_RADIUS * 1.2f - HEALTH_BAR_RADIUS;

    public static final float HEALTH_BAR_RADIUS = TURRET_RADIUS * 1.2f;

    public static final float HEALTH_BAR_INNER_RADIUS_RATIO = TURRET_RADIUS * 1.1f / HEALTH_BAR_RADIUS;

    public static final float HEALTH_BAR_DANGEROUS_ANGLE = 90*MathUtils.degRad;

    public static final Color HEALTH_BAR_DANGEROUS_ANGLE_COLOR = Color.RED;

    public static final Color HEALTH_BAR_COLOR = Color.WHITE;

    public static final Color HEALTH_BAR_HEALTH_OVER_FLOW_COLOR = Color.LIME;

    public static final int HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING = 4;

    public static final String HEALTH_BAR_NAMING_WHEN_SAVING = "HealthBar";

    public static final int HEALTH_BAR_SAVING_FROM_ANGLE = HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING;

    public static final int HEALTH_BAR_SAVING_TO_ANGLE = 360;

    public static final float HEALTH_BAR_TWEEN_DURATION = 200; // ms

    public static final Interpolation HEALTH_BAR_TWEEN_INTERPOLATION = Interpolation.exp5Out;



    public static final float STARS_SPEED = WORLD_SIZE / 70.0f;

    public static final float STARS_MAX_OFFSET_DISTANCE = WORLD_SIZE / 10.0f;

    public static final float STARS_MAX_RADIUS = WORLD_SIZE / 300.0f;

    public static final float STARS_MIN_RADIUS = WORLD_SIZE / 1800.0f;

    public static final float STARS_MOVING_ANGLE = 40f * MathUtils.degRad;

    public static final int STARS_COUNT = 400;

    public static final float STARS_POLAR_TWEEN_DURATION = SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION;

    public static final float STARS_POLAR_TWEEN_THETA_MAX = MathUtils.degreesToRadians * 200;

    public static final float STARS_POLAR_TWEEN_THETA_MINIMUM_MULTIPLIER = 0.17f;



    public static final float GAME_OVER_BG_R = WORLD_SIZE * (1125f/1080f);



    //public static final float FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED = 720f; // For this game it's equal for all the text.


    public static final float MY_PROGRESS_BAR_DEFAULT_PERCENTAGE_BAR_HEIGHT_RATIO = 0.6f;

    //public static final float SCORE_FONT_SCALE = 0.5f;

    public static final float SCORE_MULTIPLIER_MIN = 1f;

    public static final float SCORE_MULTIPLIER_INCREMENT = 0.5f;

    public static final float SCORE_MULTIPLIER_MAX = SCORE_MULTIPLIER_MIN + (D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS -1) * SCORE_MULTIPLIER_INCREMENT;

    //public static final Interpolation SCORE_MULTIPLIER_TWEEN_INTERPOLATION = new MyInterpolation.ConstantLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS);
    public static final Interpolation SURVIVAL_SCORE_MULTIPLIER_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS,
            D_SURVIVAL_DIFFICULTY_TIME_SCALE,
            D_SURVIVAL_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation SURVIVAL_SCORE_MULTIPLIER_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_SURVIVAL_NUMBER_OF_DIFFICULTY_LEVELS,
            0.3f/ D_SURVIVAL_DURATION_OF_EACH_DIFFICULTY_LEVEL,
            5,
            D_SURVIVAL_DIFFICULTY_TIME_SCALE,
            D_SURVIVAL_DIFFICULTY_OUTPUT_SCALE
    );



    public static final float SCORE_TXT_HEIGHT = WORLD_SIZE / 9f;

    public static final float SCORE_TXT_MARGIN = WORLD_SIZE / 35f;

    public static final float SCORE_FADE_OUT_TWEEN_DURATION = 700;

    public static final String SCORE_PREFERENCES_NAME = "Score";

    public static final String SCORE_BEST_KEY = "Best";

    public static final Color SCORE_COLOR = Color.WHITE;

    public static final Color SCORE_BEST_COLOR = HEALTH_BAR_HEALTH_OVER_FLOW_COLOR;


    public static final float GAMEOVER_LAYER_SCORE_TXT_HEIGHT = WORLD_SIZE / 8f;

    public static final float GAMEOVER_LAYER_NEW_BEST_TXT_HEIGHT = WORLD_SIZE * 0.053f;

    public static final float GAMEOVER_LAYER_TAP_ANY_WHERE_TO_TXT_HEIGHT = WORLD_SIZE * 70f/1440f;

    //public static final float GAMEOVER_LAYER_START_AGAIN_TXT_Y =

    public static final float GAMEOVER_LAYER_START_AGAIN_TXT_HEIGHT = GAMEOVER_LAYER_TAP_ANY_WHERE_TO_TXT_HEIGHT;

    public static final float GAMEOVER_LAYER_TAP_ANY_WHERE_TO_START_AGAIN_TXT_LINE_SPACING = WORLD_SIZE * 58f/1440f;


    public static final float BULLET_SPEED_MULTIPLIER_TXT_HEIGHT = WORLD_SIZE / 30f;

    public static final Color BULLET_SPEED_MULTIPLIER_TXT_COLOR = /*new Color(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1)*/Color.WHITE;

    public static final float BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_HEIGHT = WORLD_SIZE * 17f/1440f;

    public static final float BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_WIDTH = WORLD_SIZE * 130f/1440f;

    public static final Color BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_BG_COLOR = new Color(BG_COLOR_GREY, BG_COLOR_GREY, BG_COLOR_GREY, 1);

    public static final Color BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_PERCENTAGE_BAR_COLOR = Color.WHITE;

    public static final float BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TXT_DIFF_Y = WORLD_SIZE / 130f;

    public static final float BULLET_SPEED_MULTIPLIER_PROGRESS_BAR_TWEEN_DURATION = 200;


    public static final float PAUSE_SYMBOL_MARGIN_RIGHT = SCORE_TXT_MARGIN;

    public static final float PAUSE_SYMBOL_MARGIN_UP = SCORE_TXT_MARGIN;

    public static final float PAUSE_SYMBOL_HEIGHT = WORLD_SIZE * 79f/1080f;

    public static final float PAUSE_SYMBOL_WIDTH = PAUSE_SYMBOL_HEIGHT * 58f/79f;

    public static final float PAUSE_TEXT_HEIGHT = WORLD_SIZE * 0.25f;

    public static final float PAUSE_3_2_1_HEIGHT = TURRET_RADIUS * 2 * 1.3f;

    public static final float PAUSE_MENU_BUTTON_HEIGHT = WORLD_SIZE / 10f;

    public static final float PAUSE_MENU_Y = WORLD_SIZE / 6f;

    public static final float PAUSE_MENU_MARGIN_BETWEEN_BUTTONS = WORLD_SIZE / 20f;



    public static final float STAR_BULLET_SCORE_BONUS = 50f; //seconds

    public static final int STAR_BULLET_FIRST_STAGE_DURATION = 1500;

    public static final int STAR_BULLET_SECOND_STAGE_DURATION = 2500;

    public static final int STAR_BULLET_THIRD_STAGE_DURATION = 2000;

    public static final int STAR_BULLET_TOTAL_DURATION = STAR_BULLET_FIRST_STAGE_DURATION + STAR_BULLET_SECOND_STAGE_DURATION + STAR_BULLET_THIRD_STAGE_DURATION;

    /*public static final float STAR_BULLET_TRAIL_WARP_VELOCITY_MULTIPLIER_INITIAL_VALUE = 3;

    public static final float STAR_BULLET_TRAIL_WARP_VELOCITY_MULTIPLIER_FINAL_VALUE = 60;*/

    public static final int STAR_BULLET_TRAIL_WARP_BLUR_KERNEL_SIZE = 11;

    public static final float STAR_BULLET_TRAIL_WARP_BLUR_RESOLUTION_DIVISOR = 6f;

    public static final float STAR_BULLET_TRAIL_WARP_TOTAL_DISTANCE = WORLD_SIZE / 8f;

    public static final MyInterpolation STAR_BULLET_FIRST_STAGE_INTERPOLATION = MyInterpolation.myLinear;

    public static final MyInterpolation STAR_BULLET_FIRST_STAGE_INTERPOLATION_INTEGRATION_OUT = MyInterpolation.myPow2Out;

    // The further the star from the center, the faster it'll travel. That's why we have STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MAX and STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MIN.
    public static final float STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MAX = WORLD_SIZE * 9f;

    public static final float STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MIN = STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_MAX / 3.5f;

    public static final MyInterpolation STAR_BULLET_FAST_FORWARD_WARP_VELOCITY_MULTIPLIER_POLAR_INTERPOLATION = MyInterpolation.myPow3In;

    // The further the star from the center, the bigger its size will be. That's why we have STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MAX and STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MIN.
    public static final float STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MAX = STARS_MAX_RADIUS * 1.1f;

    public static final float STAR_BULLET_FAST_FORWARD_WARP_RADIUS_MIN = STARS_MIN_RADIUS / 1f;

    public static final Interpolation STAR_BULLET_SECOND_STAGE_INTERPOLATION = /*MyInterpolation.myReciprocal_950In*/new Star.TrailWarpInterpolation(0.9f, 22);

    public static final float STAR_BULLET_SECOND_STAGE_WHITE_TEXTURE_HIDES_EVERYTHING_DELAY_PERCENTAGE = 0.95f;

    public static final MyInterpolation STAR_BULLET_THIRD_STAGE_INTERPOLATION_IN = new MyInterpolation.MyInterpolationIn(new MyInterpolation.MyPow(20));

    public static final Interpolation STAR_BULLET_THIRD_STAGE_SCORE_INTERPOLATION = new ScoreTimerStuff.TimePlayedSoFarStarBulletThirdStageInterpolation();



    public static final float PLANETS_TIMER_FLASHES_WHEN_ZERO_DURATION = 0.5f/*0.1f*/; //sec

    public static final Interpolation PLANETS_TIMER_FLASHES_WHEN_ZERO_INTERPOLATION = new MyInterpolation.ConstantLinearTimeLinearOutput(2);

    public static final float PLANETS_QUESTION_MARK_SPECIAL_BULLETS_PROBABILITY = 0.2f;








    public static final float LEVEL_FINISH_FINISH_TEXT_TWEEN_DURATION = 350; //ms

    public static final Interpolation LEVEL_FINISH_FINISH_TEXT_TWEEN_INTERPOLATION = new MyInterpolation.MyPulses(1);

    public static final float LEVEL_FINISH_FINISH_TEXT_INITIAL_ALPHA = 0.55f;

    public static final float LEVEL_FINISH_FINISH_TEXT_FINAL_ALPHA = 1f;

    public static final float LEVEL_FINISH_FINISH_TEXT_INITIAL_HEIGHT = 0.485f * TURRET_RADIUS;

    public static final float LEVEL_FINISH_FINISH_TEXT_FINAL_HEIGHT = 0.5f * TURRET_RADIUS;




    public static final float SPECIAL_BULLET_UI_MARGIN_BETWEEN_ACTORS = SCORE_TXT_MARGIN;

    public static final float SPECIAL_BULLET_UI_X = WORLD_SIZE * 0.415f;

    public static final float SPECIAL_BULLET_UI_Y = WORLD_SIZE - (SCORE_TXT_MARGIN + SCORE_TXT_HEIGHT /*+ BULLET_SPEED_MULTIPLIER_TXT_HEIGHT*/);

    public static final float SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH = WORLD_SIZE / 15f;

    public static final float SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_PROGRESS_BAR_HEIGHT = WORLD_SIZE / 40f;

    public static final float SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_MARGIN_BETWEEN_IMAGE_AND_PROGRESS_BAR = WORLD_SIZE / 120f;

    public static final float TWO_EXIT_PORTAL_UI_GLOW_DURATION = 0.4f; // sec






    public static final String DIFFICULTY_PREFIX = "Difficulty: ";



    public static final float CRYSTAL_LEVEL_TIME = 5/*0.04f*//*0.5f*/; //minutes

    public static final SpecialBullet[] CRYSTAL_SPECIAL_BULLETS = {SpecialBullet.MIRROR};

    public static final int CRYSTAL_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int CRYSTAL_SHIELDS_MIN_COUNT = 4;


    public static final int D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS = 6;

    public static final MyInterpolation D_CRYSTAL_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;


    public static final int D_CRYSTAL_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_CRYSTAL_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_CRYSTAL_BULLETS_MAX_NUMBER_PER_ATTACK = D_CRYSTAL_BULLETS_MIN_NUMBER_PER_ATTACK + (D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_CRYSTAL_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_CRYSTAL_BULLETS_INITIAL_NO_PER_ATTACK = D_CRYSTAL_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_CRYSTAL_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_MAX = D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_CRYSTAL_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    /*public static final Interpolation D_CRYSTAL_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.RepeatedCurveCustomScaleIntervals(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            0.15f,
            new MyInterpolation.MyInterpolationIn.MyInterpolationIn(new MyInterpolation.MyExp(3)),
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );*/

    public static final Interpolation D_CRYSTAL_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_CRYSTAL_FAKE_WAVE_PROBABILITY_INITIAL = 0.1f;

    public static final float D_CRYSTAL_FAKE_WAVE_PROBABILITY_MAX = 0.35f;

    public static final Interpolation D_CRYSTAL_FAKE_WAVE_PROBABILITY_DIFFICULTY_CURVE = new MyInterpolation.RepeatedCurveCustomScaleIntervals(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            0.15f,
            new MyInterpolation.MyInterpolationIn(new MyInterpolation.MyExp(3)),
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );

    public static final MyInterpolation.ConstantExponentialInTimeLinearOutput D_CRYSTAL_NUMBER_OF_FAKE_WAVES_PROBABILITY = new MyInterpolation.ConstantExponentialInTimeLinearOutput(SHIELDS_ACTIVE_DEFAULT, 2); // parameter n will be changed during the gameplay.

    public static final float D_CRYSTAL_FAKE_TWEEN_DURATION = 550; // ms

    public static final Interpolation CRYSTAL_FAKE_TWEEN_INTERPOLATION = MyInterpolation.threePulses;


    public static final float D_CRYSTAL_SPECIAL_BULLETS_PROBABILITY = 0.25f;

    public static final float D_CRYSTAL_MIRROR_CONTROLS_DURATION = 1750/*1750000*/; // ms


    public static final Interpolation D_CRYSTAL_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation D_CRYSTAL_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_CRYSTAL_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_CRYSTAL_DIFFICULTY_TIME_SCALE,
            D_CRYSTAL_DIFFICULTY_OUTPUT_SCALE
    );









    public static final float DIZZINESS_LEVEL_TIME = 5; //minutes

    public static final int DIZZINESS_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int DIZZINESS_SHIELDS_MIN_COUNT = 4;

    public static final int D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS = 6;


    public static final SpecialBullet[] DIZZINESS_SPECIAL_BULLETS = {SpecialBullet.FASTER_DIZZINESS_ROTATION};

    public static final float D_DIZZINESS_SPECIAL_BULLETS_PROBABILITY = 0.25f;

    public static final float D_DIZZINESS_FASTER_ROTATIONAL_SPEED_BULLET_DURATION = 3000; // ms

    public static final float D_DIZZINESS_FASTER_ROTATIONAL_SPEED_BULLET_MULTIPLIER = 2f;


    public static final MyInterpolation D_DIZZINESS_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;

    //public static final float DIZZINESS_DIZZINESS_ROTATIONAL_SPEED = -12; //deg/sec


    public static final float D_DIZZINESS_ROTATIONAL_SPEED_MIN = -6f; // deg/sec

    public static final float D_DIZZINESS_ROTATIONAL_SPEED_INCREMENT = -3; // deg/sec

    public static final float D_DIZZINESS_ROTATIONAL_SPEED_MAX = D_DIZZINESS_ROTATIONAL_SPEED_MIN + (D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_DIZZINESS_ROTATIONAL_SPEED_INCREMENT;

    public static final Interpolation D_DIZZINESS_ROTATIONAL_SPEED_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );


    public static final int D_DIZZINESS_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_DIZZINESS_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_DIZZINESS_BULLETS_MAX_NUMBER_PER_ATTACK = D_DIZZINESS_BULLETS_MIN_NUMBER_PER_ATTACK + (D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_DIZZINESS_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_DIZZINESS_BULLETS_INITIAL_NO_PER_ATTACK = D_DIZZINESS_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_DIZZINESS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final float D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_MAX = D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_DIZZINESS_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    /*public static final Interpolation D_DIZZINESS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.RepeatedCurveCustomScaleIntervals(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.15f,
            new MyInterpolation.MyInterpolationIn.MyInterpolationIn(new MyInterpolation.MyExp(3)),
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );*/

    public static final Interpolation D_DIZZINESS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );




    public static final Interpolation D_DIZZINESS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation D_DIZZINESS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_DIZZINESS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_DIZZINESS_DIFFICULTY_TIME_SCALE,
            D_DIZZINESS_DIFFICULTY_OUTPUT_SCALE
    );









    public static final float LAZER_LEVEL_TIME = 5/*0.5f*/; //minutes

    public static final int LAZER_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int LAZER_SHIELDS_MIN_COUNT = 4;

    public static final int D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS = 6;


    public static final MyInterpolation D_LAZER_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_LAZER_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;


    public static final int LAZER_NUMBER_OF_LAZER_ATTACKS = D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS*2 - 1;

    public static final int LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR = 7;

    public static final int LAZER_MIN_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR = 5;

    public static final int LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR_INCREMRNT = 1;

    public static final float D_LAZER_PROVIDED_ARMOR_BULLETS_EACH_ATTACK_DECREMENT = 0.5f;

    //public static final float D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS = LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR + (LAZER_NUMBER_OF_LAZER_ATTACKS-1)*D_LAZER_PROVIDED_ARMOR_BULLETS_EACH_ATTACK_DECREMENT; // Nth term of an arithmetic sequence.
    public static final float D_LAZER_MAX_NUM_OF_PROVIDED_ARMOR_BULLETS = LAZER_MIN_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR + LAZER_NECESSARY_NUMBER_OF_ARMOR_BULLETS_TO_ACTIVATE_THE_LAZER_ARMOR_INCREMRNT*LAZER_NUMBER_OF_LAZER_ATTACKS;

    public static final float LAZER_LAZER_TIMER_DURATION = LAZER_LEVEL_TIME*60*1000/(LAZER_NUMBER_OF_LAZER_ATTACKS+1);



    public static final float LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT = WORLD_SIZE / 20f;

    public static final float LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_WIDTH = LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT * 0.93504589f;

    public static final float LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT = LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT * 1.819289476578635f;

    public static final float LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_WIDTH = LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT * 0.95890291f;

    public static final float LAZER_TEXT_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT = WORLD_SIZE / 28.5f;

    public static final float LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y = WORLD_SIZE * 0.505f;

    public static final float LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_Y = LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y - (LAZER_ARMOR_GLOWING_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT-LAZER_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_HEIGHT)/2f;

    public static final float LAZER_TEXT_ARMOR_BULLET_HOW_MANY_COLLECTED_UI_Y = WORLD_SIZE * 0.457f;

    public static final float LAZER_ALPHA_ACTION_DURATION = 0.25f; // sec

    public static final float LAZER_GUN_HEIGHT = WORLD_SIZE / 10f;

    public static final float LAZER_GUN_WIDTH = LAZER_GUN_HEIGHT * 187.587f/137.039f;

    public static final float LAZER_BEAM_HEIGHT = WORLD_SIZE / 95f;

    public static final float LAZER_GLOW_HEIGHT = WORLD_SIZE / 4f;

    public static final float LAZER_BEAM_GLOW_WIDTH_MULTIPLIER = 435.477f/314.675f;

    public static final float LAZER_HALO_ARMOR_HEIGHT = 2*TURRET_RADIUS*2f;

    public static final float LAZER_HALO_ARMOR_WIDTH = TURRET_RADIUS*2f;

    //public static final float LAZER_ATTACK_TOTAL_TIME = 2500f;

    public static final float LAZER_GUN_FADE_IN_TWEEN_DURATION = 400f;

    public static final float LAZER_GLOW_SHRINKING_TWEEN_DURATION = 1000f;

    public static final Interpolation LAZER_GLOW_SHRINKING_TWEEN_INTERPOLATION = Interpolation.exp5Out;

    public static final float LAZER_BEAM_SHRINKING_TWEEN_DURATION = 3200f;

    public static final Interpolation LAZER_BEAM_SHRINKING_TWEEN_INTERPOLATION = Interpolation.exp5In;

    public static final float LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_LOW = 0.8f;

    public static final float LAZER_BEAM_SHRINKING_HEIGHT_MULTIPLIER_HIGH = 1.2f;

    public static final float LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_DURATION = LAZER_BEAM_SHRINKING_TWEEN_DURATION*0.7f;

    public static final Interpolation LAZER_BEAM_VISIBILITY_PROBABILITY_TWEEN_INTERPOLATION = Interpolation.exp5In;

    public static final float LAZER_HALO_ARMOR_BLINKING_TWEEN_DURATION = LAZER_BEAM_SHRINKING_TWEEN_DURATION;

    public static final float LAZER_HALO_ARMOR_BLINKING_START_ALPHA = 0.6f;



    public static final float D_LAZER_YAMANI_HEALTH_AFFECTION_AMOUNT = -0.4f; // -40%



    public static final int D_LAZER_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_LAZER_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_LAZER_BULLETS_MAX_NUMBER_PER_ATTACK = D_LAZER_BULLETS_MIN_NUMBER_PER_ATTACK + (D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_LAZER_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_LAZER_BULLETS_INITIAL_NO_PER_ATTACK = D_LAZER_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_LAZER_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS,
            D_LAZER_DIFFICULTY_TIME_SCALE,
            D_LAZER_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_LAZER_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_LAZER_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_LAZER_BULLETS_SPEED_MULTIPLIER_MAX = D_LAZER_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_LAZER_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final Interpolation D_LAZER_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS,
            D_LAZER_DIFFICULTY_TIME_SCALE,
            D_LAZER_DIFFICULTY_OUTPUT_SCALE
    );
    
    


    public static final Interpolation D_LAZER_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS,
            D_LAZER_DIFFICULTY_TIME_SCALE,
            D_LAZER_DIFFICULTY_OUTPUT_SCALE
    );
    
    public static final Interpolation D_LAZER_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_LAZER_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_LAZER_DIFFICULTY_TIME_SCALE,
            D_LAZER_DIFFICULTY_OUTPUT_SCALE
    );








    public static final float PORTALS_LEVEL_TIME = 5;

    public static final int PORTALS_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int PORTALS_SHIELDS_MIN_COUNT = 4;

    public static final int D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS = 6;


    public static final float D_PORTALS_PORTAL_PROBABILITY = 0.3f;

    public enum BulletPortalType {PORTAL_ENTRANCE, PORTAL_EXIT} // PORTAL_ENTRANCE: When the bullet enters the portal, it'll disappear. PORTAL_EXIT: The bullet will come out of the portal and appear.

    public enum BulletPortalRole {CLOSE_ENTRANCE_PORTAL, OPEN_EXIT_PORTAL, CLOSE_EXIT_PORTAL, OPEN_AND_CLOSE_EXIT_PORTAL}

    public static final float D_PORTALS_ENTRANCE_EXIT_POSITION = WORLD_SIZE * 0.35f;

    public static final float PORTALS_ENTRANCE_EXIT_DIAMETER = TURRET_RADIUS * 1.25f;

    public static final float PORTALS_CONTAINER_PORTAL_ALPHA_ACTION_DURATION = 0.2f; // sec


    public static final SpecialBullet[] PORTALS_SPECIAL_BULLETS = {SpecialBullet.TWO_EXIT_PORTAL};

    public static final float D_PORTALS_SPECIAL_BULLETS_PROBABILITY = 0.25f;

    public static final int D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES = 3;


    public static final MyInterpolation D_PORTALS_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_PORTALS_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;


    public static final int D_PORTALS_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_PORTALS_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_PORTALS_BULLETS_MAX_NUMBER_PER_ATTACK = D_PORTALS_BULLETS_MIN_NUMBER_PER_ATTACK + (D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_PORTALS_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_PORTALS_BULLETS_INITIAL_NO_PER_ATTACK = D_PORTALS_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_PORTALS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_PORTALS_DIFFICULTY_TIME_SCALE,
            D_PORTALS_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_PORTALS_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_PORTALS_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_PORTALS_BULLETS_SPEED_MULTIPLIER_MAX = D_PORTALS_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_PORTALS_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final Interpolation D_PORTALS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_PORTALS_DIFFICULTY_TIME_SCALE,
            D_PORTALS_DIFFICULTY_OUTPUT_SCALE
    );


    
    
    public static final Interpolation D_PORTALS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_PORTALS_DIFFICULTY_TIME_SCALE,
            D_PORTALS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation D_PORTALS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_PORTALS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_PORTALS_DIFFICULTY_TIME_SCALE,
            D_PORTALS_DIFFICULTY_OUTPUT_SCALE
    );








    public static final float T1_LEVEL_TIME = 5/*0.5f*/;

    public static final int T1_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int T1_SHIELDS_MIN_COUNT = 4;

    public static final int D_T1_NUMBER_OF_DIFFICULTY_LEVELS = 6;

    public static final float D_T1_SPECIAL_BULLETS_PROBABILITY = 0.35f;

    public static final SpecialBullet[] T1_SPECIAL_BULLETS = {SpecialBullet.REWIND};


    //public static final float AFFECT_TIMER_TWEEN_DURATION = 500;

    public static final Interpolation AFFECT_TIMER_TWEEN_INTERPOLATION = new ScoreTimerStuff.AffectTimerInterpolation()/*new ScoreTimerStuff.TimePlayedSoFarStarBulletThirdStageInterpolation()*/;

    public static final float D_T1_AFFECT_TIMER_ORDINARY_AMOUNT = NO_DEATH ? 0 : -500;

    public static final float D_T1_AFFECT_TIMER_BOMB_AMOUNT = NO_DEATH ? 0 : -2500;

    public static final float D_T1_AFFECT_TIMER_HEART_AMOUNT = +1000;


    public static final Interpolation AFFECT_TIMER_COLOR_TWEEN_INTERPOLATION = Interpolation.pow4In;

    public static final float AFFECT_TIMER_COLOR_TWEEN_DURATION = 500;


    public static final MyInterpolation D_T1_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_T1_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;


    public static final int D_T1_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_T1_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_T1_BULLETS_MAX_NUMBER_PER_ATTACK = D_T1_BULLETS_MIN_NUMBER_PER_ATTACK + (D_T1_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_T1_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_T1_BULLETS_INITIAL_NO_PER_ATTACK = D_T1_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_T1_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_T1_NUMBER_OF_DIFFICULTY_LEVELS,
            D_T1_DIFFICULTY_TIME_SCALE,
            D_T1_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_T1_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_T1_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_T1_BULLETS_SPEED_MULTIPLIER_MAX = D_T1_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_T1_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_T1_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final Interpolation D_T1_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_T1_NUMBER_OF_DIFFICULTY_LEVELS,
            D_T1_DIFFICULTY_TIME_SCALE,
            D_T1_DIFFICULTY_OUTPUT_SCALE
    );
    
    

    public static final Interpolation D_T1_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_T1_NUMBER_OF_DIFFICULTY_LEVELS,
            D_T1_DIFFICULTY_TIME_SCALE,
            D_T1_DIFFICULTY_OUTPUT_SCALE
    );
    
    public static final Interpolation D_T1_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_T1_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_T1_DIFFICULTY_TIME_SCALE,
            D_T1_DIFFICULTY_OUTPUT_SCALE
    );










    public static final float BIG_BOSS_LEVEL_TIME = 5;

    public static final int BIG_BOSS_SHIELDS_MAX_COUNT = SHIELDS_UNIVERSAL_MAX_COUNT;

    public static final int BIG_BOSS_SHIELDS_MIN_COUNT = 4;

    public static final int D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS = 6;

    public static final float D_BIG_BOSS_SPECIAL_BULLETS_PROBABILITY = 0.35f;

    public static final SpecialBullet[] BIG_BOSS_SPECIAL_BULLETS = {SpecialBullet.MIRROR, SpecialBullet.FASTER_DIZZINESS_ROTATION, SpecialBullet.TWO_EXIT_PORTAL, SpecialBullet.REWIND};


    public static final float D_BIG_BOSS_AFFECT_TIMER_ORDINARY_AMOUNT = NO_DEATH ? 0 : -500;

    public static final float D_BIG_BOSS_AFFECT_TIMER_BOMB_AMOUNT = NO_DEATH ? 0 : -2500;

    public static final float D_BIG_BOSS_AFFECT_TIMER_HEART_AMOUNT = +1000;

    public static final float D_BIG_BOSS_AFFECT_TIMER_LAZER_AMOUNT = NO_DEATH ? 0 : -15000;

    public static final Interpolation BIG_BOSS_AFFECT_TIMER_LAZER_INTERPOLATION = new ScoreTimerStuff.TimePlayedSoFarStarBulletThirdStageInterpolation();


    public static final MyInterpolation D_BIG_BOSS_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;


    public static final float D_BIG_BOSS_ROTATIONAL_SPEED_MIN = -6f; // deg/sec

    public static final float D_BIG_BOSS_ROTATIONAL_SPEED_INCREMENT = -3; // deg/sec

    public static final float D_BIG_BOSS_ROTATIONAL_SPEED_MAX = D_BIG_BOSS_ROTATIONAL_SPEED_MIN + (D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_BIG_BOSS_ROTATIONAL_SPEED_INCREMENT;

    public static final Interpolation D_BIG_BOSS_ROTATIONAL_SPEED_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_BIG_BOSS_DIFFICULTY_TIME_SCALE,
            D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE
    );


    public static final int D_BIG_BOSS_BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int D_BIG_BOSS_BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int D_BIG_BOSS_BULLETS_MAX_NUMBER_PER_ATTACK = D_BIG_BOSS_BULLETS_MIN_NUMBER_PER_ATTACK + (D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS -1) * D_BIG_BOSS_BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int D_BIG_BOSS_BULLETS_INITIAL_NO_PER_ATTACK = D_BIG_BOSS_BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final Interpolation D_BIG_BOSS_BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_BIG_BOSS_DIFFICULTY_TIME_SCALE,
            D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE
    );


    public static final float D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_INITIAL = 1;

    public static final float D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_MAX = D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_INITIAL + (D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS-1) * D_BIG_BOSS_BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final Interpolation D_BIG_BOSS_BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleIntervals(
            D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_BIG_BOSS_DIFFICULTY_TIME_SCALE,
            D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation D_BIG_BOSS_DIFFICULTY_LEVEL_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleIntervals(
            D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS,
            D_BIG_BOSS_DIFFICULTY_TIME_SCALE,
            D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation D_BIG_BOSS_DIFFICULTY_LEVEL_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierDifficultyLevelStuff.ProgressBarTweenInterpolation(
            D_BIG_BOSS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.05f,
            2,
            D_BIG_BOSS_DIFFICULTY_TIME_SCALE,
            D_BIG_BOSS_DIFFICULTY_OUTPUT_SCALE
    );














    public static final int FINISH_WHEN_STOPPING_THE_GAMEPLAY_INITIAL_CAPACITY = 24;

    //public static final int PAUSE_WHEN_PAUSING_STAR_BULLET_INITIAL_CAPACITY = 16;








    public static final String ASSETS_LOGO_ALONE = "Logo.png";

    public static final String ASSETS_ALL = "All.atlas";

    public static final String ASSETS_LOGO = "Logo";

    public static final String ASSETS_MOUNTAIN = "Big Mountain in the Back";

    public static final String ASSETS_TREE_BACK = "Big Tree in the Back";

    public static final String ASSETS_TREE_FRONT = "Front Big Tree";

    public static final String ASSETS_FRONT_GRASS = "Front Grass";

    public static final String ASSETS_TALL_GRASS = "Middle Tall Grass";

    public static final String ASSETS_START = "START";

    public static final String ASSETS_SURVIVAL = "SURVIVAL";

    public static final String ASSETS_STAR = /*"Star"*/"Big Circle";

    public static final String ASSETS_MANY_TREES = "Too Many Trees";

    public static final String ASSETS_FREE_CONTROLLER_BG = "Free Controller BG";

    public static final String ASSETS_RESTRICTED_CONTROLLER_RIGHT_BG = "Restricted Controller Right BG";

    public static final String ASSETS_RESTRICTED_CONTROLLER_LEFT_BG = "Restricted Controller Left BG";

    public static final String ASSETS_CONTROLLER_STICK = "Free Controller Stick";

    public static final String ASSETS_TURRET = "Turret";

    public static final String ASSETS_BULLET = "Bullet";

    public static final String ASSETS_PLUS_BULLET = "Plus";

    public static final String ASSETS_MINUS_BULLET = "Minus";

    public static final String ASSETS_HEART_BULLET = "Heart";

    public static final String ASSETS_BOMB_BULLET = "Bomb";

    public static final String ASSETS_STAR_BULLET = "Star";

    public static final String ASSETS_QUESTION_MARK_BULLET = "Question Mark";

    public static final String ASSETS_SHIELD_DISABLING_BULLET = "Shield Disabling";

    public static final String ASSETS_MIRROR_BULLET = "Mirror";

    public static final String ASSETS_FASTER_DIZZINESS_ROTATION = "Faster Dizziness Rotation";

    public static final String ASSETS_ARMOR_BULLET = "Armor";

    public static final String ASSETS_2_EXIT_PORTAL_BULLET = "2 Exit Portal";

    public static final String ASSETS_2_EXIT_PORTAL_BULLET_GLOW = "2 Exit Portal Glow";

    public static final String ASSETS_REWIND_BULLET = "Rewind";

    public static final String ASSETS_PAUSE_TEXT = "Pause Text";

    public static final String ASSETS_PAUSE_SYMBOL = "Pause Symbol";

    public static final String ASSETS_PAUSE_1 = "1";

    public static final String ASSETS_PAUSE_2 = "2";

    public static final String ASSETS_PAUSE_3 = "3";

    public static final String ASSETS_PAUSE_RESTART = "Restart";

    public static final String ASSETS_PAUSE_RESUME = "Resume";

    public static final String ASSETS_PAUSE_HOME = "Home";

    public static final String ASSETS_DIMMING_OVERLAY = "Dimming Overlay";

    public static final String ASSETS_HEALTH_BAR = "Big Circle";

    public static final String ASSETS_FONT = "PublicSans-ExtraBoldItalic";

    public static final String ASSETS_FONT_FNT_INTERNAL = "PublicSans-ExtraBoldItalic.fnt";

    public static final String ASSETS_BIG_CIRCLE = "Big Circle";

    public static final String ASSETS_GAME_OVER_BG = ASSETS_BIG_CIRCLE;

    public static final String ASSETS_STAR_GLOW = "Star Glow";

    public static final String ASSETS_RESTRICTED = "RESTRICTED";

    public static final String ASSETS_FREE = "FREE";

    public static final String ASSETS_T1 = "T1";

    public static final String ASSETS_ARMOR_HALO = "Halo Armor";

    public static final String ASSETS_LAZER_GUN = "Lazer Gun";

    public static final String ASSETS_ARMOR_BLACK = "Armor Black";

    public static final String ASSETS_ARMOR_GLOWING = "Armor Glowing";

    public static final String ASSETS_LAZER_BEAM = "Lazer Beam";

    public static final String ASSETS_LAZER_GLOW = "Lazer Glow";

    public static final String ASSETS_PORTALS_ENTRANCE = "Portal Entrance";

    public static final String ASSETS_PORTALS_EXIT = "Portal Exit";

}
