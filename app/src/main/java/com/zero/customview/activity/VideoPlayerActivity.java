package com.zero.customview.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zero.customview.R;
import com.zero.customview.utils.SystemUtils;
import com.zero.customview.view.DanmukuPopupwindow;
import com.zero.customview.view.danmaku.DanmakuManager;
import com.zero.customview.view.danmaku.DanmakuMsg;
import com.zero.customview.view.vedio.BalloonRelativeLayout;
import com.zero.customview.view.vedio.CustomVideoView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import master.flame.danmaku.ui.widget.DanmakuView;

public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        EmojiconGridFragment.OnEmojiconClickedListener {
    @BindView(R.id.videoView)
    CustomVideoView videoView;
    @BindView(R.id.balloonRelativeLayout)
    BalloonRelativeLayout rlBalloon;
    @BindView(R.id.danmaku_view)
    DanmakuView danmakuView;
    @BindView(R.id.iv_vedio_music)
    ImageView ivVedioMusic;
    @BindView(R.id.iv_vedio_love)
    ImageView ivVedioLove;
    @BindView(R.id.iv_vedio_face)
    ImageView ivVedioFace;
    @BindView(R.id.iv_vedio_msg)
    ImageView ivVedioMsg;
    @BindView(R.id.iv_vedio_gift)
    ImageView ivVedioGift;
    @BindView(R.id.ll_bottom_button)
    LinearLayout llBottomButton;
    @BindView(R.id.iv_vedio_close)
    ImageView ivVedioClose;

    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    @BindView(R.id.et_danmuku_content)
    EditText etDanmukuContent;
    @BindView(R.id.iv_danmuku_face)
    ImageView ivDanmukuFace;
    @BindView(R.id.tv_danmuku_send)
    TextView tvDanmukuSend;
    @BindView(R.id.fl_danmuku_face)
    FrameLayout flDanmukuFace;
    @BindView(R.id.ll_danmuku_input)
    LinearLayout llDanmukuInput;
    private Context mContext;
    private DanmakuManager danmakuManager;
    private boolean isContinue = true;
    private int TIME = 100;
    private int emotionHeight;
    private final LayoutTransition transitioner = new LayoutTransition();

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

        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY",
                SystemUtils.getScreenHeight((Activity) mContext), emotionHeight).
                setDuration(transitioner.getDuration(LayoutTransition.APPEARING));
        transitioner.setAnimator(LayoutTransition.APPEARING, animIn);
        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "translationY",
                emotionHeight,
                SystemUtils.getScreenHeight((Activity)mContext)).
                setDuration(transitioner.getDuration(LayoutTransition.DISAPPEARING));
        transitioner.setAnimator(LayoutTransition.DISAPPEARING, animOut);
        llDanmukuInput.setLayoutTransition(transitioner);
    }

    private void initData() {
        Observable.create(new ObservableOnSubscribe<DanmakuMsg>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<DanmakuMsg> emitter) throws Exception {
                while (isContinue) {
                    int time = new Random().nextInt(1000);
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
                        Thread.sleep(1000);
                    } catch (Exception e) {
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

    private void startSong() {

    }

    private void startLike(View view) {
        int[] position = new int[2];
        view.getLocationInWindow(position);
        Log.d(TAG, "startLike: getLocationInWindow:" + position[0] + "," + position[1]);

        rlBalloon.addHeart(new PointF(position[0], position[1]));
    }

    private void startSend() {
        SystemUtils.hideSoftInput(etDanmukuContent);
        llDanmukuInput.setVisibility(View.GONE);
        llBottomButton.setVisibility(View.VISIBLE);
        String content = etDanmukuContent.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            int usrId = 1;
            int usrType = 1;
            int iconId = R.mipmap.ic_header_admin_96;
            danmakuManager.addDanmu(new DanmakuMsg(usrId, usrType, usrId, iconId, content));

            etDanmukuContent.setText("");
        }
    }

    private void startFace() {
        EmojiconsFragment emojFragemnt = EmojiconsFragment.newInstance(false);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.add(R.id.fl_danmuku_face, emojFragemnt).commit();

        SystemUtils.hideSoftInput(etDanmukuContent);
        emotionHeight = SystemUtils.getKeyboardHeight((Activity) mContext);
        flDanmukuFace.getLayoutParams().height = emotionHeight;
        llDanmukuInput.setVisibility(View.VISIBLE);
        llBottomButton.setVisibility(View.INVISIBLE);
    }

    private void startMessage(View view) {
        llDanmukuInput.setVisibility(View.VISIBLE);
        llBottomButton.setVisibility(View.INVISIBLE);
        SystemUtils.showKeyBoard(etDanmukuContent);
    }

    private void startGift() {

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
        if (llDanmukuInput.getVisibility() == View.VISIBLE) {
            llDanmukuInput.setVisibility(View.INVISIBLE);
        }
        if (llBottomButton.getVisibility() != View.VISIBLE) {
            llBottomButton.setVisibility(View.VISIBLE);
        }
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

    @OnClick({R.id.iv_vedio_music, R.id.iv_vedio_love, R.id.iv_vedio_face,
            R.id.iv_vedio_msg, R.id.iv_vedio_gift, R.id.iv_vedio_close,
            R.id.iv_danmuku_face, R.id.tv_danmuku_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_vedio_music:
                startSong();
                break;
            case R.id.iv_vedio_love:
                startLike(view);
                break;
            case R.id.iv_vedio_face:
                startFace();
                break;
            case R.id.iv_vedio_msg:
                startMessage(view);
                break;
            case R.id.iv_vedio_gift:
                startGift();
                break;
            case R.id.iv_vedio_close:
                finish();
                break;
            case R.id.iv_danmuku_face:
                startFace();
                break;
            case R.id.tv_danmuku_send:
                startSend();
                break;
            default:
                break;
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(etDanmukuContent, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(etDanmukuContent);
    }
}
