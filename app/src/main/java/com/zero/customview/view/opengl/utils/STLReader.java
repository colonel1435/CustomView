package com.zero.customview.view.opengl.utils;

import android.content.Context;

import com.zero.customview.view.opengl.OpenGLConst;
import com.zero.customview.view.opengl.bean.Model;
import com.zero.customview.view.opengl.bean.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/22 0022 17:25
 * Refer: http://blog.csdn.net/huachao1001
 */

public class STLReader {
    private StlLoadListener stlLoadListener;

    public Model parserBinStlInSDCard(String path)
            throws IOException {

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        return parseBinStl(fis);
    }

    public Model parserBinStlInAssets(Context context, String fileName)
            throws IOException {

        InputStream is = context.getAssets().open(fileName);
        return parseBinStl(is);
    }

    public Model parseStlWithTextureInAssets(Context context, String fileName)
            throws IOException{
        String stlName = fileName + OpenGLConst.STL_POSTFIX;
        String textureName = fileName + OpenGLConst.PXY_POSTFIX;
        String picName = fileName + OpenGLConst.JPG_POSTFIX;
        InputStream stlInput = context.getAssets().open(stlName);
        InputStream textureInput = context.getAssets().open(textureName);

        return parseStlWithTexturePic(stlInput, textureInput, picName);
    }

    //解析二进制的Stl文件
    public Model parseBinStl(InputStream in) throws IOException {
        if (stlLoadListener != null)
            stlLoadListener.onstart();
        Model model = new Model();
        //前面80字节是文件头，用于存贮文件名；
        in.skip(80);

        //紧接着用 4 个字节的整数来描述模型的三角面片个数
        byte[] bytes = new byte[4];
        in.read(bytes);// 读取三角面片个数
        int facetCount = ByteUtils.byte4ToInt(bytes, 0);
        model.setFacetCount(facetCount);
        if (facetCount == 0) {
            in.close();
            return model;
        }

        // 每个三角面片占用固定的50个字节
        byte[] facetBytes = new byte[50 * facetCount];
        // 将所有的三角面片读取到字节数组
        in.read(facetBytes);
        //数据读取完毕后，可以把输入流关闭
        in.close();


        parseModel(model, facetBytes);


        if (stlLoadListener != null)
            stlLoadListener.onFinished();
        return model;
    }

    /**
     * 解析模型数据，包括顶点数据、法向量数据、所占空间范围等
     */
    private void parseModel(Model model, byte[] facetBytes) {
        int facetCount = model.getFacetCount();
        /**
         *  每个三角面片占用固定的50个字节,50字节当中：
         *  三角片的法向量：（1个向量相当于一个点）*（3维/点）*（4字节浮点数/维）=12字节
         *  三角片的三个点坐标：（3个点）*（3维/点）*（4字节浮点数/维）=36字节
         *  最后2个字节用来描述三角面片的属性信息
         * **/
        // 保存所有顶点坐标信息,一个三角形3个顶点，一个顶点3个坐标轴
        float[] verts = new float[facetCount * 3 * 3];
        // 保存所有三角面对应的法向量位置，
        // 一个三角面对应一个法向量，一个法向量有3个点
        // 而绘制模型时，是针对需要每个顶点对应的法向量，因此存储长度需要*3
        // 又同一个三角面的三个顶点的法向量是相同的，
        // 因此后面写入法向量数据的时候，只需连续写入3个相同的法向量即可
        float[] vnorms = new float[facetCount * 3 * 3];
        //保存所有三角面的属性信息
        short[] remarks = new short[facetCount];

        int stlOffset = 0;
        try {
            for (int i = 0; i < facetCount; i++) {
                if (stlLoadListener != null) {
                    stlLoadListener.onLoading(i, facetCount);
                }
                for (int j = 0; j < 4; j++) {
                    float x = ByteUtils.byte4ToFloat(facetBytes, stlOffset);
                    float y = ByteUtils.byte4ToFloat(facetBytes, stlOffset + 4);
                    float z = ByteUtils.byte4ToFloat(facetBytes, stlOffset + 8);
                    stlOffset += 12;

                    if (j == 0) {//法向量
                        vnorms[i * 9] = x;
                        vnorms[i * 9 + 1] = y;
                        vnorms[i * 9 + 2] = z;
                        vnorms[i * 9 + 3] = x;
                        vnorms[i * 9 + 4] = y;
                        vnorms[i * 9 + 5] = z;
                        vnorms[i * 9 + 6] = x;
                        vnorms[i * 9 + 7] = y;
                        vnorms[i * 9 + 8] = z;
                    } else {//三个顶点
                        verts[i * 9 + (j - 1) * 3] = x;
                        verts[i * 9 + (j - 1) * 3 + 1] = y;
                        verts[i * 9 + (j - 1) * 3 + 2] = z;

                        //记录模型中三个坐标轴方向的最大最小值
                        if (i == 0 && j == 1) {
                            model.minX = model.maxX = x;
                            model.minY = model.maxY = y;
                            model.minZ = model.maxZ = z;
                        } else {
                            model.minX = Math.min(model.minX, x);
                            model.minY = Math.min(model.minY, y);
                            model.minZ = Math.min(model.minZ, z);
                            model.maxX = Math.max(model.maxX, x);
                            model.maxY = Math.max(model.maxY, y);
                            model.maxZ = Math.max(model.maxZ, z);
                        }
                    }
                }
                short r = ByteUtils.byte2ToShort(facetBytes, stlOffset);
                stlOffset = stlOffset + 2;
                remarks[i] = r;
            }
        } catch (Exception e) {
            if (stlLoadListener != null) {
                stlLoadListener.onFailure(e);
            } else {
                e.printStackTrace();
            }
        }
        //将读取的数据设置到Model对象中
        model.setVerts(verts);
        model.setVnorms(vnorms);
        model.setRemarks(remarks);

    }

