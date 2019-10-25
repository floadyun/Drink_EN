package com.iwinad.drink.seriaport;

/**
 * Created by x on 2017/12/29.
 */

public class SerialPortResponse {
    public SerialPortResponse(){

    }
    
    public static int ERR_SUCCEED = 0;
    public static int ERR_WAITING = 1;
    public static int ERR_START = 2;

    public static int ERR_FAILED = -1;
    public static int ERR_BUSY = -2;  // 忙碌
    public static int ERR_TIME_OUT = -3;  // 超时

    public int errorCode = 0;
    public String errorMessage = null;
    public int timeLeft = 0;    // 剩余时间，当errorCode为1是有效
}
