package com.changren.android.launcher.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.changren.android.launcher.R;

public class ColorArcProgressBar extends View {

//    private PromptDialog promptDialog;
    /**
     * 直径
     */
    private float diameter = 400;
    /**
     * 圆心X坐标
     */
    private float centerX;
    /**
     * 圆心Y坐标
     */
    private float centerY;

    /**
     * 整体弧形的画笔
     */
    private Paint allArcPaint;
    /**
     * 当前实际进度的画笔
     */
    private Paint progressPaint;
    /**
     * 显示内容的画笔
     */
    private Paint vTextPaint;
    /**
     * 显示单位的画笔
     */
    private Paint hintPaint;
    /**
     * 刻度的画笔
     */
    private Paint degreePaint;
    /**
     * 显示标题的画笔
     */
    private Paint curSpeedPaint;

    /**
     * 弧形的外切矩阵区域
     */
    private RectF bgRect;

    /**
     * 进度增长的动画类
     */
    private ValueAnimator progressAnimator;
    /**
     * Canvas抗锯齿工具类
     */
    private PaintFlagsDrawFilter mDrawFilter;
    /**
     * 颜色渐变工具类
     */
    private SweepGradient sweepGradient;

    /**
     * 3x3的位置坐标矩阵类
     */
    private Matrix rotateMatrix;

    /**
     * 圆弧的起始角度
     */
    private float startAngle = 135;
    /**
     * 扫描角度
     */
    private float sweepAngle = 270;
    /**
     * 进度对应的圆弧角度
     */
    private float currentAngle = 0;
    private float lastAngle;

    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};

    /**
     * 最大进度值，默认100
     */
    private float maxValues = 100;
    /**
     * 当前进度值，默认100
     */
    private float curValues = 0;

    /**
     * 进度条的完整刻度线宽
     */
    private float bgArcWidth = dipToPx(10);
    /**
     * 进度条的进度线宽
     */
    private float progressWidth = dipToPx(10);
    /**
     * 显示内容部分的字体大小
     */
    private float textSize = dipToPx(120);
    /**
     * 显示单位部分的字体大小
     */
    private float hintSize = dipToPx(20);
    /**
     * 显示标题部分的字体大小
     */
    private float curSpeedSize = dipToPx(20);
    /**
     * 动画持续时间 ms
     */
    private int animationDuration = 1000;
    /**
     * 间隔，参与矩形大小计算
     */
    private float longDegree = dipToPx(13);
    /**
     * 间隔，参与矩形大小计算
     */
    private float shortDegree = dipToPx(5);
    /**
     * 每格刻度的间距
     */
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);

    /**
     * 单位/标题的字体颜色
     */
    private int hintColor;
    /**
     * 外刻度线的长线条颜色
     */
    private int longDegreeColor;
    /**
     * 外刻度线的长线条颜色
     */
    private int shortDegreeColor;
    /**
     * 默认进度条颜色
     */
    private int bgArcColor;
    /**
     * 标题内容
     */
    private String titleString;
    /**
     * 单位内容
     */
    private String hintString;

    private boolean isNeedTitle;
    private boolean isNeedUnit;
    private boolean isNeedDial;
    private boolean isNeedContent;

    /**
     * sweepAngle/maxValues的值
     */
    private float k;

    public ColorArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (JwlUtils.isLogin()) {
//            LogPlus.e("**点击测试**");
//            //已安装，打开程序，需传入参数包名：
//            if (InstallUtils.isAvilible(Utils.getApp().getApplicationContext(), "com.changren.android.healthmonitor")) {
//                LogPlus.e("**已安装");
//                Intent i = new Intent();
//                ComponentName cn = new ComponentName("com.changren.android.healthmonitor",
//                        "com.changren.android.healthmonitor.ui.activity.GeneralReportActivity");
//                i.setComponent(cn);
//
//                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                Utils.getApp().getApplicationContext().startActivity(i);
//            }
//            //未安装，跳转至market下载该程序
//            else {
//                //            Uri uri = Uri.parse("market://details?id=com.tencent.mm");//id为包名
//
//                // 直接从指定网址下载
//                // Uri uri = Uri.parse("http://dldir1.qq.com/foxmail/weixin521android400.apk");
//
//                //            Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                //            getContext(). startActivity(it);
//                LogPlus.e("**应用未安装**");
//            }
//
//        } else {
//            LoginDialog();
////            setCurrentValues(0);
//        }

        return super.onTouchEvent(event);
    }

