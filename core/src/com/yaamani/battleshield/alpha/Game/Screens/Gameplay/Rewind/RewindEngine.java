package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.LinkedList;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class RewindEngine implements Updatable {

    private Pool<BulletRecord> bulletRecordPool;



    private float deltaTimeFromTheLastPushedEvent;
    private float totalTimeForAllPushedEvents;
    private boolean eventPushed;

    private LinkedList<RewindEvent> rewindEvents; // When you pop, you pop the most recent event.


    // Rewinding.
    private float deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession;


    private GameplayScreen gameplayScreen;

    public RewindEngine(GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
        initializeRewindBulletEventPool();
        rewindEvents = new LinkedList<>();
    }

    @Override
    public void update(float delta) {
        //delta = Math.abs(delta);

        if (!gameplayScreen.isRewinding()) {

            if (eventPushed & !rewindEvents.isEmpty()) {
                eventPushed = false;
                totalTimeForAllPushedEvents += deltaTimeFromTheLastPushedEvent;
                deltaTimeFromTheLastPushedEvent = 0;
            }

            deltaTimeFromTheLastPushedEvent += delta;

        } else {

            deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession -= delta; // delta passed is negative.

            if (!rewindEvents.isEmpty()) {
                if (deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession >= rewindEvents.peek().deltaTimeToTheNextEvent) {
                    //deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession -=;
                }
            }

        }

    }

    public void pushRewindEvent(RewindEvent rewindEvent) {
        setDeltaTimeForLastPushedEvent();

        rewindEvents.push(rewindEvent);
        eventPushed = true;
    }

    public BulletRecord obtainBulletRecord() {
        return bulletRecordPool.obtain();
    }

    public void setDeltaTimeForLastPushedEvent() {
        if (!rewindEvents.isEmpty())
            rewindEvents.peek().setDeltaTimeToTheNextEvent(deltaTimeFromTheLastPushedEvent);
    }

    public void startRewinding() {
        deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession = 0;
    }




    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeRewindBulletEventPool() {
        bulletRecordPool = new Pool<BulletRecord>(REWIND_BULLET_EVENT_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected BulletRecord newObject() {
                return new BulletRecord();
            }

            @Override
            public BulletRecord obtain() {
                BulletRecord obj = super.obtain();
                obj.inPosY = 0;
                obj.outPosY = 0;
                obj.wasFake = false;
                obj.parentContainer = null;
                obj.region = null;
                obj.effect = null;

                return obj;
            }
        };
    }


    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public static class RewindEvent {

        private float deltaTimeToTheNextEvent; // Next here means the event that happened after this one (while playing not during rewinding).

        public float getDeltaTimeToTheNextEvent() {
            return deltaTimeToTheNextEvent;
        }

        private void setDeltaTimeToTheNextEvent(float deltaTimeToTheNextEvent) {
            this.deltaTimeToTheNextEvent = deltaTimeToTheNextEvent;
        }
    }

}
