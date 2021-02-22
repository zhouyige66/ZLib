package cn.roy.zlib.log;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

/**
 * @Description: 转换工具
 * @Author: Roy Z
 * @Date: 2019-08-07 17:04
 * @Version: v1.0
 */
public class FileSizeConvertUtil {
    public static final int UNIT_1000 = 1000;
    public static final int UNIT_1024 = 1024;

    @Documented
    @IntDef({UNIT_1000, UNIT_1024})
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    public @interface UnitType {

    }

    public static String getReadableFileSize(long size){
        return getReadableFileSize(size,UNIT_1024);
    }

    /**
     * 获取向下取整可读的文件大小
     *
     * @param size 字节总数
     * @param unit 换算单位
     * @return
     */
    public static String getReadableFileSize(long size, @UnitType int unit) {
        if (size <= 0) {
            return "0B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = 0;
        if (size < unit) {
            digitGroups = 0;
        } else if (size < Math.pow(unit, 2)) {
            digitGroups = 1;
        } else if (size < Math.pow(unit, 3)) {
            digitGroups = 2;
        } else if (size < Math.pow(unit, 4)) {
            digitGroups = 3;
        } else {
            digitGroups = 4;
        }

        return (int)Math.floor(size / Math.pow(unit, digitGroups)) + units[digitGroups];
    }

    public static String getReadableFileSize2(long size) {
        return getReadableFileSize2(size, UNIT_1024);
    }

    public static String getReadableFileSize2(long size, @UnitType int unit) {
        if (size <= 0) {
            return "0B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = 0;
        if (size < unit) {
            digitGroups = 0;
        } else if (size < Math.pow(unit, 2)) {
            digitGroups = 1;
        } else if (size < Math.pow(unit, 3)) {
            digitGroups = 2;
        } else if (size < Math.pow(unit, 4)) {
            digitGroups = 3;
        } else {
            digitGroups = 4;
        }

        return new DecimalFormat("0.##").format(size / Math.pow(unit, digitGroups))
                + units[digitGroups];
    }

}
