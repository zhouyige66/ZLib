package cn.roy.zlib.http.metrics

/**
 * @Description: 请求耗时统计事件
 * @Author: Roy Z
 * @Date: 2021/03/09
 * @Version: v1.0
 */
data class RequestCostEvent(var url: String, var cost: Long) {
}
