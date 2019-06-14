package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MyProgressBar extends Actor {

    private NinePatch bg;
    private NinePatch percentageBar;

    private TextureRegion region;
    private float percentageBarHeightRatio;
    private float percentageBarX, percentageBarY;

    private float percentage;

    /**
     *
     * @param region a {@link TextureRegion} for a circle or rounded rectangle or rectangle. The class is designed to work best with these shapes.
     */
    public MyProgressBar(TextureRegion region, float percentage, float percentageBarHeightRatio) {
        this.region = region;
        this.percentage = percentage;
        this.percentageBarHeightRatio = percentageBarHeightRatio;

        int  regionWidth2 = region.getRegionWidth()/2, regionHeight2 = region.getRegionHeight()/2;
        bg = new NinePatch(region, regionWidth2-1, regionWidth2, regionHeight2-1, regionHeight2);
        percentageBar = new NinePatch(region, regionWidth2-1, regionWidth2, regionHeight2-1, regionHeight2);
    }

    public MyProgressBar(TextureRegion region, float percentage) {
        this(region, percentage, 1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getBgColor());
        bg.draw(batch, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        batch.setColor(getPercentageBarColor());
        float h = getHeight()*percentageBarHeightRatio;
        percentageBar.draw(batch,
                getX() + percentageBarX,
                getY() + percentageBarY,
                getOriginX(),
                getOriginY(),
                h + percentage * (getWidth() - (getHeight() - h) - h),
                h,
                getScaleX(),
                getScaleY(),
                getRotation());
    }

    public void setBgColor(Color color) {
        bg.setColor(color);
    }

    public void setPercentageBarColor(Color color) {
        percentageBar.setColor(color);
    }

    public Color getBgColor() {
        return bg.getColor();
    }

    public Color getPercentageBarColor() {
        return percentageBar.getColor();
    }

    public float getPercentageBarHeightRatio() {
        return percentageBarHeightRatio;
    }

    public void setPercentageBarHeightRatio(float percentageBarHeightRatio) {
        if (percentageBarHeightRatio > 1)
            throw new ValueOutOfRangeException("Maximum value is 1.");
        if (percentageBarHeightRatio < 0)
            throw new ValueOutOfRangeException("Minimum value is 0.");

        this.percentageBarHeightRatio = percentageBarHeightRatio;

        updatePercentageBar9PatchDimension(getHeight());
    }

    public TextureRegion getRegion() {
        return region;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        if (percentage > 1)
            throw new ValueOutOfRangeException("Maximum value is 1.");
        if (percentage < 0)
            throw new ValueOutOfRangeException("Minimum value is 0.");

        this.percentage = percentage;
    }



    @Override
    public void setHeight(float height) {
        updateBg9PatchDimension(height);
        updatePercentageBar9PatchDimension(height);

        super.setHeight(height);
    }

    private void updateBg9PatchDimension(float height) {
        float height2 = height/2f;

        bg.setTopHeight(height2);
        bg.setBottomHeight(height2);
        bg.setRightWidth(height2);
        bg.setLeftWidth(height2);
    }

    private void updatePercentageBar9PatchDimension(float height) {
        float height2Ratio = height/2f * percentageBarHeightRatio;
        percentageBar.setTopHeight(height2Ratio);
        percentageBar.setBottomHeight(height2Ratio);
        percentageBar.setRightWidth(height2Ratio);
        percentageBar.setLeftWidth(height2Ratio);

        float moveBy = (height - height*percentageBarHeightRatio)/2f;
        percentageBarX = moveBy;
        percentageBarY = moveBy;
    }

    @Override
    public void setSize(float width, float height) {
        updateBg9PatchDimension(height);
        updatePercentageBar9PatchDimension(height);

        super.setSize(width, height);
    }

    @Override
    public void sizeBy(float size) {
        updateBg9PatchDimension(getHeight()+size);
        updatePercentageBar9PatchDimension(getHeight()+size);
        super.sizeBy(size);
    }

    @Override
    public void sizeBy(float width, float height) {
        updateBg9PatchDimension(getHeight()+height);
        updatePercentageBar9PatchDimension(getHeight()+height);
        super.sizeBy(width, height);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        updateBg9PatchDimension(height);
        updatePercentageBar9PatchDimension(height);
        super.setBounds(x, y, width, height);
    }
}
