package com.PersianGeeks.internetspeedmeterpro.lite.handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;



import java.util.Date;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import com.PersianGeeks.internetspeedmeterpro.lite.statics.StaticHelper;
import com.PersianGeeks.internetspeedmeterpro.lite.util.CodeHelper;


public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;
    private Context cx;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        this.cx = context;
        CodeHelper codeHelper = new CodeHelper();

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {

                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msg_from =msg_from.replaceFirst(Pattern.quote("+98"),"0");

                        if (!phoneNumber(context).equals(msg_from))
                            return;
                        String msgBody = msgs[i].getMessageBody();
                        Log.i("PYAM-----> ", msgBody);
                        logText("Received SMS from "+msg_from,msgBody);

                        if(msgBody.equals("Risk of theft")){
                            logText("Received SMS ","<<< Risk of theft >>>");
                            Intent inten = new Intent("ACTION3");
                            LocalBroadcastManager.getInstance(context).sendBroadcast(inten);
                            return;
                        }
                        if (!isSmsActive(context))
                            return;
                        String[] res = codeHelper.parsCode(msgBody);
                        if (res.length == 7){
                            Intent inte = new Intent("ACTION2");


                            for (int j = 0 ; j<= res.length ; j++){
                                switch (j){
                                    case 0:

                                        logText("Humidity: ",res[j].trim());
                                        inte.putExtra("Humidity", "Humidity: "+ res[j].trim());
                                        break;
                                    case 1:

                                        logText("Temperature: ",res[j].trim());
                                        inte.putExtra("Temperature", "Temperature: "+ res[j].trim());
                                        break;
                                    case 2:

                                        logText("CO2: ",res[j].trim());
                                        inte.putExtra("CO2", "CO2: "+ res[j].trim());
                                        break;
                                    case 3:
                                        syncKeysOne(Integer.valueOf(res[j].trim()));
                                        logText("syncKeysOne: ", res[j].trim());
                                        break;
                                    case 4:
                                        syncKeysTwo(Integer.valueOf(res[j].trim()));
                                        logText("syncKeysTwo: ", res[j].trim());
                                        break;
                                    case 5:
                                        syncKeysThree(Integer.valueOf(res[j].trim()));
                                        logText("syncKeysThree: ", res[j].trim());
                                        break;
                                    case 6:
                                        syncKeysFour(Integer.valueOf(res[j].trim()));
                                        logText("syncKeysFour: ", res[j].trim());
                                        break;

                                }
                            }
                            LocalBroadcastManager.getInstance(context).sendBroadcast(inte);
                        }
                    }

                } catch (Exception e) {
                    Log.e("erooorrrr" ,e.getCause().toString());
                    Log.e("erooorrrr" ,e.getStackTrace().toString());
                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    protected boolean isSmsActive(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("isSMS", true);
    }

    protected String phoneNumber(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("deviceNumber", "Does not exist");
    }

    protected String logText(String desc ,String text){
        return StaticHelper.TERMINAL_MESSAGE += "[Log - "+ new Date().getHours()+":"+ new Date().getMinutes()+":"+ new Date().getSeconds()+" ] "+desc+" "+text+" \n";
    }

    protected void syncKeysOne(Integer input) {
        String bin = Integer.toBinaryString(input);
        String padded = new String(new char[8 - bin.length()]).replace('\0', '0') + bin;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(padded);
        stringBuilder = stringBuilder.reverse();
        char[] charArray = stringBuilder.toString().toCharArray();
        int flag = 1;
        for (int i = 0; i < charArray.length; i++) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cx);
            SharedPreferences.Editor editor = prefs.edit();
            Boolean bool = false;

            if (charArray[i] == '0') {
                bool = false;
            } else {
                bool = true;
            }
            if(i == 8 || i == 7 || i == 6){
                if (i == 8) {
                    editor.putBoolean("automatic", bool);
                    editor.commit();

                    continue;
                }
                if (i == 7) {

                    editor.putBoolean("lights", bool);
                    editor.commit();

                    continue;
                }
                if (i == 6) {
                    editor.putBoolean("burglar", bool);
                    editor.commit();

                    continue;
                }
            }

            editor.putBoolean("key" + flag, bool);
            editor.commit();
            flag++;
        }
    }

    protected void syncKeysTwo(Integer input) {
        String bin = Integer.toBinaryString(input);
        String padded = new String(new char[8 - bin.length()]).replace('\0', '0') + bin;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(padded);
        stringBuilder = stringBuilder.reverse();
        char[] charArray = stringBuilder.toString().toCharArray();
        int flag = 6;
        for (int i = 0; i < charArray.length; i++) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cx);
            SharedPreferences.Editor editor = prefs.edit();
            Boolean bool = false;
            if (charArray[i] == '0') {
                bool = false;
            } else {
                bool = true;
            }
            editor.putBoolean("key" + flag, bool);
            editor.commit();
            flag++;
        }
    }


    protected void syncKeysThree(Integer input) {
        String bin = Integer.toBinaryString(input);
        String padded = new String(new char[8 - bin.length()]).replace('\0', '0') + bin;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(padded);
        stringBuilder = stringBuilder.reverse();
        char[] charArray = stringBuilder.toString().toCharArray();
        int flag = 14;
        for (int i = 0; i < charArray.length; i++) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cx);
            SharedPreferences.Editor editor = prefs.edit();
            Boolean bool = false;
            if (charArray[i] == '0') {
                bool = false;
            } else {
                bool = true;
            }
            editor.putBoolean("key" + flag, bool);
            editor.commit();
            flag++;
        }
    }


    protected void syncKeysFour(Integer input) {
        String bin = Integer.toBinaryString(input);
        String padded = new String(new char[8 - bin.length()]).replace('\0', '0') + bin;
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(padded);
        stringBuilder = stringBuilder.reverse();
        char[] charArray = stringBuilder.toString().toCharArray();
        int flag = 22;
        for (int i = 0; i < charArray.length; i++) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cx);
            SharedPreferences.Editor editor = prefs.edit();
            Boolean bool = false;
            if (charArray[i] == '0') {
                bool = false;
            } else {
                bool = true;
            }
            editor.putBoolean("key" + flag, bool);
            editor.commit();
            flag++;
        }
    }




}