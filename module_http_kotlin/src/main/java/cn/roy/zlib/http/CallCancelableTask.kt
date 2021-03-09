package cn.roy.zlib.http

import cn.roy.zlib.http.metrics.StatisticsEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @Description: 可取消的请求
 * @Author: Roy Z
 * @Date: 2021/03/08
 * @Version: v1.0
 */
class CallCancelableTask<T>(private var call: Call<T>, private val callback: RequestCallback<T>) :
        AbsCancelableTask(),Callback<T> {

    override fun doCancel() {
        call.cancel()
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val statisticsEvent = StatisticsEvent(taskId())
        statisticsEvent.setType(StatisticsEvent.SUCCESS)
        postEvent(statisticsEvent)
        response.body()?.let { callback.success(it) }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        val statisticsEvent = StatisticsEvent(taskId())
        statisticsEvent.setType(StatisticsEvent.FAIL)
        postEvent(statisticsEvent)
        var msg = t.message
        if (msg != null) {
            callback.fail(-1, msg)
        } else {
            callback.fail(-1, "发生错误")
        }
    }

}