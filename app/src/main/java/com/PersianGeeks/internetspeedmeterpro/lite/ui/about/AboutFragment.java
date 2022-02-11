package com.PersianGeeks.internetspeedmeterpro.lite.ui.about;

import static com.PersianGeeks.internetspeedmeterpro.lite.statics.StaticHelper.v;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.PersianGeeks.internetspeedmeterpro.lite.R;
import com.PersianGeeks.internetspeedmeterpro.lite.base.BaseFragment;

public class AboutFragment extends BaseFragment {
    private TextView version;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_about, container, false);
        version = v.findViewById(R.id.version);
        version.setText("("+version.getText()+" "+appVersion()+")");
        return v;
    }
}
