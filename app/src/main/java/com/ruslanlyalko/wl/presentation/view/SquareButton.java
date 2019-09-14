package com.ruslanlyalko.wl.presentation.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.ruslanlyalko.wl.R;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

public class SquareButton extends AppCompatTextView {

    private static final String PROPERTY_RADIUS = "radius";
    private static final String PROPERTY_WIDTH = "width";
    private Paint paint = new Paint();
    private int maxRadius;
    private int minRadius;
    private boolean isProgressShown;
    private int width;
    private int radius;
    private ValueAnimator animator;
    private CharSequence text;

    public SquareButton(final Context context) {
        super(context);
        text = getText();
    }

    public SquareButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        text = getText();
    }

    public SquareButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        text = getText();
    }

    private int dp2px(final float dimension) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimension,
                getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        if (width == 0) width = getWidth();
        if (minRadius == 0) {
            minRadius = (int) getResources().getDimension(R.dimen.card_radius);
            radius = minRadius;
        }
        maxRadius = getHeight() / 2;
        int half = getWidth() / 2;
        int halfWidth = width / 2;
        int leftTopX = half - halfWidth;
        int rightBotX = half + halfWidth;
        canvas.drawRoundRect(leftTopX, 0, rightBotX, getHeight(), radius, radius, paint);
        super.onDraw(canvas);
    }

    public void showProgress(boolean show) {
        if (isProgressShown == show) return;
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        isProgressShown = show;
        if (show) {
            setEnabled(false);
            setText("");
        } else {
            setEnabled(true);
            setText(text);
        }
        PropertyValuesHolder propertyRadius = PropertyValuesHolder.ofInt(PROPERTY_RADIUS, show ? minRadius : maxRadius, show ? maxRadius : minRadius);
        PropertyValuesHolder propertyWidth = PropertyValuesHolder.ofInt(PROPERTY_WIDTH, show ? getWidth() : getHeight(), show ? getHeight() : getWidth());
        animator = new ValueAnimator();
        animator.setValues(propertyWidth, propertyRadius);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            radius = (int) animation.getAnimatedValue(PROPERTY_RADIUS);
            width = (int) animation.getAnimatedValue(PROPERTY_WIDTH);
            invalidate();
        });
        animator.start();
    }

    public boolean isProgressShown() {
        return isProgressShown;
    }
}
