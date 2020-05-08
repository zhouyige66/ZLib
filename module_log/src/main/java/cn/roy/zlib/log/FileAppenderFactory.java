package cn.roy.zlib.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/8 15:46
 * @Version: v1.0
 */
public class FileAppenderFactory {

    public static FileAppender<ILoggingEvent> createFileAppender(LoggerContext loggerContext,
                                                                 FileAppenderProperty prop,
                                                                 boolean isSingleModel) {
        Level level = prop.getLevel();

        // 格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(prop.getEncoderPattern());
        encoder.start();
        // 过滤器
        LevelFilter filter = new LevelFilter();
        filter.setContext(loggerContext);
        filter.setLevel(level);
        if (!isSingleModel) {
            filter.setOnMatch(FilterReply.ACCEPT);
            filter.setOnMismatch(FilterReply.DENY);
        }
        filter.start();
        // appender
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setEncoder(encoder);
        fileAppender.setName(level.levelStr);
        fileAppender.setFile(prop.getLogFilePath());
        fileAppender.addFilter(filter);
        fileAppender.setPrudent(false);
        fileAppender.setAppend(true);
        // 策略
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(prop.getLogFileNamePattern());
        String maxFileSize = FileSizeConversionUtil.getFileSizeWithInteger(prop.getSingleFileSize());
        String totalSize = FileSizeConversionUtil.getFileSizeWithInteger(prop.getTotalFileSize());
        int maxHistory = prop.getMaxHistory();
        rollingPolicy.setMaxFileSize(FileSize.valueOf(maxFileSize));
        rollingPolicy.setTotalSizeCap(FileSize.valueOf(totalSize));
        rollingPolicy.setMaxHistory(maxHistory);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.start();
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        return fileAppender;
    }

}
