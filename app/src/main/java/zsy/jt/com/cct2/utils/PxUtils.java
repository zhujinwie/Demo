package zsy.jt.com.cct2.utils;

import android.content.Context;

/**
 * 像素工具转换类
 *
 * **/
public class PxUtils {

    public static int dip2px(Context context,float dpValue){

        float scale = context.getResources().getDisplayMetrics().density;

        return (int)(dpValue*scale +0.5f);

    }


    public static int sp2px(Context context,float spValue){

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        return (int)(spValue*fontScale+0.5f);

    }

    public static int px2dip(Context context,float pxValue){

        float scale = context.getResources().getDisplayMetrics().density;

        return (int)(pxValue/scale + 0.5f);
    }

    public static int px2sp(Context context,float pxValue){

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        return (int)(pxValue/fontScale +0.5f);
    }

}
