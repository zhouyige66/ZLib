package cn.roy.zlib.http;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @Description: Retrofit工厂类
 * @Author: Roy Z
 * @Date: 2019-08-02 16:35
 * @Version: v1.0
 */
public class RetrofitFactory {
    private static final int BASE_TIME_OUT = 5 * 1000;

    private static void log(String text) {
        Log.d("http", text);
    }

    public static Retrofit create(String baseUrl) {
        return create(baseUrl, BASE_TIME_OUT, BASE_TIME_OUT, BASE_TIME_OUT);
    }

    /**
     * 创建Retrofit
     *
     * @param baseUrl
     * @param connectTimeout
     * @param writeTimeout
     * @param readTimeout
     * @return
     */
    public static Retrofit create(@NonNull String baseUrl, int connectTimeout, int writeTimeout,
                                  int readTimeout) {
        if (connectTimeout <= 0 || writeTimeout <= 0 || readTimeout <= 0) {
            throw new HttpRequestException(-1, "超时时间设置有误");
        }

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        log("请求方法：" + request.method());
                        log("请求url：" + request.url());
                        Headers headers = request.headers();
                        log("请求header：" + JSON.toJSONString(headers));
                        RequestBody requestBody = request.body();
                        if (requestBody != null) {
                            Buffer buffer = new Buffer();
                            requestBody.writeTo(buffer);
                            log("请求body：" + buffer.readUtf8());
                        }
                        Response response = chain.proceed(chain.request());
                        ResponseBody responseBody = response.body();
                        String content = responseBody.string();
                        log("请求返回body：" + content);
                        // 取出后不能再取，所以需要构建新的ResponseBody
                        ResponseBody newResponseBody = ResponseBody.create(responseBody.contentType(),
                                content);
                        return response.newBuilder().body(newResponseBody).build();
                    }
                });
        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        if (!TextUtils.isEmpty(baseUrl)) {
            retrofitBuilder.baseUrl(baseUrl);
        }
        Retrofit retrofit = retrofitBuilder
                .addConverterFactory(new Retrofit2ConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }

}
