package vn.monkey.icco.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by ANHTH on 06-Jun-16.
 */

public class DisableScrollGridView extends GridView {
    public DisableScrollGridView(Context context) {
        super(context);
    }

    public DisableScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DisableScrollGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }


}
