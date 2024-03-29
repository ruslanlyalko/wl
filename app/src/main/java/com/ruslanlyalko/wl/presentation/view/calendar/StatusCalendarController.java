package com.ruslanlyalko.wl.presentation.view.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.ruslanlyalko.wl.R;
import com.ruslanlyalko.wl.presentation.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.ruslanlyalko.wl.presentation.view.calendar.StatusCalendarView.FILL_LARGE_INDICATOR;
import static com.ruslanlyalko.wl.presentation.view.calendar.StatusCalendarView.NO_FILL_LARGE_INDICATOR;
import static com.ruslanlyalko.wl.presentation.view.calendar.StatusCalendarView.SMALL_INDICATOR;

public class StatusCalendarController {

    public static final int IDLE = 0;
    public static final int EXPOSE_CALENDAR_ANIMATION = 1;
    public static final int EXPAND_COLLAPSE_CALENDAR = 2;
    public static final int ANIMATE_INDICATORS = 3;
    static final int MAX_COEFFICIENT = 40;
    private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;
    private static final int LAST_FLING_THRESHOLD_MILLIS = 300;
    private static final int DAYS_IN_WEEK = 7;
    private static final float SNAP_VELOCITY_DIP_PER_SECOND = 400;
    private static final float ANIMATION_SCREEN_SET_DURATION_MILLIS = 700;
    private int eventIndicatorStyle = SMALL_INDICATOR;
    private int currentDayIndicatorStyle = FILL_LARGE_INDICATOR;
    private int currentSelectedDayIndicatorStyle = FILL_LARGE_INDICATOR;
    private int paddingWidth = 40;
    private int paddingHeight = 40;
    private int textHeight;
    private int textWidth;
    private int widthPerDay;
    private int monthsScrolledSoFar;
    private int heightPerDay;
    private int textSize = 30;
    private int width;
    private int height;
    private int paddingRight;
    private int paddingLeft;
    private int maximumVelocity;
    private int densityAdjustedSnapVelocity;
    private int distanceThresholdForAutoScroll;
    private int targetHeight;
    private int dayPadding;
    private int animationStatus = 0;
    private int firstDayOfWeekToDraw = Calendar.MONDAY;
    private float xIndicatorOffset;
    private float multiDayIndicatorStrokeWidth;
    private float bigCircleIndicatorRadius;
    private float smallIndicatorRadius;
    private float lineIndicatorRadius;
    private float growFactor = 0f;
    private float screenDensity = 1;
    private float growfactorIndicator;
    private float distanceX;
    private long lastAutoScrollFromFling;

    private boolean useThreeLetterAbbreviation = false;
    private boolean isSmoothScrolling;
    private boolean isScrolling;
    private boolean shouldDrawDaysHeader = true;
    private boolean shouldDrawIndicatorsBelowSelectedDays = false;
    private boolean displayOtherMonthDays = false;
    private boolean shouldSelectFirstDayOfMonthOnScroll = true;

    private StatusCalendarView.StatusCalendarViewListener listener;
    private VelocityTracker velocityTracker = null;
    private StatusCalendarController.Direction currentDirection = StatusCalendarController.Direction.NONE;
    private Date currentDate = new Date();
    private Locale locale;
    private Calendar lastCalender;
    private Calendar currentCalender;
    private Calendar todayCalender;
    private Calendar calendarWithFirstDayOfMonth;
    private Calendar eventsCalendar;
    private EventsContainer eventsContainer;
    private PointF accumulatedScrollOffset = new PointF();
    private OverScroller scroller;
    private Paint dayPaint = new Paint();
    private Paint background = new Paint();
    private Rect textSizeRect;
    private String[] dayColumnNames;

    // colors
    private int multiEventIndicatorColor;
    private int currentDayBackgroundColor;
    private int currentDayTextColor;
    private int headerDayTextColor;
    private int calenderTextColor;
    private int currentSelectedDayBackgroundColor;
    private int currentSelectedDayTextColor;
    private int calenderBackgroundColor = Color.WHITE;
    private int otherMonthDaysTextColor;
    private TimeZone timeZone;
    private int coefficient;

    /**
     * Only used in onDrawCurrentMonth to temporarily calculate previous month days
     */
    private Calendar tempPreviousMonthCalendar;

