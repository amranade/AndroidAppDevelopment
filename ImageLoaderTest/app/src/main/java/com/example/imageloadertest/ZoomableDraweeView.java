package com.example.imageloadertest;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

public class ZoomableDraweeView extends DraweeView {
    private static final SpringSystem ZOOM_SPRING_SYSTEM = SpringSystem.create();
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener;
    private GestureDetector mGestureDetector;
    private float mZoomPivotX = 0.f, mZoomPivotY = 0.f;
    private Spring mSpring;

    public ZoomableDraweeView(Context context) {
        super(context);
        init();
    }

    public ZoomableDraweeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private class SpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            mScaleFactor = (float) spring.getCurrentValue();
            invalidate();
        }
    }

    private void init() {
        mOnScaleGestureListener = new ScaleListsner();
        mSpring = ZOOM_SPRING_SYSTEM.createSpring()
                .setSpringConfig(new SpringConfig(120.0, 20.0));
        mSpring.setOvershootClampingEnabled(false);
        mSpring.addListener(new SpringListener());
        mSpring.setCurrentValue(1.f);
        mScaleDetector = new ScaleGestureDetector(getContext(), mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                if (isReset()) {
                    zoom(2.f, e.getX(), e.getY());
                } else {
                    zoom(1.f, e.getX(), e.getY());
                }
//                invalidate();
                return super.onDoubleTap(e);
            }
        });
    }

    private class ScaleListsner extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1.f, Math.min(mScaleFactor, 5.0f));
            mSpring.setEndValue(mScaleFactor);
//            invalidate();
            return true;
        }
    }

    private boolean isReset() {
        return mScaleFactor == 1.f;
    }

    private void zoom(float zoom, float pivotX, float pivotY) {
        mScaleFactor = zoom;
        mZoomPivotX = pivotX;
        mZoomPivotY = pivotY;
        mSpring.setEndValue(mScaleFactor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();
        setScaleX(mScaleFactor);
        setScaleY(mScaleFactor);
        setPivotX(mZoomPivotX);
        setPivotY(mZoomPivotY);
//        canvas.scale(mScaleFactor, mScaleFactor);
//        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2 &&
                event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            int actionIndex = event.getActionIndex();
            mZoomPivotX = event.getX(actionIndex);
            mZoomPivotY = event.getY(actionIndex);
        }
        return mGestureDetector.onTouchEvent(event) || mScaleDetector.onTouchEvent(event);
//        int pointerCount = event.getPointerCount();
//        String pointerEventDebug = "Action: " + event.getAction() + " Count: " + pointerCount;
//        for (int i = 0;i<pointerCount; ++i) {
//            int pointerId = event.getPointerId(i);
//            pointerEventDebug += (" point_" + i + event.getX(pointerId) + "," + event.getY(pointerId));
//        }
//        Log.d("MyDebug", pointerEventDebug);
//        return true;
    }
}
