package cn.roy.zlib.httptest;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2021/02/05
 * @Version: v1.0
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result success(T t) {
        Result<Object> result = new Result<>();
        result.setCode(200);
        result.setData(t);
        return result;
    }

    public static Result fail(int code, String msg) {
        Result result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
