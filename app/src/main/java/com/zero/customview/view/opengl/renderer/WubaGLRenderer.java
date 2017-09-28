package com.zero.customview.view.opengl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LightingColorFilter;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;

import com.zero.customview.view.opengl.bean.Model;
import com.zero.customview.view.opengl.bean.Point;
import com.zero.customview.view.opengl.utils.ByteUtils;
import com.zero.customview.view.opengl.utils.STLReader;

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
        /*
        try {
            mContext = context;
            model = new STLReader().parserBinStlInAssets(context, "dragon.stl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        try {
            mContext = context;
            STLReader reader = new STLReader();
            Model model = reader.parseStlWithTextureInAssets(context, "stl/dragon");
            models.add(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotate(float degree) {
        mDegree = degree;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.74f, 0.76f, 0.78f, 0f); /***  colorSilver  ***/
        gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度缓存
        gl.glClearDepthf(1.0f); // 设置深度缓存值
        gl.glDepthFunc(GL10.GL_LEQUAL); // 设置深度缓存比较函数
        gl.glShadeModel(GL10.GL_SMOOTH);// 设置阴影模式GL_SMOOTH
        float r = model.getRadius();
        //r是半径，不是直径，因此用0.5/r可以算出放缩比例
        mScalef = 0.5f / r;
        mCenterPoint = model.getCentrePoint();

        openLight(gl);
        enableMaterial(gl);
        initConfigData(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(width, height)指定了视口的大小
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION); // 设置投影矩阵
        gl.glLoadIdentity(); // 设置矩阵为单位矩阵，相当于重置矩阵
        GLU.gluPerspective(gl, 45.0f, ((float) width) / height, 1f, 100f);// 设置透视范围

        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
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

        //为了能有立体感觉，通过改变mDegree值，让模型不断旋转
        gl.glRotatef(mDegree, 0, 1, 0);

        //将模型放缩到View刚好装下
        gl.glScalef(mScalef, mScalef, mScalef);
        //把模型移动到原点
        gl.glTranslatef(-mCenterPoint.x, -mCenterPoint.y,
                -mCenterPoint.z);


        /*
        //=================== Light ==============================//
        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // 允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色

        //设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT, 0, model.getVnormBuffer());
        // 设置三角形顶点数据源
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.getVertBuffer());

        // 绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, model.getFacetCount() * 3);

        // 取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        */
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
