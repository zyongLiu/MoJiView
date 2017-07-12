package com.liu.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.liu.library.DrawDataReadly;
import com.liu.library.R;
import com.liu.library.bean.ForecaseBean;
import com.liu.library.bean.LineBean;
import com.liu.library.utils.DisplayUtil;
import com.liu.library.utils.LinearGradientUtil;
import com.liu.library.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by Liu on 2017/1/16.
 */
public class ForecaseView extends View {
    private Context mContext;

    private Paint mPaint;

    private int numValue = 26;

    private TextPaint mTextPaint;
    //下方文字字体大小
    private int textSize = 40;
    //小块宽度
    private float cellWidth = 112;
    //块间距
    private float division = 3;

    private int mHeight, mWidth;
    //路径
    private Path path;
    //滑动百分比
    private float rate = 0f;

    private float temperatureHeight;
    private float aqiHeight;
    private float windHeight;
    private float timeHeight;

    private float marginLeft = 10, marginRight = 10, marginTop = 10, marginBottom;
    //内容区域宽度
    private float contentWidth;
    //温度块高度
    private float temBlockHeight = 40f;
    //平滑指数
    private float smoothness = 0.33f;
    //风力测试数据
    private List<Integer[]> windData;
    //真实数据
    private List<PointF> mPoint;
    //真实数据处理后 画贝塞尔曲线的点
    private List<PointF> drawPoint = new ArrayList<>();
    //风力块高度
    private float windBlockHeight = 40f;

    private static Paint.FontMetrics mFontMetricsBuffer = new Paint.FontMetrics();

    //数据
    private List<ForecaseBean> forecaseBeen;
    //最高最低气温
    private int maxTem, minTem;

    private WeakHashMap<Integer, LinearGradientUtil> colorGradient = new WeakHashMap<>();

    private DrawDataReadly drawDataReadly;

    //空气质量开始绘制位置
    private float textStart;
    //空气质量最终位置
    private float textEnd;
    //视图区域左侧
    private int leftX;
    //视图区域右侧
    private int rightY;

    public ForecaseView(Context context) {
        this(context, null);
    }

