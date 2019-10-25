package com.iwinad.drink.seriaport;

/**
 * Created by yko on 2017/12/30.
 */

public interface ICommonResult<T> {
    void callback(T data);
}
