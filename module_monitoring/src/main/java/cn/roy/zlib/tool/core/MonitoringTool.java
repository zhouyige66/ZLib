package cn.roy.zlib.tool.core;

import cn.roy.zlib.tool.bean.LogItemBean;

/**
 * @Description: 监视工具
 * @Author: Roy Z
 * @Date: 2021/06/25
 * @Version: v1.0
 */
public class MonitoringTool {

    private MonitoringTool() {

    }

    public static void v(String tag, String msg) {
        addLog(LogItemBean.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        addLog(LogItemBean.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        addLog(LogItemBean.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        addLog(LogItemBean.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        addLog(LogItemBean.ERROR, tag, msg);
    }

    private static void addLog(int level, String tag, String msg) {
        LogItemBean bean = new LogItemBean(level, tag, msg);
        Recorder.getInstance().addLog(bean);
    }

}
