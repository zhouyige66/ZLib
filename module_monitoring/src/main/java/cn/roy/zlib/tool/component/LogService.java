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

import java.util.HashMap;

import cn.roy.zlib.tool.bean.LogItemBean;
import cn.roy.zlib.tool.core.AbsFloatView;
import cn.roy.zlib.tool.core.FloatLogView;
import cn.roy.zlib.tool.core.FloatWindowManager;

/**
 * @Description 悬浮显示日志后台服务
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogService extends Service {
    public static final int LogViewId = 0;

    private HashMap<Integer, AbsFloatView> floatViewContainer;
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

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        floatViewContainer = new HashMap<>();
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
            LogItemBean bean = intent.getParcelableExtra("data");
            if (bean != null) {
                FloatLogView logView = (FloatLogView) showView(LogViewId);
                logView.addLog(bean);
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

        if (floatViewContainer != null) {
            for (Integer key : floatViewContainer.keySet()) {
                hideView(key);
            }
            for (AbsFloatView floatView : floatViewContainer.values()) {
                floatView = null;
            }
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
        Log.i("kk20", msg);
    }

    private AbsFloatView initFloatView(int id) {
        AbsFloatView commonFloatView = null;
        switch (id) {
            case LogViewId:
                FloatLogView floatLogView = new FloatLogView(this);
                floatLogView.setViewFocusable(false);
                int width = floatLogView.getDisplayPoint().x * 2 / 3;
                floatLogView.getLayoutParams().width = width;
                floatLogView.getLayoutParams().height = width * 4 / 3;
                commonFloatView = floatLogView;
                break;
            default:
                break;
        }
        // 添加到floatViewContainer中
        floatViewContainer.put(id, commonFloatView);
        return commonFloatView;
    }

    private AbsFloatView showView(int id) {
        AbsFloatView floatView = floatViewContainer.get(id);
        if (floatView == null) {
            floatView = initFloatView(id);
        }
        FloatWindowManager.showFloatView(getApplicationContext(), floatView);
        return floatView;
    }

    private void hideView(int id) {
        AbsFloatView floatView = floatViewContainer.get(id);
        if (floatView != null) {
            FloatWindowManager.hideFloatView(getApplicationContext(), floatView);
        }
    }

}