//    private void LoginDialog() {
//        PromptButton cancel = new PromptButton("取消", null);
//        cancel.setTextColor(ContextCompat.getColor(getContext(), R.color.user_dialog_btn_color));
//        promptDialog.showWarnAlert("当前未登录，点击取消，退回到桌面！", cancel,
//                new PromptButton("去登录", new PromptButton.PromptButtonListener() {
//                    @Override
//                    public void onClick(PromptButton button) {
//                        Intent intent = new Intent(Utils.getApp(), LoginActivity.class);
//                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                        Utils.getApp().getApplicationContext().startActivity(intent);
//                    }
//                }));
//    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_front_color2, color1);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_front_color3, color1);
        colors = new int[]{color1, color2, color3, color3};

        sweepAngle = a.getInteger(R.styleable.ColorArcProgressBar_total_angle, 270);
        bgArcWidth = a.getDimension(R.styleable.ColorArcProgressBar_back_width, dipToPx(8));
        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_front_width, dipToPx(10));
        diameter = a.getDimension(R.styleable.ColorArcProgressBar_circle_size, dipToPx(3 * getScreenWidth() / 5));
        isNeedTitle = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_title, false);
        isNeedContent = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_content, false);
        isNeedUnit = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_unit, false);
        isNeedDial = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_dial, false);
        hintString = a.getString(R.styleable.ColorArcProgressBar_string_unit);
        titleString = a.getString(R.styleable.ColorArcProgressBar_string_title);
        curValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 0);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 100);
        setMaxValues(maxValues);
        setCurrentValues(curValues);

        a.recycle();

    }

