package cn.roy.zlib.http.retrofit;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * @Description: 通用的请求接口
 * @Author: Roy Z
 * @Date: 2019/2/11 18:34
 * @Version: v1.0
 */
public interface ApiService {

    @GET("{url}")
    Observable<ResponseBody> get(@Path(value = "url", encoded = true) String url);

    @GET("{url}")
    Observable<ResponseBody> get(@Path(value = "url", encoded = true) String url,
                               @HeaderMap Map<String, String> headerMap);

    @GET("{url}")
    Observable<ResponseBody> get(@Path(value = "url", encoded = true) String url,
                               @HeaderMap Map<String, String> headerMap,
                               @QueryMap Map<String, String> queryMap);

    @POST("{url}")
    Observable<ResponseBody> post(@Path(value = "url", encoded = true) String url);

    @POST("{url}")
    Observable<ResponseBody> post(@Path(value = "url", encoded = true) String url,
                                @Body Object object);

    @POST("{url}")
    Observable<ResponseBody> postWithHeaderMap(@Path(value = "url", encoded = true) String url,
                                             @HeaderMap Map<String, String> headerMap);

    @POST("{url}")
    Observable<ResponseBody> postWithHeaderMap(@Path(value = "url", encoded = true) String url,
                                             @HeaderMap Map<String, String> headerMap,
                                             @Body Object object);

    @POST("{url}")
    Observable<ResponseBody> postWithQueryMap(@Path(value = "url", encoded = true) String url,
                                            @QueryMap Map<String, String> queryMap);

    @POST("{url}")
    Observable<ResponseBody> postWithQueryMap(@Path(value = "url", encoded = true) String url,
                                            @QueryMap Map<String, String> queryMap,
                                            @Body Object object);

    @POST("{url}")
    Observable<ResponseBody> postWithHeaderMapAndQueryMap(@Path(value = "url", encoded = true) String url,
                                                        @HeaderMap Map<String, String> headerMap,
                                                        @QueryMap Map<String, String> queryMap);

    @POST("{url}")
    Observable<ResponseBody> postWithHeaderMapAndQueryMap(@Path(value = "url", encoded = true) String url,
                                                        @HeaderMap Map<String, String> headerMap,
                                                        @QueryMap Map<String, String> queryMap,
                                                        @Body Object object);
}
