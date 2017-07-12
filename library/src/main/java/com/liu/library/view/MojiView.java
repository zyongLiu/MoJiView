package com.liu.library.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liu.library.DrawDataReadly;
import com.liu.library.R;
import com.liu.library.bean.ForecaseBean;
import com.liu.library.utils.DisplayUtil;

import java.util.List;

/**
 * Created by Liu on 2017/6/23.
 */

public class MojiView extends LinearLayout {
    private Context mContext;

    private ForecaseView forecaseView;
    private TextView tvTemMax;
    private TextView tvTemMin;
    private TextView tvAQH;
    private TextView tvWind;

    private int textHeight;
    public MojiView(Context context) {
        this(context, null);
    }

    public MojiView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MojiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init();
    }

    private void init() {
        View.inflate(mContext, R.layout.layout_mojiview,this);
        forecaseView=  findViewById(R.id.fv);
        tvTemMax=  findViewById(R.id.tvTemMax);
        tvTemMin=  findViewById(R.id.tvTemMin);
        tvAQH=  findViewById(R.id.tvAQH);
        tvWind=  findViewById(R.id.tvWind);

        textHeight = DisplayUtil.dip2px(mContext, 20);

        forecaseView.setDrawDataReadly(new DrawDataReadly() {
            @Override
            public void temMax(float height, int value) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight);
                params.setMargins(0, (int) (height - textHeight / 2), 0, 0);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                tvTemMax.setText(value + "°");
                tvTemMax.setLayoutParams(params);
            }

            @Override
            public void temMin(float height, int value) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight);
                params.setMargins(0, (int) (height - textHeight / 2), 0, 0);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                tvTemMin.setText(value + "°");
                tvTemMin.setLayoutParams(params);
            }

            @Override
            public void aqhWind(float aqh, float wind) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight);
                params.setMargins(0, (int) (aqh - textHeight), 0, 0);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                tvAQH.setLayoutParams(params);


                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, textHeight);
                params1.setMargins(0, (int) (wind - textHeight), 0, 0);
                params1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                tvWind.setLayoutParams(params1);
            }
        });
    }

    public void setForecaseBeen(List<ForecaseBean> forecaseBeen) {
        forecaseView.setForecaseBeen(forecaseBeen);
    }
}
