package cn.roy.zlib.tool.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Description 日志bean
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogBean implements Parcelable {
    public static final int VERBOSE = 0;
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;

    private int logLevel;
    private String logTag;
    private String logText;
    private long date;

    public LogBean(int logLevel, String logTag, String logText) {
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logText = logText;

        this.date = System.currentTimeMillis();
    }

    public int getLogLevel() {
        return logLevel;
    }

    public String getLogLevelStr() {
        String s = "V";
        switch (logLevel) {
            case 1:
                s = "D";
                break;
            case 2:
                s = "I";
                break;
            case 3:
                s = "W";
                break;
            case 4:
                s = "E";
                break;
            default:
                break;
        }
        return s;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogTag() {
        return logTag;
    }

    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.logLevel);
        dest.writeString(this.logTag);
        dest.writeString(this.logText);
        dest.writeLong(this.date);
    }

    protected LogBean(Parcel in) {
        this.logLevel = in.readInt();
        this.logTag = in.readString();
        this.logText = in.readString();
        this.date = in.readLong();
    }

    public static final Creator<LogBean> CREATOR = new Creator<LogBean>() {
        @Override
        public LogBean createFromParcel(Parcel source) {
            return new LogBean(source);
        }

        @Override
        public LogBean[] newArray(int size) {
            return new LogBean[size];
        }
    };
}
