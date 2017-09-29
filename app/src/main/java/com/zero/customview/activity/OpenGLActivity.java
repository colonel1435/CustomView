package com.zero.customview.activity;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Scroller;

import com.zero.customview.R;
import com.zero.customview.utils.RotateUtils;
import com.zero.customview.view.opengl.renderer.TriangleGLRenderer;
import com.zero.customview.view.opengl.renderer.WubaGLRenderer;

import io.reactivex.Flowable;

public class OpenGLActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private final float SCALE_STEP = 0.001f;
    private final float ROTATE_STEP = 25;
    private final float ROTATE_MAX = (float) Math.PI;
    private GLSurfaceView glSurfaceView;
    private WubaGLRenderer glRenderer;
    private TriangleGLRenderer triangleGLRenderer;
    private float rotateDegreen = 0;
    private GestureDetector mGestureDetector;
    private DefaultTouchListener mTouchListener;
    private Scroller mScroller;
    private int mWidth;
    private double mPerimeter = 2 * Math.PI;

    private static final int DISTANCE = 50;
    private static final int VELOCITY = 0;
    private  double nLenStart = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkOpenGLSupport()) {
            glSurfaceView = new GLSurfaceView(this);
            glRenderer = new WubaGLRenderer(this);
            glSurfaceView.setRenderer(glRenderer);
            mTouchListener = new DefaultTouchListener();
            glSurfaceView.setOnTouchListener(mTouchListener);
            glSurfaceView.setFocusable(true);
            glSurfaceView.setClickable(true);
            glSurfaceView.setLongClickable(true);
            mGestureDetector = new GestureDetector(this, new DefaultGestureDetector());
            mScroller = new Scroller(this);
            setContentView(glSurfaceView);

            glSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    glSurfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mWidth = glSurfaceView.getWidth();
                    glRenderer.setMaxWidth(mWidth);
                }
            });
        } else {
            setContentView(R.layout.activity_open_gl);
        }
    }

    private boolean checkOpenGLSupport() {
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        boolean es2Support = info.reqGlEsVersion >= 0x2000;
        boolean isEmulator = Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86"));

        return es2Support || isEmulator;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    private class DefaultTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int nCnt = event.getPointerCount();
            if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)
            {
                int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));


                nLenStart = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
            }else if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP  && 2 == nCnt)
            {
                int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
                double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                float scaleRate;
                if (nLenEnd > nLenStart) {
                    scaleRate = (float) (nLenEnd - nLenStart)/mWidth  * 10;
                    glRenderer.XScalef=glRenderer.XScalef + scaleRate*SCALE_STEP;
                    glRenderer.YScalef=glRenderer.YScalef + scaleRate*SCALE_STEP;
                    glRenderer.ZScalef=glRenderer.ZScalef + scaleRate*SCALE_STEP;
                } else {
                    scaleRate = (float) (nLenStart - nLenEnd)/mWidth * 10;
                    glRenderer.XScalef=glRenderer.XScalef - scaleRate*SCALE_STEP;
                    glRenderer.YScalef=glRenderer.YScalef - scaleRate*SCALE_STEP;
                    glRenderer.ZScalef=glRenderer.ZScalef - scaleRate*SCALE_STEP;
                }

                Log.d(TAG, "onTouch: scaleX -> " + glRenderer.XScalef
                            + " scaleY -> " + glRenderer.YScalef
                            + " scaleZ -> " + glRenderer.ZScalef);
            }
            return mGestureDetector.onTouchEvent(event);
        }
    }


    private class DefaultGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float rotateRate = 0f;
            if (e1.getX() - e2.getX() > DISTANCE && Math.abs(velocityX) > VELOCITY) {
                rotateRate = (e1.getX() - e2.getX()) / mWidth * 10;
                glRenderer.yRotate = glRenderer.yRotate - rotateRate * ROTATE_STEP;
            } else if (e2.getX() - e1.getX() > DISTANCE && Math.abs(velocityX) > VELOCITY) {
                rotateRate = (e2.getX() - e1.getX()) / mWidth * 10;
                glRenderer.yRotate = glRenderer.yRotate + rotateRate * ROTATE_STEP;
            } else if (e1.getY() - e2.getY() > DISTANCE && Math.abs(velocityY) > VELOCITY) {
                rotateRate = (e1.getY() - e2.getY()) / mWidth * 10;
                glRenderer.xRotate = glRenderer.xRotate + rotateRate * ROTATE_STEP;
            } else if (e2.getY() - e1.getY() > DISTANCE && Math.abs(velocityY) > VELOCITY) {
                rotateRate = (e2.getY() - e1.getY()) / mWidth * 10;
                glRenderer.xRotate = glRenderer.xRotate - rotateRate * ROTATE_STEP;
            }
            Log.i(TAG, "rotate -> " + rotateRate + " xRotate -> " + glRenderer.xRotate
                    + " yRotate -> " + glRenderer.yRotate);

            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }
}
