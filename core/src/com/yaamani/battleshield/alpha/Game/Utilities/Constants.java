package com.yaamani.battleshield.alpha.Game.Utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreMultiplierStuff;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.ScoreStuff;
import com.yaamani.battleshield.alpha.Game.Starfield.Star;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.MyMath;

public final class Constants {

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



    public static final float PINK_TO_GREY_DURATION = 50/*0*/;

    public static final float STARS_FADE_IN_DURATION = 30/*0*/;

    public static final float STARS_UPWARDS_DURATION = 500/*0*/;

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

    public static final float MM_START_TXT_WIDTH = MM_START_TXT_HEIGHT * 3.98480139f;

    public static final float MM_SURVIVAL_TXT_HEIGHT = WORLD_SIZE * 0.1f;

    public static final float MM_SURVIVAL_TXT_WIDTH = MM_SURVIVAL_TXT_HEIGHT * 658.266f/108.484f;

    public static final float MM_PLANETS_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;

    public static final float MM_CRYSTAL_TXT_HEIGHT = MM_SURVIVAL_TXT_HEIGHT;
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
    public enum GameplayMode {SURVIVAL, CRYSTAL}


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



    public static final float TURRET_RADIUS = WORLD_SIZE / 15f;

    public static final int BULLETS_NUMBER_OF_DIFFICULTY_LEVELS = 6;

    public static final float BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL = 50; //sec

    public static final float BULLETS_DIFFICULTY_INCREASE_DURATION = 300f; //sec

    public static final MyInterpolation BULLETS_DIFFICULTY_TIME_SCALE = MyInterpolation.myLinear;

    public static final MyInterpolation BULLETS_DIFFICULTY_OUTPUT_SCALE = MyInterpolation.myLinear;

    public static final int BULLETS_MIN_NUMBER_PER_ATTACK = 1;

    public static final int BULLETS_NUMBER_PER_ATTACK_DECREMENT = 1;

    public static final int BULLETS_MAX_NUMBER_PER_ATTACK = BULLETS_MIN_NUMBER_PER_ATTACK + (BULLETS_NUMBER_OF_DIFFICULTY_LEVELS-1) * BULLETS_NUMBER_PER_ATTACK_DECREMENT;

    public static final int BULLETS_DEFAULT_NO_PER_ATTACK = BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final float BULLETS_DECREASE_NUMBER_PER_ATTACK_EVERY = BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL; //sec

    //public static final Interpolation BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS);
    public static final Interpolation BULLETS_DECREASE_NUMBER_PER_ATTACK_DIFFICULTY_CURVE = new MyInterpolation.ConstantCustomScaleSteps(
            BULLETS_NUMBER_OF_DIFFICULTY_LEVELS,
            BULLETS_DIFFICULTY_TIME_SCALE,
            BULLETS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final float BULLETS_SPEED_INITIAL = WORLD_SIZE / 3f; // per sec

    public static final float BULLETS_SPEED_MULTIPLIER_INITIAL = 1f;

    public static final float BULLETS_SPEED_MULTIPLIER_INCREMENT = 0.2f;

    public static final float BULLETS_SPEED_MULTIPLIER_MAX = BULLETS_SPEED_MULTIPLIER_INITIAL + (BULLETS_NUMBER_OF_DIFFICULTY_LEVELS-1) * BULLETS_SPEED_MULTIPLIER_INCREMENT;

    public static final float BULLETS_UPDATE_SPEED_MULTIPLIER_EVERY = BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL; //sec

    /*public static final Interpolation BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.ExponentialInCurvesLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS, 0.1f, 5);*/
    public static final Interpolation BULLETS_INCREASE_SPEED_MULTIPLIER_DIFFICULTY_CURVE = new MyInterpolation.RepeatedCurveCustomScaleSteps(
            BULLETS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.06f,
            new MyInterpolation.MyInterpolationIn.MyInterpolationIn(new MyInterpolation.MyExp(3)),
            BULLETS_DIFFICULTY_TIME_SCALE,
            BULLETS_DIFFICULTY_OUTPUT_SCALE
    );
    public static final float BULLETS_ORDINARY_HEIGHT = WORLD_SIZE / 27.69230769f;

    public static final float BULLETS_ORDINARY_WIDTH_RATIO = 1f/3f;

    public static final float BULLETS_ORDINARY_WIDTH = BULLETS_ORDINARY_HEIGHT*BULLETS_ORDINARY_WIDTH_RATIO;

    public static final float BULLETS_SPECIAL_DIAMETER = WORLD_SIZE * 4f/80f;

    public static final float BULLETS_DISTANCE_BETWEEN_TWO = WORLD_SIZE / 60f;

    public static final float BULLETS_SPECIAL_WAVE_LENGTH = (BULLETS_ORDINARY_HEIGHT + BULLETS_DISTANCE_BETWEEN_TWO) * /*BULLETS_MIN_NUMBER_PER_ATTACK*/5;

    public static final float BULLETS_CLEARANCE_BETWEEN_WAVES = 1 * (BULLETS_ORDINARY_HEIGHT + BULLETS_DISTANCE_BETWEEN_TWO);

    public static final int BULLETS_POOL_INITIAL_CAPACITY = 60;

    public enum WaveAttackType {SINGLE, DOUBLE, ROUND}

    public enum RoundType {CLOCKWISE, ANTI_CLOCKWISE}

    public static final WaveAttackType[] WAVE_TYPES_PROBABILITY = {/*WaveAttackType.SINGLE,*/
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,

            WaveAttackType.DOUBLE,
            WaveAttackType.DOUBLE,

            WaveAttackType.ROUND
    };

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
        MINUS, HEART, STAR, // Good
        PLUS, BOMB, SHIELD_DISABLING, MIRROR /*Crystal planet only*/, // Bad
        QUESTION_MARK
    }

