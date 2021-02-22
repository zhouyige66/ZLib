package cn.roy.zlib.http.retrofit;

import java.io.IOException;

import cn.roy.zlib.http.core.HttpRequestCallback;
import cn.roy.zlib.http.core.HttpRequestCancelable;
import cn.roy.zlib.http.core.HttpResponsePretreatment;
import cn.roy.zlib.http.exception.ConvertException;
import cn.roy.zlib.http.exception.RequestException;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 11:06
 * @Version: v1.0
 */
public class ResponseObserver<T> implements Observer<ResponseBody>, HttpRequestCancelable {
    private ResponseConverter<T> converter;
    private Disposable disposable;
    private boolean canceled = false;

    public ResponseObserver(HttpResponsePretreatment pretreatment, HttpRequestCallback<T> callback) {
        converter = new ResponseConverter<T>(pretreatment, callback);
    }

    public void publish(Exception ex) {
        converter.publishException(ex);
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        if (canceled) {
            return;
        }

        try {
            String result = responseBody.string();
            converter.publish(result);
        } catch (IOException e) {
            e.printStackTrace();
            converter.onError(new ConvertException(-1,"数据转换出错"));
        }
    }

    @Override
    public void onError(Throwable e) {
        if (canceled) {
            return;
        }

        converter.publishException(new RequestException(-1,e.getMessage()));
    }

    @Override
    public void onComplete() {

    }

    @Override
    public boolean isCancel() {
        return canceled;
    }

    @Override
    public void cancel() {
        if (canceled) {
            return;
        }
        canceled = true;
        if (disposable != null) {
            disposable.dispose();
        }
    }

}
