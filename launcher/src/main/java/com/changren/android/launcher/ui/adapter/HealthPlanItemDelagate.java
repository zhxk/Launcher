package com.changren.android.launcher.ui.adapter;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.changren.android.component.adapter.recyclerview.base.ItemViewDelegate;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.PlanTodayBean;
import com.changren.android.launcher.ui.constants.Constants;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.ToastUtils;
import com.changren.android.launcher.util.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by zhy on 16/6/22.
 */
public class HealthPlanItemDelagate implements ItemViewDelegate<DataSource>, View.OnClickListener {

    private TextView planTvMore;        //更多控件
    private TextView remindMedication;  //检测提醒
    private TextView planBpDetect;      //血压检测
    private TextView planGluDetect;     //血糖检测
    private TextView planWeightDetect;  //体重检测

    private TextView planNow;   //当前计划
    private TextView planNext;  //下一步计划

    private RecyclerView planLineInfo;  //时间轴控件
    private PlanLineItemAdapter planLineAdapter;
    private List<PlanTodayBean.PlanBean> planTodayList;


    @Override
    public int getItemViewLayoutId() {
        return R.layout.home_scrollbar_fragment_health_plan;
    }

    @Override
    public boolean isForViewType(DataSource item, int position) {
        return item.getItem_type().equals("P");
    }

    @Override
    public void convert(ViewHolder holder, DataSource s, int position) {
        List<PlanTodayBean.PlanBean> datas = ((PlanTodayBean) s).getDatas();
//        LogUtils.i("datas == > " + new Gson().toJson(datas));
        planTvMore = holder.getView(R.id.plan_tv_more);
        planLineInfo = holder.getView(R.id.plan_line_info);
        remindMedication = holder.getView(R.id.plan_line_remind_medication);
        planBpDetect = holder.getView(R.id.plan_line_remind_bp);
        planGluDetect = holder.getView(R.id.plan_line_remind_glu);
        planWeightDetect = holder.getView(R.id.plan_line_remind_weight);
        planNow = holder.getView(R.id.plan_now);
        planNext = holder.getView(R.id.plan_next);

        if (datas != null) {
            filterData(datas);
            initRecyclerView(Utils.getApp());
        }
        planTvMore.setOnClickListener(this);
        remindMedication.setOnClickListener(this);
        planBpDetect.setOnClickListener(this);
        planGluDetect.setOnClickListener(this);
        planWeightDetect.setOnClickListener(this);
    }

