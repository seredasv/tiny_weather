package com.example.ssereda.tinyweather.utils;

import android.content.Context;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;

//            Open weather map -> OpenweathermapProviderType
//            Yahoo! Weather -> YahooWeatherProviderType
//            Weather underground -> WeatherundergroundProviderType
//            Forecast.io -> ForecastIOProviderType

public class Weather {
    private static Weather instance;

    public static synchronized Weather getInstance() {
        if (instance == null) {
            instance = new Weather();
        }
        return instance;
    }

    public WeatherConfig config() {
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en"; // If you want to use english
        config.maxResult = 10; // Max number of cities retrieved
        config.numDays = 10; // Max num of days in the forecast

        return config;
    }

    public WeatherClient weatherClient(Context context) {
        WeatherClient weatherClient = null;
        try {
            weatherClient = (new WeatherClient.ClientBuilder()).attach(context)
                    .httpClient(WeatherDefaultClient.class)
                    .provider(new OpenweathermapProviderType())
                    .config(config())
                    .build();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return weatherClient;
    }
}
