package com.ruslanlyalko.wl.presentation.view.calendar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;

class AnimationHandler {

    private static final int HEIGHT_ANIM_DURATION_MILLIS = 650;
    private static final int INDICATOR_ANIM_DURATION_MILLIS = 600;
    private boolean isAnimating = false;
    private StatusCalendarController statusCalendarController;
    private StatusCalendarView mStatusCalendarViewView;
    private StatusCalendarView.StatusCalendarAnimationListener statusCalendarAnimationListener;

    AnimationHandler(StatusCalendarController statusCalendarController, StatusCalendarView statusCalendarViewView) {
        this.statusCalendarController = statusCalendarController;
        this.mStatusCalendarViewView = statusCalendarViewView;
    }

    void setStatusCalendarAnimationListener(StatusCalendarView.StatusCalendarAnimationListener compactCalendarAnimationListener) {
        this.statusCalendarAnimationListener = compactCalendarAnimationListener;
    }

    void openCalendar() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        statusCalendarController.setAnimationStatus(StatusCalendarController.EXPAND_COLLAPSE_CALENDAR);
        setUpAnimationLisForOpen(heightAnim);
        mStatusCalendarViewView.getLayoutParams().height = 0;
        mStatusCalendarViewView.requestLayout();
        mStatusCalendarViewView.startAnimation(heightAnim);
    }

    void closeCalendar() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        setUpAnimationLisForClose(heightAnim);
        statusCalendarController.setAnimationStatus(StatusCalendarController.EXPAND_COLLAPSE_CALENDAR);
        mStatusCalendarViewView.getLayoutParams().height = mStatusCalendarViewView.getHeight();
        mStatusCalendarViewView.requestLayout();
        mStatusCalendarViewView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        final Animator indicatorAnim = getIndicatorAnimator(1f, statusCalendarController.getDayIndicatorRadius());
        final Animation heightAnim = getExposeCollapsingAnimation(true);
        mStatusCalendarViewView.getLayoutParams().height = 0;
        mStatusCalendarViewView.requestLayout();
        setUpAnimationLisForExposeOpen(indicatorAnim, heightAnim);
        mStatusCalendarViewView.startAnimation(heightAnim);
    }

    void closeCalendarWithAnimation() {
        if (isAnimating) {
            return;
        }
        isAnimating = true;
        final Animator indicatorAnim = getIndicatorAnimator(statusCalendarController.getDayIndicatorRadius(), 1f);
        final Animation heightAnim = getExposeCollapsingAnimation(false);
        mStatusCalendarViewView.getLayoutParams().height = mStatusCalendarViewView.getHeight();
        mStatusCalendarViewView.requestLayout();
        setUpAnimationLisForExposeClose(indicatorAnim, heightAnim);
        mStatusCalendarViewView.startAnimation(heightAnim);
    }

    private void setUpAnimationLisForExposeOpen(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.EXPOSE_CALENDAR_ANIMATION);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                indicatorAnim.start();
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.ANIMATE_INDICATORS);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.IDLE);
                onOpen();
                isAnimating = false;
            }
        });
    }

    private void setUpAnimationLisForExposeClose(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.EXPOSE_CALENDAR_ANIMATION);
                indicatorAnim.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.IDLE);
                onClose();
                isAnimating = false;
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                statusCalendarController.setAnimationStatus(StatusCalendarController.ANIMATE_INDICATORS);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
    }

    @NonNull
    private Animation getExposeCollapsingAnimation(final boolean isCollapsing) {
        Animation heightAnim = getCollapsingAnimation(isCollapsing);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return heightAnim;
    }

    @NonNull
    private Animation getCollapsingAnimation(boolean isCollapsing) {
        return new CollapsingAnimation(mStatusCalendarViewView, statusCalendarController, statusCalendarController.getTargetHeight(), getTargetGrowRadius(), isCollapsing);
    }

    @NonNull
    private Animator getIndicatorAnimator(float from, float to) {
        ValueAnimator animIndicator = ValueAnimator.ofFloat(from, to);
        animIndicator.setDuration(INDICATOR_ANIM_DURATION_MILLIS);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                statusCalendarController.setGrowFactorIndicator((Float) animation.getAnimatedValue());
                mStatusCalendarViewView.invalidate();
            }
        });
        return animIndicator;
    }

    private int getTargetGrowRadius() {
        int heightSq = statusCalendarController.getTargetHeight() * statusCalendarController.getTargetHeight();
        int widthSq = statusCalendarController.getWidth() * statusCalendarController.getWidth();
        return (int) (0.5 * Math.sqrt(heightSq + widthSq));
    }

    private void onOpen() {
        if (statusCalendarAnimationListener != null) {
            statusCalendarAnimationListener.onOpened();
        }
    }

    private void onClose() {
        if (statusCalendarAnimationListener != null) {
            statusCalendarAnimationListener.onClosed();
        }
    }

    private void setUpAnimationLisForOpen(Animation openAnimation) {
        openAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                onOpen();
                isAnimating = false;
            }
        });
    }

    private void setUpAnimationLisForClose(Animation openAnimation) {
        openAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                super.onAnimationEnd(animation);
                onClose();
                isAnimating = false;
            }
        });
    }

    public boolean isAnimating() {
        return isAnimating;
    }
}

