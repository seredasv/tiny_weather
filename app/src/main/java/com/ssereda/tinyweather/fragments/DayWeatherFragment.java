package com.ssereda.tinyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ssereda.tinyweather.MainActivity;
import com.ssereda.tinyweather.R;
import com.ssereda.tinyweather.utils.DBHelper;
import com.ssereda.tinyweather.utils.WeatherUtils;

public class DayWeatherFragment extends Fragment {
    private WeatherUtils weatherUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherUtils = WeatherUtils.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_day, null);

        Tracker tracker = MainActivity.getTracker(MainActivity.TrackerName.APP_TRACKER);
        tracker.setScreenName("Day Weather Fragment");
        tracker.send(new HitBuilders.AppViewBuilder().build());

        ListView lvDayForecast = (ListView) view.findViewById(R.id.lv_day_forecast);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_place_id", Context.MODE_PRIVATE);
        String placeID = sharedPreferences.getString(DBHelper.PLACES_ID, "");
        if (placeID != null && placeID.length() > 0) {
            weatherUtils.getForecastWeather(getActivity(), placeID, lvDayForecast, R.layout.item_day_forecast);
        }

        return view;
    }
}
