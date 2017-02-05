package com.sky.facedetectiontrackerdemo.util;

public class Constant {
    public static final int PERMISSIONS_REQUEST_CAMERA = 0;
    public static final int FN_CAMERA_FRAME_TIME = 1;
    public static final int FN_DETECT_TIME = 2;
    public static final int FN_DRAW_POINT_TIME = 3;

    public static String pkgName = "com.sky.facedetectiontrackerdemo";
    public static String cfgdir = "/data/data/com.sky.facedetectiontrackerdemo/model/";
    public static String[] previewSize = new String[]{"1280*720", "640*480"};
    public static int[][] previewSizeArray = {{1280, 720}, {640, 480}};
    public static int previewWidth = 640;
    public static int previewHeight = 480;
    public static int screenWidth = 1920;
    public static int screenHeight = 1080;
    public static float PreViewScale = 0;
    public static int leftMargin = 240;
    public static int ScaledWidth;
    public static int ScaledHeight;
    public static int previewBufLength = 0;
    public static boolean isShowScreenshot = true;
    public static boolean haveCameraPermission = false;

}
