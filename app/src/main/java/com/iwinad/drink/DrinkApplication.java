package com.iwinad.drink;

import android.app.Application;
import android.util.Log;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.vise.log.ViseLog;
import com.vise.log.inner.LogcatTree;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/8/28
 * @description:
 */
public class DrinkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        initLogger();
    }
    /**
     * 初始化日志打印
     */
    private void initLogger(){
        AndroidLogAdapter logAdapter = new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        };
        Logger.addLogAdapter(logAdapter);

        ViseLog.getLogConfig()
                .configAllowLog(true)//是否输出日志
                .configShowBorders(true)//是否排版显示
                .configTagPrefix("ViseLog")//设置标签前缀
                .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")//个性化设置标签，默认显示包名
                .configLevel(Log.VERBOSE);//设置日志最小输出级别，默认Log.VERBOSE
        ViseLog.plant(new LogcatTree());//添加打印日志信息到Logcat的树
    }
}
