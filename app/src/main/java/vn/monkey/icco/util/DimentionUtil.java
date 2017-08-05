package vn.monkey.icco.util;

import android.content.Context;

/**
 * Created by hoang on 6/18/2016.
 */

public class DimentionUtil {

    public static int convertPixelToDip(Context context, int pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pixel - 0.5f) / scale);
    }

    public static int converDipToPixel(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

}
