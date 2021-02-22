package cn.roy.zlib.http

/**
 * @Description: 请求统计
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
class RequestMetrics {
    private var cancelCount = 0
    private var successCount = 0
    private var failCount = 0
    private var averageTime = 0
}