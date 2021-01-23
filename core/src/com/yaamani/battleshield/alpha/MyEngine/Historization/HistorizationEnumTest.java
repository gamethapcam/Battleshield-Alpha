package com.yaamani.battleshield.alpha.MyEngine.Historization;

import com.yaamani.battleshield.alpha.MyEngine.ValueOutOfRangeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HistorizationEnumTest {

    enum Days {SATURDAY, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY}

    @Test
    public void testConstructorThrowsValueOutOfRangeException() {
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> {
            HistorizedEnum<Days> historizedEnum = new HistorizedEnum<>(Days.class, 0);
        });

        Assertions.assertThrows(ValueOutOfRangeException.class, () -> {
            HistorizedEnum<Days> historizedEnum = new HistorizedEnum<>(Days.class, -1);
        });
    }

    @Test
    public void testGetValueThrowsValueOutOfRangeException() {
        HistorizedEnum<Days> historizedEnum = new HistorizedEnum<>(Days.class, 3);
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedEnum.getValue(-3));
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedEnum.getValue(-5));
        Assertions.assertThrows(ValueOutOfRangeException.class, () -> historizedEnum.getValue(3));
    }

    @Test
    public void testGetValueThrowsRuntimeException() {
        HistorizedEnum<Days> historizedEnum = new HistorizedEnum<>(Days.class, 3);
        Assertions.assertThrows(RuntimeException.class, () -> historizedEnum.getValue(0));

        historizedEnum.setValue(Days.FRIDAY);
        Assertions.assertThrows(RuntimeException.class, () -> historizedEnum.getValue(1));
    }

    @Test
    public void testGetValue() {
        HistorizedEnum<Days> historizedEnum = new HistorizedEnum<>(Days.class, 3);

        historizedEnum.setValue(Days.SATURDAY);
        Assertions.assertEquals(Days.SATURDAY, historizedEnum.getValue());

        historizedEnum.setValue(Days.SUNDAY);
        Assertions.assertEquals(Days.SUNDAY, historizedEnum.getValue());
        Assertions.assertEquals(Days.SATURDAY, historizedEnum.getValue(-1));

        historizedEnum.setValue(Days.MONDAY);
        Assertions.assertEquals(Days.MONDAY, historizedEnum.getValue());
        Assertions.assertEquals(Days.SUNDAY, historizedEnum.getValue(-1));
        Assertions.assertEquals(Days.SATURDAY, historizedEnum.getValue(-2));

        historizedEnum.setValue(Days.TUESDAY);
        Assertions.assertEquals(Days.TUESDAY, historizedEnum.getValue());
        Assertions.assertEquals(Days.MONDAY, historizedEnum.getValue(-1));
        Assertions.assertEquals(Days.SUNDAY, historizedEnum.getValue(-2));

        historizedEnum.setValue(Days.WEDNESDAY);
        Assertions.assertEquals(Days.WEDNESDAY, historizedEnum.getValue());
        Assertions.assertEquals(Days.TUESDAY, historizedEnum.getValue(-1));
        Assertions.assertEquals(Days.MONDAY, historizedEnum.getValue(-2));


    }
}
