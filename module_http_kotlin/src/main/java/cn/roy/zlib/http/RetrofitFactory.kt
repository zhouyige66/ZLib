package cn.roy.zlib.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Description: Retrofit工厂
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
object RetrofitFactory {

    @JvmStatic
    fun create(baseUrl: String): Retrofit {
        val configurator = object : RequestConfigurator {
            override fun baseUrl(): String {
                return baseUrl
            }

            override fun connectTimeout(): TimeoutParam {
                return TimeoutParam(5, TimeUnit.SECONDS)
            }

            override fun writeTimeout(): TimeoutParam {
                return TimeoutParam(5, TimeUnit.SECONDS)
            }

            override fun readTimeout(): TimeoutParam {
                return TimeoutParam(5, TimeUnit.SECONDS)
            }

            override fun callTimeout(): TimeoutParam {
                return TimeoutParam(15, TimeUnit.SECONDS)
            }

            override fun logger(): RequestLogger {
                return object : RequestLogger {
                    override fun print(log: String) {
                        Log.d("http", log)
                    }
                }
            }

            override fun requestInterceptors(): List<Interceptor>? {
                return null
            }

            override fun callAdapterFactories(): List<CallAdapter.Factory>? {
                return null
            }

            override fun converterFactories(): List<Converter.Factory>? {
                return null
            }
        }
        return create(configurator)
    }

    @JvmStatic
    fun create(configurator: RequestConfigurator): Retrofit {
        val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(configurator.writeTimeout().timeout,
                        configurator.writeTimeout().timeUnit)
                .writeTimeout(configurator.writeTimeout().timeout,
                        configurator.writeTimeout().timeUnit)
                .readTimeout(configurator.readTimeout().timeout,
                        configurator.readTimeout().timeUnit)
                .callTimeout(if (configurator.callTimeout().timeout <= 0) 0
                else configurator.callTimeout().timeout,
                        configurator.callTimeout().timeUnit)
        val baseInterceptor = Interceptor { chain ->
            Log.d("roy", "intercept 执行")
            var timestamp = System.currentTimeMillis()
            val httpUrl = chain.request().url
            var uri = httpUrl.toUri()
            val response = chain.proceed(chain.request())
            var cost = System.currentTimeMillis() - timestamp
            configurator.logger().print("<-- 访问：${uri}，请求总耗时：${cost} (ms)-->")
            response
        }
        okHttpClientBuilder.addInterceptor(baseInterceptor)
        if (configurator.requestInterceptors() != null) {
            for (interceptor in configurator.requestInterceptors()!!) {
                okHttpClientBuilder.addInterceptor(interceptor)
            }
        }
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            configurator.logger().print(message)
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = okHttpClientBuilder
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build()
        val retrofitBuilder = Retrofit.Builder().baseUrl(configurator.baseUrl())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
        if (configurator.callAdapterFactories() != null) {
            for (callAdapterFactory in configurator.callAdapterFactories()!!) {
                retrofitBuilder.addCallAdapterFactory(callAdapterFactory)
            }
        }
        if (configurator.converterFactories() != null) {
            for (converterFactory in configurator.converterFactories()!!) {
                retrofitBuilder.addConverterFactory(converterFactory)
            }
        } else {
            retrofitBuilder.addConverterFactory(GsonConverterFactory.create())
        }
        return retrofitBuilder.build()
    }

}