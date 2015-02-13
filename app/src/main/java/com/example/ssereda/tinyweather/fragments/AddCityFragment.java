package com.example.ssereda.tinyweather.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.utils.Utils;
import com.survivingwithandroid.weather.lib.model.City;

public class AddCityFragment extends Fragment {
    TextView TVcityHeader;
    EditText ETenterCity;
    ListView LVcityList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_add, null);

        TVcityHeader = (TextView) view.findViewById(R.id.tv_city_header);
        ETenterCity = (EditText) view.findViewById(R.id.et_enter_city);
        LVcityList = (ListView) view.findViewById(R.id.lv_city_list);
        LVcityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                LVcityList.getItemAtPosition(position);
                City city = (City) LVcityList.getAdapter().getItem(position);
                Log.e("mylog", city.getName());
                Log.e("mylog", city.getId());

                Utils.getCurrentCondition(city.getId());
            }
        });

        ETenterCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 3) {
                    Utils.searchCity(charSequence, getActivity(), LVcityList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Criteria criteria = null;
//        Criteria criteria = new Criteria();
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//        criteria.setCostAllowed(false);

        Utils.searchCityByLocation(getActivity(), LVcityList, criteria);

        return view;
    }
}