    private void parseTexture(Model model, byte[] textureBytes) {
        int facetCount = model.getFacetCount();
        // 三角面个数有三个顶点，一个顶点对应纹理二维坐标
        float[] textures = new float[facetCount * 3 * 2];
        int textureOffset = 0;
        for (int i = 0; i < facetCount * 3; i++) {
            //第i个顶点对应的纹理坐标
            //tx和ty的取值范围为[0,1],表示的坐标位置是在纹理图片上的对应比例
            float tx = ByteUtils.byte4ToFloat(textureBytes, textureOffset);
            float ty = ByteUtils.byte4ToFloat(textureBytes, textureOffset + 4);

            textures[i * 2] = tx;
            //我们的pxy文件原点是在左下角，因此需要用1减去y坐标值
            textures[i * 2 + 1] = 1 - ty;

            textureOffset += 8;
        }
        model.setTextures(textures);
    }

    public Model parseStlWithTexture(InputStream stlInput, InputStream textureInput) throws IOException {
        Model model = parseBinStl(stlInput);
        int facetCount = model.getFacetCount();
        // 三角面片有3個頂點，一個頂點有2個坐標軸數據，每個坐標軸數據是float類型（4字節）
        byte[] textureBytes = new byte[facetCount * 3 * 2 * 4];
        textureInput.read(textureBytes);// 將所有紋理坐標讀出來
        parseTexture(model, textureBytes);
        return model;
    }

    public Model parseStlWithTexturePic(InputStream stlInput, InputStream textureInput,
                                        String picName) throws IOException {
        Model model = parseBinStl(stlInput);
        model.setPictureName(picName);
        int facetCount = model.getFacetCount();
        byte[] textureBytes = new byte[facetCount * 3 * 2 * 4];
        textureInput.read(textureBytes);
        parseTexture(model, textureBytes);
        return model;
    }
    public static float getR(List<Model> models) {
        float maxRadius = models.get(0).getRadius();
        for(Model model : models) {
            if (model.getRadius() > maxRadius) {
                maxRadius = model.getRadius();
            }
        }
        return maxRadius;
    }

    public static Point getCenter(List<Model> models) {
        return new Point(0f, 0f, 0f);
    }

    public static interface StlLoadListener {
        void onstart();

        void onLoading(int cur, int total);

        void onFinished();

        void onFailure(Exception e);
    }
}