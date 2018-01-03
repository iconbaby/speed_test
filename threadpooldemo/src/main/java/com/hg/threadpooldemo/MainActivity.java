package com.hg.threadpooldemo;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn_click);
        btn.setOnClickListener(this);
        threadPoolExecutor = new ThreadPoolExecutor(3,
                30,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(6));

    }

    @Override
    public void onClick(View view) {
        /*
        * threadPoolExecutro
        * */
//        for (int i = 0; i < 30; i++) {
//            final int finali = i;
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    SystemClock.sleep(2000);
//                    Log.d(TAG, "run: " + finali);
//                }
//            };
//            threadPoolExecutor.execute(runnable);
//        }

        /*
        * fixedThreadPool
        * */
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 30; i++) {
            final int finali = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        Log.i(TAG, "run: " + finali);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            fixedThreadPool.execute(runnable);
        }

    }
}
