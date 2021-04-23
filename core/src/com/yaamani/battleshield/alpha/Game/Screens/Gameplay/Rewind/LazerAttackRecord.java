package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.LazerAttackStuff;
import com.yaamani.battleshield.alpha.MyEngine.Timeline;

public class LazerAttackRecord extends RewindEngine.RewindEvent {

    public int currentNumOfLazerAttacksThatTookPlace;
    public int currentNecessaryNumOfArmorBulletsForTheNextAttack;
    public int currentNumOfSpawnedArmorBulletsForTheNextAttack;
    public int currentNumOfCollectedArmorBulletsByThePlayerForNextAttack;

    public LazerAttackRecord(GameplayScreen gameplayScreen) {
        super(gameplayScreen);
    }

    @Override
    public void onStart(float overTimeMillis) {
        LazerAttackStuff lazerAttackStuff = gameplayScreen.getLazerAttackStuff();

        lazerAttackStuff.setCurrentNumOfLazerAttacksThatTookPlace(currentNumOfLazerAttacksThatTookPlace);
        lazerAttackStuff.setCurrentNecessaryNumOfArmorBulletsForTheNextAttack(currentNecessaryNumOfArmorBulletsForTheNextAttack);
        lazerAttackStuff.setCurrentNumOfSpawnedArmorBulletsForTheNextAttack(currentNumOfSpawnedArmorBulletsForTheNextAttack);
        lazerAttackStuff.setCurrentNumOfCollectedArmorBulletsByThePlayerForNextAttack(currentNumOfCollectedArmorBulletsByThePlayerForNextAttack);

        lazerAttackStuff.updateCharacterSequenceForCollectedArmorBulletsText();

        lazerAttackStuff.setLazerAttacking(true);

        gameplayScreen.getBulletsHandler().calculateTimestampsForArmorBulletsToPrepareForTheNextLazerAttack();

        Timeline lazerAttackTimeline = lazerAttackStuff.getLazerAttack().getLazerAttackTimeline();
        lazerAttackTimeline.setCurrentTime(lazerAttackTimeline.getDurationMillis() - overTimeMillis);
    }
}
