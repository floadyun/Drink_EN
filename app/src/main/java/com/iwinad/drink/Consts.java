package com.iwinad.drink;

import com.iwinad.drink.model.FaceInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/5
 * @description:
 */
public class Consts {
    //姓名
    private static final String[] NAMES = new String[]{"果子博弈","Olia","洛小鱼","Andier","felix.zhou","Juan.Zhao"};

    private static final int[] IMAGES = new int[]{R.drawable.face_image_1,R.drawable.face_image_2,R.drawable.face_image_3,R.drawable.face_image_4,R.drawable.face_image_5,R.drawable.face_image_6};
    //识别类型
    public static final String FACE_TYPE = "face_type";
    //人脸图片路径
    public static final String FACE_IAMGE_PATH = "face_image_path";

    public static List<FaceInfoEntity> faceInfoEntities;

    public static List<FaceInfoEntity> getFaceInfoEntities(){
        if(faceInfoEntities==null){
            faceInfoEntities = new ArrayList<>();
            for (int i=0;i<NAMES.length;i++){
                FaceInfoEntity faceInfoEntity = new FaceInfoEntity();
                faceInfoEntity.姓名 = NAMES[i];
                faceInfoEntity.faceImage = IMAGES[i];
                faceInfoEntities.add(faceInfoEntity);
            }
        }
        return faceInfoEntities;
    }
}
