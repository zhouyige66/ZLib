package cn.roy.zlib.log;

import android.content.Context;
import android.text.TextUtils;

import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;

/**
 * @Description: Logback配置器
 * @Author: Roy Z
 * @Date: 2019-08-09 10:23
 * @Version: v1.0
 */
public class LogbackConfigBuilder {
    private Context context;
    private LoggerContext loggerContext;
    private Logger rootLogger;
    private Level rootLevel = Level.DEBUG;
    // logcat配置
    private Level logcatLevel;
    private String logcatEncodePattern;
    // 日志存储单个文件配置
    private LogbackConfigSingleFileBuilder singleFileBuilder;
    private LogbackConfigMultipleFileBuilder multipleFileBuilder;

    public LogbackConfigBuilder(Context context) {
        this.context = context;
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.rootLogger = (ch.qos.logback.classic.Logger)
                LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    public Logger getRootLogger() {
        return rootLogger;
    }

    public LoggerContext getLoggerContext() {
        return loggerContext;
    }

    public LogbackConfigBuilder setRootLevel(Level rootLevel) {
        this.rootLevel = rootLevel;
        return this;
    }

    public LogbackConfigBuilder setLogcatAppenderProp(Level logcatLevel, String encodePattern) {
        this.logcatLevel = logcatLevel;
        this.logcatEncodePattern = encodePattern;
        return this;
    }

    public LogbackConfigSingleFileBuilder setSingleFileAppenderProp(FileAppenderProperty prop) {
        if (singleFileBuilder == null) {
            singleFileBuilder = new LogbackConfigSingleFileBuilder();
            singleFileBuilder.setBuilder(this);
        }
        singleFileBuilder.setFileAppenderProp(prop);
        return singleFileBuilder;
    }

    public LogbackConfigMultipleFileBuilder addFileAppenderProp(FileAppenderProperty prop) {
        if (multipleFileBuilder == null) {
            multipleFileBuilder = new LogbackConfigMultipleFileBuilder();
            multipleFileBuilder.setBuilder(this);
        }
        multipleFileBuilder.addFileAppenderProp(prop);
        return multipleFileBuilder;
    }

    public void build() {
        loggerContext.stop();
        rootLogger.setLevel(rootLevel);

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
    }

    public void buildDefault() {
        String storagePath = AndroidStorageUtil.getStoragePath();
        if (!storagePath.endsWith("/")) {
            storagePath = storagePath.concat(File.separator);
        }
        // 日志文件夹
        String packageName = context.getPackageName();
        String logFilePath = storagePath.concat(packageName).concat(File.separator).concat("log");
        String logFileName = logFilePath + File.separator + "%s.txt";
        String fileNamePattern = logFilePath + File.separator + "%s.%d{yyyy-MM-dd}.%i.txt";

        FileAppenderProperty debugProp = create(Level.DEBUG, logFileName, fileNamePattern);
        FileAppenderProperty infoProp = create(Level.INFO, logFileName, fileNamePattern);
        FileAppenderProperty warnProp = create(Level.WARN, logFileName, fileNamePattern);
        FileAppenderProperty errorProp = create(Level.ERROR, logFileName, fileNamePattern);

        setLogcatAppenderProp(Level.DEBUG, FileAppenderProperty.PATTERN_DEFAULT);
        build();

        rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext, debugProp,
                false));
        rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext, infoProp,
                false));
        rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext, warnProp,
                false));
        rootLogger.addAppender(FileAppenderFactory.createFileAppender(loggerContext, errorProp,
                false));
    }

    private FileAppenderProperty create(Level level, String logFileName,
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
        // 默认保存最大天数为7
        int maxHistory = 7;
        FileAppenderProperty prop = new FileAppenderProperty.Builder(level)
                .setLogFilePath(logFilePath)
                .setLogFileNamePattern(rollingFileNamePattern)
                .setSingleFileSize(singleFileSize)
                .setTotalFileSize(totalFileSize)
                .setMaxHistory(maxHistory)
                .build();

        return prop;
    }

}
