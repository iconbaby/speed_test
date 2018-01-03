package com.hg.uploaddemo;


import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by slkk on 2018/1/3.
 */

public class UploadFile implements Runnable {
    private CountDownLatch downLatch;//计数器
    private String fileName;
    private OnThreadResultListener listener;//任务线程回调接口
    private int percent = 0;
    private Random mRandom;

    public UploadFile(CountDownLatch downLatch, String fileName, OnThreadResultListener listener) {
        this.downLatch = downLatch;
        this.fileName = fileName;
        this.listener = listener;
        mRandom = new Random();
    }

    @Override
    public void run() {
        try {
            while (percent <= 100) {
                listener.onProgressChange(percent);
                percent += 1;
                Thread.sleep(mRandom.nextInt(60) + 30);
            }
            this.downLatch.countDown();
            listener.onFinish();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onInterrupted();
        }
    }
}
