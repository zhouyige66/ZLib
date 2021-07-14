package cn.roy.zlib.tool;

import cn.roy.zlib.tool.bean.LogBean;
import cn.roy.zlib.tool.core.Recorder;

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
        addLog(LogBean.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        addLog(LogBean.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        addLog(LogBean.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        addLog(LogBean.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        addLog(LogBean.ERROR, tag, msg);
    }

    private static void addLog(int level, String tag, String msg) {
        LogBean bean = new LogBean(level, tag, msg);
        Recorder.getInstance().addLog(bean);
    }

}
