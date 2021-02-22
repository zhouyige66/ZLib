package cn.roy.zlib.log;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import ch.qos.logback.classic.Level;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/02/15
 * @Version: v1.0
 */
public class Sample {

    public void config(Context context) {
        String storagePath = Environment.getDataDirectory().getAbsolutePath();
        boolean granted = AndroidStorageUtil.isStoragePermissionGranted(context);
        if (AndroidStorageUtil.isExternalStorageAvailable() && granted) {
            storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            storagePath = storagePath.concat(File.separator).concat(context.getPackageName());
        }
        // 日志文件夹
        String logFilePath = storagePath.concat(File.separator).concat("log");
        String logFileName = logFilePath + File.separator + "%s.txt";
        String fileNamePattern = logFilePath + File.separator + "%s.%d{yyyy-MM-dd}.%i.txt";
        FileAppenderProperty debugProp = create(Level.DEBUG, logFileName, fileNamePattern);
        FileAppenderProperty infoProp = create(Level.INFO, logFileName, fileNamePattern);
        FileAppenderProperty warnProp = create(Level.WARN, logFileName, fileNamePattern);
        FileAppenderProperty errorProp = create(Level.ERROR, logFileName, fileNamePattern);
    }

    private FileAppenderProperty create(Level level, String logFileName, String fileNamePattern) {
        String logNamePrefix = level.levelStr.toLowerCase();
        String logFilePath = String.format(logFileName, logNamePrefix);
        String rollingFileNamePattern = fileNamePattern.replace("%s", logNamePrefix);

        // 读取手机存储
        long internalTotalSize = AndroidStorageUtil.getInternalTotalSize();
        long sdCardTotalSize = AndroidStorageUtil.getSDCardTotalSize();
        long max = Math.max(internalTotalSize, sdCardTotalSize);
        // 存储文件总大小为存储的1/10;
        long totalFileSize = max / 20;
        // 单个文件最大10M
        long singleFileSize = 1024 * 1024 * 10;
        // 默认保存最大天数为7
        int maxHistory = 7;
        FileAppenderProperty prop = new FileAppenderProperty.Builder(level)
                .setLogFilePath(logFilePath)
                .setLogFileNamePattern(rollingFileNamePattern)
                .setSingleFileSize(singleFileSize)
                .setTotalFileSize(totalFileSize)
                .setMaxHistory(maxHistory)
                .build();

        return prop;
    }

}
