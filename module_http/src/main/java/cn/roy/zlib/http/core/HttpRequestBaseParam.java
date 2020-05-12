package cn.roy.zlib.http.core;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/5/9 10:18
 * @Version: v1.0
 */
public class HttpRequestBaseParam {
    private long connectTimeout;
    private long writeTimeout;
    private long readTimeout;
    private long callTimeout;

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getCallTimeout() {
        return callTimeout;
    }

    public void setCallTimeout(long callTimeout) {
        this.callTimeout = callTimeout;
    }
}
