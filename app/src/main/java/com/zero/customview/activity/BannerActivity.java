package com.zero.customview.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zero.customview.R;
import com.zero.customview.adapter.banner.DepthPageTransformer;
import com.zero.customview.adapter.banner.ScalePageTransformer;
import com.zero.customview.adapter.banner.ZoomOutPageTransformer;
import com.zero.customview.fragment.banner.BannerFragment;
import com.zero.customview.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private Context mContext;
    private List<Fragment> mPages;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        mPages = new ArrayList<>();
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_bridge,
                getString(R.string.title_brige)));
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_night,
                getString(R.string.title_night)));
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_pig,
                getString(R.string.title_animal)));
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_ocean,
                getString(R.string.title_ocean)));
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_mountain,
                getString(R.string.title_mountain)));
        mPages.add(BannerFragment.newInstance(R.mipmap.ic_bg_road,
                getString(R.string.title_road)));
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mPages);
        viewpager.setAdapter(mPagerAdapter);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setPageTransformer(false,
                new ZoomOutPageTransformer());
//                new DepthPageTransformer());
//                new ScalePageTransformer());

    }

    private class PagerAdapter extends FragmentPagerAdapter {
        List<Fragment> pages;

        public List<Fragment> getPages() {
            return pages;
        }

        public PagerAdapter(FragmentManager fm, List<Fragment> pages) {
            super(fm);
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
            return pages.get(position);
        }

        @Override
        public int getCount() {
            return pages.size();
        }


    }
}
