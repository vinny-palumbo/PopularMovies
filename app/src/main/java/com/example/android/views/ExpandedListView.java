/*
 * Code copied from netzpurist's solution to the following question on stack overflow:
 *  "Calculate the size of a list view or how to tell it to fully expand"
 *
 *  http://stackoverflow.com/questions/2312683/calculate-the-size-of-a-list-view-or-how-to-tell-it-to-fully-expand
 */

package com.example.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

public class ExpandedListView extends ListView {

    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != old_count) {
            old_count = getCount();
            params = getLayoutParams();
            params.height = getCount() * (old_count > 0 ? getChildAt(0).getHeight() : 0);
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }

}