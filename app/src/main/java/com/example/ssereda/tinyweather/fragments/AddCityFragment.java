package com.example.ssereda.tinyweather.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.CityAdapter;
import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Weather;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.exception.LocationProviderNotFoundException;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;

import java.util.List;

public class AddCityFragment extends Fragment {
    private EditText etEnterCity;
    private ListView lvCityList;
    private ImageView ivTrackingOnOff;
    private boolean isTracking = true;
    private Criteria criteria;
    private WeatherClient weatherClient;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherClient = Weather.getInstance().weatherClient(getActivity());
        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    private void updateDrawer() {
        ListView listView = (ListView) getActivity().findViewById(R.id.left_drawer);
        if (db != null && db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_NAME};
            Cursor cursor = db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
            listView.setAdapter(listView.getAdapter());
            ((NavigationDrawerAdapter) listView.getAdapter()).changeCursor(cursor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_add, null);

        etEnterCity = (EditText) view.findViewById(R.id.et_enter_city);
        lvCityList = (ListView) view.findViewById(R.id.lv_city_list);
        LinearLayout linLayTrackingOnOff = (LinearLayout) view.findViewById(R.id.linear_layout_tracking_on_off);
        ivTrackingOnOff = (ImageView) view.findViewById(R.id.image_view_tracking_on_off);
        lvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) lvCityList.getAdapter().getItem(position);

                if (db != null && db.isOpen()) {
                    ContentValues contentValues = new ContentValues();
                    if (city.getId() != null) {
                        contentValues.put(DBHelper.PLACES_ID, city.getId());
                    }
                    if (city.getName() != null) {
                        contentValues.put(DBHelper.PLACES_NAME, city.getName());
                    }
                    db.insert(DBHelper.TABLE_PLACES, null, contentValues);
                }

                Toast.makeText(getActivity(),
                        getActivity().getResources().getString(R.string.toast_add_city),
                        Toast.LENGTH_SHORT).show();

                updateDrawer();
            }
        });

        if (isTracking) {
            if (criteria == null) {
                criteria = new Criteria();
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setCostAllowed(false);
            }

            searchCityByLocation(getActivity(), lvCityList, criteria);
        }

        linLayTrackingOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    ivTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_off));

                    isTracking = false;

                    searchCityByLocation(getActivity(), lvCityList, null);
                } else {
                    ivTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_on));

                    isTracking = true;

                    etEnterCity.setText("");
                    if (criteria == null) {
                        criteria = new Criteria();
                        criteria.setPowerRequirement(Criteria.POWER_LOW);
                        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                        criteria.setCostAllowed(false);
                    }

                    searchCityByLocation(getActivity(), lvCityList, criteria);
                }
            }
        });

        etEnterCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 3) {
                    searchCity(charSequence, getActivity(), lvCityList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void searchCityByLocation(final Context context, final ListView listView, final Criteria criteria) {
        try {
            weatherClient.searchCityByLocation(WeatherClient.createDefaultCriteria(), new WeatherClient.CityEventListener() {
                //            weatherClient.searchCityByLocation(criteria, new WeatherClient.CityEventListener() {
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

    private void searchCity(CharSequence charSequence, final Context context, final ListView listView) {
        if (weatherClient != null) {
            weatherClient.searchCity(charSequence.toString(), new WeatherClient.CityEventListener() {
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
}
