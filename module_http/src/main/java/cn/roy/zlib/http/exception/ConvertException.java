package cn.roy.zlib.http.exception;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/2/13 10:29
 * @Version: v1.0
 */
public class ConvertException extends Exception {
    private int code;
    private String msg;
    private String originData;

    public ConvertException(int code, String msg) {
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

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }
}
