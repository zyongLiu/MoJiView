package com.liu.library.bean;

/**
 * Created by Liu on 2017/3/2.
 */
public class LineBean {
    //  y=kx+b
    private float k;
    private float b;

    public LineBean(float k, float b) {
        this.k = k;
        this.b = b;
    }

    public float getK() {
        return k;
    }

    public void setK(float k) {
        this.k = k;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getY(float x) {
        return k * x + b;
    }
}
