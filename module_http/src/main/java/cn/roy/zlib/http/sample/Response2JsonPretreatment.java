package cn.roy.zlib.http.sample;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.roy.zlib.http.core.HttpResponsePretreatment;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/11 09:50
 * @Version: v1.0
 */
public class Response2JsonPretreatment implements HttpResponsePretreatment<JSONObject> {

    @Override
    public JSONObject pretreatment(String result) {
        return JSON.parseObject(result);
    }

}
