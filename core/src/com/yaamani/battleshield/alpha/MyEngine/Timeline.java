package com.yaamani.battleshield.alpha.MyEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import java.util.Iterator;
import java.util.LinkedList;

public class Timeline extends Timer {

    private static final String TAG = Timeline.class.getSimpleName();

    private Array<Timer> timersAscendingStartTime;
    private FloatArray startTimeOfEachTimer;
    private int nextI;
    private LinkedList<Timer> currentlyRunning;

    //private Array<Timer> timersDescendingFinishTime;


    /**
     *
     * @param initialCapacity It's recommended to pass the exact number of timers that will be added to avoid array copying.
     */
    public Timeline(int initialCapacity) {
        timersAscendingStartTime = new Array<>(true, initialCapacity, Timer.class);
        startTimeOfEachTimer = new FloatArray(true, initialCapacity);
        currentlyRunning = new LinkedList<>();

        //timersDescendingFinishTime = new Array<>(true, initialCapacity, Timer.class);
    }

    /**
     * It's recommended to add timers in ascending order based on start time to avoid shifting array elements.
     * @param timer
     * @param startTimeMillis relative to the starting time of this timeline object.
     */
    public void addTimer(Timer timer, float startTimeMillis) {
        timersAscendingStartTime.add(timer);
        startTimeOfEachTimer.add(startTimeMillis);

        if (timersAscendingStartTime.size > 1) {
            // Calculate the index of the last timer with less than or equal start time.
            float[] startTimes = startTimeOfEachTimer.items;
            int i;
            for (i = startTimeOfEachTimer.size - 2; i >= 0; i--) {
                if (startTimes[i] <= startTimeMillis)
                    break;
            }

            // Shift to i+1
            if (i+1 < timersAscendingStartTime.size-1) {
                Timer[] timers = timersAscendingStartTime.items;
                MyMath.shiftElement(timers, startTimeOfEachTimer.size - 1, i + 1);
                MyMath.shiftElement(startTimes, startTimeOfEachTimer.size - 1, i + 1);
            }
        }

        super.setDurationMillis(startTimeOfEachTimer.peek() + timersAscendingStartTime.peek().getDurationMillis());
    }

    @Override
    public void onStart() {
        super.onStart();

        nextI = 0;
        currentlyRunning.clear();
    }

    @Override
    public boolean onUpdate(float delta) {

        Iterator<Timer> it = currentlyRunning.iterator();
        while (it.hasNext()) {
            if (!it.next().isStarted())
                it.remove();
        }

        if (nextI < startTimeOfEachTimer.size) {
            float currentTime = getCurrentTime();
            float nextStartTime = startTimeOfEachTimer.get(nextI);

            while (currentTime >= nextStartTime) {

                Timer nextTimer = timersAscendingStartTime.get(nextI);
                nextTimer.start();
                float overTime = currentTime - nextStartTime;
                nextTimer.setCurrentTime(overTime);
                currentlyRunning.add(nextTimer);

                nextI++;
                if (nextI >= startTimeOfEachTimer.size)
                    break;
                nextStartTime = startTimeOfEachTimer.get(nextI);
            }
        }
        return super.onUpdate(delta);
    }

    @Override
    public void onPause() {
        super.onPause();

        for (Timer currentlyRunningTimer : currentlyRunning)
            currentlyRunningTimer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();

        for (Timer currentlyRunningTimer : currentlyRunning)
            currentlyRunningTimer.resume();
    }

    @Override
    public void onFinish() {
        super.onFinish();

        Iterator<Timer> it = currentlyRunning.iterator();
        while (it.hasNext()) {
            it.next().finish();
            it.remove();
        }
    }

    // TODO: setPercentage, setCurrentTime, -ve delta handling.

    @Override
    public void setDurationMillis(float durationMillis) {
        //super.setDurationMillis(durationMillis);
        Gdx.app.error(TAG, "You cannot set the duration for a timeline object. It's calculated internally.");
    }

    /*public Array<Timer> getTimersAscendingStartTime() {
        return timersAscendingStartTime;
    }

    public FloatArray getStartTimeOfEachTimer() {
        return startTimeOfEachTimer;
    }*/
}
