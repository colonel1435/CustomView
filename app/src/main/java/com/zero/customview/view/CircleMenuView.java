package com.zero.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zero.customview.R;
import com.zero.customview.utils.DisplayUtils;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/23 0023 11:06
 */

public class CircleMenuView extends View {

    private static final String TAG = CircleMenuView.class.getSimpleName()+"@wumin";
    public static final int DEFAULT_WIDTH = 480;

    enum TYPE {NONE, RIGHT, BOTTOM, LEFT, TOP, CENTER}

    private static final float DEFAULT_TEXT_SIZE = 12;
    private static final String DEFAULT_TEXT_CENTER = "OK";
    private static final String DEFAULT_TEXT_TOP = "Top";
    private static final String DEFAULT_TEXT_BOTTOM = "Bottom";
    private static final String DEFAULT_TEXT_LEFT = "Left";
    private static final String DEFAULT_TEXT_RIGHT = "Right";
    private static final int DEFAULT_ANIMATION_DURATION = 100;
    private static final int DEFAULT_SCALE_INDEX = 10;
    private static final int DEFAULT_OUTTER_ANGLE = 84;
    private static final int DEFAULT_INNER_ANGLE = 80;
    private static final int DEFAULT_SPACE_ANGLE = 10;
    private Context mContext;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint.FontMetricsInt fontMetrics;

    private Path centerPath;
    private Path topPath;
    private Path bottomPath;
    private Path leftPath;
    private Path rightPath;

    private Region centerRegion;
    private Region topRegion;
    private Region bottomRegion;
    private Region leftRegion;
    private Region rightRegion;
    private Region globalRegion;

    private RectF outCircle;
    private RectF innerCircle;
    private String centerText;
    private String topText;
    private String bottomText;
    private String leftText;
    private String rightText;
    private int touchType = -1;
    private int currentType = -1;

    private int regionColor;
    private int touchColor;
    private int textColor;
    private int touchDrawColor;
    private float textSize;

    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;

    private int outRadius;
    private int innerRadius;
    private float centerRadius;
    private float mOutAngle;
    private float mInnerAngle;

    private boolean enableRotate;

    private float scaleIndex;
    private onMenuClickListener mClickListener;
    public CircleMenuView(Context context) {
        this(context, null);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleMenuView);
        touchColor = ta.getColor(R.styleable.CircleMenuView_circle_menu_touchColor,
                Color.YELLOW);
        regionColor = ta.getColor(R.styleable.CircleMenuView_circle_menu_backgoundColor,
                Color.BLUE);
        textColor = ta.getColor(R.styleable.CircleMenuView_circle_menu_textColor,
                Color.WHITE);
        textSize = ta.getDimension(R.styleable.CircleMenuView_circle_menu_textSize,
                DEFAULT_TEXT_SIZE);
        centerText = ta.getString(R.styleable.CircleMenuView_circle_menu_textCenter);
        topText = ta.getString(R.styleable.CircleMenuView_circle_menu_textTop);
        bottomText = ta.getString(R.styleable.CircleMenuView_circle_menu_textBottom);
        leftText = ta.getString(R.styleable.CircleMenuView_circle_menu_textLeft);
        rightText = ta.getString(R.styleable.CircleMenuView_circle_menu_textRight);
        enableRotate = ta.getBoolean(R.styleable.CircleMenuView_circle_menu_enableRotate, true);
        ta.recycle();
        initView();
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(regionColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(DisplayUtils.dip2px(mContext, textSize));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = mTextPaint.getFontMetricsInt();

        topPath = new Path();
        bottomPath = new Path();
        leftPath = new Path();
        rightPath = new Path();
        centerPath = new Path();

        topRegion = new Region();
        bottomRegion = new Region();
        leftRegion = new Region();
        rightRegion = new Region();
        centerRegion = new Region();
        scaleIndex = DisplayUtils.dip2px(mContext, DEFAULT_SCALE_INDEX);
        mOutAngle = DEFAULT_OUTTER_ANGLE;
        mInnerAngle = -DEFAULT_INNER_ANGLE;

        touchDrawColor = touchColor;
        checkMenuText();
    }