    public static final SpecialBullet[] GOOD_BULLETS_PROBABILITY = {
            SpecialBullet.STAR,

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

    public static final float BULLETS_ORDINARY_AFFECT_HEALTH_BY = -/*0.04f*/0;

    public static final float BULLETS_BOMB_AFFECT_HEALTH_BY = -/*0.2f*/0;

    public static final float BULLETS_HEART_AFFECT_HEALTH_BY = +0.07f;






    //public static final float SHIELDS_RADIUS = /*TURRET_RADIUS * 1.1f*/ TURRET_RADIUS * 1.3f;

    //public static final float SHIELDS_THICKNESS = WORLD_SIZE / 90f;

    public static final float SHIELDS_INNER_RADIUS = TURRET_RADIUS * 1.15f;

    public static final float SHIELDS_RADIUS = SHIELDS_INNER_RADIUS + WORLD_SIZE / 90f;

    public static final float SHIELDS_INNER_RADIUS_RATIO = SHIELDS_INNER_RADIUS/SHIELDS_RADIUS;

    public static final float SHIELDS_OMEGA_SETTER_PHI_MULTIPLIER = 10f;

    public static final float SHIELDS_FREE_ANGLE = 10f; //Deg

    public static final Color SHIELDS_COLOR = Color.WHITE;

    public static final int SHIELDS_MAX_COUNT = 8;

    public static final int SHIELDS_MIN_COUNT = 3/*2*/;

    public static final float SHIELDS_SKIP_ANGLE_WHEN_SAVING = 3f; //Deg

    public static final String SHIELDS_NAMING_WHEN_SAVING = "Shields";

    public static final float SHIELDS_SAVING_FROM_ANGLE = 360f/SHIELDS_MAX_COUNT;

    public static final float SHIELDS_SAVING_TO_ANGLE = 360f/SHIELDS_MIN_COUNT;

    public static final int SHIELDS_ACTIVE_DEFAULT = 6;

    public static final float SHIELDS_ON_DISPLACEMENT = WORLD_SIZE / 100f;

    public static final float[] SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY = {60f, 0, /*18f*/360f/5f/2f, 0, /*-12.85714286f*/0, 0};
    //SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[0] is the shift angle when the number of shields = SHIELDS_MIN_COUNT ..... And SHIELDS_SHIFT_ANGLES_FREE_GAMEPLAY[length-1] is the shift angle  when the number of shields = SHIELDS_MAX_COUNT.

    public static final float[] SHIELDS_SHIFT_ANGLES_RESTRICTED_GAMEPLAY = {/*90f, 45f, 18f, 0, -12.85714286f, 0*/-60f, -45f, 0, 360f/6f/2f, 360f/7f, 360f/8f};

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

    public static final float SCORE_MULTIPLIER_MAX = SCORE_MULTIPLIER_MIN + (BULLETS_NUMBER_OF_DIFFICULTY_LEVELS-1) * SCORE_MULTIPLIER_INCREMENT;

    //public static final Interpolation SCORE_MULTIPLIER_TWEEN_INTERPOLATION = new MyInterpolation.ConstantLinearTimeLinearOutput(BULLETS_NUMBER_OF_DIFFICULTY_LEVELS);
    public static final Interpolation SCORE_MULTIPLIER_TWEEN_INTERPOLATION = new MyInterpolation.ConstantCustomScaleSteps(
            BULLETS_NUMBER_OF_DIFFICULTY_LEVELS,
            BULLETS_DIFFICULTY_TIME_SCALE,
            BULLETS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final Interpolation SCORE_MULTIPLIER_PROGRESS_BAR_TWEEN_INTERPOLATION = new ScoreMultiplierStuff.ProgressBarTweenInterpolation(
            BULLETS_NUMBER_OF_DIFFICULTY_LEVELS,
            0.3f/BULLETS_DURATION_OF_EACH_DIFFICULTY_LEVEL,
            5,
            BULLETS_DIFFICULTY_TIME_SCALE,
            BULLETS_DIFFICULTY_OUTPUT_SCALE
    );

    public static final float SCORE_TXT_HEIGHT = WORLD_SIZE / 9f;

    public static final float SCORE_TXT_MARGIN = WORLD_SIZE / 40f;

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


    public static final float BULLET_SPEED_MULTIPLIER_TXT_HEIGHT = WORLD_SIZE / 31.5f;

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

    public static final float PAUSE_TEXT_HEIGHT = WORLD_SIZE * 422f/1080f;

    public static final float PAUSE_TEXT_WIDTH = PAUSE_TEXT_HEIGHT * 900f/422f;

    public static final float PAUSE_3_2_1_HEIGHT = TURRET_RADIUS * 2 * 0.7f;



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

    public static final Interpolation STAR_BULLET_THIRD_STAGE_SCORE_INTERPOLATION = new ScoreStuff.TimePlayedSoFarStarBulletThirdStageInterpolation();



    public static final float PLANETS_TIMER_FLASHES_WHEN_ZERO_DURATION = 0.5f/*0.1f*/; //sec

    public static final Interpolation PLANETS_TIMER_FLASHES_WHEN_ZERO_INTERPOLATION = new MyInterpolation.ConstantLinearTimeLinearOutput(2);

    public static final float PLANETS_QUESTION_MARK_SPECIAL_BULLETS_PROBABILITY = 0.2f;



    public static final float CRYSTAL_LEVEL_TIME = 5; //minutes

    public static final int CRYSTAL_LEVEL_DIFFICULTY_LEVELS = 6;

    public static final SpecialBullet[] CRYSTAL_PLANET_SPECIAL_BULLETS = {SpecialBullet.MIRROR};

    public static final float CRYSTAL_PLANET_SPECIAL_BULLETS_PROBABILITY = 0.3f;

    public static final float CRYSTAL_MIRROR_CONTROLS_DURATION = 2000; //ms





    public static final int PAUSE_WHEN_PAUSING_FINISH_WHEN_LOSING_INITIAL_CAPACITY = 24;

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

    public static final String ASSETS_PAUSE_TEXT = "Pause Text";

    public static final String ASSETS_PAUSE_SYMBOL = "Pause Symbol";

    public static final String ASSETS_DIMMING_OVERLAY = "Dimming Overlay";

    public static final String ASSETS_HEALTH_BAR = "Big Circle";

    public static final String ASSETS_FONT = "Uni Neue Bold Italic";

    public static final String ASSETS_FONT_FNT_INTERNAL = "Uni Neue Bold Italic.fnt";

    public static final String ASSETS_BIG_CIRCLE = "Big Circle";

    public static final String ASSETS_GAME_OVER_BG = ASSETS_BIG_CIRCLE;

    public static final String ASSETS_STAR_GLOW = "Star Glow";

    public static final String ASSETS_RESTRICTED = "RESTRICTED";

    public static final String ASSETS_FREE = "FREE";

}
