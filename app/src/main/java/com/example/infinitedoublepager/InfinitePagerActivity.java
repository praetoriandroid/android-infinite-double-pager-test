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

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public class InfinitePagerActivity extends AppCompatActivity {

    private static final int NOT_SET = -1;

    private ViewGroup[] pageContainers;
    private View primaryPage;
    private View secondaryPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager pager = new ViewPager(this);
        setContentView(pager);

        pageContainers = new ViewGroup[]{
                new FrameLayout(this),
                new FrameLayout(this),
                new FrameLayout(this),
                new FrameLayout(this)
        };
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
        final int initialPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 4;
        pager.setCurrentItem(initialPosition);

        primaryPage = new PageContentView(this, 0xff6060ff, "even");
        Log.d("###", "even: " + primaryPage);
        secondaryPage = new PageContentView(this, 0xffff6060, "odd");
        Log.d("###", "odd: " + secondaryPage);
        pageContainers[0].addView(primaryPage);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int primaryPosition = initialPosition;
            int secondaryPosition = NOT_SET;
            int selectedPosition = NOT_SET;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("###", "onPageScrolled(" + position + ", " + positionOffset + ", " + positionOffsetPixels + ")");
                if (positionOffsetPixels == 0) {
                    selectedPosition = position;
                    return;
                }

                int secondaryPageOffset = position == primaryPosition ? 1 : -1;
                int currentSecondaryPosition = primaryPosition + secondaryPageOffset;
                if (secondaryPosition != currentSecondaryPosition) {
                    if (secondaryPosition != NOT_SET) {
                        removePage(secondaryPosition, secondaryPage);
                    }
                    addPage(currentSecondaryPosition, secondaryPage);
                    secondaryPosition = currentSecondaryPosition;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("###", "onPageSelected(" + position + ")");
                selectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("###", "onPageScrollStateChanged(" + state + ")");
                switch (state) {
                    case SCROLL_STATE_IDLE:
//                        Log.d("###", "stop scrolling (to the " + (scrollToLeftPage ? "left" : "right") + " page)");
                        boolean pageSelectionChanged = primaryPosition != selectedPosition;
                        Log.d("###", "stop scrolling: page" + (pageSelectionChanged ? "" : " not") + " changed, primary: " + primaryPosition + "[" + primaryPage + "], secondary: " + secondaryPosition + "[" + secondaryPage + "], selected: " + selectedPosition);
                        if (pageSelectionChanged) {
                            swapPrimaryPage();
                            secondaryPosition = primaryPosition;
                            primaryPosition = selectedPosition;
                        }
                        if (secondaryPosition != NOT_SET) {
                            removePage(secondaryPosition, secondaryPage);
                        }
                        secondaryPosition = NOT_SET;
                        break;
                }
            }

            private void removePage(int position, View page) {
                Log.d("###", "removePage(" + position + ", " + page + ")");
                pageContainers[position % 4].removeView(page);
            }

            private void addPage(int position, View page) {
                Log.d("###", "addPage(" + position + ", " + page + ")");
                pageContainers[position % 4].addView(page);
            }

            private void swapPrimaryPage() {
                View page = primaryPage;
                primaryPage = secondaryPage;
                secondaryPage = page;
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
            Log.d("###", getText() + ": onMeasure()");
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public String toString() {
            return Integer.toHexString(System.identityHashCode(this));
        }
    }
}
