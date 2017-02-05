package com.sky.facedetection.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.nio.ByteBuffer;


/**
 * Created by xk on 2016/11/25.
 */

public class BitmapRgbShow {
    private String TAG = "FaceOverlyFragment";
    Bitmap mBitmap = null;
    public static BitmapRgbShow mBitmapRgbShow = null;

    public static synchronized BitmapRgbShow getInstance() {
        if (mBitmapRgbShow == null) {
            mBitmapRgbShow = new BitmapRgbShow();
        }
        return mBitmapRgbShow;
    }

    public BitmapRgbShow() {
        Log.i(TAG,"constructor BitmapRgbShow");
    }


    public void setBitmapRgbShow(ByteBuffer buffer, Canvas canvas, int width, int height,float left, float top) {
        Log.i(TAG, "setBitmapRgbShow");
        try {
            if (buffer != null) {
                buffer.rewind();
                Bitmap  mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);// 创建一个指定大小格式的Bitmap对象
                buffer.rewind();
                mBitmap.copyPixelsFromBuffer(buffer);
                canvas.drawBitmap(mBitmap, left, top, null);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "Exception:" + e.toString());
        }
       // mBitmap.recycle();
        Log.i(TAG, "setBitmapRgbShow finish");
    }

}
