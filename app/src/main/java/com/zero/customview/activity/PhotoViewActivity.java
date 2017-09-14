package com.zero.customview.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zero.customview.R;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotoViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_fade_photo)
    ImageView ivFadePhoto;
    @BindView(R.id.iv_drag_photo)
    ImageView ivDragPhoto;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        ButterKnife.bind(this);

        mContext = this;

        initView();
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.title_activity_photo_view));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void startFadePhoto(View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        Intent intent = new Intent(mContext, FadePhotoActivity.class);
        intent.putExtra("photo", "");
        intent.putExtra("locationX", location[0]);
        intent.putExtra("locationY", location[0]);
        intent.putExtra("width", 0);
        intent.putExtra("height", 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void startDragPhoto(View view) {
        Intent intent = new Intent(mContext, DragPhotoActivity.class);
        int location[] = new int[2];

        view.getLocationOnScreen(location);
        intent.putExtra("locationX", location[0]);
        intent.putExtra("locationY", location[1]);
        intent.putExtra("height", view.getHeight());
        intent.putExtra("width", view.getWidth());

        startActivity(intent);
        overridePendingTransition(0,0);
    }
    @OnClick({R.id.iv_fade_photo, R.id.iv_drag_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_fade_photo:
                startFadePhoto(view);
                break;
            case R.id.iv_drag_photo:
                startDragPhoto(view);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
