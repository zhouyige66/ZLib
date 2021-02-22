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
 * @Description: FileAppender工厂
 * @Author: Roy Z
 * @Date: 2020/5/8 15:46
 * @Version: v1.0
 */
public class FileAppenderFactory {

    public static FileAppender<ILoggingEvent> create(LoggerContext loggerContext,
                                                     FileAppenderProperty prop,
                                                     boolean isSingleModel) {
        // appender
        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(loggerContext);
        // 格式
        Level level = prop.getLevel();
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(prop.getEncoderPattern());
        encoder.start();
        fileAppender.setEncoder(encoder);
        fileAppender.setName(level.levelStr);
        fileAppender.setFile(prop.getLogFilePath());
        // 过滤
        LevelFilter filter = new LevelFilter();
        filter.setContext(loggerContext);
        filter.setLevel(level);
        if (!isSingleModel) {
            filter.setOnMatch(FilterReply.ACCEPT);
            filter.setOnMismatch(FilterReply.DENY);
        }
        filter.start();
        fileAppender.addFilter(filter);
        fileAppender.setPrudent(false);
        fileAppender.setAppend(true);
        // 策略
        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy =
                new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(prop.getLogFileNamePattern());
        String maxFileSize = FileSizeConvertUtil.getReadableFileSize(prop.getSingleFileSize());
        String totalSize = FileSizeConvertUtil.getReadableFileSize(prop.getTotalFileSize());
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
