package cn.roy.zlib.tool.component;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import cn.roy.zlib.tool.bean.LogBean;
import cn.roy.zlib.tool.core.LogFloatView;
import cn.roy.zlib.tool.core.FloatWindowManager;

/**
 * @Description 悬浮显示日志后台服务
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogService extends Service {
    private PowerManager.WakeLock wakeLock;
    private BroadcastReceiver mHomeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            log("监听到home键");
                        } else {
                            SYSTEM_DIALOG_REASON_RECENT_APPS:
                            log("监听到多任务切换键");
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private LogFloatView logFloatView;

    @Override
    public void onCreate() {
        super.onCreate();

        // 申请锁
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "FloatLog:WakeLock");
            if (wakeLock != null) {
                wakeLock.acquire();
            }
        }
        // 注册广播
        IntentFilter mHomeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeListenerReceiver, mHomeFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            LogBean bean = intent.getParcelableExtra("data");
            if (bean != null) {
                if (logFloatView == null) {
                    logFloatView = new LogFloatView(this);
                    logFloatView.setViewFocusable(false);
                    int width = logFloatView.getDisplayPoint().x * 2 / 3;
                    logFloatView.getLayoutParams().width = width;
                    logFloatView.getLayoutParams().height = width * 4 / 3;
                }
                FloatWindowManager.addFloatView(getApplicationContext(), logFloatView);
                logFloatView.addLog(bean);
            }
        }
        return super.onStartCommand(intent, START_FLAG_RETRY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (wakeLock != null) {
            wakeLock.release();
        }
        if (logFloatView != null) {
            FloatWindowManager.removeFloatView(getApplicationContext(), logFloatView);
            logFloatView = null;
        }
        unregisterReceiver(mHomeListenerReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void log(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.d("kk20", msg);
    }

}
