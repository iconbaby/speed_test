package com.hg.countdownlatchdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new WorkThread("thread1", countDownLatch).start();
        new WorkThread("thread2", countDownLatch).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "work end");
    }

    class WorkThread extends Thread {
        private String threadName;
        private CountDownLatch countDownLatch;

        public WorkThread(String name, CountDownLatch countDownLatch) {
            this.threadName = name;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            Log.i(TAG, "thread : " + threadName + "start run");
            try {
                Thread.sleep(3000);
                countDownLatch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
