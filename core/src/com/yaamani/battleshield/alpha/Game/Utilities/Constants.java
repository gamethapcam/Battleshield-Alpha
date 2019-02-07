package com.yaamani.battleshield.alpha.Game.Utilities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

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

    public static final float START_TXT_APPEAR_DURATION = 25/*0*/;



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

    public static final float MM_FREE_TXT_FINAL_Y = WORLD_SIZE * 0.67679167f;
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


    public enum ControllerPosition {RIGHT, LEFT}
    //Controller size refers to the diameter of the BG.
    public enum ControllerSize {SMALL, LARGE}

    public static final ControllerSize CONTROLLER_DEFAULT_SIZE = ControllerSize.LARGE;

    public static final float CONTROLLER_LARGE_SIZE = 0.4f; //Inches

    public static final float CONTROLLER_SMALL_SIZE = 0.25f; //Inches

    public static final float CONTROLLER_MARGIN = 0.45f; //Inches

    public static final float CONTROLLER_STICK_RATIO = 0.5f; //Inches



    public static final float TURRET_RADIUS = WORLD_SIZE / 15f;

    public static final int BULLETS_MAX_NUMBER_PER_ATTACK = 10;

    public static final int BULLETS_DEFAULT_NO_PER_ATTACK = BULLETS_MAX_NUMBER_PER_ATTACK;

    public static final int BULLETS_MIN_NUMBER_PER_ATTACK = 3;

    public static final float BULLETS_DECREASE_NO_PER_ATTACK_EVERY = 3000/*000*/;

    public static final float BULLETS_ORDINARY_HEIGHT = WORLD_SIZE / 80f;

    public static final float BULLETS_ORDINARY_WIDTH_RATIO = 3f/1f;

    public static final float BULLETS_ORDINARY_WIDTH = BULLETS_ORDINARY_HEIGHT*BULLETS_ORDINARY_WIDTH_RATIO;

    public static final float BULLETS_SPECIAL_DIAMETER = WORLD_SIZE * 4f/80f;

    public static final float BULLETS_DISTANCE_BETWEEN_TWO = WORLD_SIZE / 60f;

    public static final float BULLETS_SPECIAL_WAVE_LENGTH = (BULLETS_ORDINARY_WIDTH + BULLETS_DISTANCE_BETWEEN_TWO) * BULLETS_MIN_NUMBER_PER_ATTACK;

    public static final float BULLETS_CLEARANCE_BETWEEN_WAVES = 1 * (BULLETS_ORDINARY_WIDTH + BULLETS_DISTANCE_BETWEEN_TWO);

    public static final float BULLETS_SPEED = WORLD_SIZE / 2.5f;

    public static final int BULLETS_POOL_INITIAL_CAPACITY = 50;

    public enum WaveAttackType {SINGLE, DOUBLE, ROUND}

    public enum RoundType {CLOCKWISE, ANTI_CLOCKWISE}

    public static final WaveAttackType[] WAVE_TYPES_PROBABILITY = {WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,
            WaveAttackType.SINGLE,
            WaveAttackType.DOUBLE,
            WaveAttackType.DOUBLE,
            WaveAttackType.ROUND};

    public enum WaveBulletsType {ORDINARY, SPECIAL_GOOD, SPECIAL_BAD}

    public enum SpecialBullet {MINUS, HEART,
                               PLUS, BOMB}

    public static final SpecialBullet[] GOOD_BULLETS_PROBABILITY = {SpecialBullet.HEART, SpecialBullet.MINUS};

    public static final SpecialBullet[] BAD_BULLETS_PROBABILITY = {SpecialBullet.BOMB, SpecialBullet.PLUS};

    public static final WaveBulletsType[] WAVE_BULLETS_TYPE_PROBABILITY = {WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,
            WaveBulletsType.ORDINARY,
            WaveBulletsType.SPECIAL_GOOD,
            WaveBulletsType.SPECIAL_BAD};

    public static final float BULLETS_ORDINARY_AFFECT_HEALTH_BY = -0.04f/*0*/;

    public static final float BULLETS_BOMB_AFFECT_HEALTH_BY = -0.2f/*0*/;

    public static final float BULLETS_HEART_AFFECT_HEALTH_BY = +0.07f;






    //public static final float SHIELDS_RADIUS = /*TURRET_RADIUS * 1.1f*/ TURRET_RADIUS * 1.3f;

    //public static final float SHIELDS_THICKNESS = WORLD_SIZE / 90f;

    public static final float SHIELDS_INNER_RADIUS = TURRET_RADIUS * 1.3f;

    public static final float SHIELDS_RADIUS = SHIELDS_INNER_RADIUS + WORLD_SIZE / 90f;

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

    public static final float[] SHIELDS_SHIFT_ANGLES = {30f, 0, 18f, 0, -12.85714286f, 0};
    //SHIELDS_SHIFT_ANGLES[0] is the shift angle when the number of shields = SHIELDS_MIN_COUNT ..... And SHIELDS_SHIFT_ANGLES[length-1] is the shift angle  when the number of shields = SHIELDS_MAX_COUNT.

    public static final float SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION = 800;



    //public static final float HEALTH_BAR_RADIUS = TURRET_RADIUS * 1.1f;

    //public static final float HEALTH_BAR_THICKNESS = TURRET_RADIUS * 1.2f - HEALTH_BAR_RADIUS;

    public static final float HEALTH_BAR_RADIUS = TURRET_RADIUS * 1.2f;

    public static final float HEALTH_BAR_INNER_RADIUS = TURRET_RADIUS * 1.1f;

    public static final float HEALTH_BAR_DANGEROUS_ANGLE = 90*MathUtils.degRad;

    public static final Color HEALTH_BAR_DANGEROUS_ANGLE_COLOR = Color.RED;

    public static final Color HEALTH_BAR_COLOR = Color.WHITE;

    public static final Color HEALTH_BAR_HEALTH_OVER_FLOW_COLOR = Color.LIME;

    public static final int HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING = 4;

    public static final String HEALTH_BAR_NAMING_WHEN_SAVING = "HealthBar";

    public static final int HEALTH_BAR_SAVING_FROM_ANGLE = HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING;

    public static final int HEALTH_BAR_SAVING_TO_ANGLE = 360;



    public static final float STAR_MAX_SPEED = WORLD_SIZE / 70.0f;

    public static final float STAR_MAX_OFFSET_DISTANCE = WORLD_SIZE / 10.0f;

    public static final float STAR_MAX_RADIUS = WORLD_SIZE / 200.0f;

    public static final int STARS_COUNT = 500;

    public static final float STARS_RADIAL_TWEEN_DURATION = SHIELDS_ROTATION_OMEGA_ALPHA_TWEEN_DURATION;

    public static final float STARS_RADIAL_TWEEN_THETA_MAX = MathUtils.degreesToRadians * 200;

    public static final float STARS_RADIAL_TWEEN_THETA_MINIMUM_MULTIPLIER = 0.17f;



    public static final float GAME_OVER_BG_R = WORLD_SIZE * (1125f/1080f);



    public static final float FONT_THE_RESOLUTION_AT_WHICH_THE_SCALE_WAS_DECIDED = 720f; // For this game it's equal for all the text.



    public static final float SCORE_FONT_SCALE = 1f;

    public static final float SCORE_FADE_OUT_TWEEN_DURATION = 700;

    public static final String SCORE_PREFERENCES_NAME = "Score";

    public static final String SCORE_BEST_KEY = "Best";

    public static final Color SCORE_COLOR = Color.WHITE;

    public static final Color SCORE_BEST_COLOR = HEALTH_BAR_HEALTH_OVER_FLOW_COLOR;



    public static final String ASSETS_LOGO_ALONE = "Logo.png";

    public static final String ASSETS_ALL = "All.atlas";

    public static final String ASSETS_LOGO = "Logo";

    public static final String ASSETS_MOUNTAIN = "Big Mountain in the Back";

    public static final String ASSETS_TREE_BACK = "Big Tree in the Back";

    public static final String ASSETS_TREE_FRONT = "Front Big Tree";

    public static final String ASSETS_FRONT_GRASS = "Front Grass";

    public static final String ASSETS_TALL_GRASS = "Middle Tall Grass";

    public static final String ASSETS_START = "START";

    public static final String ASSETS_STAR = /*"Star"*/"Big Circle";

    public static final String ASSETS_MANY_TREES = "Too Many Trees";

    public static final String ASSETS_CONTROLLER_BG = "Controller BG";

    public static final String ASSETS_CONTROLLER_STICK = "Controller Stick";

    public static final String ASSETS_TURRET = "Turret";

    public static final String ASSETS_BULLET = "Bullet";

    public static final String ASSETS_PLUS_BULLET = "Plus";

    public static final String ASSETS_MINUS_BULLET = "Minus";

    public static final String ASSETS_HEART_BULLET = "Heart";

    public static final String ASSETS_BOMB_BULLET = "Bomb";

    public static final String ASSETS_HEALTH_BAR = "Big Circle";

    public static final String ASSETS_FONT = "Uni Neue Bold Italic";

    public static final String ASSETS_FONT_FNT_INTERNAL = "Uni Neue Bold Italic.fnt";

    public static final String ASSETS_GAME_OVER_BG = "Big Circle";

    public static final String ASSETS_RESTRICTED = "RESTRICTED";

    public static final String ASSETS_FREE = "FREE";

}