    private void filterData(List<PlanTodayBean.PlanBean> data) {
//        LogUtils.d("filterData", new Gson().toJson(data));
        Application app = Utils.getApp();
        if (data == null) {
            return;
        }
        planTodayList = new ArrayList<>();
        int isLastPos = -1;
        int isAferPos = 0;
        for (int i = 0; i < data.size(); i++) {
            if (getNowTime() <= getPlanTime(data.get(i))) {
                if (i == data.size() - 1) {
                    isLastPos = -1;
                    isAferPos = i - 1;
                } else {
                    isLastPos = i;
                    isAferPos = i-1;
                }
                break;
            }
        }

        if (data.size() < 4) {
            for (int i = 0; i < data.size(); i++) {
                planTodayList.add(data.get(i));
            }
            for (int i = 0; i < (4 - data.size()); i++) {
                PlanTodayBean.PlanBean dataBean = new PlanTodayBean.PlanBean();
                dataBean.setTime("");
                dataBean.setType(6);
                planTodayList.add(dataBean);
            }
        } else if (isLastPos == 0 && isAferPos < 0) {//当前时间的前面 没有更小的时间event(即第一个点)
            for (int i = 0; i < 4; i++) {
                planTodayList.add(data.get(i));
            }
        } else if (isLastPos < 0 && data.size() > 4) {//当前时间的后面 没有更大的时间event(即最后一个点)
            for (int i = data.size() - 4; i < data.size(); i++) {
                planTodayList.add(data.get(i));
            }
        } else if (isLastPos > 0 && data.size() - isLastPos >= 4) {//当前时间的后面 有更大的时间event，且多于4个
            for (int i = isLastPos; i < /*data.size()*/(isLastPos + 4); i++) {
                planTodayList.add(data.get(i));
            }
        } else if (isLastPos > 0 && data.size() - isLastPos < 4) {//当前时间的后面 有更大的时间event，但是不足4个
            for (int i = data.size() - 4; i < data.size(); i++) {
                planTodayList.add(data.get(i));
            }
        } else {
//            LogUtils.d("isLastPos == " + isLastPos + "  isAfterPos == " + isAferPos);
        }
        if (data.size() == 1) {
            if (data.get(0).getType() == Constants.TYPE_REMIND_MEDICATION) {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), data.get(0).getDrug_name()));
            } else {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), getRemindName(data.get(0).getType())));
            }
        } else if (data.size() >= 2) {
            if (data.get(0).getType() == Constants.TYPE_REMIND_MEDICATION && data.get(1).getType() == Constants.TYPE_REMIND_MEDICATION) {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), data.get(0).getDrug_name()));
                planNext.setText(getShowText(app.getApplicationContext(), data.get(1).getTime(), data.get(1).getDrug_name()));
            } else if (data.get(0).getType() == Constants.TYPE_REMIND_MEDICATION && data.get(1).getType() != Constants.TYPE_REMIND_MEDICATION) {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), data.get(0).getDrug_name()));
                planNext.setText(getShowText(app.getApplicationContext(), data.get(1).getTime(), getRemindName(data.get(1).getType())));
            } else if (data.get(0).getType() != Constants.TYPE_REMIND_MEDICATION && data.get(1).getType() == Constants.TYPE_REMIND_MEDICATION) {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), getRemindName(data.get(0).getType())));
                planNext.setText(getShowText(app.getApplicationContext(), data.get(1).getTime(), data.get(1).getDrug_name()));
            } else {
                planNow.setText(getShowText(app.getApplicationContext(), data.get(0).getTime(), getRemindName(data.get(0).getType())));
                planNext.setText(getShowText(app.getApplicationContext(), data.get(1).getTime(), getRemindName(data.get(0).getType())));
            }
        }
    }

    private String getRemindName(int remindType) {
        String remindName = "";
        switch (remindType) {
            case Constants.TYPE_REMIND_BP:
                remindName = Utils.getApp().getResources().getString(R.string.plan_line_BP);
                break;
            case Constants.TYPE_REMIND_GLU:
                remindName = Utils.getApp().getResources().getString(R.string.plan_line_GLU);
                break;
            case Constants.TYPE_REMIND_WEIGHT:
                remindName = Utils.getApp().getResources().getString(R.string.plan_line_weight);
                break;
        }
            return remindName;
    }

    private String getShowText(Context context, String time, String event) {
        return String.format(context.getResources().getString(R.string.plan_remind_str1), time, event);
    }

    private int getNowTime() {
        Calendar calendar = Calendar.getInstance();
        //现在的时间
        int nowHours = calendar.get(Calendar.HOUR_OF_DAY); //时
        int nowMinute = calendar.get(Calendar.MINUTE); //分
        int nowMinutes = (nowHours * 60) + nowMinute; //转成分钟
        return nowMinutes;
    }

    private int getPlanTime(PlanTodayBean.PlanBean planTodayBean) {
        //获取到的计划时间
        String time = planTodayBean.getTime();
        String planHours = time.substring(0, time.indexOf(":")); //：左边的数据
        int planHours2 = Integer.parseInt(planHours);
        String planMinute = time.substring(time.length() - 2); //从后往前取两位
        int planMinutes = (planHours2 * 60) + Integer.parseInt(planMinute); //转换为分钟
        return planMinutes;
    }

    // 初始化recyclerview
    private void initRecyclerView(Context context) {
        planLineAdapter = new PlanLineItemAdapter(context, R.layout.plan_line_item, planTodayList);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        planLineInfo.setLayoutManager(manager);
        planLineInfo.setAdapter(planLineAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.plan_tv_more:
                if (Utils.isAvilible(v.getContext(), Constants.PLAN_PACKAGE_NAME) && !"".equals(AppConfig.getToken())) {
                    Utils.toHealthPlan(v.getContext(), Constants.PLAN_PACKAGE_NAME, Constants.PLAN_MAIN_CLASS_NAME);
                } else {
                    ToastUtils.showShort(R.string.no_healthplan_app);
                }
                break;
            case R.id.plan_line_remind_medication:
                if (Utils.isAvilible(v.getContext(), Constants.PLAN_PACKAGE_NAME) && !"".equals(AppConfig.getToken())) {
                    Utils.toHealthPlan(v.getContext(), Constants.PLAN_PACKAGE_NAME, Constants.REMIND_MEDICATION_CLASS_NAME);
                } else {
                    ToastUtils.showShort(R.string.no_healthplan_app);
                }
                break;
            case R.id.plan_line_remind_glu:
            case R.id.plan_line_remind_bp:
            case R.id.plan_line_remind_weight:
                if (Utils.isAvilible(v.getContext(), Constants.PLAN_PACKAGE_NAME) && !"".equals(AppConfig.getToken())) {
                    Utils.toHealthPlan(v.getContext(), Constants.PLAN_PACKAGE_NAME, Constants.REMIND_DETECT_CLASS_NAME);
                } else {
                    ToastUtils.showShort(R.string.no_healthplan_app);
                }
                break;
        }
    }

    /*private void toHealthPlan(Context context, String packageName, String className) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        context.startActivity(intent);
    }*/

    //    @Override
    //    public boolean isForViewType(ChatMessage item, int position)
    //    {
    //        return !item.isComMeg();
    //    }
    //
    //    @Override
    //    public void convert(ViewHolder holder, ChatMessage chatMessage, int position)
    //    {
    //        holder.setText(R.id.chat_send_content, chatMessage.getContent());
    //        holder.setText(R.id.chat_send_name, chatMessage.getName());
    //        holder.setImageResource(R.id.chat_send_icon, chatMessage.getIcon());
    //    }
}
