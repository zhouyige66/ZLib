package cn.roy.zlib.log;

import android.text.TextUtils;

import ch.qos.logback.classic.Level;

/**
 * @Description: logback配置属性
 * @Author: Roy Z
 * @Date: 2019-08-07 14:52
 * @Version: v1.0
 */
public class FileAppenderProperty {
    public static String PATTERN_DEFAULT =
            "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    private final Level level;// 日志级别
    private final String encoderPattern;// 日志输出样式
    private final String logFilePath;// 日志文件路径
    private final String logFileNamePattern;// 日志存档路径Pattern
    private final long singleFileSize;// 单个文件大小
    private final long totalFileSize;// 总文件的大小
    private final int maxHistory;// 存档文件最大数量

    private FileAppenderProperty(Builder builder) {
        this.level = builder.level;
        this.encoderPattern = builder.encoderPattern;
        this.logFilePath = builder.logFilePath;
        this.logFileNamePattern = builder.logFileNamePattern;
        this.singleFileSize = builder.singleFileSize;
        this.totalFileSize = builder.totalFileSize;
        this.maxHistory = builder.maxHistory;
    }

    public String getEncoderPattern() {
        return encoderPattern;
    }

    public Level getLevel() {
        return level;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public String getLogFileNamePattern() {
        return logFileNamePattern;
    }

    public long getSingleFileSize() {
        return singleFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public static class Builder {
        private final Level level;// 日志级别
        private String encoderPattern = PATTERN_DEFAULT;// 日志输出样式
        private String logFilePath;// 日志文件路径
        private String logFileNamePattern;// 日志存档路径Pattern
        private long singleFileSize = -1;// 单个文件大小
        private long totalFileSize = -1;// 总文件的大小
        private int maxHistory = -1;// 存档文件最大数量

        public Builder(Level level) {
            this.level = level;
        }

        public Builder setEncoderPattern(String encoderPattern) {
            this.encoderPattern = encoderPattern;
            return this;
        }

        public Builder setLogFilePath(String logFilePath) {
            this.logFilePath = logFilePath;
            return this;
        }

        public Builder setLogFileNamePattern(String logFileNamePattern) {
            this.logFileNamePattern = logFileNamePattern;
            return this;
        }

        public Builder setSingleFileSize(long singleFileSize) {
            this.singleFileSize = singleFileSize;
            return this;
        }

        public Builder setTotalFileSize(long totalFileSize) {
            this.totalFileSize = totalFileSize;
            return this;
        }

        public Builder setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
            return this;
        }

        public FileAppenderProperty build() {
            // 统一处理默认值
            if (totalFileSize == -1) {
                long internalTotalSize = AndroidStorageUtil.getInternalTotalSize();
                long sdCardTotalSize = AndroidStorageUtil.getSDCardTotalSize();
                long max = Math.max(internalTotalSize, sdCardTotalSize);
                // 存储文件总大小为存储的1/20;
                totalFileSize = max / 20;
            }
            // 单个文件最大10M
            if (singleFileSize == -1) {
                singleFileSize = 1024 * 1024 * 10;
            }
            if (maxHistory == -1) {
                maxHistory = 7;
            }

            if (TextUtils.isEmpty(logFilePath)) {
                throw new RuntimeException("日志保存地址为空");
            }
            if (TextUtils.isEmpty(logFileNamePattern)) {
                throw new RuntimeException("日志存档路径Pattern，请参照" +
                        "\"https://logback.qos.ch/manual/appenders.html#SizeAndTimeBasedFNATP\"" +
                        "或\"www.logback.cn\"");
            }

            return new FileAppenderProperty(this);
        }
    }

}
