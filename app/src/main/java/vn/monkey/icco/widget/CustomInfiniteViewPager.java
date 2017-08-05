package vn.monkey.icco.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import vn.monkey.icco.infiniteViewPager.InfiniteViewPager;

/**
 * Created by ANHTH on 06-Jul-16.
 */

public class CustomInfiniteViewPager extends InfiniteViewPager {

    public CustomInfiniteViewPager(Context context) {
        super(context);
    }

    public CustomInfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
