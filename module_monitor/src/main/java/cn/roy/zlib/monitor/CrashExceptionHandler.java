package cn.roy.zlib.monitor;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Description 自定义异常处理
 * @Author kk20
 * @Date 2017/5/18
 * @Version V1.0.0
 */
public class CrashExceptionHandler implements UncaughtExceptionHandler {
    private static CrashExceptionHandler instance;// CrashHandler实例

    // 程序的Context对象
    private Context mContext;
    // 系统默认的UncaughtException处理类
    private UncaughtExceptionHandler mDefaultHandler;
    // 用来存储设备信息和异常信息
    private Map<String, String> infoMap = new HashMap<>();
    // 日志文件名的一部分
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    private boolean autoSaveCrash = false;
    private String logPath;
    private CustomExceptionHandler customExceptionHandler = null;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashExceptionHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static synchronized CrashExceptionHandler getInstance() {
        if (instance == null) {
            synchronized (CrashExceptionHandler.class) {
                if (instance == null) {
                    instance = new CrashExceptionHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        if (autoSaveCrash) {
            // 收集设备参数信息
            collectDeviceInfo();
            // 保存日志文件
            saveCrashInfo2File(ex);
        }
        if (customExceptionHandler != null) {
            customExceptionHandler.handleException(ex);
            System.exit(0);
            return true;
        }

        return false;
    }

    private void collectDeviceInfo() {
        infoMap.clear();
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infoMap.put("versionName", versionName);
                infoMap.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String result = writer.toString();
        sb.append("---------------异常堆栈信息---------------");
        sb.append(result);
        sb.append("---------------异常堆栈信息结束---------------");
        try {
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + ".log";
            logPath = logPath == null ?
                    (mContext.getCacheDir().getAbsolutePath() + File.separator)
                    : logPath;
            String path = logPath.endsWith("/") ? logPath : (logPath.concat("/"))
                    + "crash" + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path + fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void setAutoSaveCrash(boolean autoSaveCrash) {
        this.autoSaveCrash = autoSaveCrash;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setCustomExceptionHandler(CustomExceptionHandler customExceptionHandler) {
        this.customExceptionHandler = customExceptionHandler;
    }

    public interface CustomExceptionHandler {
        void handleException(Throwable ex);
    }

}
