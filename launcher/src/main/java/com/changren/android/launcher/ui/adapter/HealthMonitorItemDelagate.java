package com.changren.android.launcher.ui.adapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.changren.android.component.adapter.recyclerview.base.ItemViewDelegate;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.HealthUserScore;
import com.changren.android.launcher.util.AppConfig;
import com.changren.android.launcher.util.AppConstants;
import com.changren.android.launcher.util.InstallUtils;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.Utils;

import java.util.List;

import butterknife.BindViews;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * ------------------------------------------------------------------------------------
 * Copyright (C) 2017-2018, by Shanghai ChangRen Mdt InfoTech Ltd, All rights reserved.
 * ------------------------------------------------------------------------------------
 *
 * @author : wsy,jwl
 * @ClassName :HealthMonitorItemDelagate.java
 * @Description :
 * @CreateDate :
 * Version ：1.0
 * UpdateHistory ： 2019/2/20 13:53
 */
public class HealthMonitorItemDelagate implements ItemViewDelegate<DataSource>, View.OnClickListener {
    Activity mActivity;

    @BindViews({R.id.btn_nibp_click, R.id.btn_spo2_click, R.id.btn_glu_click, R.id.btn_temp_click})
    List<Button> buttonList;

    public HealthMonitorItemDelagate(Activity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.home_scrollbar_fragment_health_monitor;
    }

    @Override
    public boolean isForViewType(DataSource item, int position) {
        return item.getItem_type().equals("M");
    }

    @Override
    public void convert(ViewHolder holder, DataSource s, int position) {
        holder.setOnClickListener(R.id.btn_nibp_click, this);
        holder.setOnClickListener(R.id.btn_spo2_click, this);
        holder.setOnClickListener(R.id.btn_glu_click, this);
        holder.setOnClickListener(R.id.btn_temp_click, this);
        holder.setOnClickListener(R.id.tv_more, this);
        bindData(holder, (HealthUserScore) s);
    }

