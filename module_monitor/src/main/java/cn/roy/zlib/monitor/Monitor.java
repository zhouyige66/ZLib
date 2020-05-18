package cn.roy.zlib.monitor;

import android.content.Context;
import android.text.TextUtils;

import com.github.moduth.blockcanary.BlockCanary;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/12 16:11
 * @Version: v1.0
 */
public class Monitor {
    private Context applicationContext;
    private boolean isCrashMonitorEnable = false;
    private boolean autoSaveCrashLog =false;
    private String crashMonitorLogPath;
    private CrashExceptionHandler.CustomExceptionHandler customExceptionHandler;
    private boolean isBlockMonitorEnable = false;
    private int blockMonitorTimeout;
    private String blockMonitorLogPath;

    public Monitor(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    public Monitor setCrashMonitorEnable(boolean crashMonitorEnable) {
        isCrashMonitorEnable = crashMonitorEnable;
        return this;
    }

    public Monitor setAutoSaveCrashLog(boolean autoSaveCrashLog) {
        this.autoSaveCrashLog = autoSaveCrashLog;
        return this;
    }

    public Monitor setCrashMonitorLogPath(String crashMonitorLogPath) {
        this.crashMonitorLogPath = crashMonitorLogPath;
        return this;
    }

    public Monitor setCustomExceptionHandler(CrashExceptionHandler.CustomExceptionHandler customExceptionHandler) {
        this.customExceptionHandler = customExceptionHandler;
        return this;
    }

    public Monitor setBlockMonitorEnable(boolean blockMonitorEnable) {
        isBlockMonitorEnable = blockMonitorEnable;
        return this;
    }

    public Monitor setBlockMonitorTimeout(int blockMonitorTimeout) {
        this.blockMonitorTimeout = blockMonitorTimeout;
        return this;
    }

    public Monitor setBlockMonitorLogPath(String blockMonitorLogPath) {
        this.blockMonitorLogPath = blockMonitorLogPath;
        return this;
    }

    public void init() {
        if (isCrashMonitorEnable) {
            // 记录崩溃日志
            CrashExceptionHandler handler = CrashExceptionHandler.getInstance();
            handler.init(this.applicationContext);
            handler.setAutoSaveCrash(autoSaveCrashLog);
            if (!TextUtils.isEmpty(this.crashMonitorLogPath)) {
                handler.setLogPath(this.crashMonitorLogPath);
            }
            if (this.customExceptionHandler != null) {
                handler.setCustomExceptionHandler(customExceptionHandler);
            }
        }
        if (isBlockMonitorEnable) {
            // 在主进程初始化调用哈
            AppBlockCanaryContext appBlockCanaryContext = new AppBlockCanaryContext();
            appBlockCanaryContext.setBlockLogPath(blockMonitorLogPath);
            appBlockCanaryContext.setBlockTimeout(blockMonitorTimeout);
            BlockCanary.install(applicationContext, appBlockCanaryContext).start();
        }
    }

}
