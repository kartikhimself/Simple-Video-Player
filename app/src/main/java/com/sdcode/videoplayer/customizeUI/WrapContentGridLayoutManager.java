package com.sdcode.videoplayer.customizeUI;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Xi developer on 6/23/2017.
 */

public class WrapContentGridLayoutManager extends GridLayoutManager {


    public WrapContentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
        }
    }
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
