package com.example.infinitedoublepager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public class TwoPageCarousel {

    private static final int NOT_SET = -1;
    private static final int INITIAL_POSITION = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 4;

    private ViewGroup[] pageContainers;
    private View primaryPage;
    private View secondaryPage;

    public TwoPageCarousel(View primaryPage, View secondaryPage) {
        this.primaryPage = primaryPage;
        this.secondaryPage = secondaryPage;
    }

    public void setupPager(ViewPager pager) {
        Context context = primaryPage.getContext();
        pageContainers = new ViewGroup[]{
                new FrameLayout(context),
                new FrameLayout(context),
                new FrameLayout(context),
                new FrameLayout(context)
        };
        pageContainers[0].addView(primaryPage);

        pager.setAdapter(new CarouselPagerAdapter());
        pager.addOnPageChangeListener(new PageJuggler());
        pager.setCurrentItem(INITIAL_POSITION);
    }

    private class CarouselPagerAdapter extends PagerAdapter {
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
    }

    private class PageJuggler implements ViewPager.OnPageChangeListener {
        int primaryPosition = INITIAL_POSITION;
        int secondaryPosition = NOT_SET;
        int selectedPosition = NOT_SET;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (positionOffsetPixels == 0) {
                Log.d("###", "onPageScrolled(" + position + ", " + positionOffset + ", " + positionOffsetPixels + "): select position: " + position);
                selectedPosition = position;
                return;
            }

            int secondaryPageOffset = position == primaryPosition ? 1 : -1;
            int currentSecondaryPosition = primaryPosition + secondaryPageOffset;
            if (secondaryPosition != currentSecondaryPosition) {
                Log.d("###", "onPageScrolled(" + position + ", " + positionOffset + ", " + positionOffsetPixels + "): secondary position changed: " + secondaryPosition + " -> " + currentSecondaryPosition);
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
            if (pageContainers[position % 4].getChildCount() == 0) {
                throw new IllegalStateException();
            }
            if (pageContainers[position % 4].getChildAt(0) != page) {
                throw new IllegalStateException();
            }
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
    }
}
