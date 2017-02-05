
/**
 * Created by xk on 2016/11/24.
 */
package com.sky.facedetection.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sky.facedetection.util.Constant;

public class ConfigUtils {
    public static String TAG = "ConfigUtils";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor shareEditor;
    private static ConfigUtils preferenceUtils = null;
    public static final String PREFERENCE_FILE_NAME = "AppConfig";
    public static final String PREVIEW_WIDTH = "preview_width";
    public static final String PREVIEW_HEIGHT = "preview_height";
    public static final String PREVIEW_SIZE = "preview_size";

    protected ConfigUtils(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        shareEditor = sharedPreferences.edit();
    }
    public static ConfigUtils getInstance(Context context){
        if (preferenceUtils == null) {
            synchronized (ConfigUtils.class) {
                if (preferenceUtils == null) {
                    preferenceUtils = new ConfigUtils(context.getApplicationContext());
                }
            }
        }
        return preferenceUtils;
    }

    public void loadCameraPreviewSizeConfig(){
        int previewWidth = getIntParam(PREVIEW_WIDTH);
        int previewHeight = getIntParam(PREVIEW_HEIGHT);
        Log.i(TAG, "init PreviewSize,width = " + previewWidth + ",height=" + previewHeight);
        if (previewHeight != 0 && previewWidth != 0) {
            Constant.previewWidth = previewWidth;
            Constant.previewHeight = previewHeight;
            Constant.previewBufLength = Constant.previewWidth * Constant.previewHeight * 3 / 2;
        }
    }
    public  String getpreviewSizeText(){
        String text = getStringParam(PREVIEW_SIZE);
        if(text== null || text=="")
            text = Integer.toString(Constant.previewWidth)+"*"+Integer.toString(Constant.previewHeight);
        return text;
    }

    public void resetPreviewSize(int index) {
        int width = Constant.previewSizeArray[index][0];
        int height = Constant.previewSizeArray[index][1];
        setIntParam(PREVIEW_WIDTH, width);
        setIntParam(PREVIEW_HEIGHT, height);
        setStringParam(PREVIEW_SIZE, Constant.previewSize[index]);
    }

    public void resetPreviewSize(int width,int height,String size) {
        setIntParam(PREVIEW_WIDTH, width);
        setIntParam(PREVIEW_HEIGHT, height);
        setStringParam(PREVIEW_SIZE, size);
    }

    public  int getIntParam(String key){
        return  sharedPreferences.getInt(key,0);
    }
    public void setIntParam(String key, int value ){
        shareEditor.putInt(key,value);
        shareEditor.commit();
    }

    public void setStringParam(String key, String  value ){
        shareEditor.putString(key,value);
        shareEditor.commit();
    }
    public String getStringParam(String key){
        return sharedPreferences.getString(key, "");
    }
}
