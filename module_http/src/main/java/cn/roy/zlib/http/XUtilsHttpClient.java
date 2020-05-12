package cn.roy.zlib.http;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Map;

import cn.roy.zlib.http.core.HttpRequestCallback;
import cn.roy.zlib.http.core.HttpRequestCancelable;
import cn.roy.zlib.http.xutils.XUtilHttpCancelable;
import cn.roy.zlib.http.xutils.XUtilsHttpCallback;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 10:25
 * @Version: v1.0
 */
public class XUtilsHttpClient extends AbstractHttpClient {

    @Override
    public <T> HttpRequestCancelable getByChild(String requestTag, String url,
                                            Map<String, String> headerMap,
                                            Map<String, String> queryMap,
                                            HttpRequestCallback<T> callback) {
        RequestParams params = new RequestParams();
        Callback.Cancelable cancelable = x.http().get(params, new XUtilsHttpCallback<>(callback));
        return new XUtilHttpCancelable(cancelable);
    }

    @Override
    public <T> HttpRequestCancelable postByChild(String requestTag, String url,
                                             Map<String, String> headerMap,
                                             Map<String, String> queryMap,
                                             Object obj,
                                             HttpRequestCallback<T> callback) {
        return null;
    }

}
