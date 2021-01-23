package com.yaamani.battleshield.alpha.MyEngine.Historization;

import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;


public class HistorizedLong {

    private static final String TAG = HistorizedLong.class.getSimpleName();

    private int theNumOfValuesToBeRecorded;

    private long[] history;
    private int currentIndex = -1;
    private int numOfRecordedValues;

    /**
     *
     * @param theNumOfValuesToBeRecorded must be greater than 0.
     * @throws
     */
    public HistorizedLong(int theNumOfValuesToBeRecorded) {
        if (theNumOfValuesToBeRecorded <= 0)
            throw new ValueOutOfRangeException("theNumOfValuesToBeRecorded(" + theNumOfValuesToBeRecorded + " must be greater than 0.");
        this.theNumOfValuesToBeRecorded = theNumOfValuesToBeRecorded;
        history = new long[theNumOfValuesToBeRecorded];
    }

    /**
     *
     * @param theNumOfValuesToBeRecorded must be greater than 0.
     * @param initialValue
     */
    public HistorizedLong(int theNumOfValuesToBeRecorded, long initialValue) {
        this(theNumOfValuesToBeRecorded);
        setValue(initialValue);
    }

    /**
     * <p>getValue(0) -> returns the current value.</p>
     * <p>getValue(-1) -> returns the previous value.</p>
     * <p>getValue(-2) -> returns the value before the previous one.</p>
     * @param i belongs to (-theNumOfValuesToBeRecorded, 0].
     * @throws ValueOutOfRangeException when the given i is outside its correct range.
     * @throws RuntimeException when the value corresponding to the given i isn't recorded yet.
     * @return
     */
    public long getValue(int i) {
        if (i > 0 | i <= -theNumOfValuesToBeRecorded)
            throw new ValueOutOfRangeException("i belongs to (-theNumOfValuesToBeRecorded, 0].");
        else if (i*-1 > numOfRecordedValues-1)
            throw new RuntimeException("i = " + i + " isn't recorded yet.");

        int index = (currentIndex+i);
        if (index < 0) index = index + theNumOfValuesToBeRecorded;
        //System.out.println("[" + TAG + "] i = " + i + ", index = " + index);
        return history[index];
    }

    /**
     * {@link HistorizedLong#getValue(int)}.
     * @return getValue(0).
     */
    public long getValue() {
        return getValue(0);
    }

    public void setValue(long value) {
        appendToHistory(value);
    }

    private void appendToHistory(long value) {
        currentIndex++;
        if (currentIndex >= theNumOfValuesToBeRecorded) currentIndex = 0;
        history[currentIndex] = value;

        numOfRecordedValues = MathUtils.clamp(numOfRecordedValues+1, 0, theNumOfValuesToBeRecorded);
    }

}
