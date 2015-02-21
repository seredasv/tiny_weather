package com.example.ssereda.tinyweather.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Weather;
import com.survivingwithandroid.weather.lib.model.City;

public class AddCityFragment extends Fragment {
    private EditText etEnterCity;
    private ListView lvCityList;
    private ImageView ivTrackingOnOff;
    private boolean isTracking = true;
    private Criteria criteria;
    private Weather weather;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weather = Weather.getInstance(getActivity());
        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    private void updateDrawer() {
        ListView listView = (ListView) getActivity().findViewById(R.id.left_drawer);
        if (db != null && db.isOpen()) {
            String[] columns = new String[]{DBHelper.ID, DBHelper.PLACES_ID, DBHelper.PLACES_NAME};
            Cursor cursor = db.query(DBHelper.TABLE_PLACES, columns, null, null, null, null, null);
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

            weather.searchCityByLocation(getActivity(), lvCityList, criteria, R.layout.item_city_list);
        }

        linLayTrackingOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTracking) {
                    ivTrackingOnOff.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_view_pin_off));

                    isTracking = false;

                    weather.searchCityByLocation(getActivity(), lvCityList, null, R.layout.item_city_list);
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

                    weather.searchCityByLocation(getActivity(), lvCityList, criteria, R.layout.item_city_list);
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
                    weather.searchCity(charSequence, getActivity(), lvCityList, R.layout.item_city_list);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }
}
