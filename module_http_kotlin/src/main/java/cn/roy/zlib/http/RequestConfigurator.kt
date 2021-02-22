package cn.roy.zlib.http

import okhttp3.Interceptor
import retrofit2.CallAdapter
import retrofit2.Converter

/**
 * @Description: 请求配置器
 * @Author: Roy Z
 * @Date: 2021/02/07
 * @Version: v1.0
 */
interface RequestConfigurator {

    /**
     * 配置baseUrl
     */
    fun baseUrl():String

    /**
     * 配置连接超时时间
     */
    fun connectTimeout(): TimeoutParam

    /**
     * 配置写超时时间
     */
    fun writeTimeout(): TimeoutParam

    /**
     * 配置读超时时间
     */
    fun readTimeout(): TimeoutParam

    /**
     * 配置请求总超时时间
     */
    fun callTimeout(): TimeoutParam

    /**
     * 配置log
     */
    fun logger(): RequestLogger

    /**
     * 配置请求拦截器
     */
    fun requestInterceptors(): List<Interceptor>?

    /**
     * 配置call适配器
     */
    fun callAdapterFactories(): List<CallAdapter.Factory>?

    /**
     * 配置数据转换器
     */
    fun converterFactories(): List<Converter.Factory>?
}