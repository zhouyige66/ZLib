package cn.roy.zlib.http.retrofit;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import cn.roy.zlib.http.core.HttpRequestBaseParam;
import cn.roy.zlib.http.exception.RequestException;
import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: Retrofit工厂类
 * @Author: Roy Z
 * @Date: 2019-08-02 16:35
 * @Version: v1.0
 */
public class RetrofitFactory {
    /**
     * 创建Retrofit
     *
     * @param baseUrl
     * @param baseParam
     * @return
     */
    public static Retrofit create(@NonNull String baseUrl, HttpRequestBaseParam baseParam)
            throws RequestException {
        if (baseParam == null || baseParam.getConnectTimeout() < 0
                || baseParam.getWriteTimeout() < 0) {
            throw new RequestException(-1, "超时时间设置有误");
        }
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        System.out.println(message);
                    }
                });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(baseParam.getConnectTimeout(), TimeUnit.MICROSECONDS)
                .writeTimeout(baseParam.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(baseParam.getReadTimeout(), TimeUnit.MILLISECONDS)
                .callTimeout(baseParam.getCallTimeout() <= 0 ? 0 : baseParam.getCallTimeout(),
                        TimeUnit.MILLISECONDS)
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        if (!TextUtils.isEmpty(baseUrl)) {
            retrofitBuilder.baseUrl(baseUrl);
        }
        Retrofit retrofit = retrofitBuilder
//                .addConverterFactory(new Retrofit2ConverterFactory())// 装换成JSON
                .addConverterFactory(GsonConverterFactory.create())// 装换成JSON
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit;
    }

}
