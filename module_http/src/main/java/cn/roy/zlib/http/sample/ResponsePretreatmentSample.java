package cn.roy.zlib.http.sample;

import com.alibaba.fastjson.JSONObject;

import cn.roy.zlib.http.exception.ResponseException;
import io.reactivex.functions.Function;

/**
 * @Description: 结果预处理
 * @Author: Roy Z
 * @Date: 2019-08-02 17:01
 * @Version: v1.0
 */
public class ResponsePretreatmentSample implements Function<JSONObject, JSONObject> {

    @Override
    public JSONObject apply(JSONObject jsonObject) throws Exception {
        if (jsonObject.getIntValue("code") == 200) {
            Object obj = jsonObject.get("data");
            if (obj instanceof JSONObject) {
                return (JSONObject) obj;
            } else {
                JSONObject data = new JSONObject();
                data.put("data", obj);
                return data;
            }
        } else {
            throw new ResponseException(jsonObject.getIntValue("code"),
                    jsonObject.getString("msg"));
        }
    }

}
