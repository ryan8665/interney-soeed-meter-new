package com.PersianGeeks.internetspeedmeterpro.lite.ui.home;

import static com.PersianGeeks.internetspeedmeterpro.lite.statics.StaticHelper.v;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.PersianGeeks.internetspeedmeterpro.lite.R;
import com.PersianGeeks.internetspeedmeterpro.lite.base.BaseFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class HomeFragment extends BaseFragment {
    private AdView adView;
    private TextView speedText = null;
    private AdView mAdView;

    long exRX, exTX;
    long nowRX, nowTX;
    double rxBPS, txBPS;
    public double drx = 10, dtx = 10, dall;
    public String speed = "0 Kb/s", down = "Download 0 Kb/s",
            up = "Upload 0 Kb/s";
    int flagCount = 0;
    double brx = 0, btx = 0;
    float[][] chartArray = new float[3][21];

    private LineChartView linechart;
    private LineChartData linedata;
    private int maxNumberOfLines = 3;
    private int numberOfPoints = 20;

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;


    String[] PERMISSIONS = {
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
//            android.Manifest.permission.BLUETOOTH_ADMIN,
//            android.Manifest.permission.RECEIVE_SMS,
//            android.Manifest.permission.SEND_SMS,
//            android.Manifest.permission.READ_SMS,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
//            android.Manifest.permission.VIBRATE,

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter("speed"));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speed= intent.getStringExtra("speed");
            downloadSpeed= intent.getStringExtra("downloadSpeed");
            uploadSpeed= intent.getStringExtra("uploadSpeed");
            totalDownload= intent.getStringExtra("totalDownload");
            totalDownloadWIFI= intent.getStringExtra("totalDownloadWIFI");
            totalDownloadData= intent.getStringExtra("totalDownloadData");
            totalUpload= intent.getStringExtra("totalUpload");
            totalUploadWIFI= intent.getStringExtra("totalUploadWIFI");
            totalUploadData= intent.getStringExtra("totalUploadData");
            speedText.setText(speed);
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        speedText = v.findViewById(R.id.textViewSpeed);
        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        linechart = (LineChartView) v.findViewById(R.id.line_chart_total);
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
//        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        adView = (AdView) v.findViewById(R.id.adView);
//        loadBanner();

        return v;
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void lineData(float a, int flag) {
        NumberFormat n = NumberFormat.getInstance();
        if (a == 0) {
            if (chartArray[flag][18] == 0) {
                a = (float) 0.1;
            }
        }
        float res;
        if (a / 1000 >= 1) {

            res = (int) a / 1000;

        } else {
            try {
                res = a / 1000;
                n.setMaximumFractionDigits(1);
                res = Float.parseFloat(n.format(res));
            } catch (Exception e) {
                res = a / 1000;
            }

        }
        float[] tempArry = new float[20];
        for (int i = 0; i < 20; i++) {
            if (i == 0) {
                if (res == 0) {
                    if (chartArray[flag][18] == 0) {
                        res = (float) 0.1;
                    }
                }
                tempArry[i] = res;
            } else {
                tempArry[i] = chartArray[flag][i - 1];

            }

        }
        for (int i = 0; i < 20; i++) {
            chartArray[flag][i] = tempArry[i];
        }

    }

    private void generatelineData() {

        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < 1; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();

            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);

            switch (i) {
                case 0:
                    // line.setStrokeWidth(4);
                    line.setColor(ChartUtils.COLOR_GREEN);
                    break;
                case 2:
                    line.setColor(ChartUtils.COLOR_ORANGE);
                    break;
                case 3:
                    line.setColor(ChartUtils.COLOR_RED);
                    break;

                default:
                    line.setColor(ChartUtils.COLOR_RED);
                    break;
            }

            line.setShape(ValueShape.CIRCLE);
            line.setCubic(false);
            line.setFilled(false);
            line.setHasLabels(false);
            line.setHasLabelsOnlyForSelected(false);
            line.setHasLines(true);
            line.setHasPoints(false);
            lines.add(line);
        }

        linedata = new LineChartData(lines);

        if (hasAxes) {
            Axis axisY = new Axis().setHasLines(true);
            Axis axisX = new Axis().setHasLines(true);
            // axisY.generateAxisFromRange(0,1000,5);

            axisY.setMaxLabelChars(5);
            // axisY.setAutoGenerated(true);
            if (hasAxesNames) {
                // axisX.setName("Axis X");

            }
            linedata.setAxisXBottom(axisX);
            linedata.setAxisYLeft(axisY);
            if (isAdded()) {
                axisY.setName(getResources().getString(R.string.project_id));
            }
        } else {
            // linedata.setAxisXBottom(null);
            linedata.setAxisYLeft(null);
        }

        linedata.setBaseValue(Integer.MAX_VALUE);
        linechart.setLineChartData(linedata);

    }


}
