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
import com.zero.customview.view.danmaku.DanmakuMsg;
import com.zero.customview.view.vedio.BalloonRelativeLayout;
import com.zero.customview.view.vedio.CustomVideoView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.schedulers.Schedulers;
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
    private boolean isContinue = true;
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
        initData();
    }

    private void initData() {
        Observable.create(new ObservableOnSubscribe<DanmakuMsg>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<DanmakuMsg> emitter) throws Exception {
                while (isContinue) {
                    int time = new Random().nextInt(500);
                    int usrId = new Random().nextInt(3);
                    int usrType = new Random().nextInt(2);
                    int randId = new Random().nextInt(4);
                    int iconId;
                    switch (randId) {
                        case 0:
                            iconId = R.mipmap.ic_header_beast;
                            break;
                        case 1:
                            iconId = R.mipmap.ic_header_monky;
                            break;
                        case 2:
                            iconId = R.mipmap.ic_header_pig;
                            break;
                        case 3:
                        default:
                            iconId = R.mipmap.ic_default_header;
                            break;
                    }
                    String content = "hello," + time;
                    emitter.onNext(new DanmakuMsg(usrId, usrType, usrId, iconId, content));
                    try {
                        Thread.sleep(time);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
          .subscribe(new Consumer<DanmakuMsg>() {
              @Override
              public void accept(DanmakuMsg danmakuMsg) throws Exception {
                danmakuManager.addDanmu(danmakuMsg);
              }
          });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isContinue = false;
        danmakuManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isContinue = true;
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
