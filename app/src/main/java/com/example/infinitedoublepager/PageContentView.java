package com.example.infinitedoublepager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
class PageContentView extends TextView {
    public PageContentView(Context context, int backgroundColor, CharSequence text) {
        super(context);
        setTextSize(42f);
        setTextColor(0xffffffff);
        setTypeface(Typeface.DEFAULT_BOLD);
        setGravity(Gravity.CENTER);
        setup(backgroundColor, text);
    }

    public void setup(int backgroundColor, CharSequence text) {
        setBackgroundColor(backgroundColor);
        setText(text);
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
