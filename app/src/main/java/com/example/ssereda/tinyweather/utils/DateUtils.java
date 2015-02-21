package com.example.ssereda.tinyweather.utils;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    Date date;
    GregorianCalendar gregorianCalendar;

    public DateUtils(Date date, GregorianCalendar gregorianCalendar) {
        this.date = date;
        this.gregorianCalendar = gregorianCalendar;
    }

    public void sdfTime(TextView textView) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        textView.setText("last update: " + sdfTime.format(gregorianCalendar.getTime()));
    }

    public void sdfWeekDay(TextView textView, int addDay) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, addDay);
        textView.setText(sdfWeekDay.format(gregorianCalendar.getTime()));
    }

    public void sdfWeekDayAndTime(TextView tvDayData, TextView tvUpdateTime) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfWeekDayAndTime = new SimpleDateFormat("EEEE HH:mm", Locale.ENGLISH);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

        tvDayData.setText(sdfWeekDayAndTime.format(gregorianCalendar.getTime()));
        tvUpdateTime.setText("last update: " + sdfTime.format(gregorianCalendar.getTime()));
    }

    public void sdfDay(int position, TextView textView) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, position + 1);
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        textView.setText(sdfDay.format(gregorianCalendar.getTime()));
    }

    public void sdfHour(int position, TextView textView) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.HOUR, position + 1);
        SimpleDateFormat sdfHour = new SimpleDateFormat("dd/MMM HH", Locale.ENGLISH);

        textView.setText(sdfHour.format(gregorianCalendar.getTime()) + ":00");
    }
}