    public ForecaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        context.obtainStyledAttributes(attrs, R.styleable.forecastView);
    }

    public ForecaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    public List<ForecaseBean> getForecaseBeen() {
        return forecaseBeen;
    }

    public DrawDataReadly getDrawDataReadly() {
        return drawDataReadly;
    }

    public void setDrawDataReadly(DrawDataReadly drawDataReadly) {
        this.drawDataReadly = drawDataReadly;
    }

    public void setForecaseBeen(List<ForecaseBean> forecaseBeen) {
        lineList = null;
        this.forecaseBeen = forecaseBeen;
        maxTem = minTem = forecaseBeen.get(0).getTemValue();
        numValue = forecaseBeen.size();
        mPoint = new ArrayList<>();
        windData = new ArrayList<>();
        int windStart = -1, windEnd = -1, windValue = 0;
        for (int i = 0; i < forecaseBeen.size(); i++) {
            ForecaseBean bean = forecaseBeen.get(i);
            if (maxTem < bean.getTemValue()) {
                maxTem = bean.getTemValue();
            }
            if (minTem > bean.getTemValue()) {
                minTem = bean.getTemValue();
            }
            //计算温度
            mPoint.add(new PointF(i, bean.getTemValue()));
            //计算风力
            if (bean.getWindPower() != windValue) {
                if (windEnd == windStart) {
                    if (windStart == -1) {
                        windEnd = windStart = i;
                    } else {
                        windEnd = i - 1;
                        windData.add(new Integer[]{windStart, windEnd, windValue});
                        windStart = i;
                    }
                } else if (windEnd < windStart) {
                    windEnd = i - 1;
                    windData.add(new Integer[]{windStart, windEnd, windValue});
                    windStart = i;
                } else {
                    windData.add(new Integer[]{windStart, windEnd, windValue});
                    windStart = i;
                }
                windValue = bean.getWindPower();
            }
            if (i == forecaseBeen.size() - 1) {
                windEnd = i;
                windData.add(new Integer[]{windStart, windEnd, windValue});
            }
            //空气质量
            if (i == forecaseBeen.size() - 1) {
                forecaseBeen.get(i).setAhqDiffRe(0);
            } else {
                forecaseBeen.get(i).setAhqDiffRe(forecaseBeen.get(i).getAhqLv() - forecaseBeen.get(i + 1).getAhqLv());
            }
        }
        computeBesselPoints();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.YELLOW);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
        cellWidth = mTextPaint.measureText("00:0000");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            contentWidth = numValue * (cellWidth + division);
            mWidth = (int) (contentWidth + marginLeft + marginRight);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = 1200;
        }

        temperatureHeight = (mHeight - marginTop - marginBottom) * 2 / 3;
        aqiHeight = windHeight = timeHeight = temperatureHeight / 6;
        textStart = cellWidth / 2 + marginLeft;
        textEnd = mWidth - marginRight - cellWidth / 2;

        leftX = 0;
        rightY = mWidth;

        if (drawDataReadly != null) {
            drawDataReadly.aqhWind(marginTop + temperatureHeight + aqiHeight, marginTop + temperatureHeight + aqiHeight + windHeight);
            drawDataReadly.temMax(getRealY(maxTem), maxTem);
            drawDataReadly.temMin(getRealY(minTem), minTem);
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 是否要画空气质量
     */
    private boolean isAQHDraw = false;

    private int leftVisiableIndex = 0;
    private int rightVisiableIndex;

    @Override
    protected void onDraw(Canvas canvas) {
        long startDate = System.currentTimeMillis();
        rightVisiableIndex = numValue;
        isAQHDraw = true;
        //空气质量 文字

        float centerTextX = textStart + rate * (textEnd - textStart);
        mTextPaint.setTextSize(textSize);
        //空气质量#AD951E
        path = new Path();
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < numValue; i++) {
            temBlockHeight = forecaseBeen.get(i).getAhqLv() * aqiHeight / 7;
            //空气质量 块状体
            float textWidth = mTextPaint.measureText(forecaseBeen.get(i).getAhqValue() + "");

            int visiable = visiableContain(i * (cellWidth + division) + marginLeft, cellWidth * (i + 1) + division * i + marginLeft);
            if (visiable == 0) {
                RectF rectF = new RectF(
                        i * (cellWidth + division) + marginLeft,
                        marginTop + temperatureHeight + aqiHeight - temBlockHeight,
                        cellWidth * (i + 1) + division * i + marginLeft,
                        marginTop + temperatureHeight + aqiHeight);
                path.addRoundRect(rectF,
                        new float[]{15, 15, 15, 15, 0, 0, 0, 0}, Path.Direction.CW);
                //left
                float colorRate = (centerTextX + textWidth / 2 - cellWidth * (i + 1) - division * i - marginLeft) / (textWidth + division);
                //left
                if (centerTextX + textWidth / 2 > cellWidth * (i + 1) + division * i + marginLeft && centerTextX - textWidth / 2 < (i + 1) * (cellWidth + division) + marginLeft) {
                    mPaint.setColor(
                            getGradientUtil(forecaseBeen.get(i).getAhqLv()).getColor(1 - colorRate)
                    );
                } else if (centerTextX > cellWidth * i + division * i + marginLeft && centerTextX < cellWidth * (i + 1) + division * i + marginLeft) {
                    mPaint.setColor(getGradientUtil(forecaseBeen.get(i).getAhqLv()).getEndColor());
                } else {
                    mPaint.setColor(getGradientUtil(forecaseBeen.get(i).getAhqLv()).getStartColor());
                }
                canvas.drawPath(path, mPaint);
                path.reset();
            } else if (visiable > 0) {
                rightVisiableIndex = i;
                break;
            } else {
                leftVisiableIndex = i;
                continue;
            }


            //画滚动条的温度
            if (centerTextX > i * (cellWidth + division) + marginLeft && centerTextX < cellWidth * (i + 1) + division * i + marginLeft) {
                lastTemText = forecaseBeen.get(i).getTemValue() + "°";
            }

            if (isAQHDraw) {
                int nowAHQLvRe = forecaseBeen.get(i).getAhqDiffRe();
                if (nowAHQLvRe < 0) {
                    if (centerTextX + textWidth / 2 <= (cellWidth + division) * (i + 1) + marginLeft) {
                        canvas.drawText(forecaseBeen.get(i).getAhqValue() + "", centerTextX, marginTop + temperatureHeight + aqiHeight - temBlockHeight, mTextPaint);
                        isAQHDraw = false;
                    }
                } else if (nowAHQLvRe == 0) {
                    if (centerTextX <= (cellWidth + division) * (i + 1) + marginLeft - 0.5f * division) {
                        canvas.drawText(forecaseBeen.get(i).getAhqValue() + "", centerTextX, marginTop + temperatureHeight + aqiHeight - temBlockHeight, mTextPaint);
                        isAQHDraw = false;
                    }
                } else {
                    if (centerTextX - textWidth / 2 <= cellWidth * (i + 1) + division * i + marginLeft) {
                        canvas.drawText(forecaseBeen.get(i).getAhqValue() + "", centerTextX, marginTop + temperatureHeight + aqiHeight - temBlockHeight, mTextPaint);
                        isAQHDraw = false;
                    }
                }
            }


        }
        //风力
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.GREEN);
        mTextPaint.setTextSize(30f);
        mTextPaint.getFontMetrics(mFontMetricsBuffer);
        for (int i = 0; i < windData.size(); i++) {
            Integer[] d = windData.get(i);
            int visiable = visiableContain(d[0] * cellWidth + division * (d[0]) + marginLeft, (d[1] + 1) * cellWidth + division * (d[1]) + marginLeft);
            if (visiable == 0) {
                RectF rectF = new RectF(d[0] * cellWidth + division * (d[0]) + marginLeft,
                        marginTop + temperatureHeight + aqiHeight + windHeight - windBlockHeight,
                        (d[1] + 1) * cellWidth + division * (d[1]) + marginLeft,
                        marginTop + temperatureHeight + aqiHeight + windHeight);
                path.addRoundRect(rectF,
                        new float[]{15, 15, 15, 15, 0, 0, 0, 0}, Path.Direction.CW);
                mPaint.setColor(Color.LTGRAY);
                canvas.drawPath(path, mPaint);
                canvas.drawText(d[2] + "级", marginLeft + cellWidth * (d[0] + d[1] + 1) / 2 + division * (d[0] + d[1]) / 2,
                        marginTop + temperatureHeight + aqiHeight + windHeight - mFontMetricsBuffer.descent
                        , mTextPaint);
                path.reset();
            } else if (visiable > 0) {
                break;
            } else {
                continue;
            }
        }
        //时间
        mTextPaint.setTextSize(40f);
        mTextPaint.getFontMetrics(mFontMetricsBuffer);
