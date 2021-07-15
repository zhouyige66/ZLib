package cn.roy.zlib.tool.bean;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/4/6 19:45
 * @Version: v1.0
 */
public class CheckBean {
    private String text;
    private boolean checked = false;

    public CheckBean(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
