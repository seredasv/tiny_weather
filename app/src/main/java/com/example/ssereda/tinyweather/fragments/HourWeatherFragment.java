package com.example.ssereda.tinyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.WeatherUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class HourWeatherFragment extends Fragment {
    private WeatherUtils weatherUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherUtils = WeatherUtils.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_hour, null);

        // add adMob to layout
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        ListView lvHourForecast = (ListView) view.findViewById(R.id.lv_hour_forecast);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_place_id", Context.MODE_PRIVATE);
        String placeID = sharedPreferences.getString(DBHelper.PLACES_ID, "");
        if (placeID != null && placeID.length() > 0) {
            weatherUtils.getHourForecastWeather(getActivity(), placeID, lvHourForecast, R.layout.item_hour_forecast);
        }

        return view;
    }
}
