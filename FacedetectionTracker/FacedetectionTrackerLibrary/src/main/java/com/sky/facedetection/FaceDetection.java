package com.sky.facedetection;

import android.util.Log;
import com.sky.facedetection.facepoint.FacePoint;

public class FaceDetection {

	public static String TAG = "FaceDetection";
	static {
		Log.i(TAG, "load library facedetection");
		System.loadLibrary("facedetection");
	}
	public static native int cameraInit(String dir,int width,int height);

	public static native FacePoint cameraCapture(Object buf, int data_len);

	public static native FacePoint cameraCapture2(Object buf, int data_len,int torgb);

	public  static native void cameraUnInit();

	public  static native void setGameMode(boolean mode);
}
