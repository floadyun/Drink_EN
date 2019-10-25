package com.iwinad.drink.api;

import com.base.lib.http.RetrofitManager;
import com.base.lib.http.base.BaseLoader;
import com.iwinad.drink.model.FaceInfoEntity;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/8/28
 * @description:
 */
public class ApiLoader extends BaseLoader {

    public static ApiService aService = RetrofitManager.getRetrofit().create(ApiService.class);

    interface ApiService{
        /**
         * 上传用户头像
         * @return
         */
        @FormUrlEncoded
        @POST("face")
        Observable<FaceInfoEntity> uploadAvatarObservable(@FieldMap Map<String,String> image);
    }
    /**
     * 获取个人用户信息
     * @param observer
     */
    public static void uploadImage(String imageBase64, Observer<FaceInfoEntity> observer){
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("file",imageBase64);
        setSubscribe(aService.uploadAvatarObservable(paramsMap),observer);
    }
}