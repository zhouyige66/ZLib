package cn.roy.zlib.log;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/8 15:14
 * @Version: v1.0
 */
public class LogbackConfigSingleFileBuilder {
    private LogbackConfigBuilder builder;
    private FileAppenderProperty singleFileAppenderProp;

    public void setBuilder(LogbackConfigBuilder builder) {
        this.builder = builder;
    }

    public LogbackConfigSingleFileBuilder setFileAppenderProp(FileAppenderProperty prop) {
        this.singleFileAppenderProp = prop;
        return this;
    }

    public void build() {
        builder.build();

        Logger rootLogger = builder.getRootLogger();
        LoggerContext loggerContext = builder.getLoggerContext();
        rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext,
                singleFileAppenderProp, true));
    }

}
