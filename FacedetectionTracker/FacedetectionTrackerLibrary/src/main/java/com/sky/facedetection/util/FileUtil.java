package com.sky.facedetection.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {

    public static String TAG = "FileUtil";
    public Context mContext;

    private static FileUtil fileUtil = null;

    public static FileUtil getInstance(Context context) {
        if (fileUtil == null) {
            synchronized (FileUtil.class) {
                if (fileUtil == null) {
                    fileUtil = new FileUtil(context);
                }
            }
        }
        return fileUtil;
    }


    public FileUtil(Context context) {
        super();
        mContext = context;
    }

    public void setCfgFile() {
        Log.i(TAG, "setCfgFile");
        mkModelDir(Constant.cfgdir);
        setLbpcascade_frontalface();
        sethape_predictor_68_face_landmarks();
        haarcascade_eye_tree_eyeglasses();
        setHaarcascade_frontalface_alt2();
        setAgeModelFile();
        setSexAndGlassesModelFile();
        //new copyFileTask().execute();
    }


private class copyFileTask extends AsyncTask<Void,Void,Void>{
    @Override
    protected Void doInBackground(Void... params) {

        Log.i(TAG,"AsyncTask doInBackground");
        setAgeModelFile();
        //setSexAndGlassesModelFile();
        return null;
    }

}

    private void  setAgeModelFile(){
        setAge_net();
        setdeploy_agePath();
        setbinaryproto();

    }
    private void setSexAndGlassesModelFile(){
        setPcamat();
        setGender();
        setGlasses();
    }

    private void mkModelDir(String path) {
        Log.i(TAG, "mkdir:" + path);
        File file = new File(path);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void setLbpcascade_frontalface() {
        Log.i(TAG, "setLbpcascade_frontalface");
        String lbpcascade_frontalfaceXmlPath = "/data/data/" + Constant.pkgName + "/model/lbpcascade_frontalface.xml";
        File file = new File(lbpcascade_frontalfaceXmlPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(lbpcascade_frontalfaceXmlPath, "lbpcascade_frontalface.xml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }

    private void sethape_predictor_68_face_landmarks() {
        Log.i(TAG, "sethape_predictor_68_face_landmarks");
        String datPath = "/data/data/" + Constant.pkgName + "/model/shape_predictor_68_face_landmarks.dat";
        File file = new File(datPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(datPath, "shape_predictor_68_face_landmarks.dat");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }

    private void haarcascade_eye_tree_eyeglasses() {
        Log.i(TAG, "haarcascade_eye_tree_eyeglasses");
        String haarcascade_eye_tree_eyeglassesXmlPath = "/data/data/" + Constant.pkgName + "/model/haarcascade_eye_tree_eyeglasses.xml";
        File file = new File(haarcascade_eye_tree_eyeglassesXmlPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(haarcascade_eye_tree_eyeglassesXmlPath, "haarcascade_eye_tree_eyeglasses.xml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }

    }

    private void setHaarcascade_frontalface_alt2() {
        Log.i(TAG, "haarcascade_frontalface_alt2");
        String haarcascade_frontalface_alt2XmlPath = "/data/data/" + Constant.pkgName + "/model/haarcascade_frontalface_alt2.xml";
        File file = new File(haarcascade_frontalface_alt2XmlPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(haarcascade_frontalface_alt2XmlPath, "haarcascade_frontalface_alt2.xml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }

    private void setAge_net() {
        Log.i(TAG, "setAge_net");
        String age_netPath = "/data/data/" + Constant.pkgName + "/model/age_net.caffemodel";

        File file = new File(age_netPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(age_netPath, "age_net.caffemodel");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }


    private void setdeploy_agePath() {
        Log.i(TAG, "sethape_predictor_68_face_landmarks");
        String deploy_agePath = "/data/data/" + Constant.pkgName + "/model/deploy_age.prototxt";
        File file = new File(deploy_agePath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(deploy_agePath, "deploy_age.prototxt");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }

    private void setbinaryproto() {
        Log.i(TAG, "setbinaryproto");
        String binaryprotoPath = "/data/data/" + Constant.pkgName + "/model/mean.binaryproto";
        File file = new File(binaryprotoPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(binaryprotoPath, "mean.binaryproto");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }

    private void setPcamat() {
        Log.i(TAG, "setPcamat");
        String pcamatPath = "/data/data/" + Constant.pkgName + "/model/pcamat.bin";
        File file = new File(pcamatPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(pcamatPath, "pcamat.bin");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }


    private void setGender() {
        Log.i(TAG, "setGender");
        String genderPath = "/data/data/" + Constant.pkgName + "/model/gender.yml";
        File file = new File(genderPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(genderPath, "gender.yml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }
    private void setGlasses() {
        Log.i(TAG, "setGlasses");
        String glassesPath = "/data/data/" + Constant.pkgName + "/model/glasses.yml";
        File file = new File(glassesPath);
        Log.i(TAG, "file.exists():" + file.exists());
        if (!file.exists()) {
            try {
                copyBigDataToData(glassesPath, "glasses.yml");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException:" + e.toString());
            }
        }
    }






    private void copyBigDataToData(String strOutFileName, String fileName) throws IOException {
        Log.i(TAG, "copyBigDataToData");
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = mContext.getAssets().open(fileName);

        byte[] buffer = new byte[1024 * 1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
        Log.i(TAG, "copyBigDataToData finish");
    }

    /**
     *  从assets目录中复制整个文件夹内容
     *  @param  context  Context 使用CopyFiles类的Activity
     *  @param  oldPath  String  原文件路径  如：/aa
     *  @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    public void copyFilesFassets(Context context,String oldPath,String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }
    }
