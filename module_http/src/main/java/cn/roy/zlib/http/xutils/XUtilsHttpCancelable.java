package cn.roy.zlib.http.xutils;

import org.xutils.common.Callback;

import cn.roy.zlib.http.core.HttpRequestCancelable;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 17:05
 * @Version: v1.0
 */
public class XUtilsHttpCancelable implements HttpRequestCancelable {
    Callback.Cancelable cancelable;

    public XUtilsHttpCancelable(Callback.Cancelable cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public boolean isCancel() {
        return cancelable.isCancelled();
    }

    @Override
    public void cancel() {
        cancelable.cancel();
    }

}
