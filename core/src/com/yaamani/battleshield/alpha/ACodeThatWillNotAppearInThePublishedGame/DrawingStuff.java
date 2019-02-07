package com.yaamani.battleshield.alpha.ACodeThatWillNotAppearInThePublishedGame;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

import java.io.IOException;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class DrawingStuff { // POJO ;)

    public static Pixmap drawTurret(float WorldSize, int targetResolution) {
        int radius = worldUnitsIntoResolution(TURRET_RADIUS, WorldSize, targetResolution);
        Pixmap pix = new Pixmap(radius*2, radius*2, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        pix.fillCircle(pix.getWidth()/2, pix.getHeight()/2, radius);

        return pix;
    }

    public static Pixmap drawBullet(float WorldSize, int targetResolution) {
        int bulletHeight = worldUnitsIntoResolution(BULLETS_ORDINARY_HEIGHT, WorldSize, targetResolution);
        int bulletWidth = (int) (bulletHeight * BULLETS_ORDINARY_WIDTH_RATIO);
        Pixmap pix = new Pixmap((int) (bulletHeight * BULLETS_ORDINARY_WIDTH_RATIO), bulletHeight, Pixmap.Format.RGBA8888);
        pix.setColor(Color.WHITE);
        // Drawing a perfectly round rectangle.
        int circleRadius = bulletHeight/2;
        pix.fillCircle(circleRadius, circleRadius, circleRadius);
        pix.fillRectangle(circleRadius, 0, bulletWidth - 2*circleRadius, bulletHeight);
        pix.fillCircle(bulletWidth - circleRadius, circleRadius, circleRadius);

        return pix;
    }


    /**
     * @param R refers to the radius.
     * @param T refers to thickness.
     * @param omega is the arc angle in radians.
     * @return A texture of the roundedArc.
     */
    public static Pixmap drawRoundedArc(float R, float T, float omega, Color color,float worldSize, int targetResolution) {
        // The Math is of course vague. So I highly recommend checking out (Shields Dimensions.png) in the Non-Finalized Assets folder.
        float L0 = T/2;
        float phi = L0/(R+L0);
        float theta = omega-2*phi;
        float L1 = 2*(R+L0) * MathUtils.sin(theta/2);
        float L2 = (R+L0)*MathUtils.cos(theta/2);
        float L3 = (R+T)-L2;

        int pixWidth = worldUnitsIntoResolution(L1+2*L0, worldSize, targetResolution);
        int pixHeight = worldUnitsIntoResolution(L0+L3, worldSize, targetResolution);

        int RInResolution = worldUnitsIntoResolution(R, worldSize, targetResolution);
        int TInResolution = worldUnitsIntoResolution(T, worldSize, targetResolution);

        Pixmap pix = drawSharpArcLogic(RInResolution,
                TInResolution,
                theta,
                pixWidth,
                pixHeight,
                pixWidth/2,
                worldUnitsIntoResolution(-L2+L0, worldSize, targetResolution),
                color);

        pix.fillCircle(TInResolution/2, TInResolution/2, TInResolution/2);
        pix.fillCircle(pixWidth - TInResolution/2, TInResolution/2, TInResolution/2);

        return pix;
    }

    /**
     * @param R refers to the radius.
     * @param T refers to thickness.
     * @param theta is the arc angle in radians.
     * @return A texture of the sharpArc.
     */
    public static Pixmap drawSharpArc(float R, float T, float theta, Color color,float worldSize, int targetResolution) {
        // The Math is of course vague. So I highly recommend checking out (Shields Dimensions.png) in the Non-Finalized Assets folder.
        float sinHalfTheta = MathUtils.sin(theta/2);
        float L4 = 2*R*sinHalfTheta;
        float L5 = T*sinHalfTheta;
        float L6;

        int pixWidth;
        int pixHeight;
        if (theta < MathUtils.PI) {
            L6 = R * MathUtils.cos(theta/2);
            pixWidth = worldUnitsIntoResolution(L4 + 2 * L5, worldSize, targetResolution);

        } else {
            L6 = (R+T) * MathUtils.cos((theta)/2);
            pixWidth = worldUnitsIntoResolution(2 * (R + T), worldSize, targetResolution);
        }

        pixHeight = worldUnitsIntoResolution(R + T - L6, worldSize, targetResolution);

        Pixmap pix = drawSharpArcLogic(worldUnitsIntoResolution(R, worldSize, targetResolution),
                worldUnitsIntoResolution(T, worldSize, targetResolution),
                theta,
                pixWidth,
                pixHeight,
                (int) (pixWidth/2f),
                worldUnitsIntoResolution( -L6, worldSize, targetResolution),
                color);

        return pix;
    }

    private static Pixmap drawSharpArcLogic(float R, float T, float theta, int pixWidth, int pixHeight, int centerPointX, int centerPointY, Color color) {
        // The Math is of course vague. So I highly recommend checking out (Shields Dimensions.png) in the Non-Finalized Assets folder.
        Pixmap pix = new Pixmap(pixWidth, pixHeight, Pixmap.Format.RGBA8888);
        /*pix.setColor(Color.DARK_GRAY);
        pix.fill();*/
        pix.setColor(color);

        for (int x = 0; x < pixWidth; x++) {
            for (int y = 0; y < pixHeight; y++) {
                // Translation af axis, in order to make the center point equal to (centerPointX, centerPointY).
                int X = x - centerPointX;
                int Y = y - centerPointY;

                float r = (float) Math.sqrt(X * X + Y * Y);
                float a = MathUtils.atan2(Math.abs(X), Y);

                if (a <= theta / 2 & r > R & r < R + T) {
                    pix.drawPixel(x, y);
                }
            }
        }

        return pix;
    }



    public static Texture pixToTex(Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        //pixmap.dispose();
        return texture;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------

    /*public static void saveShieldsWithVariousAngles(float fromAngleDeg,
                                                    float toAngleDeg,
                                                    float skipAngleDeg,
                                                    float freeAngleBetweenShieldsDeg,
                                                    String externalFolderPath,
                                                    int targetResolution) {

        if (fromAngleDeg > toAngleDeg) throw new ValueOutOfRangeException("fromAngleDeg can't be greater than toAngleDeg");
        else if (skipAngleDeg <= 0) throw new ValueOutOfRangeException("skipAngleDeg must be greater than zero");

        for (float i = fromAngleDeg; i <= toAngleDeg; i += skipAngleDeg) {
            Pixmap pix = drawRoundedArc(SHIELDS_RADIUS,
                    SHIELDS_THICKNESS,
                    MathUtils.degRad * (i - freeAngleBetweenShieldsDeg),
                    SHIELDS_COLOR,
                    WORLD_SIZE,
                    targetResolution);


            FileHandle fileHandle = new FileHandle(externalFolderPath + "/" + SHIELDS_NAMING_WHEN_SAVING + (int) i + ".png");
            PixmapIO.PNG png = new PixmapIO.PNG();
            png.setFlipY(true);
            try {
                png.write(fileHandle, pix);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    public static void saveTurret(float worldSize, int targetResolution, String externalFolderPath) {
        Pixmap pix = drawTurret(worldSize, targetResolution);
        PixmapIO.writePNG(new FileHandle(externalFolderPath + "/" + ASSETS_TURRET + ".png"), pix);
    }

    public static void saveBullet(float worldSize, int targetResolution, String externalFolderPath) {
        Pixmap pix = drawBullet(worldSize, targetResolution);
        PixmapIO.writePNG(new FileHandle(externalFolderPath + "/" + ASSETS_BULLET + ".png"), pix);
    }

    public static void saveHealthBarWithVariousAngles(int fromAngleDeg,
                                                      int toAngleDeg,
                                                      int skipAngleDeg,
                                                      int targetResolution,
                                                      String externalFolderPath) {

        if (fromAngleDeg > toAngleDeg) throw new ValueOutOfRangeException("fromAngleDeg can't be greater than toAngleDeg");
        if (skipAngleDeg <= 0) throw new ValueOutOfRangeException("skipAngleDeg must be greater than zero");

        /*for (float i = fromAngleDeg; i <= toAngleDeg; i += skipAngleDeg) {
            Pixmap pix = drawSharpArc(HEALTH_BAR_RADIUS,
                    HEALTH_BAR_THICKNESS,
                    MathUtils.degRad * i,
                    HEALTH_BAR_COLOR,
                    WORLD_SIZE,
                    targetResolution);

            FileHandle fileHandle = new FileHandle(externalFolderPath + "/" + HEALTH_BAR_NAMING_WHEN_SAVING + (int) i + ".png");
            PixmapIO.PNG png = new PixmapIO.PNG();
            png.setFlipY(true);
            try {
                png.write(fileHandle, pix);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------------------

    public static int worldUnitsIntoResolution(float inWorldUnits, float worldSize, int targetResolution) {
        int inResolutionUnits = (int) (inWorldUnits / worldSize * targetResolution);
        return inResolutionUnits;
    }

    public static float resolutionIntoWorldUnits(int inResolutionUnits, float worldSize, int targetResolution) {
        float inWorldUnits = inResolutionUnits * worldSize / targetResolution;
        return inWorldUnits;
    }
}
