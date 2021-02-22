package cn.roy.zlib.http.core;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import cn.roy.zlib.http.core.HttpRequestBaseParam;
import cn.roy.zlib.http.core.HttpRequestCallback;
import cn.roy.zlib.http.core.HttpRequestCancelable;
import cn.roy.zlib.http.core.HttpRequestClient;
import cn.roy.zlib.http.core.HttpRequestLogger;
import cn.roy.zlib.http.core.HttpResponsePretreatment;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 13:58
 * @Version: v1.0
 */
public abstract class AbstractHttpClient implements HttpRequestClient {
    protected HttpRequestBaseParam baseParam;
    protected HttpRequestLogger logger;
    protected HttpResponsePretreatment responsePretreatment;

    @Override
    public void config(HttpRequestBaseParam baseParam) {
        this.baseParam = baseParam;
    }

    @Override
    public void setHttpRequestLogger(HttpRequestLogger logger) {
        this.logger = logger;
    }

    @Override
    public void setHttpResponsePretreatment(HttpResponsePretreatment httpResponsePretreatment) {
        this.responsePretreatment = httpResponsePretreatment;
    }

    @Override
    public <T> HttpRequestCancelable get(String requestTag, String url,
                                     Map<String, String> headerMap,
                                     Map<String, String> queryMap,
                                     HttpRequestCallback<T> callback) {
        log(false, requestTag, url, headerMap, queryMap, null);
        return getByChild(requestTag, url, headerMap, queryMap, callback);
    }

    @Override
    public <T> HttpRequestCancelable post(String requestTag, String url,
                                      Map<String, String> headerMap,
                                      Map<String, String> queryMap,
                                      Object obj,
                                      HttpRequestCallback<T> callback) {
        log(true, requestTag, url, headerMap, queryMap, obj);
        return postByChild(requestTag, url, headerMap, queryMap, obj, callback);
    }

    private void log(boolean post, String requestTag, String url,
                     Map<String, String> headerMap,
                     Map<String, String> queryMap,
                     Object obj) {
        if (logger != null) {
            logger.print(requestTag + "，" + (post ? "POST" : "GET") + "请求URL：" + url);
            if (headerMap != null) {
                logger.print(requestTag + "，Header：" + JSON.toJSONString(headerMap));
            }
            if (queryMap != null) {
                logger.print(requestTag + "，Query Param：" + JSON.toJSONString(queryMap));
            }
            if (obj != null) {
                logger.print(requestTag + "，Body Param：" + JSON.toJSONString(obj));
            }
        }
    }

    public abstract <T> HttpRequestCancelable getByChild(String requestTag, String url,
                                                     Map<String, String> headerMap,
                                                     Map<String, String> queryMap,
                                                     HttpRequestCallback<T> callback);

    public abstract <T> HttpRequestCancelable postByChild(String requestTag, String url,
                                                      Map<String, String> headerMap,
                                                      Map<String, String> queryMap,
                                                      Object obj,
                                                      HttpRequestCallback<T> callback);

}
