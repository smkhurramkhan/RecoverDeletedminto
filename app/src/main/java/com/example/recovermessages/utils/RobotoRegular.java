package com.example.recovermessages.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class RobotoRegular extends TextView {
    public RobotoRegular(Context context) {
        super(context);
        setFont();
    }

    public RobotoRegular(Context context, AttributeSet set) {
        super(context, set);
        setFont();
    }

    public RobotoRegular(Context context, AttributeSet set, int defaultStyle) {
        super(context, set, defaultStyle);
        setFont();
    }

    public void setFont() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf"));
    }
}
