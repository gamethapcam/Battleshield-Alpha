package com.yaamani.battleshield.alpha.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

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

        TexturePacker.process(settings,
                "/Users/mac/OneDrive/Battleshield(Alpha)/Unpacked Assets/1080p",
                "/Users/mac/OneDrive/Battleshield(Alpha)/android/assets",
                "All");
    }

}
