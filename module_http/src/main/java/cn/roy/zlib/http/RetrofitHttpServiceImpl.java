package cn.roy.zlib.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description: http服务基础实现
 * @Author: Roy Z
 * @Date: 2019/2/12 14:49
 * @Version: v1.0
 */
public class RetrofitHttpServiceImpl implements HttpService {
    private volatile static RetrofitHttpServiceImpl instance;

    private ConcurrentHashMap<String, ApiService> apiServiceContainer;
    private Function<JSONObject, JSONObject> preDealFunction = null;

    /**
     * 被调用的时候被装载，实现延迟加载（由JVM保证线程安全）
     */
    private static class HttpServiceHolder {
        private static RetrofitHttpServiceImpl client = new RetrofitHttpServiceImpl();
    }

    public static RetrofitHttpServiceImpl getInstance() {
        return HttpServiceHolder.client;
    }

    private RetrofitHttpServiceImpl() {
        apiServiceContainer = new ConcurrentHashMap<>();
    }

    @Override
    public void get(String url, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.get(urls[1]), observer);
    }

    @Override
    public void get(String url, @NonNull Map<String, String> headers, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.get(urls[1], headers), observer);
    }

    @Override
    public void get(String url, @NonNull Map<String, String> headers, @NonNull Map<String, String> queryMap, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.get(urls[1], headers, queryMap), observer);
    }

    @Override
    public void post(String url, Object obj, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.post(urls[1], obj), observer);
    }

    @Override
    public void post(String url, @NonNull Map<String, String> headers, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.post(urls[1], headers), observer);
    }

    @Override
    public void post(String url, @NonNull Map<String, String> headers, @NonNull Map<String, String> queryMap, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.post(urls[1], headers, queryMap), observer);
    }

    @Override
    public void post(String url, @NonNull Map<String, String> headers, @NonNull Map<String, String> queryMap, Object obj, Observer<JSONObject> observer) {
        String[] urls = validUrl(url);
        ApiService apiService = getApiService(urls[0]);
        associate(apiService.post(urls[1], headers, queryMap, obj), observer);
    }

    /**
     * 配置预处理器
     *
     * @param preDealFunction
     */
    public void setPreDealFunction(Function<JSONObject, JSONObject> preDealFunction) {
        this.preDealFunction = preDealFunction;
    }

    private String[] validUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new HttpRequestException(-1, "url不能为空");
        }

        // 头部为"http://"或"https://"
        if (url.length() < 8) {
            throw new HttpRequestException(-1, "url长度过短");
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
        System.out.println("baseUrl:" + baseUrl);
        System.out.println("relativePath:" + relativePath);

        String[] urls = new String[]{baseUrl, relativePath};
        return urls;
    }

    private ApiService getApiService(String baseUrl) {
        ApiService service = apiServiceContainer.get(baseUrl);
        if (service == null) {
            service = RetrofitFactory.create(baseUrl).create(ApiService.class);
            apiServiceContainer.put(baseUrl, service);
        }

        return service;
    }

    private void associate(Observable<JSONObject> observable, Observer<JSONObject> observer) {
        if (preDealFunction != null) {
            // 拦截数据，进行预处理
            observable = observable.map(preDealFunction);
        }
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
