package com.liu.library.utils;

import android.graphics.Color;

/**
 * Created by Liu on 2017/1/17.
 */
public class LinearGradientUtil {
    private int mStartColor;
    private int mEndColor;

    public LinearGradientUtil(int startColor, int endColor) {
        this.mStartColor = startColor;
        this.mEndColor = endColor;
    }

    public void setStartColor(int startColor) {
        this.mStartColor = startColor;
    }

    public void setEndColor(int endColor) {
        this.mEndColor = endColor;
    }

    public int getStartColor() {
        return mStartColor;
    }


    public int getEndColor() {
        return mEndColor;
    }


    //获取某一个百分比间的颜色,radio取值[0,1]
    public int getColor(float radio) {
        radio=radio<0?0:radio;
        radio=radio>1?1:radio;

        int redStart = Color.red(mStartColor);
        int blueStart = Color.blue(mStartColor);
        int greenStart = Color.green(mStartColor);
        int redEnd = Color.red(mEndColor);
        int blueEnd = Color.blue(mEndColor);
        int greenEnd = Color.green(mEndColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.rgb(red, greed, blue);
    }
}
