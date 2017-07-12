package com.liu.mojiview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.liu.library.bean.ForecaseBean;
import com.liu.library.view.MojiView;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity{
    private MojiView mojiView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mojiView= (MojiView) findViewById(R.id.mojiView);
        setData();
    }

    private void setData() {
        List<ForecaseBean> data = new ArrayList<>();
        data.add(new ForecaseBean("08:00", 2, 26, -11, "0"));
        data.add(new ForecaseBean("现在", 1, 26, -10, "-1"));
        data.add(new ForecaseBean("10:00", 3, 23, -6, "-1"));
        data.add(new ForecaseBean("11:00", 3, 20, -3, "-1"));
        data.add(new ForecaseBean("12:00", 3, 17, -1, "-1"));
        data.add(new ForecaseBean("13:00", 3, 15, 1, "-1"));
        data.add(new ForecaseBean("14:00", 3, 16, 2, "-1"));
        data.add(new ForecaseBean("15:00", 3, 18, 4, "-1"));
        data.add(new ForecaseBean("16:00", 3, 16, 1, "-1"));
        data.add(new ForecaseBean("17:00", 3, 16, -2, "2"));
        data.add(new ForecaseBean("18:00", 2, 18, -5, "-1"));
        data.add(new ForecaseBean("18:07", 2, 18, -7, "-1"));
        data.add(new ForecaseBean("19:00", 2, 25, -8, "-1"));
        data.add(new ForecaseBean("20:00", 2, 33, -10, "-1"));
        data.add(new ForecaseBean("21:00", 1, 38, -11, "-1"));
        data.add(new ForecaseBean("22:00", 1, 39, -11, "1"));
        data.add(new ForecaseBean("23:00", 1, 36, -12, "-1"));
        data.add(new ForecaseBean("明天", 1, 32, -12, "-1"));
        data.add(new ForecaseBean("01:00", 1, 28, -12, "-1"));
        data.add(new ForecaseBean("02:00", 1, 25, -12, "-1"));
        data.add(new ForecaseBean("03:00", 1, 23, -12, "-1"));
        data.add(new ForecaseBean("04:00", 1, 23, -12, "0"));
        data.add(new ForecaseBean("05:00", 1, 23, -12, "-1"));
        data.add(new ForecaseBean("05:47", 1, 23, -12, "-1"));
        data.add(new ForecaseBean("06:00", 1, 25, -11, "-1"));
        data.add(new ForecaseBean("07:00", 2, 28, -11, "-1"));
        mojiView.setForecaseBeen(data);
    }




}
