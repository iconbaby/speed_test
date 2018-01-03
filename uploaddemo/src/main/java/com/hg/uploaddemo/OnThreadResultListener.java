package com.hg.uploaddemo;

/**
 * Created by slkk on 2018/1/3.
 * 任务线程的回调接口
 */

public interface OnThreadResultListener {
    void onProgressChange(int percent);//进度变化回调

    void onFinish();//线程完成时回调

    void onInterrupted();//线程被中断回调
}
