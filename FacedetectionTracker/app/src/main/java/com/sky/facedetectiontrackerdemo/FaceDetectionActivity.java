package com.sky.facedetectiontrackerdemo;

//2016.11.18

import com.sky.facedetectiontrackerdemo.util.Constant;
import com.sky.facedetectiontrackerdemo.adapter.ListViewAdapter;
import com.sky.facedetectiontrackerdemo.config.ConfigUtils;
import com.sky.facedetectiontrackerdemo.view.FaceOverlyFragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.sky.facedetectiontrackerdemo.util.Constant.*;

public class FaceDetectionActivity extends Activity {
    public static String TAG = "FaceDetectionActivity";

    public TextView cameraRateTextView;
    public TextView frameTimeTextView;
    public TextView detectTimeTextView;
    public TextView drawPointTimeTextView;
    public TextView cameraPreviewSize;
    public int METHOD = 0;
    public GifDrawable listviewSelectedDrawle;
    public int maskListviewSelectedPostion = -1;
    public int itemIndex = 0;
    ListView masklistview;
    ListView funcOptionListview;
    FaceOverlyFragment.MethodChooseInterface methodChooseInterface = null;
    FrameLayout faceMaskLayout;
    GifImageView gifMask;
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
    ArrayList<Integer> gifDrawableIDItems = new ArrayList<Integer>();
    private boolean isMaskListviewLayoutShown = false;
    PowerManager pManager;
    PowerManager.WakeLock mWakeLock;
    Handler faceMaskHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://显示gif
                    showMaskGif(msg.getData());
                    break;
                default:
                    break;
            }
        }
    };
    FaceOverlyFragment.TrackCallBack mTrackCallBack = new FaceOverlyFragment.TrackCallBack() {
        private int what = -1;
        private long debugTime = 0;
        private String msg;

        @Override
        public void onTrackInfo(final int what, final String msg) {
            this.what = what;
            this.msg = msg;
            showDebugMsg("info");
        }

        @Override
        public void onTrackdetected(int what, long time) {
            this.what = what;
            this.debugTime = time;
            showDebugMsg("time");
        }

        @Override
        public void onTrackMask(Bundle bundle) {
            Message msg = new Message();
            msg.what = 0;
            msg.setData(bundle);
            faceMaskHandler.sendMessage(msg); //发送消息
        }

        private void showDebugMsg(final String type) {
            FaceDetectionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (type.equals("time")) {
                        switch (what) {
                            case Constant.FN_DETECT_TIME:
                                detectTimeTextView.setText("detect time: " + debugTime);
                                break;
                            case Constant.FN_DRAW_POINT_TIME:
                                drawPointTimeTextView.setText("draw face point time: " + debugTime);
                                break;
                            case Constant.FN_CAMERA_FRAME_TIME:
                                int rate = (int) (1000 / debugTime);
                                frameTimeTextView.setText("per frame time: " + debugTime);
                                cameraRateTextView.setText("Camera rate: " + rate);
                            default:
                                break;
                        }
                    } else if (type.equals("info")) {
                        switch (what) {
                            case 1:
                                frameTimeTextView.setText(msg);
                                break;
                            case 2:
                                cameraRateTextView.setText(msg);
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, TAG + "--onCreate");
        ConfigUtils.getInstance(this).loadCameraPreviewSizeConfig();
        requestPerssion();
        setContentView(R.layout.activity_face_detection);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getScreenSize();
        initView();
    }

    public void initView() {
        funcOptionListview = (ListView) findViewById(R.id.func_option_listview);
        masklistview = (ListView) findViewById(R.id.mask_listview);

        cameraRateTextView = (TextView) findViewById(R.id.camera_rate);
        frameTimeTextView = (TextView) findViewById(R.id.per_frame_time);
        detectTimeTextView = (TextView) findViewById(R.id.detect_time);
        drawPointTimeTextView = (TextView) findViewById(R.id.drawpoint_time);
        cameraPreviewSize = (TextView) findViewById(R.id.camera_previewsize);
        cameraPreviewSize.setText("Camera:" + ConfigUtils.getInstance(this).getpreviewSizeText());

        faceMaskLayout = (FrameLayout) findViewById(R.id.layout_facemask);
        gifMask = (GifImageView) findViewById(R.id.gifmask);
        initFuncOptionListItem();
    }

    private void requestPerssion() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            //has permission, do operation directly
            Constant.haveCameraPermission = true;
            Log.i(TAG, "user has the permission already!");
        } else {
            //do not have permission
            Log.i(TAG, "user do not have this permission!");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.i(TAG, "we should explain why we need this permission!");
                new AlertDialog.Builder(this)
                        .setMessage("You need to allow access to camera permission")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(FaceDetectionActivity.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        PERMISSIONS_REQUEST_CAMERA);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
                //  return;
            } else {
                // No explanation needed, we can request the permission.
                Log.i(TAG, "==request the permission==");

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "user granted the permission!");
                    Constant.haveCameraPermission = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "user denied the permission!");
                    Constant.haveCameraPermission = false;
                    Toast.makeText(FaceDetectionActivity.this, "camera 没有权限！", Toast.LENGTH_LONG).show();
                }
                //return;
            }
        }
    }

    protected void onResume() {
        Log.i(TAG, TAG + " onResume");
        super.onResume();
        pManager = ((PowerManager) getSystemService(POWER_SERVICE));
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, "facedetect");
        mWakeLock.acquire();
        FaceOverlyFragment localFaceOverlapFragment = (FaceOverlyFragment) getFragmentManager().findFragmentById(R.id.FaceOverlyFragment);
        localFaceOverlapFragment.registTrackCallback(mTrackCallBack);
        this.methodChooseInterface = localFaceOverlapFragment.getMethodChooseInterface();
        initFuncMode();
    }

    private void initFuncOptionListItem() {
        Log.i(TAG, "initFuncOptionListItem");
        int[] thumbnails = {R.drawable.btn_sy_sx_nor, R.drawable.btn_sy_mj_nor, R.drawable.setting};
        String[] titles = {this.getResources().getString(R.string.jiance), this.getResources().getString(R.string.mask), "设置"};
        ArrayList<HashMap<String, Object>> mainItems = new ArrayList<HashMap<String, Object>>();
        for (int k = 0; k < thumbnails.length; k++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("icon", thumbnails[k]);
            map.put("name", titles[k]);
            mainItems.add(map);
        }
        //funcOptionListview.setSelection(ListView.CHOICE_MODE_NONE);
        ListViewAdapter adapter = new ListViewAdapter(this, mainItems,
                R.layout.functionlistviewitem, new String[]{"icon", "name"},
                new int[]{R.id.icon, R.id.name});
        funcOptionListview.setAdapter(adapter);
        funcOptionListview.post(new Runnable() {
            @Override
            public void run() {
                funcOptionListview.getChildAt(0).setBackgroundColor(Color.rgb(136, 136, 136));
            }
        });
        funcOptionListview.setOnItemClickListener(new mMainListviewOnItemClickListener());
    }

    public boolean initMaskListItemDone = false;

    private void initMaskListItem() {
        Log.i(TAG, "initMaskListItem");
        if (initMaskListItemDone) {
            return;
        }
        initMaskListItemDone = true;
        int[] thumbnailsID = {R.drawable.mask1_thumbnails, R.drawable.mask2_thumbnails, R.drawable.mask3_thumbnails, R.drawable.mask4_thumbnails,R.drawable.glasses};
        int[] gifDrawableID = {R.drawable.mask1, R.drawable.mask2, R.drawable.mask3, R.drawable.mask4,R.drawable.glasses};
        for (int k = 0; k < thumbnailsID.length; k++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("icon", thumbnailsID[k]);
            items.add(map);
        }
        for (int k = 0; k < gifDrawableID.length; k++) {
            gifDrawableIDItems.add(gifDrawableID[k]);
        }
        masklistview.setSelection(ListView.CHOICE_MODE_SINGLE);
        ListViewAdapter adapter = new ListViewAdapter(this, items,
                R.layout.masklistviewitem, new String[]{"icon"},
                new int[]{R.id.mask_icon});
        masklistview.setAdapter(adapter);
        masklistview.setOnItemClickListener(new mListviewOnItemClickListener());
    }

    private void initFuncMode() {
        funcOptionListview.setSelection(0);
        if (this.methodChooseInterface != null)
            this.methodChooseInterface.onMethodChoose(METHOD);
    }

    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Constant.screenHeight = display.getHeight();
        Constant.screenWidth = display.getWidth();
        Log.i(TAG, "screenHeight=" + Constant.screenHeight + ",screenWidth=" + Constant.screenWidth);
    }

    private void showMaskGif(Bundle bundle) {
        Log.i(TAG, "showMaskGif by id");

        float faceWidth = bundle.getFloat("faceWidth");
        float faceHeight = bundle.getFloat("faceHeight");
        float centerX = bundle.getFloat("centerX");
        float centerY = bundle.getFloat("centerY");
        boolean isDetectedFace = bundle.getBoolean("isDetectedFace");
        Log.i(TAG, "showMaskGif isDetectedFace=" + isDetectedFace);
        boolean isGlasses = false;
        if (isDetectedFace) {



            if (listviewSelectedDrawle == null&&maskListviewSelectedPostion!=-1) {
                try {
                    listviewSelectedDrawle = new GifDrawable(FaceDetectionActivity.this.getResources(), gifDrawableIDItems.get(maskListviewSelectedPostion));
                     isGlasses = this.getResources().getResourceName(gifDrawableIDItems.get(0)).contains("glasses");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "new GifDrawable IOException:" + e.toString());
                }

//                try {
//                    listviewSelectedDrawle = new GifDrawable(FaceDetectionActivity.this.getResources(), gifDrawableIDItems.get(0));
//                    boolean isGlasses = this.getResources().getResourceName(gifDrawableIDItems.get(0)).contains("glasses");
// this.getResources().getResourceName(gifDrawableIDItems.get(0)).indexOf("glasses");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "new GifDrawable IOException:" + e.toString());
//                }
            }

            Matrix matrix = new Matrix();
            int gifWidth = listviewSelectedDrawle.getIntrinsicWidth();
            int gifHeight = listviewSelectedDrawle.getIntrinsicHeight();
            Log.i(TAG, "gifWidth=" + gifWidth + ",gifHeight=" + gifHeight);
            float scaleX = faceWidth / gifWidth;
            float scaleY = faceHeight / gifHeight;
            Log.i(TAG, "scaleX=" + scaleX + ",scaleY=" + scaleY);
            if(isGlasses){
                float transX = centerX - gifWidth / 2 + leftMargin;
                float transY = centerY - gifHeight / 2;
                Log.i(TAG, "transX=" + transX + ",transY=" + transY);

                matrix.preScale(scaleX, scaleX, centerX + leftMargin, centerY);
                matrix.preTranslate(transX, transY);

            }else{
                float transX = centerX - gifWidth / 2 + leftMargin;
                float transY = centerY - gifHeight / 2;
                Log.i(TAG, "transX=" + transX + ",transY=" + transY);

                matrix.preScale(scaleX, scaleX, centerX + leftMargin, centerY);
                matrix.preTranslate(transX, transY);
            }

            gifMask.setImageMatrix(matrix);
            gifMask.setImageDrawable(listviewSelectedDrawle);
        }
    }

    private void onMainClick(int position) {
        if (position == 0) {//jiance
            METHOD = position;
            if (this.methodChooseInterface != null)
                this.methodChooseInterface.onMethodChoose(METHOD);
            if (isMaskListviewLayoutShown) {
                isMaskListviewLayoutShown = false;
                masklistview.setVisibility(View.GONE);
            }
            faceMaskLayout.setVisibility(View.GONE);
            gifMask.setVisibility(View.GONE);

        } else if (position == 1) {//mask
            METHOD = position;
            if (this.methodChooseInterface != null)
                this.methodChooseInterface.onMethodChoose(position);
            initMaskListItem();
            faceMaskLayout.setVisibility(View.VISIBLE);
            gifMask.setVisibility(View.VISIBLE);
            if (!isMaskListviewLayoutShown) {
                isMaskListviewLayoutShown = true;
                masklistview.setVisibility(View.VISIBLE);
                TranslateAnimation localTranslateAnimation1 = new TranslateAnimation(1, 0.0F, 1, 0.0F, 1, 1.0F, 1, 0.0F);
                localTranslateAnimation1.setDuration(300L);
            }
        } else if (position == 2) {
            settingDialog();
        }
    }

    public void settingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cameraResolution);
        builder.setSingleChoiceItems(previewSize, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "which=" + which);
                itemIndex = which;
            }
        });
        builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ConfigUtils.getInstance(FaceDetectionActivity.this).resetPreviewSize(itemIndex);
                Toast.makeText(FaceDetectionActivity.this, R.string.restart, Toast.LENGTH_LONG).show();
                //FaceDetectionActivity.this.finish();
            }
        });

        builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public class mMainListviewOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "OnItemClickListener-position:" + position);
            if (((ListView) parent).getTag() != null) {
                ((View) ((ListView) parent).getTag()).setBackgroundDrawable(null);
            }
            ((ListView) parent).setTag(view);
            view.setBackgroundColor(Color.rgb(136, 136, 136));
            if (position != 0) {
                funcOptionListview.getChildAt(0).setBackgroundDrawable(null);
            }
            onMainClick(position);
        }
    }

    public class mListviewOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "OnItemClickListener-position:" + position);
            if (((ListView) parent).getTag() != null) {
                ((View) ((ListView) parent).getTag()).setBackgroundDrawable(null);
            }
            ((ListView) parent).setTag(view);
            view.setBackgroundColor(Color.rgb(106, 180, 93));
            maskListviewSelectedPostion = position;
        }
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, TAG + " onPause");
        if (null != mWakeLock) {
            mWakeLock.release();
        }
    }

    protected void onStop() {
        super.onStop();
        initMaskListItemDone = false;
        Log.i(TAG, TAG + " onStop");
    }

    protected void onDestroy() {
        Log.i(TAG, TAG + " onDestroy");
        super.onDestroy();
    }
}