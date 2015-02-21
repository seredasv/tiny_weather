package com.example.ssereda.tinyweather.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.DateUtils;
import com.example.ssereda.tinyweather.utils.Weather;

import java.util.Date;
import java.util.GregorianCalendar;

public class WeatherFragment extends Fragment {
    private String placesID, placesName;
    private TextView tvWind, tvHumidity, tvCurrentTemperature, tvTemperature_1, tvTemperature_2,
            tvTemperature_3;
    private ImageView imageViewCurrentWeatherIcon;
    private Weather weather;
    private DateUtils dateUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateUtils = new DateUtils(new Date(), new GregorianCalendar());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            placesID = bundle.getString(DBHelper.PLACES_ID);
            placesName = bundle.getString(DBHelper.PLACES_NAME);
        }

        weather = Weather.getInstance(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("last_place_id", Context.MODE_PRIVATE);

        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(DBHelper.PLACES_ID, placesID);
            editor.putString(DBHelper.PLACES_NAME, placesName);
            editor.apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        imageViewCurrentWeatherIcon = (ImageView) view.findViewById(R.id.image_view_current_weather);
        TextView tvCurrentDayData = (TextView) view.findViewById(R.id.text_view_current_day_data);
        TextView tvDayData_1 = (TextView) view.findViewById(R.id.text_view_day_data_1);
        TextView tvDayData_2 = (TextView) view.findViewById(R.id.text_view_day_data_2);
        TextView tvDayData_3 = (TextView) view.findViewById(R.id.text_view_day_data_3);
        TextView tvPlaceName = (TextView) view.findViewById(R.id.text_view_place_name);
        tvWind = (TextView) view.findViewById(R.id.text_view_wind);
        tvHumidity = (TextView) view.findViewById(R.id.text_view_humidity);
        tvCurrentTemperature = (TextView) view.findViewById(R.id.text_view_current_temperature);
        tvCurrentTemperature.setTypeface(typeface);
        tvTemperature_1 = (TextView) view.findViewById(R.id.text_view_temperature_1);
        tvTemperature_2 = (TextView) view.findViewById(R.id.text_view_temperature_2);
        tvTemperature_3 = (TextView) view.findViewById(R.id.text_view_temperature_3);
        TextView tvWeatherUpdateTime = (TextView) view.findViewById(R.id.text_view_weather_update_time);
        ImageView imageViewUpdateWeather = (ImageView) view.findViewById(R.id.image_view_update_weather);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.material_toolbar);

        imageViewUpdateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placesID != null && placesID.length() > 0) {
                    weather.getCurrentCondition(placesID, tvWind, tvCurrentTemperature,
                            tvHumidity, imageViewCurrentWeatherIcon);
                    weather.getForecastWeather(placesID, 1, tvTemperature_1);
                    weather.getForecastWeather(placesID, 2, tvTemperature_2);
                    weather.getForecastWeather(placesID, 3, tvTemperature_3);
                }

                setUpdatedText(R.id.text_view_weather_update_time);
            }
        });

        toolbar.setTitle(placesName);

        if (placesID != null && placesID.length() > 0) {
            weather.getCurrentCondition(placesID, tvWind, tvCurrentTemperature,
                    tvHumidity, imageViewCurrentWeatherIcon);
            weather.getForecastWeather(placesID, 1, tvTemperature_1);
            weather.getForecastWeather(placesID, 2, tvTemperature_2);
            weather.getForecastWeather(placesID, 3, tvTemperature_3);
        }

        dateUtils.sdfWeekDayAndTime(tvCurrentDayData, tvWeatherUpdateTime);
        dateUtils.sdfWeekDay(tvDayData_1, 1);
        dateUtils.sdfWeekDay(tvDayData_2, 2);
        dateUtils.sdfWeekDay(tvDayData_3, 3);

        if (placesName != null && placesName.length() > 0) {
            tvPlaceName.setText(placesName);
        }

        return view;
    }

    private void setUpdatedText(int id) {
        if (getView() != null) {
            TextView textView = (TextView) getView().findViewById(id);
            if (dateUtils != null) {
                dateUtils = null;
            }
            dateUtils = new DateUtils(new Date(), new GregorianCalendar());
            dateUtils.sdfTime(textView);
        }
    }
}
