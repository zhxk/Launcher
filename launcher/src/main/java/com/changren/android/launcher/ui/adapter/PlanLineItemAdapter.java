package com.changren.android.launcher.ui.adapter;

import android.content.Context;

import com.changren.android.component.adapter.recyclerview.CommonAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.PlanTodayBean;
import com.changren.android.launcher.ui.constants.Constants;

import java.util.List;

public class PlanLineItemAdapter extends CommonAdapter<PlanTodayBean.PlanBean> {

    public PlanLineItemAdapter(Context context, int layoutId, List<PlanTodayBean.PlanBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, PlanTodayBean.PlanBean planTodayBean, int position) {
        holder.setText(R.id.plan_time, planTodayBean.getTime());
        if (planTodayBean.getType() == Constants.TYPE_REMIND_MEDICATION) { //用药提醒
            holder.setImageResource(R.id.plan_info_img, R.mipmap.plan_line_blue_medication_45);
        } else if (planTodayBean.getType() == Constants.TYPE_REMIND_BP) { //血压检测提醒
            holder.setImageResource(R.id.plan_info_img, R.mipmap.plan_line_blue_bp_icon_45);
        } else if (planTodayBean.getType() == Constants.TYPE_REMIND_WEIGHT) { //体重检测提醒
            holder.setImageResource(R.id.plan_info_img, R.mipmap.plan_line_blue_weight_icon_45);
        } else if (planTodayBean.getType() == Constants.TYPE_REMIND_GLU) { //血糖检测提醒
            holder.setImageResource(R.id.plan_info_img, R.mipmap.plan_line_blue_glu_45);
        } else if (planTodayBean.getType() == Constants.TYPE_DEFAULT){
            holder.setImageResource(R.id.plan_info_img, 0);
        }
    }
}
