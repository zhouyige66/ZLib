package cn.roy.zlib.tool.core;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.roy.zlib.tool.R;
import cn.roy.zlib.tool.adapter.CommonAdapter;
import cn.roy.zlib.tool.adapter.base.ViewHolder;
import cn.roy.zlib.tool.bean.CheckBean;
import cn.roy.zlib.tool.bean.LogBean;

/**
 * @Description 悬浮日志视图
 * @Author kk20
 * @Date 2018/4/16
 * @Version V1.0.0
 */
public class LogFloatView extends AbsFloatView implements View.OnClickListener {
    private static final int FLAG_EXPAND = 0;// 展开
    private static final int FLAG_SHRINK = 1;// 收缩

    // 配置的宽高
    private int width = 0;
    private int height = 0;

    // 工具栏
    private View v_control;
    private TextView tv_level, tv_tag;
    private ImageView iv_clean;
    private ImageView iv_expand;
    private ImageView iv_close;
    private RecyclerView recyclerView;
    // 过滤设置
    private View v_filter;
    private TextView tv_filter_title;
    private RecyclerView recyclerView_choose;
    private CheckBox cb_select_all;
    private Button btn_confirm, btn_cancel;
    // 适配器
    private CommonAdapter<CheckBean> filterAdapter;
    private LogListAdapter logListAdapter;

    private List<CheckBean> levels;// 等级
    private List<CheckBean> tags;// 标签
    private List<CheckBean> filterDataList;// 筛选适配器数据
    private List<LogBean> logBeanList;// log适配器数据
    private Set<Integer> selectLevelSet;// 当前选中的显示等级
    private Set<String> selectTagSet;// 当前选中的显示标签
    private boolean isShowLevelFilter;// 是否是等级筛选
    private boolean isSelectAllTag = true;// 是否全选

    public LogFloatView(Context context) {
        super(context, R.layout.layout_log_view);
    }

