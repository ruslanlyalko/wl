package com.ruslanlyalko.wl.presentation.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ruslan Lyalko
 * on 17.08.2018.
 */
public class DateUtils {

    private static final String FORMAT_STANDARD_DATE = "dd MMMM yyyy";
    private static final String FORMAT_NORMAL_DATE = "d MMM";
    private static final String FORMAT_NORMAL_DATE_YEAR = "d MMM yyyy";
    private static final String FORMAT_DATE_FULL = "EEEE d MMM";
    private static final String FORMAT_DATE = "EE, d MMM";
    private static final String FORMAT_HOLIDAY_KEY = "yyyyMMdd";
    private static final String FORMAT_TIME = "HH:mm";
    private static final String FORMAT_MONTH = "MMMM";
    private static final String FORMAT_YEAR = "yy";

    public static Date getDate(final Date date, final int year, final int month, final int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
//
//    public static String toStringBirthday(final Date date) {
//        if (date == null) return "";
//        Calendar today = Calendar.getInstance();
//        Calendar dob = Calendar.getInstance();
//        dob.setTime(date);
//        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
//        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--;
//        }
//        return new SimpleDateFormat(FORMAT_STANDARD_DATE + "' (" + age + ")'", Locale.US).format(date);
//    }

    public static String toStringStandardDate(final Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_STANDARD_DATE, Locale.US).format(date);
    }

    public static String toStringNormalDate(final Date date) {
        if (date == null) return "";
        if (isCurrentYear(date))
            return new SimpleDateFormat(FORMAT_NORMAL_DATE, Locale.US).format(date);
        return new SimpleDateFormat(FORMAT_NORMAL_DATE_YEAR, Locale.US).format(date);
    }

    public static String toStringDate(final Date date) {
        return new SimpleDateFormat(FORMAT_DATE, Locale.US).format(date);
    }

    public static String toStringTime(final Date date) {
        return new SimpleDateFormat(FORMAT_TIME, Locale.US).format(date);
    }

    public static Date getDate(final Date date, final int hours, final int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public static String toStringHolidayKey(final Date date) {
        return new SimpleDateFormat(FORMAT_HOLIDAY_KEY, Locale.US).format(date);
    }

    private static boolean isTomorrow(final Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, +1);
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == tomorrow.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == tomorrow.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isYesterday(final Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == yesterday.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == yesterday.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isToday(final Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public static Date getStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    public static Date getEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        return calendar.getTime();
    }

    public static String getMonth(final Date date) {
        if (isCurrentYear(date))
            return new SimpleDateFormat(FORMAT_MONTH, Locale.US).format(date);
        return new SimpleDateFormat(FORMAT_MONTH, Locale.US).format(date)
                + "'" + new SimpleDateFormat(FORMAT_YEAR, Locale.US).format(date);
    }

    private static boolean isCurrentYear(final Date date) {
        Calendar year = Calendar.getInstance();
        year.setTime(date);
        return year.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
    }

    public static Date getLastDateOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int numDays = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DAY_OF_MONTH, numDays);
        return calendar.getTime();
    }

    public static Date getFirstDateOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStart(date));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    public static String toString(final Date date, final String format) {
        return new SimpleDateFormat(format, Locale.US).format(date);
    }

    public static Calendar getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar;
    }

    public static Date get1DaysAgo(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    public static Calendar get1DaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -2);
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar;
    }

    public static Calendar get1DaysForward() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        return calendar;
    }

    public static Calendar get1MonthForward() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        return calendar;
    }

    public static Calendar get1YearAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static boolean dateEquals(final Date d1, final Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static int getYearIndex(final Date date, final Date firstWorkingDay) {
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        Calendar fwd = Calendar.getInstance();
        fwd.setTime(firstWorkingDay);
        int yearInd = today.get(Calendar.YEAR) - fwd.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < fwd.get(Calendar.DAY_OF_YEAR)) {
            yearInd--;
        }
        return yearInd;
    }

    public static Date addDay(final Date date, final int days) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.DAY_OF_MONTH, days);
        return c1.getTime();
    }

    public static Date addWeek(final Date date, final int weeks) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        c1.add(Calendar.WEEK_OF_YEAR, weeks);
        return c1.getTime();
    }

    public static boolean isWeekends(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    public static int daysBetween(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return daysBetween(calendar1, calendar2);
    }

    public static int daysBetween(Calendar day1, Calendar day2) {
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();
        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;
            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);
            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public static boolean isSameMonth(final Date date) {
        Calendar year = Calendar.getInstance();
        year.setTime(date);
        return year.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH);
    }

    private static boolean isSameMonth(final Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    public static String toStringDates(final Date from, final Date to) {
        if (dateEquals(from, to)) {
            return toStringNormalDate(from);
        }
        if (isSameMonth(from, to)) {
            return String.format(Locale.US, "%s - %s", toString(from, "d"), toStringNormalDate(to));
        } else
            return String.format(Locale.US, "%s - %s", toString(from, "d MMM"), toStringNormalDate(to));
    }
}
