package com.yaamani.battleshield.alpha.MyEngine.MyText;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * <p>
 * A One line text that extends {@link Actor} which means it has all the features of an Actor [x, y, width, height .....].
 * </p>
 * <p>
 * If {@link #aspectRatioLocked} {@code == true}, the text can't be stretched and you can change one dimension at a time[width or height]
 * and the other will be changed automatically.
 * </p>
 * <p>
 * padding and kerning may not be supported yet.
 * </p>
 */

public class SimpleText extends Actor {

    public static final String TAG = SimpleText.class.getSimpleName();

    private MyBitmapFont myBitmapFont;
    private String charSequence;
    private boolean aspectRatioLocked;

    private TextureRegion _temp;

    private Array<BitmapFont.Glyph> glyphs;

    private int charSequenceWidthPixelUnits;
    private int heightPixelUnits;
    private int yoffsetPlusHeightMax;

    public SimpleText(MyBitmapFont myBitmapFont, String charSequence) {
        this.myBitmapFont = myBitmapFont;
        glyphs = new Array<>(true, 50);
        setCharSequence(charSequence, true);
        this.aspectRatioLocked = true;

        _temp = new TextureRegion();

        //setDebug(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        float cursorX = 0;
        float w, xoffset = 0, myOffsetPixelUnits, myYOffsetWorldUnits, xadvance, kerning;
        int i = 0;
        for (BitmapFont.Glyph glyph : glyphs) {

            _temp.setRegion(myBitmapFont.pages[glyph.page], glyph.srcX, glyph.srcY, glyph.width, glyph.height);

            w = (float) glyph.width / charSequenceWidthPixelUnits * getWidth();
            if (i > 0) xoffset = (float) glyph.xoffset / charSequenceWidthPixelUnits * getWidth();
            myOffsetPixelUnits = yoffsetPlusHeightMax - ((glyph.yoffset) + glyph.height);
            myYOffsetWorldUnits = myOffsetPixelUnits / heightPixelUnits * getHeight();
            xadvance = (float) glyph.xadvance / charSequenceWidthPixelUnits * getWidth();

            if (i > 0) {
                kerning = (float) glyphs.get(i-1).getKerning((char) glyph.id) / charSequenceWidthPixelUnits * getWidth();
                cursorX += kerning;
                //Gdx.app.log(TAG, "" + glyphs.get(i-1).id + ", " + glyph.id + ", " + glyphs.get(i-1).getKerning((char) glyph.id) + ", " + kerning);
            }

            if (glyph.id != ' ') {
                if (aspectRatioLocked)
                    batch.draw(_temp, getX() + cursorX + xoffset, getY() + myYOffsetWorldUnits, w, w * glyph.height / glyph.width);
                else
                    batch.draw(_temp, getX() + cursorX + xoffset, getY() + myYOffsetWorldUnits, w, getHeight() * glyph.height / heightPixelUnits);
            }

            cursorX += xadvance;
            i++;
        }
    }

    public String getCharSequence() {
        return charSequence;
    }

    /**
     *
     * @param charSequence
     * @param updateWidth When false, the new {@code charSequence} will fit the current width. when true, the width will be changed according to the new {@code charSequence}.
     */
    public void setCharSequence(String charSequence, boolean updateWidth) {
        if (charSequence.contains("\n"))
            //Gdx.app.log(TAG, "All the characters will be written in one line. (No multiline)");
            throw new UnsupportedOperationException("Multiline isn't supported.");

        //Gdx.app.log(TAG, charSequence);

        float prevHeight = getHeight();

        this.charSequence = charSequence/*.replaceAll("\", ")*/;
        glyphs.clear();

        if (charSequence.isEmpty()) {
            return;
        }

        float prevCharSequenceWidthPixels = charSequenceWidthPixelUnits;
        charSequenceWidthPixelUnits = 0;

        BitmapFont.Glyph firstCharInSeq = myBitmapFont.data.getGlyph(charSequence.charAt(0));

        yoffsetPlusHeightMax = (firstCharInSeq.yoffset) + firstCharInSeq.height;
        int yoffsetMin = (firstCharInSeq.yoffset);

        char c;
        for (int i = 0, length = charSequence.length(); i < length; i++) {
            c = charSequence.charAt(i);

            BitmapFont.Glyph glyph = myBitmapFont.getData().getGlyph(c);
            if (glyph == null) continue;

            glyphs.add(glyph);



            // charSequenceWidthPixelUnits.
            if (i == 0 & length == 1)
                charSequenceWidthPixelUnits -= glyph.xoffset;
            else if (i < length - 1)
                charSequenceWidthPixelUnits += glyph.xadvance;
            if (i == length-1)
                charSequenceWidthPixelUnits += glyph.xoffset + glyph.width;

            if (i > 0) {
                float kerning = (float) glyphs.get(i-1).getKerning((char) glyph.id);
                charSequenceWidthPixelUnits += kerning;
                //Gdx.app.log(TAG, "" + glyphs.get(i-1).id + ", " + glyph.id + ", " + glyphs.get(i-1).getKerning((char) glyph.id) + ", " + kerning);
            }


            //Gdx.app.log(TAG, c + ", id = " + glyph.id + ", yoffset = " + (glyph.yoffset));


            // heightPixelUnits parameters.
            int yoffsetPlusHeight = (glyph.yoffset) + glyph.height;
            if (yoffsetPlusHeight > yoffsetPlusHeightMax) {
                yoffsetPlusHeightMax = yoffsetPlusHeight;
            }
            if ((glyph.yoffset) < yoffsetMin)
                yoffsetMin = (glyph.yoffset);
        }

        // heightPixelUnits.
        int base = myBitmapFont.data.base;
        if (yoffsetPlusHeightMax < base) yoffsetPlusHeightMax = base;
        if (yoffsetMin > base) yoffsetMin = base;
        heightPixelUnits = yoffsetPlusHeightMax - yoffsetMin;

        if (updateWidth) {
            if (prevCharSequenceWidthPixels != 0)
                setWidth((float) charSequenceWidthPixelUnits / prevCharSequenceWidthPixels * getWidth());
            else if (prevHeight != 0)
                setWidth(getWidthAccordingToTheRatio(prevHeight));
        }
    }

    @Override
    public void setHeight(float height) {
        if (aspectRatioLocked)
            setWidth(getWidthAccordingToTheRatio(height));
        super.setHeight(height);
    }

    @Override
    public float getHeight() {
        if (charSequence == null) return 0;
        if (charSequence.isEmpty() | !aspectRatioLocked)
            return super.getHeight();
        return getHeightAccordingToTheRatio(getWidth());
    }

    private float getHeightAccordingToTheRatio(float width) {
        if (charSequenceWidthPixelUnits == 0) return 0;
        return (float) heightPixelUnits / charSequenceWidthPixelUnits * width;
    }

    private float getWidthAccordingToTheRatio(float height) {
        if (heightPixelUnits == 0) return 0;
        return (float) charSequenceWidthPixelUnits / heightPixelUnits * height;
    }

    public boolean isAspectRatioLocked() {
        return aspectRatioLocked;
    }

    public void lockAspectRatio() {
        if (!this.aspectRatioLocked)
            setWidth(getWidthAccordingToTheRatio(getHeight()));

        this.aspectRatioLocked = true;
    }

    public void unlockAspectRatio(float width, float height) {
        this.aspectRatioLocked = false;
        setSize(width, height);
    }

    @Deprecated
    @Override
    public void setRotation(float degrees) {
        throw new UnsupportedOperationException("Rotation isn't supported yet.");
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param width
     * @param height
     */
    @Override
    public void setSize(float width, float height) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time when aspectRatioLocked is true. " +
                            "You can change one at a time and the other will be change automatically."
            );

        super.setSize(width, height);
        /*_getHeight = height;
        _getWidth = width;*/
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param size
     */
    @Override
    public void sizeBy(float size) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time. You can change one at a time and the other will be change automatically."
            );

        super.sizeBy(size);
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param width
     * @param height
     */
    @Override
    public void sizeBy(float width, float height) {
        if (aspectRatioLocked)

            throw new RuntimeException(
                    "You can't change width and height at the same time. You can change one at a time and the other will be change automatically."
            );

        super.sizeBy(width, height);
    }

    /**
     * @exception RuntimeException is thrown when trying to use this function while {@code aspectRatioLocked == true}
     * @param x
     * @param y
     * @param width
     * @param height
     */
    @Override
    public void setBounds(float x, float y, float width, float height) {
        if (aspectRatioLocked)
            throw new RuntimeException(
                    "You can't change width and height at the same time when aspectRatioLocked is true." +
                            "You can change one at a time and the other will be change automatically. " +
                            "Use setBoundsWidth(x, y, width) or setBoundsHeight(x, y, height) instead."
            );

        super.setBounds(x, y, width, height);
    }

    public void setBoundsWidth(float x, float y, float width) {
        if (aspectRatioLocked) {
            float height = getHeightAccordingToTheRatio(width);
            super.setBounds(x, y, width, height);
        } else
            super.setBounds(x, y, width, getHeight());
    }

    public void setBoundsHeight(float x, float y, float height) {
        if (aspectRatioLocked) {
            float width = getWidthAccordingToTheRatio(height);
            super.setBounds(x, y, width, height);
        } else
            super.setBounds(x, y, getWidth(), height);
    }
}
