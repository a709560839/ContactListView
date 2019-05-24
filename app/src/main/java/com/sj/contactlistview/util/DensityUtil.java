package com.sj.contactlistview.util;

import android.content.Context;

public class DensityUtil {
    /**
     * dp 转 px
     *
     * @param context .
     * @param dpValue .
     * @return .
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px 转 dp
     *
     * @param context .
     * @param pxValue .
     * @return .
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
