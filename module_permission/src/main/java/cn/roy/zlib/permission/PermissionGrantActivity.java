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
    // 是否已经申请过权限
    private boolean isNeedCheckPermission = true;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isNeedCheckPermission) {
            String[] permissions = getRequirePermissions();
            if (!PermissionUtil.hasPermissions(this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                allPermissionGranted(); // 全部权限都已获取
            }
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
        if (requestCode == PERMISSION_REQUEST_CODE && isAllPermissionGranted(grantResults)) {
            isNeedCheckPermission = true;
            // 低版本有可能权限被拒绝了，grantResults依然返回0，再使用权限检查器检查一遍
            if (!PermissionUtil.hasPermissions(this, getRequirePermissions())) {
                showMissingPermissionDialog();
            } else {
                allPermissionGranted();
            }
        } else {
            isNeedCheckPermission = false;
            showMissingPermissionDialog();
        }
    }

    // 返回传递的权限参数
    private String[] getRequirePermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    // 全部权限均已获取
    private void allPermissionGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    // 含有全部的权限
    private boolean isAllPermissionGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.permission_lack_dialog_title));
        builder.setMessage(getString(R.string.permission_lack_dialog_msg));
        builder.setNegativeButton(R.string.permission_lack_dialog_negative_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(PERMISSIONS_DENIED);
                        finish();
                    }
                });
        builder.setPositiveButton(R.string.permission_lack_dialog_positive_button,
                new DialogInterface.OnClickListener() {
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
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
