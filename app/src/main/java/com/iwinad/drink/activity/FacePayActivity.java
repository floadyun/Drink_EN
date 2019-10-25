package com.iwinad.drink.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.base.BaseObserver;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.api.ApiLoader;
import com.iwinad.drink.model.FaceInfoEntity;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:刷脸支付
 */
public class FacePayActivity extends AppBaseActivity {

    @BindView(R.id.face_image_view)
    ImageView faceImage;
    @BindView(R.id.person_info_view)
    View infoView;
    @BindView(R.id.identity_status_view)
    View identityStatusView;

    private Handler mHandler;

    private String imagePath;

    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_pay);
        ButterKnife.bind(this);
        imagePath = getIntent().getStringExtra(Consts.FACE_IAMGE_PATH);

        mHandler = new Handler();

        if(!TextUtils.isEmpty(imagePath)){
            uploadImage(imagePath);
        }else {
            toEndActivity();
            setIdentityFailure();
        }
    }
    /**
     * 上传图片识别
     * @param imagePath
     */
    private void uploadImage(String imagePath){
        ApiLoader.uploadImage(ImageUtil.imageToBase64(imagePath), new BaseObserver<FaceInfoEntity>(this) {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onSuccess(FaceInfoEntity value) {
                isIdentifySuccess(value);
                toEndActivity();
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                toEndActivity();
                setIdentityFailure();
            }
        });
    }
    /**
     * 跳转最后一个页面
     */
    private void toEndActivity(){
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FacePayActivity.this,EndActivity.class);
                startActivity(intent);
            }
        };
        mHandler.postDelayed(runnable,3000);
    }
    /**
     * 判断是否存在人脸信息
     * @param faceInfoEntity
     * @return
     */
    private void isIdentifySuccess(FaceInfoEntity faceInfoEntity){
        for (FaceInfoEntity faceInfo:Consts.getFaceInfoEntities()){
            if(faceInfoEntity.姓名.equals(faceInfo.姓名)){
                infoView.setBackgroundResource(faceInfoEntity.faceImage);
                faceImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                return ;
            }
        }
        setIdentityFailure();
    }
    /**
     * 识别失败
     */
    private void setIdentityFailure(){
        faceImage.setImageResource(R.drawable.avator_identity_failure);
        infoView.setBackgroundResource(R.drawable.face_failure);
        identityStatusView.setBackgroundResource(R.drawable.bg_identity_failure);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(runnable!=null){
            mHandler.removeCallbacks(runnable);
        }
    }
}
