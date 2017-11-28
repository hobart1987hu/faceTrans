package org.hobart.facetrans.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import org.hobart.facetrans.ui.view.LinearAnimation;

/**
 * 此类代码可以简单封装一下， 可以作为一个控件使用
 * Created by huzeyin on 2017/11/1.
 */
public class RadarView extends View {

    private static final int COLORS[] = {Color.argb(4, 0, 202, 255), Color.argb(4, 0, 202, 255), Color.argb(33, 0, 202, 255), Color.TRANSPARENT, Color.TRANSPARENT};
    private static final float[] POSITIONS = {0.0f, 0.042f, 0.084f, 0.085f, 1.0f};

    private Paint mBgPaint;

    private Paint mOuterCirclePaint;

    private Paint mCenterCirclePaint;

    private Paint mInternalCirclePaint;

    private Paint mLinePaint;

    private Paint mSweepPaint;

    private SweepGradient mSweepGradient;

    private RectF mRect;

    private Matrix mMatrix;

    private float degree = 0;


    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mOutCircleRadius;
    private int mCenterCircleRadius;
    private int mInternalCircleRadius;

    private void init() {

        mRect = new RectF(0, 0, 0, 0);

        mOutCircleRadius = dip2px(334) / 2;
        mCenterCircleRadius = dip2px(200) / 2;
        mInternalCircleRadius = dip2px(86) / 2;

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.parseColor("#0f009CF7"));

        mOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterCirclePaint.setColor(Color.parseColor("#3300C6F7"));
        mOuterCirclePaint.setStrokeWidth(1);
        mOuterCirclePaint.setStyle(Paint.Style.STROKE);

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterCirclePaint.setColor(Color.parseColor("#3300C6F7"));
        mCenterCirclePaint.setStrokeWidth(1);
        mCenterCirclePaint.setStyle(Paint.Style.STROKE);

        mInternalCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInternalCirclePaint.setColor(Color.parseColor("#3300C6F7"));
        mInternalCirclePaint.setStrokeWidth(1);
        mInternalCirclePaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.parseColor("#3300C6F7"));
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mSweepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSweepPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mRect.set(0, 0, width, height);
        mSweepGradient = new SweepGradient(mRect.centerX(), mRect.centerY(), COLORS, POSITIONS);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int Width = getMeasuredWidth();
        int Height = getMeasuredHeight();
        int pointX = Width / 2;
        int pointY = Height / 2;
        //draw background
        canvas.drawCircle(pointX, pointY, mOutCircleRadius, mBgPaint);
        //draw big circle
        canvas.drawCircle(pointX, pointY, mOutCircleRadius, mOuterCirclePaint);
        //draw center circle
        canvas.drawCircle(pointX, pointY, mCenterCircleRadius, mCenterCirclePaint);
        //draw internal circle
        canvas.drawCircle(pointX, pointY, mInternalCircleRadius, mInternalCirclePaint);

        //draw line  vertical
        //float startX, float startY, float stopX, float stopY,
        canvas.drawLine(pointX, 0, pointX, mOutCircleRadius * 2, mLinePaint);
        //draw line  horizontal
        canvas.drawLine(0, pointY, mOutCircleRadius * 2, pointY, mLinePaint);

        drawSweepGradient(canvas);
    }


    private void drawSweepGradient(Canvas canvas) {
        mMatrix.reset();
        mMatrix.postRotate(degree, mRect.centerX(), mRect.centerY());
        mSweepPaint.setShader(mSweepGradient);
        canvas.concat(mMatrix);
        canvas.drawArc(mRect, 0, 30, true, mSweepPaint);
    }

    public void startRotate() {
        stopRotate();
        startRotate(2 * 1000);
    }

    public void stopRotate() {
        clearAnimation();
    }

    private void startRotate(long duration) {
        LinearAnimation animation = new LinearAnimation();
        animation.setDuration(duration);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        animation.setLinearAnimationListener(new LinearAnimation.LinearAnimationListener() {
            @Override
            public void applyTans(float interpolatedTime) {
                degree = 360 * interpolatedTime;
                invalidate();
            }
        });
        startAnimation(animation);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
