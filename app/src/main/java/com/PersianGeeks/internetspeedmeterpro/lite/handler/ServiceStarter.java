package com.PersianGeeks.internetspeedmeterpro.lite.handler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;

import com.PersianGeeks.internetspeedmeterpro.lite.services.ConnectorService;


public class ServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isStartup(context)){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context,new Intent(context, ConnectorService.class));
            }
            else {
                context.startService(new Intent(context, ConnectorService.class));
            }
        }
    }
    protected Boolean isStartup(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getBoolean("startup", true);
    }
}