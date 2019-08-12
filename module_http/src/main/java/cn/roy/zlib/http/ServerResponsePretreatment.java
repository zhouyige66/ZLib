package cn.roy.zlib.http;

import com.alibaba.fastjson.JSONObject;

import io.reactivex.functions.Function;

/**
 * @Description: 结果预处理
 * @Author: Roy Z
 * @Date: 2019-08-02 17:01
 * @Version: v1.0
 */
public class ServerResponsePretreatment implements Function<JSONObject, JSONObject> {

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
            throw new HttpResponseException(jsonObject.getIntValue("code"),
                    jsonObject.getString("msg"));
        }
    }

}
