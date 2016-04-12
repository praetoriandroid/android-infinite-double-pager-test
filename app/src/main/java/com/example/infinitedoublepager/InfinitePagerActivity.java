package com.example.infinitedoublepager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

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

    @SuppressLint("ViewConstructor")
    static class PageContentView extends TextView {
        public PageContentView(Context context, int backgroundColor, CharSequence text) {
            super(context);
            setTextSize(42f);
            setTextColor(0xffffffff);
            setTypeface(Typeface.DEFAULT_BOLD);
            setBackgroundColor(backgroundColor);
            setText(text);
            setGravity(Gravity.CENTER);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.d("###", getText() + ": onDraw()");
            super.onDraw(canvas);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Log.d("###", getText() + ": onMeasure()");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public String toString() {
            return Integer.toHexString(System.identityHashCode(this));
        }
    }
}
