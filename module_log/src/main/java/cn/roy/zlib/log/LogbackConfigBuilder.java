package cn.roy.zlib.log;

import android.content.Context;
import android.text.TextUtils;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;

/**
 * @Description: Logback配置器
 * @Author: Roy Z
 * @Date: 2019-08-09 10:23
 * @Version: v1.0
 */
public class LogbackConfigBuilder {
    private final LoggerContext loggerContext;
    private Context context;
    private Level rootLevel;
    private boolean isCustom = false;
    // logcat配置
    private Level logcatLevel;
    private String logcatEncodePattern;
    // 日志存储单个文件配置
    private LogbackConfigProperty singleFileAppenderProp;
    // 日志分Level独立存储配置
    private Map<String, LogbackConfigProperty> multiFileAppenderProps;

    public LogbackConfigBuilder(Context context, Level rootLevel) {
        this.context = context;
        this.rootLevel = rootLevel;

        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    public LogbackConfigBuilder setLogcatAppenderProp(Level logcatLevel, String encodePattern) {
        this.isCustom = true;
        this.logcatLevel = logcatLevel;
        this.logcatEncodePattern = encodePattern;
        return this;
    }

    public LogbackConfigBuilder setSingleFileAppenderProp(LogbackConfigProperty prop) {
        this.isCustom = true;
        this.singleFileAppenderProp = prop;
        return this;
    }

    public LogbackConfigBuilder addFileAppenderProp(LogbackConfigProperty prop) {
        this.isCustom = true;
        if (multiFileAppenderProps == null) {
            multiFileAppenderProps = new HashMap<>();
        }
        multiFileAppenderProps.put(prop.getLevel().levelStr, prop);
        return this;
    }

    public void build() {
        loggerContext.stop();
        Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(rootLevel);

        // 创建默认
        if (!isCustom) {
            createDefaultProp();
        }
        // 创建用户自定义的
        if (logcatLevel != null) {
            // 输出格式
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            if (TextUtils.isEmpty(logcatEncodePattern)) {
                encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
            } else {
                encoder.setPattern(logcatEncodePattern);
            }
            encoder.start();
            // 过滤等级
            LevelFilter filter = new LevelFilter();
            filter.setContext(loggerContext);
            filter.setLevel(logcatLevel);
            filter.start();
            // setup LogcatAppender
            LogcatAppender logcatAppender = new LogcatAppender();
            logcatAppender.setContext(loggerContext);
            logcatAppender.setEncoder(encoder);
            logcatAppender.start();
            logcatAppender.addFilter(filter);

            rootLogger.addAppender(logcatAppender);
        }
        // 单文件优先
        if (singleFileAppenderProp != null) {
            rootLogger.addAppender(createFileAppender(singleFileAppenderProp, true));
            return;
        }
        // 日志文件分类
        if (!multiFileAppenderProps.isEmpty()) {
            for (LogbackConfigProperty prop : multiFileAppenderProps.values()) {
                rootLogger.addAppender(createFileAppender(prop, false));
            }
        }
    }

    private void createDefaultProp() {
        String storagePath = AndroidStorageUtil.getStoragePath();
        if(!storagePath.endsWith("/")){
            storagePath = storagePath.concat(File.separator);
        }
        // 日志文件夹
        String packageName  = context.getPackageName();
        String logFilePath = storagePath.concat(packageName).concat(File.separator).concat("log");
        String logFileName = logFilePath + File.separator + "%s.txt";
        String fileNamePattern = logFilePath + File.separator + "%s.%d{yyyy-MM-dd}.%i.txt";

        LogbackConfigProperty debugProp = create(Level.DEBUG, logFileName, fileNamePattern);
        LogbackConfigProperty infoProp = create(Level.INFO, logFileName, fileNamePattern);
        LogbackConfigProperty warnProp = create(Level.WARN, logFileName, fileNamePattern);
        LogbackConfigProperty errorProp = create(Level.ERROR, logFileName, fileNamePattern);
        setLogcatAppenderProp(Level.DEBUG, LogbackConfigProperty.PATTERN_DEFAULT);
        addFileAppenderProp(debugProp);
        addFileAppenderProp(infoProp);
        addFileAppenderProp(warnProp);
        addFileAppenderProp(errorProp);
    }

    private LogbackConfigProperty create(Level level, String logFileName,
                                         String fileNamePattern) {
        String logNamePrefix = level.levelStr.toLowerCase();
        String logFilePath = String.format(logFileName, logNamePrefix);
        String rollingFileNamePattern = fileNamePattern.replace("%s", logNamePrefix);

        // 读取手机存储
        long internalTotalSize = AndroidStorageUtil.getInternalTotalSize();
        long sdCardTotalSize = AndroidStorageUtil.getSDCardTotalSize();
        long max = Math.max(internalTotalSize, sdCardTotalSize);
        // 存储文件总大小为存储的1/10;
        long totalFileSize = max / 20;
        // 单个文件最大10M
        long singleFileSize = 1024 * 1024 * 10;
        // 默认保存最大天数为5
        int maxHistory = 5;
        LogbackConfigProperty prop = new LogbackConfigProperty.Builder(level)
                .setLogFilePath(logFilePath)
                .setLogFileNamePattern(rollingFileNamePattern)
                .setSingleFileSize(singleFileSize)
                .setTotalFileSize(totalFileSize)
                .setMaxHistory(maxHistory)
                .build();

        return prop;
    }

    private FileAppender<ILoggingEvent> createFileAppender(LogbackConfigProperty prop,
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
