package cn.roy.zlib.http.metrics

/**
 * @Description: 请求统计监听器
 * @Author: Roy Z
 * @Date: 2021/03/10
 * @Version: v1.0
 */
interface RequestMetricsListener {

    fun onStatisticsChange()

    fun onAnalysisChange(url: String, averageTime: Long)

}