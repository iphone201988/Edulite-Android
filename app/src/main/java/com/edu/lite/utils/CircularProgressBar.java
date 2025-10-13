package com.edu.lite.utils;




import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.edu.lite.R;

public class CircularProgressBar extends View {

    private int mViewWidth;
    private int mViewHeight;

    private final float mStartAngle = -90;  // start from top
    private float mSweepAngle = 0;          // sweep angle
    private float mMaxSweepAngle = 360;
    private float mStrokeWidth = 22;
    private int mAnimationDuration = 600;
    private int mMaxProgress = 100;
    private int currentProgress;

    private boolean mRoundedCorners = true;

    private int circleBackColor;
    private int startColor, endColor;

    private Paint mPaint;
    private Paint circlePaint;
    private RectF mRect;

    private int centerX, centerY, radius;

    private TextView textView;

    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyCustomView);
        mRoundedCorners = ta.getBoolean(R.styleable.MyCustomView_roundcorner, true);
        circleBackColor = ta.getColor(R.styleable.MyCustomView_circlebackgroundcolor, Color.parseColor("#ecedee"));
        mStrokeWidth = ta.getDimension(R.styleable.MyCustomView_circlestrokewidth, 22);
        startColor = ta.getColor(R.styleable.MyCustomView_startcolor, Color.parseColor("#4A90E2"));
        endColor = ta.getColor(R.styleable.MyCustomView_endcolor, Color.parseColor("#FFFFFF"));
        currentProgress = ta.getInt(R.styleable.MyCustomView_progess, 0);
        ta.recycle();

        mSweepAngle = calcSweepAngleFromProgress(currentProgress);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);
        mPaint.setShadowLayer(8, 0, 0, Color.parseColor("#1A3F7EF8"));
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStrokeWidth(mStrokeWidth - 1);
        circlePaint.setColor(circleBackColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width); // keep view square
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initMeasurements();
        drawCircleBackground(canvas);
        drawProgressArc(canvas);
        drawText(canvas);
    }

    private void initMeasurements() {
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        centerX = mViewWidth / 2;
        centerY = mViewHeight / 2;
        radius = Math.min(centerX, centerY);
        if (mRect == null) {
            float offset = mStrokeWidth;
            mRect = new RectF(offset, offset, 2 * radius - offset, 2 * radius - offset);
        }
    }




    private void drawCircleBackground(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius - mStrokeWidth, circlePaint);
    }

    private void drawProgressArc(Canvas canvas) {
        float progressValue = calcProgressFromSweepAngle(mSweepAngle);

        int[] colors;
        float[] positions;

        // Smooth fade but visible till full circle
        if (progressValue >= 100f) {
            // Full progress — solid gradient (no fade)
            colors = new int[]{
                    startColor,
                    endColor,
                    startColor  // repeat to close the sweep properly
            };
            positions = new float[]{0.5f, 2f, 1f};
        } else if (progressValue >= 80f) {
            // Between 80–99 → very light fade to avoid disappearing
            colors = new int[]{
                    startColor,
                    endColor,
                    adjustAlpha(endColor, 0.4f)
            };
            positions = new float[]{0f, 0.9f, 1f};
        } else {
            // Normal fade for 0–79%
            colors = new int[]{
                    startColor,
                    endColor,
                    Color.TRANSPARENT
            };
            positions = new float[]{0f, 0.85f, 1f};
        }

        Shader sweepGradient = new SweepGradient(centerX, centerY, colors, positions);

        // Rotate gradient so it starts at top (-90°)
        android.graphics.Matrix gradientMatrix = new android.graphics.Matrix();
        gradientMatrix.postRotate(-90, centerX, centerY);
        sweepGradient.setLocalMatrix(gradientMatrix);

        mPaint.setShader(sweepGradient);
        canvas.drawArc(mRect, mStartAngle, mSweepAngle, false, mPaint);
    }

    /** Helper: change color transparency */
    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    private void drawText(Canvas canvas) {
        String text = "+" + calcProgressFromSweepAngle(mSweepAngle);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#4A90E2"));
        textPaint.setTextSize(radius / 1.5f);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float y = centerY - (fm.ascent + fm.descent) / 2;

        canvas.drawText(text, centerX, y, textPaint);
    }

    private float calcSweepAngleFromProgress(int progress) {
        return (mMaxSweepAngle / mMaxProgress) * progress;
    }

    private int calcProgressFromSweepAngle(float sweepAngle) {
        return (int) ((sweepAngle * mMaxProgress) / mMaxSweepAngle);
    }

    public void setProgress(int progress) {
        currentProgress = progress;
        ValueAnimator animator = ValueAnimator.ofFloat(mSweepAngle, calcSweepAngleFromProgress(progress));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(mAnimationDuration);
        animator.addUpdateListener(animation -> {
            mSweepAngle = (float) animation.getAnimatedValue();
            if (textView != null)
                textView.setText("+" + calcProgressFromSweepAngle(mSweepAngle));
            invalidate();
        });
        animator.start();
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
