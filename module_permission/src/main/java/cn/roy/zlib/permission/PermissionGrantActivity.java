package cn.roy.zlib.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * @Description: 权限申请页面
 * @Author: Roy Z
 * @Date: 2018/8/15 下午1:47
 * @Version: v1.0
 */
public class PermissionGrantActivity extends AppCompatActivity {
    /**
     * 请求权限被授于
     */
    public static final int PERMISSIONS_GRANTED = 0;
    /**
     * 请求权限被拒绝
     */
    public static final int PERMISSIONS_DENIED = 1;
    /**
     * 请求权限传递参数key
     */
    public static final String EXTRA_PERMISSIONS = "permission.extra_permission";
    // 系统权限管理页面请求code
    private static final int PERMISSION_REQUEST_CODE = 0;
    // 系统权限管理页面请求code
    public static final int CODE_PERMISSION_GRANT_REQUEST = 10000;
    // 方案
    private static final String PACKAGE_URL_SCHEME = "package:";

    private boolean isRequireCheck; // 是否需要系统权限检测

    /**
     * 启动授权页面
     *
     * @param activity
     * @param permissions
     */
    public static void jump2PermissionGrantActivity(Activity activity, String[] permissions) {
        Intent intent = new Intent(activity, PermissionGrantActivity.class);
        intent.putExtra(PermissionGrantActivity.EXTRA_PERMISSIONS, permissions);
        activity.startActivityForResult(intent, CODE_PERMISSION_GRANT_REQUEST);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRequireCheck) {
            String[] permissions = getPermissions();
            if (!PermissionUtil.hasPermissions(this, permissions)) {
                requestPermissions(permissions); // 请求权限
            } else {
                allPermissionsGranted(); // 全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过
     * 如果权限缺失, 则提示Dialog
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            // 低版本有可能权限被拒绝了，grantResults依然返回0，再使用权限检查器检查一遍
            if (!PermissionUtil.hasPermissions(this, getPermissions())) {
                showMissingPermissionDialog();
            } else {
                allPermissionsGranted();
            }
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    // 返回传递的权限参数
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    // 请求权限兼容低版本
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionGrantActivity.this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\n请点击“设置”-“权限”-打开所需权限。\n最后点击两次“后退”按钮即可返回。");

        // 拒绝, 退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

}
