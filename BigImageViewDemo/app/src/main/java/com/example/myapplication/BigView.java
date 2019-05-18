package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.IOException;
import java.io.InputStream;

public class BigView  extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {


    private Rect rect;
    private BitmapFactory.Options options;
    private GestureDetector mGestureDetector;
    private Scroller mScroller;
    private int mImageWidth;
    private int mImageHeight;
    private BitmapRegionDecoder mDecoder;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private Bitmap bitmap;
    public BigView(Context context) {
        this(context,null,0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //制定区域加载
        rect = new Rect();
        //需要服用
        options = new BitmapFactory.Options();
        //手势识别
        mGestureDetector = new GestureDetector(context, this);
        setOnTouchListener( this);
        //滑动帮助
        mScroller = new Scroller( context);
    }


    public void setImage(InputStream is){
        //获取图片信息 款高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);
        mImageHeight = options.outHeight;
        mImageWidth = options.outWidth;
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        // 获取图片区域解码器
        try {
            mDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();;   //重新绘制

    }

    /**
     * 测量
     * 在测量的时候  我们需要把我们的内存区域获取 存入到mRect中
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取测量view的大小
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        //确定重新加载大图区域
        rect.left = 0;
        rect.top = 0;
        rect.right= mImageWidth;
        //获取一个缩放因子
        mScale = mViewWidth/(float)mImageWidth;
        rect.bottom = (int) (mViewHeight/mScale);

    }

    /**
     * 绘制内容
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //解码器为null直接返回
        if(null == mDecoder){
            return;
        }
        //服用上一张图片
        options.inBitmap = bitmap;
        //解码制定的区域
        bitmap = mDecoder.decodeRegion(rect, options);
        //把得到的矩阵  大小的内存进行缩放， 得到view的大小
        Matrix matrix = new Matrix();
        matrix.setScale(mScale, mScale);
        //画出来
        canvas.drawBitmap(bitmap, matrix, null);




    }

    @Override
    public boolean onDown(MotionEvent e) {
        //如果移动还没有停止， 强制停止
        if (!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }
        //继续接受后续事件
        return true;
    }

    /**
     * 处理滑动的事件
     * @param e1    按下
     * @param e2   移动
     * @param distanceX  左右移动
     * @param distanceY  上下移动
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //上下移动的时候, 需要改变显示区域  更改rect
        rect.offset(0, (int) distanceY);
        //处理移动到两端的问题
        if (rect.bottom > mImageHeight){
            rect.bottom = mImageHeight;
            rect.top = mImageHeight -(int)(mViewHeight/mScale);

        }
        if (rect.top <0){
            rect.top = 0;
            rect.bottom = (int) (mViewHeight/mScale);
        }
        invalidate();
        return false;
    }

    /**
     * 处理惯性滑动
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mScroller.fling(0, rect.top, 0 , (int)-velocityY, 0,0,0,mImageHeight - (int)(mViewHeight/mScale));
        return false;
    }

    /**
     * 使用上一个接口计算出的结果
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.isFinished()){
            return;
        }
        //true表示没有滑动结束
        if (mScroller.computeScrollOffset()){
            rect.top = mScroller.getCurrY();
            rect.bottom = rect.top + (int)(mViewHeight/mScale);
            invalidate();;
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }



    @Override
    public void onLongPress(MotionEvent e) {

    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
