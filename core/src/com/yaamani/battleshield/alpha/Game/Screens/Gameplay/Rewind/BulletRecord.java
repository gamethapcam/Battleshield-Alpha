package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Bullet;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Utilities.Constants;

public class BulletRecord extends RewindEngine.RewindEvent {

    private static final String TAG = BulletRecord.class.getSimpleName();

    public float inPosY;
    public float outPosY;

    public Bullet.BulletType bulletType;
    public Constants.SpecialBullet specialType;
    public boolean questionMark;

    public float timeAfterFakeTweenFinished;

    public int parentContainerIndex;

    public Constants.BulletPortalType bulletPortalType;

    //public boolean effectTookPlace;

    public BulletRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        Bullet bullet = gameplayScreen.getBulletsHandler().getBulletPool().obtain();
        /*if (bulletPortalType == Constants.BulletPortalType.PORTAL_ENTRANCE)
            Gdx.app.log(TAG, "HERE!!");*/
        bullet.attachForRewinding(this);
    }

    @Override
    public String toString() {
        return "\n{" + BulletRecord.class.getSimpleName() + "|"
                + super.toString() + "|"
                + inPosY + "|"
                + outPosY + "|"
                + bulletType + "|"
                + specialType + "|"
                + questionMark + "|"
                + timeAfterFakeTweenFinished + "|"
                + parentContainerIndex + "|"
                + bulletPortalType/* + "|"
                + effectTookPlace + "}"*/;
    }
}
