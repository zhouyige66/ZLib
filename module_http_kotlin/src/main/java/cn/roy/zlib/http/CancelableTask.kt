package cn.roy.zlib.http

/**
 * @Description: 可取消的请求任务
 * @Author: Roy Z
 * @Date: 2021/03/08
 * @Version: v1.0
 */
interface CancelableTask {

    /**
     * 获取任务id
     */
    fun taskId(): String

    /**
     * 取消任务
     */
    fun cancel()

    /**
     * 任务是否取消
     */
    fun isCancel(): Boolean

}