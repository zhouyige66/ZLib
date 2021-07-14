package cn.roy.zlib.tool.bean;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2020/4/6 19:45
 * @Version: v1.0
 */
public class CheckBean {
    private boolean checked = false;
    private String itemText;

    public CheckBean(String itemText) {
        this.itemText = itemText;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
