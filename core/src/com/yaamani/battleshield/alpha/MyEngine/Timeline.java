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
     * It's recommended to add timers in ascending order based on start time to avoid or minimize shifting array elements.
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

        durationMillis = startTimeOfEachTimer.peek() + timersAscendingStartTime.peek().durationMillis;
    }

    @Override
    public void onStart() {
        super.onStart();

        nextI = 0;
        currentlyRunning.clear();

        for (Timer timer : timersAscendingStartTime) {
            timer.setPercentage(0);
            timer.onUpdate(0);
            timer.started = false;
        }
    }

    @Override
    public boolean onUpdate(float delta) {

        currentTime += delta*MyMath.SECONDS_TO_MILLIS;
        percentage = currentTime / durationMillis;

        Iterator<Timer> it = currentlyRunning.iterator();
        while (it.hasNext()) {
            if (!it.next().isStarted())
                it.remove();
        }

        if (delta > 0) { // O(1) most of the time, theta(n) if all the timers have the same start time.
            if (nextI < startTimeOfEachTimer.size) {
                //float currentTime = getCurrentTime();
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
        } else if (delta < 0) { // O(n), theta(n)
            Timer[] timers = timersAscendingStartTime.items;
            float[] startTimes = startTimeOfEachTimer.items;

            for (int i = timersAscendingStartTime.size-1; i >= 0; i--) {
                if (startTimes[i] < currentTime) {
                    if (!timers[i].isStarted()) {
                        if (currentTime < startTimes[i] + timers[i].durationMillis) {
                            timers[i].setCurrentTime(currentTime - startTimes[i]);
                            timers[i].onUpdate(0);
                            currentlyRunning.add(timers[i]);
                        }
                    }
                } else {
                    if (startTimes[i] > currentTime) {

                        timers[i].setPercentage(0);
                        timers[i].onUpdate(0);

                        if (nextI < timersAscendingStartTime.size) {
                            if (startTimes[i] < startTimes[nextI])
                                nextI = i;
                        } else {
                            nextI = i;
                        }
                    }
                }
            }
        }



        return /*super.onUpdate(delta)*/true;
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

    @Override
    protected void setCurrentTime(float currentTime) {
        super.setCurrentTime(currentTime);

        settingCurrentTimeCalculations(currentTime);
    }

    @Override
    public void setPercentage(float percentage) {
        super.setPercentage(percentage);

        settingCurrentTimeCalculations(percentage * durationMillis);
    }

    private void settingCurrentTimeCalculations(float currentTime) {
        currentlyRunning.clear();

        Timer[] timers = timersAscendingStartTime.items;
        float[] startTimes = startTimeOfEachTimer.items;

        nextI = timersAscendingStartTime.size;

        for (int i = timersAscendingStartTime.size-1; i >= 0; i--) {
            if (startTimes[i] > currentTime) {

                if (startTimes[i] < startTimes[nextI])
                    nextI = i;

            } else {

                if (startTimes[i] + timers[i].durationMillis > currentTime) {
                    currentlyRunning.add(timers[i]);
                    timers[i].setCurrentTime(currentTime - startTimes[i]);
                    timers[i].onUpdate(0);
                } else {
                    timers[i].finish();
                }

            }
        }
    }

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