    private void bindData(ViewHolder holder, HealthUserScore data) {
        LogUtils.i("bindData 检测数据=="+ data.toString());
        if (AppConfig.isLogin() && data.getSystolic_blood_pressure() > 0) {
            if (data.getBlood_pressure_short_tip() != null && data.getBlood_pressure_short_tip().equals("正常")) {
                holder.setText(R.id.tv_nibp_value, "" + data.getSystolic_blood_pressure() + "/" + data.getDiastolic_blood_pressure());
                holder.setText(R.id.tv_nibp_result, data.getBlood_pressure_short_tip());
                holder.setTextColor(R.id.tv_nibp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
                holder.setTextColor(R.id.tv_nibp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
            } else {
                holder.setTextColor(R.id.tv_nibp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
                holder.setTextColor(R.id.tv_nibp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
            }
        } else if (!AppConfig.isLogin()) {
            holder.setText(R.id.tv_nibp_value, "--/--");
            holder.setText(R.id.tv_nibp_result, "未检测");
            holder.setTextColor(R.id.tv_nibp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
            holder.setTextColor(R.id.tv_nibp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
        }

        if (AppConfig.isLogin() && data.getGlu() != null) {
            if (data.getBlood_sugar_short_tip() != null && data.getBlood_sugar_short_tip().equals("正常")) {
                holder.setText(R.id.tv_glu_value, data.getGlu());
                holder.setText(R.id.tv_glu_result, data.getBlood_sugar_short_tip());
                holder.setTextColor(R.id.tv_glu_value, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
                holder.setTextColor(R.id.tv_glu_result, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
            } else {
                holder.setTextColor(R.id.tv_glu_value, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
                holder.setTextColor(R.id.tv_glu_result, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
            }

        } else if (!AppConfig.isLogin()) {
            holder.setText(R.id.tv_glu_value, "--/--");
            holder.setText(R.id.tv_glu_result, "未检测");
            holder.setTextColor(R.id.tv_glu_value, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
            holder.setTextColor(R.id.tv_glu_result, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
        }

        if (AppConfig.isLogin() && data.getSpo2() != null) {

            if (data.getBlood_oxygen_short_tip() != null && data.getBlood_oxygen_short_tip().equals("正常")) {
                holder.setText(R.id.tv_spo2_value, data.getSpo2() + "%");
                holder.setText(R.id.tv_spo2_result, data.getBlood_oxygen_short_tip());
                holder.setTextColor(R.id.tv_spo2_value, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
                holder.setTextColor(R.id.tv_spo2_result, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
            } else {
                holder.setTextColor(R.id.tv_spo2_value, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
                holder.setTextColor(R.id.tv_spo2_result, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
            }
        } else if (!AppConfig.isLogin()) {
            holder.setText(R.id.tv_spo2_value, "--/--");
            holder.setText(R.id.tv_spo2_result, "未检测");
            holder.setTextColor(R.id.tv_spo2_value, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
            holder.setTextColor(R.id.tv_spo2_result, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
        }

        if (AppConfig.isLogin() && data.getTemp() != null) {

            if (data.getTemp_short_tip() != null && data.getTemp_short_tip().equals("正常")) {
                holder.setText(R.id.tv_temp_value, data.getTemp() + "℃");
                holder.setText(R.id.tv_temp_result, data.getTemp_short_tip());
                holder.setTextColor(R.id.tv_temp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
                holder.setTextColor(R.id.tv_temp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_green));
            } else {
                holder.setTextColor(R.id.tv_temp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
                holder.setTextColor(R.id.tv_temp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_red));
            }

        } else if (!AppConfig.isLogin()) {
            holder.setText(R.id.tv_temp_value, "--/--");
            holder.setText(R.id.tv_temp_result, "未检测");
            holder.setTextColor(R.id.tv_temp_value, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
            holder.setTextColor(R.id.tv_temp_result, ContextCompat.getColor(mActivity, R.color.user_health_score_black));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nibp_click:

                LogUtils.i("血压按钮点击");
                executePageTurn(0);
                break;
            case R.id.btn_spo2_click:
                LogUtils.i("血氧按钮点击");
                executePageTurn(1);
                break;
            case R.id.btn_glu_click:
                LogUtils.i("血糖按钮点击");
                executePageTurn(3);
                break;
            case R.id.btn_temp_click:
                LogUtils.i("体温按钮点击");
                executePageTurn(2);
                break;
            case R.id.tv_more:
                LogUtils.i("更多");
                if (InstallUtils.isAvilible(Utils.getApp().getApplicationContext(), AppConstants.MONITOR_PACKAGE_NAME)) {
                    //已安装，打开程序，需传入参数包名：
                    LogUtils.i("**已安装");
                    Intent intent = new Intent();
                    ComponentName cn = new ComponentName(AppConstants.MONITOR_PACKAGE_NAME, AppConstants.MONITOR_MAIN);
                    intent.setComponent(cn);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                } else {//未安装
                    LogUtils.i("**应用未安装**");
                }
                break;
            default:
        }
    }

    /**
     * @param index index跳转携带的页面参数
     */
    private void executePageTurn(int index) {
        if (InstallUtils.isAvilible(Utils.getApp().getApplicationContext(), AppConstants.MONITOR_PACKAGE_NAME)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("monitor_index", index);
            ComponentName cn = new ComponentName(AppConstants.MONITOR_PACKAGE_NAME, AppConstants.MONITOR_EXAMINATION);
            intent.setComponent(cn);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } else {
            LogUtils.i("**应用未安装**");
        }
    }
    //    @Override
    //    public boolean isForViewType(ChatMessage item, int position) {
    //        return item.isComMeg();
    //    }
    //
    //    @Override
    //    public void convert(ViewHolder holder, ChatMessage chatMessage, int position) {
    //        holder.setText(R.id.chat_from_content, chatMessage.getContent());
    //        holder.setText(R.id.chat_from_name, chatMessage.getName());
    //        holder.setImageResource(R.id.chat_from_icon, chatMessage.getIcon());
    //    }

}
