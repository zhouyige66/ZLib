package cn.roy.zlib;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Trace;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.qos.logback.classic.Level;
import cn.roy.zlib.http.RetrofitFactory;
import cn.roy.zlib.http.metrics.RequestMetrics;
import cn.roy.zlib.http.metrics.RequestMetricsListener;
import cn.roy.zlib.httptest.APITest;
import cn.roy.zlib.log.AndroidStorageUtil;
import cn.roy.zlib.log.FileAppenderProperty;
import cn.roy.zlib.log.LogConfigBuilder;
import cn.roy.zlib.log.LogUtil;
import cn.roy.zlib.monitor.ExceptionInfoLogger;
import cn.roy.zlib.monitor.Monitor;
import cn.roy.zlib.permission.PermissionGrantActivity;
import cn.roy.zlib.permission.PermissionUtil;
import cn.roy.zlib.tool.MonitoringToolSDK;
import cn.roy.zlib.tool.MonitoringTool;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private APITest apiTest;
    private boolean hasPermission = false;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("首页");
        button = findViewById(R.id.button1);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);

        initHttp();
        new Monitor(this)
                .setBlockMonitorEnable(true)
                .setBlockMonitorTimeout(2000)
                .setCrashMonitorEnable(true)
                .setCrashLogAutoSave(true)
                .setExceptionInfoLogger(new ExceptionInfoLogger() {
                    @Override
                    public void print(String exceptionInfo) {
                        LogUtil.e(exceptionInfo);
                    }
                })
                .init();
        hasPermission = PermissionUtil.hasPermissions(this, permissions);
        if (hasPermission) {
            button.setText("已授权");
            initLogback();
        }

        RequestMetrics.Companion.getInstance().registerListener(new RequestMetricsListener() {
            @Override
            public void onStatisticsChange() {
                int initCount = RequestMetrics.StatisticCount.INSTANCE.getInitCount();
                int cancelCount = RequestMetrics.StatisticCount.INSTANCE.getCancelCount();
                int successCount = RequestMetrics.StatisticCount.INSTANCE.getSuccessCount();
                int failCount = RequestMetrics.StatisticCount.INSTANCE.getFailCount();
                LogUtil.d("发起请求：" + initCount + "，取消：" + cancelCount + "，成功：" + successCount + "，失败：" + failCount);
            }

            @Override
            public void onAnalysisChange(@NotNull String url, long averageTime) {
                LogUtil.d("最近访问请求：" + url + "，该请求平均耗时：" + averageTime + "ms");
            }
        });

        MonitoringToolSDK.init(new MonitoringToolSDK.Options(this).setMaxLogCount(50));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PermissionGrantActivity.CODE_PERMISSION_GRANT_REQUEST) {
            if (resultCode == PermissionGrantActivity.PERMISSIONS_GRANTED) {
                button.setText("已授权");
                initLogback();
            } else {
                Toast.makeText(this, "读写权限未被授予，请点击授权重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.beginSection("onClick");
        }
        switch (v.getId()) {
            case R.id.button1:
                requestPermission();
                break;
            case R.id.button2:
                MonitoringTool.d("roy", "点击了测试1");
//                Call<Result<JsonObject>> call = apiTest.getSessionId();
//                CancelableTask task = RequestExecutor.execute(call, new RequestCallback<Result<JsonObject>>() {
//                    @Override
//                    public void success(Result<JsonObject> jsonObjectResult) {
//                        LogUtil.d("" + jsonObjectResult.getCode());
//                    }
//
//                    @Override
//                    public void fail(int code, @NotNull String msg) {
//                        LogUtil.d(msg);
//                    }
//                });
                break;
            case R.id.button3:
                MonitoringTool.i("kk20", "点击了测试1");
//                Observable<Result<User>> observable = apiTest.getUser();
//                RequestExecutor.execute(observable, new RequestCallback<Result<User>>() {
//                    @Override
//                    public void success(Result<User> user) {
//                        LogUtil.d("" + user.getCode());
//                    }
//
//                    @Override
//                    public void fail(int code, @NotNull String msg) {
//                        LogUtil.d(msg);
//                    }
//                });
                break;
            case R.id.button4:
                MonitoringTool.e("zzy", "点击了测试1");
//                Debug.startMethodTracing();
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Debug.stopMethodTracing();
                break;
            case R.id.button5:
                MonitoringTool.w("zhou", "点击测试");
//                int i = 0;
//                int k = 100 / i;
//                System.out.println("计算结果：" + k);
                break;
            case R.id.button6:
                singleModel();
                break;
            case R.id.button7:
                multipleModel();
                break;
            case R.id.button8:
                int random = new Random().nextInt(4);
                String log = "点击测试" + random;
                switch (random) {
                    case 0:
                        LogUtil.d(log);
                        break;
                    case 1:
                        LogUtil.i(log);
                        break;
                    case 2:
                        LogUtil.w(log);
                        break;
                    case 3:
                        LogUtil.e(log);
                        break;
                    default:
                        LogUtil.d("默认");
                        break;
                }
            case R.id.button9:
                getSharedPreferences("test", MODE_PRIVATE).edit().putString("name", "roy").apply();
                break;
            default:
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }
    }

    private void requestPermission() {
        if (hasPermission) {
            return;
        }
        PermissionGrantActivity.jump2PermissionGrantActivity(this, permissions);
    }

    private void initHttp() {
//        Retrofit retrofit = RetrofitFactory.create("http://10.0.2.2:9999/");
//        Retrofit retrofit = RetrofitFactory.create("http://192.168.1.8:9999/");
        Retrofit retrofit = RetrofitFactory.create("http://10.185.42.79:9999/");
        apiTest = retrofit.create(APITest.class);
    }

    private void initLogback() {
        new LogConfigBuilder(this).setRootLevel(Level.DEBUG).buildDefault();
    }

    private void singleModel() {
        String storagePath = Environment.getDataDirectory().getAbsolutePath();
        boolean granted = AndroidStorageUtil.isStoragePermissionGranted(this);
        if (AndroidStorageUtil.isExternalStorageAvailable() && granted) {
            storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            storagePath = storagePath.concat(File.separator).concat(this.getPackageName());
        }
        String logFileDir = storagePath.concat(File.separator).concat("log");
        String logFileName = logFileDir + File.separator + "log.txt";
        String fileNamePattern = logFileDir + File.separator + "log_%d{yyyy-MM-dd}@%i.txt";
        FileAppenderProperty prop = new FileAppenderProperty.Builder(Level.INFO)
                .setTotalFileSize(1024 * 30)
                .setSingleFileSize(1024 * 10)
                .setMaxHistory(2)
                .setLogFilePath(logFileName)
                .setLogFileNamePattern(fileNamePattern)
                .build();
        new LogConfigBuilder(this)
                .setRootLevel(Level.DEBUG)
                .setLogcatAppenderProp(Level.INFO, FileAppenderProperty.PATTERN_DEFAULT)
                .buildForSingleFileModel(prop);
    }

    private void multipleModel() {
        String storagePath = Environment.getDataDirectory().getAbsolutePath();
        boolean granted = AndroidStorageUtil.isStoragePermissionGranted(this);
        if (AndroidStorageUtil.isExternalStorageAvailable() && granted) {
            storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            storagePath = storagePath.concat(File.separator).concat(this.getPackageName());
        }
        // 日志文件夹
        String logFilePath = storagePath.concat(File.separator).concat("log");
        String logFileName = logFilePath + File.separator + "%s.txt";
        String fileNamePattern = logFilePath + File.separator + "%s_%d{yyyy-MM-dd}@%i.txt";
        FileAppenderProperty debugProp = create(Level.DEBUG, logFileName, fileNamePattern);
        FileAppenderProperty infoProp = create(Level.INFO, logFileName, fileNamePattern);
        FileAppenderProperty warnProp = create(Level.WARN, logFileName, fileNamePattern);
        FileAppenderProperty errorProp = create(Level.ERROR, logFileName, fileNamePattern);
        List<FileAppenderProperty> list = new ArrayList<>();
        list.add(debugProp);
        list.add(infoProp);
        list.add(warnProp);
        list.add(errorProp);
        new LogConfigBuilder(this)
                .setRootLevel(Level.DEBUG)
                .setLogcatAppenderProp(Level.INFO, FileAppenderProperty.PATTERN_DEFAULT)
                .buildForMultipleFileModel(list);
    }

    private FileAppenderProperty create(Level level, String logFileName, String fileNamePattern) {
        String logNamePrefix = level.levelStr.toLowerCase();
        String logFilePath = String.format(logFileName, logNamePrefix);
        String rollingFileNamePattern = fileNamePattern.replace("%s", logNamePrefix);

        // 读取手机存储
        long internalTotalSize = AndroidStorageUtil.getInternalTotalSize();
        long sdCardTotalSize = AndroidStorageUtil.getSDCardTotalSize();
        long max = Math.max(internalTotalSize, sdCardTotalSize);
        // 存储文件总大小为存储的1/10;
        long totalFileSize = max / 20;
        // 单个文件最大10M
        long singleFileSize = 1024 * 1024 * 10;
        // 默认保存最大天数为7
        int maxHistory = 7;
        FileAppenderProperty prop = new FileAppenderProperty.Builder(level)
                .setLogFilePath(logFilePath)
                .setLogFileNamePattern(rollingFileNamePattern)
                .setSingleFileSize(singleFileSize)
                .setTotalFileSize(totalFileSize)
                .setMaxHistory(maxHistory)
                .build();

        return prop;
    }
}
