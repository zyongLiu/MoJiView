package com.liu.library.bean;

/**
 * Created by Liu on 2017/1/19.
 */
public class ForecaseBean {
    //时间07:00
    private String time;
    //风力 2级
    private int windPower;
    //大气指数 88
    private int ahqValue;
    //大气等级 PM2.5
    private int ahqLv;
    //温度
    private int temValue;
    //日出 日落
    private String describe;
    //天气状况 日出、日落
    private String weather;

//    private int ahqDiff=0;

    private int ahqDiffRe=0;


    public ForecaseBean(String time, int windPower, int ahqValue, String describe, String weather) {
        this.time = time;
        this.windPower = windPower;
        this.ahqValue = ahqValue;
        this.describe = describe;
        this.weather = weather;
    }

    public ForecaseBean(String time, int windPower, int ahqValue, int temValue, String weather) {
        this.time = time;
        this.windPower = windPower;
        this.ahqValue = ahqValue;
        this.temValue = temValue;
        this.weather = weather;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    public int getAhqDiff() {
//        return ahqDiff;
//    }
//
//    public void setAhqDiff(int ahqDiff) {
//        this.ahqDiff = ahqDiff;
//    }

    public int getAhqDiffRe() {
        return ahqDiffRe;
    }

    public void setAhqDiffRe(int ahqDiffRe) {
        this.ahqDiffRe = ahqDiffRe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getAhqLv() {
        if (ahqValue>=0&&ahqValue<=50){
            return 1;//#3E6932  #41A31F
        }else if (ahqValue>50&&ahqValue<=100){
            return 2; //#6E6B3A #AD941E
        }else if (ahqValue>100&&ahqValue<=150){
            return 3;//#635259 #B36258
        }else if (ahqValue>150&&ahqValue<=200){
            return 4;// #46527D #675FB3
        }else if (ahqValue>200&&ahqValue<=300){
            return 5;//#514D6F #695CAB
        }else if (ahqValue>300&&ahqValue<=500){
            return 6;//#624D5E #894E72
        }else {
            return 0;
        }
    }

    public void setAhqLv(int ahqLv) {
        this.ahqLv = ahqLv;
    }

    public int getWindPower() {
        return windPower;
    }

    public void setWindPower(int windPower) {
        this.windPower = windPower;
    }

    public int getAhqValue() {
        return ahqValue;
    }

    public void setAhqValue(int ahqValue) {
        this.ahqValue = ahqValue;
    }

    public int getTemValue() {
        return temValue;
    }

    public void setTemValue(int temValue) {
        this.temValue = temValue;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
