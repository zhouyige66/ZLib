package cn.roy.zlib.httptest;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/02/05
 * @Version: v1.0
 */
public interface APITest {

    @GET("session/getId")
    Call<Result<JsonObject>> getSessionId();

    @GET("session/getUser")
    Observable<Result<User>> getUser();

    @GET("session/getUser")
    Observable<JsonObject> getUser2();

}
