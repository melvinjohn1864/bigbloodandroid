package com.bigb.bigblood.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.bigb.bigblood.R;
import com.bigb.bigblood.components.Utilities.Font;

/**
 * Created by Melvin John
 */

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context, attrs);
    }

    private void setFont(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.CustomView);
            int font = array.getInt(R.styleable.CustomView_font_name, -1);
            setCustomTypeface(context, font);
            array.recycle();
        }
    }

    public void setCustomTypeface(Context context, int font) {
        Typeface typeface = Font.getFontType(context, font);
        if (typeface != null) {
            setTypeface(typeface);
        }
    }
}
