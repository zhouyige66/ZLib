package cn.roy.zlib.http

import cn.roy.zlib.http.metrics.StatisticsAnalysis
import cn.roy.zlib.http.metrics.StatisticsEvent
import java.util.*

/**
 * @Description: 可取消的网络请求任务
 * @Author: Roy Z
 * @Date: 2021/03/09
 * @Version: v1.0
 */
abstract class AbsCancelableTask : CancelableTask {
    private var id: String = UUID.randomUUID().toString()
    private var cancel = false
    var isComplete = false

    override fun taskId(): String {
        return id
    }

    override fun cancel() {
        if (cancel || isComplete) {
            return
        }

        cancel = true
        val statisticsEvent = StatisticsEvent(taskId())
        statisticsEvent.setType(StatisticsEvent.CANCEL)
        postEvent(statisticsEvent)
        doCancel()
    }

    override fun isCancel(): Boolean {
        return cancel
    }

    fun postEvent(event: StatisticsEvent) {
        StatisticsAnalysis.instance.statistics(event)
    }

    abstract fun doCancel()

}