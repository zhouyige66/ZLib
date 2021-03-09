package cn.roy.zlib.http

/**
 * @Description: 请求回调接口
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
interface RequestCallback<Result> {

    /**
     * 成功回调
     */
    fun success(result:Result)

    /**
     * 失败回调
     */
    fun fail(code:Int,msg:String)

}