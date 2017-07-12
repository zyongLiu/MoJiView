package com.liu.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * dp、sp 转换为 px 的工具类
 *
 * Created by sunfusheng on 16/5/31.
 */
public class DisplayUtil {

    public static int statusBarHeight = 0;
    public static int screenHeight = 0;

    // 将px值转换为dip或dp值，保证尺寸大小不变
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 将dip或dp值转换为px值，保证尺寸大小不变
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // 将px值转换为sp值，保证文字大小不变
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    // 将sp值转换为px值，保证文字大小不变
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 屏幕宽度（像素）
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    // 屏幕高度（像素）
    public static int getWindowHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 获得屏幕尺寸
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * 获得状态栏高度
     * 不能再onCreate方法中调用
     *
     * @return
     */
    public static int getStatusBarHeight(Activity context) {
        if (statusBarHeight == 0) {
            Rect frame = new Rect();
            context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
        }
        return statusBarHeight;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (screenHeight == 0) {
            screenHeight = ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getHeight();
        }

        return screenHeight;
    }
}