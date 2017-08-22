package com.example.myapplication;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;

import static android.content.ContentValues.TAG;


public class DoubleSeekBar extends View {

    private static final float THUMB_SHADOW = 3.5f;
    private static final float Y_OFFSET = 1.75f;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final float HIT_SCOPE_RATIO = 1.2f;

    private int mThumbRadius;//点击图标圆的半径
    private int mThumbColor;
    private int mProgressColor;
    private int mProgressBackgroundColor;
    private int mProgressWidth;
    private int mTextColor;
    private int mTextSize;


    public Point mStartPoint = new Point();//progress 起点
    public  float mFirstThumbRatio = 0.0f;//相对于mProgress的比例
    public  float mSecondThumbRatio = 1.0f;
    public  float mThirdThumbRatio = 0.0f;
    public  float mFourthThumbRatio = 1.0f;


    private RectF mFirstThumb = new RectF();
    private RectF mSecondThumb = new RectF();
    private RectF mThirdThumb = new RectF();
    private RectF mFourthThumb = new RectF();

    private int mProgressLength;
    private int mThumbShadowRadius;
    private int mYOffset;


    public String mFirstPrompt;
    public String mSecondPrompt;
    public String mThirdPrompt;
    public String mFourthPrompt;


    private Paint mProgressPaint = new Paint();
    private Paint mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private  Paint paint_Line =new Paint();
    private  Paint paint_Point=new Paint();

    private onSeekBarChangeListener mOnSeekBarChangeListener;
    private onSeekBar_VChangeListener mOnSeekBar_VChangeListener;

