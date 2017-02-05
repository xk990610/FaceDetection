package com.sky.facedetection;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.SurfaceHolder;

import com.sky.facedetection.camra.CameraInterface;
import com.sky.facedetection.config.ConfigUtils;
import com.sky.facedetection.facepoint.FacePoint;
import com.sky.facedetection.util.Constant;
import com.sky.facedetection.util.FileUtil;

import java.nio.ByteBuffer;

/**
 * Created by xk on 2016/12/7.
 */

public class FacedetectionInterface {
    public String TAG = "FacedetectionInterface";
    public static FacedetectionInterface facedetectionTracker = null;
    public Context mContext = null;
    private static final int FN_INIT_SUCCESS = 0;
    private static final int FN_CAMERA_FRAME_TIME = 1;
    private static final int FN_DETECT_TIME = 2;
    private FacedetectionTrackCallBack mFacedetectionTrackCallBackListener;
    private boolean killed = false;
    private boolean isNV21ready = false;
    private byte[] NV21;
    private byte[] yuv;
    private ByteBuffer CameraBuffer;
    private long lastFrameTime = 0;

    public FacedetectionInterface(Context context) {
        mContext = context;
    }

    public static FacedetectionInterface getInstance(Context context) {
        if (facedetectionTracker == null) {
            synchronized (FacedetectionInterface.class) {
                if (facedetectionTracker == null) {
                    facedetectionTracker = new FacedetectionInterface(context.getApplicationContext());
                }
            }
        }
        return facedetectionTracker;
    }

    public int init(String pkgName, int previewWidth, int previewHeight) {
        Log.i(TAG, "init-pkgName =" + pkgName);
        Log.i(TAG, "init-previewWidth =" + previewWidth + ",previewHeight=" + previewHeight);
        Constant.pkgName = pkgName;
        Constant.cfgdir = "/data/data/" + pkgName + "/model/";
        Constant.previewWidth = previewWidth;
        Constant.previewHeight = previewHeight;
        initCfgFile();
        int ret = Jni_cameraInit(Constant.cfgdir, previewWidth, previewHeight);
        initBuffer();
        return ret;
    }

    public int init(String pkgName) {
        int ret = init(pkgName, Constant.previewWidth, Constant.previewHeight);
        return ret;
    }



    private void initPreviewSizeConfig() {
        ConfigUtils.getInstance(mContext).loadCameraPreviewSizeConfig();
    }

    private void initCfgFile() {
        FileUtil fileUtil = new FileUtil(mContext);
        fileUtil.setCfgFile();
    }

    private int Jni_cameraInit(String dir, int width, int height) {
        return FaceDetection.cameraInit(dir, width, height);
    }

    private void initBuffer() {
        if (Constant.previewBufLength == 0) {
            Constant.previewBufLength = Constant.previewWidth * Constant.previewHeight * 4;
        }
        if (NV21 == null) {
            NV21 = new byte[Constant.previewBufLength];
        }
        if (yuv == null) {
            yuv = new byte[Constant.previewBufLength];
        }
        if (CameraBuffer == null) {
            CameraBuffer = ByteBuffer.allocateDirect(Constant.previewBufLength);
        }
    }
    public int getPreviewWidth() {
        return Constant.previewWidth;
    }
    public int getPreviewHeight() {
        return Constant.previewHeight;
    }
    public int getCameraFacing(){
      return   CameraInterface.getInstance().cameraFacing();
    }
    public void setGameStatus(boolean status){
        FaceDetection.setGameMode(status);
    }

    public boolean startCameraPreview(SurfaceHolder holder) {
        boolean ret = CameraInterface.getInstance().openCamera();
        Log.i(TAG, "openCamera result:" + ret);
        if (ret) {
            CameraInterface.getInstance().setCameraParameters();
            CameraInterface.getInstance().setPreviewDisplay(holder);
            CameraInterface.getInstance().startPreview();
            CameraInterface.getInstance().setPreviewCallback(mPreviewCallback);
        }
        return ret;
    }

    public boolean startCameraPreview(SurfaceTexture surfaceTexture) {
        boolean ret = CameraInterface.getInstance().openCamera();
        Log.i(TAG, "openCamera result:" + ret);
        if (ret) {
            CameraInterface.getInstance().setCameraParameters();
            CameraInterface.getInstance().setPreviewTexture(surfaceTexture);
            CameraInterface.getInstance().startPreview();
            CameraInterface.getInstance().setPreviewCallback(mPreviewCallback);
        }
        return ret;
    }

    android.hardware.Camera.PreviewCallback mPreviewCallback = new android.hardware.Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
            long currentTime = System.currentTimeMillis();
            Log.i(TAG, "onPreviewFrame currentTimeMillis:" + currentTime);
            long frameTime = currentTime - lastFrameTime;
           // camera.addCallbackBuffer(data);
            lastFrameTime = currentTime;
            Log.i(TAG, "onPreviewFrame frameTime:" + frameTime);
            synchronized (NV21) {
                isNV21ready = true;
                System.arraycopy(data, 0, NV21, 0, data.length);
            }
            if (mFacedetectionTrackCallBackListener != null) {
                mFacedetectionTrackCallBackListener.onTrackTime(FN_CAMERA_FRAME_TIME, frameTime);
            }
        }
    };

    public void startFaceDetect() {
        if (!mThread.isAlive()){
            mThread.start();
        }else{
            Log.i(TAG,"thread is started,maybe something error");
        }
    }

    Thread mThread = new Thread() {
        public void run() {
            while (!killed) {
                if (killed)
                    break;
                if (isNV21ready) {
                    synchronized (NV21) {
                        System.arraycopy(NV21, 0, yuv, 0, NV21.length);
                        isNV21ready = false;
                    }
                    Log.i(TAG, "start face capture");
                    long captureStartTime = System.currentTimeMillis();
                    CameraBuffer.clear();
                    CameraBuffer.put(yuv);
                    FacePoint facePoint = FaceDetection.cameraCapture2(CameraBuffer, Constant.previewBufLength,1);
                    mFacedetectionTrackCallBackListener.onTrackFacePoint(facePoint,CameraBuffer);
                    long captureUseTime = System.currentTimeMillis() - captureStartTime;
                    if (mFacedetectionTrackCallBackListener != null) {
                        mFacedetectionTrackCallBackListener.onTrackTime(FN_DETECT_TIME, captureUseTime);
                    }
                    Log.i(TAG, "captureUseTime:" + captureUseTime);
                }
            }
        }
    };

    private void pause() {
        killed = true;
    }

    public void unInit() {
        killed = true;
        CameraInterface.getInstance().releaseCamera();
        FaceDetection.cameraUnInit();
    }

    public void registFacedetectionTrackCallback(FacedetectionTrackCallBack trackCallBack) {
        this.mFacedetectionTrackCallBackListener = trackCallBack;
    }
    public static abstract interface FacedetectionTrackCallBack {
        public abstract void onInfo(int what ,String msg);
        public abstract void onTrackFacePoint(FacePoint facePoint);
        public abstract void onTrackFacePoint(FacePoint facePoint,ByteBuffer buffer);
        public abstract void onTrackTime(int what, long time);
    }
}
