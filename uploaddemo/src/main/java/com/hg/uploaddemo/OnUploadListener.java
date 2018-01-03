package com.hg.uploaddemo;

/**
 * Created by slkk on 2018/1/3.
 */

public interface OnUploadListener {
    void onAllSuccess();

    void onAllFailed();

    void onThreadProgressChange(int position, int percent);

    void onThreadFinish(int position);

    void onThreadInterrupted(int position);
}
