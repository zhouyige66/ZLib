package cn.roy.zlib.http

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @Description: 请求执行器
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
object RequestExecutor {

    @JvmStatic
    fun <T> execute(observable: Observable<T>, callback: RequestCallback<T>): RequestCancelable<T> {
        val cancelable = RequestCancelable(callback)
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cancelable)
        return cancelable
    }

}