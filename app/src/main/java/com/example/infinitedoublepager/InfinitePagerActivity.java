package com.example.infinitedoublepager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class InfinitePagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewPager pager = new ViewPager(this) {
            @Override
            public void addView(View child, int index, ViewGroup.LayoutParams params) {
                Log.d("###", "pager.addView(child, " + index + ", params)");
                super.addView(child, index, params);
            }
        };
        setContentView(pager);

        setupFragmentCarousel(pager);
    }

    private void setupViewCarousel(ViewPager pager) {
        View primaryPage = new PageContentView(this, 0xff6060ff, "even");
        Log.d("###", "even: " + primaryPage);
        View secondaryPage = new PageContentView(this, 0xffff6060, "odd");
        Log.d("###", "odd: " + secondaryPage);
        TwoPageCarousel carousel = new TwoPageCarousel(primaryPage, secondaryPage);
        carousel.setupPager(pager);
    }

    private void setupFragmentCarousel(ViewPager pager) {
        TwoFragmentCarousel carousel = new TwoFragmentCarousel(getSupportFragmentManager()) {
            @Override
            public Fragment createPrimaryItem() {
                PageFragment page = new PageFragment();
                page.setup(0xff6060ff, "even");
                return page;
            }

            @Override
            public Fragment createSecondaryItem() {
                PageFragment page = new PageFragment();
                page.setup(0xffff6060, "odd");
                return page;
            }
        };
        carousel.setupPager(pager);
    }

}
