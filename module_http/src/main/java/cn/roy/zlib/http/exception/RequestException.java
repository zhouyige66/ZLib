package cn.roy.zlib.http.exception;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/2/13 10:29
 * @Version: v1.0
 */
public class RequestException extends Exception {
    private int code;
    private String msg;

    public RequestException(int code, String msg) {
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