    public DoubleSeekBar(Context context) {
        this(context, null, 0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoubleSeekBar, defStyleAttr, R.style.DoubleSeekBar);
        mThumbRadius = a.getDimensionPixelSize(R.styleable.DoubleSeekBar_DB_ThumbRadius, 0);
        mThumbColor = a.getColor(R.styleable.DoubleSeekBar_DB_ThumbColor, 0);
        mTextColor = a.getColor(R.styleable.DoubleSeekBar_DB_TextColor, 0);
        mProgressColor = a.getColor(R.styleable.DoubleSeekBar_DB_ProgressColor, 0);
        mProgressBackgroundColor = a.getColor(R.styleable.DoubleSeekBar_DB_ProgressBackgroundColor, 0);
        mTextSize = a.getDimensionPixelSize(R.styleable.DoubleSeekBar_DB_TextSize, 0);
        mProgressWidth = a.getDimensionPixelOffset(R.styleable.DoubleSeekBar_DB_ProgressWidth, 0);
        a.recycle();
        //初始化画笔
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        paint_Point.setColor(Color.BLACK);//画点颜色
        //paint_Line=a.getColor(R.styleable.DoubleSeekBar_DB_ProgressColor, 0);//画线颜色

        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mThumbShadowRadius = (int) (getResources().getDisplayMetrics().density * THUMB_SHADOW);
        mYOffset = (int) (getResources().getDisplayMetrics().density * Y_OFFSET);
        if(!isInEditMode()) {
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, mThumbPaint);
            mThumbPaint.setShadowLayer(mThumbShadowRadius, 0, mYOffset, KEY_SHADOW_COLOR);
        }
        mThumbPaint.setColor(mThumbColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //just care height
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //int suggestHeight = (int) (3 * mThumbRadius + mThumbShadowRadius - fontMetrics.top + fontMetrics.bottom + getPaddingTop() + getPaddingBottom());
        int suggestHeight =5;//高度定死
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                resolveSizeAndState(suggestHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mStartPoint.set(getPaddingLeft() + mThumbRadius, h - getPaddingBottom() - mThumbRadius - mThumbShadowRadius);
        mStartPoint.set(150,230);
        //mProgressLength = w - getPaddingLeft() - getPaddingRight() - 2 * mThumbRadius;
        mProgressLength=200;//长度定死

        mFirstThumb.set(mStartPoint.x + mProgressLength * mFirstThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x + mProgressLength * mFirstThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);

        mSecondThumb.set(mStartPoint.x + mProgressLength * mSecondThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x + mProgressLength * mSecondThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);

        mThirdThumb.set(mStartPoint.x - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - mProgressLength * mThirdThumbRatio - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - mProgressLength * mThirdThumbRatio + HIT_SCOPE_RATIO * mThumbRadius);

        mFourthThumb.set(mStartPoint.x  - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y- mProgressLength * mFourthThumbRatio - HIT_SCOPE_RATIO * mThumbRadius,
                mStartPoint.x  + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y- mProgressLength * mFourthThumbRatio + HIT_SCOPE_RATIO * mThumbRadius);

    }

    @Override
    public void onDraw(Canvas canvas) {

        //horiSeekBar===================================================
        int y = mStartPoint.y;
        int firstX = mStartPoint.x;
        int secondX = (int) mFirstThumb.centerX();
        int thirdX = (int) mSecondThumb.centerX();
        //draw progress
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(firstX, y, secondX, y, mProgressPaint);
        mProgressPaint.setColor(mProgressColor);
        canvas.drawLine(secondX, y, thirdX, y, mProgressPaint);
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(thirdX, y, firstX + mProgressLength, y, mProgressPaint);

        //draw Thumb
        canvas.drawCircle(secondX, y, mThumbRadius, mThumbPaint);
        canvas.drawCircle(thirdX, y, mThumbRadius, mThumbPaint);

        mFirstPrompt = ratio2DateString(mFirstThumbRatio);
        mSecondPrompt = ratio2DateString(mSecondThumbRatio);

        //verticalSeekBar=============================================
//        int y = mStartPoint.y;
//        int firstX = mStartPoint.x;
        int secondY = (int) mThirdThumb.centerY();
        int thirdY = (int) mFourthThumb.centerY();
        //draw progress
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(firstX, y, firstX, secondY, mProgressPaint);
        mProgressPaint.setColor(mProgressColor);
        canvas.drawLine(firstX, secondY, firstX, thirdY, mProgressPaint);
        mProgressPaint.setColor(mProgressBackgroundColor);
        canvas.drawLine(firstX, thirdY,firstX, y- mProgressLength, mProgressPaint);

        canvas.drawCircle(firstX, secondY, mThumbRadius, mThumbPaint);
        canvas.drawCircle(firstX, thirdY, mThumbRadius, mThumbPaint);

        mThirdPrompt = ratio2DateString(mThirdThumbRatio);
        mFourthPrompt = ratio2DateString(mFourthThumbRatio);

        //drawpaint and line
        int X_start =(int)mFirstThumb.centerX();
        int Y_start =(int) mThirdThumb.centerY();
        canvas.drawPoint(X_start,Y_start,paint_Point);

        int X_stop =(int) mSecondThumb.centerX();
        int Y_stop =(int) mFourthThumb.centerY();
        canvas.drawPoint(X_stop,Y_stop,paint_Point);

        canvas.drawLine(X_start,Y_start,X_stop,Y_stop,mProgressPaint);
        canvas.drawLine(X_stop,Y_stop,500,Y_stop,mProgressPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.fistThumbRatio = mFirstThumbRatio;
        ss.secondThumbRatio = mSecondThumbRatio;
        ss.thirdThumbRatio=mThirdThumbRatio;
        ss.fourthThumbRatio=mFourthThumbRatio;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            mFirstThumbRatio = ((SavedState) state).fistThumbRatio;
            mSecondThumbRatio = ((SavedState) state).secondThumbRatio;
            mThirdThumbRatio= ((SavedState) state).thirdThumbRatio;
            mFourthThumbRatio= ((SavedState) state).fourthThumbRatio;
            super.onRestoreInstanceState(((SavedState) state).getSuperState());
        }else {
            super.onRestoreInstanceState(state);
        }
    }

    private static final int INVALID_POINTER = -1;


    static class SelectInfo {
        int pointerId = INVALID_POINTER;
        boolean isCaptured = false;

        void invalid() {
            pointerId = INVALID_POINTER;
            isCaptured = false;
        }
    }

    private SelectInfo firstInfo = new SelectInfo();
    private SelectInfo secondInfo = new SelectInfo();
    private SelectInfo thirdInfo = new SelectInfo();
    private SelectInfo fourthInfo = new SelectInfo();


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int firstPointerId = MotionEventCompat.getPointerId(event, 0);
                if (mFirstThumb.contains(x, y)) {
                    firstInfo.pointerId = firstPointerId;
                    firstInfo.isCaptured = true;
                }
                if (mSecondThumb.contains(x, y)) {
                    secondInfo.pointerId = firstPointerId;
                    secondInfo.isCaptured = true;
                    Log.d(TAG, "onTouchEvent:secondInfo.isCaptured = true;");
                }
                if (mThirdThumb.contains(x, y)) {
                    thirdInfo.pointerId = firstPointerId;
                    thirdInfo.isCaptured = true;
                }
                if (mFourthThumb.contains(x, y)) {
                    fourthInfo.pointerId = firstPointerId;
                    fourthInfo.isCaptured = true;
                    Log.d(TAG, "onTouchEvent: fourthInfo.isCaptured = true;");

                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = MotionEventCompat.getActionIndex(event);
                float otherX = MotionEventCompat.getX(event, index);
                float otherY = MotionEventCompat.getY(event, index);
                if (mFirstThumb.contains(otherX, otherY)) {
                    firstInfo.isCaptured = true;
                    firstInfo.pointerId = MotionEventCompat.getPointerId(event, index);
                    Log.d(TAG, "onTouchEvent:  firstInfo.isCaptured = true;");
                }
                if (mSecondThumb.contains(otherX, otherY)) {
                    secondInfo.isCaptured = true;
                    secondInfo.pointerId = MotionEventCompat.getPointerId(event, index);

                }
                if (mThirdThumb.contains(otherX, otherY)) {
                    thirdInfo.isCaptured = true;
                    thirdInfo.pointerId = MotionEventCompat.getPointerId(event, index);
                    Log.d(TAG, "onTouchEvent:  thirdInfo.isCaptured = true;");
                }
                if (mFourthThumb.contains(otherX, otherY)) {
                    fourthInfo.isCaptured = true;
                    fourthInfo.pointerId = MotionEventCompat.getPointerId(event, index);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                if (firstInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, firstInfo.pointerId);
                    float firstX = MotionEventCompat.getX(event, i);
                    if (moveFirstThumb(firstX)) return false;
                }

                if (secondInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, secondInfo.pointerId);
                    float secondX = MotionEventCompat.getX(event, i);
                    if (moveSecondThumb(secondX)) return false;
                }

                if (thirdInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, thirdInfo.pointerId);
                    float firstY = MotionEventCompat.getY(event, i);
                    if (moveThirdThumb(firstY)) return false;
                }

