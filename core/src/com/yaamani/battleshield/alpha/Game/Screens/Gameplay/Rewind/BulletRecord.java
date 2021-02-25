package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Bullet;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.BulletsAndShieldContainer;

public class BulletRecord extends RewindEngine.RewindEvent {

    public float inPosY;

    public float outPosY;

    public boolean wasFake;

    public BulletsAndShieldContainer parentContainer;

    public TextureRegion region;

    public Bullet.BulletEffect effect; // null if the effect didn't take place.

}
