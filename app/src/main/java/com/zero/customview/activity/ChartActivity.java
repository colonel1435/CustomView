package com.zero.customview.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zero.customview.R;
import com.zero.customview.adapter.FragmentPageAdapter;
import com.zero.customview.fragment.chart.BarFragment;
import com.zero.customview.fragment.chart.LineFragment;
import com.zero.customview.fragment.chart.RadarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tl_chart)
    TabLayout mTablayout;
    @BindView(R.id.vp_chart)
    ViewPager mViewpager;

    private List<String> mTitles;
    private List<Fragment> mPages;
    private FragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        mTitles = new ArrayList<>();
        mTitles.add(getString(R.string.chart_radar));
        mTitles.add(getString(R.string.chart_line));
        mTitles.add(getString(R.string.chart_bar));

        mPages = new ArrayList<>();
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.chart_view));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadarFragment radarFragment = RadarFragment.newInstance("0", "radar");
        mPages.add(radarFragment);

        BarFragment barFragment = BarFragment.newInstance("1", "bar");
        mPages.add(barFragment);

        LineFragment lineFragment = LineFragment.newInstance("2", "line");
        mPages.add(lineFragment);

        FragmentManager fm = getSupportFragmentManager();
        mPagerAdapter = new FragmentPageAdapter(fm, mPages, mTitles);
        mViewpager.setAdapter(mPagerAdapter);
        mViewpager.addOnPageChangeListener(mChangeListener);
        mTablayout.setupWithViewPager(mViewpager);
        mViewpager.setCurrentItem(0);
    }

    private ViewPager.OnPageChangeListener mChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewpager.setCurrentItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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
