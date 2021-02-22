package cn.roy.zlib.http

/**
 * @Description: 请求回调接口
 * @Author: Roy Z
 * @Date: 2021/02/04
 * @Version: v1.0
 */
interface RequestCallback<Result> {

    fun success(result:Result)

    fun fail(code:Int,msg:String)

}