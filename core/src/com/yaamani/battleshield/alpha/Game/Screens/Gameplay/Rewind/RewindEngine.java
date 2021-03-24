package com.yaamani.battleshield.alpha.Game.Screens.Gameplay.Rewind;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
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
    private Pool<PortalRecord> portalRecordPool;
    private Pool<BulletEffectRecord> bulletEffectRecordPool;
    private Pool<TouchInputRecord> touchInputRecordPool;
    private Pool<AffectTimerRecord> affectTimerRecordPool;
    private Pool<AffectTimerColorRecord> affectTimerColorRecordPool;



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
        initializePortalRecordPool();
        initializeBulletEffectRecordPool();
        initializeTouchInputRecordPool();
        initializeAffectTimerRecordPool();
        initializeAffectTimerColorRecordPool();
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


            TouchInputRecord lastTouchInputRecord = null;

            while (true) {

                // Gdx.app.log(TAG, "currentInUseBulletsCount = " + gameplayScreen.getBulletsHandler().getCurrentInUseBulletsCount());

                if (!rewindEvents.isEmpty()) {
                    RewindEvent event = rewindEvents.peek();
                    if (deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession >= event.deltaTimeToTheNextEvent) {
                        Gdx.app.log(TAG, "@" + deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession + " rewinding " + event.toString());
                        deltaTimeFromTheLastPoppedEventInTheCurrentRewindingSession -= event.deltaTimeToTheNextEvent;

                        if (event instanceof TouchInputRecord)
                            lastTouchInputRecord = (TouchInputRecord) rewindEvents.pop();
                        else
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

            if (lastTouchInputRecord != null)
                lastTouchInputRecord.onStart();
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
        else if (event instanceof PortalRecord)
            portalRecordPool.free((PortalRecord) event);
        else if (event instanceof BulletEffectRecord)
            bulletEffectRecordPool.free((BulletEffectRecord) event);
        else if (event instanceof TouchInputRecord)
            touchInputRecordPool.free((TouchInputRecord) event);
        else if (event instanceof AffectTimerRecord)
            affectTimerRecordPool.free((AffectTimerRecord) event);
        else if (event instanceof AffectTimerColorRecord)
            affectTimerColorRecordPool.free((AffectTimerColorRecord) event);
        else
            throw new IllegalStateException("A subclass of RewindEvent that you forgot to free.");
    }

    public BulletRecord obtainBulletRecord() {
        return bulletRecordPool.obtain();
    }

    public PlusMinusBulletsRecord obtainPlusMinusBulletsRecord() {
        return plusMinusBulletsRecordPool.obtain();
    }

    public PortalRecord obtainPortalRecord() {
        return portalRecordPool.obtain();
    }

    public BulletEffectRecord obtainBulletEffectRecord(BulletEffectRecord.BulletEffectRecordType bulletEffectRecordType) {
        BulletEffectRecord record = bulletEffectRecordPool.obtain();
        record.bulletEffectRecordType = bulletEffectRecordType;
        return record;
    }

    public TouchInputRecord obtainTouchInputRecord() {
        return touchInputRecordPool.obtain();
    }

    public AffectTimerRecord obtainAffectTimerRecord() {
        return affectTimerRecordPool.obtain();
    }

    public AffectTimerColorRecord obtainAffectTimerColorRecord() {
        return affectTimerColorRecordPool.obtain();
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
                obj.timeAfterFakeTweenFinished = Float.MAX_VALUE/2f;
                obj.parentContainerIndex = -1;
                obj.bulletPortalType = null;
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

    private void initializePortalRecordPool() {
        portalRecordPool = new Pool<PortalRecord>(REWIND_PORTAL_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected PortalRecord newObject() {
                return new PortalRecord(gameplayScreen);
            }

            @Override
            public PortalRecord obtain() {
                PortalRecord obj = super.obtain();
                obj.containerIndex = -1;
                obj.duration = 0;
                return obj;
            }
        };
    }

    private void initializeBulletEffectRecordPool() {
        bulletEffectRecordPool = new Pool<BulletEffectRecord>(REWIND_BULLET_EFFECT_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected BulletEffectRecord newObject() {
                return new BulletEffectRecord(gameplayScreen);
            }
        };
    }

    private void initializeTouchInputRecordPool() {
        touchInputRecordPool = new Pool<TouchInputRecord>(REWIND_TOUCH_INPUT_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected TouchInputRecord newObject() {
                return new TouchInputRecord(gameplayScreen);
            }

            @Override
            public TouchInputRecord obtain() {
                TouchInputRecord obj = super.obtain();
                obj.controllerPosition = null;
                obj.event = null;
                obj.x = -1;
                obj.y = -1;
                obj.pointer = -1;
                return obj;
            }
        };
    }

    private void initializeAffectTimerRecordPool() {
        affectTimerRecordPool = new Pool<AffectTimerRecord>(REWIND_AFFECT_TIMER_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected AffectTimerRecord newObject() {
                return new AffectTimerRecord(gameplayScreen);
            }

            @Override
            public AffectTimerRecord obtain() {
                AffectTimerRecord obj = super.obtain();
                obj.affectTimerTweenInitialValue = -1;
                obj.affectTimerTweenFinalValue = -1;
                obj.affectTimerTweenDurationMillis = -1;
                obj.affectTimerTweenInterpolation = null;

                obj.affectTimerTweenFinalPercentage = -1;
                return obj;
            }
        };
    }

    private void initializeAffectTimerColorRecordPool() {
        affectTimerColorRecordPool = new Pool<AffectTimerColorRecord>(REWIND_AFFECT_TIMER_COLOR_RECORD_POOL_INITIAL_CAPACITY, Integer.MAX_VALUE, false) {
            @Override
            protected AffectTimerColorRecord newObject() {
                return new AffectTimerColorRecord(gameplayScreen);
            }

            @Override
            public AffectTimerColorRecord obtain() {
                AffectTimerColorRecord obj = super.obtain();
                obj.affectTimerTweenInitialValue = -1;
                obj.affectTimerTweenFinalValue = -1;
                obj.affectTimerColorTweenDurationMillis = -1;
                obj.affectTimerColorTweenFinalPercentage = -1;
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
