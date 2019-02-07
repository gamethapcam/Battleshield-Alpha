package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedScreen;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.Junk.AdvancedDrawing;
import com.yaamani.battleshield.alpha.MyEngine.Resizable;

import static com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame.DrawingStuff.*;
import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class ExperimentsScreen extends AdvancedScreen {

    private Turret turret;

    //private MaskTexture maskTexture;
    private final int shieldsCount = 4;
    private final float shiftAngle = 0;
    private Array<GunBulletsShield> gunBulletsShieldArray;



    public ExperimentsScreen(AdvancedStage game, boolean transform) {
        super(game, transform);
        /*maskTexture = new MaskTexture();
        addActor(maskTexture);*/

        gunBulletsShieldArray = new Array<GunBulletsShield>();
        for (int i = 0; i < shieldsCount; i++) {
            GunBulletsShield gunBulletsShield;
            gunBulletsShield = new GunBulletsShield(/*getStage().getViewport()*/);
            gunBulletsShieldArray.add(gunBulletsShield);
            addActor(gunBulletsShield);
            gunBulletsShield.rotateBy(i*(360f/shieldsCount)+shiftAngle);
        }

        turret = new Turret();
        //turret.turretImage.setVisible(false);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height, float worldWidth, float worldHeight) {
        for (int i = 0; i < gunBulletsShieldArray.size; i++) gunBulletsShieldArray.get(i).resize(width, height, worldWidth, worldHeight);
        turret.resize(width, height, worldWidth, worldHeight);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //maskTexture.dispose();
        for (int i = 0; i < gunBulletsShieldArray.size; i++) gunBulletsShieldArray.get(i).dispose();
        turret.dispose();
    }










    private class Turret implements Disposable, Resizable {

        private Texture turretTexture;
        private Image turretImage;

        public Turret() {
            turretTexture = pixToTex(drawTurret(WORLD_SIZE, 1080));
            turretImage = new Image(turretTexture);
            addActor(turretImage);
            //turretImage.setColor(1, 1, 1, 0.5f);
            turretImage.setSize(resolutionIntoWorldUnits(turretTexture.getWidth(), WORLD_SIZE, 1080),
                    resolutionIntoWorldUnits(turretTexture.getHeight(), WORLD_SIZE, 1080));

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getStage().getViewport().getWorldWidth(), getStage().getViewport().getWorldHeight());
        }

        @Override
        public void resize(int width, int height, float worldWidth, float worldHeight) {
            turretImage.setPosition(worldWidth/2f - TURRET_RADIUS,
                    worldHeight/2f - TURRET_RADIUS);
        }

        @Override
        public void dispose() {
            turretTexture.dispose();
        }
    }











    private class GunBulletsShield extends Group implements Disposable, Resizable {

        //private Viewport viewport;

        private Texture bulletTexture;
        private Array<Image> bullets;

        private Texture shieldTexture;
        private Image shield;

        private GunBulletsShield(/*Viewport viewport*/) {
            //this.viewport = viewport;

            bulletTexture = pixToTex(drawBullet(WORLD_SIZE, MAX_RESOLUTION_SUPPORTED));

            bullets = new Array<Image>();
            for (int i = 0; i < 15; i++) {
                Image bullet = initializeBullet();
                bullets.add(bullet);
                bullet.setY(bullet.getY() + i*(bullet.getWidth() + BULLETS_DISTANCE_BETWEEN_TWO));
            }
            //initializeShield();
        }


        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
        }

        @Override
        public void dispose() {
            bulletTexture.dispose();
            shieldTexture.dispose();
        }


        @Override
        public void resize(int width, int height, float worldWidth, float worldHeight) {
            setPosition(worldWidth/2f,worldHeight/2f);
        }


        // -------------------- INITIALIZERS --------------------

        private Image initializeBullet() {
            Image bullet;
            bullet = new Image(bulletTexture);
            addActor(bullet);
            bullet.setSize(resolutionIntoWorldUnits(bulletTexture.getWidth(), WORLD_SIZE, MAX_RESOLUTION_SUPPORTED),
                            resolutionIntoWorldUnits(bulletTexture.getHeight(), WORLD_SIZE, MAX_RESOLUTION_SUPPORTED));
            bullet.setPosition(-bullet.getWidth()/2, 12);
            bullet.setOrigin(bullet.getWidth()/2, bullet.getHeight()/2);
            bullet.rotateBy(90);
            return bullet;
        }

        /*private void initializeShield() {
            shieldTexture = pixToTex(
                                        drawRoundedArc(SHIELDS_RADIUS,
                                                SHIELDS_THICKNESS,
                                                MathUtils.degRad*(360f/shieldsCount-10),
                                                Color.WHITE,
                                                WORLD_SIZE,
                                                1080)
            );
            shield = new Image(shieldTexture);
            addActor(shield);
            shield.setSize(resolutionIntoWorldUnits(shieldTexture.getWidth(), WORLD_SIZE, 1080),
                            resolutionIntoWorldUnits(shieldTexture.getHeight(), WORLD_SIZE, 1080));
            //shield.setSize(shieldTexture.getWidth()/3, shieldTexture.getHeight()/3);
            shield.setPosition(-shield.getWidth()/2, SHIELDS_RADIUS - shield.getHeight() + SHIELDS_THICKNESS);
            shield.setOrigin(shield.getWidth()/2, shield.getHeight()/2);
            shield.rotateBy(180);

            Gdx.app.log(GunBulletsShield.class.getSimpleName(), "shieldTexture size = " + shieldTexture.getWidth() + ", " + shieldTexture.getHeight());
        }*/
    }














    private class Controls extends Group implements Disposable, Resizable {



        @Override
        public void dispose() {

        }

        @Override
        public void resize(int width, int height, float worldWidth, float worldHeight) {

        }
    }
















    private class MaskTexture extends Actor implements Disposable{
        //private Pixmap pixmap;
        private Texture texture;
        private TextureRegion region;
        private Texture mask;

        private AdvancedDrawing advancedDrawing;

        private MaskTexture() {
            /*pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.drawPixel(0, 0, Color.WHITE.toIntBits());*/

            advancedDrawing = new AdvancedDrawing();

            texture = new Texture(/*pixmap*/Gdx.files.internal("badlogic.jpg"));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            region = new TextureRegion(texture);
            setBounds(region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionWidth());

            mask = new Texture(Gdx.files.internal("badlogic Mask.jpg"));

            setWidth(20);
            setHeight(20);
            }

        @Override
        public void draw(Batch batch, float parentAlpha) {

            advancedDrawing.drawMasked(batch, region, getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), mask);

            //batch.draw(turretTexture, 0, 0, TURRET_RADIUS, TURRET_RADIUS);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            setX(getX() + 1*delta);
        }

        @Override
        public void dispose() {
            texture.dispose();
            mask.dispose();
            advancedDrawing.dispose();
        }
    }
}
