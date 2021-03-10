package cn.roy.zlib.http

import android.util.Log
import cn.roy.zlib.http.metrics.StatisticsEvent
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @Description: 可取消的请求（防止任务执行导致的内存泄漏）
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
class ObserverCancelableTask<T>(private var callback: RequestCallback<T>) : AbsCancelableTask(),
        Observer<T> {
    private lateinit var disposable: Disposable

    override fun doCancel() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    override fun onSubscribe(d: Disposable) {
        Log.d("roy", "执行了onSubscribe")
        this.disposable = d
    }

    override fun onNext(t: T) {
        Log.d("roy", "执行了onNext")
        isComplete = true
        val statisticsEvent = StatisticsEvent(taskId())
        statisticsEvent.setType(StatisticsEvent.SUCCESS)
        postEvent(statisticsEvent)
        callback.success(t)
    }

    override fun onError(e: Throwable) {
        Log.d("roy", "执行了onError")
        isComplete = true
        val statisticsEvent = StatisticsEvent(taskId())
        statisticsEvent.setType(StatisticsEvent.FAIL)
        postEvent(statisticsEvent)
        var msg = e.message
        if (msg != null) {
            callback.fail(-1, msg)
        } else {
            callback.fail(-1, "发生错误")
        }
    }

    override fun onComplete() {
        Log.d("roy", "执行了onComplete")
    }

}