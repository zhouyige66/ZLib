package cn.roy.zlib.http.retrofit;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.roy.zlib.http.core.AbstractHttpClient;
import cn.roy.zlib.http.core.HttpRequestCallback;
import cn.roy.zlib.http.core.HttpRequestCancelable;
import cn.roy.zlib.http.exception.RequestException;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 09:52
 * @Version: v1.0
 */
public class RetrofitHttpClient extends AbstractHttpClient {
    private ConcurrentHashMap<String, ApiService> apiServiceContainer = new ConcurrentHashMap<>();

    @Override
    public <T> HttpRequestCancelable getByChild(String requestTag, String url,
                                                Map<String, String> headerMap,
                                                Map<String, String> queryMap,
                                                HttpRequestCallback<T> callback) {
        ResponseObserver<T> observer = new ResponseObserver(responsePretreatment, callback);
        try {
            String[] urls = validUrl(url);
            ApiService apiService = getApiService(urls[0]);
            Observable<ResponseBody> observable = apiService.get(urls[1]);
            associate(observable, observer);
        } catch (RequestException e) {
            e.printStackTrace();
            observer.publish(e);
        }
        return observer;
    }

    @Override
    public <T> HttpRequestCancelable postByChild(String requestTag, String url,
                                                 Map<String, String> headerMap,
                                                 Map<String, String> queryMap, Object obj,
                                                 HttpRequestCallback<T> callback) {
        ResponseObserver<T> observer = new ResponseObserver(responsePretreatment, callback);
        try {
            String[] urls = validUrl(url);
            ApiService apiService = getApiService(urls[0]);
            Observable<ResponseBody> observable = apiService.post(urls[1]);
            associate(observable, observer);
        } catch (RequestException e) {
            e.printStackTrace();
            observer.publish(e);
        }
        return observer;
    }

    private String[] validUrl(String url) throws RequestException {
        if (TextUtils.isEmpty(url)) {
            throw new RequestException(-1, "url不能为空");
        }

        // 头部为"http://"或"https://"
        if (url.length() < 8) {
            throw new RequestException(-1, "url长度过短");
        }

        int index = url.indexOf("/", 8);
        String baseUrl;
        String relativePath;
        if (index == -1) {
            baseUrl = url;
            relativePath = "";
        } else {
            baseUrl = url.substring(0, index + 1);
            relativePath = url.substring(index + 1);
        }
        logger.print("url拆解后，baseUrl：" + baseUrl);
        logger.print("url拆解后，relativePath：" + relativePath);

        String[] urls = new String[]{baseUrl, relativePath};
        return urls;
    }

    private ApiService getApiService(String baseUrl) throws RequestException {
        ApiService service = apiServiceContainer.get(baseUrl);
        if (service == null) {
            service = RetrofitFactory.create(baseUrl, baseParam).create(ApiService.class);
            apiServiceContainer.put(baseUrl, service);
        }

        return service;
    }

    private void associate(Observable<ResponseBody> observable, ResponseObserver observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

}
