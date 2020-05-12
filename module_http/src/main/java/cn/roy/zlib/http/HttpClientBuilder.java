package cn.roy.zlib.http;

import cn.roy.zlib.http.core.HttpRequestBaseParam;
import cn.roy.zlib.http.core.HttpRequestClient;
import cn.roy.zlib.http.core.HttpRequestLogger;
import cn.roy.zlib.http.core.HttpResponsePretreatment;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 09:41
 * @Version: v1.0
 */
public class HttpClientBuilder {
    private HttpRequestBaseParam baseParam;
    private HttpRequestLogger logger;
    private HttpResponsePretreatment httpResponsePretreatment;

    public HttpClientBuilder setBaseParam(HttpRequestBaseParam baseParam) {
        this.baseParam = baseParam;
        return this;
    }

    public HttpClientBuilder setLogger(HttpRequestLogger logger) {
        this.logger = logger;
        return this;
    }

    public HttpClientBuilder setHttpResponsePretreatment(HttpResponsePretreatment httpResponsePretreatment) {
        this.httpResponsePretreatment = httpResponsePretreatment;
        return this;
    }

    public HttpRequestClient buildClient(Class<? extends HttpRequestClient> clazz) {


        return null;
    }

}