//    private void getHealthScore() {
//        HealthScore healthScore = new HealthScore();
//        LogPlus.d("**健康指数信息**", healthScore.toString());
//        if (healthScore.getIndex() > 0) {
//            setCurrentValues(healthScore.getIndex());
//        }
//
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //        int width = (int) (2 * longDegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        //        int height= (int) (2 * longDegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        int width = (int) (shortDegree * 2 + diameter + progressWidth * 2);
        int height = (int) (shortDegree * 2 + diameter + progressWidth * 2);

        width = getSize(width, widthMeasureSpec);
        height = getSize(height, heightMeasureSpec);

        //        Log.e(TAG, "set width="+width+ "---height="+height+"---diameter="+diameter);
        setMeasuredDimension(width, height);

    }

    private int getSize(int defaultSize, int measureSpec) {
        int realSize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        //        Log.e(TAG, "getSize mode="+mode+ "---size="+size);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                realSize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                realSize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                realSize = size;
                break;
            }
        }
        return realSize;
    }

    private void initView() {
        //        Log.e(TAG, "initView width="+getMeasuredWidth()+ "---height="+getMeasuredWidth());
        //圆形进度条的半径
        //        diameter = 3 * getScreenWidth() / 5;
        //弧形的矩阵区域
        bgRect = new RectF();
        //        bgRect.top = longDegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        //        bgRect.left = longDegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE;
        //        bgRect.right = diameter + (longDegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);
        //        bgRect.bottom = diameter + (longDegree + progressWidth/2 + DEGREE_PROGRESS_DISTANCE);
        bgRect.top = shortDegree;
        bgRect.left = shortDegree;
        bgRect.right = shortDegree * 2 + diameter + progressWidth * 2;
        bgRect.bottom = shortDegree * 2 + diameter + progressWidth * 2;
        //        Log.e(TAG, "bgRect.top="+bgRect.top+ "---bgRect.left="+bgRect.left+"--bgRect.right="+bgRect.right
        //                +"---bgRect.bottom"+bgRect.bottom);

        //圆心
        //        centerX = (2 * longDegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;
        //        centerY = (2 * longDegree + progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE)/2;
        centerX = (shortDegree * 2 + progressWidth * 2 + diameter) / 2;
        centerY = (shortDegree * 2 + progressWidth * 2 + diameter) / 2;

        if (hintColor == 0) {
            hintColor = getColor(R.color.colorArcProgressBar_text_color);
        }

        if (longDegreeColor == 0) {
            longDegreeColor = getColor(R.color.colorArcProgressBar_text_color);
        }

        if (bgArcColor == 0) {
            bgArcColor = getColor(R.color.colorArcProgressBar_text_color);
        }

        if (shortDegreeColor == 0) {
            shortDegreeColor = getColor(R.color.colorArcProgressBar_text_color);
        }

        //外部刻度线
        degreePaint = new Paint();
        degreePaint.setColor(longDegreeColor);

        //整个弧形
        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor(bgArcColor);
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        //当前进度的弧形
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(Color.GREEN);

        //内容显示文字
        vTextPaint = new Paint();
        vTextPaint.setTextSize(textSize);
        vTextPaint.setColor(hintColor);
        vTextPaint.setTextAlign(Paint.Align.CENTER);

        //显示单位文字
        hintPaint = new Paint();
        hintPaint.setTextSize(hintSize);
        hintPaint.setColor(hintColor);
        hintPaint.setTextAlign(Paint.Align.CENTER);

        //显示标题文字
        curSpeedPaint = new Paint();
        curSpeedPaint.setTextSize(curSpeedSize);
        curSpeedPaint.setColor(hintColor);
        curSpeedPaint.setTextAlign(Paint.Align.CENTER);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();

//        //先弹窗提示"是否退出登录"
//        if (promptDialog == null) {
//            //创建对象
//            promptDialog = new PromptDialog((Activity) getContext());
//            //设置自定义属性
//            promptDialog.getDefaultBuilder().touchAble(true).round(10).loadingDuration(3000);
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        if (isNeedDial) {
            //画刻度线
            for (int i = 0; i < 40; i++) {
                if (i > 15 && i < 25) {
                    canvas.rotate(9, centerX, centerY);
                    continue;
                }
                if (i % 5 == 0) {
                    degreePaint.setStrokeWidth(dipToPx(2));
                    degreePaint.setColor(longDegreeColor);
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - longDegree, degreePaint);
                } else {
                    degreePaint.setStrokeWidth(dipToPx(1.4f));
                    degreePaint.setColor(shortDegreeColor);
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longDegree - shortDegree) / 2,
                            centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - (longDegree - shortDegree) / 2 - shortDegree, degreePaint);
                }

                canvas.rotate(9, centerX, centerY);
            }
        }

        //整个弧
        canvas.drawArc(bgRect, startAngle, sweepAngle, false, allArcPaint);

        //设置渐变色
        rotateMatrix.setRotate(130, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);

        //当前进度
        canvas.drawArc(bgRect, startAngle, currentAngle, false, progressPaint);

        if (isNeedContent) {
            canvas.drawText(String.format("%.0f", curValues), centerX, centerY + textSize / 4, vTextPaint);
        }
        if (isNeedUnit) {
            canvas.drawText(hintString, centerX, centerY + diameter / 4 + textSize / 4, hintPaint);
        }
        if (isNeedTitle) {
            canvas.drawText(titleString, centerX, centerY - diameter / 4 - textSize / 4, curSpeedPaint);
        }

        invalidate();

    }

    /**
     * 设置最大值
     *
     * @param maxValues
     */
    public void setMaxValues(float maxValues) {
        this.maxValues = maxValues;
        k = sweepAngle / maxValues;
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues > maxValues) {
            currentValues = maxValues;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        lastAngle = currentAngle;
        setAnimation(lastAngle, currentValues * k, animationDuration);
    }

    /**
     * 设置整个圆弧宽度
     *
     * @param bgArcWidth
     */
    public void setBgArcWidth(int bgArcWidth) {
        this.bgArcWidth = bgArcWidth;
    }

    /**
     * 设置进度宽度
     *
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth) {
        this.progressWidth = progressWidth;
    }

    /**
     * 设置速度文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置单位文字大小
     *
     * @param hintSize
     */
    public void setHintSize(int hintSize) {
        this.hintSize = hintSize;
    }

    /**
     * 设置单位文字
     *
     * @param hintString
     */
    public void setUnit(String hintString) {
        this.hintString = hintString;
        invalidate();
    }

    /**
     * 设置直径大小
     *
     * @param diameter
     */
    public void setDiameter(int diameter) {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    private void setTitle(String title) {
        this.titleString = title;
    }

    /**
     * 设置是否显示标题
     *
     * @param isNeedTitle
     */
    private void setIsNeedTitle(boolean isNeedTitle) {
        this.isNeedTitle = isNeedTitle;
    }

    /**
     * 设置是否显示单位文字
     *
     * @param isNeedUnit
     */
    private void setIsNeedUnit(boolean isNeedUnit) {
        this.isNeedUnit = isNeedUnit;
    }

    /**
     * 设置是否显示外部刻度盘
     *
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial) {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int duration) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(duration);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                curValues = currentAngle / k;
            }
        });
        progressAnimator.start();
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 根据res ID获取颜色值
     *
     * @param id
     * @return
     */
    private int getColor(int id) {
        if (getContext() == null) {
            return 0xffffff;
        }
        return ContextCompat.getColor(getContext(), id);
    }
}
