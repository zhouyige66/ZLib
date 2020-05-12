package cn.roy.zlib.http.core;

import cn.roy.zlib.http.exception.ResponseException;

/**
 * @Description: http返回结果预处理
 * @Author: Roy Z
 * @Date: 2020/5/11 09:05
 * @Version: v1.0
 */
public interface HttpResponsePretreatment<Data> {

    /**
     * 预处理
     *
     * @param result http请求返回初始数据
     * @return 预处理后返回的数据，eg：json，ResultData
     */
    Data pretreatment(String result) throws ResponseException;

}
