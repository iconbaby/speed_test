package com.hg.uploaddemo;

/**
 * Created by slkk on 2018/1/3.
 */

public interface OnAllThreadResultListener {
    void onSuccess();//所有线程执行完毕

    void onFailed();//所有线程执行出现问题
}
