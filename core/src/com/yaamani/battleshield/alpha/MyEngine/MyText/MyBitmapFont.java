package com.yaamani.battleshield.alpha.MyEngine.MyText;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MyBitmapFont extends Actor {

    TextureRegion[] pages;
    MyBitmapFontData data;

    public MyBitmapFont(FileHandle fntFile, TextureRegion... pages) {
        this.pages = pages;
        data = new MyBitmapFontData(fntFile);
    }

    public TextureRegion getPage(int index) {
        if (index < 0)
            throw new ArrayIndexOutOfBoundsException("index must be a non-negative value.");
        else if (index >= pages.length)
            throw new ArrayIndexOutOfBoundsException("The index you entered is greater than the highest index in the pages array (" + pages.length + ").");

        return pages[index];
    }

    public BitmapFont.BitmapFontData getData() {
        return data;
    }
}