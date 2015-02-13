package com.example.ssereda.tinyweather.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.Calendar;

public class WeatherFragment extends Fragment {
    private int id;
    private String placesID, placesCountry, placesRegion, placesName;
    private TextView textViewWind, textViewTemperature, textViewHumidity, textViewCurrentTemperature;
    private Calendar calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt(DBHelper.ID);
            placesID = bundle.getString(DBHelper.PLACES_ID);
            placesCountry = bundle.getString(DBHelper.PLACES_COUNTRY);
            placesRegion = bundle.getString(DBHelper.PLACES_REGION);
            placesName = bundle.getString(DBHelper.PLACES_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

        TextView textViewCurrentDayData = (TextView) view.findViewById(R.id.text_view_current_day_data);
        TextView textViewDayData_1 = (TextView) view.findViewById(R.id.text_view_day_data_1);
        TextView textViewDayData_2 = (TextView) view.findViewById(R.id.text_view_day_data_2);
        TextView textViewDayData_3 = (TextView) view.findViewById(R.id.text_view_day_data_3);
        TextView textViewPlaceName = (TextView) view.findViewById(R.id.text_view_place_name);
        textViewWind = (TextView) view.findViewById(R.id.text_view_wind);
        textViewTemperature = (TextView) view.findViewById(R.id.text_view_temperature);
        textViewHumidity = (TextView) view.findViewById(R.id.text_view_humidity);
        textViewCurrentTemperature = (TextView) view.findViewById(R.id.text_view_current_temperature);
        textViewCurrentTemperature.setTypeface(typeface);
        TextView textViewTemperature_1 = (TextView) view.findViewById(R.id.text_view_temperature_1);
        TextView textViewTemperature_2 = (TextView) view.findViewById(R.id.text_view_temperature_2);
        TextView textViewTemperature_3 = (TextView) view.findViewById(R.id.text_view_temperature_3);

        MainActivity.toolbar.setTitle(placesName);

        if (placesID != null && placesID.length() > 0) {
            getCurrentCondition(placesID);
        }

        calendar = Calendar.getInstance();
        String currentDayOfWeek = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        String currentHour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String currentMinute = String.valueOf(calendar.get(Calendar.MINUTE));
        textViewCurrentDayData.setText(currentDayOfWeek + "  " + currentHour + ":" + currentMinute);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_1 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_1.setText(dayOfWeek_1);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_2 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_2.setText(dayOfWeek_2);

        calendar.add(Calendar.DAY_OF_WEEK, 1);
        String dayOfWeek_3 = String.valueOf(getWeekDayName(changeFirstDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1)));
        textViewDayData_3.setText(dayOfWeek_3);

        if (placesName != null && placesName.length() > 0) {
            textViewPlaceName.setText(placesName);
        }

        return view;
    }

    private void getCurrentCondition(String cityID) {
        MainActivity.weatherClient.getCurrentCondition(new WeatherRequest(cityID), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather currentWeather) {
                int currentTemperature = (int) currentWeather.weather.temperature.getTemp();
                int currentHumidity = (int) currentWeather.weather.currentCondition.getHumidity();
                int currentWindSpeed = (int) currentWeather.weather.wind.getSpeed();

                textViewWind.setText(String.valueOf(currentWindSpeed) + " m/s");
                textViewTemperature.setText(String.valueOf(currentTemperature) + "\u00b0");
                textViewCurrentTemperature.setText(String.valueOf(currentTemperature) + "\u00b0");
                textViewHumidity.setText(String.valueOf(currentHumidity) + " mm");

//                weatherIcon.setImageResource(WeatherIconMapper.getWeatherResource(currentWeather.weather.currentCondition.getIcon(), currentWeather.weather.currentCondition.getWeatherId()));
//
//                setToolbarColor(currentWeather.weather.temperature.getTemp());
            }

            @Override
            public void onWeatherError(WeatherLibException weatherLibException) {
                Log.e("mylog", "weather lib exception");
                Log.e("mylog", String.valueOf(weatherLibException));
            }

            @Override
            public void onConnectionError(Throwable t) {
                Log.e("mylog", "connection error");
            }
        });
    }

    public String getWeekDayName(int weekDay) {
        switch (weekDay) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "";
        }
    }

    public int changeFirstDayOfWeek(int dayOfWeek) {
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
            return dayOfWeek;
        } else {
            return dayOfWeek;
        }
    }
}
