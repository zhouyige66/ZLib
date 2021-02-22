package cn.roy.zlib.http

import java.util.concurrent.TimeUnit

/**
 * @Description: 超时参数
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
class TimeoutParam constructor(timeout:Long,timeUnit: TimeUnit){
    var timeout:Long = timeout
    var timeUnit:TimeUnit = timeUnit
}