package cn.roy.zlib.http

/**
 * @Description: 日志接口
 * @Author: Roy Z
 * @Date: 2021/02/14
 * @Version: v1.0
 */
interface RequestLogger {

    /**
     * 打印日志信息
     */
    fun print(log:String)

}