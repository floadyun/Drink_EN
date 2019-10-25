package com.iwinad.drink.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.ApiHelper;
import com.base.lib.http.base.BaseObserver;
import com.base.lib.util.FileUtil;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.api.ApiLoader;
import com.iwinad.drink.model.FaceInfoEntity;
import com.iwinad.drink.model.FaceType;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.animation.ValueAnimator.*;

public class MainActivity extends AppBaseActivity {

    private String imagePath;

    private Uri imgUri;

    private static final int TAKE_PHOTO = 1;

    private int[] viewIds = new int[]{R.id.main_menu_1,R.id.main_menu_2,R.id.main_menu_3,
            R.id.main_menu_4,R.id.main_menu_5,R.id.main_menu_6,R.id.main_menu_7};

    private String[] permissions = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

        for (int i=0;i<viewIds.length;i++){
            startViewAnimation(viewIds[i]);
        }
        checkPermissions(permissions, 1, new PermissionsResultListener() {
            @Override
            public void onSuccessful(int[] results) {

            }
            @Override
            public void onFailure() {

            }
        });
    }
    private void startViewAnimation(int viewId){
        View animView = findViewById(viewId);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animView,"ScaleX", 0.9f);
        scaleX.setDuration(1000);
        scaleX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(animView,"ScaleX", 1.0f);
                scaleX.setDuration(1000);
                scaleX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        startViewAnimation(viewId);
                    }
                });
                scaleX.start();
            }
        });
        scaleX.start();

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animView,"ScaleY", 0.9f);
        scaleY.setDuration(1000);
        scaleY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(animView,"ScaleY", 1.0f);
                scaleY.setDuration(1000);
                scaleY.start();
            }
        });
        scaleY.start();
    }
    /**
     * 跳转至人脸表情识别
     * @param view
     */
    public void gotoFaceRecognition(View view){
        Intent intent = new Intent(this,IdentifyMoodActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if(resultCode == RESULT_OK) {
                        uploadImage(imagePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private void uploadImage(String imagePath){
        ApiLoader.uploadImage(ImageUtil.imageToBase64(imagePath), new BaseObserver<FaceInfoEntity>(this) {
            @Override
            public void onSuccess(FaceInfoEntity value) {
                gotoSelectDrink(value);
            }
        });
    }
    private void gotoSelectDrink(FaceInfoEntity faceInfoEntity){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("faceInfo",faceInfoEntity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Subscribe
    public void onEventMainThread(FaceType faceType){
        if(faceType.type== FaceType.FACE_TYPE_1){
     //       uploadImage(faceType.imagePath);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
