package com.example.infinitedoublepager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public class InfinitePagerActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager pager = new ViewPager(this);
        setContentView(pager);

        final ViewGroup[] pageContainers = {
                new FrameLayout(this),
                new FrameLayout(this),
                new FrameLayout(this),
                new FrameLayout(this)
        };
        TextView tv = new TextView(this);
        tv.setText("#0");
        pageContainers[0].addView(tv);
        tv = new TextView(this);
        tv.setText("#1");
        pageContainers[1].addView(tv);
        tv = new TextView(this);
        tv.setText("#2");
        pageContainers[2].addView(tv);
        tv = new TextView(this);
        tv.setText("#3");
        pageContainers[3].addView(tv);
        pager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.d("###", "instantiateItem(container, " + position + ")");
                ViewGroup item = pageContainers[position % 4];
                container.addView(item);
                return item;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Log.d("###", "destroyItem(container, " + position + ")");
                container.removeView(pageContainers[position % 4]);
            }
        });
        pager.setCurrentItem((Integer.MAX_VALUE / 2) & 0xfffffffc);

        final TextView even = new PageContentView(this, 0xff6060ff, "even");
        Log.d("###", "even: " + even);
        final TextView odd = new PageContentView(this, 0xffff6060, "odd");
        Log.d("###", "odd: " + odd);
        pageContainers[0].addView(even);
        pageContainers[1].addView(odd);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            boolean startScrolling = true;
            boolean scrollToLeftPage;
            int lastPosition;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("###", "onPageScrolled(" + position + ", " + positionOffset + ", " + positionOffsetPixels + ")");
                if (positionOffsetPixels != 0) {
                    lastPosition = position;
                    if (startScrolling) {
                        int halfWidth = even.getMeasuredWidth() / 2;
                        scrollToLeftPage = positionOffsetPixels > halfWidth;
                        Log.d("###", "start scrolling: scroll to the " + (scrollToLeftPage ? "left" : "right") + " page");
                        if (scrollToLeftPage) {
                            int page = position % 4;
                            int rightContainer = (page + 2) % 4;
                            Log.d("###", "page: " + page + ", right page: " + rightContainer);
                            View rightPage = rightContainer % 2 == 0 ? even : odd;
                            pageContainers[rightContainer].removeView(rightPage);
                            pageContainers[page].addView(rightPage);
                        }
                        startScrolling = false;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("###", "onPageSelected(" + position + ")");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("###", "onPageScrollStateChanged(" + state + ")");
                switch (state) {
                    case SCROLL_STATE_DRAGGING:
                        break;
                    case SCROLL_STATE_IDLE:
                        Log.d("###", "stop scrolling (to the " + (scrollToLeftPage ? "left" : "right") + " page)");
                        if (!scrollToLeftPage) {
                            int previousContainer = lastPosition % 4;
                            int rightContainer = (previousContainer + 2) % 4;
                            Log.d("###", "previous page: " + previousContainer + ", now right page: " + rightContainer);
                            View previousPage = previousContainer % 2 == 0 ? even : odd;
                            pageContainers[previousContainer].removeView(previousPage);
                            pageContainers[rightContainer].addView(previousPage);
                        }
                        startScrolling = true;
                        break;
                }
            }
        });
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
            Log.d("###", getText() + "onMeasure()");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
