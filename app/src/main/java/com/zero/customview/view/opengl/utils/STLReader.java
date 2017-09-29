package com.zero.customview.view.opengl.utils;

import android.content.Context;
import android.util.Log;

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
    private final String TAG = this.getClass().getSimpleName()+"@wumin";
    private static final int STL_HEADER_SIZE = 80;
    private static final int STL_FACET_NUM_SIZE = 4;
    private static final int STL_FACET_SIZE = 50;
    private static final int STL_FACET_ATTR = 2;
    private static final int STL_FACET_EDGE_NUM = 3;
    private static final int STL_FACET_COORD_NUM = 3;
    private static final int STL_TEXTURE_COORD_NUM = 2;
    private static final int STL_FLOAT_SIZE = 4;
    private StlLoadListener stlLoadListener;

    public Model parserBinStlInSDCard(String path)
            throws IOException {

        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        return parseBinStl(fis);
    }

    public Model parserBinStlInAssets(Context context, String fileName)
            throws IOException {

        if (!fileName.endsWith(OpenGLConst.STL_POSTFIX)) {
            return null;
        }
        InputStream is = context.getAssets().open(fileName);
        Log.d(TAG, "parserBinStlInAssets: " + fileName);
        return parseBinStl(is);
    }

    public Model parserBinStlInSdcard(File file)
            throws IOException {

        if (!file.getName().endsWith(OpenGLConst.STL_POSTFIX)) {
            return null;
        }

        InputStream is = new FileInputStream(file);
        Log.d(TAG, "parserBinStlInSdcard: " + file.getName());
        return parseBinStl(is);
    }

    public Model parseStlWithTextureInAssets(Context context, String fileName)
            throws IOException{
        String stlName = fileName + OpenGLConst.STL_POSTFIX;
        String textureName = fileName + OpenGLConst.PXY_POSTFIX;
        String picName = fileName + OpenGLConst.PNG_POSTFIX;
        InputStream stlInput = context.getAssets().open(stlName);
        InputStream textureInput = context.getAssets().open(textureName);

        return parseStlWithTexturePic(stlInput, textureInput, picName);
    }

    /**
     *  @description    Parse stl binary file
     *  @param in
     *  @return com.zero.customview.view.opengl.bean.Model
     *  @author Mr.wumin
     *  @time 2017/9/28 0028 14:29
     */
    public Model parseBinStl(InputStream in) throws IOException {
        if (stlLoadListener != null)
            stlLoadListener.onstart();
        Model model = new Model();
        /***    1, 80B is the header   ***/
        byte[] headerBytes = new byte[STL_HEADER_SIZE];
        in.read(headerBytes);
        model.setHeader(ByteUtils.byteToString(headerBytes));
//        in.skip(STL_HEADER_SIZE);

        /***    2, Next 4B is the num of triangle facet     ***/
        byte[] bytes = new byte[STL_FACET_NUM_SIZE];
        in.read(bytes);
        int facetCount = ByteUtils.byte4ToInt(bytes, 0);
        model.setFacetCount(facetCount);
        if (facetCount == 0) {
            in.close();
            return model;
        }

        /**    3, Every facet occupies 50 Bytes, normal vector occupies 3*4 B,
         *         edge abs occupies 3*4*3 B; The rest 2B is back up;
         */
        byte[] facetBytes = new byte[STL_FACET_SIZE * facetCount];
        in.read(facetBytes);
        in.close();


        parseByteToModel(model, facetBytes);


        if (stlLoadListener != null)
            stlLoadListener.onFinished();
        return model;
    }

    /**
     *  @description    Parse bytes array to model,such as normal vector,
     *                  edge coord.
     *  @param model, facetBytes
     *  @return void
     *  @author Mr.wumin
     *  @time 2017/9/28 0028 14:40
     */
    private void parseByteToModel(Model model, byte[] facetBytes) {
        /**
         *  Every facet occupy 50B;
         *  Normal vector : (x, y ,z) * 4B = 12B
         *  Edge coord : (x, y , z) * 3 edges * 4B = 36B
         *  Attribute : 2B
         */
        int facetCount = model.getFacetCount();
        /***    All the edge coord float    ***/
        float[] verts = new float[facetCount * STL_FACET_EDGE_NUM * STL_FACET_COORD_NUM];
        /**
         *  All the normal vector float.
         *  We need the normal vector of per edge when draw the model,
         *  and normal vector of per edge in the facet are the same.
         *  so we should write 3 normal vectors which is the same.
         */
        float[] vnorms = new float[facetCount * STL_FACET_EDGE_NUM * STL_FACET_EDGE_NUM];
        /***    All attributes of every facet   ***/
        short[] remarks = new short[facetCount];

        int stlOffset = 0;
        try {
            for (int i = 0; i < facetCount; i++) {
                if (stlLoadListener != null) {
                    stlLoadListener.onLoading(i, facetCount);
                }
                for (int j = 0; j < 4; j++) {
                    float x = ByteUtils.byte4ToFloat(facetBytes, stlOffset);
                    float y = ByteUtils.byte4ToFloat(facetBytes, stlOffset + STL_FLOAT_SIZE);
                    float z = ByteUtils.byte4ToFloat(facetBytes, stlOffset + 2*STL_FLOAT_SIZE);
                    stlOffset += 3*STL_FLOAT_SIZE;

                    if (j == 0) {/***    Normal vectors     ***/
                        vnorms[i * 9] = x;
                        vnorms[i * 9 + 1] = y;
                        vnorms[i * 9 + 2] = z;
                        vnorms[i * 9 + 3] = x;
                        vnorms[i * 9 + 4] = y;
                        vnorms[i * 9 + 5] = z;
                        vnorms[i * 9 + 6] = x;
                        vnorms[i * 9 + 7] = y;
                        vnorms[i * 9 + 8] = z;
                    } else {/***    Edge coords     ***/
                        verts[i * 9 + (j - 1) * 3] = x;
                        verts[i * 9 + (j - 1) * 3 + 1] = y;
                        verts[i * 9 + (j - 1) * 3 + 2] = z;

                        /***    The max and min value in three coord axis   ***/
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
                /***    End of 2B are attributes    ***/
                short r = ByteUtils.byte2ToShort(facetBytes, stlOffset);
                stlOffset = stlOffset + STL_FACET_ATTR;
                remarks[i] = r;
            }
        } catch (Exception e) {
            if (stlLoadListener != null) {
                stlLoadListener.onFailure(e);
            } else {
                e.printStackTrace();
            }
        }
        /***    Set values      ***/
        model.setVerts(verts);
        model.setVnorms(vnorms);
        model.setRemarks(remarks);

    }

    /**
     *  @description    Parse texture
     *  @param model, textureBytes
     *  @return void
     *  @author Mr.wumin
     *  @time 2017/9/28 0028 14:54
     */
    private void parseTexture(Model model, byte[] textureBytes) {
        int facetCount = model.getFacetCount();
        /***    There are three edges in facet, and one edge correspond to 2-D coord    ***/
        float[] textures = new float[facetCount * STL_FACET_EDGE_NUM * STL_TEXTURE_COORD_NUM];
        int textureOffset = 0;
        for (int i = 0; i < facetCount * STL_FACET_EDGE_NUM; i++) {
            /***    tx and ty in the range of [0, 1], it said the scale of texture picture  ***/
            float tx = ByteUtils.byte4ToFloat(textureBytes, textureOffset);
            float ty = ByteUtils.byte4ToFloat(textureBytes, textureOffset + STL_FLOAT_SIZE);

            /**
             *  In the pxy, the zero point is at the left-bottom,
             *  however the android zero point is at the left-top.
             *  So it should be decreased by one;
             */

            tx = 1.0f;
            ty = 0.0f;
            textures[i * STL_TEXTURE_COORD_NUM] = tx;
            textures[i * STL_TEXTURE_COORD_NUM + 1] = 1 - ty;

            textureOffset += (2*STL_FLOAT_SIZE);
        }
        model.setTextures(textures);
    }

    public Model parseStlWithTexture(InputStream stlInput, InputStream textureInput) throws IOException {
        Model model = parseBinStl(stlInput);
        int facetCount = model.getFacetCount();
        byte[] textureBytes = new byte[facetCount * STL_FACET_EDGE_NUM
                                        * STL_TEXTURE_COORD_NUM * STL_FLOAT_SIZE];
        textureInput.read(textureBytes);
        parseTexture(model, textureBytes);
        return model;
    }

    public Model parseStlWithTexturePic(InputStream stlInput, InputStream textureInput,
                                        String picName) throws IOException {
        Model model = parseBinStl(stlInput);
        model.setPictureName(picName);
        int facetCount = model.getFacetCount();
        byte[] textureBytes = new byte[facetCount * STL_FACET_EDGE_NUM
                                        * STL_TEXTURE_COORD_NUM * STL_FLOAT_SIZE];
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