//        for (int i = 0; i < numValue; i++) {
        for (int i = leftVisiableIndex; i < rightVisiableIndex; i++) {
            int visiable = visiableContain(marginLeft + cellWidth * i + division * i, marginLeft + cellWidth * (i + 1) + division * i);
            if (visiable == 0) {
                canvas.drawText(forecaseBeen.get(i).getTime(), marginLeft + cellWidth * (2 * i + 1) / 2 + division * i,
                        marginTop + temperatureHeight + aqiHeight + windHeight + timeHeight - mFontMetricsBuffer.descent, mTextPaint);
            } else if (visiable > 0) {
                LogUtils.d("break3:" + i);
                break;
            } else {
                continue;
            }
        }
        //画曲线
        if (drawPoint != null && drawPoint.size() > 0) {
            drawBezier(canvas);
        }
        float y = getFlingBoxY2(centerTextX);

        RectF rectWeather = new RectF(
                centerTextX + DisplayUtil.dip2px(mContext, 5),
                y - DisplayUtil.dip2px(mContext, 40),
                centerTextX + DisplayUtil.dip2px(mContext, 25),
                y - DisplayUtil.dip2px(mContext, 20));

        //画曲线上图片
        bitmaps = new ArrayList<>();
        for (int i = leftVisiableIndex; i < rightVisiableIndex; i++) {
            Bitmap bitmap = null;
            ForecaseBean bean = forecaseBeen.get(i);
            if (bean.getWeather().equals("0")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w0);
            } else if (bean.getWeather().equals("1")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w1);
            } else if (bean.getWeather().equals("2")) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w2);
            }

            if (bitmap != null) {
                float bitmapPointX = getRealX(mPoint.get(i).x);
                float bitmapPointY = getRealY(mPoint.get(i).y);
                RectF rectF = new RectF(
                        bitmapPointX - DisplayUtil.dip2px(mContext, 10),
                        bitmapPointY - DisplayUtil.dip2px(mContext, 30),
                        bitmapPointX + DisplayUtil.dip2px(mContext, 10),
                        bitmapPointY - DisplayUtil.dip2px(mContext, 10));
                bitmaps.add(new String[]{bitmapPointX + "", bean.getWeather()});
                //画静止天气
                if (centerTextX + DisplayUtil.dip2px(mContext, 30) <= (bitmapPointX - DisplayUtil.dip2px(mContext, 10)) ||
                        centerTextX - DisplayUtil.dip2px(mContext, 30) >= (bitmapPointX + DisplayUtil.dip2px(mContext, 10))) {

                    int visiable = visiableContain(rectF.left, rectF.right);
                    if (visiable == 0) {
                        canvas.drawBitmap(bitmap, null, rectF, mPaint);
                    } else if (visiable > 0) {
                        LogUtils.d("break4:" + i);
                        break;
                    }
                }
            }
        }

        //画滚动框
        for (int i = 0; i < bitmaps.size(); i++) {
            String[] strings = bitmaps.get(i);
            if (centerTextX >= Float.parseFloat(strings[0])) {
                if (strings[1].equals("0")) {
                    lastBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w0);
                } else if (strings[1].equals("1")) {
                    lastBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w1);
                } else if (strings[1].equals("2")) {
                    lastBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.w2);
                }
            }
        }
        Rect temBoxRect = new Rect(
                (int) centerTextX - DisplayUtil.dip2px(mContext, 40),
                (int) y - DisplayUtil.dip2px(mContext, 45),
                (int) centerTextX + DisplayUtil.dip2px(mContext, 40),
                (int) y - DisplayUtil.dip2px(mContext, 10));
        if (lastBitmap != null) {
            //画出背景图片
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.hour_24_float);
            drawable.setBounds(temBoxRect);
            drawable.draw(canvas);
            //画温度
            mFontMetricsBuffer = mTextPaint.getFontMetrics();