                if (fourthInfo.isCaptured) {
                    int i = MotionEventCompat.findPointerIndex(event, fourthInfo.pointerId);
                    float secondY = MotionEventCompat.getY(event, i);
                    if (moveFourthThumb(secondY)) return false;
                }

                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                int pointerId = MotionEventCompat.getPointerId(event, MotionEventCompat.getActionIndex(event));
                if (firstInfo.pointerId == pointerId) {
                    firstInfo.invalid();
                }
                if (secondInfo.pointerId == pointerId) {
                    secondInfo.invalid();
                }
                if (thirdInfo.pointerId == pointerId) {
                    thirdInfo.invalid();
                }
                if (fourthInfo.pointerId == pointerId) {
                    fourthInfo.invalid();
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                firstInfo.invalid();
                secondInfo.invalid();
                thirdInfo.invalid();
                fourthInfo.invalid();
                break;
        }

        if (firstInfo.isCaptured || secondInfo.isCaptured) {
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(this, mFirstThumbRatio, mSecondThumbRatio);
            }
            invalidate();
        }

        if (thirdInfo.isCaptured || fourthInfo.isCaptured) {
                if (mOnSeekBar_VChangeListener != null) {
                    mOnSeekBar_VChangeListener.onProgress_VChanged(this, mThirdThumbRatio, mFourthThumbRatio);
                }
                invalidate();
        }

            //===========================================================================================
//        if(ViewCompat.isAttachedToWindow(this)){
//            getParent().requestDisallowInterceptTouchEvent(firstInfo.isCaptured || secondInfo.isCaptured);
//        }
            //===========================================================================================

            return firstInfo.isCaptured || secondInfo.isCaptured || thirdInfo.isCaptured || fourthInfo.isCaptured;
        }

    public boolean moveSecondThumb(float x) {
        if (x > mFirstThumb.right + mThumbRadius) {
            mSecondThumbRatio = (x - mStartPoint.x) / mProgressLength;
            if (mSecondThumbRatio > 1) {
                mSecondThumbRatio = 1;
                return true;
            }
           float SoffsetX = x - mSecondThumb.centerX();
            mSecondThumb.offset(SoffsetX, 0);
            Log.d(TAG, "moveSecondThumb: ");
        }
        return false;
    }

    public boolean moveFirstThumb(float x) {
        if (x < mSecondThumb.left - mThumbRadius) {//可以移动的范围
            mFirstThumbRatio = (x - mStartPoint.x) / mProgressLength;
            if (mFirstThumbRatio < 0) {
                mFirstThumbRatio = 0f;
                return true;
            }
           float FoffsetX = x - mFirstThumb.centerX();
            mFirstThumb.offset(FoffsetX, 0);
            Log.d(TAG, "moveFirstThumb: ");

        }
        return false;
    }

    public boolean moveFourthThumb(float y) {
        if (y < mThirdThumb.top - mThumbRadius) {//可以移动的范围
            mFourthThumbRatio = (y - mStartPoint.y) / mProgressLength;
            if (mFourthThumbRatio <-1) {
                mFourthThumbRatio = -1;
                return true;
            }
            float offsetY = y - mFourthThumb.centerY();
            mFourthThumb.offset(0, offsetY);
            Log.d(TAG, "moveFourthThumb: ");
        }
        return false;
    }

    public boolean moveThirdThumb(float y) {
        if (y > mFourthThumb.bottom + mThumbRadius) {//可以移动的范围
            mThirdThumbRatio = (y - mStartPoint.y) / mProgressLength;
            if (mThirdThumbRatio >0) {
                mThirdThumbRatio = 0f;
                return true;
            }
            float offsetY = y - mThirdThumb.centerY();
            mThirdThumb.offset(0, offsetY);
            Log.d(TAG, "moveThirdThumb: ");
        }
        return false;
    }

    /**
     * override this method
     * custom own tip text
     * @param ratio firstThumb and secondThumb range 0.0-1.0;
     * @return
     */
    protected String ratio2DateString(float ratio) {
        StringBuilder builder = new StringBuilder();

        int hour=(int)(100*ratio);
        String string= String.valueOf(hour);
        builder.append(string);

        return builder.toString();
    }
    //=========================================改动前==========================
