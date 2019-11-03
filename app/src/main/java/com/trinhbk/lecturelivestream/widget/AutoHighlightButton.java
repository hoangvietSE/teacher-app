package com.trinhbk.lecturelivestream.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class AutoHighlightButton extends AppCompatButton {

    public AutoHighlightButton(Context context) {
        super(context);
    }

    public AutoHighlightButton(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setCustomFont(context, attrs);
    }

    public AutoHighlightButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setCustomFont(context, attrs);
    }

//    private void setCustomFont(Context ctx, AttributeSet attrs) {
//        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.StyleTextView);
//        String customFont = a.getString(R.styleable.StyleTextView_customFontTextView);
//        setCustomFont(ctx, customFont);
//        setText(getText());
//        a.recycle();
//    }

//    public boolean setCustomFont(Context ctx, String asset) {
//        Typeface tf = null;
//        try {
//            tf = ThemeManager.INSTANCE.getTypeface(asset);
//        } catch (Exception e) {
//            return false;
//        }
//
//        setTypeface(tf);
//        return true;
//    }

    @Override
    public void setPressed(boolean pressed) {
        if (isEnabled()) {
            if (pressed) {
                setAlpha(0.5f);
            } else {
                setAlpha(1.0f);
            }
        }
        super.setPressed(pressed);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.5f);
        }
        super.setEnabled(enabled);
    }
}
