package com.zero.customview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CanvasViewActivity extends AppCompatActivity {

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_view);
//        setContentView(new ClockView(this));
        mContext = this;
    }

}
