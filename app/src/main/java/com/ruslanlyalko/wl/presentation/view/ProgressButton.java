package com.ruslanlyalko.wl.presentation.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruslanlyalko.wl.R;

public class ProgressButton extends RelativeLayout {

    private static final String PROPERTY_RADIUS = "mCurrentRadius";
    private static final String PROPERTY_WIDTH = "mCurrentWidth";
    private static final long ANIMATION_DURATION = 300;

    private TextView mTextView;
    private ProgressBar mProgressBar;

    private CharSequence mButtonText = "";
    private int mButtonColor = Color.BLACK;
    private int mProgressColor = Color.WHITE;
    private float mRadius = 0;

    private Paint mPaint = new Paint();
    private int mCurrentWidth;
    private int mMaxRadius;
    private int mMinRadius;
    private int mCurrentRadius;
    private boolean mIsProgressShown;
    private ValueAnimator mAnimator;

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.button_progress, this);
        initComponents();
        setWillNotDraw(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0);
        float radius = a.getDimension(R.styleable.ProgressButton_pb_buttonRadius, 0);
        setRadius(radius);
        int buttonColor = R.styleable.ProgressButton_pb_buttonColor;
        if (a.hasValue(buttonColor)) {
            mButtonColor = a.getColor(buttonColor, Color.BLACK);
            setButtonColor(mButtonColor);
        }
        int progressColor = R.styleable.ProgressButton_pb_progressColor;
        if (a.hasValue(progressColor)) {
            mProgressColor = a.getColor(progressColor, Color.WHITE);
            setProgressColor(mProgressColor);
        }
        int ap = a.getResourceId(R.styleable.ProgressButton_pb_textAppearance, -1);
        TypedArray appearance = null;
        if (ap != -1) {
            appearance = context.obtainStyledAttributes(ap, R.styleable.TextAppearance);
        }
        if (appearance != null) {
            mTextView.setTextAppearance(context, ap);
//            readTextAppearance(context, appearance, attrs, false);
            appearance.recycle();
        }
        String text = a.getString(R.styleable.ProgressButton_pb_buttonText);
        setButtonText(text);
        float textSize = a.getDimensionPixelSize(R.styleable.ProgressButton_pb_textSize, 16);
        setTextSize(textSize);
        int textColor = R.styleable.ProgressButton_pb_textColor;
        if (a.hasValue(textColor)) {
            setTextColor(a.getColor(textColor, Color.WHITE));
        }
        a.recycle();
    }

    private void initComponents() {
        mTextView = findViewById(R.id.text1);
        mProgressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (mCurrentWidth == 0) mCurrentWidth = getWidth();
        mMaxRadius = getHeight() / 2;
        int half = getWidth() / 2;
        int halfWidth = mCurrentWidth / 2;
        int leftTopX = half - halfWidth;
        int rightBotX = half + halfWidth;
        canvas.drawRoundRect(leftTopX, 0, rightBotX, getHeight(), mCurrentRadius, mCurrentRadius, mPaint);
        super.onDraw(canvas);
    }

    public boolean isProgressShown() {
        return mIsProgressShown;
    }

    public void showProgress(boolean show) {
        if (mIsProgressShown == show) return;
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        mIsProgressShown = show;
        if (show) {
            setEnabled(false);
            mTextView.setText("");
            mProgressBar.setVisibility(VISIBLE);
        } else {
            setEnabled(true);
            mTextView.setText(mButtonText);
            mProgressBar.setVisibility(GONE);
        }
        int aWidth = getWidth();
        int aHeight = getHeight();
        mMaxRadius = getHeight() / 2;
        PropertyValuesHolder propertyRadius = PropertyValuesHolder.ofInt(PROPERTY_RADIUS, show ? mMinRadius : mMaxRadius, show ? mMaxRadius : mMinRadius);
        PropertyValuesHolder propertyWidth = PropertyValuesHolder.ofInt(PROPERTY_WIDTH, show ? aWidth : aHeight, show ? aHeight : aWidth);
        mAnimator = new ValueAnimator();
        mAnimator.setValues(propertyWidth, propertyRadius);
        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.addUpdateListener(animation -> {
            mCurrentRadius = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
            mCurrentWidth = (int) animation.getAnimatedValue(PROPERTY_WIDTH);
            invalidate();
        });
        mAnimator.start();
    }

    public CharSequence getButtonText() {
        return mTextView.getText();
    }

    public void setButtonText(CharSequence text) {
        mButtonText = text;
        mTextView.setText(mButtonText);
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        mMinRadius = (int) radius;
        mCurrentRadius = mMinRadius;
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public void setButtonColor(int color) {
        mButtonColor = color;
        mPaint.setColor(mButtonColor);
        invalidate();
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int color) {
        mProgressColor = color;
        mProgressBar.getIndeterminateDrawable().setColorFilter(mProgressColor, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public int getTextColor() {
        return mTextView.getCurrentTextColor();
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public float getTextSize() {
        return mTextView.getTextSize();
    }

    public void setTextSize(float textSize) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }
}
