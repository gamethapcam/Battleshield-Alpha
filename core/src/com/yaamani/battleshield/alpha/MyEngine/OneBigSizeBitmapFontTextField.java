package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

@Deprecated
public class OneBigSizeBitmapFontTextField {
    // Feel free to add getter and setters if needed.

    private BitmapFont font;
    private GlyphLayout glyphLayout;

    private CharSequence charSequence;
    private Color color;
    private float targetWidth;
    private int halign;
    private boolean wrap;
    private String truncate;

    private float scale;
    private float theResolutionAtWhichTheScaleWasDecided;
    private float behindTheScenesScale;
    // Any change in the resolution or the world size will change how the text will be displayed on the screen (size-wise).
    // So, theResolutionAtWhichTheScaleWasDecided attribute ensures that the text remains consistent (size-wise) regardless of the world size or the resolution.
    // If the scale == 1, this means that the size of the characters displayed on the screen will be exactly equal their size on the bitmap when the window's resolution (height in this game) == theResolutionAtWhichTheScaleWasDecided.

    public OneBigSizeBitmapFontTextField(BitmapFont font,
                                         CharSequence charSequence,
                                         Color color,
                                         float targetWidth,
                                         int halign,
                                         boolean wrap,
                                         String truncate,
                                         float scale,
                                         float theResolutionAtWhichTheScaleWasDecided) {

        this.font = font;

        this.charSequence = charSequence;
        this.color = color;
        this.targetWidth = targetWidth;
        this.halign = halign;
        this.wrap = wrap;
        this.truncate = truncate;

        setScale(scale, theResolutionAtWhichTheScaleWasDecided);
    }

    private void newGlyph() { // Any change in any of the attributes above except font and glyphLayout requires calling this method in order to properly be updated.
        glyphLayout = new GlyphLayout(font, charSequence, 0, charSequence.length(), color, targetWidth, halign, wrap, truncate);

        //glyphLayout
    }

    public void setCharSequence(CharSequence charSequence) {
        this.charSequence = charSequence;

        newGlyph();
    }

    public void setScale(float scale, float theResolutionAtWhichTheScaleWasDecided) {
        this.scale = scale;
        this.theResolutionAtWhichTheScaleWasDecided = theResolutionAtWhichTheScaleWasDecided;

        behindTheScenesScale = WORLD_SIZE/theResolutionAtWhichTheScaleWasDecided * scale;
        updateFontScale();
    }

    private void updateFontScale() {
        font.getData().setScale(behindTheScenesScale);

        newGlyph();
    }

    public void setColor(Color color) {
        this.color = color;

        newGlyph();
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(new Color(r, g, b, a));
    }

    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }

    public CharSequence getCharSequence() {
        return charSequence;
    }

    public Color getColor() {
        return color;
    }

    public float getTargetWidth() {
        return targetWidth;
    }

    public float getScale() {
        return scale;
    }

    public float getTheResolutionAtWhichTheScaleWasDecided() {
        return theResolutionAtWhichTheScaleWasDecided;
    }

    public void draw(Batch batch, float x, float y) {
        updateFontScale();
        font.draw(batch, glyphLayout, x, y);
    }
}
