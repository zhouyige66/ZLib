package cn.roy.zlib.http.metrics

import java.util.concurrent.ConcurrentHashMap

/**
 * @Description: 请求统计
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
class RequestMetrics private constructor() {
    private lateinit var mRequestMetricsListener: RequestMetricsListener

    // 请求耗时
    private var requestCostMap = ConcurrentHashMap<String, RequestCostDetail>()

    companion object {
        val instance: RequestMetrics by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RequestMetrics()
        }
    }

    fun registerListener(listener: RequestMetricsListener) {
        mRequestMetricsListener = listener
    }

    fun release() {
        requestCostMap.clear()
    }

    fun statistics(event: StatisticsEvent) {
        when (event.getType()) {
            StatisticsEvent.INIT -> StatisticCount.initCount++
            StatisticsEvent.CANCEL -> StatisticCount.cancelCount++
            StatisticsEvent.SUCCESS -> StatisticCount.successCount++
            StatisticsEvent.FAIL -> StatisticCount.failCount++
        }
        mRequestMetricsListener?.onStatisticsChange()
    }

    fun analysisRequest(event: AnalysisEvent) {
        val url = event.url
        var requestCostDetail = requestCostMap[url]
        if (requestCostDetail == null) {
            requestCostDetail = RequestCostDetail(url)
            requestCostMap[url] = requestCostDetail
        }
        requestCostDetail.totalTime = requestCostDetail.totalTime + event.cost
        requestCostDetail.count = requestCostDetail.count + 1
        mRequestMetricsListener?.onAnalysisChange(url, requestCostDetail.getAverageTime())
    }

    fun getRequestAverageTime(url: String): Long {
        val cost = requestCostMap[url]
        return cost?.getAverageTime() ?: 0
    }

    fun getRequestCount(url: String): Int {
        val cost = requestCostMap[url]
        return cost?.count ?: 0
    }

    object StatisticCount {
        var initCount = 0
        var cancelCount = 0
        var successCount = 0
        var failCount = 0
    }

    data class RequestCostDetail(var url: String) {
        var totalTime: Long = 0
        var count: Int = 0

        fun getAverageTime(): Long {
            if (count == 0) {
                return 0
            }
            return totalTime / count
        }
    }

}