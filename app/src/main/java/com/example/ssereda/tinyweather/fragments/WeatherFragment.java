package com.example.ssereda.tinyweather.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ssereda.tinyweather.R;

public class WeatherFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, null);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        TextView textView = (TextView) view.findViewById(R.id.text_view_today_temperature);
        TextView textViewLeftDayData = (TextView) view.findViewById(R.id.text_view_left_day_data);
        TextView textViewMiddleDayData = (TextView) view.findViewById(R.id.text_view_middle_day_data);
        TextView textViewRightDayData = (TextView) view.findViewById(R.id.text_view_right_day_data);
        textView.setTypeface(typeface);

        return view;
    }
}
