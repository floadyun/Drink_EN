package com.iwinad.drink.seriaport;

/**
 * Created by yko on 2017/12/30.
 */

public class MixDrinkInfo {
    public int timeOut;   // 调酒的等待时间
    public int type;    // 饮料的分类
    public int[] bottles;   // 所需原料所在的酒瓶
    public int[] formulaCapacitys;  // 配方所需量
}
