package com.sky.facedetectiontrackerdemo.view;

import android.util.Log;

import com.sky.facedetection.facepoint.FacePoint;

/**
 * Created by server2003 on 2017/1/11.
 */

public class FaceInfo {
    private String TAG = "FaceInfo";

    public double[] lastRotat_ver = new double[]{0,0,0};
    String rotat_ver = null;
    String dealRotat_ver = null;
    public void faceRate(FacePoint facePoint) {

        double vectorX = 0,vectorY = 0,vectorZ = 0;
        if (facePoint.face_cnt != 0) {
            for (int i = 0; i < facePoint.face_cnt; i++) {
                vectorX = facePoint.face_description[i].rotat_ver[0];
                vectorY = facePoint.face_description[i].rotat_ver[1];
                vectorZ = facePoint.face_description[i].rotat_ver[2];
                rotat_ver =  "orignal data-rotat_ver:"+(int)vectorX+"&&"+(int)vectorY+"&&"+(int)vectorZ;
               // Log.i(TAG,"orignal data-vectorX:"+(int)vectorX+",vectorY:"+(int)vectorY+",vectorZ:"+(int)vectorZ);

                if(Math.abs(vectorX)>=40){
                    vectorX = lastRotat_ver[0];
                }else {
                    if(vectorX - lastRotat_ver[0]>15){
                        vectorX = lastRotat_ver[0] +15;
                    }else if(vectorX - lastRotat_ver[0]<=-15){
                        vectorX = lastRotat_ver[0] -15;
                    }
                }
                if(Math.abs(vectorY)>=40){
                    vectorY = lastRotat_ver[1];
                }else {
                    if(vectorY - lastRotat_ver[1]>15){
                        vectorY = lastRotat_ver[1] +15;
                    }else if(vectorY - lastRotat_ver[1]<=-15){
                        vectorY = lastRotat_ver[1] -15;
                    }
                }
                if(Math.abs(vectorZ)>=40){
                    vectorZ = lastRotat_ver[2];
                }else {
                    if(vectorZ - lastRotat_ver[2]>15){
                        vectorZ = lastRotat_ver[2] +15;
                    }else if(vectorZ - lastRotat_ver[2]<=-15){
                        vectorZ = lastRotat_ver[2] -15;
                    }
                }
                lastRotat_ver[0] =vectorX;
                lastRotat_ver[1] =vectorY;
                lastRotat_ver[2] =vectorZ;
                dealRotat_ver = "deal data-rotat_ver:"+(int)lastRotat_ver[0]+"&&"+(int)lastRotat_ver[1]+"&&"+(int)lastRotat_ver[2];
                //Log.i(TAG,"deal data-vectorX:"+(int)vectorX+",vectorY:"+(int)vectorY+",vectorZ:"+(int)vectorZ);
            }
            // FaceOverlyFragment.this.mListener.onTrackInfo(2,rotat_ver);
            // FaceOverlyFragment.this.mListener.onTrackInfo(1,dealRotat_ver);
        }else{
            for(int m = 0;m<3;m++){
                lastRotat_ver[m] = 0;
            }
        }
    }

    boolean isOpenMouth = false;
    private void facePostureDetect(FacePoint facePoint){
        if (facePoint.face_cnt != 0) {

            for (int i = 0; i < facePoint.face_cnt; i++) {
                int PointCnt = facePoint.face_description[i].PointCnt;
                if (PointCnt != 0) {
                    float diatance = (float) ((facePoint.face_description[i].array[57].y+facePoint.face_description[i].array[66].y)/2 -
                            (facePoint.face_description[i].array[51].y+facePoint.face_description[i].array[62].y)/2);

                    float lipsThick = (float) (facePoint.face_description[i].array[57].y-facePoint.face_description[i].array[66].y +
                            facePoint.face_description[i].array[62].y-facePoint.face_description[i].array[51].y)/2;

                    Log.i(TAG,"diatance="+diatance);
                    Log.i(TAG,"lipsThick="+lipsThick);
                    if(diatance>lipsThick*2){
                        isOpenMouth = true;
                    }else {
                        isOpenMouth = false;
                    }
                    Log.i(TAG,"isOpenMouth:"+isOpenMouth);
                    String text = "isOpenMouth:"+isOpenMouth;
                    //FaceOverlyFragment.this.mListener.onTrackInfo(1,text);

                    //left eye
                    float leftEyeHeight = (float) ((facePoint.face_description[i].array[46].y+facePoint.face_description[i].array[47].y)/2 -
                            (facePoint.face_description[i].array[43].y+facePoint.face_description[i].array[44].y)/2);

                    float leftEyeWidth = (float) ((facePoint.face_description[i].array[45].x-facePoint.face_description[i].array[42].x));

                    Log.i(TAG,"leftEyeHeight="+leftEyeHeight);
                    Log.i(TAG,"leftEyeWidth="+leftEyeWidth);

                    //right eye
                    float rightEyeHeight = (float) ((facePoint.face_description[i].array[40].y+facePoint.face_description[i].array[41].y)/2 -
                            (facePoint.face_description[i].array[37].y+facePoint.face_description[i].array[38].y)/2);

                    float rightEyeWidth = (float) ((facePoint.face_description[i].array[39].x-facePoint.face_description[i].array[36].x));

                    Log.i(TAG,"rightEyeHeight="+rightEyeHeight);
                    Log.i(TAG,"rightEyeWidth="+rightEyeWidth);
                }
            }
        }

    }
}
