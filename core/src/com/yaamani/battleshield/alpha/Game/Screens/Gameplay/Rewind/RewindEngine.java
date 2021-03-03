package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Arrays;
import java.util.LinkedList;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class RewindEngine implements Updatable {

    private static final String TAG = RewindEngine.class.getSimpleName();

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

            onEventPushed();

            deltaTimeFromTheLastPushedEvent += delta;

        } else {

            deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession -= delta; // delta passed is negative.


            while (true) {

                // Gdx.app.log(TAG, "currentInUseBulletsCount = " + gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount());

                if (!rewindEvents.isEmpty()) {
                    RewindEvent event = rewindEvents.peek();
                    if (deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession >= event.deltaTimeToTheNextEvent) {
                        Gdx.app.log(TAG, "@" + deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession + " rewinding " + event.toString());
                        deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession -= event.deltaTimeToTheNextEvent;
                        rewindEvents.pop().onStart();
                    } else
                        break;
                } else if (gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount() == 0 & !gameplayScreen.getRewindNegativeToPositiveDeltaFractionTween().isStarted()) {
                    gameplayScreen.getRewindNegativeToPositiveDeltaFractionTween().start(); // Transition from rewinding to not rewinding.
                    break;
                } else
                    break;
            }
        }
    }

    private void onEventPushed() {
        if (eventPushed & !rewindEvents.isEmpty()) {
            eventPushed = false;

            while (totalTimeForAllPushedEvents > REWIND_MAX_SAVING_TIME) {
                RewindEvent dequeuedEvent = rewindEvents.removeLast();
                totalTimeForAllPushedEvents -= dequeuedEvent.deltaTimeToTheNextEvent;
                Gdx.app.log(TAG, "XX Rewind Event Removed XX");
            }
        }
    }

    public void pushRewindEvent(RewindEvent rewindEvent) {
        setDeltaTimeForLastPushedEvent();

        rewindEvents.push(rewindEvent);
        eventPushed = true;
        if (!rewindEvents.isEmpty())
            totalTimeForAllPushedEvents += deltaTimeFromTheLastPushedEvent;
        deltaTimeFromTheLastPushedEvent = 0;

        // Gdx.app.log(TAG, Arrays.toString(rewindEvents.toArray()));
    }

    public BulletRecord obtainBulletRecord() {
        return bulletRecordPool.obtain();
    }

    public void setDeltaTimeForLastPushedEvent() {
        if (!rewindEvents.isEmpty())
            rewindEvents.peek().setDeltaTimeToTheNextEvent(deltaTimeFromTheLastPushedEvent);
        else
            totalTimeForAllPushedEvents = 0;

    }

    public void startRewinding() {
        deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession = 0;
        setDeltaTimeForLastPushedEvent();
        onEventPushed();
        Gdx.app.log(TAG, Arrays.toString(rewindEvents.toArray()));
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
                obj.bulletType = null;
                obj.specialType = null;
                obj.wasFake = false;
                obj.parentContainer = null;
                obj.effectTookPlace = false;
                obj.gameplayScreen = gameplayScreen;

                return obj;
            }
        };
    }


    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public static abstract class RewindEvent {

        private float deltaTimeToTheNextEvent; // Next here means the event that happened after this one (while playing not during rewinding).

        public float getDeltaTimeToTheNextEvent() {
            return deltaTimeToTheNextEvent;
        }

        private void setDeltaTimeToTheNextEvent(float deltaTimeToTheNextEvent) {
            this.deltaTimeToTheNextEvent = deltaTimeToTheNextEvent;
        }

        public abstract void onStart();

        @Override
        public String toString() {
            return deltaTimeToTheNextEvent + "|@" + Integer.toHexString(this.hashCode());
        }
    }

}
