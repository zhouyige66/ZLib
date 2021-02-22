package cn.roy.zlib.http

import android.util.Log
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @Description: 可取消的请求（防止任务执行导致的内存泄漏）
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
class RequestCancelable<T>(callback: RequestCallback<T>) : Observer<T> {
    private val callback = callback
    private var cancel = false
    private lateinit var disposable: Disposable

    fun cancel() {
        if (cancel) {
            return
        }

        cancel = true
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    fun isCancel(): Boolean {
        return cancel
    }

    override fun onSubscribe(d: Disposable) {
        Log.d("roy", "onSubscribe 执行")
        this.disposable = d
    }

    override fun onNext(t: T) {
        Log.d("roy", "onNext 执行")
        callback.success(t)
    }

    override fun onError(e: Throwable) {
        Log.d("roy", "onError 执行")
        var msg = e.message
        if (msg != null) {
            callback.fail(-1, msg)
        } else {
            callback.fail(-1, "发生错误")
        }
    }

    override fun onComplete() {
    }

}