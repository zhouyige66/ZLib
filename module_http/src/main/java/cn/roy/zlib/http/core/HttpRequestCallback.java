package cn.roy.zlib.http.core;

/**
 * @Description: http请求回调
 * @Author: Roy Z
 * @Date: 2020/5/8 16:49
 * @Version: v1.0
 */
public interface HttpRequestCallback<T> {

    /**
     * 用于反序列化时使用
     *
     * @return
     */
    Class<T> getTClass();

    /**
     * 访问成功返回
     *
     * @param data
     */
    void success(T data);

    /**
     * 访问失败
     *
     * @param code
     * @param msg
     */
    void fail(int code, String msg);
}
