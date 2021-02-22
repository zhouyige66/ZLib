package cn.roy.zlib.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

/**
 * @Description: 权限检测工具
 * @Author: Roy Z
 * @Date: 2019-08-04 20:53
 * @Version: v1.0
 */
public class PermissionUtil {

    /**
     * 获取TargetSDKVersion
     *
     * @param context
     * @return
     */
    public static int getTargetSDKVersion(@NonNull Context context) {
        int targetSDKVersion = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSDKVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return targetSDKVersion;
    }

    /**
     * 判断权限集合
     *
     * @param context
     * @param permissions
     * @return true-已授权，false-未授权
     */
    public static boolean hasPermissions(@NonNull Context context, @NonNull String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断指定权限是否被授予
     *
     * @param context
     * @param permission
     * @return true-已授权，false-未授权
     */
    public static boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        /**
         * 1.Context.checkSelfPermission(String permission)
         * 2.ContextCompat.checkSelfPermission(String permission)
         *   2.1 调用Context.checkPermission(@NonNull String permission, int pid, int uid)
         *   2.2 targetSDKVersion<23时，不起作用
         * 3.PackageManager.checkPermission(String permName, String pkgName)
         * 4.AppOpsManager.permissionToOp(permission)，要求VERSION>=23
         * 5.PermissionChecker.checkSelfPermission(@NonNull Context context,@NonNull String permission)
         *   5.1 targetSDKVersion>=23时无效，小于23时，直接返回true，检测结果不准确
         */
        boolean hasPermission;

        // 以23版本为界（Build.VERSION_CODES.M）
        int targetSDKVersion = getTargetSDKVersion(context);
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 23) {// Android 6.0及以上
            if (targetSDKVersion >= 23) {// 动态授权，检测方案不同
                hasPermission = context.checkSelfPermission(permission) ==
                        PackageManager.PERMISSION_GRANTED;
            } else {// 使用兼容类PermissionChecker
                hasPermission = PermissionChecker.checkSelfPermission(context, permission) ==
                        PermissionChecker.PERMISSION_GRANTED;
            }
        } else {
            // 检测结果不准确，有可能被应用管家之类的关闭了，最好是try catch处理一下
            hasPermission = ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED;
        }

        return hasPermission;
    }

}
