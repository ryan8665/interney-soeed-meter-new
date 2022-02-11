package com.PersianGeeks.internetspeedmeterpro.lite;

import android.os.Bundle;
import android.view.MenuItem;

import com.PersianGeeks.internetspeedmeterpro.lite.base.BaseCompatActivity;
import com.PersianGeeks.internetspeedmeterpro.lite.statics.Data;
import com.PersianGeeks.internetspeedmeterpro.lite.ui.about.AboutFragment;
import com.PersianGeeks.internetspeedmeterpro.lite.ui.home.HomeFragment;
import com.PersianGeeks.internetspeedmeterpro.lite.ui.options.OptionFragment;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.ads.MobileAds;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends BaseCompatActivity {

    @Override
    protected void onDestroy() {
        Data.flag = false;
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        Data.flag = true;
        super.onResume();
    }
    @Override
    protected void onStop() {
        Data.flag = false;
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeFragment fragment = new HomeFragment();
        ft.replace(R.id.container_frag, fragment);
        ft.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment fragment = new HomeFragment();
                    ft.replace(R.id.container_frag, fragment);
                    ft.commit();
                    return true;
                case R.id.navigation_option:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container_frag, new OptionFragment())
                            .commit();
                    return true;
                case R.id.navigation_about:
                    AboutFragment AboutFragment = new AboutFragment();
                    ft.replace(R.id.container_frag, AboutFragment);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };


}