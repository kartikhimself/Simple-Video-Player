package com.sdcode.videoplayer.customizeUI;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import net.steamcrafted.materialiconlib.MaterialIconView;


public class NonScrollImageView extends MaterialIconView {

    public NonScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        return false;
    }

}
