package cn.roy.zlib.monitor;

import android.app.Application;
import android.content.Intent;

import com.github.moduth.blockcanary.BlockCanary;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/12 16:11
 * @Version: v1.0
 */
public class MonitorAbleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 在主进程初始化调用哈
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        // 抓起崩溃日志
        CrashMonitor.getInstance().init(this);
    }

}
