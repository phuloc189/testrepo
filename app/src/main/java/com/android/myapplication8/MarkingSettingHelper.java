package com.android.myapplication8;

public class MarkingSettingHelper {

    public static final String TAG = "MarkingSettingHelper";
    int markingValue_int;

    public MarkingSettingHelper(int value) {
        if (value > 1023) {
            markingValue_int = 1023;
        } else if (markingValue_int < 0) {
            markingValue_int = 0;
        } else {
            markingValue_int = value;
        }
    }

    public boolean[] getByteArray() {
        boolean[] result = new boolean[10];
        for (int i = 0; i < 10; i++) {
            result[i] = (markingValue_int & (1 << i)) > 0;
        }
        return result;
    }

    public boolean checkIfMatch(int markingValue) {
        if (markingValue < 0 || markingValue > (Util.CARD_MARKING_MAX_NUMBER_OF_VALUES - 1)) {
            return false;
        }
        return (markingValue_int & (1 << markingValue)) > 0;
    }

    public void setMarking(int which, boolean value) {
        if (which > 9 || which < 0) {
            Util.logDebug(TAG, "error: this bit is out of bound");
            return;
        }
        if (value) {
            markingValue_int = markingValue_int | (1 << which);
        } else {
            markingValue_int = markingValue_int & ~(1 << which); //false, then we delete that exactly bit
        }
    }

    public int getMarkingValue_int() {
        return markingValue_int;
    }
}
