package com.example.infinitedoublepager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class InfinitePagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager pager = new ViewPager(this);
        setContentView(pager);

        View primaryPage = new PageContentView(this, 0xff6060ff, "even");
        Log.d("###", "even: " + primaryPage);
        View secondaryPage = new PageContentView(this, 0xffff6060, "odd");
        Log.d("###", "odd: " + secondaryPage);
        TwoPageCarousel carousel = new TwoPageCarousel(primaryPage, secondaryPage);
        carousel.setupPager(pager);
    }

}
