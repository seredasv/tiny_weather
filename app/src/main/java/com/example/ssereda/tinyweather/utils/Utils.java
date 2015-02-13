package com.example.ssereda.tinyweather.utils;

import android.content.Context;
import android.location.Criteria;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ListView;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.CityAdapter;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.LocationProviderNotFoundException;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.model.WeatherForecast;
import com.survivingwithandroid.weather.lib.model.WeatherHourForecast;

import java.util.List;

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

    public static void searchCityByLocation(final Context context, final ListView listView, final Criteria criteria) {
        try {
            MainActivity.weatherClient.searchCityByLocation(WeatherClient.createDefaultCriteria(), new WeatherClient.CityEventListener() {
                //            MainActivity.weatherClient.searchCityByLocation(criteria, new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    if (criteria != null) {
                        CityAdapter cityAdapter = new CityAdapter(context, R.layout.item_city_list, cities);
                        listView.setAdapter(cityAdapter);
                    }
                }

                @Override
                public void onWeatherError(WeatherLibException wle) {
                    Log.e("mylog", "weather lib exception");
                }

                @Override
                public void onConnectionError(Throwable t) {
                    Log.e("mylog", "on connection error");
                }
            });
        } catch (LocationProviderNotFoundException e) {
            Log.e("mylog", String.valueOf(e));
        }
    }

    public static void searchCity(CharSequence charSequence, final Context context, final ListView listView) {
        if (MainActivity.weatherClient != null) {
            MainActivity.weatherClient.searchCity(charSequence.toString(), new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {
                    CityAdapter cityAdapter = new CityAdapter(context, R.layout.item_city_list, cities);
                    listView.setAdapter(cityAdapter);
                }

                @Override
                public void onWeatherError(WeatherLibException e) {
                    Log.e("mylog", "weather error");
                }

                @Override
                public void onConnectionError(Throwable throwable) {
                    Log.e("mylog", "connection error");
                }
            });
        }
    }

    protected static void updateView(Object obj) {
        CurrentWeather weather = (CurrentWeather) obj;
//        cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
//        condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
//        temp.setText("" + ((int) weather.temperature.getTemp()));
//        unitTemp.setText(weather.getUnit().tempUnit);
    }

    public static void getHourForecastWeather(String cityID) {
        MainActivity.weatherClient.getHourForecastWeather(cityID, new WeatherClient.HourForecastWeatherEventListener() {
                    @Override
                    public void onWeatherRetrieved(WeatherHourForecast weatherHourForecast) {
                        updateView(weatherHourForecast);
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

    public void getForecastWeather(String cityID) {
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
