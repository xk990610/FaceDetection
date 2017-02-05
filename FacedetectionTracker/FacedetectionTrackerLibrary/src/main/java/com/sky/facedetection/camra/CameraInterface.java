package com.sky.facedetection.camra;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

import com.sky.facedetection.util.Constant;

import java.io.IOException;
import java.util.List;

public class CameraInterface {

    public final static String TAG = "CameraInterface";
    public static CameraInterface mCameraInterface;
    public Camera mCamera;
    /**
     * 是否打开前置相机,true为前置,false为后置
     */
    public int mCameraFacing = 0;
    public Context mContext = null;
    Camera.PreviewCallback previewCallback;

    public CameraInterface() {
        Log.i(TAG, "constructor CameraInterface");
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public void setPreviewDisplay(SurfaceHolder holder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void setPreviewTexture(SurfaceTexture textuew) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(textuew);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void startPreview() {
        Log.i(TAG, "startPreview");
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback paramPreviewCallback) {
        Log.i(TAG, "setPreviewCallback");
        this.previewCallback = paramPreviewCallback;
        if (this.mCamera != null)
            this.mCamera.setPreviewCallback(paramPreviewCallback);
    }

    /**
     * 根据当前照相机状态(前置或后置)，打开对应相机
     */
    public boolean openCamera() {
        Log.i(TAG, "openCamera");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        CameraInfo cameraInfo = new CameraInfo();
        int CamerasNumber = Camera.getNumberOfCameras();
        Log.i(TAG, "Camera.getNumberOfCameras()=" + CamerasNumber);
        if (CamerasNumber == 0) {
            Log.i(TAG, "no Camera");
            return false;
        } else {
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                Log.i(TAG, "cameraInfo.facing=" + cameraInfo.facing);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        Log.i(TAG, "open CAMERA_FACING_FRONT");
                        mCamera = Camera.open(i);
                        mCameraFacing = 1;
                    } catch (Exception e) {
                        Log.i(TAG, "openCamera CAMERA_FACING_FRONT error:" + e.toString());
                        if (mCamera != null) {
                            mCamera.release();
                            mCamera = null;
                        }
                        return false;
                    }
                }
            }
            if(mCamera ==null){
                Log.i(TAG,"openCamera CAMERA_FACING_FRONT error,try open back camera");
                try {
                    mCamera = Camera.open();
                    mCameraFacing = 0;
                } catch (Exception e) {
                    mCamera = null;
                    Log.i(TAG, "openCamera  error:" + e.toString());
                    return false;
                }
            }
        }
    return true;
}

    public int cameraFacing() {
        return mCameraFacing;
    }

    /**
     * 设置照相机参数
     */
    public void setCameraParameters() {
        Log.i(TAG, "setCameraParameters");

        if (mCamera == null) {
            Log.i(TAG, "mCamera is null,return");
            return;
        }


        Camera.Parameters parameters = mCamera.getParameters();
        // 选择合适的预览尺寸
        List<Size> sizeList = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < sizeList.size(); i++) {
            Log.i(TAG, "SupportedPreviewSize-width=" + sizeList.get(i).width + "height=" + sizeList.get(i).height);
        }
        Log.i(TAG, "Constant.previewWidth=" + Constant.previewWidth);
        Log.i(TAG, "Constant.previewHeight=" + Constant.previewHeight);
        if (sizeList.size() > 0) {
            // 预览图片大小
            parameters.setPreviewSize(Constant.previewWidth, Constant.previewHeight);
        }

        parameters.setPreviewFormat(ImageFormat.NV21);
        Log.i(TAG, "PreviewFormat = " + parameters.getPreviewFormat());

        List<int[]> fpsRange = parameters.getSupportedPreviewFpsRange();
        Log.i(TAG, "fpsRange size:" + fpsRange.size());
        for (int j = 0; j < fpsRange.size(); j++) {
            int[] range = fpsRange.get(j);
            for (int k = 0; k < range.length; k++) {

                Log.i(TAG, "getSupportedPreviewFpsRange-fps=" + range[k] + ",k=" + k);
            }
        }
        parameters.setPreviewFpsRange(30000, 30000);

        // 设置图片格式
//		parameters.setPictureFormat(ImageFormat.JPEG);
//		parameters.setJpegQuality(100);
//		parameters.setJpegThumbnailQuality(100);

        // 自动聚焦模式
       // parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (mCameraFacing == 1) {//前置摄像头
            parameters.setRotation(0);// （默认摄像头是横拍）
        } else if (mCameraFacing == 0) {//后置摄像头
            parameters.setRotation(180);// （默认摄像头是横拍）
        }

        // 预览图片旋转90°
        mCamera.setDisplayOrientation(0);// 预览转90°

        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Log.i(TAG, "setParameters  error:" + e.toString());
        }

    }


    public void releaseCamera() {
        Log.i(TAG, "releaseCamera");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

}