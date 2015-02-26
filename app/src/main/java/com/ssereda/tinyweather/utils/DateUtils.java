package com.ssereda.tinyweather.utils;

import android.content.Context;
import android.widget.TextView;

import com.ssereda.tinyweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    Date date;
    GregorianCalendar gregorianCalendar;

    public DateUtils(Date date, GregorianCalendar gregorianCalendar) {
        this.date = date;
        this.gregorianCalendar = gregorianCalendar;
    }

    public void sdfTime(Context context, TextView textView) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

        textView.setText(context.getResources().getString(R.string.last_update) + " " + sdfTime.format(gregorianCalendar.getTime()));
    }

    public void sdfWeekDay(TextView textView, int addDay) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfWeekDay = new SimpleDateFormat("EEEE");

        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, addDay);
        textView.setText(sdfWeekDay.format(gregorianCalendar.getTime()));
    }

    public void sdfWeekDayAndTime(Context context, TextView tvDayData, TextView tvUpdateTime) {
        gregorianCalendar.setTime(date);
        SimpleDateFormat sdfWeekDayAndTime = new SimpleDateFormat("EEEE HH:mm");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

        tvDayData.setText(sdfWeekDayAndTime.format(gregorianCalendar.getTime()));
        tvUpdateTime.setText(context.getResources().getString(R.string.last_update) + " " + sdfTime.format(gregorianCalendar.getTime()));
    }

    public void sdfDay(int position, TextView textView) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.DAY_OF_WEEK, position + 1);
        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");

        textView.setText(sdfDay.format(gregorianCalendar.getTime()));
    }

    public void sdfHour(int position, TextView textView) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.add(GregorianCalendar.HOUR, position + 1);
        SimpleDateFormat sdfHour = new SimpleDateFormat("dd/MMM HH");

        textView.setText(sdfHour.format(gregorianCalendar.getTime()) + ":00");
    }
}
