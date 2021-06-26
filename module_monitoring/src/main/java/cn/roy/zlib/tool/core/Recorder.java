package cn.roy.zlib.tool.core;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.roy.zlib.tool.bean.LogItemBean;
import cn.roy.zlib.tool.bean.TagBean;
import cn.roy.zlib.tool.component.AlertWindowPermissionGrantActivity;
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
    private List<LogItemBean> originData;
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

    public List<LogItemBean> getLogList(Set<Integer> levels, Set<String> tags) {
        List<LogItemBean> logItemBeans = new ArrayList<>();
        for (String tag : tags) {
            TagBean tagBean = tagBeanMap.get(tag);
            if (tagBean != null) {
                List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
                if (!logItemBeanList.isEmpty()) {
                    for (LogItemBean bean : logItemBeanList) {
                        if (levels.contains(bean.getLogLevel())) {
                            logItemBeans.add(bean);
                        }
                    }
                }
            }
        }
        return logItemBeans;
    }

    public List<LogItemBean> getAllLogList() {
        return new ArrayList<>(originData);
    }

    /**
     * 添加日志到容器中
     *
     * @param logItemBean
     */
    public void addLog(LogItemBean logItemBean) {
        originData.add(logItemBean);
        int size = originData.size();
        if (size > maxLogItemCount) {
            int removeSize = size - maxLogItemCount + 1;
            List<LogItemBean> removeLogItemBeans = originData.subList(0, removeSize);
            for (LogItemBean item : removeLogItemBeans) {
                int logLevel = item.getLogLevel();
                String logTag = item.getLogTag();
                // 从相应的标签列表中移除
                TagBean tagBean = tagBeanMap.get(logTag);
                if (tagBean != null) {
                    List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
                    if (!logItemBeanList.isEmpty()) {
                        logItemBeanList.remove(item);
                    }
                    if (logItemBeanList.isEmpty()) {
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
            originData.removeAll(removeLogItemBeans);
        }

        String logTag = logItemBean.getLogTag();
        int logLevel = logItemBean.getLogLevel();
        // 维护tagBeanMap
        TagBean tagBean = tagBeanMap.get(logTag);
        if (tagBean != null) {
            List<LogItemBean> logItemBeanList = tagBean.logItemBeanList;
            logItemBeanList.add(logItemBean);
        } else {
            tagBean = new TagBean();
            tagBean.tagName = logTag;
            tagBean.logItemBeanList = new ArrayList<>();
            tagBean.logItemBeanList.add(logItemBean);
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
            Intent i = new Intent(appContext, AlertWindowPermissionGrantActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(i);
            return;
        }
        hasRequestDrawOverlaysPermission = true;
        // 构建日志实体，显示在悬浮窗口
        Intent intent = new Intent(appContext, LogService.class);
        intent.putExtra("data", logItemBean);
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
