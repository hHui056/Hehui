package com.beidouapp.et.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by allen on 2017/2/4.
 */
public class LogFileUtil {
    public static String LOG_PATH = "/storage/emulated/0";//日志文件存储路径

    /**
     * 错误写日志文件
     *
     * @param data
     */
    public static void writeErrorLog(String data) {
        data = getNowTime() + " " + data + "\n";
        File f = new File(LOG_PATH);
        if (!f.exists()) {
            f.mkdirs();
        }
        File file = new File(LOG_PATH + "/iLinkErrorlog.txt");
        if (!file.exists()) {//新建文件写入
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file, true);
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//直接写文件
            try {
                FileWriter writer = new FileWriter(file, true);
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 记录当前时间
     *
     * @return
     */
    public static String getNowTime() {
        Date time = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd(HH:mm:ss)");
        String timeString = formatter.format(time);
        return timeString;
    }
}
