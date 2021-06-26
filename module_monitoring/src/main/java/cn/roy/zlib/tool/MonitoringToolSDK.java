package cn.roy.zlib.tool;

import android.content.Context;

import cn.roy.zlib.tool.core.Recorder;

/**
 * @Description 悬浮窗监视日志SDK
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public final class MonitoringToolSDK {
    // 私有构造方法
    private MonitoringToolSDK() {

    }

    public static void init(Options options) {
        Recorder.getInstance().setAppContext(options.getContext());
        Recorder.getInstance().setMaxLogItemCount(options.getMaxLogCount());
    }

    public static final class Options {
        private Context context;
        private int maxLogCount = 1000;

        public Options(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public Options setMaxLogCount(int maxLogCount) {
            this.maxLogCount = maxLogCount;
            return this;
        }

        public int getMaxLogCount() {
            return maxLogCount;
        }
    }

}
