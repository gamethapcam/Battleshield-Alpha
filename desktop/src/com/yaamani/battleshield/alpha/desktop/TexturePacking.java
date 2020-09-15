package com.yaamani.battleshield.alpha.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.saveShieldsWithVariousAngles;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.SHIELDS_FREE_ANGLE;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.SHIELDS_SAVING_FROM_ANGLE;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.SHIELDS_SAVING_TO_ANGLE;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.SHIELDS_SKIP_ANGLE_WHEN_SAVING;

class TexturePacking {

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 4096;
        settings.maxHeight = 4096;
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
        settings.flattenPaths = true;
        settings.combineSubdirectories = true;
        //settings.fast = true;

        //saveProgrammaticallyGeneratedTextures(1080);

        TexturePacker.process(settings,
                "Unpacked Assets/1080p",
                "android/assets",
                "All");
    }


    /*private static void saveProgrammaticallyGeneratedTextures(int targetResolution) {
        saveShieldsWithVariousAngles(SHIELDS_SAVING_FROM_ANGLE,
                SHIELDS_SAVING_TO_ANGLE,
                SHIELDS_SKIP_ANGLE_WHEN_SAVING,
                SHIELDS_FREE_ANGLE,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/ShieldsWithVariousAngles",
                targetResolution);

        //saveTurret(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay");

        //saveBullet(WORLD_SIZE, targetResolution, "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/");

        *//*saveHealthBarWithVariousAngles(HEALTH_BAR_SAVING_FROM_ANGLE,
                HEALTH_BAR_SAVING_TO_ANGLE,
                HEALTH_BAR_SKIP_ANGLE_WHEN_SAVING,
                targetResolution,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Non-Finalized Assets/1080p/Gameplay/HealthBarWithVariousAngles");*//*
    }*/
}
