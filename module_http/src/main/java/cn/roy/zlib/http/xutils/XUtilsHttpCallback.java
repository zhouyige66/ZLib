package cn.roy.zlib.http.xutils;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;

import cn.roy.zlib.http.core.HttpRequestCallback;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 15:48
 * @Version: v1.0
 */
public class XUtilsHttpCallback<T> implements Callback.CommonCallback<String> {
    private HttpRequestCallback<T> callback;

    public XUtilsHttpCallback(HttpRequestCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void onSuccess(String result) {
        T t = JSON.parseObject(result, callback.getTClass());
        callback.success(t);
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
