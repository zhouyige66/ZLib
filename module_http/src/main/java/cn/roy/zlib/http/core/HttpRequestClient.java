package cn.roy.zlib.http.core;

import java.util.Map;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 09:18
 * @Version: v1.0
 */
public interface HttpRequestClient {
    void config(HttpRequestBaseParam baseParam);

    void setHttpRequestLogger(HttpRequestLogger logger);

    void setHttpResponsePretreatment(HttpResponsePretreatment httpResponsePretreatment);

    <T> HttpRequestCancelable get(String requestTag,
                                  String url,
                                  Map<String, String> headerMap,
                                  Map<String, String> queryMap,
                                  HttpRequestCallback<T> callback);

    <T> HttpRequestCancelable post(String requestTag,
                                   String url,
                                   Map<String, String> headerMap,
                                   Map<String, String> queryMap,
                                   Object obj,
                                   HttpRequestCallback<T> callback);

}
