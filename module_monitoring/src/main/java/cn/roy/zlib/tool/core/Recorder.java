package cn.roy.zlib.tool.core;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.roy.zlib.tool.bean.LogBean;
import cn.roy.zlib.tool.bean.TagBean;
import cn.roy.zlib.tool.component.ApplyAlertWindowPermissionActivity;
import cn.roy.zlib.tool.component.LogService;
import cn.roy.zlib.tool.util.AppOpsManagerUtil;

/**
 * @Description: 记录器
 * @Author: Roy Z
 * @Date: 2020/4/6 14:59
 * @Version: v1.0
 */
public final class Recorder {
    private Context appContext;
    private int maxLogItemCount = 1000;// 默认日志容器存储最大值
    private List<LogBean> originData;
    private Map<String, TagBean> tagBeanMap;
    private Map<Integer, Set<String>> levelTagMap;
    private boolean hasRequestDrawOverlaysPermission = false;

    private Recorder() {
        originData = new ArrayList<>();
        tagBeanMap = new HashMap<>();
        levelTagMap = new HashMap<>();
    }

    private static Recorder instance;

    public static Recorder getInstance() {
        if (instance == null) {
            synchronized (Recorder.class) {
                if (instance == null) {
                    instance = new Recorder();
                }
            }
        }
        return instance;
    }

    public void setAppContext(Context context) {
        this.appContext = context;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setMaxLogItemCount(int maxLogItemCount) {
        this.maxLogItemCount = maxLogItemCount;
    }

    public List<String> getLogTagList(Set<Integer> levels) {
        List<String> tags = new ArrayList<>();
        for (Integer level : levels) {
            Set<String> tagSet = levelTagMap.get(level);
            if (tagSet != null && !tagSet.isEmpty()) {
                tags.addAll(tagSet);
            }
        }
        return tags;
    }

    public List<LogBean> getLogList(Set<Integer> levels, Set<String> tags) {
        List<LogBean> logBeans = new ArrayList<>();
        for (String tag : tags) {
            TagBean tagBean = tagBeanMap.get(tag);
            if (tagBean != null) {
                List<LogBean> logBeanList = tagBean.logBeanList;
                if (!logBeanList.isEmpty()) {
                    for (LogBean bean : logBeanList) {
                        if (levels.contains(bean.getLogLevel())) {
                            logBeans.add(bean);
                        }
                    }
                }
            }
        }
        return logBeans;
    }

    public List<LogBean> getAllLogList() {
        return new ArrayList<>(originData);
    }

    /**
     * 添加日志到容器中
     *
     * @param logBean
     */
    public void addLog(LogBean logBean) {
        originData.add(logBean);
        int size = originData.size();
        if (size > maxLogItemCount) {
            int removeSize = size - maxLogItemCount + 1;
            List<LogBean> removeLogBeans = originData.subList(0, removeSize);
            for (LogBean item : removeLogBeans) {
                int logLevel = item.getLogLevel();
                String logTag = item.getLogTag();
                // 从相应的标签列表中移除
                TagBean tagBean = tagBeanMap.get(logTag);
                if (tagBean != null) {
                    List<LogBean> logBeanList = tagBean.logBeanList;
                    if (!logBeanList.isEmpty()) {
                        logBeanList.remove(item);
                    }
                    if (logBeanList.isEmpty()) {
                        tagBeanMap.remove(logTag);
                    }
                }
                Set<String> tagSet = levelTagMap.get(logLevel);
                if (tagSet != null) {
                    tagSet.remove(logTag);
                    if (tagSet.isEmpty()) {
                        levelTagMap.remove(logLevel);
                    }
                }
            }
            originData.removeAll(removeLogBeans);
        }

        String logTag = logBean.getLogTag();
        int logLevel = logBean.getLogLevel();
        // 维护tagBeanMap
        TagBean tagBean = tagBeanMap.get(logTag);
        if (tagBean != null) {
            List<LogBean> logBeanList = tagBean.logBeanList;
            logBeanList.add(logBean);
        } else {
            tagBean = new TagBean();
            tagBean.tagName = logTag;
            tagBean.logBeanList = new ArrayList<>();
            tagBean.logBeanList.add(logBean);
            tagBeanMap.put(logTag, tagBean);
        }
        // 维护levelTagMap
        Set<String> tagSet = levelTagMap.get(logLevel);
        if (tagSet == null) {
            tagSet = new HashSet<>();
            levelTagMap.put(logLevel, tagSet);
        }
        tagSet.add(logTag);

        if (appContext == null) {
            return;
        }
        if (!AppOpsManagerUtil.checkDrawOverlays(appContext)) {
            if (hasRequestDrawOverlaysPermission) {
                return;
            }
            hasRequestDrawOverlaysPermission = true;
            Intent i = new Intent(appContext, ApplyAlertWindowPermissionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(i);
            return;
        }
        hasRequestDrawOverlaysPermission = true;
        // 构建日志实体，显示在悬浮窗口
        Intent intent = new Intent(appContext, LogService.class);
        intent.putExtra("data", logBean);
        appContext.startService(intent);
    }

    /**
     * 清空数据
     */
    public void clear() {
        originData.clear();
        tagBeanMap.clear();
        levelTagMap.clear();
    }

}
