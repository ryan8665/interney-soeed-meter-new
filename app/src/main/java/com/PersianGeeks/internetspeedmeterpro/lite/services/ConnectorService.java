package com.PersianGeeks.internetspeedmeterpro.lite.services;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.PersianGeeks.internetspeedmeterpro.lite.MainActivity;
import com.PersianGeeks.internetspeedmeterpro.lite.R;
import com.PersianGeeks.internetspeedmeterpro.lite.statics.Data;
import com.PersianGeeks.internetspeedmeterpro.lite.util.Empty;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectorService extends Service {
    private NotificationManager mNotificationManager;
    private NotificationChannel mChannel;
    private String channelId = "channel-01";
    private String channelName = "Internet Speed Meter";
    boolean isFirst = true;
    long exRX, exTX;
    long nowRX, nowTX;
    double rxBPS, txBPS;
    private boolean firstTime = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter("ACTION3"));
        Log.i("Speed meter", "Service is Started");
        doTest();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopSelf();
        }
        Log.i("Speed meter", "Service Force Restart");
        super.onDestroy();
    }

    public void doTest() {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                if (autoHide()) {
                    String s, d, u;
                    double rxDiff = 0, txDiff = 0;
                    if (exRX == 0 || exTX == 0) {
                        exTX = TrafficStats.getTotalTxBytes();
                        exRX = TrafficStats.getTotalRxBytes();
                    }
                    nowTX = TrafficStats.getTotalTxBytes();
                    nowRX = TrafficStats.getTotalRxBytes();
                    rxDiff = nowRX - exRX;
                    txDiff = nowTX - exTX;

                    rxBPS = (rxDiff / (1000 / 1000));
                    txBPS = (txDiff / (1000 / 1000));


                    exRX = nowRX;
                    exTX = nowTX;

                    s = calculateData(txBPS + rxBPS);
                    d = calculateData(rxBPS);
                    u = calculateData(txBPS);
                    Data.setData(s, d, u, txBPS + rxBPS, rxBPS, txBPS);
                    publishData(s, d, u);
                    showNotification(
                            getResources().getString(R.string.speed) + ": "
                                    + s,
                            getResources().getString(R.string.download)
                                    + ": "
                                    + d
                                    + "  "
                                    + getResources().getString(
                                    R.string.upload) + ": " + u);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        stopForeground(STOP_FOREGROUND_REMOVE);
                    } else {
                        disappearNotification();
                    }

                }
            }

        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 999);

    }

    private void disappearNotification() {
        stopForeground(false);
        mNotificationManager.cancel(3128);
    }

    private void showNotification(String title, String body) {
        try {
            if (hide()) {
                showNotification(getApplicationContext(), title, body);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopForeground(STOP_FOREGROUND_REMOVE);
                } else {
                    disappearNotification();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String calculateData(double a) {
        String res;

        if (a / 1000 >= 1) {
            if ((a / 1000) / 1000000 >= 1) {
                res = Math.abs((int) (a / 1000) / 1000000) + " GB/s";
            } else {
                if ((a / 1000) / 1000 >= 1) {
                    res = Math.abs((int) (a / 1000) / 1000) + " " + getResources().getString(R.string.mb);
                } else {
                    res = Math.abs((int) a / 1000) + " " + getResources().getString(R.string.kb);
                }
            }

        } else {
            res = Math.abs((int) a) + " " + getResources().getString(R.string.b);
        }
        return res;

    }

    private String calculateIcon(double a) {
        try {
            String res = null;
            if (a / 1000 >= 1) {
                if ((a / 1000) / 1000 >= 1) {
                    int alpha = (int) ((a / 1000) / 1000);
                    switch (alpha) {
                        case 1:
                            res = "mb1_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 2:
                            res = "mb2_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 3:
                            res = "mb3_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 4:
                            res = "mb4_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 5:
                            res = "mb5_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 6:
                            res = "mb6_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 7:
                            res = "mb7_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 8:
                            res = "mb8_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 9:
                            res = "mb9_" + (((int) (a / 1000) % 1000) + "").substring(0, 1);
                            break;
                        case 10:
                            res = "mb10";
                            break;
                        case 11:
                            res = "mb11";
                            break;
                        case 12:
                            res = "mb12";
                            break;
                        case 13:
                            res = "mb13";
                            break;
                        case 14:
                            res = "mb14";
                            break;
                        case 15:
                            res = "mb15";
                            break;
                        case 16:
                            res = "mb16";
                            break;
                        case 17:
                            res = "mb17";
                            break;
                        case 18:
                            res = "mb18";
                            break;
                        case 19:
                            res = "mb19";
                            break;
                        case 20:
                            res = "mb20";
                            break;
                        case 21:
                            res = "mb21";
                            break;
                        case 22:
                            res = "mb22";
                            break;
                        case 23:
                            res = "mb23";
                            break;
                        case 24:
                            res = "mb24";
                            break;
                        case 25:
                            res = "mb25";
                            break;
                        case 26:
                            res = "mb26";
                            break;
                        case 27:
                            res = "mb27";
                            break;
                        case 28:
                            res = "mb28";
                            break;
                        case 29:
                            res = "mb29";
                            break;
                        case 30:
                            res = "mb30";
                            break;
                        case 31:
                            res = "mb31";
                            break;
                        case 32:
                            res = "mb32";
                            break;
                        case 33:
                            res = "mb33";
                            break;
                        case 34:
                            res = "mb34";
                            break;
                        case 35:
                            res = "mb35";
                            break;
                        case 36:
                            res = "mb36";
                            break;
                        case 37:
                            res = "mb37";
                            break;
                        case 38:
                            res = "mb38";
                            break;
                        case 39:
                            res = "mb39";
                            break;
                        case 40:
                            res = "mb40";
                            break;
                        case 41:
                            res = "mb41";
                            break;
                        case 42:
                            res = "mb42";
                            break;
                        case 43:
                            res = "mb43";
                            break;
                        case 44:
                            res = "mb44";
                            break;
                        case 45:
                            res = "mb45";
                            break;
                        case 46:
                            res = "mb46";
                            break;
                        case 47:
                            res = "mb47";
                            break;
                        case 48:
                            res = "mb48";
                            break;
                        case 49:
                            res = "mb49";
                            break;
                        case 50:
                            res = "mb50";
                            break;
                        case 51:
                            res = "mb51";
                            break;
                        case 52:
                            res = "mb52";
                            break;
                        case 53:
                            res = "mb54";
                            break;
                        case 55:
                            res = "mb55";
                            break;
                        case 56:
                            res = "mb56";
                            break;
                        case 57:
                            res = "mb57";
                            break;
                        case 58:
                            res = "mb58";
                            break;
                        case 59:
                            res = "mb59";
                            break;
                        case 60:
                            res = "mb60";
                            break;
                        case 61:
                            res = "mb61";
                            break;
                        case 62:
                            res = "mb62";
                            break;
                        case 63:
                            res = "mb63";
                            break;
                        case 64:
                            res = "mb64";
                            break;
                        case 65:
                            res = "mb65";
                            break;
                        case 66:
                            res = "mb66";
                            break;
                        case 67:
                            res = "mb67";
                            break;
                        case 68:
                            res = "mb68";
                            break;
                        case 69:
                            res = "mb69";
                            break;
                        case 70:
                            res = "mb70";
                            break;
                        case 71:
                            res = "mb71";
                            break;
                        case 72:
                            res = "mb72";
                            break;
                        case 73:
                            res = "mb73";
                            break;
                        case 74:
                            res = "mb74";
                            break;
                        case 75:
                            res = "mb75";
                            break;
                        case 76:
                            res = "mb76";
                            break;
                        case 77:
                            res = "mb77";
                            break;
                        case 78:
                            res = "mb78";
                            break;
                        case 79:
                            res = "mb79";
                            break;
                        case 80:
                            res = "mb80";
                            break;
                        case 81:
                            res = "mb81";
                            break;
                        case 82:
                            res = "mb82";
                            break;
                        case 83:
                            res = "mb83";
                            break;
                        case 84:
                            res = "mb84";
                            break;
                        case 85:
                            res = "mb85";
                            break;
                        case 86:
                            res = "mb86";
                            break;
                        case 87:
                            res = "mb88";
                            break;
                        case 88:
                            res = "mb88";
                            break;
                        case 89:
                            res = "mb89";
                            break;
                        case 90:
                            res = "mb90";
                            break;
                        case 91:
                            res = "mb91";
                            break;
                        case 92:
                            res = "mb92";
                            break;
                        case 93:
                            res = "mb93";
                            break;
                        case 94:
                            res = "mb94";
                            break;
                        case 95:
                            res = "mb95";
                            break;
                        case 96:
                            res = "mb96";
                            break;
                        case 97:
                            res = "mb97";
                            break;
                        case 98:
                            res = "mb98";
                            break;
                        case 99:
                            res = "mb99";
                            break;
                        default:
                            res = "wkb0";
                            break;


                    }
                } else {
                    res = "wkb" + (int) a / 1000;
                }

            } else {
                res = "wkb0";
            }


            return res;
        } catch (Exception e) {
            Log.e("Service Exception", e.getMessage());
            return "wkb0";
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected boolean hide() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("notificationSetting", true);
    }

    protected boolean autoHide() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (prefs.getBoolean("autoHideSetting", true)) {
            return isNetworkAvailable();
        } else {
            return true;
        }
    }

    protected boolean lockScreen() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("hideOnLockSetting", true);
    }

    protected boolean showExteraInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("hideExtera", true);
    }

    protected boolean showDailyDataUsage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return prefs.getBoolean("showDataUsage", false);
    }

    protected String getWifiName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return wifiInfo.getSSID();
        }
        return null;
    }

    protected boolean isWifiConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;


    }

    protected String carrierName() {
        TelephonyManager manager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Speed Meter", "BroadcastReceiver");

        }
    };

    private void publishData(String speed, String download, String upload) {
        Intent inten = new Intent("speed");
        inten.putExtra("speed", speed);
        inten.putExtra("download", download);
        inten.putExtra("upload", upload);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(inten);
    }

    private String setTrance(double a, double b) {
        double c = (a - b);

        return notifSpeedCalculator((int) c);

    }

    private String notifSpeedCalculator(int a) {
        String res;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);

        if (a / 1024 >= 1) {
            if ((a / 1000) / 1048576 >= 1) {
                res = df.format(Math.abs((double) (a / 1024) / 1048576)) + " GB";
            } else {
                if ((a / 1024) / 1024 >= 1) {
                    res = df.format(Math.abs((double) (a / 1024) / 1024)) + " MB";
                } else {
                    res = Math.abs((int) a / 1024) + " KB";
                }
            }

        } else {
            res = Math.abs((int) a) + getResources().getString(R.string.download);
        }
        return res;

    }

    protected void dailyUsageController() {
        try {
            if(Empty.isEmpty(Data.dailyDataUsage.getDate())){
                Data.dailyDataUsage = new Date();
            }
        } catch (Exception e) {
            Data.dailyDataUsage = new Date();
        }
        Date now = new Date();
        if (Data.dailyDataUsage.getDate() < now.getDate()) {
            getTrans();
            Data.dailyDataUsage = now;
        }

    }

    private void getTrans() {
        Data.dailyTotlSend = TrafficStats.getTotalTxBytes();
        Data.dailyTotalRecive = TrafficStats.getTotalRxBytes();
        Data.totalsend = TrafficStats.getTotalTxBytes();
        Data.totalrec = TrafficStats.getTotalRxBytes();
        Data.mobilesend = TrafficStats.getMobileTxBytes();
        Data.mobilerec = TrafficStats.getMobileRxBytes();
        Data.wifirec = TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes();
        Data.wifisend = TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
    }

    public void showNotification(Context context, String title, String body) {
        int visibility;
        if (lockScreen()) {
            visibility = NotificationCompat.VISIBILITY_PRIVATE;
        } else {
            visibility = NotificationCompat.VISIBILITY_PUBLIC;
        }

        dailyUsageController();
        if (showDailyDataUsage()) {
            if (isWifiConnected()) {
                body = getResources().getString(R.string.wifi) + ": " + setTrance(
                        (TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes())
                                - (TrafficStats.getMobileTxBytes() + TrafficStats.getMobileRxBytes()), Data.wifirec + Data.wifisend);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    body += "  " + getWifiName();
                }
            } else {
                body = getResources().getString(R.string.mobile) + ": " + setTrance(TrafficStats.getMobileTxBytes() + TrafficStats.getMobileRxBytes(), Data.mobilerec + Data.mobilesend);
                body += "  " + carrierName();
            }
        }

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = channelId;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);

            Intent in = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, in, PendingIntent.FLAG_ONE_SHOT);


            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setPriority(PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(getResources().getIdentifier(
                            calculateIcon(txBPS + rxBPS), "drawable",
                            ConnectorService.this.getPackageName()))
                    .setAutoCancel(true)
                    .setVisibility(visibility)
                    .setContentText(body).build();

            startForeground(8665, notification);
        } else {


            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(getResources().getIdentifier(
                                    calculateIcon(txBPS + rxBPS), "drawable",
                                    ConnectorService.this.getPackageName()))
                            .setContentTitle(title)
                            .setAutoCancel(true)
                            .setVisibility(visibility)
                            .setContentText(body);

            int notificationId = 8665;

            NotificationManager notifyMgr =
                    (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);

            notifyMgr.notify(notificationId, builder.build());

        }
    }
}
