package com.yaamani.battleshield.alpha.MyEngine.Historization;

import com.badlogic.gdx.math.MathUtils;
import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;


public class HistorizedByte {

    private static final String TAG = HistorizedByte.class.getSimpleName();

    private int theNumOfValuesToBeRecorded;

    private byte[] history;
    private int currentIndex = -1;
    private int numOfRecordedValues;

    /**
     *
     * @param theNumOfValuesToBeRecorded must be greater than 0.
     * @throws
     */
    public HistorizedByte(int theNumOfValuesToBeRecorded) {
        if (theNumOfValuesToBeRecorded <= 0)
            throw new ValueOutOfRangeException("theNumOfValuesToBeRecorded(" + theNumOfValuesToBeRecorded + " must be greater than 0.");
        this.theNumOfValuesToBeRecorded = theNumOfValuesToBeRecorded;
        history = new byte[theNumOfValuesToBeRecorded];
    }


    /**
     *
     * @param theNumOfValuesToBeRecorded must be greater than 0.
     * @param initialValue
     */
    public HistorizedByte(int theNumOfValuesToBeRecorded, byte initialValue) {
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
    public byte getValue(int i) {
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
     * {@link HistorizedByte#getValue(int)}.
     * @return getValue(0).
     */
    public byte getValue() {
        return getValue(0);
    }

    public void setValue(byte value) {
        appendToHistory(value);
    }

    private void appendToHistory(byte value) {
        currentIndex++;
        if (currentIndex >= theNumOfValuesToBeRecorded) currentIndex = 0;
        history[currentIndex] = value;

        numOfRecordedValues = MathUtils.clamp(numOfRecordedValues+1, 0, theNumOfValuesToBeRecorded);
    }

}