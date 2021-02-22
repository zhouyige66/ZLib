package cn.roy.zlib.http.retrofit;

import com.google.gson.Gson;

import cn.roy.zlib.http.core.HttpRequestCallback;
import cn.roy.zlib.http.core.HttpResponsePretreatment;
import cn.roy.zlib.http.exception.ConvertException;
import cn.roy.zlib.http.exception.RequestException;
import cn.roy.zlib.http.exception.ResponseException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/10 17:36
 * @Version: v1.0
 */
public class ResponseConverter<T> implements ObservableOnSubscribe<T>, Observer<T> {
    private HttpResponsePretreatment pretreatment;
    private HttpRequestCallback<T> callback;
    private ObservableEmitter<T> emitter;

    public ResponseConverter(HttpResponsePretreatment pretreatment, HttpRequestCallback<T> callback) {
        this.pretreatment = pretreatment;
        this.callback = callback;

        Observable<T> observable = Observable.create(this);
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
        this.emitter = emitter;
    }

    public void publish(String result) {
        String convert;
        Gson gson = new Gson();
        if (this.pretreatment != null) {
            Object obj = null;
            try {
                obj = this.pretreatment.pretreatment(result);
            } catch (ResponseException e) {
                e.printStackTrace();
                this.emitter.onError(e);
                return;
            }
            convert = gson.toJson(obj);
        } else {
            convert = result;
        }
        Class<T> tClass = callback.getTClass();
        if (tClass == String.class) {
            this.emitter.onNext((T) convert);
        } else if (tClass == Integer.class) {
            this.emitter.onNext((T) Integer.valueOf(convert));
        } else if (tClass == Boolean.class) {
            this.emitter.onNext((T) Boolean.valueOf(convert));
        } else {
            T t = gson.fromJson(convert, tClass);
            this.emitter.onNext(t);
        }
    }

    public void publishException(Exception ex) {
        this.emitter.onError(ex);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        callback.success(t);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof RequestException) {
            RequestException exception = (RequestException) e;
            callback.fail(exception.getCode(), exception.getMsg());
        } else if (e instanceof ConvertException) {
            ConvertException exception = (ConvertException) e;
            callback.fail(exception.getCode(), exception.getMsg());
        } else if (e instanceof ResponseException) {
            ResponseException exception = (ResponseException) e;
            callback.fail(exception.getCode(), exception.getMsg());
        } else {
            callback.fail(-1, e.getMessage());
        }
    }

    @Override
    public void onComplete() {

    }
}
