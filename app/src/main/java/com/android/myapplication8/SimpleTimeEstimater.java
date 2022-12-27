package com.android.myapplication8;

import java.util.Calendar;

public class SimpleTimeEstimater {

    public static final long SECOND_IN_MILLIS = 1000;

    public static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;

    public static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;

    public static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    public static final long WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS;

    public static final long MONTH_IN_MILLIS_APPROX = 30 * DAY_IN_MILLIS;

    public static final long YEAR_IN_MILLIS_APPROX = 365 * DAY_IN_MILLIS;

    public enum TimeInterval {
        NOW(0, "just now"),
        MINUTE(MINUTE_IN_MILLIS, "minutes ago"),
        FEW_MINUTES(10 * MINUTE_IN_MILLIS, "less than an hour ago"),
        HOUR(HOUR_IN_MILLIS, "more than an hour ago"),
        MORE_THAN_AN_HOUR(2 * HOUR_IN_MILLIS, "few hours ago"),
        FEW_HOURS(5 * HOUR_IN_MILLIS, "many hours ago"),
        DAY(DAY_IN_MILLIS, "more than a day ago"),
        MORE_THAN_A_DAY(2 * DAY_IN_MILLIS, "few days ago"),
        WEEK(WEEK_IN_MILLIS, "more than a week ago"),
        MORE_THAN_A_WEEK(2 * WEEK_IN_MILLIS, "weeks ago"),
        MONTH(MONTH_IN_MILLIS_APPROX, "more than a month ago"),
        MORE_THAN_A_MONTH(2 * MONTH_IN_MILLIS_APPROX, "few months ago"),
        FEW_MONTH(5 * MONTH_IN_MILLIS_APPROX, "many months ago"),
        YEAR(YEAR_IN_MILLIS_APPROX, "eon ago");
        public final long timeInMillis;
        public final String flavourText;

        TimeInterval(long timeInMillis, String text) {
            this.timeInMillis = timeInMillis;
            this.flavourText = text;
        }
    }

    private static TimeInterval[] timeMilestones = {
            TimeInterval.NOW,
            TimeInterval.MINUTE,
            TimeInterval.FEW_MINUTES,
            TimeInterval.HOUR,
            TimeInterval.MORE_THAN_AN_HOUR,
            TimeInterval.FEW_HOURS,
            TimeInterval.DAY,
            TimeInterval.MORE_THAN_A_DAY,
            TimeInterval.WEEK,
            TimeInterval.MORE_THAN_A_WEEK,
            TimeInterval.MONTH,
            TimeInterval.MORE_THAN_A_MONTH,
            TimeInterval.FEW_MONTH,
            TimeInterval.YEAR};


    public static String howLongUntilNow(long pointInThePast) {
        long timeUntilNow = Calendar.getInstance().getTimeInMillis() - pointInThePast;
        if (timeUntilNow < 0) {
            return "bruh";
        } else if (timeUntilNow >= TimeInterval.YEAR.timeInMillis) {
            return TimeInterval.YEAR.flavourText;
        }

        int tension = 0;
        int ptrTail = 0;
        int ptrHead = timeMilestones.length - 1;
        int ptrMid = 0;

        while (tension++ < 100) {
            if ((ptrHead - ptrTail) <= 1) {
                break;
            }
            ptrMid = ptrTail + (int) Math.floor((ptrHead - ptrTail) / 2.0);
            if (timeUntilNow < timeMilestones[ptrMid].timeInMillis) {
                ptrHead = ptrMid;
            } else {
                ptrTail = ptrMid;
            }
        }

        return timeMilestones[ptrTail].flavourText;
    }
}
