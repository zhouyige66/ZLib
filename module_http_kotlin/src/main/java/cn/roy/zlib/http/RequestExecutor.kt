package cn.roy.zlib.http

import cn.roy.zlib.http.metrics.StatisticsAnalysis
import cn.roy.zlib.http.metrics.StatisticsEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call

/**
 * @Description: 请求执行器
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
object RequestExecutor {

    @JvmStatic
    fun <T> execute(observable: Observable<T>, callback: RequestCallback<T>): CancelableTask {
        val cancelable = ObserverCancelableTask(callback)
        val statisticsEvent = StatisticsEvent(cancelable.taskId())
        statisticsEvent.setType(StatisticsEvent.INIT)
        StatisticsAnalysis.instance.post(statisticsEvent)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cancelable)
        return cancelable
    }

    @JvmStatic
    fun <T> execute(call: Call<T>, callback: RequestCallback<T>): CancelableTask {
        val cancelable = CallCancelableTask(call, callback)
        val statisticsEvent = StatisticsEvent(cancelable.taskId())
        statisticsEvent.setType(StatisticsEvent.INIT)
        StatisticsAnalysis.instance.post(statisticsEvent)
        call.enqueue(cancelable)
        return cancelable
    }

}