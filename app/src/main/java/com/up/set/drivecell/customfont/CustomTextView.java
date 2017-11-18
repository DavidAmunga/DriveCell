package com.up.set.drivecell.customfont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.up.set.drivecell.R;


/**
 * Created by amush on 12-Nov-17.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {


    private int typefaceType;
    private TypeFactory mFontFactory;


    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    public CustomTextView(Context context) {
        super(context);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {


        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomTextView,
                0, 0);
        try {
            typefaceType = array.getInteger(R.styleable.CustomTextView_font_name, 0);
        } finally {
            array.recycle();
        }
        if (!isInEditMode()) {
            setTypeface(getTypeFace(typefaceType));
        }

    }

    public Typeface getTypeFace(int type) {
        if (mFontFactory == null)
            mFontFactory = new TypeFactory(getContext());

        switch (type) {
            case Constants.A_ROMAN:
                return mFontFactory.avenirRoman;

            case Constants.A_LIGHT:
                return mFontFactory.avenirLight;

            case Constants.A_BOOK:
                return mFontFactory.avenirBook;
            default:
                return mFontFactory.avenirBook;
        }
    }

    public interface Constants {
        int A_BOOK = 1,
                A_LIGHT = 2,
                A_ROMAN = 3;
    }


}