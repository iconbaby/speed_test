package com.hg.speedtest;

import android.content.ComponentName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by slkk on 2018/1/2.
 */

public class MultiThread {
    public FileInfo downloadInfo;
    public  int threadNum = 5;

    public void download(String filePath, String dest, int threadNum) {
        downloadInfo = new FileInfo();
        try {
            URL url = new URL(filePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                int fileSize = conn.getContentLength();
                downloadInfo.setTotalByte(fileSize);
                String fileName = getFileName(filePath);
                File file = new File(dest + File.separator + fileName);
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(fileSize);
                raf.close();
                int block = fileSize % threadNum == 0 ? fileSize / threadNum : fileSize / threadNum + 1;
                long start_time = System.currentTimeMillis();
                downloadInfo.startTime = start_time;
                for (int threadld = 0; threadld < threadNum; threadld++) {
                    new DownloadThread(threadld, block, file, url).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //由路径获取文件名。
    private String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }

    //文件下载线程
    class DownloadThread extends Thread {

        long end_time = 0;
        int hasDownload = 0;
        int start, end, threadId;
        File file = null;
        URL url = null;

        public DownloadThread(int threadId, int block, File file, URL url) {
            this.threadId = threadId;
            start = block * threadId;
            end = block * (threadId + 1) - 1;
            this.file = file;
            this.url = url;
        }

        public void run() {
            try {
                //获取连接并设置相关属性。
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                //此步骤是关键。
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
                if (conn.getResponseCode() == 206) {
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    //移动指针至该线程负责写入数据的位置。
                    raf.seek(start);
                    //读取数据并写入
                    InputStream inStream = conn.getInputStream();
                    byte[] b = new byte[1024];
                    int len = 0;

                    while ((len = inStream.read(b)) != -1) {
                        hasDownload += len;
                        long currentTime = System.currentTimeMillis();
                        downloadInfo.setCurrentSpeed(hasDownload / (currentTime - downloadInfo.startTime) * 1000);
                        raf.write(b, 0, len);
                    }
                    System.out.println("线程" + threadId + "下载完毕");
                    System.out.println("downloadInfo.currentSpeed" + downloadInfo.getCurrentSpeed());
                    threadNum -= 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
