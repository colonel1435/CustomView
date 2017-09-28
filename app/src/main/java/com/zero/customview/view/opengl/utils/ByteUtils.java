package com.zero.customview.view.opengl.utils;

import com.zero.customview.view.opengl.bean.Model;
import com.zero.customview.view.opengl.bean.Point;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Description
 * Author : Mr.wumin
 * Email  : fusu1435@163.com
 * Date   : 2017/9/22 0022 17:23
 */

public class ByteUtils {
    public static FloatBuffer floatToBuffer(float[] a) {
        /***    Float occupies 4B   ***/
        ByteBuffer bb = ByteBuffer.allocateDirect(a.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(a);
        buffer.position(0);
        return buffer;
    }

    public static int byte4ToInt(byte[] bytes, int offset) {
        int b3 = bytes[offset + 3] & 0xFF;
        int b2 = bytes[offset + 2] & 0xFF;
        int b1 = bytes[offset + 1] & 0xFF;
        int b0 = bytes[offset + 0] & 0xFF;
        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static short byte2ToShort(byte[] bytes, int offset) {
        int b1 = bytes[offset + 1] & 0xFF;
        int b0 = bytes[offset + 0] & 0xFF;
        return (short) ((b1 << 8) | b0);
    }

    public static float byte4ToFloat(byte[] bytes, int offset) {

        return Float.intBitsToFloat(byte4ToInt(bytes, offset));
    }

    public static String byteToString(byte[] bytes) {
        return new String(bytes);
    }

}
