package com.hg.randomaccessfiledemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread() {
            @Override
            public void run() {
                String filesDir = getFilesDir().toString();
                File file = new File(filesDir + File.separator + "tst.txt");
                Log.i(TAG, "run: " + filesDir);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    raf.write("abcde".getBytes());
                    raf.seek(3);
                    int read = raf.read();
                    Log.i(TAG, "run: " + (char) read);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
