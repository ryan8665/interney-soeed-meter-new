package com.PersianGeeks.internetspeedmeterpro.lite.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.PersianGeeks.internetspeedmeterpro.lite.services.ConnectorService;


public class RestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(RestartBroadcastReceiver.class.getSimpleName(), "Service Stopped, but this is a never ending service.");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context,new Intent(context, ConnectorService.class));
        }
        else {
            context.startService(new Intent(context, ConnectorService.class));
        }

    }
}