package com.iwinad.drink.model;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/4
 * @description:
 */
public class FaceType {

    public static final int FACE_TYPE_1 = 1;

    public static final int FACE_TYPE_2 = 2;

    public int type;

    public String imagePath;

    public FaceType(int type, String imagePath) {
        this.type = type;
        this.imagePath = imagePath;
    }
}
