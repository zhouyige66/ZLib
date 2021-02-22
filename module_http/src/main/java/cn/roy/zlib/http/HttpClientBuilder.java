package cn.roy.zlib.http;

import cn.roy.zlib.http.core.HttpRequestBaseParam;
import cn.roy.zlib.http.core.HttpRequestClient;
import cn.roy.zlib.http.core.HttpRequestLogger;
import cn.roy.zlib.http.core.HttpResponsePretreatment;
import cn.roy.zlib.http.retrofit.RetrofitHttpClient;
import cn.roy.zlib.http.xutils.XUtilsHttpClient;

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
        if (clazz == RetrofitHttpClient.class) {
            RetrofitHttpClient retrofitHttpClient = new RetrofitHttpClient();
            retrofitHttpClient.config(this.baseParam);
            retrofitHttpClient.setHttpRequestLogger(this.logger);
            retrofitHttpClient.setHttpResponsePretreatment(httpResponsePretreatment);
            return retrofitHttpClient;
        } else if (clazz == XUtilsHttpClient.class) {
            XUtilsHttpClient xUtilsHttpClient = new XUtilsHttpClient();
            xUtilsHttpClient.config(this.baseParam);
            xUtilsHttpClient.setHttpRequestLogger(this.logger);
            xUtilsHttpClient.setHttpResponsePretreatment(httpResponsePretreatment);
            return xUtilsHttpClient;
        } else {
            try {
                HttpRequestClient httpRequestClient = clazz.newInstance();
                httpRequestClient.config(this.baseParam);
                httpRequestClient.setHttpRequestLogger(this.logger);
                httpRequestClient.setHttpResponsePretreatment(this.httpResponsePretreatment);
                return httpRequestClient;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