    StatusCalendarController(Paint dayPaint, OverScroller scroller, Rect textSizeRect, AttributeSet attrs,
                             Context context, int currentDayBackgroundColor, int calenderTextColor,
                             int currentSelectedDayBackgroundColor, VelocityTracker velocityTracker,
                             int multiEventIndicatorColor, EventsContainer eventsContainer,
                             Locale locale, TimeZone timeZone) {
        this.dayPaint = dayPaint;
        this.scroller = scroller;
        this.textSizeRect = textSizeRect;
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.calenderTextColor = calenderTextColor;
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
        this.otherMonthDaysTextColor = calenderTextColor;
        this.velocityTracker = velocityTracker;
        this.multiEventIndicatorColor = multiEventIndicatorColor;
        this.eventsContainer = eventsContainer;
        this.locale = locale;
        this.timeZone = timeZone;
        this.displayOtherMonthDays = false;
        loadAttributes(attrs, context);
        init(context);
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatusCalendarView, 0, 0);
            try {
                currentDayBackgroundColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
                calenderTextColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarTextColor, calenderTextColor);
                currentDayTextColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarCurrentDayTextColor, calenderTextColor);
                headerDayTextColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarHeaderDaysTextColor, calenderTextColor);
                otherMonthDaysTextColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarOtherMonthDaysTextColor, otherMonthDaysTextColor);
                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor);
                currentSelectedDayTextColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarCurrentSelectedDayTextColor, calenderTextColor);
                calenderBackgroundColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarBackgroundColor, calenderBackgroundColor);
                multiEventIndicatorColor = typedArray.getColor(R.styleable.StatusCalendarView_statusCalendarMultiEventIndicatorColor, multiEventIndicatorColor);
                textSize = typedArray.getDimensionPixelSize(R.styleable.StatusCalendarView_statusCalendarTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.StatusCalendarView_statusCalendarTargetHeight,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight, context.getResources().getDisplayMetrics()));
                dayPadding = typedArray.getDimensionPixelSize(R.styleable.StatusCalendarView_statusCalendarDayPadding,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dayPadding, context.getResources().getDisplayMetrics()));
                eventIndicatorStyle = typedArray.getInt(R.styleable.StatusCalendarView_statusCalendarEventIndicatorStyle, SMALL_INDICATOR);
                currentDayIndicatorStyle = typedArray.getInt(R.styleable.StatusCalendarView_statusCalendarCurrentDayIndicatorStyle, FILL_LARGE_INDICATOR);
                currentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.StatusCalendarView_statusCalendarCurrentSelectedDayIndicatorStyle, FILL_LARGE_INDICATOR);
                displayOtherMonthDays = typedArray.getBoolean(R.styleable.StatusCalendarView_statusCalendarDisplayOtherMonthDays, displayOtherMonthDays);
                shouldSelectFirstDayOfMonthOnScroll = typedArray.getBoolean(R.styleable.StatusCalendarView_statusCalendarShouldSelectFirstDayOfMonthOnScroll, shouldSelectFirstDayOfMonthOnScroll);
            } finally {
                typedArray.recycle();
            }
        }
    }

    private void init(Context context) {
        currentCalender = Calendar.getInstance(timeZone, locale);
        lastCalender = Calendar.getInstance(timeZone, locale);
        todayCalender = Calendar.getInstance(timeZone, locale);
        calendarWithFirstDayOfMonth = Calendar.getInstance(timeZone, locale);
        eventsCalendar = Calendar.getInstance(timeZone, locale);
        tempPreviousMonthCalendar = Calendar.getInstance(timeZone, locale);
        // make setMinimalDaysInFirstWeek same across android versions
        eventsCalendar.setMinimalDaysInFirstWeek(1);
        calendarWithFirstDayOfMonth.setMinimalDaysInFirstWeek(1);
        todayCalender.setMinimalDaysInFirstWeek(1);
        currentCalender.setMinimalDaysInFirstWeek(1);
        lastCalender.setMinimalDaysInFirstWeek(1);
        tempPreviousMonthCalendar.setMinimalDaysInFirstWeek(1);
        setFirstDayOfWeek(firstDayOfWeekToDraw);
        setUseWeekDayAbbreviation(false);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(Typeface.SANS_SERIF);
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calenderTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), textSizeRect);
        textHeight = textSizeRect.height() * 3;
        textWidth = textSizeRect.width() * 2;
        todayCalender.setTime(new Date());
        setToMidnight(todayCalender);
        currentCalender.setTime(currentDate);
        lastCalender.setTime(currentDate);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        initScreenDensityRelatedValues(context);
        xIndicatorOffset = 3.5f * screenDensity;
        //scale small indicator by screen density
        lineIndicatorRadius = 1.5f * screenDensity;
        smallIndicatorRadius = 3f * screenDensity;
        //just set a default growFactor to draw full calendar when initialised
        growFactor = Integer.MAX_VALUE;
        coefficient = MAX_COEFFICIENT;
    }

    private void initScreenDensityRelatedValues(Context context) {
        if (context != null) {
            screenDensity = context.getResources().getDisplayMetrics().density;
            final ViewConfiguration configuration = ViewConfiguration
                    .get(context);
            densityAdjustedSnapVelocity = (int) (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND);
            maximumVelocity = configuration.getScaledMaximumFlingVelocity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            multiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        }
    }

    private void setCalenderToFirstDayOfMonth(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        setMonthOffset(calendarWithFirstDayOfMonth, currentDate, scrollOffset, monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
    }

    private void setMonthOffset(Calendar calendarWithFirstDayOfMonth, Date currentDate, int scrollOffset, int monthOffset) {
        calendarWithFirstDayOfMonth.setTime(currentDate);
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, scrollOffset + monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MINUTE, 0);
        calendarWithFirstDayOfMonth.set(Calendar.SECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MILLISECOND, 0);
    }

    void setShouldSelectFirstDayOfMonthOnScroll(boolean shouldSelectFirstDayOfMonthOnScroll) {
        this.shouldSelectFirstDayOfMonthOnScroll = shouldSelectFirstDayOfMonthOnScroll;
    }

    void setDisplayOtherMonthDays(boolean displayOtherMonthDays) {
        this.displayOtherMonthDays = displayOtherMonthDays;
    }

    void shouldDrawIndicatorsBelowSelectedDays(boolean shouldDrawIndicatorsBelowSelectedDays) {
        this.shouldDrawIndicatorsBelowSelectedDays = shouldDrawIndicatorsBelowSelectedDays;
    }

    void setCurrentDayIndicatorStyle(int currentDayIndicatorStyle) {
        this.currentDayIndicatorStyle = currentDayIndicatorStyle;
    }

    void setEventIndicatorStyle(int eventIndicatorStyle) {
        this.eventIndicatorStyle = eventIndicatorStyle;
    }

    void setCurrentSelectedDayIndicatorStyle(int currentSelectedDayIndicatorStyle) {
        this.currentSelectedDayIndicatorStyle = currentSelectedDayIndicatorStyle;
    }

    float getScreenDensity() {
        return screenDensity;
    }

    float getDayIndicatorRadius() {
        return bigCircleIndicatorRadius;
    }

    float getGrowFactorIndicator() {
        return growfactorIndicator;
    }

    void setGrowFactorIndicator(float growfactorIndicator) {
        this.growfactorIndicator = growfactorIndicator;
    }

    void setAnimationStatus(int animationStatus) {
        this.animationStatus = animationStatus;
    }

    int getTargetHeight() {
        return targetHeight;
    }

    void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public int getDayPadding() {
        return dayPadding;
    }

    public void setDayPadding(final int dayPadding) {
        this.dayPadding = dayPadding;
    }

    int getWidth() {
        return width;
    }

    void setListener(StatusCalendarView.StatusCalendarViewListener listener) {
        this.listener = listener;
    }

    void removeAllEvents() {
        eventsContainer.removeAllEvents();
    }

    void setFirstDayOfWeek(int day) {
        if (day < 1 || day > 7) {
            throw new IllegalArgumentException("Day must be an int between 1 and 7 or DAY_OF_WEEK from Java Calendar class. For more information please see Calendar.DAY_OF_WEEK.");
        }
        this.firstDayOfWeekToDraw = day;
        setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        eventsCalendar.setFirstDayOfWeek(day);
        calendarWithFirstDayOfMonth.setFirstDayOfWeek(day);
        todayCalender.setFirstDayOfWeek(day);
        currentCalender.setFirstDayOfWeek(day);
        tempPreviousMonthCalendar.setFirstDayOfWeek(day);
    }

    void setCurrentSelectedDayBackgroundColor(int currentSelectedDayBackgroundColor) {
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor;
    }

    void setCurrentSelectedDayTextColor(int currentSelectedDayTextColor) {
        this.currentSelectedDayTextColor = currentSelectedDayTextColor;
    }

    void setCalenderBackgroundColor(int calenderBackgroundColor) {
        this.calenderBackgroundColor = calenderBackgroundColor;
    }

    void setCurrentDayBackgroundColor(int currentDayBackgroundColor) {
        this.currentDayBackgroundColor = currentDayBackgroundColor;
    }

    void setCurrentDayTextColor(int currentDayTextColor) {
        this.currentDayTextColor = currentDayTextColor;
    }

    public void setHeaderDayTextColor(final int headerDayTextColor) {
        this.headerDayTextColor = headerDayTextColor;
    }

    void showNextMonth() {
        monthsScrolledSoFar = monthsScrolledSoFar - 1;
        accumulatedScrollOffset.x = monthsScrolledSoFar * width;
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.getTime(), 0, 1);
            setCurrentDate(calendarWithFirstDayOfMonth.getTime());
        }
        performMonthScrollCallback();
    }

    void showPreviousMonth() {
        monthsScrolledSoFar = monthsScrolledSoFar + 1;
        accumulatedScrollOffset.x = monthsScrolledSoFar * width;
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.getTime(), 0, -1);
            setCurrentDate(calendarWithFirstDayOfMonth.getTime());
        }
        performMonthScrollCallback();
    }

    void setLocale(TimeZone timeZone, Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("Locale cannot be null.");
        }
        if (timeZone == null) {
            throw new IllegalArgumentException("TimeZone cannot be null.");
        }
        this.locale = locale;
        this.timeZone = timeZone;
        this.eventsContainer = new EventsContainer(Calendar.getInstance(this.timeZone, this.locale));
        // passing null will not re-init density related values - and that's ok
        init(null);
    }

    void setUseWeekDayAbbreviation(boolean useThreeLetterAbbreviation) {
        this.useThreeLetterAbbreviation = useThreeLetterAbbreviation;
        this.dayColumnNames = WeekUtils.getWeekdayNames(locale, firstDayOfWeekToDraw, this.useThreeLetterAbbreviation);
    }

    void setDayColumnNames(String[] dayColumnNames) {
        if (dayColumnNames == null || dayColumnNames.length != 7) {
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }

    void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader) {
        this.shouldDrawDaysHeader = shouldDrawDaysHeader;
    }

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
        widthPerDay = (width) / DAYS_IN_WEEK;
        heightPerDay = targetHeight > 0 ? targetHeight / 7 : height / 7;
        this.width = width;
        this.distanceThresholdForAutoScroll = (int) (width * 0.50);
        this.height = height;
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;
        //makes easier to find radius
        bigCircleIndicatorRadius = getInterpolatedBigCircleIndicator();
        // scale the selected day indicators slightly so that event indicators can be drawn below
        bigCircleIndicatorRadius = shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == SMALL_INDICATOR ? bigCircleIndicatorRadius * 0.75f : bigCircleIndicatorRadius;
    }

    //assume square around each day of width and height = heightPerDay and get diagonal line length
    //interpolate height and radius
    //https://en.wikipedia.org/wiki/Linear_interpolation
    private float getInterpolatedBigCircleIndicator() {
        float x0 = textSizeRect.height();
        float x1 = heightPerDay; // take into account indicator offset
        float x = (x1 + textSizeRect.height()) / 2f; // pick a point which is almost half way through heightPerDay and textSizeRect
        double y1 = 0.5 * Math.sqrt((x1 * x1) + (x1 * x1));
        double y0 = 0.5 * Math.sqrt((x0 * x0) + (x0 * x0));
        return (float) (y0 + ((y1 - y0) * ((x - x0) / (x1 - x0))));
    }

    void onDraw(Canvas canvas) {
        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        calculateXPositionOffset();
        if (animationStatus == EXPOSE_CALENDAR_ANIMATION) {
            drawCalendarWhileAnimating(canvas);
        } else if (animationStatus == ANIMATE_INDICATORS) {
            drawCalendarWhileAnimatingIndicators(canvas);
        } else {
            drawCalenderBackground(canvas);
            drawScrollableCalender(canvas);
        }
    }

    private void drawCalendarWhileAnimatingIndicators(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, growFactor, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(Color.WHITE);
        drawScrollableCalender(canvas);
    }

    private void drawCalendarWhileAnimating(Canvas canvas) {
        background.setColor(calenderBackgroundColor);
        background.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, growFactor, background);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(Color.WHITE);
        drawScrollableCalender(canvas);
    }

    void onSingleTapUp(MotionEvent e) {
        // Don't handle single tap when calendar is scrolling and is not stationary
        if (isScrolling()) {
            return;
        }
        int dayColumn = Math.round((paddingLeft + e.getX() - paddingWidth - paddingRight) / widthPerDay);
        int dayRow = Math.round((e.getY() - paddingHeight) / heightPerDay);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        int firstDayOfMonth = getDayOfWeek(calendarWithFirstDayOfMonth);
        int dayOfMonth = ((dayRow - 1) * 7 + dayColumn) - firstDayOfMonth;
        if (dayOfMonth < calendarWithFirstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                && dayOfMonth >= 0) {
            calendarWithFirstDayOfMonth.add(Calendar.DATE, dayOfMonth);
            currentCalender.setTimeInMillis(calendarWithFirstDayOfMonth.getTimeInMillis());
            performOnDayClickCallback(currentCalender.getTime());
        }
    }

    // Add a little leeway buy checking if amount scrolled is almost same as expected scroll
    // as it maybe off by a few pixels
    private boolean isScrolling() {
        float scrolledX = Math.abs(accumulatedScrollOffset.x);
        int expectedScrollX = Math.abs(width * monthsScrolledSoFar);
        return scrolledX < expectedScrollX - 5 || scrolledX > expectedScrollX + 5;
    }

    private void performOnDayClickCallback(Date date) {
        if (listener != null) {
            listener.onDayClick(date);
        }
    }

    boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //ignore scrolling callback if already smooth scrolling
        if (isSmoothScrolling) {
            return true;
        }
        if (currentDirection == StatusCalendarController.Direction.NONE) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                currentDirection = StatusCalendarController.Direction.HORIZONTAL;
            } else {
                currentDirection = StatusCalendarController.Direction.VERTICAL;
            }
        }
        isScrolling = true;
        this.distanceX = distanceX;
        return true;
    }

    boolean onTouch(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
            }
            isSmoothScrolling = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            velocityTracker.addMovement(event);
            velocityTracker.computeCurrentVelocity(500);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            handleHorizontalScrolling();
            velocityTracker.recycle();
            velocityTracker.clear();
            velocityTracker = null;
            isScrolling = false;
        }
        return false;
    }

    private void snapBackScroller() {
        float remainingScrollAfterFingerLifted1 = (accumulatedScrollOffset.x - (monthsScrolledSoFar * width));
        scroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) -remainingScrollAfterFingerLifted1, 0);
    }

    private void handleHorizontalScrolling() {
        int velocityX = computeVelocity();
        handleSmoothScrolling(velocityX);
        currentDirection = StatusCalendarController.Direction.NONE;
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        if (calendarWithFirstDayOfMonth.get(Calendar.MONTH) != currentCalender.get(Calendar.MONTH) && shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(currentCalender, currentDate, -monthsScrolledSoFar, 0);
        }
    }

    private int computeVelocity() {
        velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity);
        return (int) velocityTracker.getXVelocity();
    }

    private void handleSmoothScrolling(int velocityX) {
        int distanceScrolled = (int) (accumulatedScrollOffset.x - (width * monthsScrolledSoFar));
        boolean isEnoughTimeElapsedSinceLastSmoothScroll = System.currentTimeMillis() - lastAutoScrollFromFling > LAST_FLING_THRESHOLD_MILLIS;
        if (velocityX > densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollPreviousMonth();
        } else if (velocityX < -densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollNextMonth();
        } else if (isScrolling && distanceScrolled > distanceThresholdForAutoScroll) {
            scrollPreviousMonth();
        } else if (isScrolling && distanceScrolled < -distanceThresholdForAutoScroll) {
            scrollNextMonth();
        } else {
            isSmoothScrolling = false;
            snapBackScroller();
        }
    }

    private void scrollNextMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis();
        monthsScrolledSoFar = monthsScrolledSoFar - 1;
        performScroll();
        isSmoothScrolling = true;
        performMonthScrollCallback();
    }

    private void scrollPreviousMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis();
        monthsScrolledSoFar = monthsScrolledSoFar + 1;
        performScroll();
        isSmoothScrolling = true;
        performMonthScrollCallback();
    }

    private void performMonthScrollCallback() {
        if (listener != null) {
            listener.onMonthScroll(getFirstDayOfCurrentMonth());
        }
    }

    private void performScroll() {
        int targetScroll = monthsScrolledSoFar * width;
        float remainingScrollAfterFingerLifted = targetScroll - accumulatedScrollOffset.x;
        scroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) (remainingScrollAfterFingerLifted), 0,
                (int) (Math.abs((int) (remainingScrollAfterFingerLifted)) / (float) width * ANIMATION_SCREEN_SET_DURATION_MILLIS));
    }

    int getHeightPerDay() {
        return heightPerDay;
    }

    int getWeekNumberForCurrentMonth() {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        calendar.setTime(currentDate);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -monthsScrolledSoFar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setToMidnight(calendar);
        return calendar.getTime();
    }

    void setCurrentDate(Date dateTimeMonth) {
        distanceX = 0;
        monthsScrolledSoFar = 0;
        accumulatedScrollOffset.x = 0;
        scroller.startScroll(0, 0, 0, 0);
        lastCalender.setTime(currentDate);
        currentDate = new Date(dateTimeMonth.getTime());
        currentCalender.setTime(currentDate);
        todayCalender = Calendar.getInstance(timeZone, locale);
        setToMidnight(currentCalender);
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(final int coefficient) {
        this.coefficient = coefficient;
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    void addEvent(Event event) {
        eventsContainer.addEvent(event);
    }

    void addEvents(List<Event> events) {
        eventsContainer.addEvents(events);
    }

    List<Event> getCalendarEventsFor(long epochMillis) {
        return eventsContainer.getEventsFor(epochMillis);
    }

    List<Event> getCalendarEventsForMonth(long epochMillis) {
        return eventsContainer.getEventsForMonth(epochMillis);
    }

    void removeEventsFor(long epochMillis) {
        eventsContainer.removeEventByEpochMillis(epochMillis);
    }

    void removeEvent(Event event) {
        eventsContainer.removeEvent(event);
    }

    void removeEvents(List<Event> events) {
        eventsContainer.removeEvents(events);
    }

    void setGrowProgress(float grow) {
        growFactor = grow;
    }

    float getGrowFactor() {
        return growFactor;
    }

    boolean onDown(MotionEvent e) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.forceFinished(true);
        return true;
    }

    boolean computeScroll() {
        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.getCurrX();
            return true;
        }
        return false;
    }

    private void drawScrollableCalender(Canvas canvas) {
        drawPreviousMonth(canvas);
        drawCurrentMonth(canvas);
        drawNextMonth(canvas);
    }

    private void drawNextMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar + 1)));
    }

    private void drawCurrentMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0);
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar);
    }

    private void drawPreviousMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, -1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void calculateXPositionOffset() {
        if (currentDirection == StatusCalendarController.Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX;
        }
    }

    private void drawCalenderBackground(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
    }

    void drawEvents(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        int currentMonth = currentMonthToDrawCalender.get(Calendar.MONTH);
        List<Events> uniqEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, currentMonthToDrawCalender.get(Calendar.YEAR));
        boolean shouldDrawCurrentDayCircle = currentMonth == todayCalender.get(Calendar.MONTH);
        boolean shouldDrawSelectedDayCircle = currentMonth == currentCalender.get(Calendar.MONTH);
        boolean shouldDrawLastSelectedDayCircle = currentMonth == lastCalender.get(Calendar.MONTH);
        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);
        int currentYear = todayCalender.get(Calendar.YEAR);
        int selectedDayOfMonth = currentCalender.get(Calendar.DAY_OF_MONTH);
        int lastSelectedDayOfMonth = lastCalender.get(Calendar.DAY_OF_MONTH);
        if (uniqEvents != null) {
            for (int i = 0; i < uniqEvents.size(); i++) {
                Events events = uniqEvents.get(i);
                long timeMillis = events.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);
                int dayOfWeek = getDayOfWeek(eventsCalendar);
                int weekNumberForMonth = eventsCalendar.get(Calendar.WEEK_OF_MONTH);
                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
                float yPosition = weekNumberForMonth * heightPerDay + (paddingHeight * 0.8f);
                if (((animationStatus == EXPOSE_CALENDAR_ANIMATION || animationStatus == ANIMATE_INDICATORS) && xPosition >= growFactor) || yPosition >= growFactor) {
                    // only draw small event indicators if enough of the calendar is exposed
                    continue;
                } else if (animationStatus == EXPAND_COLLAPSE_CALENDAR && yPosition >= growFactor) {
                    // expanding animation, just draw event indicators if enough of the calendar is visible
                    continue;
                } else if (animationStatus == EXPOSE_CALENDAR_ANIMATION && (eventIndicatorStyle == FILL_LARGE_INDICATOR || eventIndicatorStyle == NO_FILL_LARGE_INDICATOR)) {
                    // Don't draw large indicators during expose animation, until animation is done
                    continue;
                }
                List<Event> eventsList = events.getEvents();
                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                int eventYear = eventsCalendar.get(Calendar.YEAR);
                boolean isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && (todayDayOfMonth == dayOfMonth) && (eventYear == currentYear);
                boolean isCurrentSelectedDay = shouldDrawSelectedDayCircle && (selectedDayOfMonth == dayOfMonth);
                boolean isLastSelectedDay = shouldDrawLastSelectedDayCircle && (lastSelectedDayOfMonth == dayOfMonth);
                if (shouldDrawIndicatorsBelowSelectedDays || (!shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay) || animationStatus == EXPOSE_CALENDAR_ANIMATION) {
                    if (eventIndicatorStyle == SMALL_INDICATOR) {
                        Event event = eventsList.get(0);
                        yPosition = yPosition + bigCircleIndicatorRadius;
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += (2 * smallIndicatorRadius);
                        }
                        drawEventIndicatorCircle(canvas, xPosition, yPosition, event.getColor());
                    } else {
                        drawEventIndicatorRect(canvas, xPosition, yPosition, eventsList.get(0).getColor(), isCurrentSelectedDay, isLastSelectedDay,
                                sameAsPrev(uniqEvents, eventsList.get(0)));
                    }
                }
            }
        }
    }

    // zero based indexes used internally so instead of returning range of 1-7 like calendar class
    // it returns 0-6 where 0 is Sunday instead of 1
    int getDayOfWeek(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeekToDraw;
        dayOfWeek = dayOfWeek < 0 ? 7 + dayOfWeek : dayOfWeek;
        return dayOfWeek;
    }

    void drawMonth(Canvas canvas, Calendar monthToDrawCalender, int offset) {
        drawEvents(canvas, monthToDrawCalender, offset);
        //offset by one because we want to start from Monday
        int firstDayOfMonth = getDayOfWeek(monthToDrawCalender);
        boolean isSameMonthAsToday = monthToDrawCalender.get(Calendar.MONTH) == todayCalender.get(Calendar.MONTH);
        boolean isSameYearAsToday = monthToDrawCalender.get(Calendar.YEAR) == todayCalender.get(Calendar.YEAR);
        boolean isSameMonthAsCurrentCalendar = monthToDrawCalender.get(Calendar.MONTH) == currentCalender.get(Calendar.MONTH) &&
                monthToDrawCalender.get(Calendar.YEAR) == currentCalender.get(Calendar.YEAR);
        int todayDayOfMonth = todayCalender.get(Calendar.DAY_OF_MONTH);
        boolean isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION;
        int maximumMonthDay = monthToDrawCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
        tempPreviousMonthCalendar.setTimeInMillis(monthToDrawCalender.getTimeInMillis());
        tempPreviousMonthCalendar.add(Calendar.MONTH, -1);
        int maximumPreviousMonthDay = tempPreviousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int dayColumn = 0, dayRow = 0; dayColumn <= 6; dayRow++) {
            if (dayRow == 7) {
                dayRow = 0;
                if (dayColumn <= 6) {
                    dayColumn++;
                }
            }
            if (dayColumn == dayColumnNames.length) {
                break;
            }
            float xPosition = widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
            float yPosition = dayRow * heightPerDay + paddingHeight;
            if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor) {
                // don't draw days if animating expose or indicators
                continue;
            }
            if (dayRow == 0) {
                // first row, so draw the first letter of the day
                if (shouldDrawDaysHeader) {
                    dayPaint.setColor(headerDayTextColor);
                    dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(headerDayTextColor);
                    canvas.drawText(dayColumnNames[dayColumn], xPosition, paddingHeight, dayPaint);
                    dayPaint.setTypeface(Typeface.DEFAULT);
                }
            } else {
                int day = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;
                int defaultCalenderTextColorToUse = calenderTextColor;
                if (currentCalender.get(Calendar.DAY_OF_MONTH) == day && isSameMonthAsCurrentCalendar && !isAnimatingWithExpose) {
                    dayPaint.setColor(currentSelectedDayBackgroundColor);
                    if (!DateUtils.dateEquals(currentCalender.getTime(), lastCalender.getTime()))
                        dayPaint.setAlpha((int) (255f * ((float) coefficient / MAX_COEFFICIENT)));
//                        drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor);
                    if (eventIndicatorStyle == FILL_LARGE_INDICATOR) {
                        if (noEventsForDay(currentCalender)) {
                            drawRect(canvas, xPosition, yPosition - (textHeight / 6));
                        }
                    } else {
                        drawCircle(canvas, bigCircleIndicatorRadius, xPosition, yPosition - (textHeight / 6));
                    }
                    dayPaint.setAlpha(255);
                    defaultCalenderTextColorToUse = currentSelectedDayTextColor;
                } else if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day && !isAnimatingWithExpose) {
//                        drawDayCircleIndicator(currentDayIndicatorStyle, canvas, xPosition, yPosition, currentDayBackgroundColor);
                    dayPaint.setColor(currentDayBackgroundColor);
                    if (eventIndicatorStyle == FILL_LARGE_INDICATOR) {
                        if (noEventsForDay(todayCalender)) {
                            drawRect(canvas, xPosition, yPosition - (textHeight / 6));
                        }
                    } else {
                        drawCircle(canvas, bigCircleIndicatorRadius, xPosition, yPosition - (textHeight / 6));
                    }
                    defaultCalenderTextColorToUse = currentDayTextColor;
                }
                if (day <= 0) {
                    if (displayOtherMonthDays) {
                        // Display day month before
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(otherMonthDaysTextColor);
                        canvas.drawText(String.valueOf(maximumPreviousMonthDay + day), xPosition, yPosition, dayPaint);
                    }
                } else if (day > maximumMonthDay) {
                    if (displayOtherMonthDays) {
                        // Display day month after
                        dayPaint.setStyle(Paint.Style.FILL);
                        dayPaint.setColor(otherMonthDaysTextColor);
                        canvas.drawText(String.valueOf(day - maximumMonthDay), xPosition, yPosition, dayPaint);
                    }
                } else {
                    dayPaint.setStyle(Paint.Style.FILL);
                    dayPaint.setColor(defaultCalenderTextColorToUse);
                    canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                }
            }
        }
    }

    private boolean sameAsPrev(final List<Events> uniqEvents, final Event event) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTimeInMillis(event.getTimeInMillis());
        if (prevCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            return false;
        prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
        Calendar itemCalendar = Calendar.getInstance();
        for (Events events : uniqEvents) {
            itemCalendar.setTimeInMillis(events.getTimeInMillis());
            if (itemCalendar.get(Calendar.DAY_OF_YEAR) == prevCalendar.get(Calendar.DAY_OF_YEAR)
                    && itemCalendar.get(Calendar.YEAR) == prevCalendar.get(Calendar.YEAR)
                    && event.getColor() == events.getEvents().get(0).getColor())
                return true;
        }
        return false;
    }

    private boolean noEventsForDay(final Calendar currentCalender) {
        int currentMonth = currentCalender.get(Calendar.MONTH);
        int currentYear = currentCalender.get(Calendar.YEAR);
        List<Events> uniqEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, currentYear);
        Calendar itemCalendar = Calendar.getInstance();
        if (uniqEvents != null)
            for (Events events : uniqEvents) {
                itemCalendar.setTimeInMillis(events.getTimeInMillis());
                if (itemCalendar.get(Calendar.DAY_OF_YEAR) == currentCalender.get(Calendar.DAY_OF_YEAR)
                        && itemCalendar.get(Calendar.YEAR) == currentCalender.get(Calendar.YEAR))
                    return false;
            }
        return true;
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color) {
        drawDayCircleIndicator(indicatorStyle, canvas, x, y, color, 1);
    }

    private void drawDayCircleIndicator(int indicatorStyle, Canvas canvas, float x, float y, int color, float circleScale) {
        float strokeWidth = dayPaint.getStrokeWidth();
        if (indicatorStyle == NO_FILL_LARGE_INDICATOR) {
            dayPaint.setStrokeWidth(2 * screenDensity);
            dayPaint.setStyle(Paint.Style.STROKE);
        } else {
            dayPaint.setStyle(Paint.Style.FILL);
        }
        drawCircle(canvas, x, y, color, circleScale);
        dayPaint.setStrokeWidth(strokeWidth);
        dayPaint.setStyle(Paint.Style.FILL);
    }

    // Draw Circle on certain days to highlight them
    private void drawCircle(Canvas canvas, float x, float y, int color, float circleScale) {
        dayPaint.setColor(color);
        if (animationStatus == ANIMATE_INDICATORS) {
            float maxRadius = circleScale * bigCircleIndicatorRadius * 1.4f;
            drawCircle(canvas, growfactorIndicator > maxRadius ? maxRadius : growfactorIndicator, x, y - (textHeight / 6));
        } else {
            drawCircle(canvas, circleScale * bigCircleIndicatorRadius, x, y - (textHeight / 6));
        }
    }

    private void drawEventIndicatorCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        if (eventIndicatorStyle == SMALL_INDICATOR) {
            dayPaint.setStyle(Paint.Style.FILL);
            drawCircle(canvas, smallIndicatorRadius, x, y);
        } else if (eventIndicatorStyle == NO_FILL_LARGE_INDICATOR) {
            dayPaint.setStyle(Paint.Style.STROKE);
            drawDayCircleIndicator(NO_FILL_LARGE_INDICATOR, canvas, x, y, color);
        } else if (eventIndicatorStyle == FILL_LARGE_INDICATOR) {
            drawDayCircleIndicator(FILL_LARGE_INDICATOR, canvas, x, y, color);
        }
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        canvas.drawCircle(x, y, radius, dayPaint);
    }

    private void drawRect(Canvas canvas, float x, float y) {
        float top = y - (heightPerDay / 2f) + dayPadding;
        float left = x - (widthPerDay / 2f) + dayPadding;
        float right = x + (widthPerDay / 2f) - dayPadding;
        float bottom = y + (heightPerDay / 2f) - dayPadding;
        canvas.drawRoundRect(left, top, right, bottom, smallIndicatorRadius, smallIndicatorRadius, dayPaint);
    }

    private void drawEventIndicatorRect(Canvas canvas, float x, float y, int color, final boolean isCurrentSelectedDay,
                                        final boolean isLastCurrentSelectedDay, final boolean isSameAsPrev) {
        dayPaint.setColor(color);
        dayPaint.setStyle(Paint.Style.FILL);
        float topMin = y - (heightPerDay / 2f) + dayPadding;
        float topMax = y + (heightPerDay / 2f) - dayPadding - (2 * lineIndicatorRadius);
        float diff = topMax - topMin;
        float top;
        if (isCurrentSelectedDay)
            top = topMax - (diff * (coefficient) / MAX_COEFFICIENT);
        else if (isLastCurrentSelectedDay)
            top = topMin + (diff * (coefficient) / MAX_COEFFICIENT);
        else
            top = topMax;
        float left = x - (widthPerDay / 2f) + dayPadding;
        float right = x + (widthPerDay / 2f) - dayPadding;
        float bottom = y + (heightPerDay / 2f) - dayPadding;
        canvas.drawRoundRect(left, top, right, bottom, lineIndicatorRadius, lineIndicatorRadius, dayPaint);
        if (isSameAsPrev) {//&& !isCurrentSelectedDay && !isLastCurrentSelectedDay) {
            canvas.drawRoundRect(left - (3 * dayPadding), topMax, left + dayPadding, bottom, lineIndicatorRadius, lineIndicatorRadius, dayPaint);
        }
    }

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }
}
