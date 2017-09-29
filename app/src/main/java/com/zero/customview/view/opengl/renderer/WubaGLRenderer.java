package com.zero.customview.view.opengl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Environment;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;

import com.zero.customview.view.opengl.OpenGLConst;
import com.zero.customview.view.opengl.bean.Model;
import com.zero.customview.view.opengl.bean.Point;
import com.zero.customview.view.opengl.utils.ByteUtils;
import com.zero.customview.view.opengl.utils.STLReader;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/22 0022 17:20
 * Refer: http://blog.csdn.net/huachao1001
 */

public class WubaGLRenderer implements GLSurfaceView.Renderer {
    private Context mContext;
    private List<Model> models = new ArrayList<>();
    private Model model;
    private Point mCenterPoint;
    private Point eye = new Point(0, 0, -3);
    private Point up = new Point(0, 1, 0);
    private Point center = new Point(0, 0, 0);
    private float mScalef = 1;
    private float mDegree = 0;
    float[] ambient = {0.9f, 0.9f, 0.9f, 1.0f,};
    float[] diffuse = {0.5f, 0.5f, 0.5f, 1.0f,};
    float[] specular = {1.0f, 1.0f, 1.0f, 1.0f,};
    float[] lightPosition = {0.5f, 0.5f, 0.5f, 0.0f,};

    float[] materialAmb = {0.4f, 0.4f, 1.0f, 1.0f};
    float[] materialDiff = {0.0f, 0.0f, 1.0f, 1.0f};
    float[] materialSpec = {1.0f, 0.5f, 0.0f, 1.0f};

    public WubaGLRenderer(Context context) {

//        try {
//            mContext = context;
//            model = new STLReader().parserBinStlInAssets(context, "stl/buddha.stl");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            mContext = context;
            STLReader reader = new STLReader();
            String rootDir = Environment.getExternalStorageDirectory() + File.separator + OpenGLConst.STL_ROOT;
            File root = new File(rootDir);
            if (root.exists()) {
                File[] files = root.listFiles();
                for (File file : files) {
                    model = reader.parserBinStlInSdcard(file);
                }
                if (model != null) {
                    models.add(model);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotate(float degree) {
        mDegree = degree;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        gl.glClearColor(0.74f, 0.76f, 0.78f, 0f); /***  colorSilver  ***/
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0f); /***  colorBlack  ***/
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glClearDepthf(1.0f);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glShadeModel(GL10.GL_SMOOTH);
        float r = model.getRadius();
        mScalef = 0.5f / r;
        mCenterPoint = model.getCentrePoint();

        openLight(gl);
        enableMaterial(gl);
//        initConfigData(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        // Set projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset matrix
        gl.glLoadIdentity();
        // Set prespective range
        GLU.gluPerspective(gl, 45.0f, ((float) width) / height, 1f, 100f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // Reset model matrix
        gl.glLoadIdentity();


        // Look at from zero point
        GLU.gluLookAt(gl, eye.x, eye.y, eye.z, center.x,
                center.y, center.z, up.x, up.y, up.z);

        // Roate itself
        gl.glRotatef(mDegree, 0, 1, 0);
        // Scale itself
        gl.glScalef(mScalef, mScalef, mScalef);
        // Translate to zero point
        gl.glTranslatef(-mCenterPoint.x, -mCenterPoint.y,
                -mCenterPoint.z);

        // Enable set normal vector
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // Enable set vertex
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        for (Model model : models) {
            // Set norm vector
            gl.glNormalPointer(GL10.GL_FLOAT, 0, model.getVnormBuffer());
            // Set vertex
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.getVertBuffer());

            // Set color
            gl.glColor4f(0.85f, 0.45f, 0.20f, 0f);
            // Draw triangle
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, model.getFacetCount() * 3);

            // Disable vertex
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            // Disable normal vector
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        }

        /*
        //=================== Texture ==============================//
        for (Model model : models) {
            // Enable texture func
            gl.glEnable(GL10.GL_TEXTURE_2D);
            // Bind texture with ID
            gl.glBindTexture(GL10.GL_TEXTURE_2D, model.getTextureIds()[0]);
            // Enable texture other func.
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            // Drawing
            gl.glNormalPointer(GL10.GL_FLOAT, 0, model.getVnormBuffer());
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.getVertBuffer());
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, model.getTextureBuffer());

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, model.getFacetCount() * 3);

            // Disable texture func,and set ID with 0;
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

            // Disable other func.
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }
        */

    }

    public void openLight(GL10 gl) {

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ByteUtils.floatToBuffer(ambient));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, ByteUtils.floatToBuffer(diffuse));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, ByteUtils.floatToBuffer(specular));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, ByteUtils.floatToBuffer(lightPosition));

    }

    public void enableMaterial(GL10 gl) {

        /***    Environment light   ***/
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ByteUtils.floatToBuffer(materialAmb));
        /***    Scatter light   ***/
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, ByteUtils.floatToBuffer(materialDiff));
        /***    Specular light  ***/
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, ByteUtils.floatToBuffer(materialSpec));

    }

    private void initConfigData(GL10 gl) {
        float r = STLReader.getR(models);
        mScalef = 0.5f / r;
        mCenterPoint = STLReader.getCenter(models);

        for (Model model : models) {
            loadTexture(gl, model, true);
        }

    }

    private void loadTexture(GL10 gl, Model model, boolean isAssets) {
        Log.d("GLRenderer", "Load texture：" + model.getPictureName());
        Bitmap bitmap = null;
        try {
            if (isAssets) {/***    Load from assets  ***/
                bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(model.getPictureName()));
            } else {/***    Load from sd storage    ***/
                bitmap = BitmapFactory.decodeFile(model.getPictureName());
            }
            /***    Create texuture object, and save ID     ***/
            int[] textures = new int[1];
            gl.glGenTextures(1, textures, 0);
            model.setTextureIds(textures);

            /***    Bind empty texture to 2d channel    ***/
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

            /***    Set attribute of currente binded channel    ***/
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                    GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                    GL10.GL_LINEAR);

            /***    Set bitmap to current 2d texture channel    ***/
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null)
                bitmap.recycle();

        }
    }

}