//            mTextPaint.getTextBounds(lastTemText,0,lastTemText.length(),mTemTextRectBuffer);

            // 计算文字baseline
            float textCenter = (mFontMetricsBuffer.ascent + mFontMetricsBuffer.descent) / 2;

            canvas.drawText(lastTemText, centerTextX - DisplayUtil.dip2px(mContext, 15),
                    y - DisplayUtil.dip2px(mContext, 30) - textCenter, mTextPaint);

            canvas.drawBitmap(lastBitmap, null, rectWeather, mPaint);
        }
        LogUtils.e("date:" + (System.currentTimeMillis() - startDate));
    }

    /**
     * 是否可见
     *
     * @param left
     * @param right
     * @return
     */
    public int visiableContain(float left, float right) {
        if (right >= leftX - 10 && left <= rightY + 10) {
            return 0;
        } else if (right < leftX - 10) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * 获取滚动框的Y值
     *
     * @param centerTextX
     * @return
     */
    private float getFlingBoxY(float centerTextX) {
        //连接直线 沿直线移动
        PointF pLeft = mPoint.get(0), pRight = mPoint.get(0);
        for (PointF pointF : mPoint) {
            if (getRealX(pointF.x) < centerTextX) {
                pLeft = pointF;
            } else if (getRealX(pointF.x) >= centerTextX) {
                pRight = pointF;
                break;
            }
        }
//        y-y0=k(x-x0)
        float y;
        if (pLeft.x == pRight.x && pLeft.y == pRight.y) {
            y = getRealY(pLeft.y);
        } else {
            y = (getRealY(pRight.y) - getRealY(pLeft.y)) * (centerTextX - getRealX(pLeft.x)) / (getRealX(pRight.x) - getRealX(pLeft.x)) + getRealY(pLeft.y);
        }
        return y;
    }

    //存储滚动框移动的线段集合
    private List<LineBean> lineList;

    /**
     * 获取滚动框的Y值
     *
     * @param centerTextX
     * @return
     */
    private float getFlingBoxY2(float centerTextX) {
        int index = (int) ((numValue - 1) * rate);
        if (lineList == null || lineList.size() == 0) {
            lineList = new ArrayList<>();
            for (int i = 0; i < mPoint.size() - 1; i++) {
                float k = (getRealY(mPoint.get(i + 1).y) - getRealY(mPoint.get(i).y)) / (getRealX(mPoint.get(i + 1).x) - getRealX(mPoint.get(i).x));
                float b = getRealY(mPoint.get(i).y) - k * getRealX(mPoint.get(i).x);
                lineList.add(new LineBean(k, b));
            }
            lineList.add(new LineBean(0, getRealY(mPoint.get(mPoint.size() - 1).y)));
        }
        return lineList.get(index).getY(centerTextX);
    }


    String lastTemText = null;
    Bitmap lastBitmap = null;
    //是否在向左滑
    boolean fowward = true;
    private List<String[]> bitmaps;

    private LinearGradientUtil getGradientUtil(int i) {
        LinearGradientUtil util = colorGradient.get(i);
        if (util == null) {
            switch (i) {
                case 1:
                    util = new LinearGradientUtil(Color.parseColor("#3E6932"), Color.parseColor("#41A31F"));
                    break;
                case 2:
                    util = new LinearGradientUtil(Color.parseColor("#6E6B3A"), Color.parseColor("#AD941E"));
                    break;
                case 3:
                    util = new LinearGradientUtil(Color.parseColor("#635259"), Color.parseColor("#B36258"));
                    break;
                case 4:
                    util = new LinearGradientUtil(Color.parseColor("#46527D"), Color.parseColor("#675FB3"));
                    break;
                case 5:
                    util = new LinearGradientUtil(Color.parseColor("#514D6F"), Color.parseColor("#695CAB"));
                    break;
                case 6:
                    util = new LinearGradientUtil(Color.parseColor("#624D5E"), Color.parseColor("#894E72"));
                    break;
                default:
                    util = null;
            }
            if (util != null) {
                colorGradient.put(i, util);
            }
        }
        return util;
    }

    /**
     * 父ScrollView滚动时触发
     *
     * @param width
     * @param total
     */
    public void onScrollChanged(int width, int oldl, int total) {
        LogUtils.d("width:" + width + ",total:" + total);
        leftX = width;
        rightY = leftX + total;
        rate = width * 1.0f / (mWidth - total);
        rate = (float) (Math.round(rate * 1000)) / 1000;
        fowward = width >= oldl;
        postInvalidate();
    }


    /**
     * 画温度曲线 贝塞尔曲线
     *
     * @param canvas
     */
    public void drawBezier(Canvas canvas) {
        Path curvePath = new Path();
        for (int i = 0; i < drawPoint.size(); i = i + 3) {
            if (i == 0) {
                curvePath.moveTo(getRealX(drawPoint.get(i).x), getRealY(drawPoint.get(i).y));
            } else {
                curvePath.cubicTo(
                        getRealX(drawPoint.get(i - 2).x), getRealY(drawPoint.get(i - 2).y),
                        getRealX(drawPoint.get(i - 1).x), getRealY(drawPoint.get(i - 1).y),
                        getRealX(drawPoint.get(i).x), getRealY(drawPoint.get(i).y));
            }
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.YELLOW);
        canvas.drawPath(curvePath, mPaint);// 绘制光滑曲线

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        for (PointF point : mPoint) {
            canvas.drawCircle(getRealX(point.x), getRealY(point.y), 6f, mPaint);
        }
    }

    /**
     * 根据数据y获取真实屏幕坐标
     *
     * @param x
     * @return
     */
    private float getRealX(float x) {
        return marginLeft + cellWidth * (2f * x + 1f) / 2f + division * x;
    }

    /**
     * 根据数据y获取真实屏幕坐标
     *
     * @param y
     * @return
     */
    private float getRealY(float y) {
        return temperatureHeight * ((15f - y) / 30f) + marginTop;
    }

    /**
     * 计算贝塞尔结点
     */
    private void computeBesselPoints() {
        drawPoint.clear();
        for (int i = 0; i < mPoint.size(); i++) {
            if (i == 0 || i == mPoint.size() - 1) {
                computeUnMonotonePoints(i, mPoint, drawPoint);
            } else {
                PointF p0 = mPoint.get(i - 1);
                PointF p1 = mPoint.get(i);
                PointF p2 = mPoint.get(i + 1);
                if ((p1.y - p0.y) * (p1.y - p2.y) >= 0) {// 极值点
                    computeUnMonotonePoints(i, mPoint, drawPoint);
                } else {
                    computeMonotonePoints(i, mPoint, drawPoint);
                }
            }
        }
    }

    /**
     * 计算非单调情况的贝塞尔结点
     */
    private void computeUnMonotonePoints(int i, List<PointF> points, List<PointF> besselPoints) {
        if (i == 0) {
            PointF p1 = points.get(0);
            PointF p2 = points.get(1);
            besselPoints.add(p1);
            besselPoints.add(new PointF(p1.x + (p2.x - p1.x) * smoothness, p1.y));
        } else if (i == points.size() - 1) {
            PointF p0 = points.get(i - 1);
            PointF p1 = points.get(i);
            besselPoints.add(new PointF(p1.x - (p1.x - p0.x) * smoothness, p1.y));
            besselPoints.add(p1);
        } else {
            PointF p0 = points.get(i - 1);
            PointF p1 = points.get(i);
            PointF p2 = points.get(i + 1);
            besselPoints.add(new PointF(p1.x - (p1.x - p0.x) * smoothness, p1.y));
            besselPoints.add(p1);
            besselPoints.add(new PointF(p1.x + (p2.x - p1.x) * smoothness, p1.y));
        }
    }

    /**
     * 计算单调情况的贝塞尔结点
     *
     * @param i
     * @param points
     * @param besselPoints
     */
    private void computeMonotonePoints(int i, List<PointF> points, List<PointF> besselPoints) {
        PointF p0 = points.get(i - 1);
        PointF p1 = points.get(i);
        PointF p2 = points.get(i + 1);
        float k = (p2.y - p0.y) / (p2.x - p0.x);
        float b = p1.y - k * p1.x;
        PointF p01 = new PointF();
        p01.x = p1.x - (p1.x - (p0.y - b) / k) * smoothness;
        p01.y = k * p01.x + b;
        besselPoints.add(p01);
        besselPoints.add(p1);
        PointF p11 = new PointF();
        p11.x = p1.x + (p2.x - p1.x) * smoothness;
        p11.y = k * p11.x + b;
        besselPoints.add(p11);
    }

}
