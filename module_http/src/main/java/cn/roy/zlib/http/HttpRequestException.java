package cn.roy.zlib.http;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/2/13 10:29
 * @Version: v1.0
 */
public class HttpRequestException extends RuntimeException {

    private int code;
    private String msg;

    public HttpRequestException(int code, String msg) {
        super(msg);

        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
