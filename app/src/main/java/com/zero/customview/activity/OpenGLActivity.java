package com.zero.customview.activity;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.zero.customview.R;
import com.zero.customview.view.opengl.renderer.TriangleGLRenderer;
import com.zero.customview.view.opengl.renderer.WubaGLRenderer;

public class OpenGLActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;
    private WubaGLRenderer glRenderer;
    private TriangleGLRenderer triangleGLRenderer;
    private float rotateDegreen = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkOpenGLSupport()) {
            glSurfaceView = new GLSurfaceView(this);
            glRenderer = new WubaGLRenderer(this);
            glSurfaceView.setRenderer(glRenderer);
            setContentView(glSurfaceView);
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

    public void rotate(float degree) {
        glRenderer.rotate(degree);
        glSurfaceView.invalidate();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rotate(rotateDegreen);
        }
    };

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
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(100);
                            rotateDegreen += 5;
                            handler.sendEmptyMessage(0x001);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();
        }
    }
}
