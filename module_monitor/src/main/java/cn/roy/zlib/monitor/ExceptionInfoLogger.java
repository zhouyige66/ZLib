package cn.roy.zlib.monitor;

/**
 * @Description: 自定义异常信息logger
 * @Author: Roy Z
 * @Date: 2021/02/23
 * @Version: v1.0
 */
public interface ExceptionInfoLogger {

    /**
     * 自定义异常信息打印（主要用于日志存储）
     *
     * @param exceptionInfo
     */
    void print(String exceptionInfo);

}
