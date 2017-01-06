package com.zero.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zero.customview.view.HorizontalProgressBar;
import com.zero.customview.view.MultilevelProgressBar;
import com.zero.customview.view.RoundImageDrawable;
import com.zero.customview.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext = null;
    private ProgressBar mProgressBar;
    private PopupWindow mPopupWindows;
    private FrameLayout mProgressLayout;
    private LinearLayout mSpeakLayout;
    private LinearLayout mSpeakErrLayout;
    private TextView mMsgText;
    private ImageView mMsgImage;
    private ImageView mTest;
    private HorizontalProgressBar mHorizontalProgress;
    private RoundProgressBar mRoundProgress;
    private MultilevelProgressBar mMultiLevelProgress;
    public static final int HORIZONTAL_PROGRESSBAR_UPDATE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mTest = (ImageView) findViewById(R.id.iv_test);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mTest.setImageDrawable(new RoundImageDrawable(bmp));
        mHorizontalProgress = (HorizontalProgressBar) findViewById(R.id.horizontal_progress_bar);
        mHorizontalProgress.setMax(100);
        mRoundProgress = (RoundProgressBar) findViewById(R.id.round_progress_bar);
        mRoundProgress.setMax(100);
        mMultiLevelProgress = (MultilevelProgressBar) findViewById(R.id.multilevel_progress_bar);
        mMultiLevelProgress.setIndeterminate(true);
        mHandler.sendEmptyMessage(HORIZONTAL_PROGRESSBAR_UPDATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showSpeakDialog(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void onDebug(View view) {
        showSpeakDialog(view);
    }

    public void dardBackground(Activity context, float alpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = alpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }
    public void showSpeakDialog(View view) {
        View popupView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_voice, null);
        mProgressLayout = (FrameLayout)popupView.findViewById(R.id.fl_progressbar);
        mSpeakLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_image);
        mSpeakErrLayout = (LinearLayout)popupView.findViewById(R.id.ll_speak_err);
        mMsgText = (TextView)popupView.findViewById(R.id.tv_msg_text);
        mMsgImage = (ImageView)popupView.findViewById(R.id.iv_msg_image);
        mPopupWindows = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindows.setTouchable(true);
        mPopupWindows.setFocusable(true);
        mPopupWindows.setOutsideTouchable(true);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable());
        mPopupWindows.setAnimationStyle(R.style.popwindow_voice_anim);
        mPopupWindows.getBackground().setAlpha(50);
        dardBackground(this, 0.4f);
        mPopupWindows.showAtLocation(view, Gravity.CENTER, 0 ,0);
        mPopupWindows.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dardBackground(MainActivity.this, 1.0f);
            }
        });
    }
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
            switch (id) {
                case HORIZONTAL_PROGRESSBAR_UPDATE:
                    int progress = mHorizontalProgress.getProgress();
                    int rProgress = mRoundProgress.getProgress();
                    mRoundProgress.setProgress(++rProgress);
                    mHorizontalProgress.setProgress(++progress);
                    if (progress >= 100) {
                        mHandler.removeMessages(HORIZONTAL_PROGRESSBAR_UPDATE);
                    }
                    mHandler.sendEmptyMessageDelayed(HORIZONTAL_PROGRESSBAR_UPDATE, 100);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
