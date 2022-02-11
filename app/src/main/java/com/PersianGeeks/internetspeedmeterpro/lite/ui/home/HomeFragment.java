package com.PersianGeeks.internetspeedmeterpro.lite.ui.home;

import static com.PersianGeeks.internetspeedmeterpro.lite.statics.StaticHelper.v;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.PersianGeeks.internetspeedmeterpro.lite.statics.Data;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends BaseFragment {
    boolean isStarted = true;
    View view;
    TextView totalSpeed;
    TextView receive;
    TextView send;

    private AdView adView;
    private TextView speedText = null;
    private AdView mAdView;

    long exRX, exTX;
    long nowRX, nowTX;
    double rxBPS;
    double txBPS;
    public double drx = 10, dtx = 10, dall;
    public String speed = "0 Kb/s", down = "Download 0 Kb/s",
            up = "Upload 0 Kb/s";
    int flagCount = 0;
    double brx = 0, btx = 0;
    float[][] chartArray = new float[3][21];

    //

    TextView wr, ws, mr, ms, tr, ts;
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabelsOutside = false;
    //
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
    public void onStart() {
        if (isStarted) {
            wr = (TextView) v.findViewById(R.id.wr);
            ws = (TextView) v.findViewById(R.id.ws);
            mr = (TextView) v.findViewById(R.id.dr);
            ms = (TextView) v.findViewById(R.id.ds);
            tr = (TextView) v.findViewById(R.id.tr);
            ts = (TextView) v.findViewById(R.id.ts);
            totalSpeed = (TextView) v.findViewById(R.id.total_speed);
            receive = (TextView) v.findViewById(R.id.receive);
            send = (TextView) v.findViewById(R.id.send);
            chart = (PieChartView) v.findViewById(R.id.pieChartView1);
            linechart = (LineChartView) v.findViewById(R.id.line_chart_total);



            isStarted = false;
        }

//        if (showAds()) {
//            runAds(adView);
//        }
        super.onStart();
    }


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
//            totalDownload= intent.getStringExtra("totalDownload");
//            totalDownloadWIFI= intent.getStringExtra("totalDownloadWIFI");
//            totalDownloadData= intent.getStringExtra("totalDownloadData");
//            totalUpload= intent.getStringExtra("totalUpload");
//            totalUploadWIFI= intent.getStringExtra("totalUploadWIFI");
            rxBPS= intent.getDoubleExtra("rx",0);
            txBPS= intent.getDoubleExtra("tx",0);

            setData(speed, downloadSpeed, uploadSpeed, txBPS + rxBPS, rxBPS, txBPS);

            showResult();

            if (loopFirst) {
                lineData((float) 0, 0);
                loopFirst = false;
            } else {
                lineData((float) dall, 0);
            }


            wr.setText("R "
                    + setTrance(Data.wifirec, TrafficStats.getTotalRxBytes() - TrafficStats.getMobileRxBytes()));
            ws.setText("S "
                    + setTrance(Data.wifisend, TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes()));
            mr.setText("R "
                    + setTrance(TrafficStats.getMobileRxBytes(), Data.mobilerec));
            ms.setText("S "
                    + setTrance(TrafficStats.getMobileTxBytes(), Data.mobilesend));
            tr.setText("R "
                    + setTrance(TrafficStats.getTotalRxBytes(), Data.totalrec));
            ts.setText("S "
                    + setTrance(TrafficStats.getTotalTxBytes(), Data.totalsend));

            generatelineData();
            generateData(false);

            generateValues();


        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        linechart = (LineChartView) v.findViewById(R.id.line_chart_total);
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (showAds()) {
            mAdView.loadAd(adRequest);
        }
        generatelineData();
//        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        adView = (AdView) v.findViewById(R.id.adView);
//        loadBanner();

        return v;
    }



    private String setTrance(double a, double b) {
        double c = Math.abs(a - b);

        return calculateData2((int) c);

    }

    private String calculateData2(int a) {
        String res;

        if (a / 1024 >= 1) {
            if ((a / 1000) / 1048576 >= 1) {
                res = Math.abs((int) (a / 1024) / 1048576) + " GB";
            } else {
                if ((a / 1024) / 1024 >= 1) {
                    res = Math.abs((int) (a / 1024) / 1024) + " MB";
                } else {
                    res = Math.abs((int) a / 1024) + " KB";
                }
            }

        } else {
            res = Math.abs((int) a) + " Byte";
        }
        return res;

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

    private boolean chartFlag = true;

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
                axisY.setName(getResources().getString(R.string.axisY));
            }
        } else {
            // linedata.setAxisXBottom(null);
            linedata.setAxisYLeft(null);
        }

        linedata.setBaseValue(Integer.MAX_VALUE);
        linechart.setLineChartData(linedata);

    }

    private void generateValues() {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = chartArray[i][j];
            }
        }
    }

    private void generateData(boolean flag) {
        double a;
        double b;
        a = dtx;
        b = drx;
        if (flag) {
            a = 1;
            b = 1;
        }

        if (a == 0 && b == 0) {
            a = 1;
            b = 1;
        }
        List<SliceValue> values = new ArrayList<SliceValue>();

        SliceValue sliceValue = new SliceValue((float) a, ChartUtils.COLOR_RED);
        values.add(sliceValue);
        SliceValue sliceValue2 = new SliceValue((float) b, ChartUtils.COLOR_ORANGE);
        values.add(sliceValue2);

        data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasLabelsOnlyForSelected(false);
        data.setHasLabelsOutside(false);
        data.setHasCenterCircle(true);

        chart.setPieChartData(data);
    }

    boolean loopFirst = true;





    public void setData(String a, String b, String c, double all,
                        double rx, double tx) {
        speed = a;
        down = b;
        up = c;
        dall = all;
        drx = rx;
        dtx = tx;

    }

    public void showResult() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (isAdded()) {
                    totalSpeed.setText(speed);
                    receive.setText(getResources().getString(R.string.download) + ": "
                            + down);
                    send.setText(getResources().getString(R.string.upload) + ": " + up);
                }
            }
        };
        handler.sendEmptyMessage(1);
    }


}
