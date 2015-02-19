package com.example.ssereda.tinyweather.adapters;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.fragments.DayWeatherFragment;
import com.example.ssereda.tinyweather.fragments.HourWeatherFragment;
import com.example.ssereda.tinyweather.fragments.WeatherFragment;
import com.example.ssereda.tinyweather.utils.DBHelper;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    public CustomPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                Log.e("mylog", "0");
                fragment = new WeatherFragment();
                String lastPlaceID = MainActivity.sharedPreferences.getString(DBHelper.ID, "");
                if (lastPlaceID != null && lastPlaceID.length() > 0) {
                    String[] columns = new String[]{DBHelper.ID , DBHelper.PLACES_COUNTRY,
                            DBHelper.PLACES_REGION, DBHelper.PLACES_ID, DBHelper.PLACES_NAME };
                    if (MainActivity.db != null && MainActivity.db.isOpen()) {
                        Cursor cursor = MainActivity.db.query(DBHelper.TABLE_PLACES, columns, DBHelper.ID
                                + " = ? ", new String[]{lastPlaceID}, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                MainActivity.toolbar.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_NAME)));

                                Bundle bundle = new Bundle();
                                bundle.putInt(DBHelper.ID, cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
                                bundle.putString(DBHelper.PLACES_ID, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_ID)));
                                bundle.putString(DBHelper.PLACES_COUNTRY, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_COUNTRY)));
                                bundle.putString(DBHelper.PLACES_REGION, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_REGION)));
                                bundle.putString(DBHelper.PLACES_NAME, cursor.getString(cursor.getColumnIndex(DBHelper.PLACES_NAME)));
                                fragment.setArguments(bundle);

                                if (MainActivity.sharedPreferences != null) {
                                    SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                                    editor.putString(DBHelper.ID, cursor.getString(cursor.getColumnIndex(DBHelper.ID)));
                                    editor.apply();
                                }
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                break;
            case 1:
                Log.e("mylog", "1");
                fragment = new DayWeatherFragment();
                break;
            case 2:
                Log.e("mylog", "2");
                fragment = new HourWeatherFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
