package com.zero.customview.activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.zero.customview.R;
import com.zero.customview.view.danmaku.DanmakuManager;
import com.zero.customview.view.vedio.BalloonRelativeLayout;
import com.zero.customview.view.vedio.CustomVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {
    @BindView(R.id.videoView)
    CustomVideoView videoView;
    @BindView(R.id.balloonRelativeLayout)
    BalloonRelativeLayout rlBalloon;
    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;

    private Context mContext;
    private DanmakuManager danmakuManager;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    private int TIME = 100;
    Handler mHandler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try {
                mHandler.postDelayed(this, TIME);
                rlBalloon.addBalloon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);

        mContext = this;
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mqr));
        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);

        danmakuManager = new DanmakuManager(mContext);
        danmakuManager.setDanmakuView(danmakuView);

    }

    @Override
    protected void onPause() {
        super.onPause();
        danmakuManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        danmakuManager.resume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        danmakuManager.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        danmakuManager.destroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start();
        mHandler.postDelayed(runnable, TIME);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        videoView.start();
    }

}
