package com.sky.facedetectiontrackerdemo.view;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sky.facedetection.FacedetectionInterface;
import com.sky.facedetection.facepoint.FacePoint;
import com.sky.facedetectiontrackerdemo.util.Constant;
import com.sky.facedetectiontrackerdemo.R;

import java.nio.ByteBuffer;

/**
 * Created by xiakai on 2016/11/11.
 */

public class FaceOverlyFragment extends Fragment {
    private static final String TAG = "FaceOverlyFragment";
    public TrackCallBack mListener;
    public TextureView mCameraPreviewTextureView;
    public SurfaceView mOverlap;
    public SurfaceView softpreviewsufaceview;
    public SurfaceHolder softpreviewSurfaceHolder;
    public SurfaceHolder mFcaePointSurfaceHolder;
    public int mDetectMode = 0;
    public int isCameraInitDone = 0;
    public FacePoint facePoint = new FacePoint();
    public Paint mPaint = null;
    boolean isDetectedFace = false;
    public Activity mActivity = null;
    public int cameraFacing = 1;

    public FaceOverlyFragment() {
        super();
        Log.i(TAG, "enter FaceOverlyFragment");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "FaceOverlyFragment onCreateView");
        View localView = inflater.inflate(R.layout.camera_overlap, container, false);
        this.mCameraPreviewTextureView = (TextureView) localView.findViewById(R.id.camerapreview);
        this.mOverlap = ((SurfaceView) localView.findViewById(R.id.facepointsurfaceview));
        mCameraPreviewTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        this.mOverlap = ((SurfaceView) localView.findViewById(R.id.facepointsurfaceview));
        this.mOverlap.setZOrderOnTop(true);
        this.mOverlap.getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.mOverlap.setFocusable(true);
        this.mOverlap.getHolder().addCallback(mSurfaceHolderCallback);
        this.softpreviewsufaceview =  ((SurfaceView) localView.findViewById(R.id.softpreviewsf));
       // this.softpreviewsufaceview.setZOrderOnTop(true);
        this.softpreviewsufaceview.getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.softpreviewsufaceview.setFocusable(true);
        this.softpreviewsufaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                softpreviewSurfaceHolder = holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        AdjustSurfaceviewSize();
        return localView;
    }

    private void AdjustSurfaceviewSize() {
        Log.i(TAG, "AdjustSurfaceviewSize");
        float ratX, ratY;
        ratX = (float) ((float) Constant.screenWidth / Constant.previewWidth);
        ratY = (float) ((float) Constant.screenHeight / Constant.previewHeight);
        Log.i(TAG, "ratX = " + ratX + ",ratY=" + ratY);
        Constant.PreViewScale = (ratX<=ratY)?ratX:ratY;
        Log.i(TAG, "PreViewScale = " + Constant.PreViewScale);
        Constant.ScaledWidth = (int) (Constant.PreViewScale * Constant.previewWidth);
        Constant.ScaledHeight = (int) (Constant.PreViewScale * Constant.previewHeight);
        Log.i(TAG, "ScaledHeight=" + Constant.ScaledHeight + ",ScaledWidth=" + Constant.ScaledWidth);
        Constant.leftMargin = (Constant.screenWidth - Constant.ScaledWidth) / 2;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                Constant.ScaledWidth, Constant.ScaledHeight);
        params.leftMargin = Constant.leftMargin;
        mCameraPreviewTextureView.setLayoutParams(params);
        FacedetectionInterface.getInstance(mActivity).registFacedetectionTrackCallback(mFacedetectionTrackCallBack);
        FacedetectionInterface.getInstance(mActivity).setGameStatus(false);
        isCameraInitDone = FacedetectionInterface.getInstance(mActivity).init(Constant.pkgName, Constant.previewWidth, Constant.previewHeight);
        mOverlap.setLayoutParams(params);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
                Constant.previewWidth, Constant.previewHeight);
        params1.leftMargin = Constant.leftMargin;
        softpreviewsufaceview.setLayoutParams(params1);
    }

    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "onSurfaceTextureAvailable...");
            boolean ret = FacedetectionInterface.getInstance(mActivity).startCameraPreview(surface);
            if(ret &isCameraInitDone == 0){
                cameraFacing = FacedetectionInterface.getInstance(mActivity).getCameraFacing();
                FacedetectionInterface.getInstance(mActivity).startFaceDetect();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.i(TAG, "onSurfaceTextureSizeChanged...");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.i(TAG, "onSurfaceTextureDestroyed...");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "mOverlap-surfaceCreated");
            mFcaePointSurfaceHolder = holder;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "mOverlap-surfaceChanged-width=" + width + ",height=" + height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "mOverlap-surfaceDestroyed");
        }
    };

    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }


    FacedetectionInterface.FacedetectionTrackCallBack mFacedetectionTrackCallBack = new FacedetectionInterface.FacedetectionTrackCallBack() {
        @Override
        public void onInfo(int what, String msg) {

        }

        @Override
        public void onTrackFacePoint(FacePoint facepoint) {
            facePoint = facepoint;
            showFaceDescription();
        }

        @Override
        public void onTrackFacePoint(FacePoint facepoint, ByteBuffer buffer) {
            facePoint = facepoint;
            showSoftPreview(buffer);
            showFaceDescription();
           // drawSoftFacePoint(facepoint);
        }

        @Override
        public void onTrackTime(int what, long time) {
            FaceOverlyFragment.this.mListener.onTrackdetected(what, time);
        }
    };

    public void showFaceDescription() {
        int detectFuncMode = FaceOverlyFragment.this.mDetectMode;
        switch (detectFuncMode) {
            case 0://jiance
                drawSoftFacePoint(facePoint);
                //drawFacePoint(facePoint);

                break;
            case 1:
                drawSoftMask(facePoint);
               // drawMask(facePoint);
                break;
            default:
                break;
        }
    }

    private MethodChooseInterface methodChooseInterface = new MethodChooseInterface() {
        public void onMethodChoose(int method) {
            Log.i(TAG, "onMethodChoose:" + method);
            mDetectMode = method;
            if(mDetectMode ==1){
                clearCanvas();
            }
        }
    };


    public Canvas lockCanvas() {
        Canvas canvas = null;
        if (mFcaePointSurfaceHolder != null) {

            canvas = mFcaePointSurfaceHolder.lockCanvas();
        }
        if (canvas == null || mFcaePointSurfaceHolder == null) {
            return null;
        }
        if (mPaint == null) {
            /** 创建曲线画笔 **/
            mPaint = new Paint();
            mPaint.setColor(Color.GREEN);
            /** 设置画笔抗锯齿 **/
            mPaint.setAntiAlias(true);
            /** 画笔的类型 **/
            mPaint.setStyle(Paint.Style.STROKE);
            /** 设置画笔变为圆滑状 **/
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            /** 设置线的宽度 **/
            mPaint.setStrokeWidth(3);
            mPaint.setTextSize(35);//设置字体大小
        }

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//clear the draw  所绘制不会提交到画布上。
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));// 显示上层绘制图片
        return canvas;
    }

    public void unlockCanvas(Canvas canvas) {
        try {
            if (mFcaePointSurfaceHolder != null && canvas!=null) {
                mFcaePointSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "Exception:" + e.toString());
        }
    }
    public void clearCanvas() {
        Canvas canvas = lockCanvas();
        unlockCanvas(canvas);
    }
    Bitmap mBitmap = null;

    public void showSoftPreview(ByteBuffer buffer) {
        Log.i(TAG, "showSoftPreview");
        Canvas canvas = null;
        if (softpreviewSurfaceHolder != null) {
            Rect dity = new Rect(0,0,640,480);
            canvas = softpreviewSurfaceHolder.lockCanvas(dity);
        }
        if (canvas == null || softpreviewSurfaceHolder == null) {
            return;
        }
//        if (mPaint == null) {
//            /** 创建曲线画笔 **/
//            mPaint = new Paint();
//            mPaint.setColor(Color.GREEN);
//            /** 设置画笔抗锯齿 **/
//            mPaint.setAntiAlias(true);
//            /** 画笔的类型 **/
//            mPaint.setStyle(Paint.Style.STROKE);
//            /** 设置画笔变为圆滑状 **/
//            mPaint.setStrokeCap(Paint.Cap.ROUND);
//            /** 设置线的宽度 **/
//            mPaint.setStrokeWidth(3);
//            mPaint.setTextSize(35);//设置字体大小
//        }
//
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//clear the draw  所绘制不会提交到画布上。
//        canvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));// 显示上层绘制图片

        try {
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);// 创建一个指定大小格式的Bitmap对象
            }
            buffer.rewind();
            mBitmap.copyPixelsFromBuffer(buffer);// 从buffer缓冲区复制位图像素,从当前位置开始覆盖位图的像素
            canvas.drawBitmap(mBitmap, 0, 0, null);

            if (softpreviewSurfaceHolder != null && canvas != null) {
                softpreviewSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "Exception:" + e.toString());
        }
    }
    public void drawSoftFacePoint(FacePoint facepoint) {
        Log.i(TAG, "drawFacePoint");
        long startTime = System.currentTimeMillis();

        Canvas canvas = null;
        if (mFcaePointSurfaceHolder != null) {
            Rect dity = new Rect(0,0,640,480);
            canvas = mFcaePointSurfaceHolder.lockCanvas(dity);
        }
        if (canvas == null || mFcaePointSurfaceHolder == null) {
            return ;
        }
        if (mPaint == null) {
            /** 创建曲线画笔 **/
            mPaint = new Paint();
            mPaint.setColor(Color.GREEN);
            /** 设置画笔抗锯齿 **/
            mPaint.setAntiAlias(true);
            /** 画笔的类型 **/
            mPaint.setStyle(Paint.Style.STROKE);
            /** 设置画笔变为圆滑状 **/
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            /** 设置线的宽度 **/
            mPaint.setStrokeWidth(3);
            mPaint.setTextSize(35);//设置字体大小
        }

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//clear the draw  所绘制不会提交到画布上。
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));// 显示上层绘制图片
        try {
            isDetectedFace = false;
            int face_cnt = facepoint.face_cnt;
            Log.i(TAG, "face_cnt=" + face_cnt);
            if (face_cnt != 0) {
                isDetectedFace = true;
                for (int i = 0; i < face_cnt; i++) {
                    int PointCnt = facepoint.face_description[i].PointCnt;
                    Log.i(TAG, "PointCnt=" + PointCnt);
                    if (PointCnt != 0) {
                        for (int k = 0; k < PointCnt; k++) {
                            float X = (float) (640-facepoint.face_description[i].array[k].x );
                            float Y = (float) (facepoint.face_description[i].array[k].y );
                            //Log.i(TAG, "k="+k+",X=" + X + ",Y=" + Y);
                            canvas.drawPoint(X, Y, mPaint);
                            //canvas.drawText(Integer.toString(k), X, Y, mPaint);
                        }
                        Log.i(TAG, "draw Point finish");
                        //drawFaceDescription(canvas, mPaint,facepoint);
                    }
                }
            }
            try {
                if (mFcaePointSurfaceHolder != null && canvas!=null) {
                    mFcaePointSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                Log.i(TAG, "Exception:" + e.toString());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "Exception:" + e.toString());
        }
        long drawPointUseTime = System.currentTimeMillis() - startTime;
        Log.i(TAG, "drawFacePoint useTime = " + drawPointUseTime);
        FaceOverlyFragment.this.mListener.onTrackdetected(Constant.FN_DRAW_POINT_TIME, drawPointUseTime);
    }
    public void drawSoftMask(FacePoint facepoint) {
        Log.i(TAG, "drawMask");
        float centerX = 0;
        float centerY = 0;
        float faceWidth = 0;
        float faceHeight = 0;
        long startTime = System.currentTimeMillis();
        isDetectedFace = false;
        int face_cnt = facepoint.face_cnt;
        Log.i(TAG, "face_cnt=" + face_cnt);
        if (face_cnt != 0) {
            isDetectedFace = true;
            for (int i = 0; i < face_cnt; i++) {
                int PointCnt = facepoint.face_description[i].PointCnt;
                Log.i(TAG, "PointCnt=" + PointCnt);
                Log.i(TAG, "FaceX=" + facepoint.face_description[i].FaceX + ",FaceY=" + facepoint.face_description[i].FaceY);
                Log.i(TAG, "FaceWidth=" + facepoint.face_description[i].FaceWidth + ",FaceHeight=" + facepoint.face_description[i].FaceHeight);
                if (cameraFacing == 1) {
                    centerX = (float) (640 - facepoint.face_description[i].array[30].x );
                } else if (cameraFacing == 0) {
                    centerX = (float) (facepoint.face_description[i].array[30].x );
                }
                centerY = (float) facepoint.face_description[i].array[30].y ;
                faceWidth = (float) ((float) Math.abs(facepoint.face_description[i].array[0].x - facepoint.face_description[i].array[16].x));
                Log.i(TAG, "centerX=" + centerX + ",centerY=" + centerY + ",faceWidth=" + faceWidth);
                faceHeight = (float) ((float) Math.abs(facepoint.face_description[i].array[0].x - facepoint.face_description[i].array[16].x) );
            }
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDetectedFace",isDetectedFace);
        bundle.putFloat("faceWidth",faceWidth);
        bundle.putFloat("faceHeight",faceWidth);
        bundle.putFloat("centerX",centerX);
        bundle.putFloat("centerY",centerY);
        FaceOverlyFragment.this.mListener.onTrackMask(bundle);
        long drawPointUseTime = System.currentTimeMillis() - startTime;
        Log.i(TAG, "drawMask useTime = " + drawPointUseTime);
        FaceOverlyFragment.this.mListener.onTrackdetected(Constant.FN_DRAW_POINT_TIME, drawPointUseTime);
    }


    public void drawMask(FacePoint facepoint) {
        Log.i(TAG, "drawMask");
        float centerX = 0;
        float centerY = 0;
        float faceWidth = 0;
        float faceHeight = 0;
        long startTime = System.currentTimeMillis();
        isDetectedFace = false;
        int face_cnt = facepoint.face_cnt;
        Log.i(TAG, "face_cnt=" + face_cnt);
        if (face_cnt != 0) {
            isDetectedFace = true;
            for (int i = 0; i < face_cnt; i++) {
                int PointCnt = facepoint.face_description[i].PointCnt;
                Log.i(TAG, "PointCnt=" + PointCnt);
                Log.i(TAG, "FaceX=" + facepoint.face_description[i].FaceX + ",FaceY=" + facepoint.face_description[i].FaceY);
                Log.i(TAG, "FaceWidth=" + facepoint.face_description[i].FaceWidth + ",FaceHeight=" + facepoint.face_description[i].FaceHeight);
                if (cameraFacing == 1) {
                    centerX = (float) (Constant.ScaledWidth - facepoint.face_description[i].array[30].x * Constant.PreViewScale);
                } else if (cameraFacing == 0) {
                    centerX = (float) (facepoint.face_description[i].array[30].x * Constant.PreViewScale);
                }
                centerY = (float) facepoint.face_description[i].array[30].y * Constant.PreViewScale;
                faceWidth = (float) ((float) Math.abs(facepoint.face_description[i].array[0].x - facepoint.face_description[i].array[16].x) * Constant.PreViewScale);
                Log.i(TAG, "centerX=" + centerX + ",centerY=" + centerY + ",faceWidth=" + faceWidth);
                faceHeight = (float) ((float) Math.abs(facepoint.face_description[i].array[0].x - facepoint.face_description[i].array[16].x) * Constant.PreViewScale);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDetectedFace",isDetectedFace);
        bundle.putFloat("faceWidth",faceWidth);
        bundle.putFloat("faceHeight",faceWidth);
        bundle.putFloat("centerX",centerX);
        bundle.putFloat("centerY",centerY);
        FaceOverlyFragment.this.mListener.onTrackMask(bundle);
        long drawPointUseTime = System.currentTimeMillis() - startTime;
        Log.i(TAG, "drawMask useTime = " + drawPointUseTime);
        FaceOverlyFragment.this.mListener.onTrackdetected(Constant.FN_DRAW_POINT_TIME, drawPointUseTime);
    }


    public void drawFacePoint(FacePoint facepoint) {
        Log.i(TAG, "drawFacePoint");
        long startTime = System.currentTimeMillis();
        Canvas canvas = lockCanvas();
        try {
            isDetectedFace = false;
            int face_cnt = facepoint.face_cnt;
            Log.i(TAG, "face_cnt=" + face_cnt);
            if (face_cnt != 0) {
                isDetectedFace = true;
                for (int i = 0; i < face_cnt; i++) {
                    int PointCnt = facepoint.face_description[i].PointCnt;
                    Log.i(TAG, "PointCnt=" + PointCnt);
                    if (PointCnt != 0) {
                        for (int k = 0; k < PointCnt; k++) {
                            float X = (float) (Constant.ScaledWidth - facepoint.face_description[i].array[k].x * Constant.PreViewScale);
                            float Y = (float) (facepoint.face_description[i].array[k].y * Constant.PreViewScale);
                            //Log.i(TAG, "k="+k+",X=" + X + ",Y=" + Y);
                            canvas.drawPoint(X, Y, mPaint);
                            //canvas.drawText(Integer.toString(k), X, Y, mPaint);
                        }
                        Log.i(TAG, "draw Point finish");
                        drawFaceDescription(canvas, mPaint,facepoint);
                    }
                }
            }
            unlockCanvas(canvas);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i(TAG, "Exception:" + e.toString());
        }
        long drawPointUseTime = System.currentTimeMillis() - startTime;
        Log.i(TAG, "drawFacePoint useTime = " + drawPointUseTime);
        FaceOverlyFragment.this.mListener.onTrackdetected(Constant.FN_DRAW_POINT_TIME, drawPointUseTime);
    }

    public Bitmap FaceRect = null;
    public void drawFaceDescription(Canvas canvas, Paint paint,FacePoint facepoint) {
        Log.i(TAG,"drawFaceDescription");
        String sexText = this.getString(R.string.sex) + ":  ";
        String withGlassText = this.getString(R.string.withglass) + ":  ";
        String ageText = this.getString(R.string.age) + ":  ";
        String AgePredict = "";
        String isSmileText = "";

        if(facepoint==null){
            return;
        }
        int face_cnt = facepoint.face_cnt;
        Log.i(TAG, "face_cnt=" + face_cnt);

        if (face_cnt != 0) {

            for (int i = 0; i < face_cnt; i++) {
                int PointCnt = facepoint.face_description[i].PointCnt;
                Log.i(TAG, "PointCnt=" + PointCnt);
                float startX = Constant.ScaledWidth - (facepoint.face_description[i].FaceX + facepoint.face_description[i].FaceWidth) * Constant.PreViewScale;
                float topY = facepoint.face_description[i].FaceY * Constant.PreViewScale;
                float rightX = Constant.ScaledWidth - facepoint.face_description[i].FaceX * Constant.PreViewScale;
                float bottomY = (facepoint.face_description[i].FaceY + facepoint.face_description[i].FaceHeight) * Constant.PreViewScale;
                //canvas.drawRect(startX+20, startY+20, rightX-20, bottomY-20, mPaint);
                if(FaceRect ==null){
                    FaceRect = BitmapFactory.decodeResource(getResources(), R.drawable.over);
                }
                NinePatch np = new NinePatch(FaceRect, FaceRect.getNinePatchChunk(), null);
                Rect rect = new Rect((int) startX, (int) topY, (int) rightX, (int) bottomY);
                np.draw(canvas, rect);

                int isSmile = facepoint.face_description[i].isSmile;
                int Age = facepoint.face_description[i].Age;
                switch (Age){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        AgePredict = "0-2岁婴儿";
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        AgePredict = "3-6岁幼儿";
                        break;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                        AgePredict = "7-12岁小学生";
                        break;
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                        AgePredict = "13-18岁中学生";
                        break;
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                        AgePredict = "19-22岁大学生";
                        break;
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                        AgePredict = "23-28岁职场新人";
                        break;
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                        AgePredict = "29-35岁职场精英";
                        break;
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                        AgePredict = "36-45岁经理";
                        break;
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                        AgePredict = "45-55岁中年";
                        break;
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                        AgePredict = "55+老年";
                        break;
                    default:
                        AgePredict = "";
                        break;
                }
                ageText = ageText+AgePredict;
                canvas.drawText(ageText, rightX + 20, topY + 150, paint);
                if(AgePredict.equalsIgnoreCase("")|| AgePredict.trim().equals("")){
                }else{
                    if(Age%2 ==0){
                        withGlassText += this.getString(R.string.no);
                    }else if(Age%2 ==1){
                        withGlassText += this.getString(R.string.yes);
                    }
                    if((Age/2)%2 ==0){
                        sexText += this.getString(R.string.male);
                    }else if((Age/2)%2 ==1){
                        sexText += this.getString(R.string.female);
                    }
                }
                canvas.drawText(sexText, rightX + 20, topY, paint);
                canvas.drawText(withGlassText, rightX + 20, topY + 100, paint);
                canvas.drawText(getString(R.string.smile) + ":" + Integer.toString(isSmile), rightX + 20, topY + 50, paint);
            }
        }
    }
    public void onPause() {
        super.onPause();

        Log.i(TAG, "onPause");
    }

    public void onStop() {
        super.onStop();
        FacedetectionInterface.getInstance(mActivity).unInit();
        Log.i(TAG, TAG + " onStop");
    }

    public void onDestory() {
        super.onDestroy();
        Log.i(TAG, "onDestory");
    }

    public MethodChooseInterface getMethodChooseInterface() {
        return this.methodChooseInterface;
    }

    public void registTrackCallback(TrackCallBack paramTrackCallBack) {
        this.mListener = paramTrackCallBack;
    }

    public static abstract interface TrackCallBack {
        public abstract void onTrackInfo(int what, String msg);
        public abstract void onTrackdetected(int what, long time);
        public abstract void onTrackMask(Bundle bundle);
    }

    public static abstract interface MethodChooseInterface {
        public abstract void onMethodChoose(int paramInt);
    }

}
