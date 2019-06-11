package com.yaamani.battleshield.alpha.Game.Starfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsHandler;
import com.yaamani.battleshield.alpha.MyEngine.AdvancedStage;
import com.yaamani.battleshield.alpha.MyEngine.MyInterpolation;
import com.yaamani.battleshield.alpha.MyEngine.Tween;
import com.yaamani.battleshield.alpha.Game.Utilities.Assets;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;
import static com.yaamani.battleshield.alpha.MyEngine.MyInterpolation.*;

public class StarsContainer extends Group implements Disposable{

    public final String TAG = this.getClass().getSimpleName();

    private TextureRegion starRegion;

    private Array<Star> stars;

    private FrameBuffer frameBuffer;

    //private final Vector2 distanceOffset = new Vector2(Wo)
    private Vector2 transitionVelocity = new Vector2(0, 0);
    private Vector2 transitionVelocityMax = new Vector2(0, 0);

    private RadialTween radialTween;
    private float thetaForRadialTween; // Rad

    private BulletsHandler bulletsHandler;

    public StarsContainer(AdvancedStage game) {
        setTransform(false);

        Viewport viewport = game.getViewport();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        //game.addUpdatable(this);

        starRegion = Assets.instance.mutualAssets.star/*Assets.instance.gameplayAssets.gameOverBG*/;
        stars = new Array<Star>(false, STARS_COUNT, Star.class);
        for (int i = 0; i < STARS_COUNT; i++) {
            Star star = new Star(starRegion, viewport, i);
            stars.add(star);
        }

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), false);

        initializeRadialTween();

        /*frameBuffer.begin();
        getBatch().begin();
        for (Star star : stars) {
            star.draw(getBatch(), 1.0f);
        }

        getBatch().end();
        frameBuffer.end();
        frameBuffer.dispose();*/

    }

    @Override
    public void act(float delta) {
        radialTween.update(delta);
        for (Star star : stars) {
            star.act(delta, transitionVelocity, radialTween, thetaForRadialTween, bulletsHandler.getBulletsPerAttack());
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //super.draw(batch, parentAlpha);
        //frameBuffer.begin();

        for (Star star : stars) {
            star.draw(batch);
        }

        //frameBuffer.end();

        /*batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        //batch.setShader(null);

        batch.end();*/
    }


    public void resize(float wWidth, float wHeight) {
        for (Star star : stars) {
            star.resize(wWidth, wHeight);
        }
    }

    public void setTransitionVelocity(Vector2 transitionVelocity) {
        this.transitionVelocity = transitionVelocity;
    }

    public Vector2 getTransitionVelocity() {
        return transitionVelocity;
    }

    public Star[] getStars() {
        return stars.items;
    }

    public RadialTween getRadialTween() {
        return radialTween;
    }

    public void setBulletsHandler(BulletsHandler bulletsHandler) {
        this.bulletsHandler = bulletsHandler;
    }

    public float getThetaForRadialTween() {
        return thetaForRadialTween;
    }

    public void setThetaForRadialTween(float thetaForRadialTween) {
        this.thetaForRadialTween = thetaForRadialTween;
    }

    @Override
    public void dispose() {
        frameBuffer.dispose();
    }

    private void initializeRadialTween() {
        radialTween = new RadialTween(STARS_RADIAL_TWEEN_DURATION) {
            @Override
            public void tween(float percentage) {
                Interpolation interpolationIn = MyInterpolation.fastExp10In;
                Interpolation interpolationOut = MyInterpolation.fastExp10Out;
                if (percentage < 0.5) {
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolationIn.apply(0, STARS_RADIAL_TWEEN_THETA_MAX, percentage * 2);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolationIn.apply(0, -STARS_RADIAL_TWEEN_THETA_MAX, percentage * 2);
                } else {
                    if (getSpecialBullet() == SpecialBullet.MINUS)
                        thetaForRadialTween = interpolationOut.apply(STARS_RADIAL_TWEEN_THETA_MAX, 0, (percentage - 0.5f) * 2);
                    else if (getSpecialBullet() == SpecialBullet.PLUS)
                        thetaForRadialTween = interpolationOut.apply(-STARS_RADIAL_TWEEN_THETA_MAX, 0, (percentage - 0.5f) * 2);
                }
            }
        };
    }





















    public abstract class RadialTween extends Tween {

        private SpecialBullet specialBullet;

        public RadialTween(float durationMillis) {
            super(durationMillis);
        }

        public void start(SpecialBullet specialBullet) {
            this.specialBullet = specialBullet;
            super.start();
        }

        public void start(float delay, SpecialBullet specialBullet) {
            this.specialBullet = specialBullet;
            super.start(delay);
        }

        public SpecialBullet getSpecialBullet() {
            return specialBullet;
        }
    }
}
