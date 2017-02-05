package com.sky.facedetection.facepoint;

/**
 * Created by server2003 on 2016/11/23.
 */

public class FacearrayPoint {
    public DoublePoint[] array;
    public int PointCnt;
    public int FaceX;
    public int FaceY;
    public int FaceWidth;
    public int FaceHeight;
    public int Age;
    public int Sex; //1:male, 0:female, -1:failed
    public int isSmile;
    public int withGlass; //1: exist, -1: nonexist
    public double[] rotat_ver = new double[]{0,0,0};
    public double[] trans_ver= new double[]{0,0,0};
}
