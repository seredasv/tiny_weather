package com.example.ssereda.tinyweather.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.ssereda.tinyweather.MainActivity;
import com.example.ssereda.tinyweather.R;
import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;

public class Utils {
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
}
