package cn.roy.zlib.http.metrics

import androidx.annotation.IntDef

/**
 * @Description: 统计事件
 * @Author: Roy Z
 * @Date: 2021/03/09
 * @Version: v1.0
 */
class StatisticsEvent(taskId: String) {
    private var id = taskId

    companion object {
        const val INIT = 0x0
        const val CANCEL = 0x1
        const val SUCCESS = 0x2
        const val FAIL = 0x3
    }

    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
    @MustBeDocumented
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(INIT, CANCEL, SUCCESS, FAIL)
    annotation class Type

    @Type
    private var type: Int = 0

    fun setType(@Type type: Int) {
        this.type = type
    }

    @Type
    fun getType():Int{
        return this.type
    }

}