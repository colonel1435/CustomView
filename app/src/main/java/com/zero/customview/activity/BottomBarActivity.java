package com.zero.customview.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.zero.customview.R;
import com.zero.customview.fragment.chart.TestFragment;
import com.zero.customview.view.bottombar.AnimBottomBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomBarActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.anim_bottom_bar)
    AnimBottomBar animBottomBar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private List<Fragment> mPages;

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

        mPages = new ArrayList<>();
        mPages.add(TestFragment.newInstance("0", "This is first page!"));
        mPages.add(TestFragment.newInstance("1", "This is second page!"));
        mPages.add(TestFragment.newInstance("2", "This is third page!"));
        mPages.add(TestFragment.newInstance("3", "This is fourth page!"));
        FragmentManager fm = getSupportFragmentManager();
        viewpager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return mPages.get(position);
            }

            @Override
            public int getCount() {
                return mPages.size();
            }
        });
        animBottomBar.setViewPager(viewpager);
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
