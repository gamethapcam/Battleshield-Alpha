package com.yaamani.battleshield.alpha.Game.Screens.Gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.yaamani.battleshield.alpha.MyEngine.MyText.MyBitmapFont;
import com.yaamani.battleshield.alpha.MyEngine.MyText.SimpleText;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class TwoExitPortalUI extends Group implements Resizable {

    private Image twoExitPortalBulletImage;
    private Image glowingTwoExitPortalBulletImage;
    private SimpleText text;

    public TwoExitPortalUI(MyBitmapFont myBitmapFont, TextureRegion twoExitPortalBulletRegion, TextureRegion glowingTwoExitPortalBulletRegion) {
        twoExitPortalBulletImage = new Image(twoExitPortalBulletRegion);
        addActor(twoExitPortalBulletImage);
        twoExitPortalBulletImage.setSize(SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH);

        glowingTwoExitPortalBulletImage = new Image(glowingTwoExitPortalBulletRegion);
        addActor(glowingTwoExitPortalBulletImage);
        float widthRatio = (float) glowingTwoExitPortalBulletRegion.getRegionWidth() / twoExitPortalBulletRegion.getRegionWidth();
        float heightRatio = (float) glowingTwoExitPortalBulletRegion.getRegionHeight() / twoExitPortalBulletRegion.getRegionHeight();
        glowingTwoExitPortalBulletImage.setSize(SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH*widthRatio, SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_WIDTH*heightRatio);
        glowingTwoExitPortalBulletImage.setColor(1, 1, 1, 0);


        text = new SimpleText(myBitmapFont, "0/" + D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES);
        addActor(text);
        text.setHeight(BULLET_SPEED_MULTIPLIER_TXT_HEIGHT);

        calculatePositions();
    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        calculatePositions();
    }

    private void calculatePositions() {
        setWidth(glowingTwoExitPortalBulletImage.getWidth());
        text.setPosition(getWidth()/2f - text.getWidth()/2f, 0);
        glowingTwoExitPortalBulletImage.setX(0);
        twoExitPortalBulletImage.setX(getWidth()/2f - twoExitPortalBulletImage.getWidth()/2f);

        twoExitPortalBulletImage.setY(text.getY() + text.getHeight() + SPECIAL_BULLET_TEMP_PROGRESS_BAR_UI_MARGIN_BETWEEN_IMAGE_AND_PROGRESS_BAR);
        glowingTwoExitPortalBulletImage.setY(twoExitPortalBulletImage.getY() + twoExitPortalBulletImage.getHeight()/2f - glowingTwoExitPortalBulletImage.getHeight()/2f);
    }

    public void toggleGlow() {
        if (glowingTwoExitPortalBulletImage.getColor().a > 0.5f)
            glowingTwoExitPortalBulletImage.addAction(Actions.alpha(0, 0.5f));
        else
            glowingTwoExitPortalBulletImage.addAction(Actions.alpha(1, 0.5f));
    }

    public void resetText() {
        text.setCharSequence("0/" + D_PORTALS_TWO_PORTAL_EXIT_NUM_OF_OCCURRENCES, true);
    }
}