    @Override
    public void init(final View view) {
        initData();

        // 工具栏
        v_control = view.findViewById(R.id.v_control);
        tv_level = view.findViewById(R.id.tv_level);
        tv_tag = view.findViewById(R.id.tv_tag);
        iv_clean = view.findViewById(R.id.iv_clean);
        iv_expand = view.findViewById(R.id.iv_expand);
        iv_close = view.findViewById(R.id.iv_close);
        // 数据区
        recyclerView = view.findViewById(R.id.recyclerView);
        // 过滤选择区
        v_filter = view.findViewById(R.id.v_choose);
        tv_filter_title = view.findViewById(R.id.tv_filter_title);
        recyclerView_choose = view.findViewById(R.id.recyclerView_choose);
        cb_select_all = view.findViewById(R.id.cb_select_all);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_cancel = view.findViewById(R.id.btn_cancel);

        iv_expand.setTag(FLAG_EXPAND);
        tv_level.setOnClickListener(this);
        tv_tag.setOnClickListener(this);
        iv_clean.setOnClickListener(this);
        iv_expand.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        cb_select_all.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        // 等级与表签
        recyclerView_choose.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        recyclerView_choose.addItemDecoration(new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL));
        filterAdapter = new CommonAdapter<CheckBean>(view.getContext(),
                R.layout.item_log_level_tag, filterDataList) {
            @Override
            protected void convert(ViewHolder viewHolder, CheckBean checkBean, int position) {
                CheckBox checkBox = viewHolder.getView(R.id.cb_level);
                checkBox.setText(checkBean.getText());
                checkBox.setChecked(checkBean.isChecked());
                checkBox.setTag(position);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int index = (int) buttonView.getTag();
                    filterDataList.get(index).setChecked(isChecked);
                    // 检查所有选项是否已全选
                    if (isChecked) {
                        boolean allSelected = true;
                        int size = filterDataList.size();
                        for (int i1 = 0; i1 < size; i1++) {
                            CheckBean item = filterDataList.get(i1);
                            if (!item.isChecked()) {
                                allSelected = false;
                                break;
                            }
                        }
                        cb_select_all.setChecked(allSelected);
                    } else {
                        cb_select_all.setChecked(false);
                    }
                    if (!isShowLevelFilter) {
                        isSelectAllTag = cb_select_all.isChecked();
                    }
                });
            }
        };
        recyclerView_choose.setAdapter(filterAdapter);

        // 日志
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(Color.parseColor("#999999")));
        recyclerView.addItemDecoration(dividerItemDecoration);
        logListAdapter = new LogListAdapter(view.getContext(), logBeanList);
        recyclerView.setAdapter(logListAdapter);

        // 监听器
        setOnFloatViewEventListener(new OnFloatViewEventListener() {
            @Override
            public void onBackEvent() {

            }

            @Override
            public void onClickEvent() {

            }

            @Override
            public void onMoveEvent(AbsFloatView floatView, float x, float y) {
                int offsetX = (int) (floatView.getLayoutParams().x + x);
                int offsetY = (int) (floatView.getLayoutParams().y + y);
                if (offsetX >= 0 && offsetX < floatView.getDisplayPoint().x) {
                    floatView.getLayoutParams().x = offsetX;
                }
                if (offsetY >= 0 && offsetY < floatView.getDisplayPoint().y) {
                    floatView.getLayoutParams().y = offsetY;
                }
                FloatWindowManager.updateFloatView(
                        floatView.getView().getContext().getApplicationContext(), floatView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tv_level) {// 等级过滤
            expand();
            showLevelChoose();
        } else if (v == tv_tag) {// 标签过滤
            expand();
            showTagChoose();
        } else if (v == iv_clean) {// 清空
            Recorder.getInstance().clear();
            logBeanList.clear();
            logListAdapter.notifyDataSetChanged();
            tags.clear();
            selectTagSet.clear();
        } else if (v == iv_expand) {// 折叠
            int flag = (int) v.getTag();
            if (flag == FLAG_SHRINK) {// 当前是收缩，变为展开
                expand();
            } else {
                v.setTag(FLAG_SHRINK);
                iv_expand.setImageResource(R.drawable.ic_log_expand);
                width = getLayoutParams().width;
                height = getLayoutParams().height;
                recyclerView.setVisibility(View.GONE);
                getLayoutParams().height = v_control.getHeight();
                FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(),
                        LogFloatView.this);
            }
        } else if (v == iv_close) {// 关闭
            logBeanList.clear();
            logListAdapter.notifyDataSetChanged();
            FloatWindowManager.hideFloatView(view.getContext().getApplicationContext(), this);
        } else if (v == btn_confirm) {
            updateData();
        } else if (v == btn_cancel) {
            v_filter.setVisibility(View.GONE);
            resetFilterData();
        } else if (v == cb_select_all) {
            for (CheckBean item : filterDataList) {
                item.setChecked(cb_select_all.isChecked());
            }
            new Handler().post(() -> filterAdapter.notifyDataSetChanged());
        }
    }

    private void initData() {
        String[] levelArray = {"Verbose", "Debug", "Info", "Warn", "Error"};
        levels = new ArrayList<>(5);
        tags = new ArrayList<>();
        selectLevelSet = new HashSet<>();
        selectTagSet = new HashSet<>();
        filterDataList = new ArrayList<>();
        logBeanList = new ArrayList<>();
        // 填充数据
        int index = 0;
        for (String level : levelArray) {
            CheckBean item = new CheckBean(level);
            item.setChecked(true);
            levels.add(item);
            selectLevelSet.add(index);
            index++;
        }
        // 添加所有记录
        logBeanList.addAll(Recorder.getInstance().getAllLogList());
        setTags();
    }

    private void setTags() {
        tags.clear();
        for (String tag : Recorder.getInstance().getLogTagList(selectLevelSet)) {
            CheckBean checkBean = new CheckBean(tag);
            checkBean.setChecked(true);
            tags.add(checkBean);
            selectTagSet.add(tag);
        }
    }

    private void expand() {
        int flag = (int) iv_expand.getTag();
        if (flag == FLAG_EXPAND) {
            return;
        }

        iv_expand.setTag(FLAG_EXPAND);
        iv_expand.setImageResource(R.drawable.ic_log_shrink);
        // 设置新的param
        recyclerView.setVisibility(View.VISIBLE);
        getLayoutParams().height = height;
        FloatWindowManager.updateFloatView(view.getContext().getApplicationContext(), this);
    }

    /**
     * 显示等级过滤
     */
    private void showLevelChoose() {
        isShowLevelFilter = true;
        v_filter.setVisibility(View.VISIBLE);
        tv_filter_title.setText("请选择过滤等级");
        filterDataList.clear();
        filterDataList.addAll(levels);
        filterAdapter.notifyDataSetChanged();
        cb_select_all.setChecked(selectLevelSet.size() == levels.size());
        cb_select_all.setClickable(true);
    }

    /**
     * 显示标签过滤
     */
    private void showTagChoose() {
        isShowLevelFilter = false;
        v_filter.setVisibility(View.VISIBLE);
        tv_filter_title.setText("请选择过滤标签");
        filterDataList.clear();
        filterDataList.addAll(tags);
        filterAdapter.notifyDataSetChanged();
        if (tags.isEmpty()) {// 默认全选
            cb_select_all.setClickable(false);
            cb_select_all.setChecked(true);
        } else {
            cb_select_all.setClickable(true);
            cb_select_all.setChecked(isSelectAllTag);
        }
    }

    /**
     * 重置过滤器数据
     */
    private void resetFilterData() {
        if (isShowLevelFilter) {
            int index = 0;
            for (CheckBean item : levels) {
                item.setChecked(selectLevelSet.contains(index));
                index++;
            }
        } else {
            for (CheckBean item : tags) {
                item.setChecked(selectTagSet.contains(item.getText()));
            }
        }
    }

    private void updateData() {
        // 检查当前选项中是否至少选择一项
        boolean check = false;
        if (filterDataList.isEmpty()) {
            check = true;
        } else {
            for (CheckBean item : filterDataList) {
                if (item.isChecked()) {
                    check = true;
                    break;
                }
            }
        }
        if (!check) {
            if (isShowLevelFilter || !isSelectAllTag) {
                // 至少选择一项
                Toast.makeText(Recorder.getInstance().getAppContext(), "请至少选择一项",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        v_filter.setVisibility(View.GONE);
        if (isShowLevelFilter) {// 等级筛选
            List<Integer> list = new ArrayList<>();
            int index = 0;
            for (CheckBean item : levels) {
                if (item.isChecked()) {
                    list.add(index);
                }
                index++;
            }
            // 判断是否切换Level
            if (!selectLevelSet.containsAll(list) || selectLevelSet.size() != list.size()) {
                selectLevelSet.clear();
                selectLevelSet.addAll(list);
                isSelectAllTag = true;
                logBeanList.clear();
                logBeanList.addAll(Recorder.getInstance().getLogListByLevels(selectLevelSet));
                logListAdapter.notifyDataSetChanged();
                // 更改标签列表
                setTags();
            }
            return;
        }
        Set<String> tempSet = new HashSet<>();
        for (CheckBean item : tags) {
            if (item.isChecked()) {
                tempSet.add(item.getText());
            }
        }
        if (!selectTagSet.containsAll(tempSet) || selectTagSet.size() != tempSet.size()) {
            selectTagSet.clear();
            selectTagSet.addAll(tempSet);
            logBeanList.clear();
            logBeanList.addAll(
                    Recorder.getInstance().getLogList(selectLevelSet, selectTagSet));
            logListAdapter.notifyDataSetChanged();
        }
    }

    public void addLog(LogBean bean) {
        int logLevel = bean.getLogLevel();
        String logTag = bean.getLogTag();
        if (selectLevelSet.contains(logLevel)) {
            if (isSelectAllTag) {
                logBeanList.add(bean);
                logListAdapter.notifyDataSetChanged();
                boolean add = selectTagSet.add(logTag);
                if (add) {
                    CheckBean checkBean = new CheckBean(logTag);
                    checkBean.setChecked(true);
                    tags.add(checkBean);
                }
                return;
            }
            // 判断是否显示
            if (selectTagSet.contains(logTag)) {
                logBeanList.add(bean);
                logListAdapter.notifyDataSetChanged();
            }
        }
    }

}
