package com.example.ssereda.tinyweather.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

public class Utils {
    public static String cityID;

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return ((netInfo != null) && netInfo.isConnected());
    }

    protected static void updateView(Object obj) {
        CurrentWeather weather = (CurrentWeather) obj;
//        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
//        condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
//        temp.setText("" + ((int) weather.temperature.getTemp()));
//        unitTemp.setText(weather.getUnit().tempUnit);
    }

    public static void createNavigationDrawerAdapter(Context context) {
        Cursor cursor = null;
        if (MainActivity.db != null && MainActivity.db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_COUNTRY,
                    DBHelper.PLACES_REGION, DBHelper.PLACES_NAME};
            cursor = MainActivity.db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
        }

        String[] from = new String[]{
                DBHelper.PLACES_NAME
        };
        int[] to = new int[]{
                R.id.label
        };

        if (MainActivity.adapter != null) {
            MainActivity.adapter = null;
        }
        if (cursor != null) {
            MainActivity.adapter = new NavigationDrawerAdapter(context, R.layout.drawer_list_item, cursor, from, to, 0);
            MainActivity.drawerListView.setAdapter(MainActivity.adapter);
            MainActivity.adapter.changeCursor(cursor);
        }
    }

    public static void getHourForecastWeather(String cityID) {
        MainActivity.weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(WeatherHourForecast weatherHourForecast) {
                        updateView(weatherHourForecast);
//                        weatherHourForecast.getHourForecast().get(0).weather.temperature;
//                        weatherHourForecast.getHourForecast().get(0).timestamp;
//                        weatherHourForecast.getHourForecast().get(0).weather.clouds;
//                        weatherHourForecast.getHourForecast().get(0).weather.iconData;
//                        weatherHourForecast.getHourForecast().get(0).weather.rain;
//                        weatherHourForecast.getHourForecast().get(0).weather.snow;
//                        weatherHourForecast.getHourForecast().get(0).weather.wind;
                    }

                    @Override
                    public void onWeatherError(WeatherLibException wle) {
                        Log.e("mylog", "weather error");
                    }

                    @Override
                    public void onConnectionError(Throwable t) {
                        Log.e("mylog", "connection error");
                    }
                }
        );
    }

    public void getForecastWeather(final String cityID) {
        MainActivity.weatherClient.getForecastWeather(cityID, new WeatherClient.ForecastWeatherEventListener() {
            @Override
            public void onWeatherRetrieved(WeatherForecast weatherForecast) {
                updateView(weatherForecast);
            }

            @Override
            public void onWeatherError(WeatherLibException t) {
                Log.e("mylog", "weather error");
            }

            @Override
            public void onConnectionError(Throwable t) {
                Log.e("mylog", "connection error");
            }
        });
    }
}
