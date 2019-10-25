package com.iwinad.drink.model;

import com.base.lib.http.base.BaseEntity;

import java.io.Serializable;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/8/28
 * @description:
 */
public class FaceInfoEntity implements Serializable {
    /**
     * 姓名 : 未知
     * 性别 : 未知
     * 部门 : 未知
     * 性格 : 未知
     * 兴趣 : 未知
     */
    public String 姓名;
    public String 性别;
    public String 部门;
    public String 性格;
    public String 兴趣;
    public int faceImage;
}
