package cn.roy.zlib.monitor;

import android.content.Context;

import com.github.moduth.blockcanary.BlockCanary;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/12 16:11
 * @Version: v1.0
 */
public class Monitor {
    private Context context;
    private int blockMonitorTimeout;

    private static Monitor instance = null;

    public static Monitor getInstance(){
        if(instance == null){
            synchronized (Monitor.class){
                if(instance == null){
                    instance = new Monitor();
                }
            }
        }
        return instance;
    }

    public Monitor setContext(Context context) {
        this.context = context.getApplicationContext();
        return this;
    }

    public Monitor setBlockMonitorTimeout(int blockMonitorTimeout) {
        this.blockMonitorTimeout = blockMonitorTimeout;
        return this;
    }

    public  void init() {
        // 在主进程初始化调用哈
        BlockCanary.install(context, new AppBlockCanaryContext(blockMonitorTimeout)).start();
        // 记录崩溃日志
        CrashMonitor.getInstance().init(context.getApplicationContext());
    }

}