//    protected String ratio2DateString(float ratio) {
//        StringBuilder builder = new StringBuilder();
//        int currentMinute = (int) (24 * 60 * ratio);
//        String hour = String.valueOf(currentMinute / 60);
//
//        if (hour.length() == 1) {
//            builder.append("0").append(hour);
//        } else {
//            builder.append(hour);
//        }
//        builder.append(":");
//        String minute = String.valueOf(currentMinute % 60);
//
//        if (minute.length() == 1) {
//            builder.append("0").append(minute);
//        } else {
//            builder.append(minute);
//        }
//
//        return builder.toString();
//    }

    public void setOnSeekBarChangeListener(onSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
    }
    public interface onSeekBarChangeListener {
        void onProgressChanged(DoubleSeekBar doubleSeekBar, float firstThumbRatio, float secondThumbRatio);
    }

        public void setOnSeekBarChangeListener(onSeekBar_VChangeListener onSeekBar_VChangeListener) {
        mOnSeekBar_VChangeListener = onSeekBar_VChangeListener;
    }
        public interface onSeekBar_VChangeListener {
        void onProgress_VChanged(DoubleSeekBar doubleSeekBar, float thirdThumbRatio, float fourthThumbRatio);
    }

    /**
     * set first thumb position
     * @param ratio
     */
    public void setFirstThumbRatio(float ratio){
        mFirstThumbRatio = ratio;
        invalidate();
    }

    /**
     * set second thumb position
     * @param ratio
     */
    public void setSecondThumbRatio(float ratio){
        mSecondThumbRatio = ratio;
        invalidate();
    }

    public void setThirdThumbRatio(float ratio){
        mThirdThumbRatio = ratio;
        invalidate();
    }

    public void setFourthThumbRatio(float ratio){
        mFourthThumbRatio = ratio;
        invalidate();
    }



    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        float fistThumbRatio;
        float secondThumbRatio;
        public float thirdThumbRatio;
        public float fourthThumbRatio;

        public SavedState(Parcel source) {
            super(source);
            fistThumbRatio = source.readFloat();
            secondThumbRatio = source.readFloat();
            thirdThumbRatio= source.readFloat();
            fourthThumbRatio= source.readFloat();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(fistThumbRatio);
            out.writeFloat(secondThumbRatio);
            out.writeFloat(thirdThumbRatio);
            out.writeFloat(fourthThumbRatio);
        }
    }


    private Property<DoubleSeekBar,Float> mFirstThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"firstThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mFirstThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mFirstThumbRatio = value;
            object.mFirstThumb.set(mStartPoint.x + mProgressLength * mFirstThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x + mProgressLength * mFirstThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);
            object.invalidate();
        }
    };

    private Property<DoubleSeekBar,Float> mSecondThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"secondThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mSecondThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mSecondThumbRatio = value;
            object.mSecondThumb.set(mStartPoint.x + mProgressLength * mSecondThumbRatio - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x + mProgressLength * mSecondThumbRatio + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y + HIT_SCOPE_RATIO * mThumbRadius);
        }
    };

    private Property<DoubleSeekBar,Float> mThirdThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"thirdThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mThirdThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mThirdThumbRatio = value;
            object.mThirdThumb.set(mStartPoint.x - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - mProgressLength * mThirdThumbRatio - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y - mProgressLength * mThirdThumbRatio + HIT_SCOPE_RATIO * mThumbRadius);
        }
    };

    private Property<DoubleSeekBar,Float> mFourthThumbProperty = new Property<DoubleSeekBar, Float>(Float.class,"fourthThumbRatio") {
        @Override
        public Float get(DoubleSeekBar object) {
            return object.mFourthThumbRatio;
        }

        @Override
        public void set(DoubleSeekBar object, Float value) {
            object.mFourthThumbRatio = value;
            object.mFourthThumb.set(mStartPoint.x  - HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y- mProgressLength * mFourthThumbRatio - HIT_SCOPE_RATIO * mThumbRadius,
                    mStartPoint.x  + HIT_SCOPE_RATIO * mThumbRadius, mStartPoint.y- mProgressLength * mFourthThumbRatio + HIT_SCOPE_RATIO * mThumbRadius);
        }
    };

}
