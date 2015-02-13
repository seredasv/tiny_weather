package com.example.ssereda.tinyweather;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ssereda.tinyweather.adapters.NavigationDrawerAdapter;
import com.example.ssereda.tinyweather.fragments.AddCityFragment;
import com.example.ssereda.tinyweather.fragments.WeatherFragment;
import com.example.ssereda.tinyweather.utils.DBHelper;
import com.example.ssereda.tinyweather.utils.Utils;
import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.client.okhttp.WeatherDefaultClient;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.lang.reflect.Field;

public class MainActivity extends ActionBarActivity {
    public static final String WEATHER_FRAGMENT = "weather_fragment";
    public static final String ADD_CITY_FRAGMENT = "add_city_fragment";
    public static NavigationDrawerAdapter adapter;
    public static DrawerLayout drawerLayout;
    public static ListView drawerListView;
    public static Toolbar toolbar;
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private static long back_pressed;
    int backStack = 0;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private boolean closeDrawer = false;
    private int openFragment;
    private FrameLayout frameLayout;
    private float lastTranslate = 0.0f;
    public static WeatherClient weatherClient;
    private Cursor cursor;

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            if (count == backStack) {
                if (back_pressed + 2000 > System.currentTimeMillis()) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_exit),
                            Toast.LENGTH_SHORT).show();
                }
                back_pressed = System.currentTimeMillis();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        deleteDatabase(DBHelper.DATABASE_NAME);
        if (dbHelper == null) {
            dbHelper = new DBHelper(this);
        }
        db = dbHelper.getWritableDatabase();

        //instantiate builder
//        WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();

        //Weather config
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en"; // If you want to use english
        config.maxResult = 5; // Max number of cities retrieved
        config.numDays = 6; // Max num of days in the forecast

        // Sample WeatherLib weatherClient init
        try {

//            Open weather map -> OpenweathermapProviderType
//            Yahoo! Weather -> YahooWeatherProviderType
//            Weather underground -> WeatherundergroundProviderType
//            Forecast.io -> ForecastIOProviderType

            weatherClient = (new WeatherClient.ClientBuilder()).attach(this)
                    .httpClient(WeatherDefaultClient.class)
                    .provider(new OpenweathermapProviderType())
//                    .config(new WeatherConfig())
                    .config(config)
                    .build();

//            weatherClient.updateWeatherConfig(config);

            weatherClient.getCurrentCondition(new WeatherRequest("2988507"), new WeatherClient.WeatherEventListener() {
                @Override
                public void onWeatherRetrieved(CurrentWeather currentWeather) {
                    float currentTemp = currentWeather.weather.temperature.getTemp();
                    Log.e("mylog", "City [" + currentWeather.weather.location.getCity() + "] Current temp [" + currentTemp + "]");
                }

                @Override
                public void onWeatherError(WeatherLibException e) {
                    Log.e("mylog", "Weather Error - parsing data");
                    e.printStackTrace();
                }

                @Override
                public void onConnectionError(Throwable throwable) {
                    Log.e("mylog", "Connection error");
                    throwable.printStackTrace();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_launcher);
            setSupportActionBar(toolbar);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Fragment fragment;
                    FragmentTransaction transaction;
                    switch (menuItem.getItemId()) {
                        case R.id.action_add_city:
                            fragment = new AddCityFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(ADD_CITY_FRAGMENT);
                            if (fragment.isAdded()) {
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                transaction.replace(R.id.container, fragment, ADD_CITY_FRAGMENT);
                                transaction.commit();
                            }
                            break;
                        case R.id.action_weather_fragment:
                            fragment = new WeatherFragment();
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.addToBackStack(WEATHER_FRAGMENT);
                            if (fragment.isAdded()) {
                                transaction.show(fragment);
                                transaction.commit();
                            } else {
                                transaction.replace(R.id.container, fragment, WEATHER_FRAGMENT);
                                transaction.commit();
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        frameLayout = (FrameLayout) findViewById(R.id.container);
        LinearLayout linearLayoutAddANewCity = (LinearLayout) findViewById(R.id.ll_add_a_new_city);
        linearLayoutAddANewCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddCityFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(ADD_CITY_FRAGMENT);
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    transaction.replace(R.id.container, fragment, ADD_CITY_FRAGMENT);
                    transaction.commit();
                }
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        Utils.createNavigationDrawerAdapter(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                if (closeDrawer) {
                    displayView(openFragment);
                }
                closeDrawer = false;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // slide total container if drawer is open
                float moveFactor = (drawerListView.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    frameLayout.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    frameLayout.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    toolbar.setTranslationX(moveFactor);
                } else {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    toolbar.startAnimation(anim);
                    lastTranslate = moveFactor;
                }
            }

            public void onDrawerOpened(View drawerView) {
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        // change touch area to open drawer:
        Field mDragger = null;
        try {
            mDragger = drawerLayout.getClass().getDeclaredField("mLeftDragger");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mDragger != null) {
            mDragger.setAccessible(true);
        }
        ViewDragHelper draggerObj = null;
        try {
            if (mDragger != null) {
                draggerObj = (ViewDragHelper) mDragger.get(drawerLayout);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            if (draggerObj != null) {
                mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mEdgeSize != null) {
            mEdgeSize.setAccessible(true);
        }
        int edge = 0;
        try {
            if (mEdgeSize != null) {
                edge = mEdgeSize.getInt(draggerObj);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            if (mEdgeSize != null) {
                mEdgeSize.setInt(draggerObj, edge * 5);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        FragmentTransaction transaction;

        switch (position) {
            case 0:
//                    fragment = new HomeFragment();
//                    transaction = getFragmentManager().beginTransaction();
//                    transaction.addToBackStack(MY_AUDIO_FRAGMENT);
//                    if (fragment.isAdded()) {
//                        transaction.show(fragment);
//                        transaction.commit();
//                    } else {
//                        transaction.replace(R.id.container, fragment, MY_AUDIO_FRAGMENT);
//                        transaction.commit();
//                    }
//                break;
            default:
                break;
        }

        // update selected item and title, then close the drawer
        drawerListView.setItemChecked(position, true);
        drawerListView.setSelection(position);
//            setTitle(drawerNames[position]);
        drawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
//        drawerTitle = title;
//        getActionBar().setTitle(drawerTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openFragment = position;
            closeDrawer = true;
            drawerLayout.closeDrawer(Gravity.START);
        }
    }
}