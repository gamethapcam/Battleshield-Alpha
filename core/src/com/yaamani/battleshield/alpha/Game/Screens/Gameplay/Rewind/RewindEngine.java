package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;
import com.yaamani.battleshield.alpha.Game.Screens.Gameplay.GameplayScreen;
import com.yaamani.battleshield.alpha.MyEngine.Updatable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import static com.yaamani.battleshield.alpha.Game.Utilities.Constants.*;

public class RewindEngine implements Updatable {

    private static final String TAG = RewindEngine.class.getSimpleName();

    private Pool<BulletRecord> bulletRecordPool;
    private Pool<PlusMinusBulletsRecord> plusMinusBulletsRecordPool;



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
        initializePlusMinusBulletsRecordPool();
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
                        freeRewindEvent(event);
                    } else
                        break;
                } else if (gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount() == 0 &
                        !gameplayScreen.getBulletsAndShieldContainers()[0].getRotationOmegaAlphaTween().isStarted() &
                        !gameplayScreen.getRewindNegativeToPositiveDeltaFractionTween().isStarted()) {

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
                freeRewindEvent(dequeuedEvent);
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

    public PlusMinusBulletsRecord obtainPlusMinusBulletsRecord() {
        return plusMinusBulletsRecordPool.obtain();
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

    private void freeRewindEvent(RewindEvent event) {
        if (event instanceof BulletRecord)
            bulletRecordPool.free((BulletRecord) event);
        else if (event instanceof PlusMinusBulletsRecord)
            plusMinusBulletsRecordPool.free((PlusMinusBulletsRecord) event);
        else
            throw new IllegalStateException("A subclass of RewindEvent that you forgot to free.");
    }

    public void clearRewindEvents() {
        ListIterator<RewindEvent> it = rewindEvents.listIterator();
        while (it.hasNext()) {
            RewindEvent event = it.next();
            freeRewindEvent(event);
            it.remove();
        }
        //rewindEvents.clear();
    }



    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------
    //------------------------------ initializers ------------------------------

    private void initializeRewindBulletEventPool() {
        bulletRecordPool = new Pool<BulletRecord>(REWIND_BULLET_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected BulletRecord newObject() {
                return new BulletRecord(gameplayScreen);
            }

            @Override
            public BulletRecord obtain() {
                BulletRecord obj = super.obtain();
                obj.inPosY = 0;
                obj.outPosY = 0;
                obj.bulletType = null;
                obj.specialType = null;
                obj.wasFake = false;
                obj.parentContainerIndex = -1;
                obj.effectTookPlace = false;

                return obj;
            }
        };
    }

    private void initializePlusMinusBulletsRecordPool() {
        plusMinusBulletsRecordPool = new Pool<PlusMinusBulletsRecord>(REWIND_PLUS_MINUS_BULLETS_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected PlusMinusBulletsRecord newObject() {
                return new PlusMinusBulletsRecord(gameplayScreen);
            }

            @Override
            public PlusMinusBulletsRecord obtain() {
                PlusMinusBulletsRecord obj = super.obtain();
                obj.containerIndex = -1;
                return obj;
            }
        };
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public static abstract class RewindEvent {

        private float deltaTimeToTheNextEvent; // Next here means the event that happened after this one (while playing not during rewinding).

        protected GameplayScreen gameplayScreen;

        public RewindEvent(GameplayScreen gameplayScreen) {
            this.gameplayScreen = gameplayScreen;
        }

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
