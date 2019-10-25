package com.iwinad.drink.seriaport;


/**
 * Created by x on 2017/12/26.
 */

public abstract class BaseSerialPort<T, K>{


    public abstract void init();
    public abstract void close();
    public abstract void write(T data, ICommonResult<K> result);
}
