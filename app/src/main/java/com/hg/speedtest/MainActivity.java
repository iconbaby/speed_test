package com.hg.speedtest;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.os.SystemProperties;
import android.app.Activity;

import java.io.BufferedReader;

//import com.hisilicon.android.hisysmanager.HiSysManager;

//import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";
    public static final String TAG = "nettest";

    private TextView connnectType, downloadNowSpeed, downloadAverageSpeed;
    private Button btn;
    private ImageView needle;
    private FileInfo downloadFileInfo;
    private FileInfo uploadFileInfo;
    private boolean flag;
    private int lastDegree = 0, currentDegree;

    private TextView uploadNowSpeed;
    private TextView uploadAverageSpeed;
    private MultiThread multiThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        uploadFileInfo = new FileInfo();
        downloadFileInfo = new FileInfo();
        connnectType = (TextView) findViewById(R.id.connect_type);
        downloadNowSpeed = (TextView) findViewById(R.id.downlaod_now_speed);
        downloadAverageSpeed = (TextView) findViewById(R.id.download_ave_speed);
        uploadNowSpeed = (TextView) findViewById(R.id.upload_now_speed);
        uploadAverageSpeed = (TextView) findViewById(R.id.upload_ave_speed);
        needle = (ImageView) findViewById(R.id.needle);
        btn = (Button) findViewById(R.id.start_btn);
        btn.setOnClickListener(this);
    }

    private boolean isNetworkAvailible() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {

            return networkInfo.isConnected();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (isNetworkAvailible()) {
            connnectType.setText("网络已连接");
            btn.setText("测试中");
            pingNetWork();
        } else {
            connnectType.setText("网络未连接");
            Toast.makeText(this, "请连接网络后再测速", Toast.LENGTH_LONG).show();
        }

    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x001:
                    String pingTime = (String) msg.obj;
                    btn.setEnabled(false);
                    new DownloadThread().start();
                    new GetInfoThread().start();
//                    new UploadThread().start();
                    Log.i(TAG, "handleMessage: succefull" + pingTime);
                    break;
                case 0x002:
                    Log.i(TAG, "handleMessage: fail");
                    Toast.makeText(getApplicationContext(), "测速服务器为ping通", Toast.LENGTH_SHORT).show();
                    break;
                case 0x123:
                    ArrayList<Integer> infolist = (ArrayList<Integer>) msg.obj;
                    downloadNowSpeed.setText(infolist.get(0) + "KB/S");
                    downloadAverageSpeed.setText(infolist.get(1) + "KB/S");
                    uploadNowSpeed.setText(infolist.get(2) + "KB/S");
                    uploadAverageSpeed.setText(infolist.get(3) + "KB/S");
                    startAnimation(infolist.get(0));
                    break;
                case 0x100:
                    downloadNowSpeed.setText("0KB/S");
                    uploadNowSpeed.setText("0KB/S");
                    startAnimation(0);
                    btn.setText("开始测试");
                    btn.setEnabled(true);
                    break;

            }
        }
    };

    private void pingNetWork() {

        new Thread() {
            @Override
            public void run() {
                String line = "";
                String result = "";
                try {
                    Process p;
                    p = Runtime.getRuntime().exec(
                            "ping -c 1 " + "8.8.8.8");
                    int status = p.waitFor();
                    InputStream inputStream = p.getInputStream();
                    InputStreamReader bis = new InputStreamReader(inputStream);
                    BufferedReader br = new BufferedReader(bis);
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                    //从结果中读取ping延时
                    result = result.split("time=")[1].split("ms")[0];
                    Log.i(TAG, "pingNetWork: " + result);

                    if (status == 0) {
                        // success
                        Log.i(TAG, "status ==" + status);
                        Message msg = Message.obtain();
                        msg.obj = result;
                        msg.what = 0x001;
                        myHandler.sendMessage(msg);
                    } else {
                        // failed
                        Log.i(TAG, "status ==" + status);
                        myHandler.sendEmptyMessage(0x002);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    class DownloadThread extends Thread {

        @Override
        public void run() {
//            downloadWithHtpConc();
//            downloadWithRetrofit();
            //文件下载路径
            String filePath = "http://tds.ott.cp31.ott.cibntv.net/youku_downpage/cibn_cCIBN_YouKu_for_v5.1.0_B2017_02_27.apk";
            //文件保存路径
            String destination = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "speed";
            //打算开启的线程数
            int threadNum = 5;
            multiThread = new MultiThread();
            multiThread.download(filePath, destination, threadNum);
        }

//        private void downloadWithHtpConc() {
//            //String url_string = "http://172.16.2.202:8080/strurts2fileupload/test.rar";
////            String url_string = "http://tds.ott.cp31.ott.cibntv.net/youku_downpage/cibn_cCIBN_YouKu_for_v5.1.0_B2017_02_27.apk";
//            //            String url_string = "http://172.16.2.202:8089/Test2/test.rar";
////            String downloadUrl = SystemProperties.get("persist.sys.nettest.DOWNLOADURL");
//            String downloadUrl = "http://tds.ott.cp31.ott.cibntv.net/youku_downpage/cibn_cCIBN_YouKu_for_v5.1.0_B2017_02_27.apk";
//            if (downloadUrl == null) {
//                Log.e(TAG, "read downloadurl error");
//                return;
//            }
//            String url_string = downloadUrl;
//            long start_time, cur_time = 0;
//            URL url;
//            HttpURLConnection connection;
//            InputStream iStream = null;
//            OutputStream outputStream = null;
//            int len;
//            try {
//                url = new URL(url_string);
//                connection = (HttpURLConnection) url.openConnection();
//                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File
//                        .separator + "currentSpeed";
//                Log.d(TAG, "downloadWithHtpConc: " + fileDir);
//                File dir = new File(fileDir);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                downloadFileInfo.totalByte = connection.getContentLength();
//                File file = new File(fileDir, "test");
//                iStream = connection.getInputStream();
//                outputStream = new FileOutputStream(file);
//                start_time = System.currentTimeMillis();
//                byte[] buffer = new byte[4 * 1024];
//                while ((len = iStream.read(buffer)) != -1 && flag && (cur_time - start_time) <= 16000) {
//                    outputStream.write(buffer, 0, len);
//                    downloadFileInfo.hadFinishByte += len;
//                    cur_time = System.currentTimeMillis();
//                    if (cur_time - start_time == 0) {
//                        downloadFileInfo.spendTime = 1;
//                        downloadFileInfo.currentSpeed = 1000;
//                    } else {
//                        downloadFileInfo.spendTime = cur_time - start_time;
////                        downloadFileInfo.currentSpeed = downloadFileInfo.hadFinishByte / (cur_time - start_time) * 1000;
//                    }
//                }
//                outputStream.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    iStream.close();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    class GetInfoThread extends Thread {

        @Override
        public void run() {

            double downloadSpeedSum, counter, uploadSpeedSum;
            int downloadspeed, downloadAvarageSpeed, uploadAverageSpeed, uploadSpeed;

            try {
                downloadSpeedSum = 0;
                uploadSpeedSum = 0;
                counter = 0;
                while (multiThread.threadNum > 0) {
                    Thread.sleep(1000);
                    if (multiThread.downloadInfo == null) {
                        continue;
                    }
                    downloadFileInfo.currentSpeed = multiThread.downloadInfo.getCurrentSpeed();
                    uploadFileInfo.currentSpeed = 0;

                    downloadspeed = (int) (downloadFileInfo.currentSpeed / 1024);


                    uploadAverageSpeed = 0;
                    uploadSpeed = 0;
                    Log.e("Test", "downloadspeed:" + downloadFileInfo.currentSpeed / 1024 + "KB/S downloadAvarageSpeed:" );
                    Log.i(TAG, "run: " + uploadSpeed);
                    Log.i(TAG, "time: " + downloadFileInfo.spendTime);
                    Log.i(TAG, "time: " + uploadFileInfo.spendTime);
                    ArrayList<Integer> infoList = new ArrayList<Integer>();
                    infoList.add(downloadspeed);
                    infoList.add(0);
                    infoList.add(uploadSpeed);
                    infoList.add(uploadAverageSpeed);
                    Message msg = new Message();
                    msg.what = 0x123;
                    msg.obj = infoList;
                    myHandler.sendMessage(msg);
                    if (downloadFileInfo.spendTime > 15000 || uploadFileInfo.spendTime > 15000) {
                        break;
                    }
                }
                long endTime = System.currentTimeMillis();
                downloadAvarageSpeed = (int) ((multiThread.downloadInfo.getTotalByte()/1024)/(endTime-multiThread.downloadInfo.startTime)*1000);
                ArrayList<Integer> infoList = new ArrayList<Integer>();
                infoList.add(0);
                infoList.add(downloadAvarageSpeed);
                infoList.add(0);
                infoList.add(0);
                Message msg = new Message();
                msg.what = 0x123;
                msg.obj = infoList;
                myHandler.sendMessage(msg);

                if (((downloadFileInfo.hadFinishByte == downloadFileInfo.totalByte) || (downloadFileInfo.spendTime >= 15000) || (uploadFileInfo.spendTime >= 15000)) && flag) {
                    myHandler.sendEmptyMessage(0x100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /*
     *上传线程
     */
    class UploadThread extends Thread {
        @Override
        public void run() {
            try {
//                String uploadUrl = SystemProperties.get("persist.sys.nettest.UPLOADURL");
                String uploadUrl = "";
//                String sn = getSn();
                String sn = "12313";
                String date = getDate();
                String fileName = date.trim();
                if (sn == null) {
                    sn = "0000000";
                }

                if (uploadUrl == null) {
                    Log.e(TAG, "not read the upload url");
                    return;
                }

                File file = new File("/data/local", "test.rar");
                uploadFromBySocket(null, "uploadFile", file, fileName, uploadUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        flag = false;
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        flag = true;
        super.onResume();
    }


    //速度指针旋转动画
    private void startAnimation(int cur_speed) {
        currentDegree = getDegree(cur_speed);

        RotateAnimation rotateAnimation = new RotateAnimation(lastDegree, currentDegree, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1000);
        lastDegree = currentDegree;
        needle.startAnimation(rotateAnimation);
    }

    //速度指针旋转角度
    private int getDegree(double cur_speed) {
        int ret = 0;
        if (cur_speed >= 0 && cur_speed < 128) {
            ret = (int) (15.0 * (cur_speed / 128.0));
        } else if (cur_speed >= 128 && cur_speed < 256) {
            ret = (int) (15.0 + 15.0 / 256.0 * cur_speed);
        } else if (cur_speed >= 256 && cur_speed < 512) {
            ret = (int) (30.0 + 30.0 / 256.0 * cur_speed);
        } else if (cur_speed >= 512 && cur_speed < 1024) {
            ret = (int) (60.0 + 30.0 / 512.0 * cur_speed);
        } else if (cur_speed >= 1024 && cur_speed < 2048) {
            ret = (int) (90.0 + 10.0 / 1024.0 * cur_speed);
        } else if (cur_speed >= 1024 && cur_speed < 3072) {
            ret = (int) (100.0 + 20.0 / 2048.0 * (cur_speed - 1024));
        } else if (cur_speed >= 3072 && cur_speed < 5120) {
            ret = (int) (110.0 + 30.0 / 2048.0 * (cur_speed - 2048));
        } else if (cur_speed >= 5120 && cur_speed < 10240) {
            ret = (int) (140.0 + 40.0 / 5120.0 * (cur_speed - 5120.0));
        } else {
            ret = 180;
        }
        Log.i(TAG, "getDegree: " + ret);
        return ret;
    }

    //当前时间
    private String getDate() {
        long timeMillis = System.currentTimeMillis();
        Date date = new Date(timeMillis);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH_mm_ss");
        String dateTime = simpleDateFormat.format(date);
        return dateTime;
    }

    /**
     * @param params       传递的普通参数
     * @param uploadFile   需要上传的文件名
     * @param fileFormName 需要上传文件表单中的名字
     * @param newFileName  上传的文件名称，不填写将为uploadFile的名称
     * @param urlStr       上传的服务器的路径
     * @throws IOException
     */
    public void uploadFromBySocket(Map<String, String> params,
                                   String fileFormName, File uploadFile, String newFileName,
                                   String urlStr) throws IOException {
        if (newFileName == null || newFileName.trim().equals("")) {
            newFileName = uploadFile.getName();
        }

        StringBuilder sb = new StringBuilder();
        /**
         * 普通的表单数据
         */
        if (params != null)
            for (String key : params.keySet()) {
                sb.append("--" + BOUNDARY + "\r\n");
                sb.append("Content-Disposition: form-data; name=\"" + key
                        + "\"" + "\r\n");
                sb.append("\r\n");
                sb.append(params.get(key) + "\r\n");
            }
        else {
            sb.append("\r\n");
        }

        /**
         * 上传文件的头
         */
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"" + fileFormName
                + "\"; filename=\"" + newFileName + "\"" + "\r\n");
        sb.append("Content-Type: image/jpeg" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
        sb.append("\r\n");

        byte[] headerInfo = sb.toString().getBytes("UTF-8");
        byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

        System.out.println(sb.toString());

        URL url = new URL(urlStr);
        Socket socket = new Socket(url.getHost(), url.getPort());
        OutputStream os = socket.getOutputStream();
        PrintStream ps = new PrintStream(os, true, "UTF-8");

        // 写出请求头
        ps.println("POST " + urlStr + " HTTP/1.1");
        ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
        ps.println("Content-Length: "
                + String.valueOf(headerInfo.length + uploadFile.length()
                + endInfo.length));

        InputStream in = new FileInputStream(uploadFile);
        uploadFileInfo.totalByte = (int) uploadFile.length();
        // 写出数据
        os.write(headerInfo);
        long start_time = System.currentTimeMillis();
        long cur_time = 0;
        byte[] buf = new byte[1024 * 4];
        int len;
        while (((len = in.read(buf)) != -1) && (cur_time - start_time) <= 16000) {
            cur_time = System.currentTimeMillis();
            os.write(buf, 0, len);
            uploadFileInfo.hadFinishByte += len;
            if (cur_time - start_time == 0) {
                uploadFileInfo.currentSpeed = 1000;
                uploadFileInfo.spendTime = 1000;
            } else {
                uploadFileInfo.spendTime = cur_time - start_time;
//                uploadFileInfo.currentSpeed = uploadFileInfo.hadFinishByte / (cur_time - start_time) * 1000;
            }

        }
        os.write(endInfo);
        in.close();
        os.close();
        ps.close();
    }

//    private String getSn() {
//        String sn = null;
//        HiSysManager hiSysManager = new HiSysManager();
//        try {
//            hiSysManager.getFlashInfo("deviceinfo", 18, 24);
//            sn = readFile("/mnt/mtdinfo");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.i(TAG, "sn=" + sn);
//        return sn;
//    }

//    public String readFile(String fileName) throws IOException {
//        String res = null;
//        File file = new File(fileName);
//        if (!file.exists()) {
//            return null;
//        } else {
//            FileInputStream fis = new FileInputStream(file);
//            int length = fis.available();
//            byte[] buffer = new byte[length];
//            fis.read(buffer);
//            res = EncodingUtils.getString(buffer, "UTF-8");
//            fis.close();
//        }
//        return res;
//    }

}


