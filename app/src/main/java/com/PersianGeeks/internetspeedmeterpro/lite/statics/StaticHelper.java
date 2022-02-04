package com.PersianGeeks.internetspeedmeterpro.lite.statics;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.view.View;

import java.io.InputStream;
import java.io.OutputStream;

public class StaticHelper {
    public static String TERMINAL_MESSAGE= "";
    public static boolean CONNECTION_STATUS= false;

    public static boolean isSms ;
    public static boolean isLocation;
    public static View v;
    public static Context context ;
    public static String phone;

    public static String Temperature = "Temperature: --";
    public static String CO2= "CO2: --";
    public static String Humidity= "Humidity: --";
}
