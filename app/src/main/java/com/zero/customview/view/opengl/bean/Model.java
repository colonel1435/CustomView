package com.zero.customview.view.opengl.bean;

import com.zero.customview.view.opengl.utils.ByteUtils;

import java.nio.FloatBuffer;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/22 0022 17:22
 */

public class Model {
    /***    Header info     ***/
    private String header;
    /***    Count of the facet  ***/
    private int facetCount;
    /***    Edge coord arrays   ***/
    private float[] verts;
    /***    Normal vector arrays    ***/
    private float[] vnorms;
    /***    Attribte info of per facet  ***/
    private short[] remarks;
    /***    Texture arrays    ***/
    private float[] textures;
    /***    Texture ID arrays   ***/
    private int[] textureIds;
    /***    Picture of texture  ***/
    private String pictureName;

    /***    Buffer of edge coord    ***/
    private FloatBuffer vertBuffer;
    /***    Buffer of normal vector     ***/
    private FloatBuffer vnormBuffer;
    /***    Buffer of texture   ***/
    private FloatBuffer textureBuffer;
    /***    Max and min value of x, y, z axis   ***/
    public float maxX;
    public float minX;
    public float maxY;
    public float minY;
    public float maxZ;
    public float minZ;

    /**
     *  Get the center point of model
     */
    public Point getCentrePoint() {
        float cx = minX + (maxX - minX) / 2;
        float cy = minY + (maxY - minY) / 2;
        float cz = minZ + (maxZ - minZ) / 2;
        return new Point(cx, cy, cz);
    }

    /**
     *  Get the max radius of model
     */
    public float getRadius() {
        float dx = (maxX - minX);
        float dy = (maxY - minY);
        float dz = (maxZ - minZ);
        float max = dx;
        if (dy > max)
            max = dy;
        if (dz > max)
            max = dz;
        return max;
    }

    public void setVerts(float[] verts) {
        this.verts = verts;
        vertBuffer = ByteUtils.floatToBuffer(verts);
    }

    public void setVnorms(float[] vnorms) {
        this.vnorms = vnorms;
        vnormBuffer = ByteUtils.floatToBuffer(vnorms);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getFacetCount() {
        return facetCount;
    }

    public void setFacetCount(int facetCount) {
        this.facetCount = facetCount;
    }

    public float[] getVerts() {
        return verts;
    }

    public float[] getVnorms() {
        return vnorms;
    }

    public short[] getRemarks() {
        return remarks;
    }

    public void setRemarks(short[] remarks) {
        this.remarks = remarks;
    }

    public FloatBuffer getVertBuffer() {
        return vertBuffer;
    }

    public void setVertBuffer(FloatBuffer vertBuffer) {
        this.vertBuffer = vertBuffer;
    }

    public FloatBuffer getVnormBuffer() {
        return vnormBuffer;
    }

    public void setVnormBuffer(FloatBuffer vnormBuffer) {
        this.vnormBuffer = vnormBuffer;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(float maxZ) {
        this.maxZ = maxZ;
    }

    public float getMinZ() {
        return minZ;
    }

    public void setMinZ(float minZ) {
        this.minZ = minZ;
    }

    public float[] getTextures() {
        return textures;
    }

    public void setTextures(float[] texture) {
        this.textures = texture;
        this.textureBuffer = ByteUtils.floatToBuffer(textures);
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public void setTextureBuffer(FloatBuffer textureBuffer) {
        this.textureBuffer = textureBuffer;
    }

    public int[] getTextureIds() {
        return textureIds;
    }

    public void setTextureIds(int[] textureIds) {
        this.textureIds = textureIds;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }
}
