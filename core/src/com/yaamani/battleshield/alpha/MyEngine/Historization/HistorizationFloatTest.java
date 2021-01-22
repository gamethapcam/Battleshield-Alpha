package com.yaamani.battleshield.alpha.MyEngine.Historization;

import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HistorizationFloatTest {

    @Test
    public void testConstructorThrowsValueOutOfRangeException() {
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> {
            HistorizedFloat historizedFloat = new HistorizedFloat(0);
        });

        Assertions.assertThrows(ValueOutOfRangeException.class, () -> {
            HistorizedFloat historizedFloat = new HistorizedFloat(-1);
        });
    }

    @Test
    public void testGetValueThrowsValueOutOfRangeException() {
        HistorizedFloat historizedFloat = new HistorizedFloat(3);
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedFloat.getValue(-3));
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedFloat.getValue(-5));
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedFloat.getValue(3));
    }

    @Test
    public void testGetValueThrowsRuntimeException() {
        HistorizedFloat historizedFloat = new HistorizedFloat(3);
        Assertions.assertThrows(RuntimeException.class, () -> historizedFloat.getValue(0));

        historizedFloat.setValue(10);
        Assertions.assertThrows(RuntimeException.class, () -> historizedFloat.getValue(1));
    }

    @Test
    public void testGetValue() {
        HistorizedFloat historizedFloat = new HistorizedFloat(3);

        historizedFloat.setValue(10);
        Assertions.assertEquals(10, historizedFloat.getValue());

        historizedFloat.setValue(11);
        Assertions.assertEquals(11, historizedFloat.getValue());
        Assertions.assertEquals(10, historizedFloat.getValue(-1));

        historizedFloat.setValue(12);
        Assertions.assertEquals(12, historizedFloat.getValue());
        Assertions.assertEquals(11, historizedFloat.getValue(-1));
        Assertions.assertEquals(10, historizedFloat.getValue(-2));

        historizedFloat.setValue(13);
        Assertions.assertEquals(13, historizedFloat.getValue());
        Assertions.assertEquals(12, historizedFloat.getValue(-1));
        Assertions.assertEquals(11, historizedFloat.getValue(-2));

        historizedFloat.setValue(14);
        Assertions.assertEquals(14, historizedFloat.getValue());
        Assertions.assertEquals(13, historizedFloat.getValue(-1));
        Assertions.assertEquals(12, historizedFloat.getValue(-2));


    }
}
