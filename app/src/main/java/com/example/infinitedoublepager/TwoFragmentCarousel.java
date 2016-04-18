package com.example.infinitedoublepager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public abstract class TwoFragmentCarousel {

    private static final int NOT_SET = -1;
    private static final int INITIAL_POSITION = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % 4;

    private ViewGroup[] pageContainers;
    private View primaryPage;
    private View secondaryPage;
    private Fragment primaryPageFragment;
    private Fragment secondaryPageFragment;
    private FragmentManager fragmentManager;

    public TwoFragmentCarousel(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setupPager(ViewPager pager) {
        primaryPageFragment = createPrimaryItem();
        secondaryPageFragment = createSecondaryItem();

        Context context = pager.getContext();
        pageContainers = new ViewGroup[]{
                new FrameLayout(context),
                new FrameLayout(context),
                new FrameLayout(context),
                new FrameLayout(context)
        };

        pager.setAdapter(new CarouselPagerAdapter());
        pager.addOnPageChangeListener(new PageJuggler());
        pager.setCurrentItem(INITIAL_POSITION);
    }

    public abstract Fragment createPrimaryItem();

    public abstract Fragment createSecondaryItem();

    private class CarouselPagerAdapter extends PagerAdapter {

        private int itemCount;
        private FragmentTransaction currentTransaction;

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

            if (itemCount < 2) {
                addFragment(position);
            }

            itemCount++;
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d("###", "destroyItem(container, " + position + "): itemCount=" + itemCount);
            container.removeView(pageContainers[position % 4]);
            itemCount--;
            if (itemCount < 2) {
                Log.d("###", "destroy secondary page fragment");
                destroyPage("secondary", secondaryPageFragment);
            }
            if (itemCount < 1) {
                Log.d("###", "destroy primary page fragment");
                destroyPage("primary", primaryPageFragment);
            }
        }

        private void destroyPage(String label, Fragment pageFragment) {
            if (currentTransaction == null) {
                currentTransaction = fragmentManager.beginTransaction();
            }
            Log.d("###", "Detaching item #" + label + ": f=" + pageFragment
                    + " v=" + (pageFragment).getView());
            currentTransaction.detach(pageFragment);
        }

        private void addFragment(int position) {
            if (itemCount >= 2) {
                throw new IllegalStateException();
            }

            position %= 4;
            boolean primaryItem = position == 0;
            if (currentTransaction == null) {
                currentTransaction = fragmentManager.beginTransaction();
            }

            String label = primaryItem ? "primary" : "secondary";
            String name = "carousel:page#" + label;
            Fragment fragment = fragmentManager.findFragmentByTag(name);
            if (fragment != null) {
                Log.d("###", "Attaching item #" + label + ": f=" + fragment);
                currentTransaction.attach(fragment);
            } else {
                fragment = primaryItem ? primaryPageFragment : secondaryPageFragment;
                Log.d("###", "Adding item #" + label + ": f=" + fragment);
                int containerId = primaryItem ? R.id.carousel_first_container : R.id.carousel_second_container;
                pageContainers[position].setId(containerId);
                currentTransaction.add(containerId, fragment, name);
            }
            if (!primaryItem) {
                secondContainer = position;
            }
        }

        int secondContainer;

        @Override
        public void finishUpdate(ViewGroup container) {
            Log.d("###", "finishUpdate(): itemCount=" + itemCount);
            if (currentTransaction != null) {
                currentTransaction.commitAllowingStateLoss();
                currentTransaction = null;
                fragmentManager.executePendingTransactions();
                if (itemCount >= 2) {
                    primaryPage = pageContainers[0].getChildAt(0);
                    secondaryPage = pageContainers[secondContainer].getChildAt(0);
                    pageContainers[secondContainer].removeView(secondaryPage);
                }
            }
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
                setActiveFragment(position);
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
            setActiveFragment(position);
            selectedPosition = position;
        }

        private void setActiveFragment(int position) {
            if (primaryPosition != position) {
                primaryPageFragment.setMenuVisibility(false);
                primaryPageFragment.setUserVisibleHint(false);
                secondaryPageFragment.setMenuVisibility(true);
                secondaryPageFragment.setUserVisibleHint(true);
            }
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
