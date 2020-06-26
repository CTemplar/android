package mobileapp.ctemplar.com.ctemplarapp.repository.enums;

public enum AutoLockTime {
    INSTANT(1),
    ONE_MINUTE(60),
    TWO_MINUTES(300),
    TEN_MINUTES(600),
    ONE_HOUR(3600),
    TWENTY_FOUR_HOURS(86400);

    private final int mValue;

    AutoLockTime(int value) {
        this.mValue = value * 1000;
    }

    public static int getId(int value) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].mValue == value) {
                return i;
            }
        }
        return 1;
    }

    public static int[] timeValues() {
        int[] timeValues = new int[values().length];
        for (int i = 0; i < values().length; i++) {
            timeValues[i] = values()[i].mValue;
        }
        return timeValues;
    }
}
