package cn.roy.zlib.http.core;

/**
 * @Description: 可取消的http请求
 * @Author: Roy Z
 * @Date: 2020/5/8 17:23
 * @Version: v1.0
 */
public interface HttpRequestCancelable {
    /**
     * @return 返回该请求是否已取消
     */
    boolean isCancel();

    /**
     * 取消本次请求或取消监听（防止内存泄漏）
     */
    void cancel();
}
