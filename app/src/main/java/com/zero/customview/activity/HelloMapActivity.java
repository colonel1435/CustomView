package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.zero.customview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelloMapActivity extends AppCompatActivity {

    @BindView(R.id.map)
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_map);
        ButterKnife.bind(this);

        map.onCreate(savedInstanceState);
    }

    private void initView() {
        AMap aMap = null;
        if (aMap == null) {
            aMap = map.getMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }
}