    private void checkMenuText() {
        if (TextUtils.isEmpty(centerText)) {
            centerText = DEFAULT_TEXT_CENTER;
        }
        if (TextUtils.isEmpty(topText)) {
            topText = DEFAULT_TEXT_TOP;
        }
        if (TextUtils.isEmpty(bottomText)) {
            bottomText = DEFAULT_TEXT_BOTTOM;
        }
        if (TextUtils.isEmpty(leftText)) {
            leftText = DEFAULT_TEXT_LEFT;
        }
        if (TextUtils.isEmpty(rightText)) {
            rightText = DEFAULT_TEXT_RIGHT;
        }
    }

    public void setMenuClickListener(onMenuClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    private int measureSize(int measureSpec) {
        int defaultSize = DEFAULT_WIDTH;
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w / 2 + getPaddingLeft() - getPaddingRight();
        mCenterY = h / 2 + getPaddingTop() - getPaddingBottom();
        globalRegion = new Region(0, 0, mWidth, mHeight);
        int minWidth = Math.min(mWidth, mHeight);
        minWidth *= 0.8;
        centerRadius = minWidth / 5;
        outRadius = minWidth / 2;
        outCircle = new RectF(mCenterX - outRadius, mCenterY - outRadius,
                                mCenterX + outRadius, mCenterY + outRadius);
        innerRadius = minWidth / 4;
        innerCircle = new RectF(mCenterX - innerRadius, mCenterY - innerRadius,
                                    mCenterX + innerRadius, mCenterY + innerRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSector(canvas);
        drawText(canvas);
    }

    private void drawSector(Canvas canvas) {
        setupSector();
        mPaint.setColor(regionColor);
        canvas.drawPath(topPath, mPaint);
        canvas.drawPath(bottomPath, mPaint);
        canvas.drawPath(leftPath, mPaint);
        canvas.drawPath(rightPath, mPaint);
        canvas.drawPath(centerPath, mPaint);

        Path touchPath = getTouchPath();
        if (null != touchPath) {
            mPaint.setColor(touchDrawColor);
            canvas.drawPath(touchPath, mPaint);
        }
    }

    private void setupSector() {
        centerPath.addCircle(mCenterX, mCenterY, centerRadius, Path.Direction.CW);
        centerRegion.setPath(centerPath, globalRegion);
        rightPath = getSectorWithType(TYPE.RIGHT.ordinal());
        rightRegion.setPath(rightPath, globalRegion);
        bottomPath = getSectorWithType(TYPE.BOTTOM.ordinal());
        bottomRegion.setPath(bottomPath, globalRegion);
        leftPath = getSectorWithType(TYPE.LEFT.ordinal());
        leftRegion.setPath(leftPath, globalRegion);
        topPath = getSectorWithType(TYPE.TOP.ordinal());
        topRegion.setPath(topPath, globalRegion);
    }

    private Path getSectorWithType(int type) {
        Path path = new Path();
        RectF outRectf = new RectF(outCircle);
        RectF inRectf = new RectF(innerCircle);
        float startAngle = mInnerAngle / 2;
        if (type == touchType) {
            outRectf.inset(-scaleIndex, -scaleIndex);
        }
        path.addArc(outRectf, startAngle + (type - 1) * DEFAULT_INNER_ANGLE +
                (type - 1) * DEFAULT_SPACE_ANGLE, mOutAngle);
        path.arcTo(inRectf, startAngle + type * DEFAULT_INNER_ANGLE +
                (type - 1) * DEFAULT_SPACE_ANGLE, mInnerAngle);
        path.close();
        return path;
    }

    private Path getTouchPath() {
        Path touchPath = null;
        if (touchType == TYPE.TOP.ordinal()) {
            touchPath = topPath;
        }else if(touchType == TYPE.BOTTOM.ordinal()) {
            touchPath = bottomPath;
        } else if (touchType == TYPE.LEFT.ordinal()) {
            touchPath = leftPath;
        } else if (touchType == TYPE.RIGHT.ordinal()) {
            touchPath = rightPath;
        } else if (touchType == TYPE.CENTER.ordinal()) {
            touchPath = centerPath;
        }

        return touchPath;
    }

    private void drawText(Canvas canvas) {
        float baselineY = (fontMetrics.descent + fontMetrics.ascent)/2;
        int offset = outRadius * 3 / 4;
        canvas.drawText(topText, mCenterX, mCenterY - offset - baselineY, mTextPaint);
        canvas.drawText(bottomText,  mCenterX, mCenterY + offset - baselineY, mTextPaint);
        canvas.drawText(leftText, mCenterX - offset, mCenterY - baselineY, mTextPaint);
        canvas.drawText(rightText, mCenterX + offset, mCenterY - baselineY, mTextPaint);
        canvas.drawText(centerText, mCenterX, mCenterY - baselineY, mTextPaint);
    }

    private int getTouchType(int x, int y) {
        int type = TYPE.NONE.ordinal();
        if (centerRegion.contains(x, y)) {
            type = TYPE.CENTER.ordinal();
        } else if (topRegion.contains(x, y)) {
            type = TYPE.TOP.ordinal();
        } else if (bottomRegion.contains(x, y)) {
            type = TYPE.BOTTOM.ordinal();
        } else if (leftRegion.contains(x, y)) {
            type = TYPE.LEFT.ordinal();
        } else if (rightRegion.contains(x, y)) {
            type = TYPE.RIGHT.ordinal();
        }

        return type;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchType = getTouchType(x, y);
                currentType = touchType;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                currentType = getTouchType(x, y);
                break;
            case MotionEvent.ACTION_UP:
                currentType = getTouchType(x, y);
                if (currentType == touchType
                        && currentType != -1
                        && mClickListener != null) {
                    startClickListener(currentType);
                }
                currentType = touchType = -1;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }

        return true;
    }

    private void startClickListener(int currentPosition) {
        if (currentPosition == TYPE.CENTER.ordinal()) {
            mClickListener.onCenterClick();
        } else if (currentPosition == TYPE.TOP.ordinal()) {
            mClickListener.onTopClick();
        } else if (currentPosition == TYPE.BOTTOM.ordinal()) {
            mClickListener.onBottomClick();
        } else if (currentPosition == TYPE.LEFT.ordinal()) {
            mClickListener.onLeftClick();
        } else if (currentPosition == TYPE.RIGHT.ordinal()) {
            mClickListener.onRightClick();
        }
    }

    private void setScaleIndex(float scale) {
        this.scaleIndex = scale;
        Log.d(TAG, "setScaleIndex: " + scaleIndex);
        invalidate();
    }

    public int getTouchDrawColor() {
        return touchDrawColor;
    }

    public void setTouchDrawColor(int touchDrawColor) {
        this.touchDrawColor = touchDrawColor;
        invalidate();
    }

    public CircleMenuView setRegionColor(int regionColor) {
        this.regionColor = regionColor;
        return this;
    }

    public CircleMenuView setTouchColor(int touchColor) {
        this.touchColor = touchColor;
        return this;
    }

    public CircleMenuView setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public CircleMenuView setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public CircleMenuView setCenterText(String centerText) {
        this.centerText = centerText;
        return this;
    }

    public CircleMenuView setTopText(String topText) {
        this.topText = topText;
        return this;
    }

    public CircleMenuView setBottomText(String bottomText) {
        this.bottomText = bottomText;
        return this;
    }

    public CircleMenuView setLeftText(String leftText) {
        this.leftText = leftText;
        return this;
    }

    public CircleMenuView setRightText(String rightText) {
        this.rightText = rightText;
        return this;
    }

    public void build() {
        requestLayout();
    }

    public interface onMenuClickListener {
        /**
        *   Center click listener
        * @param
        * @return
        */
        void onCenterClick();
        /**
        *   Top click listener
        * @param
        * @return
        */
        void onTopClick();
        /**
        *   Bottom click listener
        * @param
        * @return
        */
        void onBottomClick();
        /**
        *   Left click listener
        * @param
        * @return
        */
        void onLeftClick();
        /**
        *   Right click listener
        * @param
        * @return
        */
        void onRightClick();
    }
}
