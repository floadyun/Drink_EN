package com.iwinad.drink.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.base.lib.util.AppManager;
import com.iwinad.drink.R;
import com.base.lib.baseui.AppBaseActivity;

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
public class EndActivity extends AppBaseActivity {

    private Handler mHandler;

    private int[] viewIds = new int[]{R.id.end_menu_1,R.id.end_menu_2,R.id.end_menu_3};

    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);

        mHandler = new Handler();

        runnable = new Runnable() {//5秒后返回主页
            @Override
            public void run() {
                AppManager.getInstance().killActivity(IdentifyMoodActivity.class);
                AppManager.getInstance().killActivity(SelectDrinkActivity.class);
                AppManager.getInstance().killActivity(FaceRecognitionActivity.class);
                AppManager.getInstance().killActivity(FacePayActivity.class);
                finishSelf();
            }
        };
        mHandler.postDelayed(runnable,60*1000);
        for (int i=0;i<viewIds.length;i++){
            startViewAnimation(viewIds[i]);
        }
    }
    private void startViewAnimation(int viewId){
        View animView = findViewById(viewId);
        AnimatorSet animatorSet = new AnimatorSet();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(runnable!=null){
            mHandler.removeCallbacks(runnable);
        }
    }
}
