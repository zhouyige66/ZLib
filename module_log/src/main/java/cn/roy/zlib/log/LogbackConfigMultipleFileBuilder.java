package cn.roy.zlib.log;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/8 15:14
 * @Version: v1.0
 */
public class LogbackConfigMultipleFileBuilder {
    private LogbackConfigBuilder builder;

    // 日志分Level独立存储配置
    private Map<String, FileAppenderProperty> multiFileAppenderProps;

    public void setBuilder(LogbackConfigBuilder builder) {
        this.builder = builder;
    }

    public LogbackConfigMultipleFileBuilder addFileAppenderProp(FileAppenderProperty prop) {
        if (multiFileAppenderProps == null) {
            multiFileAppenderProps = new HashMap<>();
        }
        multiFileAppenderProps.put(prop.getLevel().levelStr, prop);
        return this;
    }

    public void build() {
        builder.build();

        if (multiFileAppenderProps.isEmpty()) {
            return;
        }
        Logger rootLogger = builder.getRootLogger();
        LoggerContext loggerContext = builder.getLoggerContext();
        for (FileAppenderProperty prop : multiFileAppenderProps.values()) {
            rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext, prop,
                    false));
        }
    }
}
