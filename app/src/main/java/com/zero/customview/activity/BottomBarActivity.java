package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zero.customview.R;
import com.zero.customview.view.bottombar.AnimBottomBar;
import com.zero.customview.view.bottombar.BottomItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomBarActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_bottom_bar)
    ViewPager vpBottomBar;
    @BindView(R.id.anim_bottom_bar)
    AnimBottomBar animBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_bar);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.anim_bottom));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        animBottomBar.addItem(new BottomItem("Home", R.mipmap.ic_header_beast))
                    .addItem(new BottomItem("Friend", R.mipmap.ic_header_monky))
                    .addItem(new BottomItem("Chat", R.mipmap.ic_header_pig))
                    .addItem(new BottomItem("About", R.mipmap.ic_default_header))
                    .build();

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
