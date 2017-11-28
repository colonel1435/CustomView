package com.zero.customview.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.zero.customview.R;
import com.zero.customview.adapter.recyclerview.MyGridDividerItemDecoration;
import com.zero.customview.adapter.recyclerview.RecyclerItemClickListener;
import com.zero.customview.adapter.recyclerview.RecyclerViewCommonAdapter;
import com.zero.customview.adapter.recyclerview.RecyclerViewHolder;
import com.zero.customview.view.RoundImageDrawable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ITEM_PROGRESS_BAR = 0;
    private static final int ITEM_DRAW_VIEW = 1;
    private static final int ITEM_VIRTUAL_NUM_KEYBOARD = 2;
    private static final int ITEM_SHAPE_VIEW = 3;
    private static final int ITEM_DOWNLOAD = 4;
    private static final int ITEM_CANVAS_VIEW = 5;
    private static final int ITEM_ANIM_VIEW = 6;
    private static final int ITEM_DRAG_VIEW = 7;
    private static final int ITEM_PHOTO_VIEW = 8;
    private static final int ITEM_VIDEO_PLAYER = 9;
    private static final int ITEM_CHART = 10;
    private static final int ITEM_OPENGL_3D = 11;
    private static final int ITEM_CIRCLE_MENU = 12;
    private static final int ITEM_ANIM_BOTTOM = 13;
    private static final int ITEM_COUPON_VIEW = 14;
    private static final int ITEM_LIKE_VIEW = 15;
    private static final int ITEM_RULER_VIEW = 16;
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
        mDatas.add(getString(R.string.anim_view));
        mDatas.add(getString(R.string.drag_view));
        mDatas.add(getString(R.string.photo_view));
        mDatas.add(getString(R.string.vedio_player));
        mDatas.add(getString(R.string.chart_view));
        mDatas.add(getString(R.string.opengl_3d));
        mDatas.add(getString(R.string.circle_menu));
        mDatas.add(getString(R.string.anim_bottom));
        mDatas.add(getString(R.string.coupon_view));
        mDatas.add(getString(R.string.like_view));
        mDatas.add(getString(R.string.ruler_view));

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
            case ITEM_ANIM_VIEW:
                startActivity(new Intent(MainActivity.this, AnimActivity.class));
                break;
            case ITEM_DRAG_VIEW:
                startActivity(new Intent(MainActivity.this, DragViewActivity.class));
                break;
            case ITEM_PHOTO_VIEW:
                startActivity(new Intent(MainActivity.this, PhotoViewActivity.class));
                break;
            case ITEM_VIDEO_PLAYER:
                startActivity(new Intent(mContext, VideoPlayerActivity.class));
                break;
            case ITEM_CHART:
                startActivity(new Intent(mContext, ChartActivity.class));
                break;
            case ITEM_OPENGL_3D:
                startActivity(new Intent(mContext, OpenGLActivity.class));
                break;
            case ITEM_CIRCLE_MENU:
                startActivity(new Intent(mContext, CircleMenuActivity.class));
                break;
            case ITEM_ANIM_BOTTOM:
                startActivity(new Intent(mContext, BottomBarActivity.class));
                break;
            case ITEM_COUPON_VIEW:
                startActivity(new Intent(mContext, CouponActivity.class));
                break;
            case ITEM_LIKE_VIEW:
                startActivity(new Intent(mContext, LikeViewActivity.class));
                break;
            case ITEM_RULER_VIEW:
                startActivity(new Intent(mContext, RulerActivity.class));
                break;
            default:
                break;
        }
    }

    public void onDebug(View view) {
        startActivity(new Intent(this, ProgressBarActivity.class));
//        toNormalKeyBoard(view);
    }

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
