package com.iwinad.drink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.glide.GlideManage;
import com.iwinad.drink.Consts;
import com.iwinad.drink.util.SaveUtil;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.face.DetectorProxy;
import com.vise.face.FaceRectView;
import com.vise.face.IDataListener;
import com.iwinad.drink.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * @copyright : yixf
 *
 * @author : yixf
 *
 * @version :1.0
 *
 * @creation date: 2019/9/7
 *
 * @description:个人中心
 */
public class IdentifyMoodActivity extends AppBaseActivity {
    @BindView(R.id.face_detector_preview)
    CameraPreview mFace_detector_preview;
    @BindView(R.id.identity_mood_image)
    ImageView moodImage;
    private DetectorProxy mDetectorProxy;
    private int delayTime = 0;

    private IDataListener mDataListener = new IDataListener() {
        @Override
        public void onDetectorData(DetectorData detectorData) {
            if(detectorData.getFacesCount()>=1&&delayTime==5){
                mFace_detector_preview.getCamera().stopPreview();
                gotoSelectDrink();
                finish();
            }
        }
    };

    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_mood);
        ButterKnife.bind(this);
        initFaceDetector();

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {//延迟5秒识别
                delayTime = 5;
                gotoSelectDrink();
                finish();
            }
        },5000);
        GlideManage.getInstance().withGif(this,R.drawable.bg_expression_list,moodImage);
    }
    private void initFaceDetector(){
        //创建代理类，必须传入相机预览界面
        mDetectorProxy = new DetectorProxy.Builder(mFace_detector_preview)
                .setMinCameraPixels(3000000)
            //    .setDataListener(mDataListener)
                //设置预览相机的相机ID
                .setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .build();
    }
    /**
     * 跳转至选酒
     */
    public void gotoSelectDrink(){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        intent.putExtra(Consts.FACE_TYPE,0);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mDetectorProxy != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDetectorProxy.detector();
                }
            },1000);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mDetectorProxy != null) {
            mDetectorProxy.release();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
