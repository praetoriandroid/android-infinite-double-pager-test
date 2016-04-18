package com.example.infinitedoublepager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageFragment extends Fragment {

    private int backgroundColor;

    private String text;

    public void setup(int backgroundColor, String text) {
        this.backgroundColor = backgroundColor;
        this.text = text;

        PageContentView view = (PageContentView) getView();
        if (view != null) {
            view.setup(backgroundColor, text);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("###", "onCreateView()");
        return new PageContentView(getActivity(), backgroundColor, text);
    }
}
