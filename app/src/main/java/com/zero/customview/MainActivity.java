package com.zero.customview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.zero.customview.adapter.MyGridDividerItemDecoration;
import com.zero.customview.adapter.RecyclerItemClickListener;
import com.zero.customview.adapter.RecyclerViewCommonAdapter;
import com.zero.customview.adapter.RecyclerViewHolder;
import com.zero.customview.view.HorizontalProgressBar;
import com.zero.customview.view.MultilevelProgressBar;
import com.zero.customview.view.RoundImageDrawable;
import com.zero.customview.view.RoundProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ITEM_PROGRESS_BAR = 0;
    private static final int ITEM_DRAW_VIEW = 1;
    private static final int ITEM_VIRTUAL_NUM_KEYBOARD = 2;
    private static final int ITEM_SHAPE_VIEW = 3;
    private static final int ITEM_DOWNLOAD = 4;
    private static final int ITEM_CANVAS_VIEW = 5;
    private Context mContext = null;
    private Toolbar toolbar;
    private ImageView mTest;
    private List<String> mDatas;
    private RecyclerView mRecyclerView;
    private RecyclerViewCommonAdapter mRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initData();
        initView();

        Logger.init("wumin").methodCount(0);
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add(getString(R.string.progress_bar_item));
        mDatas.add(getString(R.string.draw_view_item));
        mDatas.add(getString(R.string.virtual_num_keyboard_item));
        mDatas.add(getString(R.string.shape_view_item));
        mDatas.add(getString(R.string.task_download));
        mDatas.add(getString(R.string.canvas_view));
    }
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTest = (ImageView) findViewById(R.id.iv_header);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mTest.setImageDrawable(new RoundImageDrawable(bmp, 200));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_button_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new MyGridDividerItemDecoration(mContext, 2, Color.WHITE));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mContext, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onItemClicked(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        mRecyclerAdapter = new RecyclerViewCommonAdapter(mContext, R.layout.activity_main_gridview_item, mDatas) {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                holder.itemView.setTag(position);
            }

            @Override
            public void convert(RecyclerViewHolder holder, Object o) {
                holder.setText(R.id.tv_item, (String)o);
            }
        };
        mRecyclerView.setAdapter(mRecyclerAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void onItemClicked(int position) {
        switch (position) {
            case ITEM_PROGRESS_BAR:
                startActivity(new Intent(MainActivity.this, ProgressBarActivity.class));
                break;
            case ITEM_DRAW_VIEW:
                startActivity(new Intent(MainActivity.this, DrawViewActivity.class));
                break;
            case ITEM_VIRTUAL_NUM_KEYBOARD:
                startActivity(new Intent(MainActivity.this, NormalKeyBoardActivity.class));
                break;
            case ITEM_SHAPE_VIEW:
                startActivity(new Intent(MainActivity.this, ShapeActivity.class));
                break;
            case ITEM_DOWNLOAD:
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
                break;
            case ITEM_CANVAS_VIEW:
                startActivity(new Intent(MainActivity.this, CanvasViewActivity.class));
                break;
            default:
                break;
        }
    }

    public void onDebug(View view) {
        startActivity(new Intent(this, ProgressBarActivity.class));
//        toNormalKeyBoard(view);
    }

    public void toNormalKeyBoard(View view) {
        startActivity(new Intent(this, NormalKeyBoardActivity.class));
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int id = msg.what;
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
